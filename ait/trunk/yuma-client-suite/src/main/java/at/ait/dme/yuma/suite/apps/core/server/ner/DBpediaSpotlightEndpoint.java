package at.ait.dme.yuma.suite.apps.core.server.ner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientResponse;

/**
 * The interface to the RESTful DBpediaSpotlight Web service.
 *
 * @author Rainer Simon
 */
@Path("rest")
public interface DBpediaSpotlightEndpoint {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("annotate")
    public ClientResponse<String> annotate(
    		@QueryParam(value = "text") String text, 
    		@QueryParam(value = "confidence") double confidence, 
    		@QueryParam(value = "support") int support);
}
