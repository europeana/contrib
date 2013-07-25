package at.ait.dme.gwt.openlayers.client.event;

import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;

import com.google.gwt.core.client.JavaScriptObject;

@Deprecated 
public abstract class VectorFeatureSelectedListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = VectorFeatureSelectedListenerImpl.createHandler(this);

    public abstract void onFeatureSelected(VectorFeature feature);
    
    void _onFeatureSelected(JavaScriptObject feature) {
    	onFeatureSelected((VectorFeature) feature);
    }
	
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
