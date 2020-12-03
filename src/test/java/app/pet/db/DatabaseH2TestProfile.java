package app.pet.db;

import java.util.Map;
import io.quarkus.test.junit.QuarkusTestProfile;

public class DatabaseH2TestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.arc.selected-alternatives", "app.pet.db.DatabasePetRepository",
            "quarkus.datasource.db-kind", "h2",
            "quarkus.datasource.jdbc.url", "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1",
            "quarkus.hibernate-orm.database.generation", "drop-and-create"
        );
    }
}
