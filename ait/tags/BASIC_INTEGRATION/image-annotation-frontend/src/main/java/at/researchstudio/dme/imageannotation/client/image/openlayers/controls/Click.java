package at.researchstudio.dme.imageannotation.client.image.openlayers.controls;

import at.researchstudio.dme.imageannotation.client.image.openlayers.event.ClickListener;

import com.google.gwt.core.client.JavaScriptObject;

public class Click extends JavaScriptObject {
	
	protected Click() {}
	
	public static Click create(ClickListener listener) {
		return create(listener.getJavaScriptObject());
	}
	
	private static native Click create(JavaScriptObject listener) /*-{
		$wnd.OpenLayers.Control.Click = $wnd.OpenLayers.Class($wnd.OpenLayers.Control, {                
	        defaultHandlerOptions: {
	            'single': true,
	            'double': false,
	            'pixelTolerance': 0,
	            'stopSingle': false,
	            'stopDouble': false
	        },
	    
	        initialize: function(options) {
	            this.handlerOptions = $wnd.OpenLayers.Util.extend(
	                {}, this.defaultHandlerOptions
	            );
	            $wnd.OpenLayers.Control.prototype.initialize.apply(
	                this, arguments
	            ); 
	            this.handler = new $wnd.OpenLayers.Handler.Click(
	                this, {
	                    'click': this.trigger
	                }, this.handlerOptions
	            );
	        },
	       	trigger: listener
		});
		
		return new $wnd.OpenLayers.Control.Click();
	}-*/;
	
	public native final void activate() /*-{
		this.activate();
	}-*/;	

}
