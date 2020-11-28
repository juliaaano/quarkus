package pet;

import static org.keycloak.OAuth2Constants.PASSWORD;
import java.util.Map;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
class NativePetResourceIT extends PetResourceTest {

    private static final Logger logger = LoggerFactory.getLogger(NativePetResourceIT.class);

    private static final KeycloakBuilder keycloak = KeycloakBuilder.builder()
        .serverUrl("http://localhost:50102/auth")
        .realm("quarkus")
        .clientId("quarkus")
        .clientSecret("quarkus")
        .grantType(PASSWORD);

    @Override
    protected String jwt(final String user) {

        final String jwt = keycloak
            .username(user)
            .password(password(user))
            .build()
            .tokenManager()
            .getAccessTokenString();

        logger.info(jwt);

        return jwt;
    }

    private String password(final String user) {
        return Map.of(
            "admin", "admin",
            "alice", "alice"
        ).get(user);
    }
}
