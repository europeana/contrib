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

package at.ait.dme.yuma.client.image.annotation.handler.selection;

import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents an image annotation selection event
 * 
 * @author Christian Sadilek 
 */
public class ImageAnnotationSelectionEvent extends GwtEvent<ImageAnnotationSelectionHandler> {

	private static final Type<ImageAnnotationSelectionHandler> TYPE = 
		new Type<ImageAnnotationSelectionHandler>();
	 
	private ImageAnnotation annotation;
	private boolean selected;
		
	public ImageAnnotationSelectionEvent(ImageAnnotation annotation, boolean selected) {
		this.annotation=annotation;
		this.selected=selected;
	}
	
	@Override
	protected void dispatch(ImageAnnotationSelectionHandler handler) {
		handler.onAnnotationSelection(this);
	}

	@Override
	public Type<ImageAnnotationSelectionHandler> getAssociatedType() {
		return TYPE;
	}
	
	public static Type<ImageAnnotationSelectionHandler> getType() {
		return TYPE;
	}

	public ImageAnnotation getAnnotation() {
		return annotation;
	}

	public boolean isSelected() {
		return selected;
	}
}
