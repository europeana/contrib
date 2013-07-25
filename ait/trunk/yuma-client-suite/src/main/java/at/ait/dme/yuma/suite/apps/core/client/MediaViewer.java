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

package at.ait.dme.yuma.suite.apps.core.client;

import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.selection.HasAnnotationSelectionHandlers;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.model.MediaFragment;

import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * Base class for all YUMA 'media viewers'. A media viewer shows
 * the annotatable media (image, map, audio, video) and provides
 * the necessary methods for showing, hiding, editing, etc.
 * annotations. 
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public abstract class MediaViewer extends Composite implements 
		HasAnnotationSelectionHandlers, HasLoadHandlers {
	
	final protected HandlerManager handlerManager = new HandlerManager(this);	
	
	@Override
	public HandlerRegistration addAnnotationSelectionHandler(AnnotationSelectionHandler handler) {
		return handlerManager.addHandler(AnnotationSelectionEvent.getType(), handler);
	}
	
	/**
	 * Returns the media fragment that is currently active in this media viewer.
	 * @return the currently active media fragment
	 */
	public abstract MediaFragment getActiveMediaFragment();		
		
	/**
	 * Selects or de-selects an annotation in this media viewer
	 * @param annotation the annotation
	 * @param selected true for select, false for de-selecet
	 */
	public abstract void selectAnnotation(Annotation annotation, boolean selected);
	
	/**
	 * Shows an annotation in this media viewer
	 * @param annotation the annotation
	 */
	public abstract void showAnnotation(Annotation annotation);	
	
	/**
	 * Hides an annotation in this media viewer (by removing it from the GUI)
	 * @param annotation the annotation
	 */
	public abstract void hideAnnotation(Annotation annotation);
	
	/**
	 * Starts editing mode for the specified annotation
	 * @param annotation the annotation
	 */
	public abstract void editAnnotation(Annotation annotation);
	
	/**
	 * Sets a reference to the edit form, so that the media viewer can update
	 * the form during editing
	 * @param editForm the edit form
	 */
	public abstract void setAnnotationEditForm(AnnotationEditForm editForm);
	
	/**
	 * Stops editing mode (for all annotations)
	 */
	public abstract void stopEditing();	
		
}
