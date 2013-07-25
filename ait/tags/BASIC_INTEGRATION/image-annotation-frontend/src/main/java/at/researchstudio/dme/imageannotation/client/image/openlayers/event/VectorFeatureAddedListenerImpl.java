package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorFeatureAddedListenerImpl {

    public native static JavaScriptObject createHandler(VectorFeatureAddedListener self) /*-{
		return function(event) {
			self.@at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureAddedListener::_onFeatureAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(event.feature);
		}
	}-*/;
	
}
