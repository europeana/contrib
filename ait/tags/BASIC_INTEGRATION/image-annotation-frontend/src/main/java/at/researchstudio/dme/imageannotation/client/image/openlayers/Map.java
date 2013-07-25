package at.researchstudio.dme.imageannotation.client.image.openlayers;

import com.google.gwt.core.client.JavaScriptObject;

public class Map extends JavaScriptObject {
	
	protected Map() {}
	
	public static native Map create(String applyTo) /*-{
		return new $wnd.OpenLayers.Map(applyTo);
	}-*/;
	
	public static native Map create(JavaScriptObject applyTo, JavaScriptObject options) /*-{
		return new $wnd.OpenLayers.Map(applyTo, options);
	}-*/;
	
	public static native Map create(String applyTo, JavaScriptObject options) /*-{
		return new $wnd.OpenLayers.Map(applyTo, options);
	}-*/;
	
	public native final void addLayer(JavaScriptObject layer) /*-{
		this.addLayer(layer);
	}-*/;
	
	public native final void setBaseLayer(JavaScriptObject layer) /*-{
		this.setBaseLayer(layer);
	}-*/;	
	
	public native final void zoomToMaxExtent() /*-{
		this.zoomToMaxExtent();
	}-*/;	
	
	public native final void addControl(JavaScriptObject control) /*-{
		this.addControl(control);
	}-*/;
	
	public native final void removeControl(JavaScriptObject control) /*-{
		this.removeControl(control);
	}-*/;
	
	public native final float getCenterLon() /*-{
		return this.getCenter().lon;
	}-*/;
	
	public native final float getCenterLat() /*-{
		return this.getCenter().lat;
	}-*/;
	
	public native final void redraw() /*-{
		this.updateSize();
	}-*/;	
	
}
