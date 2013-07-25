package at.ait.dme.gwt.openlayers.client;

import com.google.gwt.core.client.JavaScriptObject;

public class StyleMap extends JavaScriptObject {
	
	protected StyleMap() { }
	
	public static StyleMap create(Style[] styles) {
		StringBuffer sb = new StringBuffer();
		sb.append("new $wnd.OpenLayers.StyleMap({");
		
		for (int i=0; i<styles.length; i++) {
			sb.append(styles[i].toString());
		}
		
		// Remove last ','
		sb.deleteCharAt(sb.length() - 1);
		
		// Close JS object
		sb.append("\n});");
		
		// Create JS object
		return create(sb.toString());	
	}
	
	private static native StyleMap create(String js) /*-{
		return eval(js);
	}-*/;

}
