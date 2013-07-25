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

package at.ait.dme.yuma.client.image;

import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.annotation.handler.selection.HasImageAnnotationSelectionHandlers;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionEvent;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionHandler;
import at.ait.dme.yuma.client.image.shape.Shape;

import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * Base class for image composites
 * 
 * @author Christian Sadilek
 */
public abstract class ImageComposite extends Composite implements 
		HasImageAnnotationSelectionHandlers, HasLoadHandlers {
	
	final protected HandlerManager handlerManager = new HandlerManager(this);	
	
	@Override
	public HandlerRegistration addImageAnnotationSelectionHandler(
			ImageAnnotationSelectionHandler handler) {
		return handlerManager.addHandler(ImageAnnotationSelectionEvent.getType(), handler);
	}
	
	/**
	 * get the active image fragment
	 * 
	 * @return the active fragment
	 */
	public ImageFragment getImageFragment() {
		Shape shape = getActiveShape();
		if(shape==null) return null;
		
		return new ImageFragment(getVisibleRect(), getImageRect(), getActiveShape());
	}
	
	public abstract ImageRect getImageRect();
	public abstract ImageRect getVisibleRect();
	public abstract Shape getActiveShape();
	
	public abstract void showFragment(ImageAnnotation annotation);	
	public abstract void selectFragment(ImageAnnotation annotation, boolean selected);
	public abstract void hideFragment(ImageAnnotation annotation);
	
	public abstract void showActiveFragmentPanel(ImageAnnotation annotation, boolean forceVisible);
	public abstract void hideActiveFragmentPanel();	
}
