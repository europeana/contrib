package at.ait.dme.gwt.openlayers.client.layers;

import at.ait.dme.gwt.openlayers.client.JSUtils;
import at.ait.dme.gwt.openlayers.client.event.LayerEventListener;
import at.ait.dme.gwt.openlayers.client.event.VectorFeatureAddedListener;
import at.ait.dme.gwt.openlayers.client.event.VectorFeatureSelectedListener;
import at.ait.dme.gwt.openlayers.client.event.VectorFeatureUnselectedListener;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

@SuppressWarnings("deprecation")
public class Vector extends JavaScriptObject {
	
	/**
	 * Supported event types
	 */
	public static final String FEATURE_ADDED = "featureadded";
	public static final String FEATURE_SELECTED = "featureselected";
	public static final String FEATURE_UNSELECTED = "featureunselected";
	public static final String FEATURE_MODIFIED = "featuremodified";
	public static final String AFTER_FEATURE_MODIFIED = "afterfeaturemodified";
	public static final String VERTEX_MODIFIED = "vertexmodified";
	
	protected Vector() {}
	
	public static native Vector create(String name) /*-{
		return new $wnd.OpenLayers.Layer.Vector(name);
	}-*/;
	
	public static native Vector create(String name, JavaScriptObject styleMap) /*-{
		return new $wnd.OpenLayers.Layer.Vector(name, {styleMap:styleMap});
	}-*/;
	
	public native final void setVisibility(boolean visibility) /*-{
		this.setVisibility(visibility);
	}-*/;
	
	public native final boolean isVisible() /*-{
		return this.getVisibility();
	}-*/;
	
	public final native void addFeature(JavaScriptObject feature) /*-{
		this.addFeatures(feature);
	}-*/;
	
	public native final void addFeatures(JsArray<JavaScriptObject> features) /*-{
		this.addFeatures(features);
	}-*/;
	
	public native final void destroyFeatures() /*-{
		this.destroyFeatures();
	}-*/;
	
	public native final void removeFeature(JavaScriptObject feature) /*-{
		this.removeFeatures(feature);
	}-*/;
	
	public final JavaScriptObject[] getShapes() {
		return JSUtils.toJavaArray(getFeatures());
	}
	
	private native final JsArray<JavaScriptObject> getFeatures() /*-{
		var geometries = new Array();
		for (i=0; i<this.features.length; i++) {
			geometries.push(this.features[i].geometry);
		} 
		return geometries;
	}-*/; 
	
	public final void addLayerEventListener(LayerEventListener listener, String eventType) {
		_addLayerEventListener(listener.getJavaScriptObject(), eventType);
	}
	
	private native final void _addLayerEventListener(JavaScriptObject listener, String eventType) /*-{
		this.events.register(eventType, this, listener);
	}-*/;
	
	@Deprecated
	public final void addVectorFeatureAddedListener(VectorFeatureAddedListener listener) {
		_addVectorFeatureAddedListener(listener.getJavaScriptObject());
	}

	@Deprecated
	private native final void _addVectorFeatureAddedListener(JavaScriptObject listener) /*-{
		this.events.register('featureadded', this, listener);
	}-*/; 
	
	@Deprecated
	public final void addVectorFeatureSelectedListener(VectorFeatureSelectedListener listener) {
		_addVectorFeatureSelectedListener(listener.getJavaScriptObject());
	}
	
	@Deprecated
	private native final void _addVectorFeatureSelectedListener(JavaScriptObject listener) /*-{
		this.events.register('featureselected', this, listener);
	}-*/; 
	
	@Deprecated
	public final void addVectorFeatureUnselectedListener(VectorFeatureUnselectedListener listener) {
		_addVectorFeatureUnselectedListener(listener.getJavaScriptObject());
	}
	
	@Deprecated
	private native final void _addVectorFeatureUnselectedListener(JavaScriptObject listener) /*-{
		this.events.register('featureunselected', this, listener);
	}-*/; 

}
