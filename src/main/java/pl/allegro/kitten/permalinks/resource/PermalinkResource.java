package pl.allegro.kitten.permalinks.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pl.allegro.kitten.permalinks.model.Permalink;
import pl.allegro.kitten.permalinks.service.PermalinkManager;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

@Controller
@Path("/permalinks")
@Api(value = "/permalinks", description = "Source URL redirect to Destination URL")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class PermalinkResource {

    @Autowired
    private PermalinkManager permalinkManager;

    public PermalinkResource() {}

    public PermalinkResource(PermalinkManager permalinkManager) {
        this.permalinkManager = permalinkManager;
    }

    @GET
    @Path("/{sourceUrl}")
    @ApiOperation(value = "Get permalink by source URL", response = Permalink.class)
    @ApiResponses(value = {
        @ApiResponse(code = HttpServletResponse.SC_OK, message = "Permalink resource"),
        @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Permalink not found")
    })
    public Response get(@PathParam("sourceUrl") String sourceUrl) {
        Permalink permalink = permalinkManager.get(sourceUrl);

        if (null == permalink) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok().entity(permalink).build();
    }

    @POST
    @ApiOperation(value = "Create new permalink")
    @ApiResponses(value = {
        @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Permalink created successfully"),
        @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad request"),
    })
    public Response create(@Context UriInfo uriInfo, Permalink permalink) throws UnsupportedEncodingException {
        try {
            permalinkManager.set(permalink);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String resourceURL = URLEncoder.encode(permalink.getSourceUrl(), "UTF-8");
        URI resourceURI = URI.create(uriInfo.getAbsolutePath().toString() + "/" + resourceURL);

        return Response.created(resourceURI).build();
    }

    @DELETE
    @Path("/{sourceUrl}")
    @ApiOperation(value = "Delete permalink with source URL")
    @ApiResponses(value = {
        @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Permalink deleted successfully"),
        @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Permalink not found")
    })
    public Response delete(@PathParam("sourceUrl") String sourceUrl) {
        Permalink permalink = permalinkManager.get(sourceUrl);

        if (null == permalink) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        permalinkManager.delete(permalink);

        return Response.noContent().build();
    }
}
