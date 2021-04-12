package app.pet.ws;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
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
        stubForError();
        stubForFault();
        stubForTimeout();

        return Collections.singletonMap("pets_api/mp-rest/url", wireMockServer.baseUrl());
    }

    private void stubForGetPet() {
        stubFor(get(urlEqualTo("/v1/pets/123456789"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(
                    "{" +
                    "\"id\": \"123456789\"," +
                    "\"type\": \"dog\"," +
                    "\"denomination\": \"max\"," +
                    "\"variety\": \"husky\"" +
                    "}")
        ));
    }

    private void stubForError() {
        stubFor(get(urlEqualTo("/v1/pets/error"))
            .willReturn(aResponse().withStatus(500)));
    }

    private void stubForFault() {
        stubFor(get(urlEqualTo("/v1/pets/fault"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    }

    private void stubForTimeout() {
        stubFor(get(urlEqualTo("/v1/pets/timeout"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{}")
                .withFixedDelay(2000)
        ));
    }
    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();  
        }
    }
}
