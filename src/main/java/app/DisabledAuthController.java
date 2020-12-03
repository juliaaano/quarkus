package app;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.security.spi.runtime.AuthorizationController;

@Alternative
@Priority(Integer.MAX_VALUE)
@ApplicationScoped
public class DisabledAuthController extends AuthorizationController {

    @ConfigProperty(name = "app.authorization.enabled", defaultValue = "true")
    boolean authorizationEnabled;

    @Override
    public boolean isAuthorizationEnabled() {
        return authorizationEnabled;
    }
}
