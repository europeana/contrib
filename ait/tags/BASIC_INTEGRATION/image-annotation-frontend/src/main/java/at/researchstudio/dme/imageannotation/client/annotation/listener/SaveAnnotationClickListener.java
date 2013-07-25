package at.researchstudio.dme.imageannotation.client.annotation.listener;

import at.researchstudio.dme.imageannotation.client.Application;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationForm;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationTreeNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener to save annotations
 * 
 * @author Christian Sadilek
 */
public class SaveAnnotationClickListener extends AnnotationClickListener {

	// reference to the annotation form to retrieve title and text 
	private ImageAnnotationForm annotationForm;
	
	public SaveAnnotationClickListener(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, ImageAnnotationForm annotationForm) {
		
		super(annotationComposite, annotationTreeNode);
		this.annotationForm = annotationForm;
	}		
	
	public void onClick(ClickEvent event) {
		final ImageAnnotationComposite annotationComposite=getAnnotationComposite();
		annotationComposite.enableLoadingImage();		
		((FocusWidget)event.getSource()).setEnabled(false);

		ImageAnnotationTreeNode annotationTreeNode = getAnnotationTreeNode();
		String parentId = null, rootId = null;

		// check if the new annotation is a reply
		if(annotationTreeNode!=null) {
			// if the parent is a reply too it has a rootId		
			// if not the parent is also the root for new annotation
			parentId = annotationTreeNode.getAnnotationId();
			rootId = annotationTreeNode.getAnnotationRootId();
			if(rootId==null) rootId = parentId;
		}
		
		// create the new annotation
		ImageAnnotation annotation= new ImageAnnotation(Application.getImageUrl(), 
				Application.getExternalObjectId(), parentId, rootId, 							
				Application.getUser(), annotationForm.getTitle(), annotationForm.getText(),
				annotationForm.getScope());
		
		// create the fragment if necessary 
		addFragment(annotation);
		
		// now save the annotation
		getImageAnnotationService().createAnnotation(annotation,
			new AsyncCallback<ImageAnnotation>() {
				public void onFailure(Throwable caught) {
					handleFailure(caught, errorMessages.failedToSaveAnnotation());					
				}

				// on success add the annotation to the tree
				public void onSuccess(ImageAnnotation result) {
					ImageAnnotationTreeNode treeNode = getAnnotationTreeNode();
					ImageAnnotation parent = (treeNode == null) ? null : treeNode.getAnnotation();
					annotationComposite.addAnnotation(result, parent);
					annotationComposite.hideAnnotationForm(treeNode, false);
					annotationComposite.disableLoadingImage();
				}
			}
		);
	}	
}
