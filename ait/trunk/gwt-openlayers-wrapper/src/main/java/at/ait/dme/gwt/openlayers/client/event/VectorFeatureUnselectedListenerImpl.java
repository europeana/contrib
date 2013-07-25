package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorFeatureUnselectedListenerImpl {

    @SuppressWarnings("deprecation")
	public native static JavaScriptObject createHandler(VectorFeatureUnselectedListener self) /*-{
		return function(event) {
			self.@at.ait.dme.gwt.openlayers.client.event.VectorFeatureUnselectedListener::_onFeatureUnselected(Lcom/google/gwt/core/client/JavaScriptObject;)(event.feature);
		}
	}-*/;
	
}
