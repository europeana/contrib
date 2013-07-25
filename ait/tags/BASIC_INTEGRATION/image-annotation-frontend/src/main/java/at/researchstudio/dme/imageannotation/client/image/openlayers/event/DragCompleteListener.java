package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class DragCompleteListener {

	/**
	 * JavaScript handler function
	 */
    private JavaScriptObject handler = DragCompleteListenerImpl.createHandler(this);

    public abstract void onHandle(JavaScriptObject geometry, JavaScriptObject pixel);
	
	public JavaScriptObject getJavaScriptObject() {
		return handler;
	}

}
