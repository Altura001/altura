#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
DOTNET_DIR="$ROOT_DIR/backend-dotnet"

echo "Stopping web and backend dev processes..."
pkill -f "next dev" 2>/dev/null || true
pkill -f "medusa develop" 2>/dev/null || true
pkill -f "next-server" 2>/dev/null || true

kill_port_listener() {
  local port="$1"
  local pids

  pids="$(ss -ltnp 2>/dev/null | awk -v port=":${port}" '$4 ~ port { if (match($0, /pid=[0-9]+/)) { print substr($0, RSTART + 4, RLENGTH - 4) } }' | sort -u | tr '\n' ' ')"

  if [ -n "${pids// }" ]; then
    echo "Killing listeners on :$port -> $pids"
    kill $pids 2>/dev/null || true
    sleep 1
    kill -9 $pids 2>/dev/null || true
  fi
}

kill_port_listener 8000
kill_port_listener 9000

echo "Stopping Medusa backend Docker services (postgres + redis)..."
(cd "$BACKEND_DIR" && docker compose down)

echo "Stopping .NET backend Docker stack (postgres + api)..."
(cd "$DOTNET_DIR" && docker compose down)

echo "Done."
