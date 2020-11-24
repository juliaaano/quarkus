package pet.db;

import java.util.Map;
import io.quarkus.test.junit.QuarkusTestProfile;

public class DatabaseH2TestProfile implements QuarkusTestProfile {

    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.arc.selected-alternatives", "pet.db.DatabasePetRepository",
            "quarkus.datasource.db-kind", "h2",
            "quarkus.datasource.jdbc.url", "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1",
            "quarkus.liquibase.migrate-at-start", "true",
            "quarkus.liquibase.change-log", "liquibase/db.changelog-master.xml"
        );
    }
}
