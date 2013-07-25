package at.ait.dme.gwt.openlayers.client.controls;

import at.ait.dme.gwt.openlayers.client.layers.Vector;
import at.ait.dme.gwt.openlayers.client.handler.Handler;

public class DrawFeature extends Control {
	
	protected DrawFeature() {}
	
	public static native DrawFeature create(Vector layer, Handler handler) /*-{
		return new $wnd.OpenLayers.Control.DrawFeature(layer, handler);
	}-*/;

}
