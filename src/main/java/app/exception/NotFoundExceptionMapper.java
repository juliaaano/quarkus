package app.exception;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
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
