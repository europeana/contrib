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

package at.ait.dme.yuma.server.map.transformation;

import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.shape.GeoPoint;
import at.ait.dme.yuma.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.client.map.annotation.XYCoordinate;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Simple class that represents a control point.
 * 
 * @author Rainer Simon
 * @author Christian Sadilek
 *
 */
public class ControlPoint implements IsSerializable {
	
	/**
	 * Text name for this control point
	 */
	private String name;
	
	/**
	 * The map image coordinates
	 */
	private XYCoordinate xy;
	
	/**
	 * The geographical coordinates (lat/lon)
	 */
	private WGS84Coordinate latlon;
	
	public ControlPoint() {
	}
	
	public ControlPoint(String name, XYCoordinate xy, WGS84Coordinate latlon) {
		this.name = name;
		this.xy = xy;
		this.latlon = latlon;
	}
	
	public String getName() {
		return name;
	}
	
	public XYCoordinate getXY() {
		return xy;
	}
	
	public WGS84Coordinate getLatLon() {
		return latlon;
	}
	
	public String toString() {
		return name + ";" + xy.x + ";" + xy.y + ";" + latlon.lat + ";" + latlon.lon;
	}

	public static ControlPoint fromAnnotation(ImageAnnotation annotation) {
		if (annotation.getFragment() != null
				&& !(annotation.getFragment().getShape() instanceof GeoPoint))
			throw new IllegalArgumentException("annotation is not a control point");

		GeoPoint gp = (GeoPoint) annotation.getFragment().getShape();
		return new ControlPoint(gp.getName(), 
				new XYCoordinate(gp.getX(), gp.getY()),
				new WGS84Coordinate(gp.getLat(), gp.getLng()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((latlon == null) ? 0 : latlon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((xy == null) ? 0 : xy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		ControlPoint other = (ControlPoint) obj;
		if (latlon == null) {
			if (other.latlon != null)
				return false;
		} else if (!latlon.equals(other.latlon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (xy == null) {
			if (other.xy != null)
				return false;
		} else if (!xy.equals(other.xy))
			return false;
		return true;
	}
	
	
}
