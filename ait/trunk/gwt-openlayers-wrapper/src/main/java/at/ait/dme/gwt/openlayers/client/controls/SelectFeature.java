package at.ait.dme.gwt.openlayers.client.controls;

import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;
import at.ait.dme.gwt.openlayers.client.layers.Vector;

public class SelectFeature extends Control {
	
	protected SelectFeature() {}
	
	public static SelectFeature create(Vector layer) {
		return create(layer, false);
	};
	
	public static native SelectFeature create(Vector layer, boolean hover) /*-{
		return new $wnd.OpenLayers.Control.SelectFeature(layer, {hover:hover});
	}-*/;
	
	public native final void setHover(boolean hover) /*-{
		this.hover = hover;
	}-*/;
	
	public native final void select(VectorFeature feature) /*-{
		this.select(feature);
	}-*/;
	
	public native final void unselect(VectorFeature feature) /*-{
		this.unselect(feature);
	}-*/;	

}
