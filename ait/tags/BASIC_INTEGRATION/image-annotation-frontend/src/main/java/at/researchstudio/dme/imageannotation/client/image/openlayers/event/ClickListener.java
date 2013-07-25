package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class ClickListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = ClickListenerImpl.createHandler(this);

    public abstract void onHandle(float lon, float lat);
	
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
