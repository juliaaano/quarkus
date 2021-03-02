# Quarkus Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
mvn quarkus:dev
```

## Docker Login Red Hat Registry

```
docker login registry.redhat.io
```

## Packaging and running the application

The application can be packaged using `mvn package`.
It produces the `juliaaano-quarkus-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/juliaaano-quarkus-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `mvn package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `mvn package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/juliaaano-quarkus-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Container images

Build and push of containers images are powered by Jib: https://quarkus.io/guides/container-image#jib.

See [application.properties](./src/main/resources/application.properties) for container image options.

```
mvn clean package -Dquarkus.container-image.build=true
mvn clean package -Dquarkus.container-image.push=true
```

For native executable, combine with the options `-Pnative -Dquarkus.native.container-build=true`.

```
mvn clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
```

Run the container:

```
docker-compose up -d
```

## OpenShift

These instructions are intended to be used in testing and development. A different and more comprehensive approach must be considered for CI/CD.

### JVM build

```
oc new-build --binary=true --docker-image=registry.redhat.io/ubi8/openjdk-11 --name=juliaaano-quarkus --labels="app=juliaaano-quarkus"
oc start-build juliaaano-quarkus --from-dir . --follow
```

### Native build

```
mvn clean package -Pnative -Dquarkus.native.container-build=true
oc new-build --binary=true --docker-image=quay.io/quarkus/ubi-quarkus-native-binary-s2i:1.0 --name=juliaaano-quarkus --labels="app=juliaaano-quarkus"
oc start-build juliaaano-quarkus --from-file ./target/juliaaano-quarkus-1.0-SNAPSHOT-runner --follow
```

### Deployment

```
oc apply -f manifests/
oc set image deployment juliaaano-quarkus app=$(oc get istag juliaaano-quarkus:latest -o jsonpath='{.image.dockerImageReference}')
oc scale deployment juliaaano-quarkus --replicas 2
oc expose service juliaaano-quarkus
curl "http://$(oc get route juliaaano-quarkus -o jsonpath='{.spec.host}')/q/health"
```

### Clean up

```
oc delete all -l app=juliaaano-quarkus
```

## Keycloak and JWT RBAC Security

From https://quarkus.io/guides/security-jwt.

Run Keycloak as a container and manually import [keycloak-realm.json](./config/keycloak-realm.json).

```
docker-compose up -d keycloak
```

Retrieve JWT access tokens and use them:

```
export access_token=$(\
    curl -X POST http://localhost:50102/auth/realms/quarkus/protocol/openid-connect/token \
    --user quarkus-api-test-client:"" \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=bob&password=password&grant_type=password&scope=api.pets:read'' | jq --raw-output '.access_token' \
)
curl -v http://localhost:8080/pets -H "Authorization: Bearer "$access_token

export access_token=$(\
    curl -X POST http://localhost:50102/auth/realms/quarkus/protocol/openid-connect/token \
    --user quarkus-api-test-client:"" \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=alice&password=password&grant_type=password&scope=api.pets:read api.pets:write api.pets:erase' | jq --raw-output '.access_token' \
)
curl -v -X POST http://localhost:8080/pets -H "Authorization: Bearer "$access_token -H "Content-Type: application/json" -d '{"species":"bird","breed":"krakatoo"}'
```
