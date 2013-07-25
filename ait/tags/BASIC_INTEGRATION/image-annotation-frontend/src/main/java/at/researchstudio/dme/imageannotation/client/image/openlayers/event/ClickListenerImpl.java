package at.researchstudio.dme.imageannotation.client.image.openlayers.event;

import com.google.gwt.core.client.JavaScriptObject;

class ClickListenerImpl {

    public native static JavaScriptObject createHandler(ClickListener self) /*-{
		return function(e) {
			var lonlat = this.map.getLonLatFromViewPortPx(e.xy);
			self.@at.researchstudio.dme.imageannotation.client.image.openlayers.event.ClickListener::onHandle(FF)(lonlat.lon, lonlat.lat);
		}
	}-*/;

}