package app.pet;

import static java.util.UUID.randomUUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Pet details.")
public class Pet {

    @Schema(example = "73df7903-33b0-4203-b87e-26034e106542", readOnly = true)
    private String identifier;

    @NotBlank
    @Schema(example = "Cat", required = true)
    private String species;

    @NotBlank
    @Size(max = 40)
    @Schema(example = "Tuxedo", required = true)
    private String breed;

    @Size(min = 2, max = 40)
    @Schema(example = "Felix")
    private String name;

    @Schema(hidden = true)
    private boolean sampleField;

    private Pet(final String identifier) {
        this.identifier = identifier;
    }

    private Pet() {
        this(randomUUID().toString());
    }

    public static Pet pet(final String identifier, final String species, final String breed, final String name) {

        final Pet pet = new Pet(identifier);

        pet.species = species;
        pet.breed = breed;
        pet.name = name;

        return pet;
    }

    public static Pet pet(final String species, final String breed, final String name) {
        return pet(randomUUID().toString(), species, breed, name);
    }

    Pet clone(final String identifier) {

        final Pet cloned = new Pet(identifier);

        cloned.species = this.species;
        cloned.breed = this.breed;
        cloned.name = this.name;

        return cloned;
    }

    Pet merge(final Pet pet) {

        final Pet merged = new Pet(this.identifier);

        merged.species = pet.species != null ? pet.species : this.species;
        merged.breed = pet.breed != null ? pet.breed : this.breed;
        merged.name = pet.name != null ? pet.name : this.name;

        return merged;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pet other = (Pet) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        return true;
    }

    public static int getAmount() {
        return 3000 / 0;
    }
}
