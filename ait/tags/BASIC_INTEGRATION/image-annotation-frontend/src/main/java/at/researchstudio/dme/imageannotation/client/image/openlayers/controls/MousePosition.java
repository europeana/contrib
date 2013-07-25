package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import com.google.gwt.core.client.JavaScriptObject;

public class MousePosition extends JavaScriptObject {
	
	protected MousePosition() {}
	
	public static native MousePosition create() /*-{
		return new $wnd.OpenLayers.Control.MousePosition();
	}-*/;

}
