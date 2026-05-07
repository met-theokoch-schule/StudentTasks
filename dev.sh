#!/usr/bin/env bash

set -euo pipefail

PROFILE="${1:-staging}"
POLL_INTERVAL="${POLL_INTERVAL:-1}"

SERVER_PID=""
WATCHER_PID=""

calc_fingerprint() {
  find src/main/java src/main/resources \
    -type f \
    ! -path '*/target/*' \
    -printf '%T@ %p\n' 2>/dev/null | sha1sum | cut -d' ' -f1
}

cleanup() {
  local pids=()

  [[ -n "${SERVER_PID}" ]] && pids+=("${SERVER_PID}")
  [[ -n "${WATCHER_PID}" ]] && pids+=("${WATCHER_PID}")

  if ((${#pids[@]} > 0)); then
    kill "${pids[@]}" 2>/dev/null || true
    wait "${pids[@]}" 2>/dev/null || true
  fi
}

watch_sources() {
  local last_fingerprint current_fingerprint
  last_fingerprint="$(calc_fingerprint)"

  while true; do
    sleep "${POLL_INTERVAL}"
    current_fingerprint="$(calc_fingerprint)"

    if [[ "${current_fingerprint}" != "${last_fingerprint}" ]]; then
      echo "[dev] Source change detected, running mvn compile"
      mvn compile
      last_fingerprint="${current_fingerprint}"
    fi
  done
}

trap cleanup EXIT INT TERM

watch_sources &
WATCHER_PID=$!

mvn spring-boot:run \
  -Dspring-boot.run.profiles="${PROFILE}" \
  -Dspring-boot.run.addResources=true \
  -Dspring-boot.run.jvmArguments="-Dspring.thymeleaf.cache=false" &
SERVER_PID=$!

wait -n "${SERVER_PID}" "${WATCHER_PID}"
