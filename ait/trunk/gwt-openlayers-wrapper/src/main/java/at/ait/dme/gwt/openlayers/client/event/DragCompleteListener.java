package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class DragCompleteListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = DragCompleteListenerImpl.createHandler(this);

    public abstract void onHandle(JavaScriptObject feature, JavaScriptObject pixel);
	
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
