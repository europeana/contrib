package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.VectorFeature;

import com.google.gwt.core.client.JavaScriptObject;

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
