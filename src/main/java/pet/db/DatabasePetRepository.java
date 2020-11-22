package pet.db;

import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import pet.Pet;
import pet.PetRepository;

@Alternative
@ApplicationScoped
public class DatabasePetRepository implements PetRepository {

    @Override
    public Optional<Pet> find(final String identifier) {
        return Optional.of(new Pet("db", "db", "db"));
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
