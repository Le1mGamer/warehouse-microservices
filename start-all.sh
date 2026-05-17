#!/usr/bin/env bash
# Запускає всі 3 мікросервіси у фоні. Логи у /tmp/lab4-logs/.
set -e

if [ -d /opt/homebrew/opt/openjdk@17 ]; then
  export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
  export PATH="$JAVA_HOME/bin:$PATH"
fi

ROOT="$(cd "$(dirname "$0")" && pwd)"
mkdir -p /tmp/lab4-logs

echo "Starting inventory-service (port 8081)…"
( cd "$ROOT/inventory-service" && mvn -q -DskipTests spring-boot:run > /tmp/lab4-logs/inventory.log 2>&1 ) &
echo "Starting supply-service (port 8082)…"
( cd "$ROOT/supply-service"    && mvn -q -DskipTests spring-boot:run > /tmp/lab4-logs/supply.log    2>&1 ) &
echo "Starting api-gateway   (port 8080)…"
( cd "$ROOT/api-gateway"       && mvn -q -DskipTests spring-boot:run > /tmp/lab4-logs/gateway.log   2>&1 ) &

echo "Waiting until all services are UP…"
for url in http://localhost:8081/actuator/health \
           http://localhost:8082/actuator/health \
           http://localhost:8080/actuator/health; do
  until curl -fsS "$url" >/dev/null 2>&1; do sleep 2; done
  echo "  $url OK"
done
echo "All services are up. Logs at /tmp/lab4-logs/."
