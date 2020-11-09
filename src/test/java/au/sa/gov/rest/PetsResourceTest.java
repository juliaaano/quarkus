package au.sa.gov.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PetsResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
        .when()
            .get("/pets")
        .then()
            .statusCode(200)
            .body("$.size()", is(3),
                  "species", containsInAnyOrder("-Cat-", "-Dog-", "-Dog-"),
                  "breed", containsInAnyOrder("Persian Cat", "Labrador", "Stray"));
    }
}
