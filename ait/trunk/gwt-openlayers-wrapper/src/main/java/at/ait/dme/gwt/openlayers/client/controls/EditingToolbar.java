package at.ait.dme.gwt.openlayers.client.controls;

import at.ait.dme.gwt.openlayers.client.layers.Vector;

public class EditingToolbar extends Control {
	
	protected EditingToolbar() {}
	
	public static native EditingToolbar create(Vector layer) /*-{
		return new $wnd.OpenLayers.Control.EditingToolbar(layer);
	}-*/;
	


}
