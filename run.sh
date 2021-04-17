#!/bin/bash

set -euxo pipefail

docker-compose version

docker-compose up --detach keycloak postgresql pgadmin

mvn --show-version clean package -DskipTests -Dquarkus.container-image.build=true
# mvn --show-version clean package -DskipTests -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true

docker-compose run --rm --name liquibase liquibase

docker-compose up --detach app

docker-compose ps

echo "end of run.sh" > /dev/null
