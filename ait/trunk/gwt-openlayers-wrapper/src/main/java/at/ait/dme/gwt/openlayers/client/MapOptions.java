package at.ait.dme.gwt.openlayers.client;

import com.google.gwt.core.client.JavaScriptObject;

public class MapOptions extends JavaScriptObject {
	
	protected MapOptions() {}
	
	public static native MapOptions create(int width, int height, float maxResolution, int numZoomLevels) /*-{
		return {
			controls: [],
			maxExtent: new $wnd.OpenLayers.Bounds(0, 0, width, height),
			maxResolution: maxResolution,
	        numZoomLevels: numZoomLevels,
	        units: 'pixels'
		};
	}-*/;

	public static native MapOptions create(String projection, String displayProjection, String units, float maxResolution, Bounds maxExtent) /*-{
	    var options = {
	            projection: new $wnd.OpenLayers.Projection(projection),
	            displayProjection: new $wnd.OpenLayers.Projection(displayProjection),
	            units: units,
	            maxResolution: maxResolution,
	            maxExtent: maxExtent
	        };
	}-*/;

}
