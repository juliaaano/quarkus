package pet.ws;

import static pet.Pet.pet;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import pet.Pet;
import pet.PetRepository;

@Alternative
@ApplicationScoped
class WebServicePetRepository implements PetRepository {

    @Override
    public Optional<Pet> find(final String identifier) {
        return Optional.of(pet("ws", "ws", "ws"));
    }

    @Override
    public Set<Pet> find() {
        return null;
    }

    @Override
    public String create(final Pet pet) {
        return null;
    }

    @Override
    public void delete(final String identifier) {

    }
}
