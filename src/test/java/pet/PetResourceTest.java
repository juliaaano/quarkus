package pet;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.TEXT;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyString;
import java.util.Map;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
class PetResourceTest {

    @Test
    void get_pets() {

        given()
            .auth().oauth2(jwt("alice"))
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
            .auth().oauth2(jwt("alice")) // ensure user can retrieve only it's own pet, admins can get it all
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
            .auth().oauth2(jwt("admin"))
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
            .auth().oauth2(jwt("admin"))
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
        .when()
            .delete("/pets/123456789")
        .then()
            .statusCode(401);
    }

    @Test
    @DisabledOnNativeImage
    void forbidden() {

        given()
            .auth().oauth2(jwt("stranger"))
        .when()
            .get("/pets")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("stranger"))
        .when()
            .get("/pets/123456789")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("alice"))
            .contentType(JSON)
        .when()
            .post("/pets")
        .then()
            .statusCode(403);

        given()
            .auth().oauth2(jwt("alice"))
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
            .groups(role(user))
            .sign();
    }

    private String role(final String user) {
        return Map.of(
            "admin", "admin",
            "alice", "user",
            "stranger", "stranger"
        ).get(user);
    }
}
