@echo off
REM Запуск трьох сервісів у трьох окремих вікнах cmd.
REM Використання:  start-all.bat
REM Вимога: JDK 17 у PATH. Maven НЕ потрібен — використовується mvnw.cmd з кожної папки.

setlocal
set ROOT=%~dp0

echo Starting inventory-service (port 8081)...
start "inventory-service (8081)" cmd /k "cd /d %ROOT%inventory-service && mvnw.cmd -DskipTests spring-boot:run"

echo Starting supply-service (port 8082)...
start "supply-service (8082)" cmd /k "cd /d %ROOT%supply-service && mvnw.cmd -DskipTests spring-boot:run"

echo Starting api-gateway (port 8080)...
start "api-gateway (8080)" cmd /k "cd /d %ROOT%api-gateway && mvnw.cmd -DskipTests spring-boot:run"

echo.
echo Three windows opened. Wait ~30 seconds for all services to start.
echo Check health endpoints manually:
echo   curl http://localhost:8081/actuator/health
echo   curl http://localhost:8082/actuator/health
echo   curl http://localhost:8080/actuator/health
endlocal
