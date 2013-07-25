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

package at.ait.dme.yuma.suite.apps.core.client.events;

import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.shared.server.RESTfulServiceException;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationService;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Base click handler class.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public abstract class AbstractClickHandler implements ClickHandler {
	
	/**
	 * Reference to the annotation panel
	 */
	protected AnnotationPanel panel;
	
	/**
	 * Reference to the original annotation (if any)
	 */
	protected AnnotationTreeNode annotation;
	
	/**
	 * Reference to the parent annotation (if any)
	 */
	protected AnnotationTreeNode parent;
	
	/**
	 * Reference to the annotation edit form
	 */
	protected AnnotationEditForm editForm;
	
	/**
	 * Error messages
	 */
	protected I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
	
	public AbstractClickHandler(AnnotationPanel panel, AnnotationTreeNode annotation,
			 AnnotationTreeNode parent, AnnotationEditForm editForm) {
		this.panel = panel;
		this.annotation = annotation;
		this.parent = parent;
		this.editForm = editForm;
	}
		
	/**
	 * Returns a reference to the annotation service used by all click
	 * listeners to create/update/delete annotations.
	 * @return AnnotationServiceAsync the annotation service
	 */
	protected AnnotationServiceAsync getAnnotationService() {
		return (AnnotationServiceAsync) GWT.create(AnnotationService.class);
	}
	
	/**
	 * Handle failures. In case of a conflict reload the application. 
	 * @param t the failure
	 * @param defaultMessage the default error message to use
	 */
	protected void handleFailure(Throwable caught, String defaultMessage) {
		I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
		try {
			throw caught;
		} catch (RESTfulServiceException e) {
			if(e.isConflict()) {
				MessageBox.error(errorMessages.error(), errorMessages.annotationConflict());
				panel.reload();								
			} else {
				MessageBox.error(errorMessages.error(), defaultMessage + " (" + e.getMessage() + ")");
			}
		} catch (Throwable t) {
			MessageBox.error(errorMessages.error(), defaultMessage + " (" + t.getMessage() + ")");
		}									
	}
	
}
