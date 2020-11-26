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
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PetResourceTest {

@Test
    void get_pets() {

        given()
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
            .pathParam("id", identifier)
        .when()
            .delete("/pets/{id}")
        .then()
            .statusCode(204);
    }
}
