package app.pet.ws;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("v1")
@RegisterRestClient(configKey = "pets_api")
public interface PetRestClient {

    @GET
    @Path("pets/{id}")
    @Produces(APPLICATION_JSON)
    PetDTO getPet(@PathParam("id") String id);

    @GET
    @Path("pets")
    @Produces(APPLICATION_JSON)
    List<PetDTO> getPets();

    @PUT
    @Path("pets/{id}")
    @Produces(APPLICATION_XML)
    @Consumes(APPLICATION_JSON)
    String putPet(@PathParam("id") String id, PetDTO pet);
}
