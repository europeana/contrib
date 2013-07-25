package at.researchstudio.dme.annotation.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.db.AnnotationDatabase;
import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
 * This class contains the default annotation controller logic. Annotations are
 * expected to be represented as RDF/XML based on Annotea. Sub classes will
 * expose these methods through HTTP and may offer different representation
 * formats and may also change the response status codes.
 * 
 * @author Christian Sadilek
 * 
 * TODO use OPTIONS to return the supported operations. the supported operations
 * depend on the chosen annotation database e.g. FAST does not support update
 * and delete.
 * 
 */
public abstract class AbstractAnnotationController {
	protected static final String URL_ENCODING = "UTF-8";
	
	@Context
	protected HttpServletRequest request;

	/**
	 * create a new annotation
	 * 
	 * @param annotation representation
	 * @return status code 201 and new annotation representation
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws AnnotationModifiedException (409)
	 */
	protected Response createAnnotation(String annotation) throws AnnotationDatabaseException,
			AnnotationException, AnnotationModifiedException {
		
		AnnotationDatabase aDb = null;
		URI annotationId = null;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotationId = aDb.createAnnotation(annotation);
			annotation = aDb.findAnnotationById(annotationId.toString());
		} catch(AnnotationNotFoundException anfe) {
			throw new AnnotationDatabaseException(anfe);
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.created(annotationId).entity(annotation).build();
	}
	
	/**
	 * update an existing annotation
	 * 
	 * @param id
	 * @param annotation representation
	 * @return status code 200 and updated annotation representation
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500)
	 * @throws AnnotationAlreadyReferencedException (409)
	 */
	protected Response updateAnnotation(String id, String annotation)
			throws AnnotationDatabaseException, AnnotationException,
			AnnotationAlreadyReferencedException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		URI annotationId = null;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotationId=aDb.updateAnnotation(URLDecoder.decode(id,URL_ENCODING), annotation);
			annotation=aDb.findAnnotationById(annotationId.toString());
		} catch(AnnotationNotFoundException anfe) {
			throw new AnnotationDatabaseException(anfe);
		} finally {
			if(aDb!=null) aDb.disconnect();
		}	
		return Response.ok().entity(annotation).header("Location",annotationId.toString()).build(); 
	}
	
	/**
	 * delete an annotation
	 * 
	 * @param id
	 * @return status code 204
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500)
	 * @throws AnnotationAlreadyReferencedException (409)
	 */
	protected Response deleteAnnotation(String id) throws AnnotationDatabaseException,
			AnnotationException, AnnotationAlreadyReferencedException, 
			UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		try {			
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			aDb.deleteAnnotation(URLDecoder.decode(id,URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}			
		// response to DELETE without a body should return 204 NO CONTENT see 
		// http://www.w3.org/Protocols/rfc2616/rfc2616.html
		return Response.noContent().build(); 
	}
	
	/**
	 * list annotations for the given object
	 * 
	 * @param objectId
	 * @return status code 200 and representation of the found annotations
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500)
	 */
	protected Response listAnnotations(String objectId) throws AnnotationDatabaseException,
			AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		String annotations = null;
		
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotations = aDb.listAnnotations(URLDecoder.decode(objectId,URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok().entity(annotations).build();
	}
	
	/**
	 * count annotations for the given object
	 * 
	 * @param objectId
	 * @return Response - status code and count representation
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response countAnnotations(String objectId) throws AnnotationDatabaseException,
			AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		int count = 0;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			count = aDb.countAnnotations(URLDecoder.decode(objectId,URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok().entity(count).build();
	}
	
	/**
	 * list replies of the given annotation
	 * 
	 * @param annotation id
	 * @return status code 200 and representation of the found annotations (replies)
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response listAnnotationReplies(String id) throws AnnotationDatabaseException,
			AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		String annotations = null;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotations = aDb.listAnnotationReplies(URLDecoder.decode(id,URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok().entity(annotations).build();
	}
	
	/**
	 * list linked annotation to the given objectId
	 * 
	 * @param objectId
	 * @return status code 200 and representation of the found annotations
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response listLinkedAnnotations(String objectId) 
			throws AnnotationDatabaseException, AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		String annotations = null;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotations = aDb.listLinkedAnnotations(URLDecoder.decode(objectId,URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok().entity(annotations).build();
	}
	
	/**
	 * count linked annotations for the given object
	 * 
	 * @param objectId
	 * @return status code 200 and count representation
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response countLinkedAnnotations(String objectId) 
			throws AnnotationDatabaseException, AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		int count = 0;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			count = aDb.countLinkedAnnotations(URLDecoder.decode(objectId,URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok().entity(count).build();
	}
	
	/**
	 * find annotation by its id
	 * 
	 * @param id
	 * @return status code 200 and found annotation
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response findAnnotationById(String id) throws AnnotationDatabaseException,
			AnnotationNotFoundException, AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		String annotation = null;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotation = aDb.findAnnotationById(URLDecoder.decode(id, URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok(annotation).build();
	}
	
	/**
	 * find annotation body by annotation id
	 * 
	 * @param annotationId
	 * @return status code 200 and annotation body if found
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response findAnnotationBodyById(String annotationId)
				throws AnnotationDatabaseException, AnnotationNotFoundException, 
				AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		String annotation = null;
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotation = aDb.findAnnotationBodyById(URLDecoder.decode(annotationId, URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok(annotation).build();
	}
		
	/**
	 * find annotations that match the given search term
	 * 
	 * @param query
	 * @return status code 200 and found annotations
	 * @throws AnnotationDatabaseException (500)
	 * @throws AnnotationException (415)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response findAnnotations(String query) throws AnnotationDatabaseException,
			AnnotationException, UnsupportedEncodingException {
		
		AnnotationDatabase aDb = null;
		String annotation = null;		
		try {
			aDb = Config.getInstance().getAnnotationDatabase();
			aDb.connect(request);
			annotation = aDb.findAnnotations(URLDecoder.decode(query, URL_ENCODING));
		} finally {
			if(aDb!=null) aDb.disconnect();
		}
		return Response.ok(annotation).build();
	}
}
