package at.researchstudio.dme.imageannotation.client.server;

import java.util.Collection;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.server.exception.AnnotationServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * interface of the image annotation service
 * 
 * @author Christian Sadilek
 */
@RemoteServiceRelativePath("annotation")
public interface ImageAnnotationService extends RemoteService {
	
	/**
	 * create a new image annotation.
	 * 
	 * @param annotation
	 * @return then new annotation
	 * @throws AnnotationServiceException
	 */
	public ImageAnnotation createAnnotation(ImageAnnotation annotation) 
		throws AnnotationServiceException;

	/**
	 * update an existing annotation.
	 * 
	 * @param annotation
	 * @return the updated annotation
	 * @throws AnnotationServiceException
	 */
	public ImageAnnotation updateAnnotation(ImageAnnotation annotation) 
		throws AnnotationServiceException;

	/**
	 * delete an existing annotation.
	 * 
	 * @param annotationId
	 * @throws AnnotationServiceException
	 */
	public void deleteAnnotation(String annotationId) 
		throws AnnotationServiceException;

	/**
	 * list annotations of a given object id (addressable uri).
	 * 
	 * @param objectId
	 * @return collection of image annotations
	 * @throws AnnotationServiceException
	 */
	public Collection<ImageAnnotation> listAnnotations(String objectId) 
		throws AnnotationServiceException;
	
	/**
	 * find annotations that match the given search term.
	 * 
	 * @param search term
	 * @return collection of image annotations
	 * @throws AnnotationServiceException
	 */
	public Collection<ImageAnnotation> findAnnotations(String searchTerm) 
		throws AnnotationServiceException;
}
