package pet;

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
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/pets")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Tag(name = "pets", description = "operations about pets")
public class PetResource {

    private Set<Pet> pets =
            Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public PetResource() {
        pets.add(new Pet("Dog", "Labrador", "Max"));
        pets.add(new Pet("Dog", "Stray", null));
        pets.add(new Pet("Cat", "Persian Cat", "Garfield"));
    }

    @GET
    @Counted(name = "listCounter", description = "How many times pets have been listed.")
    @Operation(summary = "list all pets", description = "This operation will list all pets registered in the system.")
    @APIResponse(responseCode = "200", description = "List of pets.")
    public Set<Pet> list() {
        return pets;
    }

    @POST
    @Operation(summary = "create a pet", description = "This operation will add a pet to the system.")
    @APIResponse(responseCode = "201", description = "Pet created.")
    public Response add(Pet pet) {
        pets.add(pet);
        return Response.status(201).build();
    }

    @DELETE
    @Operation(summary = "delete a pet", description = "This operation will delete a pet from the system.")
    @APIResponse(responseCode = "204", description = "Pet deleted.")
    public Response delete(Pet pet) {
        pets.removeIf(existingPet -> existingPet.getName().contentEquals(pet.getName()));
        return Response.status(204).build();
    }

}
