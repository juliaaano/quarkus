package app.pet;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(pet().violations()).isZero();
    }

    @Test
    public void pet_species_is_valid() {

        assertThat(pet().species(null).violations())
            .as("Pet.species is null")
            .isOne();

        assertThat(pet().species("").violations())
            .as("Pet.species is blank")
            .isOne();
    }

    @Test
    public void pet_breed_is_valid() {

        assertThat(pet().breed(null).violations())
            .as("Pet.breed is null")
            .isOne();

        assertThat(pet().breed("").violations())
            .as("Pet.breed is blank")
            .isOne();

        assertThat(pet().breed("A".repeat(41)).violations())
            .as("Pet.breed is too long")
            .isOne();
    }

    @Test
    public void pet_name_is_valid() {

        assertThat(pet().name(null).violations())
            .as("Pet.name is null")
            .isZero();

        assertThat(pet().name("").violations())
            .as("Pet.name is blank")
            .isOne();

        assertThat(pet().name("A").violations())
            .as("Pet.name is too short")
            .isOne();

        assertThat(pet().name("A".repeat(41)).violations())
            .as("Pet.name is too long")
            .isOne();
    }

    private PetValidator pet() {
        return new PetValidator(validator).pet();
    }
    private static final class PetValidator {

        private String identifier;
        private String species;
        private String breed;
        private String name;

        private final Validator validator;

        public PetValidator(final Validator validator) {
            this.validator = validator;
        }

        private int violations() {
            return validator.validate(this.build()).size();
        }

        private Pet build() {

            return Pet.pet(
                this.identifier,
                this.species,
                this.breed,
                this.name
            );
        }

        private PetValidator pet() {

            this.identifier = randomUUID().toString();
            this.species = "Alien";
            this.breed = "Unknown";
            this.name = "Stranger";
            return this;
        }

        private PetValidator species(String species) {
            this.species = species;
            return this;
        }

        private PetValidator breed(String breed) {
            this.breed = breed;
            return this;
        }

        private PetValidator name(String name) {
            this.name = name;
            return this;
        }
    }
}
