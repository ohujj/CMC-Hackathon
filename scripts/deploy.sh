#!/usr/bin/env bash
set -euo pipefail

cd /home/ec2-user/CMC-Hackathon
set -a; source .env; set +a

# 현재 활성 슬롯 파악
CURRENT=$(grep -oE 'cmc-hackathon-(blue|green)' traefik/dynamic.yml | head -1 | sed 's/cmc-hackathon-//')
CURRENT=${CURRENT:-blue}
if [ "$CURRENT" = "blue" ]; then NEXT=green; else NEXT=blue; fi
echo ">>> current=$CURRENT next=$NEXT"

# 1) 인프라 보장 (Traefik + DB)
docker-compose -f docker-compose.db.yml --env-file .env up -d

# 2) idle 슬롯 새 이미지로 기동
docker-compose -f docker-compose.${NEXT}.yml --env-file .env pull
docker-compose -f docker-compose.${NEXT}.yml --env-file .env up -d --force-recreate

# 3) healthcheck 통과 대기
STATUS="starting"
for i in $(seq 1 40); do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' cmc-hackathon-${NEXT} 2>/dev/null || echo "starting")
  echo "[$i] $NEXT health=$STATUS"
  [ "$STATUS" = "healthy" ] && break
  sleep 3
done
[ "$STATUS" = "healthy" ] || { echo "ERR: $NEXT not healthy"; docker logs --tail=50 cmc-hackathon-${NEXT}; exit 1; }

# 4) Traefik 라우팅 전환 (atomic write + traefik restart로 확실히 reload)
TMP=$(mktemp)
sed "s|cmc-hackathon-${CURRENT}|cmc-hackathon-${NEXT}|g" traefik/dynamic.yml > "$TMP"
mv "$TMP" traefik/dynamic.yml
docker restart traefik >/dev/null
echo ">>> switched to $NEXT (traefik reloaded)"

# Traefik이 새 backend 잡을 시간 + healthcheck pass
sleep 8

# 5) 구 슬롯 정리
docker-compose -f docker-compose.${CURRENT}.yml --env-file .env down
echo ">>> deploy complete: $NEXT is active"
