package app;

import javax.inject.Singleton;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public class RegisterCustomModuleCustomizer implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {

        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
    }
}
