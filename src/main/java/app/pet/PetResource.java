package app.pet;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType.OAUTH2;
import java.net.URI;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.OAuthScope;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/pets")
@Produces(TEXT_PLAIN)
@Consumes(TEXT_PLAIN)
@Tag(name = "pets", description = "operations about pets")
@SecurityScheme(
    securitySchemeName = "oauth2",
    type = OAUTH2,
    description = "Authentication needed for this operation",
    flows = @OAuthFlows(
        // Issue with authorization code flow https://github.com/quarkusio/quarkus/issues/4766
        authorizationCode = @OAuthFlow(
            authorizationUrl = "http://localhost:50102/auth/realms/quarkus/protocol/openid-connect/auth",
            scopes = {
                @OAuthScope(name = "api.pets:read", description = "Allows to read pets."),
                @OAuthScope(name = "api.pets:write", description = "Allows to create and modify pets."),
                @OAuthScope(name = "api.pets:erase", description = "Allows to remove pets.")
            }
        ),
        password = @OAuthFlow(
            tokenUrl = "http://localhost:50102/auth/realms/quarkus/protocol/openid-connect/token",
            scopes = {
                @OAuthScope(name = "api.pets:read", description = "Allows to read pets."),
                @OAuthScope(name = "api.pets:write", description = "Allows to create and modify pets."),
                @OAuthScope(name = "api.pets:erase", description = "Allows to remove pets.")
            }
        )
    )
)
@RequestScoped
public class PetResource {

    private final JsonWebToken jwt;
    private final PetRepository repository;

    PetResource(JsonWebToken jwt, PetRepository repository) {
        this.repository = repository;
        this.jwt = jwt;
    }

    @GET
    @RolesAllowed("PETS_READ")
    @Produces(APPLICATION_JSON)
    @Counted(name = "getCounter", description = "Number of times GET is executed.")
    @Operation(
        summary = "get all pets",
        description = "This operation lists all pets registered in the system.")
    @APIResponse(
        responseCode = "200",
        description = "List of pets.",
        content = @Content(schema = @Schema(implementation = Pet[].class)))
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized.")
    @APIResponse(
        responseCode = "403",
        description = "Forbidden.")
    public Response get() {
        final Object pets = repository.find();
        return Response.ok(pets).build();
    }

    @GET
    @Path("/{identifier}")
    @RolesAllowed("PETS_READ")
    @Produces(APPLICATION_JSON)
    @Operation(
        summary = "get a pet",
        description = "This operation retrieves a pet by its unique identifier.")
    @APIResponse(
        responseCode = "200",
        description = "The pet.",
        content = @Content(schema = @Schema(implementation = Pet.class)))
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized.")
    @APIResponse(
        responseCode = "403",
        description = "Forbidden.")
    @APIResponse(
        responseCode = "404",
        description = "Pet not found.")
    public Response get(@PathParam("identifier") String identifier) {
        final Optional<Pet> pet = repository.find(identifier);
        return pet.map(p -> Response.ok(p).build())
                  .orElse(Response.status(NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("PETS_CREATE")
    @Consumes(APPLICATION_JSON)
    @Operation(
        summary = "create a pet",
        description = "This operation adds a pet to the system.")
    @APIResponse(
        responseCode = "201",
        description = "Pet created.",
        content = @Content(schema = @Schema(implementation = String.class, example = "1f31efb8-94ae-43ca-9a40-d966881e6ed6")))
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized.")
    @APIResponse(
        responseCode = "403",
        description = "Forbidden.")
    @APIResponse(
        responseCode = "415",
        description = "Unsupported Media Type.")
    public Response post(Pet pet) {
        final String identifier = repository.create(pet);
        return Response.created(URI.create("pets/" + identifier)).entity(identifier).build();
    }

    @DELETE
    @Path("{identifier}")
    @RolesAllowed("PETS_DELETE")
    @Operation(
        summary = "delete a pet",
        description = "This operation deletes a pet from the system.")
    @APIResponse(
        responseCode = "204",
        description = "Pet deleted.")
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized.")
    @APIResponse(
        responseCode = "403",
        description = "Forbidden.")
    public Response delete(@PathParam("identifier") String identifier) {
        repository.delete(identifier);
        return Response.noContent().build();
    }

    @GET
    @Path("/context")
    @Operation(hidden = true)
    public String context(@Context SecurityContext ctx) {

        System.out.println(jwt.toString());

        return String.format("hello + %s," + " isHttps: %s," + " authScheme: %s",
                ctx.getUserPrincipal().getName(), ctx.isSecure(), ctx.getAuthenticationScheme());
    }
}
