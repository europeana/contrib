package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class ClickListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = ClickListenerImpl.createHandler(this);

    public abstract void onHandle(int x, int y, float lon, float lat);
	
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
