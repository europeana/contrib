package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import com.google.gwt.core.client.JavaScriptObject;

class DragCompleteListenerImpl {

    public native static JavaScriptObject createHandler(DragCompleteListener self) /*-{
		return function(feature, pixel) {
			self.@at.researchstudio.dme.imageannotation.client.image.openlayers.event.DragCompleteListener::onHandle(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(feature.geometry, pixel);
		}
	}-*/;

}