package app.pet.db;

import static app.pet.Pet.pet;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.inject.Inject;
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

        final var pet = pet("a_species", "a_breed", "a_name");

        repository.create(pet);

        assertEquals(1, PetEntity.count());

        final PetEntity entity = PetEntity.findAll().firstResult();

        assertNotNull(entity.createdAt, "Entity.createdAt is null.");
        assertNotNull(entity.updatedAt, "Entity.createdAt is null.");
        assertEquals(entity.createdAt, entity.updatedAt, "Entity.createdAt and updatedAt must be equal.");

        assertEquals("a_species", entity.species);
        assertEquals("a_breed", entity.breed);
        assertEquals("a_name", entity.name);
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

        assertEquals(1, PetEntity.count());

        final PetEntity entity = PetEntity.findAll().firstResult();

        assertTrue(entity.updatedAt.compareTo(entity.createdAt) > 0, "Entity.updatedAt must be at a later time compared to createdAt.");
        assertEquals("x_name", entity.name);
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

        assertTrue(replaced, "Entity should have been replaced.");
        assertEquals(1, PetEntity.count());

        final PetEntity entity = PetEntity.findAll().firstResult();

        assertEquals(entity.createdAt, entity.updatedAt, "Entity.createdAt and updatedAt must be equal.");
        assertEquals("x_name", entity.name);
    }
}
