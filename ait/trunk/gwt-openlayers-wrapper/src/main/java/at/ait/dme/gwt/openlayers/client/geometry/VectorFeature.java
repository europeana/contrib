package at.ait.dme.gwt.openlayers.client.geometry;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorFeature extends JavaScriptObject {
	
	protected VectorFeature() {}
	
	public static native VectorFeature create(Geometry geometry) /*-{
		return new $wnd.OpenLayers.Feature.Vector(geometry);
	}-*/;
	
	public final native JavaScriptObject getGeometry() /*-{
		return this.geometry;
	}-*/;

}
