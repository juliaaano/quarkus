package au.sa.gov.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.eclipse.microprofile.metrics.annotation.Counted;

@Path("/pets")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class PetsResource {

    private Set<Pet> pets =
            Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public PetsResource() {
        pets.add(new Pet("Dog", "Labrador", "Max"));
        pets.add(new Pet("Dog", "Stray", null));
        pets.add(new Pet("Cat", "Persian Cat", "Garfield"));
    }

    @GET
    @Counted(name = "listCounter", description = "How many times pets have been listed.")
    public Set<Pet> list() {
        return pets;
    }

    @POST
    public Set<Pet> add(Pet pet) {
        pets.add(pet);
        return pets;
    }

    @DELETE
    public Set<Pet> delete(Pet pet) {
        pets.removeIf(existingPet -> existingPet.getName().contentEquals(pet.getName()));
        return pets;
    }

}
