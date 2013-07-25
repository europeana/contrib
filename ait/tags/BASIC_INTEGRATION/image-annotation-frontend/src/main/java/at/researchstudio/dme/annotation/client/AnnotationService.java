package at.researchstudio.dme.annotation.client;

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
 * the interface to the RESTful annotation middleware
 * 
 * @author Christian Sadilek
 */
@Path("annotation-middleware/annotations")
public interface AnnotationService {
	
	@POST
	@Consumes("application/rdf+xml")	
	public ClientResponse<String> createAnnotation(String annotation);
	
	@PUT
	@Consumes("application/rdf+xml")
	@Path("/annotation/{id}")
	public ClientResponse<String> updateAnnotation(@PathParam("id") String id, String annotation);
	
	@DELETE
	@Path("/annotation/{id}")
	public ClientResponse<String> deleteAnnotation(@PathParam("id") String id);
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/{obj-id}")
	public ClientResponse<String> listAnnotations(@PathParam("obj-id") String objectId);
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/search")
	public ClientResponse<String> findAnnotations(@QueryParam("q") String query);

}
