package pet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PetResourceTest {

    @Test
    public void get_pets() {
        given()
        .when()
            .get("/pets")
        .then()
            .statusCode(200)
            .body("$.size()", is(2),
                  "species", containsInAnyOrder("Cat", "Dog"),
                  "breed", containsInAnyOrder("Persian Cat", "Labrador"));
    }
}
