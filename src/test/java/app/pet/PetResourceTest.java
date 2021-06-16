package app.pet;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
@TestHTTPEndpoint(PetResource.class)
class PetResourceTest {

    @BeforeAll
    static void beforeAll() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void get_pets() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("bob"))
        .when()
            .get()
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("$.size()", is(5),
                  "identifier", hasSize(5),
                  "species", containsInAnyOrder("Cat", "Cat", "Dog", "Dog", "Pig"),
                  "breed", containsInAnyOrder("Mixed", "Persian Cat", "Labrador", "Stray", "Mini-Pig"),
                  "find { it.breed == 'Stray' }", hasKey("species"),
                  "find { it.breed == 'Stray' }", not(hasKey("name")));
        // @formatter:on
    }

    @Test
    void get_pet() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice")) // ensure user can retrieve only it's own pet
            .pathParam("id", "2df2973a-bf2e-4c4e-a0e4-6fdfa0d1b242")
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("identifier", is("2df2973a-bf2e-4c4e-a0e4-6fdfa0d1b242"),
                  "species", is("Cat"),
                  "breed", is("Persian Cat"),
                  "name", is("Garfield"));
    }

    @Test
    void post_n_delete_pet() {

        // @formatter:off
        final String identifier =
        given()
            .auth().oauth2(jwt("alice"))
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .post()
        .then()
            .statusCode(201)
            .contentType(TEXT)
            .header("Location", response -> endsWith("/api/pets/" + response.asString()))
            .body(not(emptyString()))
        .extract()
            .asString();

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
        .when()
            .delete("/{id}")
        .then()
            .statusCode(204);
        // @formatter:on
    }

    @Test
    void put_n_delete_pet() {

        final String identifier = randomUUID().toString();

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .put("/{id}")
        .then()
            .statusCode(201)
            .contentType(TEXT)
            .header("Location", response -> endsWith("/pets/" + response.asString()))
            .body(not(emptyString()));

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Birman", "name", "Boris"))
        .when()
            .put("/{id}")
        .then()
            .statusCode(204)
            .header("Location", response -> endsWith("/pets/" + identifier))
            .body(emptyString());

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("species", is("Cat"),
                  "breed", is("Birman"),
                  "name", is("Boris"));

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
        .when()
            .delete("/{id}")
        .then()
            .statusCode(204);
        // @formatter:on
    }

    @Test
    void post_patch_n_delete_pet() {

        // @formatter:off
        final String identifier =
        given()
            .auth().oauth2(jwt("alice"))
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .post()
        .then()
            .statusCode(201)
            .contentType(TEXT)
            .header("Location", response -> endsWith("/pets/" + response.asString()))
            .body(not(emptyString()))
        .extract()
            .asString();

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
            .contentType(JSON)
            .body(Map.of("breed", "Birman", "name", "Boris"))
        .when()
            .patch("/{id}")
        .then()
            .statusCode(200)
            .body("species", is("Cat"),
                  "breed", is("Birman"),
                  "name", is("Boris"));

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
        .when()
            .delete("/{id}")
        .then()
            .statusCode(204);
        // @formatter:on
    }

    @Test
    void put_with_invalid_identifier() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", "not-a-uuid")
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .put("/{id}")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("parameterViolations", hasSize(1))
            .body("parameterViolations[0].path", is("put.identifier"))
            .body("parameterViolations[0].message", is("Must match UUID format."));
        // @formatter:on
    }

    @Test
    void put_with_invalid_pet_in_es_ES() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", randomUUID().toString())
            .header("Accept-Language", "es-ES")
            .contentType(JSON)
            .body(Map.of("species", "Cat", "name", "Felix"))
        .when()
            .put("/{id}")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("parameterViolations", hasSize(1))
            .body("parameterViolations[0].path", is("put.pet.breed"))
            .body("parameterViolations[0].message", is("no debe estar vacío"));
        // @formatter:on
    }

    @Test
    void put_with_invalid_pet() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", randomUUID().toString())
            .contentType(JSON)
            .body(Map.of("species", "", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .put("/{id}")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("parameterViolations", hasSize(1))
            .body("parameterViolations[0].path", is("put.pet.species"))
            .body("parameterViolations[0].message", is("must not be blank"));
        // @formatter:on
    }

    @Test
    void post_with_invalid_pet() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("alice"))
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "", "name", "Felix", "identifier", "it-should-be-ignored"))
        .when()
            .post()
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("parameterViolations", hasSize(1))
            .body("parameterViolations[0].path", is("post.pet.breed"))
            .body("parameterViolations[0].message", is("must not be blank"));
        // @formatter:on
    }

    @Test
    void unauthorized() {

        // @formatter:off
        given()
        .when()
            .get()
        .then()
            .statusCode(401);

        given()
        .when()
            .get("/123456789")
        .then()
            .statusCode(401);

        given()
            .contentType(JSON)
        .when()
            .post()
        .then()
            .statusCode(401);

        given()
            .contentType(JSON)
        .when()
            .put("/123456789")
        .then()
            .statusCode(401);

        given()
            .contentType(JSON)
        .when()
            .patch("/123456789")
        .then()
            .statusCode(401);

        given()
        .when()
            .delete("/123456789")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void forbidden() {

        // @formatter:off
        given()
            .auth().oauth2(jwt("joe"))
        .when()
            .get()
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("joe"))
        .when()
            .get("/123456789")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
            .contentType(JSON)
        .when()
            .post()
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
            .contentType(JSON)
        .when()
            .put("/123456789")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
            .contentType(JSON)
        .when()
            .patch("/123456789")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
        .when()
            .delete("/123456789")
        .then()
            .statusCode(403);
        // @formatter:on
    }

    // @formatter:off
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
    // @formatter:on
}
