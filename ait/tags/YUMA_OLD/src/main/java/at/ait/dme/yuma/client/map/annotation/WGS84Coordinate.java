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

package at.ait.dme.yuma.client.map.annotation;

import java.io.Serializable;

/**
 * Simple wrapper class for a WGS84 (aka "GPS") coordinate.
 * 
 * @author Rainer Simon
 *
 */
public class WGS84Coordinate implements Serializable {
	private static final long serialVersionUID = 4374126089823177608L;

	/**
	 * Latitude
	 */
	public double lat;
	
	/**
	 * Longitude
	 */
	public double lon;
	
	public WGS84Coordinate() {
		// Required for GWT serialization
	}
	
	public WGS84Coordinate(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + new Double(lat).toString().hashCode();
		result = prime * result + new Double(lon).toString().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		WGS84Coordinate other = (WGS84Coordinate) obj;
		if (lat != other.lat) 
			return false;
		if (lon != other.lon) 
			return false;
		return true;
	}
}
