package at.ac.ait.dme.gtv.client;

import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.MapWidget;
import at.ait.dme.gwt.openlayers.client.layers.OSM;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * Creates a layout panel containing an OpenLayers map with
 * a plain OSM base layer.
 * 
 * @author Rainer Simon
 */
public class GTVMapPanel extends LayoutPanel {
	
	/**
	 * The OpenLayers map
	 */
	private Map map;
    
	/**
	 * Hack due to missing onResize events after load on Mozilla et al.
	 * Note: handled exactly the same way in gwt-mosaic.
	 */
    private final Timer resizeTimer = new Timer() {
        @Override
        public void run() {
        	map.redraw();
        }
    };
	
	public GTVMapPanel() { 
	    MapWidget widget = new MapWidget();
		map = widget.getMap();
		OSM osmLayer = OSM.create();
		map.addLayer(osmLayer);
		map.setBaseLayer(osmLayer);
		this.add(widget);
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		int LAYOUT_DELAY_MILLIS=10;resizeTimer.schedule(LAYOUT_DELAY_MILLIS);
	}
	
	@Override
	public void onResize() {
		super.onResize();
		map.redraw();
	}
	
	/**
	 * Re-centers the map on the specified coordinate.
	 * @param lon center longitude
	 * @param lat center latitude
	 */
	public void setMapCenter(float lon, float lat) {
		map.setCenter(lon, lat, "EPSG:4326");
	}
	
	/**
	 * Zooms the map to the specified zoom level.
	 * @param zoomLevel the zoom level
	 */
	public void setMapZoom(int zoomLevel) {
		map.zoomTo(zoomLevel);
	}
	
	/**
	 * Adds a marker at the specified coordinate.
	 * @param lon the marker longitude
	 * @param lat the marker latitude
	 */
	public void addMarker(float lon, float lat) {
		// TODO implement this method
	}

}
