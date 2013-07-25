package at.ait.dme.yuma.server.model.geo;

import java.util.ArrayList;
import java.util.List;

public class Linestring extends GeoLocation {

	private List<Point> points;
	
	public Linestring() {
		this.points = new ArrayList<Point>();
	}
	
	public Linestring(List<Point> points) {
		this.points = points;
	}
	
	public void addPoint(Point p) {
		points.add(p);
	}
	
	public List<Point> getPoints() {
		return points;
	}
	
	@Override
	public String toWKT() {
		StringBuffer sb = new StringBuffer();
		for (Point p : points) {
			sb.append(p.getX() + " " + p.getY() + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return WKT_LINESTRING.replace("@coords@", sb.toString());
	}

}
