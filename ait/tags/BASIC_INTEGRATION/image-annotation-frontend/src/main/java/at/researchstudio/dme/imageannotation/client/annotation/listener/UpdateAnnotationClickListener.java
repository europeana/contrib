package at.researchstudio.dme.imageannotation.client.annotation.listener;

import java.util.Date;

import at.researchstudio.dme.imageannotation.client.Application;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationForm;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationTreeNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener for annotation updates
 * 
 * @author Christian Sadilek
 */
public class UpdateAnnotationClickListener extends AnnotationClickListener {
	// reference to the annotation form to retrieve title and text 
	private ImageAnnotationForm annotationForm;
	
	public UpdateAnnotationClickListener(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, ImageAnnotationForm annotationForm) {
		
		super(annotationComposite, annotationTreeNode);
		this.annotationForm = annotationForm;
	}		
	
	public void onClick(ClickEvent event) {
		final ImageAnnotationComposite annotationComposite=getAnnotationComposite();
		annotationComposite.enableLoadingImage();				
		((FocusWidget)event.getSource()).setEnabled(false);

		// create a new annotation
		ImageAnnotation annotation= new ImageAnnotation(
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getAnnotationId():null, 						
				Application.getImageUrl(),
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getExternalObjectId():null,
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getParentAnnotationId():null, 						
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getAnnotationRootId():null, 							
				Application.getUser(), 
				annotationForm.getTitle(), 
				annotationForm.getText(),
				annotationForm.getScope());
		annotation.setCreated(getAnnotationTreeNode().getAnnotation().getCreated());
		annotation.setModified(new Date());
		annotation.setMimeType(getAnnotationTreeNode().getAnnotation().getMimeType());
		
		// create the fragment if necessary
		addFragment(annotation);
		
		// update the annotation on the server
		getImageAnnotationService().updateAnnotation(annotation,
			new AsyncCallback<ImageAnnotation>() {
				public void onFailure(Throwable caught) {
					handleFailure(caught, errorMessages.failedToSaveAnnotation());				
				}

				// on success update the annotation in the tree
				public void onSuccess(ImageAnnotation result) {
					if (getAnnotationTreeNode() != null)
						result.setReplies(getAnnotationTreeNode().getAnnotation().getReplies());

					annotationComposite.removeAnnotationFromTree(getAnnotationTreeNode());
					annotationComposite.addAnnotation(result, getAnnotationTreeNode().
							getParentAnnotation());
					annotationComposite.hideAnnotationForm(getAnnotationTreeNode(), false);
					annotationComposite.disableLoadingImage();
				}
			}
		);
	}	
}
