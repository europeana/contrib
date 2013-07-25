package at.ait.dme.gwt.openlayers.client.geometry;

import at.ait.dme.gwt.openlayers.client.JSUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class LineString extends Geometry {
	
	protected LineString() {}
	
	public static LineString create(Point[] points) {
		return create(JSUtils.toJSArray(points));
	}
	
	private static native LineString create(JsArray<JavaScriptObject> points) /*-{
		return new $wnd.OpenLayers.Geometry.LineString($wnd.AIT.relayArray(points));
	}-*/;

}
