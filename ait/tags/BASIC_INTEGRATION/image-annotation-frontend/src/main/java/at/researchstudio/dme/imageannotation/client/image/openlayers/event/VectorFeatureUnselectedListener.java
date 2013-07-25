package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.VectorFeature;

import com.google.gwt.core.client.JavaScriptObject;

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
