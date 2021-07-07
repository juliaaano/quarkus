package app.pet.ws;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.http.Fault.MALFORMED_RESPONSE_CHUNK;
import java.util.Collections;
import java.util.Map;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockPets implements QuarkusTestResourceLifecycleManager {

    private static final int WIREMOCK_PORT = 8090;

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {

        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();

        WireMock.configureFor(WIREMOCK_PORT);

        stubForGetPet();
        stubForGetPetError();
        stubForGetPetFault();
        stubForGetPetTimeout();

        stubForGetPets();

        stubForPutPet();

        return Collections.singletonMap("pets_api/mp-rest/url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }

    private void stubForGetPet() {
        // @formatter:off
        stubFor(get(urlEqualTo("/v1/pets/123456789"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(
                    "{" +
                    "\"id\": \"123456789\"," +
                    "\"type\": \"DOG\"," +
                    "\"denomination\": \"Max\"," +
                    "\"variety\": \"Husky\"" +
                    "}")
        ));
        // @formatter:on
    }

    private void stubForGetPetError() {
        // @formatter:off
        stubFor(get(urlEqualTo("/v1/pets/error"))
            .willReturn(aResponse().withStatus(500)));
        // @formatter:on
    }

    private void stubForGetPetFault() {
        // @formatter:off
        stubFor(get(urlEqualTo("/v1/pets/fault"))
            .willReturn(aResponse().withFault(MALFORMED_RESPONSE_CHUNK)));
        // @formatter:on
    }

    private void stubForGetPetTimeout() {
        // @formatter:off
        stubFor(get(urlEqualTo("/v1/pets/timeout"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{}")
                .withFixedDelay(2000)
        ));
        // @formatter:on
    }

    private void stubForGetPets() {
        // @formatter:off
        stubFor(get(urlEqualTo("/v1/pets"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(
                    "[" +
                    "{" +
                    "\"id\": \"123456789\"," +
                    "\"type\": \"DOG\"," +
                    "\"denomination\": \"Max\"," +
                    "\"variety\": \"Husky\"" +
                    "}," +
                    "{" +
                    "\"id\": \"987654321\"," +
                    "\"type\": \"Cat\"," +
                    "\"denomination\": \"Lisa\"," +
                    "\"variety\": \"Burmilla\"" +
                    "}," +
                    "{" +
                    "\"id\": \"111199999\"," +
                    "\"type\": \"TURTLE\"," +
                    "\"denomination\": \"Oliver\"," +
                    "\"variety\": \"NONE\"" +
                    "}" +
                    "]")
        ));
        // @formatter:on
    }

    private void stubForPutPet() {
        // @formatter:off
        stubFor(put(urlEqualTo("/v1/pets/123456789"))
            .withHeader("Accept", equalTo("application/xml"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(matchingJsonPath("$.denomination", equalTo("Max")))
            .withRequestBody(matchingJsonPath("$.type", equalTo("DOG")))
            .withRequestBody(containing("Husky"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/xml")
                .withBody("<result>created</result>")
        ));
        // @formatter:on
    }
}
