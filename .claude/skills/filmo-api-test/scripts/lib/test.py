#!/usr/bin/env python3
"""Filmo API 채점 시뮬레이터.

- /v3/api-docs 에서 spec 가져오기
- 모든 path 에 대해 happy + fuzz 케이스 실행
- 결과 집계 + 점수 산출
"""

from __future__ import annotations

import argparse
import json
import sys
import time
import uuid
from datetime import datetime, timezone
from pathlib import Path
from urllib import error, request


def http(method, url, body=None, headers=None, timeout=15):
    data = json.dumps(body).encode("utf-8") if body is not None else None
    hdrs = {"Content-Type": "application/json"}
    if headers:
        hdrs.update(headers)
    req = request.Request(url, data=data, method=method, headers=hdrs)
    t0 = time.time()
    try:
        with request.urlopen(req, timeout=timeout) as resp:
            raw = resp.read().decode("utf-8", errors="replace")
            return resp.status, raw, time.time() - t0
    except error.HTTPError as e:
        raw = e.read().decode("utf-8", errors="replace") if e.fp else ""
        return e.code, raw, time.time() - t0
    except Exception as e:  # noqa: BLE001
        return 0, f"__network_error__: {e}", time.time() - t0


def signup(base):
    """returns (token, login_id, password)"""
    suffix = uuid.uuid4().hex[:8]
    login_id = f"qa_{suffix}"
    password = "pass1234"
    body = {"loginId": login_id, "password": password, "nickname": f"QA_{suffix}"}
    s, raw, _ = http("POST", f"{base}/api/auth/signup", body)
    if s == 200:
        try:
            return json.loads(raw)["data"]["accessToken"], login_id, password
        except Exception:  # noqa: BLE001
            pass
    return None, None, None


def seed_path_values(base):
    """실제 DB에서 path variable 로 쓸 값 한 개씩 가져오기."""
    seq = None
    thea = None
    image = "test"
    s, raw, _ = http("GET", f"{base}/api/movies?size=1")
    if s == 200:
        try:
            row = json.loads(raw)["data"]["content"][0]
            seq = row["seq"]
            img = row.get("imagePath") or ""
            if img:
                image = img.replace("fileFolder/", "")
        except Exception:  # noqa: BLE001
            pass
    s, raw, _ = http("GET", f"{base}/api/theaters?size=1")
    if s == 200:
        try:
            thea = json.loads(raw)["data"]["content"][0]["theaCd"]
        except Exception:  # noqa: BLE001
            pass
    return {"seq": seq, "theaCd": thea, "imagePath": image}


def schema_example(schema, defs):
    """OpenAPI schema → 합리적 minimal 값."""
    if not isinstance(schema, dict):
        return None
    if "$ref" in schema:
        ref = schema["$ref"].split("/")[-1]
        return schema_example(defs.get(ref, {}), defs)
    t = schema.get("type")
    if t == "string":
        if schema.get("format") == "date-time":
            return "2026-01-01T00:00:00"
        if "enum" in schema:
            return schema["enum"][0]
        return "test_string"
    if t in ("integer", "number"):
        return 1
    if t == "boolean":
        return True
    if t == "array":
        return []
    if t == "object" or "properties" in schema:
        out = {}
        props = schema.get("properties", {})
        required = set(schema.get("required", []))
        for k, v in props.items():
            if k in required or len(out) < 3:
                out[k] = schema_example(v, defs)
        return out
    return None


def fill_path(template, seed):
    out = template
    out = out.replace("{seq}", str(seed["seq"] or 1))
    out = out.replace("{theaCd}", str(seed["theaCd"] or "001016"))
    out = out.replace("{imagePath}", str(seed["imagePath"]))
    out = out.replace("{ticketId}", "1")
    return out


def build_happy_body(path, method, op, defs, ctx):
    """path-specific happy body. ctx={'login_id','password','movie_seq'}."""
    if path == "/api/auth/signup":
        suf = uuid.uuid4().hex[:8]
        return {"loginId": f"hp_{suf}", "password": "pass1234", "nickname": f"H_{suf}"}
    if path == "/api/auth/login":
        return {"loginId": ctx["login_id"], "password": ctx["password"]}
    if path == "/api/users/me" and method == "PATCH":
        # nickname 중복 회피, intro 만 갱신
        return {"intro": "테스트 자기소개"}
    if path == "/api/tickets" and method == "POST":
        return {
            "movieSeq": ctx["movie_seq"],
            "cinema": "테스트 영화관",
            "watchedDate": "2026-01-01",
            "watchedTime": "19:00:00",
            "rating": 5,
            "review": "테스트 리뷰",
        }
    if path == "/api/tickets/{ticketId}" and method == "PATCH":
        return {"rating": 4, "review": "수정된 리뷰"}
    rb = op.get("requestBody", {})
    schema = (
        rb.get("content", {}).get("application/json", {}).get("schema", {})
        if rb
        else {}
    )
    return schema_example(schema, defs)


FUZZ_CASES = [
    ("empty_body", {}, None),
    ("wrong_type", {"loginId": 123, "password": ["a"], "nickname": None}, None),
    ("sql_inj", {"loginId": "' OR 1=1--", "password": "x", "nickname": "x"}, None),
    (
        "xss",
        {"loginId": "<script>alert(1)</script>", "password": "x", "nickname": "x"},
        None,
    ),
    (
        "huge_string",
        {"loginId": "x" * 5000, "password": "x" * 5000, "nickname": "x" * 5000},
        None,
    ),
    ("negative_page", None, "page=-1&size=-1"),
    ("nan_page", None, "page=abc&size=xyz"),
    ("huge_page", None, "page=999999&size=99999"),
]


def run(base):
    print(f"\n=== Filmo API 채점 시뮬레이터 → {base} ===\n", file=sys.stderr)

    # 1) spec
    s, raw, _ = http("GET", f"{base}/v3/api-docs")
    if s != 200:
        print(f"!! /v3/api-docs unreachable (status {s})", file=sys.stderr)
        sys.exit(1)
    spec = json.loads(raw)
    paths = spec.get("paths", {})
    defs = spec.get("components", {}).get("schemas", {})

    # 2) JWT + seed
    token, login_id, password = signup(base)
    if not token:
        print("!! signup 실패 — 인증 필요한 API 는 401 처리됨", file=sys.stderr)
    auth = {"Authorization": f"Bearer {token}"} if token else {}
    seed = seed_path_values(base)
    ctx = {
        "login_id": login_id or "qa_dummy",
        "password": password or "pass1234",
        "movie_seq": seed["seq"] or 1,
    }
    print(f"seed: {seed}", file=sys.stderr)

    # 3) 순회
    endpoints = []
    for path, methods in paths.items():
        for method, op in methods.items():
            if method.upper() not in ("GET", "POST", "PATCH", "PUT", "DELETE"):
                continue
            endpoints.append((path, method.upper(), op))

    happy = []
    fuzz = []

    print(f"\n총 {len(endpoints)} 엔드포인트\n", file=sys.stderr)
    print(f"{'METHOD':6} {'PATH':50} {'HAPPY':10} {'FUZZ_500':10}", file=sys.stderr)
    print("-" * 80, file=sys.stderr)

    for path, method, op in endpoints:
        url_path = fill_path(path, seed)
        # ----- happy -----
        body = (
            build_happy_body(path, method, op, defs, ctx)
            if method in ("POST", "PATCH", "PUT", "DELETE")
            else None
        )
        url = f"{base}{url_path}"
        st, raw, dt = http(method, url, body, auth)
        happy.append(
            {
                "path": path,
                "method": method,
                "status": st,
                "elapsed_ms": int(dt * 1000),
                "preview": raw[:200],
            }
        )

        # ----- fuzz -----
        fuzz_results_local = []
        for name, fbody, qstr in FUZZ_CASES:
            # body 가 있는 메서드만 body fuzz, 없는 메서드는 query fuzz 만
            if method == "GET":
                if qstr is None:
                    continue
                furl = f"{base}{url_path}?{qstr}"
                fst, fraw, fdt = http("GET", furl, None, auth)
            else:
                if fbody is None and qstr is None:
                    continue
                if fbody is not None:
                    fst, fraw, fdt = http(method, url, fbody, auth)
                else:
                    fst, fraw, fdt = http(method, f"{url}?{qstr}", None, auth)
            fuzz_results_local.append(
                {
                    "case": name,
                    "status": fst,
                    "preview": fraw[:200],
                }
            )

        # ----- 인증 없이 호출 (인증 필요한 곳 보호 확인) -----
        st_noauth, raw_noauth, _ = http(method, url, body, None)
        fuzz_results_local.append(
            {
                "case": "no_auth",
                "status": st_noauth,
                "preview": raw_noauth[:200],
            }
        )
        # ----- 깨진 토큰 -----
        st_bad, raw_bad, _ = http(
            method, url, body, {"Authorization": "Bearer junk.junk.junk"}
        )
        fuzz_results_local.append(
            {
                "case": "bad_token",
                "status": st_bad,
                "preview": raw_bad[:200],
            }
        )

        fuzz.append({"path": path, "method": method, "cases": fuzz_results_local})

        n500 = sum(1 for r in fuzz_results_local if r["status"] == 500)
        happy_str = "✅" if 200 <= st < 300 else f"❌{st}"
        fuzz_str = f"❌{n500}" if n500 else "✅0"
        print(f"{method:6} {path:50} {happy_str:10} {fuzz_str:10}", file=sys.stderr)

    # 4) 점수 산출
    total = len(endpoints)
    happy_ok = sum(1 for r in happy if 200 <= r["status"] < 300)
    api_ok_score = round(30 * happy_ok / total, 2) if total else 0

    fuzz_total = sum(len(r["cases"]) for r in fuzz)
    fuzz_500 = sum(1 for r in fuzz for c in r["cases"] if c["status"] == 500)
    exc_score = round(20 * (1 - (fuzz_500 / fuzz_total)), 2) if fuzz_total else 20

    total_score = api_ok_score + exc_score

    print("\n" + "=" * 60, file=sys.stderr)
    print(f"엔드포인트 수            : {total}", file=sys.stderr)
    print(f"happy 2xx               : {happy_ok}/{total}", file=sys.stderr)
    print(f"fuzz 케이스 총           : {fuzz_total}", file=sys.stderr)
    print(f"fuzz 중 500 발생         : {fuzz_500}", file=sys.stderr)
    print("--", file=sys.stderr)
    print(f"  API 200 OK 점수 (30)  : {api_ok_score}", file=sys.stderr)
    print(f"  예외처리 점수   (20)  : {exc_score}", file=sys.stderr)
    print(f"  합계            (50)  : {total_score}", file=sys.stderr)
    print("=" * 60 + "\n", file=sys.stderr)

    # 5) 저장
    ts = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ")
    skill_dir = Path(__file__).resolve().parents[2]
    runs = skill_dir / "runs"
    runs.mkdir(parents=True, exist_ok=True)
    out = runs / f"{ts}.json"
    out.write_text(
        json.dumps(
            {
                "timestamp": ts,
                "base": base,
                "scores": {
                    "api_ok": api_ok_score,
                    "exception": exc_score,
                    "total": total_score,
                },
                "summary": {
                    "endpoints": total,
                    "happy_2xx": happy_ok,
                    "fuzz_cases": fuzz_total,
                    "fuzz_500": fuzz_500,
                },
                "happy": happy,
                "fuzz": fuzz,
            },
            ensure_ascii=False,
            indent=2,
        )
    )

    # history append
    history = skill_dir / "history.md"
    line = (
        f"\n## {ts} ({base})\n"
        f"- happy 2xx: **{happy_ok}/{total}** → API 200 OK 점수 **{api_ok_score}/30**\n"
        f"- fuzz 500: **{fuzz_500}/{fuzz_total}** → 예외처리 점수 **{exc_score}/20**\n"
        f"- 합계: **{total_score}/50**\n"
        f"- 상세: `runs/{ts}.json`\n"
    )
    with history.open("a") as f:
        f.write(line)

    print(f"저장: {out.relative_to(skill_dir)}", file=sys.stderr)

    # 실패한 엔드포인트 / 500 발생 케이스 상세
    bad_happy = [r for r in happy if not (200 <= r["status"] < 300)]
    if bad_happy:
        print("\n[happy 비-2xx]", file=sys.stderr)
        for r in bad_happy:
            print(
                f"  {r['method']} {r['path']} → {r['status']}  :: {r['preview'][:120]}",
                file=sys.stderr,
            )

    bad_fuzz = [
        (r["path"], r["method"], c)
        for r in fuzz
        for c in r["cases"]
        if c["status"] == 500
    ]
    if bad_fuzz:
        print("\n[fuzz 500 발생 — 수정 필요]", file=sys.stderr)
        for path, method, c in bad_fuzz:
            print(
                f"  {method} {path} [{c['case']}] → 500  :: {c['preview'][:120]}",
                file=sys.stderr,
            )


def main():
    p = argparse.ArgumentParser()
    p.add_argument("env", nargs="?", default="local", choices=["local", "prod"])
    args = p.parse_args()
    base = (
        "https://filmo-api.log8.kr" if args.env == "prod" else "http://localhost:8080"
    )
    run(base)


if __name__ == "__main__":
    main()
