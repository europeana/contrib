package at.researchstudio.dme.imageannotation.client.server;

import java.util.ArrayList;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * asynchronous interface of the image annotation service
 * 
 * @author Christian Sadilek
 * @see ImageAnnotationService
 */
public interface ImageAnnotationServiceAsync {
	public void createAnnotation(ImageAnnotation annotation, 
			AsyncCallback<ImageAnnotation> callback);

	public void updateAnnotation(ImageAnnotation annotation, 
			AsyncCallback<ImageAnnotation> callback);

	public void deleteAnnotation(String annotationId, 
			AsyncCallback<Void> callback);

	public void listAnnotations(String objectId,
			AsyncCallback<ArrayList<ImageAnnotation>> callback);

	public void findAnnotations(String searchTerm,
			AsyncCallback<ArrayList<ImageAnnotation>> callback);
}
