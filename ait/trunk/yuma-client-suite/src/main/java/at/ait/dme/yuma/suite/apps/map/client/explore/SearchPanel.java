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

package at.ait.dme.yuma.suite.apps.map.client.explore;

import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.yuma.suite.apps.core.client.I18NConstants;
import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.map.client.TileBasedImageViewer;
import at.ait.dme.yuma.suite.apps.map.shared.server.FindPlaceResponse;
import at.ait.dme.yuma.suite.apps.map.shared.server.FindPlaceService;
import at.ait.dme.yuma.suite.apps.map.shared.server.FindPlaceServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Panel containing the GUI elements needed for the 'Exploration' place
 * search feature (see {@link ExplorationComposite}).
 * 
 * @author Rainer Simon
 */
public class SearchPanel extends FlowPanel {
	
	/**
	 * Reference to the search layer
	 */
	private SearchLayer searchLayer;
	
	/**
	 * Search text box
	 */
	private TextBox searchBox;
	
	private static final String POPUP_TEMPLATE =
		"Lat: @lat@<br/>" +
		"Lon: @lon@<br/>" +
    	"X/Y: @x@/@y@<br/><br/>" +
		"Interpolated from: ";
	
	public SearchPanel(TileBasedImageViewer imageComposite) {
		this.searchLayer = imageComposite.getSearchLayer();		
		I18NConstants i18n = YUMACoreProperties.getConstants();
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("explore-PlaceSearch");
		Label searchLabel = new Label(i18n.searchLabel());
		searchBox = new TextBox();
		Button searchBtn = new Button(i18n.searchButton(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doAsyncGeocoding();				
			}
		});
		hPanel.add(searchLabel);
		hPanel.add(searchBox);
		hPanel.add(searchBtn);
		
		this.add(hPanel);
	}
	
	private void doAsyncGeocoding() {
		FindPlaceServiceAsync service = (FindPlaceServiceAsync) GWT.create(FindPlaceService.class);
		if (searchBox.getValue().trim().length() > 0) {
			service.findPlace(YUMACoreProperties.getObjectURI(), searchBox.getValue(), new AsyncCallback<FindPlaceResponse>() {
				@Override
				public void onFailure(Throwable t) {
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					MessageBox.error(errorMessages.error(), t.getMessage());
				}

				@Override
				public void onSuccess(FindPlaceResponse r) {
			    	if (r != null) {
			    		StringBuffer html = new StringBuffer(POPUP_TEMPLATE
			    			.replace("@lat@", Double.toString(r.latlon.lat))
			    			.replace("@lon@", Double.toString(r.latlon.lon))
			    			.replace("@x@", Double.toString(r.xy.x))
			    			.replace("@y@", Double.toString(r.xy.y)));
			    			
				    	for (String neighbour : r.neighbours) {
				    		html.append(neighbour + ";");
				    	}
				    	searchLayer.showSearchResultPopup(r.xy.x, r.xy.y, r.name, html.toString());
			    	} else {
						I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
						MessageBox.error(errorMessages.error(), errorMessages.transformationError());
					}
				}
			});
		} 
	}

}

