package at.ait.dme.gwt.openlayers.client.controls;

import at.ait.dme.gwt.openlayers.client.event.DragCompleteListener;
import at.ait.dme.gwt.openlayers.client.layers.Vector;

import com.google.gwt.core.client.JavaScriptObject;

public class DragFeature extends Control {
	
	protected DragFeature() {}
	
	public static DragFeature create(Vector layer, DragCompleteListener listener) {
		return create(layer, listener.getJavaScriptObject());
	}
	
	private static native DragFeature create(JavaScriptObject layer, JavaScriptObject listener) /*-{
		return new $wnd.OpenLayers.Control.DragFeature(layer, { onComplete: listener });
	}-*/;

}
