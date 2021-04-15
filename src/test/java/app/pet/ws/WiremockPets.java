package app.pet.ws;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import java.util.Collections;
import java.util.Map;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockPets implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {

        wireMockServer = new WireMockServer();
        wireMockServer.start();

        stubForGetPet();
        stubForGetPetError();
        stubForGetPetFault();
        stubForGetPetTimeout();

        stubForGetPets();

        stubForPutPet();

        return Collections.singletonMap("pets_api/mp-rest/url", wireMockServer.baseUrl());
    }

    private void stubForGetPet() {
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
    }

    private void stubForGetPetError() {
        stubFor(get(urlEqualTo("/v1/pets/error"))
            .willReturn(aResponse().withStatus(500)));
    }

    private void stubForGetPetFault() {
        stubFor(get(urlEqualTo("/v1/pets/fault"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    }

    private void stubForGetPetTimeout() {
        stubFor(get(urlEqualTo("/v1/pets/timeout"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{}")
                .withFixedDelay(2000)
        ));
    }

    private void stubForGetPets() {
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
    }

    private void stubForPutPet() {
        stubFor(put(urlEqualTo("/v1/pets/123456789"))
            .withHeader("Accept", equalTo("application/xml"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(containing("Max"))
            .withRequestBody(containing("DOG"))
            .withRequestBody(containing("Husky"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/xml")
                .withBody("<result>created</result>")
        ));
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();  
        }
    }
}
