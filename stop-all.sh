#!/usr/bin/env bash
# Зупиняє всі 3 мікросервіси (за прив'язкою до портів).
for port in 8080 8081 8082; do
  pid=$(lsof -ti :$port 2>/dev/null || true)
  if [ -n "$pid" ]; then
    echo "Killing pid $pid on port $port"
    kill "$pid" 2>/dev/null || true
  else
    echo "Port $port already free"
  fi
done
