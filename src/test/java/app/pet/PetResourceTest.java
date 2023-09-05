package app.pet;

import static app.pet.mem.MemoryPetRepositoryMocks.throwRuntimException;
import static app.pet.mem.MemoryPetRepositoryMocks.throwRuntimeExceptionEmpty;
import static io.quarkus.test.junit.QuarkusMock.installMockForInstance;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import java.util.Map;
import java.util.Set;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
class PetResourceTest extends PetResourceBase {

    @Override
    protected String jwt(final String user) {
        return Jwt
            .issuer("https://example.com/issuer")
            .audience("theaudience")
            .upn(user)
            .groups(roles(user))
            .sign();
    }

    private Set<String> roles(final String user) {
        return Map.of(
            "alice", Set.of("PETS_CREATE", "PETS_READ", "PETS_UPDATE", "PETS_DELETE"),
            "bob", Set.of("PETS_READ"),
            "joe", Set.of("stranger")
        ).get(user);
    }

    @Inject
    PetRepository repository;

    @Test
    void runtime_exception_thrown() {

        installMockForInstance(throwRuntimException(), repository);

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
        .when()
            .get()
        .then()
            .statusCode(500)
            .contentType(JSON)
            .body("message", is("Unexpected runtime exception"))
            .body("details[0]", is("Underlying cause"));
    }

    @Test
    void runtime_exception_empty_thrown() {

        installMockForInstance(throwRuntimeExceptionEmpty(), repository);

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
        .when()
            .get()
        .then()
            .statusCode(500)
            .contentType(JSON)
            .body("message", is("Empty"),
                  "$", hasKey("details"));
    }

    @Test
    void get_pet_not_found() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", randomUUID().toString())
        .when()
            .get("/{id}")
        .then()
            .statusCode(404)
            .body(is(emptyOrNullString()));
    }

    @Test
    void not_found_exception() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
        .when()
            .get("/path/not-found")
        .then()
            .statusCode(404)
            .body(is(emptyOrNullString()));
    }
}
