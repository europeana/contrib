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

package at.ait.dme.yuma.suite.apps.map.client.widgets;

import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.MapOptions;
import at.ait.dme.gwt.openlayers.client.MapWidget;
import at.ait.dme.gwt.openlayers.client.controls.MouseDefaults;
import at.ait.dme.gwt.openlayers.client.controls.MousePosition;
import at.ait.dme.gwt.openlayers.client.controls.PanZoomBar;
import at.ait.dme.gwt.openlayers.client.layers.TMS;
import at.ait.dme.gwt.openlayers.client.layers.Zoomify;
import at.ait.dme.yuma.suite.apps.map.shared.Tileset;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * Wraps an OpenLayers map widget with a TMS base layer and
 * default mouse controls.
 *
 * @author Rainer Simon
 */
public class MapComponent extends LayoutPanel {
	
	/**
	 * Needed for hack due to missing onResize events after load on Mozilla et al
	 */
	private static int LAYOUT_DELAY_MILLIS = 10;
	
	/**
	 * The OpenLayers map
	 */
	private Map map;
	
	/**
	 * The map URL
	 */
	private String mapUrl;
	
	/**
	 * The map image width
	 */
	private int mapWidth;
	
	/**
	 * The map image height
	 */
	private int mapHeight;
	
	/**
	 * Hack due to missing onResize events after load on Mozilla et al.
	 * Note: handled exactly the same way in gwt-mosaic.
	 */
    private final Timer resizeTimer = new Timer() {
        @Override
        public void run() {
            map.redraw();
            map.zoomToMaxExtent();
        }
    };
	
	public MapComponent(Tileset tileset) {
		// Create map
		MapOptions mapOptions = MapOptions.create(tileset.getWidth(), tileset.getHeight(), (int) Math.pow(2, tileset.getZoomLevels()-1), tileset.getZoomLevels());
	    MapWidget widget = new MapWidget(mapOptions);
		map = widget.getMap();
		
		// Create TMS base layer
		if (tileset.getTilingScheme().equalsIgnoreCase("tms")) {
			TMS lTMS= TMS.create(tileset.getUrl(), tileset.getTileFormat());
			map.addLayer(lTMS);
			map.setBaseLayer(lTMS);
		} else {
			Zoomify lZoomify= Zoomify.create("Baselayer", tileset.getUrl(), tileset.getWidth(), tileset.getHeight());
			map.addLayer(lZoomify);
			map.setBaseLayer(lZoomify);
		}
		
		// Add controls
		map.addControl(MouseDefaults.create());
		map.addControl(PanZoomBar.create());
		map.addControl(MousePosition.create());
		
		// Keep map URL reference and width/height for later
		this.mapUrl = tileset.getUrl();
		this.mapWidth = tileset.getWidth();
		this.mapHeight = tileset.getHeight();
		
		// Create panel
		this.setWidth("100%");
		this.setHeight("100%");
		this.add(widget);
	}
	
	public Map getMap() {
		return map;
	}
	
	public String getMapURL() {
		return mapUrl;
	}
	
	public int getMapWidth() {
		return mapWidth;
	}
	
	public int getMapHeight() {
		return mapHeight;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		resizeTimer.schedule(LAYOUT_DELAY_MILLIS);
	}

}
