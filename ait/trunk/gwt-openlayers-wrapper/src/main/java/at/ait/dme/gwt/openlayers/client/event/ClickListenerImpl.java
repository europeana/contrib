package at.ait.dme.gwt.openlayers.client.event;

import com.google.gwt.core.client.JavaScriptObject;

class ClickListenerImpl {

    public native static JavaScriptObject createHandler(ClickListener self) /*-{
		return function(e) {
			var lonlat = this.map.getLonLatFromViewPortPx(e.xy);
			self.@at.ait.dme.gwt.openlayers.client.event.ClickListener::onHandle(IIFF)(e.xy.x, e.xy.y, lonlat.lon, lonlat.lat);
		}
	}-*/;

}