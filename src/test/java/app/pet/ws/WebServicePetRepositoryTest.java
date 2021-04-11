package app.pet.ws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import app.pet.Pet;
import app.pet.PetRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(WebServiceTestProfile.class)
@QuarkusTestResource(WiremockPets.class)
public class WebServicePetRepositoryTest {

    @Inject
    PetRepository repository;

    @Test
    void get_pet() {

        Optional<Pet> pet = repository.find("123456789");

        assertTrue(pet.isPresent());
        assertEquals("123456789", pet.get().getIdentifier());
    }

    @Test
    void get_pets() {


        repository.find();
        // final var pet = Pet.pet("a_species", "a_breed", "a_name");

    }
}
