package at.researchstudio.dme.imageannotation.client.image.openlayers.layers;

import com.google.gwt.core.client.JavaScriptObject;

public class Zoomify extends JavaScriptObject {
	
	protected Zoomify() {}
	
	public static native Zoomify create(String name, String url, int width, int height) /*-{
		return new $wnd.OpenLayers.Layer.Zoomify(
			name,
			url,
			new $wnd.OpenLayers.Size(width, height)
		);
	}-*/;
	
	public native final int numberOfTiers() /*-{
		return this.numberOfTiers;
	}-*/;	

}
