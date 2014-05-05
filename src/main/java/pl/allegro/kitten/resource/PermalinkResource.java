package pl.allegro.kitten.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
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
@Api(value = "/permalinks", description = "Source URL redirect to Destination URL")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class PermalinkResource {

    private PermalinkManager permalinkManager;

    @Autowired
    public PermalinkResource(PermalinkManager permalinkManager) {
        this.permalinkManager = permalinkManager;
    }

    @GET
    @Path("/{sourceUrl}")
    @ApiOperation(value = "Get permalink by source URL", response = Permalink.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Permalink resource"),
        @ApiResponse(code = 404, message = "Permalink not found")
    })
    public Response get(@PathParam("sourceUrl") String sourceUrl) {
        Permalink permalink = permalinkManager.get(sourceUrl);

        if (null == permalink) {
            return Response.status(404).build();
        }

        return Response.ok().entity(permalink).build();
    }

    @POST
    @ApiOperation(value = "Create new permalink")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Permalink created successfully"),
    })
    public Response create(@Context UriInfo uriInfo, Permalink permalink) throws Exception {
        permalinkManager.set(permalink);

        return Response.created(URI.create(uriInfo.getAbsolutePath().toString() + "/" + permalink.getSourceUrl())).build();
    }

    @DELETE
    @Path("/{sourceUrl}")
    @ApiOperation(value = "Delete permalink with source URL")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Permalink deleted successfully"),
            @ApiResponse(code = 404, message = "Permalink not found")
    })
    public Response delete(@PathParam("sourceUrl") String sourceUrl) {
        Permalink permalink = permalinkManager.get(sourceUrl);

        if (null == permalink) {
            return Response.status(404).build();
        }

        permalinkManager.delete(permalink);

        return Response.noContent().build();
    }
}
