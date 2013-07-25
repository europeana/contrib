package at.ait.dme.gwt.openlayers.client.geometry;

public class Point extends Geometry {
	
	protected Point() {}
	
	public static native Point create(float x, float y) /*-{
		return new $wnd.OpenLayers.Geometry.Point(x, y);
	}-*/;
	
	public final native float getX() /*-{
		return this.x;
	}-*/;
	
	public final native float getY() /*-{
		return this.y;
	}-*/;
	
}
