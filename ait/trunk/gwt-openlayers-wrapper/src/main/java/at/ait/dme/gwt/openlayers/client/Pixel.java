package at.ait.dme.gwt.openlayers.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Pixel extends JavaScriptObject {

	protected Pixel() { }
	
	public static native Pixel create(int x, int y) /*-{
		return new $wnd.OpenLayers.Pixel(x, y);
	}-*/;
	
	public final native float getX() /*-{
		return this.x;
	}-*/;
	
	public final native float getY() /*-{
		return this.y;
	}-*/;
	
}
