package app.pet.ws;

import static app.pet.Pet.pet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import app.pet.Pet;
import app.pet.PetRepository;

@Alternative
@ApplicationScoped
class WebServicePetRepository implements PetRepository {

    @Override
    public Optional<Pet> find(final String identifier) {
        return Optional.of(pet("ws", "ws", "ws"));
    }

    @Override
    public Set<Pet> find() {
        return new HashSet<>();
    }

    @Override
    public String create(final Pet pet) {
        return null;
    }

    @Override
    public Pet update(Pet pet) {
        return null;
    }

    @Override
    public boolean replace(Pet pet) {
        return false;
    }

    @Override
    public void delete(final String identifier) {

    }
}
