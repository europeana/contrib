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

package at.ait.dme.yuma.suite.apps.core.client.events.selection;

import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents an image annotation selection event
 * 
 * @author Christian Sadilek 
 */
public class AnnotationSelectionEvent extends GwtEvent<AnnotationSelectionHandler> {

	private static final Type<AnnotationSelectionHandler> TYPE = 
		new Type<AnnotationSelectionHandler>();
	 
	private Annotation annotation;
	private boolean selected;
		
	public AnnotationSelectionEvent(Annotation annotation, boolean selected) {
		this.annotation=annotation;
		this.selected=selected;
	}
	
	@Override
	protected void dispatch(AnnotationSelectionHandler handler) {
		handler.onAnnotationSelection(this);
	}

	@Override
	public Type<AnnotationSelectionHandler> getAssociatedType() {
		return TYPE;
	}
	
	public static Type<AnnotationSelectionHandler> getType() {
		return TYPE;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public boolean isSelected() {
		return selected;
	}
	
}
