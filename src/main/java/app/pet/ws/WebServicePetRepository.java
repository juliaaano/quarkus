package app.pet.ws;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import app.pet.Pet;
import app.pet.PetRepository;

@Alternative
@ApplicationScoped
class WebServicePetRepository implements PetRepository {

    @Inject
    @RestClient
    PetRestClient client;

    @Override
    public Optional<Pet> find(final String identifier) {

        PetResource pet = client.get(identifier);
        return Optional.of(pet.map());
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
