package app.pet;

import static java.lang.String.format;
import static java.net.URI.create;
import static java.time.Duration.ofSeconds;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
class NativePetResourceIT extends PetResourceTest {

    private static final Logger logger = LoggerFactory.getLogger(NativePetResourceIT.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected String jwt(final String user) {

        var request = HttpRequest.newBuilder()
            .uri(create("http://localhost:50102/auth/realms/quarkus/protocol/openid-connect/token"))
            .timeout(ofSeconds(5))
            .header("Authorization", basicAuth("quarkus-api-test-client:''")) // Preemptive auth, otherwise could use built-in authenticator.
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(body("grant_type=password&username=%s&password=%s&scope=%s", user, password(user), scopes(user)))
            .build();

        var accessToken = send(request).get("access_token").asText();

        logger.info(accessToken);

        return accessToken;
    }

    private BodyPublisher body(final String body, final String user, final String password, final String scopes) {
        return BodyPublishers.ofString(format(body, user, password, scopes));
    }

    private JsonNode send(final HttpRequest request) {

        HttpResponse<String> response = null;

        try {
            response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() != 200) {
            logger.error("HTTP response {} {}", response.statusCode(), response.body());
            throw new RuntimeException(format("HTTP response status code is %s", response.statusCode()));
        }

        try {
            return objectMapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String password(final String user) {
        return Map.of(
            "bob", "password",
            "alice", "password",
            "joe", "password"
        ).get(user);
    }

    private String scopes(final String user) {
        return Map.of(
            "bob", "api.pets:read",
            "alice", "api.pets:read api.pets:write api.pets:erase",
            "joe", "openid"
        ).get(user);
    }

    private static String basicAuth(final String credentials) {
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
