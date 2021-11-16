#!/bin/bash

set -euxo pipefail

mvn --show-version clean package -DskipTests -Dquarkus.container-image.build=true
# mvn --show-version clean package -DskipTests -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true -Dquarkus.container-image.image=localhost/juliaaano/quarkus-native

echo "end of build.sh" > /dev/null
