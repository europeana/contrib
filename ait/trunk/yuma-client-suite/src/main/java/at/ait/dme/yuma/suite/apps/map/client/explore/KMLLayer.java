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

import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.Style;
import at.ait.dme.gwt.openlayers.client.StyleMap;
import at.ait.dme.gwt.openlayers.client.controls.SelectFeature;
import at.ait.dme.gwt.openlayers.client.layers.KML;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.map.client.widgets.MapComponent;

/**
 * Implements functionality to load KML vector data through the server-side
 * converter servlet and display it on the OpenLayers map.
 * 
 * @author Rainer Simon
 */
public class KMLLayer {
	
	/**
	 * KML importer base URL
	 */
	private final static String KML_IMPORT_BASE_URL = "importkml";

	/**
	 * The OpenLayers map
	 */
	private Map map;
	
	/**
	 * OpenLayers KML layer
	 */
	private KML lKML = null;
	
	/**
	 * KML styleMap
	 */
	private StyleMap kmlStyleMap;
	
	/**
	 * OpenLayers controls
	 */
	private SelectFeature hilightControl = null;
	
	public KMLLayer(MapComponent mapComponent) {
		this.map = mapComponent.getMap();
		
		// Define layer style
		Style kmlStyle = new Style("default");
		kmlStyle.pointRadius = 4;
		kmlStyle.fillColor = "#ff0000";
		kmlStyle.strokeColor = "#320000";
		kmlStyle.fillOpacity = 0.35;
		kmlStyle.strokeWidth = 2;		
		kmlStyleMap = StyleMap.create(new Style[] { kmlStyle });
	}
	
	public void showKml(String url) {
		String kmlUrl = KML_IMPORT_BASE_URL + "?map=" + YUMACoreProperties.getObjectURI() + "&kml=" + url;

		// Destroy previous KML, if any
		if (lKML != null) {
			lKML.destroy();
		}
		
		// Create new KML layer		
		lKML = KML.create(kmlUrl, kmlStyleMap);
		addSelectFeature(lKML); 
		map.addLayer(lKML);
		map.setLayerZIndex(lKML, 499);
	}
	
	public void hideKml() {
		if (lKML != null)
			lKML.setVisibility(false);
	}
	
	private void addSelectFeature(KML layer) {
		hilightControl = SelectFeature.create(layer);
		hilightControl.setHover(true);
		map.addControl(hilightControl);
		hilightControl.activate();	
	}

}
