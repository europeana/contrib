package at.researchstudio.dme.imageannotation.client.image.openlayers.geometry;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import at.researchstudio.dme.imageannotation.client.image.util.JSUtils;

public class LinearRing extends Geometry {
	
	protected LinearRing() {}
	
	public static LinearRing create(Point[] points) {
		return create(JSUtils.toJSArray(points));
	}
	
	private static native LinearRing create(JsArray<JavaScriptObject> points) /*-{
		return new $wnd.OpenLayers.Geometry.LinearRing($wnd.AIT.relayArray(points));
	}-*/;

}
