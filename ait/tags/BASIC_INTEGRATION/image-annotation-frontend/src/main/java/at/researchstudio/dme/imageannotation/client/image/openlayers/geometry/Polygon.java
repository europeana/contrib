package at.researchstudio.dme.imageannotation.client.image.openlayers.geometry;

import java.util.ArrayList;
import java.util.List;

import at.researchstudio.dme.imageannotation.client.image.util.JSUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Polygon extends Geometry {
	
	protected Polygon() {}
	
	public static Polygon create(Point[] points) {
		return create(JSUtils.toJSArray(points));
	}
	
	private static native Polygon create(JsArray<JavaScriptObject> points) /*-{		
		var ring = new $wnd.OpenLayers.Geometry.LinearRing($wnd.AIT.relayArray(points));
		return new $wnd.OpenLayers.Geometry.Polygon(ring);
	}-*/;
	
	/**
	 * Returns the points of this Polygon.
	 * 
	 * WARNING! There seems to be something wrong with the Openlayers API
	 * definition here (or, rather, the OpenLayers version we have to use 
	 * because of Zoomify is probably outdated): The Polygon DOES NOT CONTAIN
	 * AN ARRAY OF LINEARRINGS named 'components'! Points must be parsed from
	 * the .toString() text of the JavaScriptObject directly!
	 * 
	 * @return the Points
	 */
	public final List<Point> getPoints() {
		String poly = this.toString();
		if (poly.startsWith("POLYGON")) {
			String[] points = poly.substring(9, poly.length() - 2).split(",");
			ArrayList<Point> result = new ArrayList<Point>();
			for (int i=0; i<points.length; i++) {
				String[] xy = points[i].split(" ");
				if (xy.length == 2) {
					try {
						float x = Float.parseFloat(xy[0]);
						float y = Float.parseFloat(xy[1]);
						result.add(Point.create(x, y));
					} catch (NumberFormatException e) {
						// Bad luck (but won't cause any exceptions later on)
					}
				}
			}
			return result;	
		} else {
			return new ArrayList<Point>();
		}
	}

}
