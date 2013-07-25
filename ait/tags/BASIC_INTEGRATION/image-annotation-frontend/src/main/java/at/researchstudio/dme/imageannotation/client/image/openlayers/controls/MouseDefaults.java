package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import com.google.gwt.core.client.JavaScriptObject;

public class MouseDefaults extends JavaScriptObject {
	
	protected MouseDefaults() {}
	
	public static native MouseDefaults create() /*-{
		return new $wnd.OpenLayers.Control.MouseDefaults();
	}-*/;

}
