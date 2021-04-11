package app.pet.ws;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import java.util.Collections;
import java.util.Map;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockPets implements QuarkusTestResourceLifecycleManager {  

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {

        wireMockServer = new WireMockServer();
        wireMockServer.start(); 

        stubFor(get(urlEqualTo("/v1/pets/123456789"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                        "{" +
                        "\"id\": \"123456789\"," +
                        "\"type\": \"dog\"," +
                        "\"denomination\": \"max\"," +
                        "\"variety\": \"husky\"" +
                        "}"
                        )));

        return Collections.singletonMap("pets_api/mp-rest/url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();  
        }
    }
}
