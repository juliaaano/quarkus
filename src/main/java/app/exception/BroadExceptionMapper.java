package app.exception;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class BroadExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = LoggerFactory.getLogger(BroadExceptionMapper.class);

    @Override
    public Response toResponse(final Throwable exception) {

        final var causes = new ArrayList<>();

        var cause = exception.getCause();
        while (cause != null) {
            causes.add(cause.getMessage());
            cause = cause.getCause();
        }

        final var response = Map.of(
            "message", exception.getMessage() != null ? exception.getMessage() : "Empty",
            "details", causes
        );

        log.error("Internal server error", exception);
        log.info("Error response mapped to: {}", response);

        return Response
            .status(INTERNAL_SERVER_ERROR)
            .type(APPLICATION_JSON)
            .entity(response)
            .build();
    }
}
