package at.ait.dme.gwt.openlayers.client.controls;

public class PanZoomBar extends Control {
	
	protected PanZoomBar() {}
	
	public static native PanZoomBar create() /*-{
		return new $wnd.OpenLayers.Control.PanZoomBar();
	}-*/;

}
