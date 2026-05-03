#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Info  { param($msg) Write-Host "[setup] $msg" -ForegroundColor Green }
function Warn  { param($msg) Write-Host "[setup] $msg" -ForegroundColor Yellow }
function Fail  { param($msg) Write-Host "[setup] $msg" -ForegroundColor Red; exit 1 }

# docker check
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Fail "Docker is not installed. Download from https://docs.docker.com/get-docker/"
}
docker compose version 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Fail "Docker Compose v2 is required. Update Docker Desktop."
}

# env
if (-not (Test-Path .env)) {
    Copy-Item .env.example .env
    Warn ".env created from .env.example"
    Warn "Open .env and fill in IYZICO_API_KEY, IYZICO_SECRET_KEY, and change the passwords."
    Warn "Then re-run this script."
    exit 0
}

# iyzico keys check
$requiredKeys = @('IYZICO_API_KEY', 'IYZICO_SECRET_KEY')
$missing = @()
foreach ($key in $requiredKeys) {
    $line = Get-Content .env | Where-Object { $_ -match "^${key}=(.*)$" } | Select-Object -First 1
    if (-not $line -or ($line -split '=', 2)[1] -eq '') {
        $missing += $key
    }
}
if ($missing.Count -gt 0) {
    Fail "The following required values are empty in .env: $($missing -join ', ')"
}

Info "Pulling images..."
docker compose pull

Info "Starting all services (this may take a minute on first run)..."
docker compose up -d

Info "Waiting for services to become healthy..."
Start-Sleep -Seconds 10

Info "Done! Services:"
Write-Host "  Frontend  ->  http://localhost"
Write-Host "  API       ->  http://localhost/api"
Write-Host "  Keycloak  ->  http://localhost:8180"
Write-Host "  RabbitMQ  ->  http://localhost:15672"
Write-Host ""
Write-Host "Run 'docker compose logs -f' to follow logs."
Write-Host "Run 'docker compose down' to stop."
