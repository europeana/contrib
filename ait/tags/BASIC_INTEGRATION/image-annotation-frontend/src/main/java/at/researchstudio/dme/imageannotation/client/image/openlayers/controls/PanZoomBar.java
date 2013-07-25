package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import com.google.gwt.core.client.JavaScriptObject;

public class PanZoomBar extends JavaScriptObject {
	
	protected PanZoomBar() {}
	
	public static native PanZoomBar create() /*-{
		return new $wnd.OpenLayers.Control.PanZoomBar();
	}-*/;

}
