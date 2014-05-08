package pl.allegro.kitten.permalinks.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.ContextLoaderListener;
import pl.allegro.kitten.permalinks.model.Permalink;
import pl.allegro.kitten.permalinks.service.PermalinkInvalidException;
import pl.allegro.kitten.permalinks.service.PermalinkManager;
import redis.embedded.RedisServer;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URLEncoder;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-application-context.xml")
public class PermalinkResourceTest extends JerseyTest {

    @Autowired
    private PermalinkManager permalinkManager;

    private static RedisServer embeddedRedisServer;
    private static final int REDIS_SERVER_PORT = 1122;

    public PermalinkResourceTest() {
        super(new WebAppDescriptor.Builder("pl.allegro.kitten")
                .contextPath("api")
                .contextParam("contextConfigLocation", "classpath:test-application-context.xml")
                .servletClass(SpringServlet.class)
                .contextListenerClass(ContextLoaderListener.class)
                .build());
    }

    @BeforeClass
    public static void prepare() throws IOException, PermalinkInvalidException {
        // Grizzly slash encoding bug fix
        System.setProperty("com.sun.grizzly.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");

        embeddedRedisServer = new RedisServer(REDIS_SERVER_PORT);
        embeddedRedisServer.start();
    }

    @AfterClass
    public static void finish() throws Exception {
        embeddedRedisServer.stop();
    }

    @Before
    public void populateRedisServer() throws PermalinkInvalidException, JSONException {
        for (int i = 0; i < 100; i++) {
            permalinkManager.set(
                new Permalink(
                    "http://source-link-" + i + ".html",
                    "http://destination-link-" + i + ".html"
                )
            );
        }
    }

    @Test
    public void should_return_permalink_resource() throws Exception {
        String sourceUrlAlreadyExistsInStorage = "http://source-link-1.html";
        String expectedDestinationUrl = "http://destination-link-1.html";

        String encodedUrl = URLEncoder.encode(sourceUrlAlreadyExistsInStorage, "UTF-8");
        ClientResponse response = resource()
                .path("/permalinks/" + encodedUrl)
                .get(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        ClientResponse deleteResponse = resource()
                .path("/permalinks/" + encodedUrl)
                .delete(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_NO_CONTENT, deleteResponse.getStatus());
    }

    @Test
    public void should_return_not_found_response() throws Exception {
        String sourceUrlNotExistsInStorage = "http://source-link-666.html";

        String encodedUrl = URLEncoder.encode(sourceUrlNotExistsInStorage, "UTF-8");
        ClientResponse response = resource().path("/permalinks/" + encodedUrl).get(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void try_create_new_permalink_with_invalid_source_url() throws Exception {
        String destinationUrl = "http://destination-category-1/destination-link-1.html";

        JSONObject permalinkData = new JSONObject();
        permalinkData.put("sourceUrl", (Object) null);
        permalinkData.put("destinationUrl", destinationUrl);

        ClientResponse response = resource()
                .path("/permalinks")
                .entity(permalinkData, MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void try_create_new_permalink_with_invalid_destination_url() throws Exception {
        String sourceUrl = "http://source-category-1/source-link-1.html";

        JSONObject permalinkData = new JSONObject();
        permalinkData.put("sourceUrl", sourceUrl);
        permalinkData.put("destinationUrl", (Object) null);

        ClientResponse response = resource()
                .path("/permalinks")
                .entity(permalinkData, MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void should_create_new_permalink_and_get_it_back_in_response() throws Exception {
        String sourceUrl = "http://source-category-1/source-link-1.html";
        String destinationUrl = "http://destination-category-1/destination-link-1.html";

        JSONObject permalinkData = new JSONObject();
        permalinkData.put("sourceUrl", sourceUrl);
        permalinkData.put("destinationUrl", destinationUrl);

        System.out.println(permalinkData.toString());

        ClientResponse response = resource()
                .path("/permalinks")
                .entity(permalinkData.toString(), MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

        String encodedSourceUrl = URLEncoder.encode(sourceUrl, "UTF-8");
        assertTrue(response.getLocation().toString().contains(encodedSourceUrl));

        response = resource()
                .path("/permalinks/" + encodedSourceUrl)
                .get(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void should_create_new_permalink_and_delete_it() throws Exception {
        String sourceUrl = "http://source-category-100/source-link-100.html";
        String destinationUrl = "http://destination-category-100/destination-link-100.html";

        JSONObject permalinkData = new JSONObject();
        permalinkData.put("sourceUrl", sourceUrl);
        permalinkData.put("destinationUrl", destinationUrl);

        ClientResponse response = resource()
                .path("/permalinks")
                .entity(permalinkData, MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

        String encodedSourceUrl = URLEncoder.encode(sourceUrl, "UTF-8");
        assertTrue(response.getLocation().toString().contains(encodedSourceUrl));

        response = resource()
                .path("/permalinks/" + encodedSourceUrl)
                .get(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        response = resource()
                .path("/permalinks/" + encodedSourceUrl)
                .delete(ClientResponse.class);
        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    }
}
