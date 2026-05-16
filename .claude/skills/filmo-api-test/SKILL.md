---
name: filmo-api-test
description: Swagger(OpenAPI) spec을 읽어 Filmo의 모든 API를 자동 테스트. (1) 200 OK 정상동작 비율, (2) 잘못된 입력에 대한 예외처리(500 발생 여부) 두 평가축에 점수 부여. 평가 기준 채점표(API 200 OK 30점 + 예외처리 20점)를 그대로 시뮬레이션.
argument-hint: "[local|prod] (기본: local)"
---

# Filmo API 채점 시뮬레이터

## 목적

해커톤 평가 기준 두 가지를 자체 검증한다.

| 채점 요소                                | 배점 | 검증 방법                                                                            |
| ---------------------------------------- | ---- | ------------------------------------------------------------------------------------ |
| API가 200 OK인 경우 올바르게 작동하는가? | 30   | **happy path 모드** — 합리적 입력으로 호출 → 2xx 비율                                |
| API 예외처리가 적절히 되어있는가?        | 20   | **fuzz 모드** — 이상한 쿼리/바디로 호출 → **500 발생 0건** + 4xx로 친절히 떨어지는지 |

테스트가 어려워 감점, 500이 나면 감점, 동작 기댓값과 다르면 감점. 따라서 **500은 절대 안 남**이 핵심.

## 실행

```bash
# 로컬 (기본)
bash .claude/skills/filmo-api-test/scripts/run.sh

# 프로덕션
bash .claude/skills/filmo-api-test/scripts/run.sh prod
```

## 출력

- **stderr**: 엔드포인트별 라이브 표 (happy 200 / fuzz 500 카운트)
- `runs/<UTC>.json`: 전체 상세 + 모든 응답 본문
- `history.md`: 매 실행 요약 append (점수 추이)

## 점수 계산

- **API_OK_SCORE** = 30 × (happy 2xx 수 / 전체 엔드포인트 수)
- **EXC_HANDLE_SCORE** = 20 × (1 - fuzz 500 발생 비율)
  - fuzz에서 500이 단 한 번이라도 나면 비율만큼 감점
  - 모든 fuzz가 4xx로 떨어지면 만점

## happy 케이스 생성 규칙

OpenAPI spec(`/v3/api-docs`)을 읽어:

- **GET**: required 쿼리/path 채움 (string→`"test"`, int→`1`, theaCd→실제 DB 값)
- **POST/PATCH/DELETE**: requestBody schema 따라 minimal valid 입력
- **인증 필요**: 자동 signup → JWT 캐시 → `Authorization: Bearer ...`
- **path variable**: 실제 DB에 있는 seq/theaCd 사용 (사전 조회)

## fuzz 케이스 (각 엔드포인트마다)

1. **빈 바디** (POST/PATCH/DELETE)
2. **필수 필드 누락**
3. **타입 깨짐** (string 자리에 숫자, 숫자 자리에 string)
4. **음수/0 page·size**
5. **없는 path variable** (`/api/movies/999999999999`)
6. **SQL injection** (`' OR 1=1--`)
7. **XSS** (`<script>alert(1)</script>`)
8. **JWT 없음** (인증 필요한 곳)
9. **깨진 JWT** (`Bearer junk`)

각 fuzz 응답이 **2xx → ANOMALY (이상하게 통과)**, **4xx → PASS**, **5xx → FAIL**.

## 평가 모드 — Swagger 시뮬

평가자가 Swagger UI에서 누르는 흐름을 그대로 따라간다:

1. spec 로드
2. 엔드포인트 순회
3. 각 엔드포인트마다 "정상 입력 1회" + "이상 입력 N회"
4. 결과 표 + 점수 산출

## 누적 구조

- `SKILL.md` — 불변 지침 (이 파일)
- `history.md` — 점수 변화 추이
- `runs/<UTC>.json` — 원본 (포렌식)

## 사용 시점

- 백엔드 API 변경 후 회귀 확인
- 평가 제출 전 최종 점검
- 새 엔드포인트 추가 시 자동 커버

## 주의

- **인증 토큰은 매 실행마다 새로 발급** (다른 테스트 사용자, 충돌 방지)
- 실제 DB에 saved_theater/users 데이터 잔존함 — fuzz로 만든 가짜 유저는 그대로 남음
- prod 모드는 실데이터에 영향 — 회원가입 한 번씩 누적되니 주의
