package pet;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import java.net.URI;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/pets")
@Produces(TEXT_PLAIN)
@Consumes(TEXT_PLAIN)
@Tag(name = "pets", description = "operations about pets")
public class PetResource {

    private final PetRepository repository;

    PetResource(PetRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Counted(name = "getCounter", description = "Number of times GET is executed.")
    @Operation(
        summary = "get all pets",
        description = "This operation lists all pets registered in the system.")
    @APIResponse(
        responseCode = "200",
        description = "List of pets.",
        content = @Content(schema = @Schema(implementation = Pet[].class)))
    public Response get() {
        final Object pets = repository.find();
        return Response.ok(pets).build();
    }

    @GET
    @Path("{identifier}")
    @Produces(APPLICATION_JSON)
    @Operation(
        summary = "get a pet",
        description = "This operation retrieves a pet by its unique identifier.")
    @APIResponse(
        responseCode = "200",
        description = "The pet.",
        content = @Content(schema = @Schema(implementation = Pet.class)))
    @APIResponse(
        responseCode = "404",
        description = "Pet not found.")
    public Response get(@PathParam("identifier") String identifier) {
        final Optional<Pet> pet = repository.find(identifier);
        return pet.map(p -> Response.ok(p).build())
                  .orElse(Response.status(NOT_FOUND).build());
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Operation(
        summary = "create a pet",
        description = "This operation adds a pet to the system.")
    @APIResponse(
        responseCode = "201",
        description = "Pet created.",
        content = @Content(schema = @Schema(implementation = String.class, example = "1f31efb8-94ae-43ca-9a40-d966881e6ed6")))
    public Response post(Pet pet) {
        final String identifier = repository.create(pet);
        return Response.created(URI.create("pets/" + identifier)).entity(identifier).build();
    }

    @DELETE
    @Path("{identifier}")
    @Operation(
        summary = "delete a pet",
        description = "This operation deletes a pet from the system.")
    @APIResponse(
        responseCode = "204",
        description = "Pet deleted.")
    public Response delete(@PathParam("identifier") String identifier) {
        repository.delete(identifier);
        return Response.noContent().build();
    }

}
