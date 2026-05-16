# 📋 CMC Hackathon — 서버 평가 체크리스트

> 배점 100점 기준. 각 항목에 우리 산출물/근거 매핑.

---

## 1. 인프라 및 DB 구성이 적절한가? — **20점**

> 인프라 구성도와 ERD를 제출해야 하며, 구성도를 토대로 WAS 파일을 업로드 한다면 이 역시 어떻게 구성했는가 등등 평가.
> **(인프라 구성도와 ERD를 제출하지 않은 경우 0점)**

### ✅ 산출물

- [x] **인프라 구성도**: [`docs/architecture.png`](./architecture.png)
- [x] **ERD**: https://www.erdcloud.com/p/g8Bmpb4XjALXSo8sc
- [x] **WAS 구성**:
  - AWS EC2 (ap-northeast-2, t-class)
  - Docker / docker-compose 기반 컨테이너 운영
  - Traefik v3 reverse proxy (`:80`)
  - **Blue-Green 무중단 배포** (`scripts/deploy.sh` + `docker-compose.{blue,green}.yml`)
  - MySQL 8.0 (Docker volume 영속화)
- [x] **Edge**: Cloudflare Tunnel(cloudflared) → HTTPS 종단
- [x] **CDN/Frontend Hosting**: Vercel (Next.js 16)

### ✅ 차별점

- 단일 컨테이너가 아닌 **blue/green 슬롯 + Traefik 라우팅 swap** 으로 무중단 배포 구현
- `forward-headers-strategy: native` 로 Cloudflare → Traefik → Spring chain X-Forwarded-Proto 신뢰

---

## 2. 코드가 잘 작성되었는가? — **30점**

> 코드를 확인할 수 있는 레포지토리 URL 을 제공해야 함. **(제공하지 않는 경우 0점)**

### ✅ 산출물

- [x] **레포 URL**: https://github.com/IISweetHeartII/CMC-Hackathon (또는 팀 organization URL)

### ✅ 코드 품질 포인트

- [x] **계층 분리**: `controller` / `service` / `repository` / `entity` / `dto`
- [x] **글로벌 공통**: `global/` 에 ErrorCode / ApiResponse / GlobalExceptionHandler / BaseEntity 통합
- [x] **응답 표준화**: 모든 API `ApiResponse<T>` 단일 포맷 (`code`, `message`, `data`)
- [x] **인증/인가**: Spring Security + JWT 필터 + `AuthenticationEntryPoint` / `AccessDeniedHandler`
- [x] **Auditing**: `BaseEntity` (`createdAt`, `updatedAt`) JPA Auditing
- [x] **Convention**: Lombok (`@Getter`, `@Builder`, `@RequiredArgsConstructor`), `@NoArgsConstructor(access=PROTECTED)`
- [x] **Swagger 통합**: Bearer JWT Authorize 버튼 활성

---

## 3. API는 200 OK인 경우 올바르게 작동하는가? — **30점**

> 심사 가능한 배포 URL 을 제공해야 함. **(제공하지 않는 경우 0점)**
> 테스팅이 어려운 경우 감점. 200 OK인 경우에 500 에러가 생기면 감점.
> 전체 API 갯수 대비 에러난 API 비율로 점수 부여.

### ✅ 산출물

- [x] **배포 URL**: https://filmo-api.log8.kr
- [x] **API 명세서 (Swagger UI)**: https://filmo-api.log8.kr/swagger-ui/index.html
- [x] **OpenAPI JSON**: https://filmo-api.log8.kr/v3/api-docs

### ✅ 정상 동작 보장

- [x] **헬스체크**: `GET /health` → 200
- [x] **Docker healthcheck**: 컨테이너 자체 헬스 검증 후 Traefik swap (deploy.sh)
- [x] **JPA Auditing 활성**: `@EnableJpaAuditing` 적용
- [x] **DB connection**: docker network `cmc-net` 내부 통신, 외부 노출 X

### 🔁 배포 전 셀프 점검

```bash
# 헬스체크
curl -fsS https://filmo-api.log8.kr/health

# Swagger 로딩
curl -fsS https://filmo-api.log8.kr/v3/api-docs | jq '.paths | keys'

# 주요 GET 엔드포인트 200 확인
curl -fsS https://filmo-api.log8.kr/api/movies
curl -fsS https://filmo-api.log8.kr/api/theaters
```

---

## 4. API 예외처리가 적절히 되어있는가? — **20점**

> 테스트 시 파라미터 정보를 임의로 넣어서 테스트할 예정.
> 쿼리 스트링을 이상하게 / http body를 이상하게 호출 시도.
> **동작 기댓값과 다를 경우 감점.**

### ✅ 글로벌 핸들러 커버리지 (`global/exception/GlobalExceptionHandler.java`)

| 시도하는 공격                 | 던지는 예외                               | 우리 응답                           |
| ----------------------------- | ----------------------------------------- | ----------------------------------- |
| JSON body 망가뜨림 (`{"x":}`) | `HttpMessageNotReadableException`         | `400 INVALID_INPUT`                 |
| 필수 필드 누락 (`@Valid`)     | `MethodArgumentNotValidException`         | `400 INVALID_INPUT` + 필드별 메시지 |
| 필수 쿼리 파라미터 누락       | `MissingServletRequestParameterException` | `400 MISSING_PARAMETER`             |
| 쿼리 타입 불일치 (`?id=abc`)  | `MethodArgumentTypeMismatchException`     | `400 TYPE_MISMATCH`                 |
| `@Validated` path/param 위반  | `ConstraintViolationException`            | `400 INVALID_INPUT` + 필드별 메시지 |
| 존재하지 않는 URL             | `NoResourceFoundException`                | `404 NOT_FOUND`                     |
| 허용 안 된 HTTP 메서드        | `HttpRequestMethodNotSupportedException`  | `405 METHOD_NOT_ALLOWED`            |
| 토큰 없음 / 만료              | (Spring Security entry point)             | `401 UNAUTHORIZED`                  |
| 권한 부족                     | (Spring Security access denied)           | `403 FORBIDDEN`                     |
| 비즈니스 규칙 위반            | `BusinessException(ErrorCode)`            | 도메인별 HTTP + 코드                |
| 그 외 모든 미처리 예외        | `Exception` (fallback)                    | `500 INTERNAL_SERVER_ERROR`         |

### ✅ 일관된 응답 포맷

모든 에러도 `ApiResponse<T>` 포맷 유지 → 클라이언트가 단일 파서로 처리 가능.

```json
{
  "code": 1003,
  "message": "잘못된 입력입니다.",
  "data": { "nickname": "닉네임은 필수입니다." }
}
```

### ✅ 로깅

모든 예외에 `log.warn` / `log.error` 로 `method + URI + 메시지` 출력 → 심사 중 어떤 호출이 어떻게 실패했는지 추적 가능.

### 🔁 셀프 적대적 테스트 (배포 전)

```bash
BASE=https://filmo-api.log8.kr

# 1. 존재하지 않는 경로
curl -s -o /dev/null -w "%{http_code}\n" $BASE/api/nope          # 401 또는 404 (보호경로면 401)

# 2. 메서드 불일치
curl -s -X DELETE -o /dev/null -w "%{http_code}\n" $BASE/health  # 405

# 3. 잘못된 JSON body
curl -s -X POST -H 'Content-Type: application/json' -d '{bad json' \
     -o /dev/null -w "%{http_code}\n" $BASE/api/auth/login       # 400

# 4. 필드 누락 (login)
curl -s -X POST -H 'Content-Type: application/json' -d '{}' \
     $BASE/api/auth/login | jq                                    # 400 + 필드별 메시지

# 5. 타입 불일치 (Long path에 문자)
curl -s -o /dev/null -w "%{http_code}\n" $BASE/api/movies/abc    # 400

# 6. 토큰 없이 보호 경로
curl -s -o /dev/null -w "%{http_code}\n" $BASE/api/users/me      # 401

# 7. 잘못된 토큰으로 보호 경로
curl -s -H 'Authorization: Bearer broken' -o /dev/null -w "%{http_code}\n" $BASE/api/users/me  # 401
```

→ 위 케이스 모두 **500 절대 안남** 보장 (감점 0).

---

## 📌 제출 체크리스트 (D-day)

- [ ] 인프라 구성도 PNG (`docs/architecture.png`)
- [ ] ERD URL 명시 (README + 제출 폼)
- [ ] 레포 URL 명시
- [ ] 배포 URL 동작 (`/health` 200 확인)
- [ ] Swagger UI 접근 가능
- [ ] API 명세서 (Swagger) URL 제출
- [ ] 위 셀프 적대적 테스트 7종 통과
- [ ] README 최신화 (도메인, 팀, 기능)
