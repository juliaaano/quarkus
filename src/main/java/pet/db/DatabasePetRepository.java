package pet.db;

import static java.util.stream.Collectors.toList;
import static pet.db.PetEntity.petEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.transaction.Transactional;
import pet.Pet;
import pet.PetRepository;

@Alternative
@ApplicationScoped
class DatabasePetRepository implements PetRepository {

    @Override
    public Optional<Pet> find(final String identifier) {
        final PetEntity entity = PetEntity.findById(identifier);
        return entity != null ? Optional.of(entity.map()) : Optional.empty();
    }

    @Override
    public List<Pet> find() {
        final Stream<PetEntity> stream = PetEntity.streamAll();
        return stream.map(PetEntity::map).collect(toList());
    }

    @Override
    @Transactional
    public String create(final Pet pet) {
        final PetEntity entity = petEntity(pet);
        entity.persist();
        return pet.getIdentifier();
    }

    @Override
    public void delete(final String identifier) {
        PetEntity.deleteById(identifier);
    }
}
