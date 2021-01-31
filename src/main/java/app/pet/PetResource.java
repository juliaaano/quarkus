package app.pet;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType.OAUTH2;
import java.net.URI;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.OAuthScope;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.UUID;

@Path("/pets")
@Consumes(TEXT_PLAIN)
@Produces(TEXT_PLAIN)
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

    private static final Logger log = LoggerFactory.getLogger(PetResource.class);

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
        description = "This operation lists all pets registered in the system."
    )
    @APIResponse(
        responseCode = "200",
        description = "List of pets.",
        content = {
            @Content(schema = @Schema(implementation = Pet[].class))
        }
    )
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized."
    )
    @APIResponse(
        responseCode = "403",
        description = "Forbidden."
    )
    public Response get() {

        var pets = repository.find();

        return Response.ok(pets).build();
    }

    @GET
    @Path("/{identifier}")
    @RolesAllowed("PETS_READ")
    @Produces(APPLICATION_JSON)
    @Operation(
        summary = "get a pet",
        description = "This operation retrieves a pet by its unique identifier."
    )
    @APIResponse(
        responseCode = "200",
        description = "The pet.",
        content = {
            @Content(schema = @Schema(implementation = Pet.class))
        }
    )
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized."
    )
    @APIResponse(
        responseCode = "403",
        description = "Forbidden."
    )
    @APIResponse(
        responseCode = "404",
        description = "Pet not found."
    )
    public Response get(@PathParam("identifier") final String identifier) {

        return repository.find(identifier)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @POST
    @RolesAllowed("PETS_CREATE")
    @Consumes(APPLICATION_JSON)
    @Produces({TEXT_PLAIN, APPLICATION_JSON})
    @Operation(
        summary = "create a pet",
        description = "This operation adds a pet to the system."
    )
    @APIResponse(
        responseCode = "201",
        description = "Pet created.",
        headers = {
            @Header(name = "Location", schema = @Schema(implementation = String.class, example = "http://my.domain/pets/1f31efb8-94ae-43ca-9a40-d966881e6ed6"))
        },
        content = {
            @Content(schema = @Schema(implementation = String.class, example = "1f31efb8-94ae-43ca-9a40-d966881e6ed6"))
        }
    )
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized."
    )
    @APIResponse(
        responseCode = "403",
        description = "Forbidden."
    )
    @APIResponse(
        responseCode = "415",
        description = "Unsupported Media Type."
    )
    public Response post(@Valid final Pet pet) {

        final String identifier = repository.create(pet);

        return Response.created(URI.create("pets/" + identifier)).entity(identifier).build();
    }

    @PUT
    @Path("/{identifier}")
    @RolesAllowed({"PETS_CREATE", "PETS_UPDATE"})
    @Consumes(APPLICATION_JSON)
    @Produces({TEXT_PLAIN, APPLICATION_JSON})
    @Operation(
        summary = "creates or replaces a pet",
        description = "This operation replaces or creates a pet if it does not exist.")
    @APIResponse(
        responseCode = "201",
        description = "Pet created.",
        headers = {
            @Header(name = "Location", schema = @Schema(implementation = String.class, example = "http://my.domain/pets/1f31efb8-94ae-43ca-9a40-d966881e6ed6"))
        },
        content = {
            @Content(schema = @Schema(implementation = String.class, example = "1f31efb8-94ae-43ca-9a40-d966881e6ed6"))
        }
    )
    @APIResponse(
        responseCode = "204",
        description = "Pet replaced.",
        headers = {
            @Header(name = "Location", schema = @Schema(implementation = String.class, example = "http://my.domain/pets/1f31efb8-94ae-43ca-9a40-d966881e6ed6"))
        }
    )
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized."
    )
    @APIResponse(
        responseCode = "403",
        description = "Forbidden."
    )
    @APIResponse(
        responseCode = "415",
        description = "Unsupported Media Type."
    )
    public Response put(@UUID @PathParam("identifier") final String identifier, @Valid final Pet pet) {

        var replaced = repository.replace(pet.clone(identifier));

        var response = replaced ? Response.status(NO_CONTENT) : Response.status(CREATED).entity(identifier);

        return response.location(URI.create("pets/" + identifier)).build();
    }

    @PATCH
    @Path("/{identifier}")
    @RolesAllowed("PETS_UPDATE")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(
        summary = "updates a pet",
        description = "This operation updates an existing pet.")
    @APIResponse(
        responseCode = "200",
        description = "Pet updated.",
        content = {
            @Content(schema = @Schema(implementation = Pet.class))
        }
    )
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized."
    )
    @APIResponse(
        responseCode = "403",
        description = "Forbidden."
    )
    @APIResponse(
        responseCode = "404",
        description = "Pet not found."
    )
    @APIResponse(
        responseCode = "415",
        description = "Unsupported Media Type."
    )
    public Response patch(@PathParam("identifier") final String identifier, final Pet request) {

        return repository.find(identifier)
                .map(pet -> pet.merge(request))
                .map(pet -> repository.update(pet))
                .map(pet -> Response.ok(pet))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @DELETE
    @Path("{identifier}")
    @RolesAllowed("PETS_DELETE")
    @Operation(
        summary = "delete a pet",
        description = "This operation deletes a pet from the system."
    )
    @APIResponse(
        responseCode = "204",
        description = "Pet deleted."
    )
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized."
    )
    @APIResponse(
        responseCode = "403",
        description = "Forbidden."
    )
    public Response delete(@PathParam("identifier") final String identifier) {
        repository.delete(identifier);
        return Response.noContent().build();
    }

    @GET
    @Path("/context")
    @Operation(hidden = true)
    public String context(@Context SecurityContext ctx) {

        log.info(jwt.toString());

        return String.format("hello + %s," + " isHttps: %s," + " authScheme: %s",
                ctx.getUserPrincipal().getName(), ctx.isSecure(), ctx.getAuthenticationScheme());
    }
}
