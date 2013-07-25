package at.ait.dme.yuma.server.model.geo;

import java.util.ArrayList;
import java.util.List;

public class Polygon extends GeoLocation {

	private List<Point> points;
	
	public Polygon() {
		this.points = new ArrayList<Point>();
	}
	
	public Polygon(List<Point> points) {
		this.points = points;
	}
	
	public void addPoint(Point p) {
		points.add(p);
	}
		
	@Override
	public String toWKT() {
		close();
		StringBuffer sb = new StringBuffer();
		for (Point p : points) {
			sb.append(p.getX() + " " + p.getY() + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return WKT_POLYGON.replace("@coords@", sb.toString());
	}
	
	private void close() {
		if (points.size() > 0) {
			if (!points.get(0).equals(points.get(points.size() - 1))) {
				points.add(new Point(points.get(points.size() - 1)));
			}
		}
	}

}
