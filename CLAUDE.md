# CMC Hackathon — 배포 및 인프라 정리

## 절대 하지 말 것

> **EC2에서 `docker run` 직접 실행 금지.**
> CI/CD(`docker-compose`)가 관리하는 컨테이너 이름(`cmc-hackathon`, `cmc-mysql`)과 충돌해서 다음 배포가 터진다.
> 컨테이너 설정 변경이 필요하면 `docker-compose.yml` 수정 후 커밋 → 자동 배포로만 반영할 것.

## 프로젝트 구조

```
CMC-Hackathon/
├── src/                  # Spring Boot 백엔드 (Java 21)
├── web/                  # Next.js 16 프론트엔드
├── scripts/              # 크롤링 스크립트 (Python)
├── docker-compose.yml    # EC2 운영용 (app + db)
├── docker-compose.local.yml  # 로컬 개발용 (db만)
└── Dockerfile            # Spring Boot 멀티스테이지 빌드
```

## 운영 URL

| 서비스        | URL                                                                         |
| ------------- | --------------------------------------------------------------------------- |
| API (Swagger) | https://filmo-api.log8.kr/swagger-ui/index.html                             |
| 프론트엔드    | https://filmo.log8.kr (DNS 설정 완료 후) / https://web-agentgram.vercel.app |

## CI/CD (GitHub Actions)

`main` 브랜치 push 시 자동 실행:

1. Gradle 빌드 (`./gradlew clean build`)
2. Docker 이미지 빌드 → Docker Hub (`hyeonwoooh/cmc-hackathon`) push
3. EC2 SSH 접속 → `git reset --hard origin/main` → `docker-compose pull & up`

워크플로우 파일: `.github/workflows/deploy.yml`

## EC2 서버

- **IP**: 3.34.97.209
- **접속**: `ssh -i ~/.ssh/id_ed25519 ec2-user@3.34.97.209`
- **리전**: ap-northeast-2 (서울)
- **레포 경로**: `/home/ec2-user/CMC-Hackathon`
- **`.env` 위치**: `/home/ec2-user/CMC-Hackathon/.env`

### EC2 실행 중인 컨테이너

```
cmc-hackathon   Spring Boot :8080
cmc-mysql       MySQL 8.0
```

### EC2 `.env` 필수 항목

```
MYSQL_ROOT_PASSWORD=...
MYSQL_DATABASE=cmc
JWT_SECRET=...
JWT_EXPIRATION=86400000
```

## Cloudflare Tunnel

- **터널명**: `filmo-api`
- **터널 ID**: `0719d482-62d5-4db4-b04f-457fc7ba54a9`
- **라우팅**: `filmo-api.log8.kr` → EC2 `localhost:8080`
- **EC2 서비스**: `sudo systemctl status cloudflared`
- **설정 파일**: `/home/ec2-user/.cloudflared/config.yml`
- **credentials**: `/home/ec2-user/.cloudflared/tunnel.json`
- **맥북 cert**: `~/.cloudflared/cert.pem`

### Cloudflare Tunnel 재시작

```bash
ssh -i ~/.ssh/id_ed25519 ec2-user@3.34.97.209
sudo systemctl restart cloudflared
```

## Vercel (프론트엔드)

- **팀**: agentgram
- **프로젝트**: web
- **배포 URL**: https://web-agentgram.vercel.app
- **커스텀 도메인**: `filmo.log8.kr` (Cloudflare DNS 설정 필요)

### 수동 재배포

```bash
cd web && vercel --prod
```

### Cloudflare에 추가해야 할 DNS 레코드 (filmo.log8.kr 연결용)

```
TXT   _vercel.log8.kr   vc-domain-verify=filmo.log8.kr,adb4dc3cca337153ee65
CNAME filmo              cname.vercel-dns.com   (Proxy OFF)
```

### 환경변수

```
NEXT_PUBLIC_API_URL=https://filmo-api.log8.kr
```

## 로컬 개발

```bash
# DB만 올리기
docker compose -f docker-compose.local.yml up -d

# 백엔드 실행
./gradlew bootRun

# 프론트엔드 실행
cd web && npm run dev
```

## 주요 설정 파일

### SecurityConfig.java

- `/`, `/swagger-ui/**`, `/v3/api-docs/**`, `/api/auth/**` → 인증 불필요
- CORS: `allowedOriginPatterns("*")`, credentials 허용

### application.yml (prod 프로파일)

- DB/JPA 설정은 환경변수로 주입
- `server.forward-headers-strategy: native` — Cloudflare Tunnel HTTPS 인식용

### docker-compose.yml

- `app`: Spring Boot (build from Dockerfile)
- `db`: MySQL 8.0 (healthcheck 포함)
- `web`: Next.js (Vercel로 분리 운영, compose에는 정의만 있음)
