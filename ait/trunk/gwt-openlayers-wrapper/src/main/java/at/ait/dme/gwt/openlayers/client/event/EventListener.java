package at.ait.dme.gwt.openlayers.client.event;

import at.ait.dme.gwt.openlayers.client.LonLat;
import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.Pixel;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class EventListener {
	
    public abstract void onEvent(LonLat lonlat, Pixel xy, String tagname);
    
    /**
     * The JavaScript handler function
     */
    private JavaScriptObject handler = null;
    
    public JavaScriptObject getHandlerFunction(Map map) {
    	if (handler == null) handler = _createHandlerFunction(this, map);
    	return handler;
    }
    
    private final native JavaScriptObject _createHandlerFunction(EventListener listener, Map map) /*-{
		return function(e) {
			var lonlat = null;
			if (e.xy) lonlat = map.getLonLatFromViewPortPx(e.xy);
			
			var id = null;
			if (e.srcElement) id = e.srcElement.id;
			if (e.target) id = e.target.id;
			listener.@at.ait.dme.gwt.openlayers.client.event.EventListener::onEvent(Lat/ait/dme/gwt/openlayers/client/LonLat;Lat/ait/dme/gwt/openlayers/client/Pixel;Ljava/lang/String;)(lonlat, e.xy, id);			
		}
    }-*/;
    

	
}
