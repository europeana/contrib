package at.ait.dme.gwt.openlayers.client.geometry;

import at.ait.dme.gwt.openlayers.client.JSUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class LinearRing extends Geometry {
	
	protected LinearRing() {}
	
	public static LinearRing create(Point[] points) {
		return create(JSUtils.toJSArray(points));
	}
	
	private static native LinearRing create(JsArray<JavaScriptObject> points) /*-{
		return new $wnd.OpenLayers.Geometry.LinearRing($wnd.AIT.relayArray(points));
	}-*/;

}
