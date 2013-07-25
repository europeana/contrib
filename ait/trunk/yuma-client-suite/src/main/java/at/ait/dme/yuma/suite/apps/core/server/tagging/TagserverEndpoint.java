package at.ait.dme.yuma.suite.apps.core.server.tagging;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.client.ClientResponse;

/**
 * The interface to the RESTful YUMA tagserver.
 * 
 * @author Rainer Simon
 */
public interface TagserverEndpoint {
	
	@GET
    @Path("/suggestions")
    public ClientResponse<String> getTagSuggestions(
    		@QueryParam("q") String query,
    		@QueryParam("limit") int limit);
	
	@GET
	@Path("/vocabularies")
	public ClientResponse<String> getVocabularies();

	@GET
	@Path("/children")
	public ClientResponse<String> getChildren(
			@QueryParam("uri") String uri);

}
