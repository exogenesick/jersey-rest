package pl.allegro.kitten.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    public Response toResponse(RestException e) {
        return Response.status(e.getCode()).entity(e).build();
    }

    @Override
    public Response toResponse(Exception e) {
        return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
