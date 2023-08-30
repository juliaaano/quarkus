#!/bin/bash

set -euxo pipefail

docker compose version

docker compose up --detach keycloak postgresql
sleep 9
docker compose run --rm liquibase

mvn --show-version clean verify -Pnative

echo "end of test-native.sh" > /dev/null
