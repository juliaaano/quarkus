package app.pet;

import static java.util.UUID.randomUUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Pet details.")
public class Pet {

    @Schema(example = "73df7903-33b0-4203-b87e-26034e106542", readOnly = true)
    private final String identifier;

    @Schema(example = "Cat")
    private String species;

    @Schema(example = "Tuxedo")
    private String breed;

    @Schema(example = "Felix")
    private String name;

    @Schema(hidden = true)
    private boolean sampleField;

    public Pet() {
        this.identifier = randomUUID().toString();
    }

    private Pet(final String identifier) {
        this.identifier = identifier;
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

    public String getIdentifier() {
        return identifier;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
