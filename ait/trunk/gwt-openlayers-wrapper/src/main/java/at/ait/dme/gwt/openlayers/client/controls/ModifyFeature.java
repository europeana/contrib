package at.ait.dme.gwt.openlayers.client.controls;

import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;
import at.ait.dme.gwt.openlayers.client.layers.Vector;

public class ModifyFeature extends Control {
	
	protected ModifyFeature() {}
	
	public static ModifyFeature create(Vector layer) {
		return create(layer, false);
	}
	
	public static native ModifyFeature create(Vector layer, boolean standalone) /*-{
		return new $wnd.OpenLayers.Control.ModifyFeature(layer, {standalone:standalone});
	}-*/;
	
	public native final void selectFeature(VectorFeature feature) /*-{
		this.selectFeature(feature);
	}-*/;
	
	public native final void unselectFeature(VectorFeature feature) /*-{
		this.unselectFeature(feature);
	}-*/;

}
