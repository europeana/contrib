package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

class DragCompleteListenerImpl {

    public native static JavaScriptObject createHandler(DragCompleteListener self) /*-{
		return function(feature, pixel) {
			self.@at.ait.dme.gwt.openlayers.client.event.DragCompleteListener::onHandle(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(feature, pixel);
		}
	}-*/;

}