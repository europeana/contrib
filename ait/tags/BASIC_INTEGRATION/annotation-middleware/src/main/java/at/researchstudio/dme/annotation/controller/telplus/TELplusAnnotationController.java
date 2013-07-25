package at.researchstudio.dme.annotation.controller.telplus;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import at.researchstudio.dme.annotation.controller.AbstractAnnotationController;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationException;

/**
 * this is a prototypical implementation of some TELplus specific annotation requests
 * as proposed by Theo van Veen.
 * 
 * @author Christian Sadilek
 */
@Path("/tp/annotations")
public class TELplusAnnotationController extends AbstractAnnotationController {
		
	@GET
	@Produces("application/rdf+xml")	
	@Path("/{obj-id}")
	public Response listAnnotations(@PathParam("obj-id") String objectId, 
			@QueryParam("result-count-type") String resultCountType) 
		throws AnnotationDatabaseException, AnnotationException, UnsupportedEncodingException {

		if(resultCountType!=null) {			
			int count =  new Integer(super.countAnnotations(objectId).getEntity().toString());			
			return createResultCountRepresentation(count, resultCountType);
		}
		
		return super.listAnnotations(objectId);
	}
	
	@GET
	@Produces("application/rdf+xml")	
	@Path("/linkedAnnotations/{obj-id}")
	public Response listLinkedAnnotations(@PathParam("obj-id") String objectId,
			@QueryParam("result-count-type") String resultCountType) 
		throws AnnotationDatabaseException, AnnotationException, UnsupportedEncodingException {
			
		if(resultCountType!=null) {			
			int count =  new Integer(super.countLinkedAnnotations(objectId).getEntity().toString());			
			return createResultCountRepresentation(count, resultCountType);
		} 
		
		return super.listLinkedAnnotations(objectId);
	}
	
	private Response createResultCountRepresentation(int count, String resultCountType) {
		Response response = null;
		
		if (resultCountType.equalsIgnoreCase("JSON")) {
			String resultCount = "{\"numberOfHits\":\"" + count + "\",\"unitName\":\"annotations\"}";
			response = Response.ok().type("application/javascript").entity(resultCount).build();
			
		} else if (resultCountType.equalsIgnoreCase("XML")) {
			String resultCount = "<result-count>\n<numberOfHits>" + count + "</numberOfHits>\n"
					+ "<unitName>annotations</unitName>\n</result-count>";
			response = Response.ok().type("application/xml").entity(resultCount).build();
		
		// all other types are supposed to describe the name of the callback function
		} else {
			String resultCount = resultCountType+"({\"numberOfHits\":\"" + count +  
				"\",\"unitName\":\"annotations\"})";
			response = Response.ok().type("application/javascript").entity(resultCount).build();
		}
		return response;
	}
}
