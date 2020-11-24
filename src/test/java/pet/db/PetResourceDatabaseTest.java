package pet.db;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.Map;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(DatabaseH2TestProfile.class)
public class PetResourceDatabaseTest {

    @Inject
    UserTransaction transaction;

    @Test
    public void get_pets() {

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
    public void get_pet() {

        given()
        .when()
            .get("/pets/2df2973a-bf2e-4c4e-a0e4-6fdfa0d1b242")
        .then()
            .statusCode(200)
            .contentType(JSON)
            .body("name", equalTo("Garfield"),
                  "species", equalTo("Cat"),
                  "breed", equalTo("Persian Cat"));
    }

    @Test
    public void post_pet() {

        given()
            .contentType(JSON)
            .body(Map.of("species", "Cat", "breed", "Tuxedo Cat", "name", "Felix"))
        .when()
            .post("/pets")
        .then()
            .statusCode(201)
            .contentType(TEXT)
            .header("Location", response -> endsWith("/pets/" + response.asString()))
            .body(not(emptyString()));
        
        final PetEntity entity = PetEntity.find("name", "Felix").singleResult();

        assertEquals("Cat", entity.species);
        assertEquals("Tuxedo Cat", entity.breed);

        assertNotNull(entity.identifier);

        assertNotNull(entity.createdAt);
        assertNotNull(entity.updatedAt);

        transaction(() -> PetEntity.deleteById(entity.identifier));
    }

    @Test
    public void delete_pet() {

        final String identifier = randomUUID().toString();

        final PetEntity entity = new PetEntity();
        entity.identifier = identifier;
        entity.species = "extinct";
        entity.breed = "test";
        entity.name = "kenny";

        transaction(entity::persist);

        given()
            .pathParam("id", identifier)
        .when()
            .delete("/pets/{id}")
        .then()
            .statusCode(204);

        assertNull(PetEntity.findById(identifier), "Entity not deleted.");
    }

    private void transaction(Runnable function) {

        try {
            transaction.begin();
            function.run();
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
