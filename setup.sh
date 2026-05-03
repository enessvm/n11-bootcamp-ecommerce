#!/usr/bin/env bash
set -euo pipefail

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
info()  { echo -e "${GREEN}[setup]${NC} $*"; }
warn()  { echo -e "${YELLOW}[setup]${NC} $*"; }
error() { echo -e "${RED}[setup]${NC} $*" >&2; exit 1; }

# check docker exists
command -v docker >/dev/null 2>&1 || error "Docker is not installed. https://docs.docker.com/get-docker/"
docker compose version >/dev/null 2>&1 || error "Docker Compose v2 is required. Update Docker Desktop or install the plugin."

# env
if [ ! -f .env ]; then
  cp .env.example .env
  warn ".env created from .env.example"
  warn "Open .env and fill in IYZICO_API_KEY, IYZICO_SECRET_KEY, and change the passwords."
  warn "Then re-run this script."
  exit 0
fi

# iyzico keys check
REQUIRED_KEYS=(IYZICO_API_KEY IYZICO_SECRET_KEY)
MISSING=()
for KEY in "${REQUIRED_KEYS[@]}"; do
  VALUE=$(grep "^${KEY}=" .env | cut -d= -f2-)
  [ -z "$VALUE" ] && MISSING+=("$KEY")
done
if [ ${#MISSING[@]} -gt 0 ]; then
  error "The following required values are empty in .env: ${MISSING[*]}"
fi

info "Pulling images..."
docker compose pull

info "Starting all services (this may take a minute on first run)..."
docker compose up -d

info "Waiting for services to become healthy..."
sleep 10

info "Done! Services:"
echo "  Frontend  →  http://localhost"
echo "  API       →  http://localhost/api"
echo "  Keycloak  →  http://localhost:8180"
echo "  RabbitMQ  →  http://localhost:15672  (${RABBITMQ_USERNAME:-guest} / see .env)"
echo ""
echo "Run 'docker compose logs -f' to follow logs."
echo "Run 'docker compose down' to stop."
