package pet.mem;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;
import static pet.Pet.pet;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import pet.Pet;
import pet.PetRepository;

@Alternative
@ApplicationScoped
class MemoryPetRepository implements PetRepository {

    private Set<Pet> pets = newSetFromMap(synchronizedMap(new LinkedHashMap<>()));

    public MemoryPetRepository() {
        pets.add(pet("Dog", "Labrador", null));
        pets.add(pet("Cat", "Persian Cat", "Garfield"));
    }

    @Override
    public Optional<Pet> find(final String identifier) {
        return pets.stream().filter(pet -> pet.getIdentifier().equals(identifier)).findFirst();
    }

    @Override
    public Set<Pet> find() {
        return pets;
    }

    @Override
    public String create(final Pet pet) {
        pets.add(pet);
        return pet.getIdentifier();
    }

    @Override
    public void delete(final String identifier) {
        pets.removeIf(existingPet -> existingPet.getIdentifier().contentEquals(identifier));
    }
}
