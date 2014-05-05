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
    public Response get(@PathParam("sourceUrl") String sourceUrl) {
        Permalink permalink = permalinkManager.get(sourceUrl);

        if (null == permalink) {
            return Response.status(404).build();
        }

        return Response.ok().entity(permalink).build();
    }

    @POST
    public Response create(@Context UriInfo uriInfo, Permalink permalink) throws Exception {
        permalinkManager.set(permalink);

        return Response.created(URI.create(uriInfo.getAbsolutePath().toString() + "/" + permalink.getSourceUrl())).build();
    }

    @DELETE
    @Path("/{sourceUrl}")
    public Response delete(@PathParam("sourceUrl") String sourceUrl) {
        Permalink permalink = permalinkManager.get(sourceUrl);

        if (null == permalink) {
            return Response.status(404).build();
        }

        permalinkManager.delete(permalink);

        return Response.noContent().build();
    }
}
