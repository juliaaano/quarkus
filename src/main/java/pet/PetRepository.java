package pet;

import java.util.Optional;
import java.util.Set;

public interface PetRepository {

    public Optional<Pet> find(String identifier);

    public Set<Pet> find();

    public String create(Pet pet);

    public void delete(String identifier);
}
