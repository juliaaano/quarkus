package pet.db;

import static pet.Pet.pet;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import pet.Pet;

@Entity
@Table(name = "pet")
class PetEntity extends PanacheEntityBase {

    @Id
    public String identifier;

    public String species;
    public String breed;
    public String name;

    PetEntity() {
    }

    static PetEntity petEntity(final Pet pet) {

        final PetEntity entity = new PetEntity();

        entity.identifier = pet.getIdentifier();
        entity.species = pet.getSpecies();
        entity.breed = pet.getBreed();
        entity.name = pet.getName();

        return entity;
    }

    Pet map() {
        return pet(this.identifier, this.species, this.breed, this.name);
    }
}
