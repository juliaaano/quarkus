package app.pet.mem;

import java.util.Set;
import app.pet.Pet;

public abstract class MemoryPetRepositoryMocks extends MemoryPetRepository {

    public static MemoryPetRepositoryMocks throwRuntimException() {
        return new MemoryPetRepositoryMocks() {
            @Override
            public Set<Pet> find() {
                throw new RuntimeException("Unexpected runtime exception", new Throwable("Underlying cause"));
            }
        };
    }

    public static MemoryPetRepositoryMocks throwRuntimeExceptionEmpty() {
        return new MemoryPetRepositoryMocks() {
            @Override
            public Set<Pet> find() {
                throw new RuntimeException();
            }
        };
    }
}
