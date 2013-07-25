package at.researchstudio.dme.imageannotation.client.image.openlayers.layers;

import at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureAddedListener;
import at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureSelectedListener;
import at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureUnselectedListener;
import at.researchstudio.dme.imageannotation.client.image.util.JSUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Vector extends JavaScriptObject {
	
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
	
	public final void addFeatures(JavaScriptObject[] features) {
		_addFeatures(JSUtils.toJSArray(features));
	};
	
	private native final void _addFeatures(JsArray<JavaScriptObject> features) /*-{
		this.addFeatures(features);
	}-*/;
	
	public native final void destroyFeatures() /*-{
		this.destroyFeatures();
	}-*/;
	
	public native final void removeFeature(JavaScriptObject feature) /*-{
		this.removeFeatures(feature);
	}-*/;
	
	public final void removeFeatures(JavaScriptObject[] features) {
		_removeFeatures(JSUtils.toJSArray(features));
	}
	
	private native final void _removeFeatures(JsArray<JavaScriptObject> features) /*-{
		this.removeFeatures(features);
	}-*/;
	
	public final JavaScriptObject[] getShapes() {
		return JSUtils.toJavaArray(_getFeatures());
	}
	
	private native final JsArray<JavaScriptObject> _getFeatures() /*-{
		var geometries = new Array();
		for (i=0; i<this.features.length; i++) {
			geometries.push(this.features[i].geometry);
		} 
		return geometries;
	}-*/; 
	
	public final void addVectorFeatureAddedListener(VectorFeatureAddedListener listener) {
		_addVectorFeatureAddedListener(listener.getJavaScriptObject());
	}
	
	private native final void _addVectorFeatureAddedListener(JavaScriptObject listener) /*-{
		this.events.register('featureadded', this, listener);
	}-*/; 
	
	public final void addVectorFeatureSelectedListener(VectorFeatureSelectedListener listener) {
		_addVectorFeatureSelectedListener(listener.getJavaScriptObject());
	}
	
	private native final void _addVectorFeatureSelectedListener(JavaScriptObject listener) /*-{
		this.events.register('featureselected', this, listener);
	}-*/; 
	
	public final void addVectorFeatureUnselectedListener(VectorFeatureUnselectedListener listener) {
		_addVectorFeatureUnselectedListener(listener.getJavaScriptObject());
	}
	
	private native final void _addVectorFeatureUnselectedListener(JavaScriptObject listener) /*-{
		this.events.register('featureunselected', this, listener);
	}-*/; 
	

}
