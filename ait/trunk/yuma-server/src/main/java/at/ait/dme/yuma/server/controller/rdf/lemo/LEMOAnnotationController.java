package at.ait.dme.yuma.server.controller.rdf.lemo;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import at.ait.dme.yuma.server.controller.AbstractAnnotationController;
import at.ait.dme.yuma.server.controller.rdf.SerializationLanguage;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationHasReplyException;
import at.ait.dme.yuma.server.exception.AnnotationModifiedException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;

/**
 * An annotation controller which produces an RDF/XML
 * representations of annotations based on the 
 * LEMO RDF annotation model.
 * 
 * @author Rainer Simon
 */
@Path("/api")
public class LEMOAnnotationController extends AbstractAnnotationController {

	@POST
	@Consumes("application/rdf+xml")
	@Path("/annotation")
	public Response createAnnotation(String annotation)
		throws AnnotationDatabaseException, InvalidAnnotationException, AnnotationModifiedException {
		
		return super.createAnnotation(annotation, new LEMOFormatHandler());
	}
	
	@PUT
	@Consumes("application/rdf+xml")
	@Path("/annotation/{id}")
	public Response updateAnnotation(@PathParam("id") String id, String annotation) 
			throws AnnotationDatabaseException, InvalidAnnotationException, AnnotationHasReplyException, UnsupportedEncodingException {
		
		return super.updateAnnotation(id, annotation, new LEMOFormatHandler());
	}
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/annotation/{id}")
	public Response getAnnotationXML(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotation(id, new LEMOFormatHandler());
	}
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/annotation/{id:.+\\.rdf}")
	public Response getAnnotationXML_forceXML(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotation(id.substring(0, id.indexOf('.')), new LEMOFormatHandler());
	}

	@GET
	@Produces("application/x-turtle")
	@Path("/annotation/{id}")
	public Response getAnnotationTurtle(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotation(id, new LEMOFormatHandler(SerializationLanguage.TURTLE));
	}
	
	@GET
	@Produces("application/x-turtle")
	@Path("/annotation/{id:.+\\.turtle}")
	public Response getAnnotationTurtle_forceTurtle(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotation(id.substring(0, id.indexOf('.')), new LEMOFormatHandler(SerializationLanguage.TURTLE));
	}

	@GET
	@Produces("text/rdf+n3")
	@Path("/annotation/{id}")
	public Response getAnnotationN3(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotation(id, new LEMOFormatHandler(SerializationLanguage.N3));
	}
	
	@GET
	@Produces("text/rdf+n3")
	@Path("/annotation/{id:.+\\.n3}")
	public Response getAnnotationN3_forceN3(@PathParam("id") String id)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotation(id.substring(0, id.indexOf('.')), new LEMOFormatHandler(SerializationLanguage.N3));
	}	
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/tree/{objectUri}")
	public Response getAnnotationTreeXML(@PathParam("objectUri") String objectUri)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotationTree(objectUri, new LEMOFormatHandler());
	}
	
	@GET
	@Produces("application/x-turtle")
	@Path("/tree/{objectUri}")
	public Response getAnnotationTreeTurtle(@PathParam("objectUri") String objectUri)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.getAnnotationTree(objectUri, new LEMOFormatHandler(SerializationLanguage.TURTLE));
	}
	
}
