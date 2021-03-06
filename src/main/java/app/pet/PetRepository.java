package app.pet;

import java.util.Optional;

public interface PetRepository {

    public Optional<Pet> find(String identifier);

    public Iterable<Pet> find();

    public String create(Pet pet);

    public Pet update(Pet pet);

    public boolean replace(Pet pet);

    public void delete(String identifier);
}
