package app.pet;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyString;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
class PetResourceTest {

    @Test
    void get_pets() {

        given()
            .auth().oauth2(jwt("bob"))
        .when()
            .get("/pets")
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("$.size()", is(5),
                  "species", containsInAnyOrder("Cat", "Cat", "Dog", "Dog", "Pig"),
                  "breed", containsInAnyOrder("Mixed", "Persian Cat", "Labrador", "Stray", "Mini-Pig"));
    }

    @Test
    void get_pet() {

        given()
            .auth().oauth2(jwt("alice")) // ensure user can retrieve only it's own pet
            .pathParam("id", "2df2973a-bf2e-4c4e-a0e4-6fdfa0d1b242")
        .when()
            .get("/pets/{id}")
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("species", equalTo("Cat"),
                  "breed", equalTo("Persian Cat"),
                  "name", equalTo("Garfield"));
    }

    @Test
    void post_n_delete_pet() {

        final String identifier =
        given()
            .auth().oauth2(jwt("alice"))
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .post("/pets")
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
        .when()
            .delete("/pets/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    void put_n_delete_pet() {

        final String identifier = randomUUID().toString();

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .put("/pets/{id}")
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
            .put("/pets/{id}")
        .then()
            .statusCode(204)
            .header("Location", response -> endsWith("/pets/" + identifier))
            .body(emptyString());

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
        .when()
            .get("/pets/{id}")
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("species", equalTo("Cat"),
                  "breed", equalTo("Birman"),
                  "name", equalTo("Boris"));

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
        .when()
            .delete("/pets/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    void post_patch_n_delete_pet() {

        final String identifier =
        given()
            .auth().oauth2(jwt("alice"))
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .post("/pets")
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
            .patch("/pets/{id}")
        .then()
            .statusCode(204)
            .header("Location", response -> endsWith("/pets/" + identifier))
            .body(emptyString());

        given()
            .auth().oauth2(jwt("bob"))
            .pathParam("id", identifier)
        .when()
            .get("/pets/{id}")
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("species", equalTo("Cat"),
                  "breed", equalTo("Birman"),
                  "name", equalTo("Boris"));

        given()
            .auth().oauth2(jwt("alice"))
            .pathParam("id", identifier)
        .when()
            .delete("/pets/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    void unauthorized() {

        given()
        .when()
            .get("/pets")
        .then()
            .statusCode(401);

        given()
        .when()
            .get("/pets/123456789")
        .then()
            .statusCode(401);

        given()
            .contentType(JSON)
        .when()
            .post("/pets")
        .then()
            .statusCode(401);

        given()
            .contentType(JSON)
        .when()
            .put("/pets/123456789")
        .then()
            .statusCode(401);

        given()
            .contentType(JSON)
        .when()
            .patch("/pets/123456789")
        .then()
            .statusCode(401);

        given()
        .when()
            .delete("/pets/123456789")
        .then()
            .statusCode(401);
    }

    @Test
    void forbidden() {

        given()
            .auth().oauth2(jwt("joe"))
        .when()
            .get("/pets")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("joe"))
        .when()
            .get("/pets/123456789")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
            .contentType(JSON)
        .when()
            .post("/pets")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
            .contentType(JSON)
        .when()
            .put("/pets/123456789")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
            .contentType(JSON)
        .when()
            .patch("/pets/123456789")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("bob"))
        .when()
            .delete("/pets/123456789")
        .then()
            .statusCode(403);
    }

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
}
