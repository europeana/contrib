package at.ait.dme.gwt.openlayers.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Bounds extends JavaScriptObject {

	protected Bounds() { }
	
	public static native Bounds create(float left, float bottom, float right, float top) /*-{
		return new $wnd.OpenLayers.Bounds(left, bottom, right, top);
	}-*/;
	
	public final native float getLeft() /*-{
		return this.left; 
	}-*/;
	
	public final native float getBottom() /*-{
		return this.bottom; 
	}-*/;
	
	public final native float getRight() /*-{
		return this.right; 
	}-*/;
	
	public final native float getTop() /*-{
		return this.top; 
	}-*/;
	
}
