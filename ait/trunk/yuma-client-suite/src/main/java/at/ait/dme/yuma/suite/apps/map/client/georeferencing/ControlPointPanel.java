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

package at.ait.dme.yuma.suite.apps.map.client.georeferencing;

import java.util.Collection;

import org.gwt.mosaic.ui.client.MessageBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.events.AnnotateClickHandler;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.model.User;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationService;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationServiceAsync;
import at.ait.dme.yuma.suite.apps.map.client.TileBasedImageViewer;
import at.ait.dme.yuma.suite.apps.map.client.annotation.MapAnnotationPanel;
import at.ait.dme.yuma.suite.apps.map.client.treeview.MapAnnotationTreeNode;

/**
 * Composite used to manage control points
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ControlPointPanel extends MapAnnotationPanel {
	
	/**
	 * 'Create control point' button
	 */
	private PushButton createButton;

	public ControlPointPanel(TileBasedImageViewer imageViewer)   {
		super(imageViewer,
				new ControlPointEditForm(imageViewer.getControlPointLayer()),
				new MapAnnotationTreeNode());
	}

	@Override
	protected Widget createHeader() {
		// The parent header panel
		FlowPanel header = new FlowPanel();
		
		// 'Help geo-Reference' label
		Label addAnnotationLabel = new Label(YUMACoreProperties.getConstants().helpGeoreference());
		addAnnotationLabel.setStyleName("imageAnnotation-add-annotation");
		header.add(addAnnotationLabel);
		
		// 'Help' link
		HTML help = new HTML("<a target=\"_blank\" href=\"userguide_" + 
				LocaleInfo.getCurrentLocale().getLocaleName()+".html\">" + 
				YUMACoreProperties.getConstants().help() + "</a>" );
		help.setStyleName("imageAnnotation-help");
		header.add(help);		
		
		// Instructions text
		Label addAnnotationHint = new Label(YUMACoreProperties.getConstants().helpGeoreferenceHint()); 
		addAnnotationHint.setStyleName("imageAnnotation-add-annotation-hint");
		header.add(addAnnotationHint);
		
		// Button panel
		HorizontalPanel buttons = new HorizontalPanel();
		
		// 'Create Control Point' button
		createButton = new PushButton(YUMACoreProperties.getConstants().actionCreateCP());
		createButton.setStyleName("imageAnnotation-button");
		createButton.addClickHandler(new AnnotateClickHandler(this, null, null, false));
		createButton.setEnabled(!User.get().isAnonymous());
		buttons.add(createButton);
		
		if (YUMACoreProperties.getObjectURI().startsWith("http://georeferencer"))
			createButton.setEnabled(false);
		
		header.add(buttons);
		
		// Placeholder for the annotation form 
		header.add(editFormPanel);
		
		return header;
	}
	
	@Override
	protected void loadAnnotations() {
		AnnotationServiceAsync imageAnnotationService = (AnnotationServiceAsync) GWT
				.create(AnnotationService.class);

		imageAnnotationService.listAnnotations(YUMACoreProperties.getObjectURI(),
			new AsyncCallback<Collection<Annotation>>() {
				public void onFailure(Throwable t) {
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					MessageBox.error(errorMessages.error(), errorMessages.failedToReadAnnotations() + " (" + t.getMessage() + ")");
				}

				public void onSuccess(Collection<Annotation> foundAnnotations) {
					annotationTree.removeItems();
					
					for (Annotation a : foundAnnotations) {
						if (isControlPoint(a)) {
							annotationTree.addAnnotation(a);
						}
					}
					
					treePanel.add(annotationTree);				
					disableLoadingImage();
					layout();
				}
			});	
	}
	
	
}
