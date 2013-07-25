package at.ait.dme.yuma.server.controller;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import at.ait.dme.yuma.server.URIBuilder;
import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationHasReplyException;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationModifiedException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;

/**
 * This class contains the default annotation controller logic.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon 
 */
public abstract class AbstractAnnotationController {
	
	protected static final String URL_ENCODING = "UTF-8";
	
	@Context
	protected HttpServletRequest request;
	
	@Context
	protected HttpServletResponse response;

	/**
	 * Create a new annotation
	 * @param annotation the JSON representation of the annotation
	 * @return status code 201 and new annotation representation
	 * @throws AnnotationDatabaseException (500)
	 * @throws InvalidAnnotationException (415)
	 * @throws AnnotationModifiedException (409)
	 */
	protected Response createAnnotation(String annotation, FormatHandler format)
		throws AnnotationDatabaseException, InvalidAnnotationException, AnnotationModifiedException {
		
		AbstractAnnotationDB db = null;
		String annotationId = null;
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			annotationId = db.createAnnotation(format.parse(annotation));
		} finally {
			if (db != null) db.disconnect();
		}
		return Response.created(URIBuilder.toURI(annotationId)).entity(annotationId).build();
	}
	
	/**
	 * Find an annotation by its ID
	 * @param annotationId the annotation ID
	 * @return status code 200 and found annotation
	 * @throws AnnotationDatabaseException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response getAnnotation(String annotationId, FormatHandler format)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		String annotation = null;
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			annotation = format.serialize(db.findAnnotationById(URLDecoder.decode(annotationId, URL_ENCODING)));
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok(annotation).build();
	}
	
	/**
	 * Update an existing annotation
	 * @param annotationId the annotation ID 
	 * @param annotation the JSON representation of the annotation
	 * @return status code 200 and updated annotation representation
	 * @throws AnnotationDatabaseException (500)
	 * @throws InvalidAnnotationException (415)
	 * @throws AnnotationHasReplyException (409)
	 * @throws UnsupportedEncodingException (500)
	 */
	protected Response updateAnnotation(String annotationId, String annotation, FormatHandler format)
			throws AnnotationDatabaseException, InvalidAnnotationException, AnnotationHasReplyException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			annotationId = db.updateAnnotation(
					URLDecoder.decode(annotationId, URL_ENCODING),
					format.parse(annotation));
			annotation = format.serialize(db.findAnnotationById(annotationId));
		} catch(AnnotationNotFoundException anfe) {
			throw new AnnotationDatabaseException(anfe);
		} finally {
			if(db != null) db.disconnect();
		}	
		return Response.ok().entity(annotationId.toString()).header("Location", URIBuilder.toURI(annotationId)).build(); 
	}
	
	/**
	 * Delete an annotation
	 * @param annotationId the annotation ID
	 * @return status code 204
	 * @throws AnnotationDatabaseException (500)
	 * @throws UnsupportedEncodingException (500)
	 * @throws AnnotationHasReplyException (409)
	 * @throws AnnotationNotFoundException (404)
	 */
	protected Response deleteAnnotation(String annotationId)
		throws AnnotationDatabaseException, AnnotationHasReplyException, UnsupportedEncodingException, AnnotationNotFoundException {
		
		AbstractAnnotationDB db = null;
		try {			
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			db.deleteAnnotation(URLDecoder.decode(annotationId, URL_ENCODING));
		} finally {
			if(db != null) db.disconnect();
		}	
		
		// response to DELETE without a body should return 204 NO CONTENT see 
		// http://www.w3.org/Protocols/rfc2616/rfc2616.html
		return Response.noContent().build(); 
	}
	
	/**
	 * Retrieve the thread which contains the given annotation
	 * @param annotationId the annotation ID
	 * @return status code 200 and representation of the annotation thread
	 * @throws AnnotationDatabaseException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response getReplies(String annotationId, FormatHandler format)
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		String thread = null;
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			thread = format.serialize(db.getReplies(URLDecoder.decode(annotationId, URL_ENCODING)));
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok().entity(thread).build();
	}
	
	/**
	 * Returns the entire tree of annotations for a given object
	 * @param objectId the object ID
	 * @return status code 200 and the representation of the found annotations
	 * @throws AnnotationDatabaseException (500)
	 * @throws InvalidAnnotationException (415)
	 * @throws UnsupportedEncodingException (500)
	 */
	protected Response getAnnotationTree(String objectId, FormatHandler format)
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		String tree = null;
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			tree = format.serialize(db.findAnnotationsForObject(URLDecoder.decode(objectId, URL_ENCODING)));
		} finally {
			if(db != null) db.disconnect();
		}

		return Response.ok().entity(tree).build();
	}
	
	/**
	 * Retrieves the number of annotations for the given object
	 * @param objectId the object ID
	 * @return status code and count representation
	 * @throws AnnotationDatabaseException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response countAnnotationsForObject(String objectId)
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		long count = 0;
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			count = db.countAnnotationsForObject(URLDecoder.decode(objectId, URL_ENCODING));
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok().entity(count).build();
	}
	
	protected Response getAnnotationsForUser(String username, FormatHandler format)
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		String annotations = null;
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			annotations = format.serialize(db.findAnnotationsForUser(URLDecoder.decode(username, URL_ENCODING)));
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok().entity(annotations).build();	
	}
	
	protected Response getMostRecent(int n, FormatHandler format) 
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		String mostRecent = null;
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			mostRecent = format.serialize(db.getMostRecent(n, true));
		} finally {
			if(db != null) db.disconnect();
		}
		
		return Response.ok().entity(mostRecent).build();
	}
				
	/**
	 * Find annotations that match the given search term
	 * @param query the query term
	 * @return status code 200 and found annotations
	 * @throws AnnotationDatabaseException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response searchAnnotations(String query, FormatHandler format)
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		String annotations = null;		
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			annotations = format.serialize(db.findAnnotations(URLDecoder.decode(query, URL_ENCODING)));
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok(annotations).build();
	}
}
