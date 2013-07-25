/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.suite.apps.image.core.shared.shape;


/**
 * represents a point/location on a map by its pixel coordinates and latitude/longitude.
 * 
 * @author Christian Sadilek
 */
public class GeoPoint extends Shape {
	private static final long serialVersionUID = 1468050282536498352L;
	
	private Point point = new Point();
	private double lat = 0.0;
	private double lng = 0.0;
	
	public GeoPoint() {
		super();
	}
	
	public GeoPoint(Shape shape) {
		super(shape);
	}
	
	public GeoPoint(GeoPoint shape) {
		super(shape);
		
		this.lat = shape.lat;
		this.lng = shape.lng;
		this.point = new Point(shape.point.getX(), shape.point.getY());
	}
	
	public GeoPoint(int x, int y, double lat, double lng) {
		this(x, y, 1, 1, new Point(x,y), lat, lng);
	}
	
	public GeoPoint(Point point, double lat, double lng) {
		this(point.getX(), point.getY(), 1, 1, point, lat, lng);
	}
	
	public GeoPoint(int left, int top, int width, int height, 
			Point point, double lat, double lng) {
		super(left, top, width, height, new Color(0,0,0), 0);
		this.point = point;
		this.lat = lat;
		this.lng = lng;
	}
	
	@Override
	public GeoPoint copy() {
		return new GeoPoint(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof GeoPoint)) return false;
		if(this==obj) return true;
				
		GeoPoint geoPoint = (GeoPoint)obj;
		if(!point.equals(geoPoint.point)) return false;
		if(lat != geoPoint.lat || lng != geoPoint.lng) return false;
	
		return super.equals(geoPoint);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Point getCoordinates() {
		return point;
	}
	
	public int getX() {
		return point.getX();
	}
	
	public int getY() {
		return point.getY();
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}
}
