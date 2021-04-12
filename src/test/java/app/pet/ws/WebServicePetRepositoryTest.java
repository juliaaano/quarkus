package app.pet.ws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
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
        assertEquals("dog", pet.get().getSpecies());
        assertEquals("husky", pet.get().getBreed());
        assertEquals("max", pet.get().getName());
    }

    @Test
    void get_pet_not_found() {

        assertTrue(repository.find("not-found").isEmpty());
    }

    @Test
    void get_pet_server_error() {

        assertThrows(WebApplicationException.class, () -> {
            repository.find("error");
        });
    }

    @Test
    void get_pet_network_fault() {

        assertThrows(ProcessingException.class, () -> {
            repository.find("fault");
        });
    }

    @Test
    void get_pet_timeout() {

        assertThrows(ProcessingException.class, () -> {
            repository.find("timeout");
        });
    }

    @Test
    void get_pets() {


        repository.find();
    }
}
