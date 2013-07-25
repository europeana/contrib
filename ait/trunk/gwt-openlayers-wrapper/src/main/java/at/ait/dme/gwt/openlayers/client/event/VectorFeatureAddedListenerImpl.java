package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorFeatureAddedListenerImpl {

    @SuppressWarnings("deprecation")
	public native static JavaScriptObject createHandler(VectorFeatureAddedListener self) /*-{
		return function(event) {
			self.@at.ait.dme.gwt.openlayers.client.event.VectorFeatureAddedListener::onFeatureAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(event.feature);
		}
	}-*/;
	
}
