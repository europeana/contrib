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

import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.image.ImageComposite;
import at.ait.dme.yuma.client.image.ImageFragment;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationComposite;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationTreeNode;
import at.ait.dme.yuma.client.image.shape.VoidShape;
import at.ait.dme.yuma.client.server.ImageAnnotationService;
import at.ait.dme.yuma.client.server.ImageAnnotationServiceAsync;
import at.ait.dme.yuma.client.server.exception.RESTfulServiceException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * base click listener class
 * 
 * @author Christian Sadilek
 */
public abstract class ImageAnnotationClickHandler implements ClickHandler {
	private ImageAnnotationComposite annotationComposite = null;
	private ImageAnnotationTreeNode annotationTreeNode = null;

	protected ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
	
	public ImageAnnotationClickHandler(ImageAnnotationComposite annotationComposite,
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
		ImageFragment fragment = null;
		if(imageComposite.getActiveShape()!=null) {
			fragment = new ImageFragment(
					imageComposite.getVisibleRect(),					
					imageComposite.getImageRect(),
					imageComposite.getActiveShape());
		} else {
			fragment = new ImageFragment(new VoidShape());
		}
		annotation.setFragment(fragment);
	}
	/**
	 * handle failures. in case of a conflict reload the application. 
	 * 
	 * @param caught
	 * @param defaultMessage
	 */
	protected void handleFailure(Throwable caught, String defaultMessage) {
		ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
		try {
			throw caught;
		} catch (RESTfulServiceException rse) {
			if(rse.isConflict()) {
				MessageBox.error(errorMessages.error(), errorMessages.annotationConflict());
				annotationComposite.refreshTree();								
			} else {
				MessageBox.error(errorMessages.error(), defaultMessage);
			}
		} catch (Throwable t) {
			MessageBox.error(errorMessages.error(), defaultMessage);
		}									
	}
}
