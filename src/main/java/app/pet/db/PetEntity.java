package app.pet.db;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import app.Entity;
import app.pet.Pet;

@jakarta.persistence.Entity
@Table(name = "pet")
class PetEntity extends Entity {

    @Id
    public String identifier;

    public String species;
    public String breed;
    public String name;

    static PetEntity petEntity(final Pet pet) {

        final var entity = new PetEntity();

        entity.identifier = pet.getIdentifier();
        entity.species = pet.getSpecies();
        entity.breed = pet.getBreed();
        entity.name = pet.getName();

        return entity;
    }

    Pet map() {

        return Pet.pet(this.identifier, this.species, this.breed, this.name);
    }

    void overwrite(final Pet pet) {

        this.species = pet.getSpecies();
        this.breed = pet.getBreed();
        this.name = pet.getName();
    }
}
