#!/usr/bin/env bash
set -euo pipefail
SKILL_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV="${1:-local}"
exec python3 "$SKILL_DIR/scripts/lib/test.py" "$ENV"
