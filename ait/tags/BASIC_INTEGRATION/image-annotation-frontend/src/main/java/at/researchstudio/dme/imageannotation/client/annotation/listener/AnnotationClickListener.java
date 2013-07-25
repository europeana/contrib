package at.researchstudio.dme.imageannotation.client.annotation.listener;

import at.researchstudio.dme.imageannotation.client.ErrorMessages;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationTreeNode;
import at.researchstudio.dme.imageannotation.client.image.ImageComposite;
import at.researchstudio.dme.imageannotation.client.image.ImageFragment;
import at.researchstudio.dme.imageannotation.client.server.ImageAnnotationService;
import at.researchstudio.dme.imageannotation.client.server.ImageAnnotationServiceAsync;
import at.researchstudio.dme.imageannotation.client.server.exception.AnnotationServiceException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

/**
 * base click handler class
 * 
 * @author Christian Sadilek
 */
public abstract class AnnotationClickListener implements ClickHandler {
	private ImageAnnotationComposite annotationComposite = null;
	private ImageAnnotationTreeNode annotationTreeNode = null;

	protected ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
	
	public AnnotationClickListener(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode) {
		this.annotationComposite=annotationComposite;
		this.annotationTreeNode=annotationTreeNode;
	}
	
	/**
	 * returns the corresponding tree node (the tree node on which
	 * the button was clicked).
	 * 
	 * @return image annotation tree node
	 */
	protected ImageAnnotationTreeNode getAnnotationTreeNode() {
		return annotationTreeNode;
	}

	/**
	 * returns the annotation composite. used to add and remove tree nodes
	 * and to update the annotation tree.
	 * 
	 * @return image annotation composite
	 */
	protected ImageAnnotationComposite getAnnotationComposite() {
		return annotationComposite;
	}
		
	/**
	 * returns a reference to the image annotation service used by all click
	 * listeners to create/update/delete annotations.
	 *  
	 * @return ImageAnnotationServiceAsync reference to the image annotation service
	 */
	protected ImageAnnotationServiceAsync getImageAnnotationService() {
		ImageAnnotationServiceAsync imageAnnotationService = 
			(ImageAnnotationServiceAsync) GWT.create(ImageAnnotationService.class);
		return imageAnnotationService;
	}
	
	/**
	 * add the active fragment to the given annotation
	 * 
	 * @param annotation
	 */
	protected void addFragment(ImageAnnotation annotation) {
		ImageComposite imageComposite=annotationComposite.getImageComposite();
		if(imageComposite.getActiveShape()!=null) {
			ImageFragment fragment = new ImageFragment(
					imageComposite.getVisibleRect(),					
					imageComposite.getImageRect(),
					imageComposite.getActiveShape());
			annotation.setAnnotatedFragment(fragment);
		}
	}
	/**
	 * handle failures. in case of a conflict reload the application. 
	 * 
	 * @param caught
	 * @param defaultMessage
	 */
	protected void handleFailure(Throwable caught, String defaultMessage) {
		if(caught instanceof AnnotationServiceException && 
				((AnnotationServiceException)caught).isConflict()) {
			Window.alert(errorMessages.annotationConflict());
			annotationComposite.refreshTree();					
		} else {
			Window.alert(defaultMessage);	
		}					
	}
	
}
