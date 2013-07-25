package at.ait.dme.yuma.server.model.geo;

public class Point extends GeoLocation {
	
	private double x, y;
	
	public Point(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	@Override
	public String toWKT() {
		return WKT_POINT
				.replace("@x@", Double.toString(x))
				.replace("@y@", Double.toString(y));
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Point))
			return false;
		
		Point p = (Point) other;
		if (p.getX() != this.getX())
			return false;
		
		if (p.getY() != this.getY())
			return false;
		
		return true;
	}
	

}
