package at.ait.dme.yuma.server.controller.rdf.pelagios;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import at.ait.dme.yuma.server.controller.AbstractAnnotationController;
import at.ait.dme.yuma.server.controller.rdf.SerializationLanguage;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;

/**
 * An annotation controller which produces a 'simplified' OAC RDF
 * representation of annotations based on the conventions of the
 * Pelagios project
 * 
 * @author Rainer Simon
 */
@Path("/api")
public class PelagiosAnnotationController extends AbstractAnnotationController {
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/annotation/{id:.+\\.pelagios}")
	public Response getAnnotationXML(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		try {
			return super.getAnnotation(id.substring(0, id.indexOf('.')), 
					new PelagiosFormatHandler(SerializationLanguage.RDF_XML));
		} catch (NotAPelagiosAnnotationException e) {
			throw new AnnotationNotFoundException();
		}
	}
	
	@GET
	@Produces("text/rdf+n3")
	@Path("/annotation/{id:.+\\.pelagios}")
	public Response getAnnotationN3(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		try {
			return super.getAnnotation(id.substring(0, id.indexOf('.')),
					new PelagiosFormatHandler(SerializationLanguage.N3));
		} catch (NotAPelagiosAnnotationException e) {
			throw new AnnotationNotFoundException();
		}
	}
	
	@GET
	@Produces("application/x-turtle")
	@Path("/annotation/{id:.+\\.pelagios}")
	public Response getAnnotationTurtle(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		try {
			return super.getAnnotation(id.substring(0, id.indexOf('.')),
					new PelagiosFormatHandler(SerializationLanguage.TURTLE));
		} catch (NotAPelagiosAnnotationException e) {
			throw new AnnotationNotFoundException();
		}
	}
	
}
