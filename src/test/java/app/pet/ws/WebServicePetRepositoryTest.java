package app.pet.ws;

import static app.pet.Pet.pet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
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
    void find_pet() {

        final var pet = repository.find("123456789");

        assertThat(pet).isPresent();

        assertThat(pet.get().getIdentifier()).isEqualTo("123456789");
        assertThat(pet.get().getSpecies()).isEqualTo("DOG");
        assertThat(pet.get().getBreed()).isEqualTo("Husky");
        assertThat(pet.get().getName()).isEqualTo("Max");
    }

    @Test
    void find_pet_not_found() {

        assertThat(repository.find("not-found")).isEmpty();
    }

    @Test
    void find_pet_server_error() {

        assertThrows(WebApplicationException.class, () -> {
            repository.find("error");
        });
    }

    @Test
    void find_pet_network_fault() {

        assertThrows(ProcessingException.class, () -> {
            repository.find("fault");
        });
    }

    @Test
    void find_pet_timeout() {

        assertThrows(ProcessingException.class, () -> {
            repository.find("timeout");
        });
    }

    @Test
    void find_pets() {

        assertThat(repository.find())
            .containsExactlyInAnyOrder(
                pet("123456789", "DOG", "Husky", "Max"),
                pet("111199999", "Turtle", "NONE", "Oliver"),
                pet("987654321", "Cat", "Burmilla", "Lisa")
            );
    }

    @Test
    void create_pet() {

        assertThat(repository.create(pet("123456789", "DOG", "Husky", "Max")))
            .contains("created");
    }
}
