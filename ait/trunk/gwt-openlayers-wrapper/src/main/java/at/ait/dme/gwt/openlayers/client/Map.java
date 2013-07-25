package at.ait.dme.gwt.openlayers.client;

import at.ait.dme.gwt.openlayers.client.event.EventListener;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

public class Map extends JavaScriptObject {
	
	/**
	 * Supported event types
	 */
	public static final String EVENT_MOUSEMOVE = "mousemove";
	public static final String EVENT_MOUSEOVER = "mouseover";
	public static final String EVENT_MOVESTART = "movestart";
	public static final String EVENT_MOVE = "move";
	public static final String EVENT_MOVEEND = "moveend";
	
	protected Map() {}
	
	public static native Map create(String applyTo) /*-{
		return new $wnd.OpenLayers.Map(applyTo);
	}-*/;
	
	public static native Map create(String applyTo, JavaScriptObject options) /*-{
		return new $wnd.OpenLayers.Map(applyTo, options);
	}-*/;
	
	public static native Map create(JavaScriptObject applyTo) /*-{
		return new $wnd.OpenLayers.Map({div: applyTo });
	}-*/;
	
	public static native Map create(JavaScriptObject applyTo, JavaScriptObject options) /*-{
		return new $wnd.OpenLayers.Map(applyTo, options);
	}-*/;
	
	public native final Element getDiv() /*-{
		return this.div;
	}-*/;
	
	public native final void addLayer(JavaScriptObject layer) /*-{
		this.addLayer(layer);
	}-*/;
	
	public native final void redraw() /*-{
		this.updateSize();
	}-*/;	
	
	public native final void setLayerZIndex(JavaScriptObject layer, int zIdx) /*-{
		this.setLayerZIndex(layer, zIdx);
	}-*/;
	
	public native final void setBaseLayer(JavaScriptObject layer) /*-{
		this.setBaseLayer(layer);
	}-*/;
	
	public native final int getZoom() /*-{
		return this.getZoom();
	}-*/;
	
	public native final void zoomTo(int level) /*-{
		this.zoomTo(level);
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
	
	public native final void setCenter(float lon, float lat) /*-{
		this.setCenter(new $wnd.OpenLayers.LonLat(lon, lat));
	}-*/;
	
	public native final void setCenter(float lon, float lat, String projection) /*-{
		this.setCenter(new $wnd.OpenLayers.LonLat(lon, lat).transform(new $wnd.OpenLayers.Projection(projection), this.getProjectionObject()));
	}-*/;
	
	public native final LonLat getLonLatFromViewPortPx(Pixel px) /*-{
		return this.getLonLatFromViewPortPx(px);
	}-*/;
	
	public native final Pixel getViewPortPxFromLonLat(LonLat lonLat) /*-{
		return this.getViewPortPxFromLonLat(lonLat);
	}-*/;
	
	public final void registerMapEventListener(EventListener listener, String eventType) {
		_registerMapEventListener(listener.getHandlerFunction(this), eventType);
	};
	
	private final native void _registerMapEventListener(JavaScriptObject handlerFunction, String eventType) /*-{
		this.events.register(eventType, this, handlerFunction);
	}-*/;
	
	public final void unregisterMapEventListener(EventListener listener, String eventType) {
		_unregisterMapEventListener(listener.getHandlerFunction(this), eventType);
	}
	
	private final native void _unregisterMapEventListener(JavaScriptObject handlerFunction, String eventType) /*-{
		this.events.unregister(eventType, this, handlerFunction);
	}-*/; 

}
