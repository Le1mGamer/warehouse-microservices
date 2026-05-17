# Запуск трьох сервісів у трьох окремих вікнах PowerShell.
# Використання:  .\start-all.ps1
# Вимога: JDK 17 у PATH. Maven НЕ потрібен — використовується mvnw.cmd з кожної папки.

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path

function Start-Service-Window([string]$name, [string]$dir, [int]$port) {
    Write-Host "Starting $name (port $port)..."
    $cmd = "cd `"$dir`"; `$Host.UI.RawUI.WindowTitle = '$name (port $port)'; .\mvnw.cmd -DskipTests spring-boot:run"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd | Out-Null
}

Start-Service-Window "inventory-service" "$root\inventory-service" 8081
Start-Service-Window "supply-service"    "$root\supply-service"    8082
Start-Service-Window "api-gateway"       "$root\api-gateway"       8080

Write-Host ""
Write-Host "Three PowerShell windows opened. Waiting for all services to become UP..."
$urls = @(
    "http://localhost:8081/actuator/health",
    "http://localhost:8082/actuator/health",
    "http://localhost:8080/actuator/health"
)
foreach ($u in $urls) {
    while ($true) {
        try {
            $r = Invoke-WebRequest -Uri $u -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
            if ($r.StatusCode -eq 200) {
                Write-Host "  $u OK"
                break
            }
        } catch {
            Start-Sleep -Seconds 2
        }
    }
}
Write-Host ""
Write-Host "All services are up." -ForegroundColor Green
Write-Host "Use stop-all.ps1 to stop them."
