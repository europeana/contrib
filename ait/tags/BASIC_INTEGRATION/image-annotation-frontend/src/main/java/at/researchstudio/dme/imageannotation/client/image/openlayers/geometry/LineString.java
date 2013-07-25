package at.researchstudio.dme.imageannotation.client.image.openlayers.geometry;

import at.researchstudio.dme.imageannotation.client.image.util.JSUtils;

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
