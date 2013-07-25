package at.researchstudio.dme.imageannotation.client.image.openlayers;

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

}
