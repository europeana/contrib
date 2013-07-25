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
import javax.ws.rs.core.Response.Status;

import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
 * This class exposes the relevant annotation controller methods to conform
 * to the Annotea protocols (see http://www.w3.org/2001/Annotea/User/Protocol.html).
 * 
 * @author Christian Sadilek
 */
@Path("/Annotation")
public class AnnoteaAnnotationController extends AbstractAnnotationController {

	@POST
	@Consumes("application/xml")
	@Override
	public Response createAnnotation(String annotation) throws AnnotationDatabaseException,
			AnnotationException, AnnotationModifiedException {
				
		return super.createAnnotation(annotation);
	}
	
	/**
	 * This method is used to support clients that can't send HTTP PUT or DELETE. We added
	 * an additional request parameter ("_method") to switch between PUT and DELETE. In case
	 * neither PUT or DELETE is specified an HTTP error (BAD REQUEST) is thrown.
	 * 
	 * @param id
	 * @param annotation
	 * @param method
	 * @return Response
	 * @throws AnnotationDatabaseException
	 * @throws AnnotationException
	 * @throws AnnotationAlreadyReferencedException
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Consumes("application/xml")
	@Path("/{id}")
	public Response updateOrDeleteAnnotation(@PathParam("id") String id, String annotation, 
			@QueryParam("_method") String method) throws AnnotationDatabaseException, 
				AnnotationException, AnnotationAlreadyReferencedException, 
				UnsupportedEncodingException {
	
		if(method!=null) {
			if(method.equalsIgnoreCase("PUT")) return super.updateAnnotation(id, annotation);
			if(method.equalsIgnoreCase("DELETE")) {
				super.deleteAnnotation(id);
				return Response.ok().build();
			}
		}
		
		return Response.status(Status.BAD_REQUEST).build();		
	}
	
	@PUT
	@Consumes("application/xml")
	@Path("/{id}")
	@Override
	public Response updateAnnotation(@PathParam("id") String id, String annotation) 
			throws AnnotationDatabaseException, AnnotationException, 
			AnnotationAlreadyReferencedException, UnsupportedEncodingException {
		
		return super.updateAnnotation(id, annotation);
	}
	
	@DELETE
	@Path("/{id}")
	@Override
	public Response deleteAnnotation(@PathParam("id") String id) 
			throws AnnotationDatabaseException, AnnotationException, 
			AnnotationAlreadyReferencedException, UnsupportedEncodingException {
		
		super.deleteAnnotation(id);
		return Response.ok().build();
	}
	
	@GET
	@Produces("application/xml")
	@Path("/{id}")
	@Override	
	public Response findAnnotationById(@PathParam("id") String id) 
			throws AnnotationDatabaseException, AnnotationException, 
			AnnotationNotFoundException, UnsupportedEncodingException {
		
		// this is to support the Annotea reply protocols
		if(id.contains("text")) 
			return super.findAnnotationBodyById(id.replace("text", ""));
		
		return super.findAnnotationById(id);
	}
	
	@GET
	@Produces("application/xml")
	@Path("/body/{id}")
	@Override	
	public Response findAnnotationBodyById(@PathParam("id") String id) 
			throws AnnotationDatabaseException, AnnotationNotFoundException, 
			AnnotationException, UnsupportedEncodingException {
		
		return super.findAnnotationBodyById(id);
	}
	
	@GET
	//@ProduceMime("application/xml")
	//annozilla needs text/rdf although the spec says application/xml
	@Produces("text/rdf") 	
	public Response listAnnotations(@QueryParam("w3c_annotates") String objectId,
			@QueryParam("w3c_reply_tree") String rootId) 
			throws AnnotationDatabaseException, AnnotationException, UnsupportedEncodingException {
		
		if(rootId!=null)
			return super.listAnnotationReplies(rootId);
	
		return super.listAnnotations(objectId);
	}	
}
