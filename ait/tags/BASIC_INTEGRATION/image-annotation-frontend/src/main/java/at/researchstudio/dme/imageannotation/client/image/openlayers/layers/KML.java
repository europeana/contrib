package at.researchstudio.dme.imageannotation.client.image.openlayers.layers;

import com.google.gwt.core.client.JavaScriptObject;

public class KML extends JavaScriptObject {
	
	protected KML() {}
	
	public static native KML create(String filename) /*-{
		return new $wnd.OpenLayers.Layer.GML("KML", filename, {
   			format: $wnd.OpenLayers.Format.KML
		});
	}-*/;
	
	public static native KML create(String filename, JavaScriptObject styleMap) /*-{
		return new $wnd.OpenLayers.Layer.GML("KML", filename, {
   			format: $wnd.OpenLayers.Format.KML,
   			styleMap: styleMap
		});
	}-*/;
	
	public native final void setVisibility(boolean visibility) /*-{
		this.setVisibility(visibility);
	}-*/;
	
	public native final void destroy() /*-{
		this.destroy();
	}-*/;
	
}
