package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorFeatureSelectedListenerImpl {

    @SuppressWarnings("deprecation")
	public native static JavaScriptObject createHandler(VectorFeatureSelectedListener self) /*-{
		return function(event) {
			self.@at.ait.dme.gwt.openlayers.client.event.VectorFeatureSelectedListener::_onFeatureSelected(Lcom/google/gwt/core/client/JavaScriptObject;)(event.feature);
		}
	}-*/;
	
}
