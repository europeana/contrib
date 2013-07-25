package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import com.google.gwt.core.client.JavaScriptObject;

public class VectorFeatureUnselectedListenerImpl {

    public native static JavaScriptObject createHandler(VectorFeatureUnselectedListener self) /*-{
		return function(event) {
			self.@at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureUnselectedListener::_onFeatureUnselected(Lcom/google/gwt/core/client/JavaScriptObject;)(event.feature);
		}
	}-*/;
	
}
