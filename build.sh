#!/bin/bash

set -euxo pipefail

mvn --show-version clean package -DskipTests -Dquarkus.container-image.build=true -Dquarkus.container-image.image=docker.io/juliaaano/quarkus:local
# mvn --show-version clean package -DskipTests -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true -Dquarkus.container-image.image=docker.io/juliaaano/quarkus-native:local

echo "end of build.sh" > /dev/null
