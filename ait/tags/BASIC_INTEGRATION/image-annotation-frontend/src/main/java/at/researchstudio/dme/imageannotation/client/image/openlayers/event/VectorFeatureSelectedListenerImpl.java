package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorFeatureSelectedListenerImpl {

    public native static JavaScriptObject createHandler(VectorFeatureSelectedListener self) /*-{
		return function(event) {
			self.@at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureSelectedListener::_onFeatureSelected(Lcom/google/gwt/core/client/JavaScriptObject;)(event.feature);
		}
	}-*/;
	
}
