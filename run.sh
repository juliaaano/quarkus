#!/bin/bash

set -euxo pipefail

docker-compose version

docker-compose up --detach keycloak postgresql pgadmin

sleep 9

docker-compose run --rm liquibase

APP_IMAGE=${1:-ghcr.io/juliaaano/quarkus:latest} docker-compose up --detach app

docker-compose ps

echo "Check this out: http://localhost:50101"

echo "end of run.sh" > /dev/null
