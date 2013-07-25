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

package at.ait.dme.yuma.client.map.explore;

import at.ait.dme.gwt.openlayers.client.LonLat;
import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.Style;
import at.ait.dme.gwt.openlayers.client.StyleMap;
import at.ait.dme.gwt.openlayers.client.geometry.Point;
import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;
import at.ait.dme.gwt.openlayers.client.layers.Vector;
import at.ait.dme.yuma.client.map.MapComponent;
import at.ait.dme.yuma.client.map.MapPopup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Implements the functionality to display place name geocoding results
 * (see {@link SearchPanel}) on the OpenLayers map with a popup.
 * 
 * @author Rainer Simon
 */
public class SearchLayer {
	
	/**
	 * The OpenLayers map
	 */
	private Map map;
	
	/**
	 * Search results map layer
	 */
	private Vector lSearchResults;
	
	/**
	 * Place search popup
	 */
	private MapPopup popupBubble = null;

	public SearchLayer(MapComponent mapComponent) {
		this.map = mapComponent.getMap();
		
		// Define layer styles
		Style searchStyle = new Style("default");
		searchStyle.pointRadius = 4;
		searchStyle.fillColor = "#ffffff";
		searchStyle.strokeColor = "#000000";
		searchStyle.fillOpacity = 0.8;
		searchStyle.strokeWidth = 2;		
		StyleMap searchStyleMap = StyleMap.create(new Style[] { searchStyle });
		
		// Create layers and add to map
		lSearchResults = Vector.create("search-results", searchStyleMap);
		lSearchResults.setVisibility(false);
		map.addLayer(lSearchResults);
		
		// Add controls
		/*
		DragFeature dragSearchResult = DragFeature.create(lSearchResults, new DragCompleteListener() {
			public void onHandle(JavaScriptObject feature, JavaScriptObject pixel) {
				if (listener != null) {
	  				Point p = (Point) ((VectorFeature) feature).getGeometry();
					listener.searchResultDraggedTo((int) p.getX(), (int) p.getY());
				}
			}
		});
		map.addControl(dragSearchResult);
		dragSearchResult.activate();
		*/
	}
	
	public void showSearchResultPopup(int x, int y, String title, String html) {
		// Remove old popup, if any
    	if (popupBubble != null) {
    		popupBubble.destroy();
    	}
    
    	// Show search result popup
    	popupBubble = new MapPopup(title, html, LonLat.create(x, y), map, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clear();
			}
		});

    	popupBubble.show();
    	
    	// Show marker on vector layer
    	Point p = Point.create(x, y);
    	lSearchResults.destroyFeatures();
    	lSearchResults.addFeature(VectorFeature.create(p));
    	lSearchResults.setVisibility(true);
	}
	
	public void clear() {
		if (popupBubble != null) {
			popupBubble.destroy();
			popupBubble = null;
		}
		
		lSearchResults.setVisibility(false);
	}
	
}
