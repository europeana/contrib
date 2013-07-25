package at.researchstudio.dme.imageannotation.client.annotation.listener;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationTreeNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener to delete an annotation
 * 
 * @author Christian Sadilek
 */
public class DeleteAnnotationClickListener extends AnnotationClickListener {
	
	public DeleteAnnotationClickListener(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode) {
		super(annotationComposite, annotationTreeNode);
	}
	
	public void onClick(ClickEvent event) {
		final ImageAnnotationComposite annotationComposite=getAnnotationComposite();
		annotationComposite.enableLoadingImage();						
		((FocusWidget)event.getSource()).setEnabled(false);
				
		getImageAnnotationService().deleteAnnotation(getAnnotationTreeNode().getAnnotationId(),
			new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					handleFailure(caught, errorMessages.failedToDeleteAnnotation());
				}

				public void onSuccess(Void result) {
					annotationComposite.removeAnnotationFromTree(getAnnotationTreeNode());
					annotationComposite.disableLoadingImage();
				}
			});
	}	
}
