package app.pet.mem;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedMap;
import static app.pet.Pet.pet;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import app.pet.Pet;
import app.pet.PetRepository;

@Alternative
@ApplicationScoped
class MemoryPetRepository implements PetRepository {

    private Set<Pet> pets = newSetFromMap(synchronizedMap(new LinkedHashMap<>()));

    MemoryPetRepository() {
        pets.add(pet("1f1518c8-0d92-4694-8dc3-118d82547e8d", "Dog", "Labrador", "Max"));
        pets.add(pet("f6184860-a801-446e-8f43-5c39bd1a77ac", "Dog", "Stray", null));
        pets.add(pet("2df2973a-bf2e-4c4e-a0e4-6fdfa0d1b242", "Cat", "Persian Cat", "Garfield"));
        pets.add(pet("e03c3727-186e-439d-a0f9-b5e6d9048c97", "Pig", "Mini-Pig", "Baby"));
        pets.add(pet("c6451685-279a-48c7-aca2-02ebf39a2bec", "Cat", "Mixed", "Grump y Cat"));

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
    public String save(final Pet pet) {
        pets.remove(pet);
        pets.add(pet);
        return pet.getIdentifier();
    }

    @Override
    public boolean replace(final Pet pet) {
        final boolean removed = pets.remove(pet);
        pets.add(pet);
        return removed;
    }

    @Override
    public void delete(final String identifier) {
        pets.removeIf(existingPet -> existingPet.getIdentifier().contentEquals(identifier));
    }
}
