"""
KOBIS 독립·예술영화 전용상영관 크롤러
목록: https://kobis.or.kr/kobis/business/mast/thea/findArtScreenStat.do
상세: https://kobis.or.kr/kobis/business/mast/thea/findArtShowHistory.do

Usage:
  pip install requests beautifulsoup4 mysql-connector-python
  python crawl_theaters.py --step 1   # 목록 수집 (theaCd 기반)
  python crawl_theaters.py --step 2   # 상세 수집 (주소/전화 등)
  python crawl_theaters.py --step all # 둘 다
"""

import requests
import mysql.connector
import time
import argparse
from datetime import datetime
from bs4 import BeautifulSoup

DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "root1234",
    "database": "indiefilm",
    "charset": "utf8mb4",
}

BASE_URL = "https://kobis.or.kr"
LIST_URL = f"{BASE_URL}/kobis/business/mast/thea/findArtScreenStat.do"
DETAIL_URL = f"{BASE_URL}/kobis/business/mast/thea/findArtShowHistory.do"

# 독립·예술영화 전용관 코드
ART_SCREEN_GB = "121001"
CURRENT_YEAR = str(datetime.now().year)

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
    "Referer": LIST_URL,
}

session = requests.Session()
session.headers.update(HEADERS)


def init_db(conn):
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS theater (
            thea_cd VARCHAR(20) PRIMARY KEY,
            scrn_cd VARCHAR(10) NOT NULL,
            thea_name VARCHAR(200),
            scrn_name VARCHAR(200),
            screen_gb VARCHAR(10),
            designated_at VARCHAR(30),
            address VARCHAR(300),
            phone VARCHAR(50),
            fax VARCHAR(50),
            homepage VARCHAR(300),
            seat_count INT,
            detail_crawled BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    """)
    conn.commit()
    cursor.close()
    print("DB 초기화 완료")


def get_csrf_token() -> str:
    """KOBIS 목록 페이지 GET → CSRF 토큰 추출"""
    resp = session.get(LIST_URL, timeout=10)
    soup = BeautifulSoup(resp.text, "html.parser")
    token_input = soup.find("input", {"name": "CSRFToken"})
    token = token_input["value"] if token_input else ""
    print(f"CSRF 토큰: {token[:20]}..." if token else "CSRF 토큰 없음 (무시)")
    return token


def crawl_list(conn):
    """Step 1: 독립·예술영화 전용상영관 목록 수집"""
    print("=== Step 1: 목록 수집 시작 ===")
    cursor = conn.cursor()

    csrf = get_csrf_token()

    resp = session.post(
        LIST_URL,
        data={
            "CSRFToken": csrf,
            "sSearchType": "",
            "sSearchYearFrom": "",
            "groupYn": "offic",
            "sFromYear": "2007",
            "sToYear": CURRENT_YEAR,
            "screenGb": ART_SCREEN_GB,  # 독립·예술영화만
        },
        timeout=15,
    )

    soup = BeautifulSoup(resp.text, "html.parser")
    rows = soup.select("table tbody tr")
    print(f"총 {len(rows)}행 발견")

    inserted = 0
    skipped = 0

    for row in rows:
        thea_cd_input = row.find("input", {"name": "theaCd"})
        scrn_cd_input = row.find("input", {"name": "scrnCd"})
        art_screen_gb_input = row.find("input", {"name": "artScreenGb"})

        if not thea_cd_input:
            continue

        thea_cd = thea_cd_input["value"]
        scrn_cd = scrn_cd_input["value"] if scrn_cd_input else "01"
        screen_gb = (
            art_screen_gb_input["value"] if art_screen_gb_input else ART_SCREEN_GB
        )

        tds = row.find_all("td")
        if len(tds) < 4:
            continue

        thea_name_td = tds[1]
        thea_name = thea_name_td.get_text(strip=True)

        scrn_name_td = tds[2]
        scrn_name = (
            scrn_name_td.get_text(strip=True).replace("(문화부지정)", "").strip()
        )

        screen_gb_td = tds[3]
        screen_gb_text = screen_gb_td.get_text(strip=True)

        designated_at_td = tds[4] if len(tds) > 4 else None
        # "2007-5 (2007.07.25)" → "2007.07.25" 추출
        designated_raw = (
            designated_at_td.get_text(strip=True) if designated_at_td else ""
        )
        import re

        date_match = re.search(r"\d{4}\.\d{2}\.\d{2}", designated_raw)
        designated_at = date_match.group() if date_match else designated_raw

        try:
            cursor.execute(
                """
                INSERT IGNORE INTO theater
                    (thea_cd, scrn_cd, thea_name, scrn_name, screen_gb, designated_at)
                VALUES (%s, %s, %s, %s, %s, %s)
                """,
                (thea_cd, scrn_cd, thea_name, scrn_name, screen_gb, designated_at),
            )
            if cursor.rowcount > 0:
                inserted += 1
            else:
                skipped += 1
        except Exception as e:
            print(f"  [DB 오류] theaCd={thea_cd}: {e}")

    conn.commit()
    cursor.close()
    print(f"=== Step 1 완료: {inserted}개 저장, {skipped}개 스킵 ===\n")


def parse_detail(html: str) -> dict:
    """KOBIS 상세 페이지 파싱 (th/td 테이블 구조)"""
    soup = BeautifulSoup(html, "html.parser")
    result = {}

    for th in soup.find_all("th"):
        key = th.get_text(strip=True)
        td = th.find_next_sibling("td")
        if not td:
            continue
        value = td.get_text(strip=True)

        if "전화" in key:
            result["phone"] = value
        elif "FAX" in key or "팩스" in key:
            result["fax"] = value
        elif "주소" in key:
            result["address"] = value
        elif "홈페이지" in key:
            # a 태그에서 href 추출
            a = td.find("a")
            result["homepage"] = a["href"] if a and a.get("href") else value
        elif "좌석" in key:
            import re

            m = re.search(r"\d+", value)
            result["seat_count"] = int(m.group()) if m else None

    return result


def crawl_detail(conn):
    """Step 2: 각 영화관 상세 페이지에서 주소/전화/홈페이지 수집"""
    cursor = conn.cursor(dictionary=True)
    update_cursor = conn.cursor()

    cursor.execute(
        "SELECT thea_cd, scrn_cd, screen_gb FROM theater WHERE detail_crawled = FALSE ORDER BY thea_cd"
    )
    rows = cursor.fetchall()
    total = len(rows)
    print(f"=== Step 2: 상세 수집 시작 ({total}개) ===")

    # CSRF는 한 번만 획득
    csrf = get_csrf_token()

    for i, row in enumerate(rows):
        thea_cd = row["thea_cd"]
        scrn_cd = row["scrn_cd"]
        screen_gb = row["screen_gb"]

        try:
            resp = session.post(
                DETAIL_URL,
                data={
                    "CSRFToken": csrf,
                    "theaCd": thea_cd,
                    "scrnCd": scrn_cd,
                    "artScreenGb": screen_gb,
                    "sSearchYear": CURRENT_YEAR,
                },
                timeout=10,
            )
            detail = parse_detail(resp.text)

            update_cursor.execute(
                """
                UPDATE theater SET
                    address = %s,
                    phone = %s,
                    fax = %s,
                    homepage = %s,
                    seat_count = %s,
                    detail_crawled = TRUE
                WHERE thea_cd = %s
                """,
                (
                    detail.get("address", ""),
                    detail.get("phone", ""),
                    detail.get("fax", ""),
                    detail.get("homepage", ""),
                    detail.get("seat_count"),
                    thea_cd,
                ),
            )

            if (i + 1) % 10 == 0:
                conn.commit()
                print(f"  {i + 1}/{total} 완료...")

        except Exception as e:
            print(f"  [오류] theaCd={thea_cd}: {e}")

        time.sleep(0.5)  # KOBIS 서버 부하 방지

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

    if args.step in ("1", "all"):
        crawl_list(conn)

    if args.step in ("2", "all"):
        crawl_detail(conn)

    conn.close()
    print("크롤링 완료!")


if __name__ == "__main__":
    main()
