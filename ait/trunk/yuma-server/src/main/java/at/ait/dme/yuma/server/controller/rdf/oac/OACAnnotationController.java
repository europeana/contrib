package at.ait.dme.yuma.server.controller.rdf.oac;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import at.ait.dme.yuma.server.controller.AbstractAnnotationController;
import at.ait.dme.yuma.server.controller.rdf.oac.OACFormatHandler;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;

/**
 * An annotation controller which produces an RDF/XML
 * representations of annotations based on the 
 * OAC RDF annotation model.
 * 
 * @author Rainer Simon
 */
@Path("/api")
public class OACAnnotationController extends AbstractAnnotationController {
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/annotation/{id:.+\\.oac}")
	public Response getAnnotation(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotation(id.substring(0, id.indexOf('.')), new OACFormatHandler());
	}
	
}
