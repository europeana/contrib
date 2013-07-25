package at.ait.dme.yuma.server.db.europeana;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.client.ClientResponse;

/**
 * The interface to the RESTful Europeana annotation service.
 * 
 * @author Christian Sadilek
 */
@Path("api/annotation")
public interface EuropeanaAnnotationAPI {
	
	@POST
	@Path("/IMAGE_ANNOTATION/{europeanaUri}")
	@Consumes("application/xml")	
	public ClientResponse<String> createAnnotation(@PathParam("europeanaUri") String europeanaUri, 
			String annotation);
	
	@POST
	@Consumes("application/xml")	
	public ClientResponse<String> createAnnotationReply(@QueryParam("predecessor") String parentId, 
			String annotation);
	
	@PUT
	@Consumes("application/xml")
	@Path("/{id}")
	public ClientResponse<String> updateAnnotation(@PathParam("id") String id, String annotation);
	
	@DELETE
	@Path("/{id}")
	public ClientResponse<String> deleteAnnotation(@PathParam("id") String id);
	
	@GET
	@Produces("application/xml")
	@Path("/IMAGE_ANNOTATION/{europeanaUri}")
	public ClientResponse<String> listAnnotations(@PathParam("europeanaUri") String europeanaUri);

	@GET
	@Produces("application/xml")
	@Path("/{id}")
	public ClientResponse<String> findAnnotationById(@PathParam("id") String id);
	
}
