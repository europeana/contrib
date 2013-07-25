package at.ait.dme.gwt.openlayers.client.controls;

public class MousePosition extends Control {
	
	protected MousePosition() {}
	
	public static native MousePosition create() /*-{
		return new $wnd.OpenLayers.Control.MousePosition();
	}-*/;

}
