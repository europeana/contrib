package at.ait.dme.gwt.openlayers.client.handler;

import com.google.gwt.core.client.JavaScriptObject;

public class Handler extends JavaScriptObject {
	
	protected Handler() { }

	private static final String PATH_CLASS = "$wnd.OpenLayers.Handler.Path";
	private static final String POINT_CLASS = "$wnd.OpenLayers.Handler.Point";
	private static final String POLYGON_CLASS = "$wnd.OpenLayers.Handler.Polygon";
	
	public static final Handler PATH = build(PATH_CLASS);
	public static final Handler POINT = build(POINT_CLASS);
	public static final Handler POLYGON = build(POLYGON_CLASS);
	
	private static native Handler build(String classname) /*-{
		return eval(classname);
	}-*/; 
	
}
