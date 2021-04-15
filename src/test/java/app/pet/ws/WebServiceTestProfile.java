package app.pet.ws;

import java.util.Map;
import io.quarkus.test.junit.QuarkusTestProfile;

public class WebServiceTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.arc.selected-alternatives", "app.pet.ws.WebServicePetRepository",
            "pets_api/mp-rest/readTimeout", "1000"
        );
    }
}
