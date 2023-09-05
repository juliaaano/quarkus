package app.pet.db;

import static app.pet.Pet.pet;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import app.pet.PetRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(DatabaseTestProfile.class)
@TestTransaction
public class DatabasePetRepositoryTest {

    @Inject
    PetRepository repository;

    @Test
    void create_pet() {

        repository.create(pet("a_species", "a_breed", "a_name"));

        assertThat(PetEntity.count()).isOne();

        final PetEntity entity = PetEntity.findAll().firstResult();

        assertThat(entity.createdAt)
            .as("createdAt is null")
            .isNotNull();

        assertThat(entity.updatedAt)
            .as("updatedAt is null")
            .isNotNull();

        assertEquals(entity.createdAt, entity.updatedAt, "createdAt and updatedAt must be equal.");

        assertThat(entity.species).isEqualTo("a_species");
        assertThat(entity.breed).isEqualTo("a_breed");
        assertThat(entity.name).isEqualTo("a_name");
    }

    @Test
    void update_pet() {

        final PetEntity pet = new PetEntity();
        pet.identifier = randomUUID().toString();
        pet.species = "a_species";
        pet.breed = "a_breed";
        pet.name = "a_name";
        pet.persist();

        repository.update(pet(pet.identifier, pet.species, pet.breed, "x_name"));

        assertThat(PetEntity.count()).isOne();

        final PetEntity entity = PetEntity.findAll().firstResult();

        assertThat(entity.updatedAt)
            .as("updatedAt must be at a later time compared to createdAt")
            .isAfter(entity.createdAt);

        assertThat(entity.name).isEqualTo("x_name");
    }

    @Test
    void replace_pet() {

        final PetEntity pet = new PetEntity();
        pet.identifier = randomUUID().toString();
        pet.species = "a_species";
        pet.breed = "a_breed";
        pet.name = "a_name";
        pet.persist();

        boolean replaced = repository.replace(pet(pet.identifier, pet.species, pet.breed, "x_name"));

        assertThat(replaced)
            .as("entity should have been replaced")
            .isTrue();

        assertThat(PetEntity.count()).isOne();

        final PetEntity entity = PetEntity.findAll().firstResult();

        assertEquals(entity.createdAt, entity.updatedAt, "Entity.createdAt and updatedAt must be equal.");
        assertThat(entity.name).isEqualTo("x_name");
    }
}
