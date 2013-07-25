package at.ait.dme.gwt.openlayers.client.layers;

import com.google.gwt.core.client.JavaScriptObject;

public class TMS extends JavaScriptObject {
	
	protected TMS() {}
	
	public static native TMS create(String url) /*-{
		return new $wnd.OpenLayers.Layer.TMS(
			'TMS',
			url,
			{ layername: '.', serviceVersion: '.', transitionEffect: 'resize', type:'png' }
		);
	}-*/;
	
	public static native TMS create(String url, String type) /*-{
		return new $wnd.OpenLayers.Layer.TMS(
			'TMS',
			url,
			{ layername: '.', serviceVersion: '.', transitionEffect: 'resize', type:type }
		);
	}-*/;
	
	public native final void destroy() /*-{
		this.destroy();
	}-*/;
	
}