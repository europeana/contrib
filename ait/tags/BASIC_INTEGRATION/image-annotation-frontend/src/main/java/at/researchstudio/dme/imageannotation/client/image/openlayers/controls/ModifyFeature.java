package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import at.researchstudio.dme.imageannotation.client.image.openlayers.layers.Vector;

import com.google.gwt.core.client.JavaScriptObject;

public class ModifyFeature extends JavaScriptObject {
	
	protected ModifyFeature() {}
	
	public static native ModifyFeature create(Vector layer) /*-{
		return new $wnd.OpenLayers.Control.ModifyFeature(layer);
	}-*/;
	
	public native final void activate() /*-{
		this.activate();
	}-*/;
	
	public native final void deactivate() /*-{
		this.deactivate();
	}-*/;	

}
