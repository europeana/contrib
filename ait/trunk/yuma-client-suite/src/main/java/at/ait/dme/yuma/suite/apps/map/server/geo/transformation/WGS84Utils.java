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

package at.ait.dme.yuma.suite.apps.map.server.geo.transformation;

import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;


/**
 * Some utility functions for handling WGS84 coordinates. 
 * 
 * @author Rainer Simon
 */
public class WGS84Utils {
	
	/**
	 * Computes the real world distance between the
	 * specified coordinate pair, based on
	 * the Haversine approximation formula.
	 * @param a the first coordinate
	 * @param b the second coordinate
	 * @return the real world distance in meters
	 */
	public static double distance(WGS84Coordinate a, WGS84Coordinate b) {
		return 0;
	}
	
	/**
	 * Computes the square of the arithmetic distance
	 * between the specified coordinate pair.
	 * 
	 * Note that this metric generally does not have much
	 * to do with real-world distance! It is simply a measure
	 * to quickly assess nearest neighbors for interpolation!
	 * 
	 * @param a the first coordinate
	 * @param b the second coordinate
	 * @return the squared arithmetic distance
	 */
	public static double arithmeticDistanceSquared(WGS84Coordinate a, WGS84Coordinate b) {
		return Math.pow(a.lat - b.lat, 2) + Math.pow(a.lon - b.lon, 2);
	}

}
