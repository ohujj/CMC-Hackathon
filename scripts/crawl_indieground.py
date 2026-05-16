"""
Indieground 독립영화 크롤러
Step 1: 목록 API로 전체 5,895편 수집
Step 2: 각 상세 페이지 크롤링

Usage:
  pip install requests beautifulsoup4 mysql-connector-python
  python crawl_indieground.py --step 1   # 목록 수집
  python crawl_indieground.py --step 2   # 상세 수집
  python crawl_indieground.py --step all # 둘 다
"""

import requests
import mysql.connector
import time
import argparse
import re
from bs4 import BeautifulSoup

# DB 설정
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "root1234",
    "database": "indiefilm",
    "charset": "utf8mb4",
}

BASE_URL = "https://indieground.kr"
LIST_API = f"{BASE_URL}/indie/dbListMore.do"
DETAIL_URL = f"{BASE_URL}/indie/movieLibraryView.do"

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
    "Referer": f"{BASE_URL}/indie/dbList.do",
    "X-Requested-With": "XMLHttpRequest",
}

session = requests.Session()
session.headers.update(HEADERS)


def init_db(conn):
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS movie (
            seq BIGINT PRIMARY KEY,
            kor_title VARCHAR(200),
            eng_title VARCHAR(200),
            director VARCHAR(200),
            actors TEXT,
            production_year VARCHAR(10),
            genre_code VARCHAR(20),
            genre_name VARCHAR(50),
            company_nm VARCHAR(200),
            distributor_nm VARCHAR(200),
            image_path VARCHAR(300),
            lss_gubun VARCHAR(10),
            duration VARCHAR(30),
            rating VARCHAR(30),
            color_type VARCHAR(20),
            synopsis TEXT,
            screenwriter VARCHAR(200),
            producer VARCHAR(300),
            release_date VARCHAR(30),
            keywords VARCHAR(300),
            detail_crawled BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    """)
    conn.commit()
    cursor.close()
    print("DB 초기화 완료")


def get_session_cookie():
    """인디그라운드 세션 쿠키 획득"""
    session.get(f"{BASE_URL}/indie/dbList.do")
    print("세션 쿠키 획득 완료")


def crawl_list(conn):
    """Step 1: 목록 API로 전체 영화 수집"""
    cursor = conn.cursor()
    page = 1
    total = None
    page_size = 9
    inserted = 0
    skipped = 0

    print("=== Step 1: 목록 수집 시작 ===")

    while True:
        start = (page - 1) * page_size + 1
        end = page * page_size

        try:
            resp = session.post(
                LIST_API,
                data={
                    "mode": "list",
                    "type": "1",
                    "page": str(page),
                    "start": str(start),
                    "end": str(end),
                    "productionYearEd": "",
                    "lssGubun": "",
                    "genres": "",
                    "orderKey": "",
                    "searchOption": "",
                    "searchKey": "",
                },
                timeout=10,
            )
            data = resp.json()
        except Exception as e:
            print(f"  [오류] page={page}: {e}")
            time.sleep(2)
            continue

        movies = data.get("list", [])
        if not movies:
            break

        if total is None:
            total = int(movies[0].get("total", 0))
            total_pages = -(-total // page_size)  # ceiling division
            print(f"총 {total}편 / {total_pages}페이지")

        for m in movies:
            seq = m.get("seq")
            if not seq:
                continue

            try:
                cursor.execute(
                    """
                    INSERT IGNORE INTO movie
                        (seq, kor_title, eng_title, director, actors,
                         production_year, genre_code, genre_name,
                         company_nm, distributor_nm, image_path, lss_gubun)
                    VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
                """,
                    (
                        seq,
                        m.get("korSubject", ""),
                        m.get("engSubject", ""),
                        m.get("director", ""),
                        m.get("actors", ""),
                        m.get("productionYear", ""),
                        m.get("genres", ""),
                        m.get("genrenms", ""),
                        m.get("companyNm", ""),
                        m.get("companyCntFilmNm", ""),
                        m.get("pathname", ""),
                        m.get("lssGubun", ""),
                    ),
                )
                if cursor.rowcount > 0:
                    inserted += 1
                else:
                    skipped += 1
            except Exception as e:
                print(f"  [DB 오류] seq={seq}: {e}")

        conn.commit()
        print(
            f"  page {page}/{total_pages if total else '?'} — 누적 {inserted}편 저장, {skipped}편 스킵"
        )

        if total and end >= total:
            break

        page += 1
        time.sleep(0.3)  # 서버 부하 방지

    cursor.close()
    print(f"=== Step 1 완료: {inserted}편 저장 ===\n")


def parse_detail(html: str) -> dict:
    """상세 페이지 파싱"""
    soup = BeautifulSoup(html, "html.parser")
    result = {}

    # dt/dd 쌍으로 파싱
    for dt in soup.find_all("dt"):
        key = dt.get_text(strip=True)
        dd = dt.find_next_sibling("dd")
        if dd:
            result[key] = dd.get_text(strip=True)

    # th/td 쌍으로 파싱 (각본, 제작 등)
    for th in soup.find_all("th"):
        key = th.get_text(strip=True)
        td = th.find_next_sibling("td")
        if td:
            result[key] = td.get_text(strip=True)

    # 상영시간 / 관람등급 / 컬러 (li 태그들)
    for li in soup.find_all("li"):
        text = li.get_text(strip=True)
        if re.search(r"\d+분", text):
            result["duration"] = text
        elif "관람가" in text or "전체" in text:
            result["rating"] = text
        elif text in ("컬러", "흑백", "Color", "B&W"):
            result["color_type"] = text

    # 시놉시스
    synopsis_dt = soup.find("dt", string=re.compile("시놉시스"))
    if synopsis_dt:
        synopsis_dd = synopsis_dt.find_next_sibling("dd")
        if synopsis_dd:
            result["synopsis"] = synopsis_dd.get_text(strip=True)

    # 키워드
    keywords = [
        a.get_text(strip=True)
        for a in soup.find_all("a")
        if a.get_text(strip=True).startswith("#")
    ]
    if keywords:
        result["keywords"] = " ".join(keywords)

    return result


def crawl_detail(conn):
    """Step 2: 상세 페이지 크롤링 (detail_crawled=false인 것만)"""
    cursor = conn.cursor(dictionary=True)
    update_cursor = conn.cursor()

    cursor.execute(
        "SELECT seq FROM movie WHERE detail_crawled = FALSE ORDER BY seq DESC"
    )
    rows = cursor.fetchall()
    total = len(rows)
    print(f"=== Step 2: 상세 수집 시작 ({total}편) ===")

    for i, row in enumerate(rows):
        seq = row["seq"]
        try:
            resp = session.get(DETAIL_URL, params={"seq": seq, "type": "D"}, timeout=10)
            detail = parse_detail(resp.text)

            update_cursor.execute(
                """
                UPDATE movie SET
                    duration = %s,
                    rating = %s,
                    color_type = %s,
                    synopsis = %s,
                    screenwriter = %s,
                    producer = %s,
                    release_date = %s,
                    keywords = %s,
                    detail_crawled = TRUE
                WHERE seq = %s
            """,
                (
                    detail.get("duration", ""),
                    detail.get("rating", ""),
                    detail.get("color_type", ""),
                    detail.get("시놉시스", detail.get("synopsis", "")),
                    detail.get("각본", ""),
                    detail.get("제작", ""),
                    detail.get("개봉", ""),
                    detail.get("keywords", ""),
                    seq,
                ),
            )

            if (i + 1) % 50 == 0:
                conn.commit()
                print(f"  {i + 1}/{total} 완료...")

        except Exception as e:
            print(f"  [오류] seq={seq}: {e}")

        time.sleep(0.2)

    conn.commit()
    cursor.close()
    update_cursor.close()
    print("=== Step 2 완료 ===\n")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--step", choices=["1", "2", "all"], default="all")
    args = parser.parse_args()

    conn = mysql.connector.connect(**DB_CONFIG)
    init_db(conn)
    get_session_cookie()

    if args.step in ("1", "all"):
        crawl_list(conn)

    if args.step in ("2", "all"):
        crawl_detail(conn)

    conn.close()
    print("크롤링 완료!")


if __name__ == "__main__":
    main()
