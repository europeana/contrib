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

import java.util.ArrayList;
import java.util.List;

import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;
import at.ait.dme.yuma.suite.apps.map.shared.geo.XYCoordinate;


/**
 * Wraps the result of a control point interpolation.
 * 
 * @author Rainer Simon
 *
 */
public class TransformationResult {
	
	/**
	 * The map image coordinates (X/Y)
	 */
	public XYCoordinate xy;
	
	/**
	 * The geographical coordinates (lat/lon)
	 */
	public WGS84Coordinate latlon;
	
	/**
	 * Region name (city, area, etc.) or <code>null</code>
	 */
	public String placename;
	
	/**
	 * The neighbor control points used for the interpolation
	 */
	public List<ControlPoint> neighbors;

	public TransformationResult(XYCoordinate xy, WGS84Coordinate latlon, List<ControlPoint> neighbors) {
		this(xy, latlon, neighbors, null);
	}
	
	public TransformationResult(XYCoordinate xy, WGS84Coordinate latlon, List<ControlPoint> neighbors, String placename) {
		this.xy = xy;
		this.latlon = latlon;
		this.neighbors = neighbors;
		this.placename = placename;
	}

	public List<String> getNeighbors() {
		List<String> result = new ArrayList<String>();
		for(ControlPoint cp : neighbors) {
			result.add(cp.getName());
		}
		return result;
	}
}
