package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.VectorFeature;
import at.researchstudio.dme.imageannotation.client.image.openlayers.layers.Vector;

import com.google.gwt.core.client.JavaScriptObject;

public class SelectFeature extends JavaScriptObject {
	
	protected SelectFeature() {}
	
	public static native SelectFeature create(Vector layer) /*-{
		return new $wnd.OpenLayers.Control.SelectFeature(layer);
	}-*/;
	
	public native final void activate() /*-{
		this.activate();
	}-*/;
	
	public native final void deactivate() /*-{
		this.deactivate();
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
