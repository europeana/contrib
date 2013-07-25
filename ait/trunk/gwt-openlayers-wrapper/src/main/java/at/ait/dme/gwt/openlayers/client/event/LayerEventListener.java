package at.ait.dme.gwt.openlayers.client.event;

import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class LayerEventListener {
	
	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = LayerEventListenerImpl.createHandler(this);

    public abstract void onEvent(VectorFeature feature);
	
    public JavaScriptObject getJavaScriptObject() {
		return handler;
	}
    
}
