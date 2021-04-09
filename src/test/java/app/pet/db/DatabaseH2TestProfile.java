package app.pet.db;

import java.util.Map;
import io.quarkus.test.junit.QuarkusTestProfile;

public class DatabaseH2TestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.arc.selected-alternatives", "app.pet.db.DatabasePetRepository"
        );
    }
}
