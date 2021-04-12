package app.pet.ws;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.pet.Pet;
import app.pet.PetRepository;

@Alternative
@ApplicationScoped
class WebServicePetRepository implements PetRepository {

    private static final Logger log = LoggerFactory.getLogger(WebServicePetRepository.class);

    @Inject
    @RestClient
    PetRestClient client;

    @Override
    public Optional<Pet> find(final String identifier) {

        final PetResource pet;
        try {
            pet = client.get(identifier);
        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() == 404) {
                log.info("Pet with id {} not found", identifier);
                return Optional.empty();
            } else {
                log.error("Failed to find pet", ex);
                throw ex;
            }
        }
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
