package at.ait.dme.gwt.openlayers.client.layers;

import com.google.gwt.core.client.JavaScriptObject;

public class OSM extends JavaScriptObject {
	
	protected OSM() {}
	
	public static native OSM create() /*-{
		return new $wnd.OpenLayers.Layer.OSM();
	}-*/;
	
	public native final void destroy() /*-{
		this.destroy();
	}-*/;
	
}
