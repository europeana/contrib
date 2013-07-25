package at.ait.dme.yuma.server.model.geo;

public abstract class GeoLocation {
	
	protected static final String WKT_LINESTRING = "LINESTRING(@coords@)";
	protected static final String WKT_POINT = "POINT(@x@ @y@)";
	protected static final String WKT_POLYGON = "POLYGON((@coords@))";
	
	public abstract String toWKT();
	
	public static GeoLocation fromWKT(String wkt) {
		// TODO implement!
		if (wkt.startsWith("POINT(")) {
			return new Point(0,0);
		} else if (wkt.startsWith("LINESTRING(")) {
			return new Linestring();
		} else if (wkt.startsWith("POLYGON((")) {
			return new Polygon(); 
		}
		return null;
	}

}
