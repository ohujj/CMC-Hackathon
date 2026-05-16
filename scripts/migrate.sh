#!/usr/bin/env bash
# 최초 1회만 실행. 기존 단일 컨테이너 구조를 Traefik 블루그린으로 전환.
set -euo pipefail
cd /home/ec2-user/CMC-Hackathon

# 기존 컨테이너 정리
docker rm -f cmc-hackathon cmc-mysql 2>/dev/null || true
docker network create cmc-net 2>/dev/null || true

# 인프라 + 첫 슬롯 가동
docker compose -f docker-compose.db.yml --env-file .env up -d
bash scripts/deploy.sh
