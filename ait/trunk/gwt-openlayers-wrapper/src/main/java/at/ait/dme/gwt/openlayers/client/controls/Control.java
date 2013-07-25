package at.ait.dme.gwt.openlayers.client.controls;

import com.google.gwt.core.client.JavaScriptObject;

public class Control extends JavaScriptObject {

	protected Control() {}
	
	public native final void activate() /*-{
		this.activate();
	}-*/;
	
	public native final void deactivate() /*-{
		this.deactivate();
	}-*/;	
	
}
