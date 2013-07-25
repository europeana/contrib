package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public class LayerEventListenerImpl {

	public native static JavaScriptObject createHandler(LayerEventListener listener) /*-{
		return function(event) {
			listener.@at.ait.dme.gwt.openlayers.client.event.LayerEventListener::onEvent(Lat/ait/dme/gwt/openlayers/client/geometry/VectorFeature;)(event.feature);
		}
	}-*/;
	
}
