package at.ait.dme.gwt.openlayers.client.controls;

public class MouseDefaults extends Control {
	
	protected MouseDefaults() {}
	
	public static native MouseDefaults create() /*-{
		return new $wnd.OpenLayers.Control.MouseDefaults();
	}-*/;

}
