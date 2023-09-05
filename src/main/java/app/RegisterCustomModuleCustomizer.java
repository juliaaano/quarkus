package app;

import jakarta.inject.Singleton;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public class RegisterCustomModuleCustomizer implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {

        // mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        mapper.setSerializationInclusion(Include.NON_NULL);
    }
}
