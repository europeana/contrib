package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

@Deprecated 
public abstract class VectorFeatureAddedListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = VectorFeatureAddedListenerImpl.createHandler(this);

    public abstract void onFeatureAdded(JavaScriptObject feature);
	
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
