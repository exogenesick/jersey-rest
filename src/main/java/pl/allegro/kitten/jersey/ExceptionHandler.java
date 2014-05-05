package pl.allegro.kitten.jersey;

import pl.allegro.kitten.model.Fault;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        Fault fault = new Fault();
        fault.setMessage(e.getMessage());

        return Response.status(500).entity(fault).build();
    }
}
