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

package at.ait.dme.yuma.suite.apps.image.client;

import org.gwt.mosaic.ui.client.DecoratedTabLayoutPanel;
import org.gwt.mosaic.ui.client.TabLayoutPanel;
import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.suite.apps.core.client.MediaViewer;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.widgets.MinMaxWindowPanel;
import at.ait.dme.yuma.suite.apps.core.shared.model.User;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation.MediaType;
import at.ait.dme.yuma.suite.apps.core.shared.server.auth.AuthService;
import at.ait.dme.yuma.suite.apps.core.shared.server.auth.AuthServiceAsync;
import at.ait.dme.yuma.suite.apps.image.core.client.treeview.ImageAnnotationEditForm;
import at.ait.dme.yuma.suite.apps.image.core.client.treeview.ImageAnnotationTreeNode;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point to the image annotation application.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class YumaImageClient implements EntryPoint {
	
	private MediaViewer imageViewer = null;
	
	public YumaImageClient() {}
	
	public void onModuleLoad() {
		String imageUrl = YUMACoreProperties.getObjectURI();
		if (imageUrl != null)
			initApplication(imageUrl);
	}
		
	private void initApplication(String imageUrl) {
		imageViewer = new ImageViewer(imageUrl);
		RootPanel.get().add(imageViewer, 10, 10);
		
		imageViewer.addLoadHandler(new LoadHandler() {
			public void onLoad(LoadEvent events) {
				AuthServiceAsync authService =
					(AuthServiceAsync) GWT.create(AuthService.class);
				
				authService.getUser(new AsyncCallback<User>() {
					public void onFailure(Throwable caught) {
						User.set(User.ANONYMOUS);
						showAnnotations();
					}
					public void onSuccess(User user) {
						User.set(user);
						showAnnotations();
					}
				});					
			}			
		});
	}

	private void showAnnotations() {
		// Create a floating window
		final WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(500, 50, 430, 500);
		window.show();
		window.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// we do not allow to close this window
				window.show();
			}
		});
		
		// Parent tab panel 
		TabLayoutPanel tabPanel = new DecoratedTabLayoutPanel();
		tabPanel.setPadding(0);
		showAnnotationsTab(tabPanel);
		window.setWidget(tabPanel);
	}
	
	private void showAnnotationsTab(TabLayoutPanel tabPanel) {
		AnnotationPanel annComposite;

		annComposite = new AnnotationPanel(
				imageViewer, 
				new ImageAnnotationEditForm(
						MediaType.IMAGE,
						((ImageViewer) imageViewer).getTagCloud()),
				new ImageAnnotationTreeNode());			

		annComposite.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				imageViewer.selectAnnotation(event.getAnnotation(), event.isSelected());
			}
		});
		tabPanel.add(annComposite, YUMACoreProperties.getConstants().tabAnnotations());
	}

	public static native void reload() /*-{
     	$wnd.location.reload();
  	}-*/;
	
}
