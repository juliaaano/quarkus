package app.pet.db;

import java.util.Map;
import io.quarkus.test.junit.QuarkusTestProfile;

public class DatabaseTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.arc.selected-alternatives", "app.pet.db.DatabasePetRepository"
        );
    }
}
