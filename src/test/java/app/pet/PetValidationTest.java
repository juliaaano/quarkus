package app.pet;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PetValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void pet_is_valid() {
        assertEquals(0, violations(pet().build()), "Pet should be valid.");
    }

    @Test
    public void pet_has_invalid_identifier() {

        var pet_01 = pet().identifier(null).build();
        var pet_02 = pet().identifier("").build();
        var pet_03 = pet().identifier("not-uuid").build();

        final String message = "[%s] Pet.identifier should be invalid.";

        assertEquals(1, violations(pet_01), format(message, "pet_01"));
        assertEquals(1, violations(pet_02), format(message, "pet_02"));
        assertEquals(1, violations(pet_03), format(message, "pet_03"));
    }

    @Test
    public void pet_has_invalid_species() {

        var pet_01 = pet().species(null).build();
        var pet_02 = pet().species("").build();

        final String message = "[%s] Pet.species should be invalid.";

        assertEquals(1, violations(pet_01), format(message, "pet_01"));
        assertEquals(1, violations(pet_02), format(message, "pet_02"));
    }

    @Test
    public void pet_has_invalid_breed() {

        var pet_01 = pet().breed(null).build();
        var pet_02 = pet().breed("").build();
        var pet_03 = pet().breed("A".repeat(41)).build();

        final String message = "[%s] Pet.breed should be invalid.";

        assertEquals(1, violations(pet_01), format(message, "pet_01"));
        assertEquals(1, violations(pet_02), format(message, "pet_02"));
        assertEquals(1, violations(pet_03), format(message, "pet_03"));
    }

    @Test
    public void pet_has_invalid_name() {

        var pet_01 = pet().name("").build();
        var pet_02 = pet().name("A").build();
        var pet_03 = pet().name("A".repeat(41)).build();

        final String message = "[%s] Pet.name should be invalid.";

        assertEquals(1, violations(pet_01), format(message, "pet_01"));
        assertEquals(1, violations(pet_02), format(message, "pet_02"));
        assertEquals(1, violations(pet_03), format(message, "pet_03"));
    }

    private int violations(Pet pet) {
        return validator.validate(pet).size();
    }

    private Builder pet() {
        return new Builder().pet();
    }

    private static final class Builder {

        private String identifier;
        private String species;
        private String breed;
        private String name;

        private Pet build() {

            return Pet.pet(
                this.identifier,
                this.species,
                this.breed,
                this.name
            );
        }

        private Builder pet() {

            this.identifier = randomUUID().toString();
            this.species = "Alien";
            this.breed = "Unknown";
            this.name = "Stranger";
            return this;
        }

        private Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        private Builder species(String species) {
            this.species = species;
            return this;
        }

        private Builder breed(String breed) {
            this.breed = breed;
            return this;
        }

        private Builder name(String name) {
            this.name = name;
            return this;
        }
    }
}
