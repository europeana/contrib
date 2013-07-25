package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import com.google.gwt.core.client.JavaScriptObject;

public class EditingToolbar extends JavaScriptObject {
	
	protected EditingToolbar() {}
	
	public static native EditingToolbar create(JavaScriptObject layer) /*-{
		return new $wnd.OpenLayers.Control.EditingToolbar(layer);
	}-*/;
	
	public static native EditingToolbar create(JavaScriptObject layer, String containerDiv) /*-{
		var container = document.getElementById("win-tools");
		return new $wnd.OpenLayers.Control.EditingToolbar(
			layer, {div: container}
		);
	}-*/;
	
	public native final void activate() /*-{
		this.activate();
	}-*/;
	
	public native final void deactivate() /*-{
		this.deactivate();
	}-*/;	

}
