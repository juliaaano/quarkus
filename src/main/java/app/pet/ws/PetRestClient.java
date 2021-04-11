package app.pet.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/v1")
@RegisterRestClient(configKey = "pets_api")
public interface PetRestClient {

    @GET
    @Path("/pets/{id}")
    PetResource get(@PathParam String id); // use standard jaxrs
}
