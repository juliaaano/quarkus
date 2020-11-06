package au.sa.gov.rest;

import static io.restassured.RestAssured.given;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PetsResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/pets")
          .then()
             .statusCode(200);
    }

}