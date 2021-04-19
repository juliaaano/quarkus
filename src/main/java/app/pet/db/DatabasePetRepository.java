package app.pet.db;

import static app.pet.db.PetEntity.petEntity;
import static java.util.stream.Collectors.toList;
import static org.eclipse.microprofile.metrics.MetricUnits.NONE;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.transaction.Transactional;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import app.pet.Pet;
import app.pet.PetRepository;

@Alternative
@ApplicationScoped
class DatabasePetRepository implements PetRepository {

    private int totalNumberOfPets = 0;

    @Gauge(name = "totalNumberOfPets", description = "Total number of pets retrieved last query.", unit = NONE)
    public int totalNumberOfPets() {
        return totalNumberOfPets;
    }

    @Override
    public Optional<Pet> find(final String identifier) {
        final PetEntity entity = PetEntity.findById(identifier);
        return entity != null ? Optional.of(entity.map()) : Optional.empty();
    }

    @Override
    public List<Pet> find() {
        final Stream<PetEntity> stream = PetEntity.streamAll();
        final List<Pet> pets = stream.map(PetEntity::map).collect(toList());
        totalNumberOfPets = pets.size();
        return pets;
    }

    @Override
    @Transactional
    public String create(final Pet pet) {
        final PetEntity entity = petEntity(pet);
        entity.persist();
        return entity.identifier;
    }

    @Override
    @Transactional
    public Pet update(final Pet pet) {
        final PetEntity entity = PetEntity.findById(pet.getIdentifier());
        entity.overwrite(pet);
        entity.persist();
        return entity.map();
    }

    @Override
    @Transactional
    public boolean replace(final Pet pet) {
        final PetEntity entity = petEntity(pet);
        final boolean deleted = PetEntity.deleteById(pet.getIdentifier());
        entity.persist();
        return deleted;
    }

    @Override
    @Transactional
    public void delete(final String identifier) {
        PetEntity.deleteById(identifier);
    }
}
