package pl.allegro.kitten.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.allegro.kitten.model.Permalink;
import pl.allegro.kitten.service.PermalinkManager;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Component
@Path("/permalinks")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class PermalinkResource {

    private PermalinkManager permalinkManager;

    @Autowired
    public PermalinkResource(PermalinkManager permalinkManager) {
        this.permalinkManager = permalinkManager;
    }

    @GET
    @Path("/{sourceUrl}")
    public Permalink get(@PathParam("sourceUrl") String sourceUrl) {
        return permalinkManager.get(sourceUrl);
    }

    @POST
    public Response create(@Context UriInfo uriInfo, Permalink permalink) throws Exception {
        permalinkManager.set(permalink);
        return Response.created(URI.create(uriInfo.getAbsolutePath().toString() + "/" + permalink.getSourceUrl())).build();
    }
}
