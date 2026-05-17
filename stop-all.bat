@echo off
REM Зупиняє процеси на портах 8080, 8081, 8082.
for %%P in (8080 8081 8082) do (
    for /f "tokens=5" %%A in ('netstat -aon ^| findstr ":%%P " ^| findstr "LISTENING"') do (
        echo Killing pid %%A on port %%P
        taskkill /F /PID %%A 2>nul
    )
)
