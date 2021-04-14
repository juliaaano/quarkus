package app.pet.ws;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
