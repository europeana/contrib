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
package at.ait.dme.yuma.suite.apps.map.client;

import org.gwt.mosaic.ui.client.DecoratedTabLayoutPanel;
import org.gwt.mosaic.ui.client.TabLayoutPanel;
import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.suite.apps.core.client.I18NConstants;
import at.ait.dme.yuma.suite.apps.core.client.MediaViewer;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.widgets.MinMaxWindowPanel;
import at.ait.dme.yuma.suite.apps.core.shared.model.User;
import at.ait.dme.yuma.suite.apps.core.shared.server.auth.AuthService;
import at.ait.dme.yuma.suite.apps.core.shared.server.auth.AuthServiceAsync;
import at.ait.dme.yuma.suite.apps.map.client.TileBasedImageViewer;
import at.ait.dme.yuma.suite.apps.map.client.annotation.MapAnnotationPanel;
import at.ait.dme.yuma.suite.apps.map.client.explore.ExplorationComposite;
import at.ait.dme.yuma.suite.apps.map.client.georeferencing.ControlPointPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the entry point to the application.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class YumaMapClient implements EntryPoint {

	private static I18NConstants annotationConstants = null;
	
	private MediaViewer mapViewer = null;
	
	public YumaMapClient() {}
	
	public void onModuleLoad() {
		String mapUrl = YUMACoreProperties.getObjectURI();
		if (mapUrl != null)
			initApplication(mapUrl);
	}
		
	private void initApplication(String mapUrl) {
		mapViewer = new TileBasedImageViewer(mapUrl);
		RootPanel.get().add(mapViewer, 0, 0);
		
		mapViewer.addLoadHandler(new LoadHandler() {
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
		
		TabLayoutPanel tabPanel = new DecoratedTabLayoutPanel();
		tabPanel.setPadding(0);
		showAnnotationsTab(tabPanel);

		showGeoReferencingTab(tabPanel);
		showExplorationTab(tabPanel);
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
				
			TileBasedImageViewer tic = (TileBasedImageViewer) mapViewer;

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem().intValue() == 0) {
					// Annotations tab
					tic.showAnnotationLayer();
				} else if (event.getSelectedItem().intValue() == 1) {
					// Georeferencing tab
					tic.showControlPointLayer();
				} else if (event.getSelectedItem().intValue() == 2) {
					// Exploration tab
				}
			}

		});

		window.setWidget(tabPanel);
	}
	
	private void showAnnotationsTab(TabLayoutPanel tabPanel) {
		AnnotationPanel annComposite;

		annComposite = new MapAnnotationPanel(mapViewer); 
		annComposite.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				mapViewer.selectAnnotation(event.getAnnotation(), event.isSelected());
			}
		});
		tabPanel.add(annComposite, YUMACoreProperties.getConstants().tabAnnotations());
	}
	
	private void showGeoReferencingTab(TabLayoutPanel tabPanel) {
		AnnotationPanel geoRefComposite =
			new ControlPointPanel((TileBasedImageViewer) mapViewer); 

		geoRefComposite.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				mapViewer.selectAnnotation(event.getAnnotation(), event.isSelected());
			}
		});
				
		tabPanel.add(geoRefComposite, YUMACoreProperties.getConstants().tabGeoReferencing());
	}
	
	private void showExplorationTab(TabLayoutPanel tabPanel) {
		ExplorationComposite expComposite = new ExplorationComposite((TileBasedImageViewer)mapViewer);
		tabPanel.add(expComposite, YUMACoreProperties.getConstants().tabExploration());
	}

	public static native void reload() /*-{
     	$wnd.location.reload();
  	}-*/;

}
