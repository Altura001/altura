#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# BACKEND_DIR="$ROOT_DIR/backend"
# BACKEND_ENV_FILE="$BACKEND_DIR/.env"
# BACKEND_ENV_TEMPLATE="$BACKEND_DIR/.env.template"
DOTNET_DIR="$ROOT_DIR/backend-dotnet"

# echo "Starting Medusa backend dependencies (Postgres + Redis)..."
# (cd "$BACKEND_DIR" && docker compose up -d postgres redis)

echo "Starting .NET backend stack (Postgres :5433 + API :8080)..."
(cd "$DOTNET_DIR" && docker compose up -d --build)

# if [ ! -f "$BACKEND_ENV_FILE" ]; then
#   cp "$BACKEND_ENV_TEMPLATE" "$BACKEND_ENV_FILE"
#   echo "Created backend/.env from .env.template"
# fi

# echo "Starting backend on http://localhost:9000 ..."
# (cd "$BACKEND_DIR" && npm run dev) &
# BACKEND_PID=$!

# echo "Starting web on http://localhost:8000 ..."
# "$ROOT_DIR/start-web.sh" &
# WEB_PID=$!

# cleanup() {
#   trap - INT TERM EXIT
#   echo ""
#   echo "Stopping backend and web..."
#   kill "$WEB_PID" "$BACKEND_PID" 2>/dev/null || true
#   wait "$WEB_PID" "$BACKEND_PID" 2>/dev/null || true
# }

trap cleanup INT TERM EXIT

echo ""
echo "Services are starting."
# echo "  Medusa API : http://localhost:9000"
echo "  .NET API   : http://localhost:8080  (health: /health, openapi: /openapi/v1.json)"
echo "  Web        : http://localhost:8000"
echo "Press Ctrl+C to stop the Medusa dev server and web (Docker stacks keep running; use ./stop-all.sh)."
wait -n "$BACKEND_PID" "$WEB_PID"
echo ""
echo "One service exited. Shutting down the other..."
