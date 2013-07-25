package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.VectorFeature;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class VectorFeatureAddedListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = VectorFeatureAddedListenerImpl.createHandler(this);

    public abstract void onFeatureAdded(VectorFeature feature);
	
    void _onFeatureAdded(JavaScriptObject feature) {
    	onFeatureAdded((VectorFeature) feature);
    }
    
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
