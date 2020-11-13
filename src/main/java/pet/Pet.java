package pet;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Pet details.")
public class Pet {

    @Schema(example = "Cat")
    private String species;

    @Schema(example = "Tuxedo")
    private String breed;

    @Schema(example = "Felix")
    private String name;

    public Pet() {
    }

    public Pet(String species, String breed, String name) {
        this.species = species;
        this.breed = breed;
        this.name = name;
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
}
