#!/bin/bash

set -euxo pipefail

docker-compose version

docker-compose up --detach keycloak postgresql pgadmin

sleep 9

docker-compose run --rm --name liquibase liquibase

APP_IMAGE=$1 docker-compose up --detach app

docker-compose ps

echo "end of run.sh" > /dev/null
