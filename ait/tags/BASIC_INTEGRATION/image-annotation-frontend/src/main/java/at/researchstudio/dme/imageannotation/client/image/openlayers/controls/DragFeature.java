package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import at.researchstudio.dme.imageannotation.client.image.openlayers.event.DragCompleteListener;
import at.researchstudio.dme.imageannotation.client.image.openlayers.layers.Vector;

import com.google.gwt.core.client.JavaScriptObject;

public class DragFeature extends JavaScriptObject {
	
	protected DragFeature() {}
	
	public static DragFeature create(Vector layer, DragCompleteListener listener) {
		return create(layer, listener.getJavaScriptObject());
	}
	
	private static native DragFeature create(JavaScriptObject layer, JavaScriptObject listener) /*-{
		return new $wnd.OpenLayers.Control.DragFeature(layer, { onComplete: listener });
	}-*/;
	
	public native final void activate() /*-{
		this.activate();
	}-*/;	

}
