package at.ait.dme.gwt.openlayers.client;

import com.google.gwt.core.client.JavaScriptObject;

public class LonLat extends JavaScriptObject {

	protected LonLat() { }
	
	public static native LonLat create(float lon, float lat) /*-{
		return new $wnd.OpenLayers.LonLat(lon, lat);
	}-*/;
	
	public final native float getLon() /*-{
		return this.lon;
	}-*/;
	
	public final native float getLat() /*-{
		return this.lat;
	}-*/;
	
}
