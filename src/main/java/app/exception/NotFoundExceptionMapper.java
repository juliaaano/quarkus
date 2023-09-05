package app.exception;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final Logger log = LoggerFactory.getLogger(NotFoundExceptionMapper.class);

    @Override
    public Response toResponse(final NotFoundException exception) {
        log.info("Exception mapped to NOT_FOUND response: {}", exception);
        return Response.status(NOT_FOUND).build();
    }
}
