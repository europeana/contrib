package at.researchstudio.dme.annotation.controller;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
 * This class exposes the relevant annotation controller methods in a 'pure' RESTful way. 
 * It uses Annotea RDF/XML as an annotation representation format but improves the interface 
 * to better fit the Rest Oriented Architecture.
 * 
 * @see AnnoteaAnnotationController
 * @author Christian Sadilek
 */
@Path("/annotations")
public class RdfAnnotationController extends AbstractAnnotationController {
	
	@POST
	@Consumes("application/rdf+xml")
	@Override
	public Response createAnnotation(String annotation) throws AnnotationDatabaseException,
			AnnotationException, AnnotationModifiedException {
					
		return super.createAnnotation(annotation);
	}
	
	@PUT
	@Consumes("application/rdf+xml")
	@Path("/annotation/{id}")
	@Override
	public Response updateAnnotation(@PathParam("id") String id, String annotation) 
			throws AnnotationDatabaseException, AnnotationException, AnnotationAlreadyReferencedException, 
			UnsupportedEncodingException {
		
		return super.updateAnnotation(id, annotation);
	}
	
	@DELETE
	@Path("/annotation/{id}")
	@Override
	public Response deleteAnnotation(@PathParam("id") String id) 
			throws AnnotationDatabaseException, AnnotationException, 
			AnnotationAlreadyReferencedException, UnsupportedEncodingException {
		
		return super.deleteAnnotation(id);
	}
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/annotation/{id}")
	@Override	
	public Response findAnnotationById(@PathParam("id") String id) 
			throws AnnotationDatabaseException, AnnotationException, 
			AnnotationNotFoundException, UnsupportedEncodingException {
		
		return super.findAnnotationById(id);
	}
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/{obj-id}")
	@Override
	public Response listAnnotations(@PathParam("obj-id") String objectId) 
			throws AnnotationDatabaseException, AnnotationException, 
			UnsupportedEncodingException {

		return super.listAnnotations(objectId);
	}
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/linkedAnnotations/{obj-id}")
	@Override	
	public Response listLinkedAnnotations(@PathParam("obj-id") String objectId) 
			throws AnnotationDatabaseException, AnnotationException, 
			UnsupportedEncodingException {
				
		return super.listLinkedAnnotations(objectId);
	}
	
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/search")
	@Override	
	public Response findAnnotations(@QueryParam("q") String query) 
			throws AnnotationDatabaseException, AnnotationException,
			UnsupportedEncodingException {
				
		return super.findAnnotations(query);
	}
}
