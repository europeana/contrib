package at.ait.dme.gwt.openlayers.client.event;

import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;

import com.google.gwt.core.client.JavaScriptObject;

@Deprecated 
public abstract class VectorFeatureUnselectedListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = VectorFeatureUnselectedListenerImpl.createHandler(this);

    public abstract void onFeatureUnselected(VectorFeature feature);
    
    void _onFeatureUnselected(JavaScriptObject feature) {
    	onFeatureUnselected((VectorFeature) feature);
    }
	
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
