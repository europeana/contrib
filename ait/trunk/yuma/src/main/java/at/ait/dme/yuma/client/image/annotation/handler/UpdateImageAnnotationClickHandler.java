/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.client.image.annotation.handler;

import java.util.Date;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationComposite;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationForm;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationTreeNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener for annotation updates
 * 
 * @author Christian Sadilek
 */
public class UpdateImageAnnotationClickHandler extends ImageAnnotationClickHandler {
	// reference to the annotation form to retrieve title and text 
	private ImageAnnotationForm annotationForm;
	
	public UpdateImageAnnotationClickHandler(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, ImageAnnotationForm annotationForm) {
		
		super(annotationComposite, annotationTreeNode);
		this.annotationForm = annotationForm;
	}		
	
	public void onClick(ClickEvent event) {
		final ImageAnnotationComposite annotationComposite=getAnnotationComposite();
		annotationComposite.enableLoadingImage();				
		((FocusWidget)event.getSource()).setEnabled(false);

		// create a new annotation
		ImageAnnotation annotation = new ImageAnnotation(
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getAnnotationId():null, 						
				Application.getImageUrl(),
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getExternalObjectId():null,
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getParentAnnotationId():null, 						
				(getAnnotationTreeNode()!=null)?getAnnotationTreeNode().getAnnotationRootId():null, 							
				Application.getUser(), 
				annotationForm.getAnnotationTitle(), 
				annotationForm.getAnnotationText(),
				annotationForm.getAnnotationScope(),
				annotationForm.getSemanticTags());
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

					annotationComposite.removeAnnotation(getAnnotationTreeNode());
					annotationComposite.addAnnotation(result, getAnnotationTreeNode().
							getParentAnnotation());
					annotationComposite.hideAnnotationForm(getAnnotationTreeNode(), false);
					annotationComposite.disableLoadingImage();
				}
			}
		);
	}	
}
