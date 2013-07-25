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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import at.ait.dme.yuma.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.client.map.annotation.XYCoordinate;

/**
 * Abstract base class for transformations that convert between two
 * cartesian coordinate systems based on Control Points.
 *
 * @author Rainer Simon
 */
public abstract class CoordinateTransformation {
	
	/**
	 * Control points
	 */
	protected List<ControlPoint> controlPoints = null;
	
	public CoordinateTransformation(List<ControlPoint> controlPoints) {
		this.controlPoints = controlPoints;
	}
	
	/**
	 * Computes the N nearest-neighbor control points to the specified coordinate.
	 * @param c the coordinate (WGS84 coordinate space)
	 * @param n the number of neighbors
	 * @return the N nearest-neighbor control points
	 * @throws TransformationException if the dataset contains less than N control points
	 */
	protected List<ControlPoint> getNNearestNeighbours(WGS84Coordinate c, int n) throws TransformationException {
		if (controlPoints.size() < n)
			throw new TransformationException("Less than " + n + " control points in data set");
		
		// Compute distance measure for all control points in data set (this is NOT real-world distance!)
		ArrayList<DistanceMetric> dMetrics = new ArrayList<DistanceMetric>();
		for (ControlPoint cp : controlPoints) {
			dMetrics.add(new DistanceMetric(cp, WGS84Utils.arithmeticDistanceSquared(c, cp.getLatLon())));
		}
		
		// Sort results 
		Collections.sort(dMetrics);
	
		// Return top n control points for interpolation
		ArrayList<ControlPoint> result = new ArrayList<ControlPoint>();
		for (int i=0; i<n; i++) {
			result.add(dMetrics.get(i).cp);
		}
		return result;
	}
	
	/**
	 * Computes the N nearest-neighbor control points to the specified
	 * map image coordinates.
	 * @param xy the coordinate (map image space)
	 * @param n the number of neighbors
	 * @return the N nearest-neighbor control points
	 * @throws TransformationException if the dataset contains less than N control points
	 */
	protected List<ControlPoint> getNNearestNeighbours(XYCoordinate xy, int n) throws TransformationException {
		if (controlPoints.size() < n)
			throw new TransformationException("Less than " + n + " control points in data set");
		
		// Compute euclidian distance (squared) for all control points in data set
		ArrayList<DistanceMetric> dMetrics = new ArrayList<DistanceMetric>();
		for (ControlPoint cp : controlPoints) {
			double d = Math.pow(cp.getXY().x - xy.x, 2) + Math.pow(cp.getXY().y - xy.y, 2);
			dMetrics.add(new DistanceMetric(cp, d));
		}
		
		// Sort results 
		Collections.sort(dMetrics);
	
		// Return top n control points for interpolation
		ArrayList<ControlPoint> result = new ArrayList<ControlPoint>();
		for (int i=0; i<n; i++) {
			result.add(dMetrics.get(i).cp);
		}
		return result;
	}
	
	public abstract TransformationResult getXYFromKnownLatLon(WGS84Coordinate coordinate) throws TransformationException;
	
	public abstract TransformationResult getLatLonFromKnownXY(XYCoordinate xy) throws TransformationException;
	
	/**
	 * Simple wrapper class that wraps control
	 * point and distance metric, and which
	 * implements the comparable interface.
	 * Needed for quick sorting when searching
	 * for nearest neighbors.
	 * @author SimonR
	 *
	 */
	private class DistanceMetric implements Comparable<DistanceMetric> {
		
		ControlPoint cp;
		
		double dMetric;
		
		DistanceMetric(ControlPoint cp, double dMetric) {
			this.cp = cp;
			this.dMetric = dMetric;
		}
		
		public int compareTo(DistanceMetric arg) {
			if (this.dMetric > arg.dMetric)
				return 1;
			
			if (this.dMetric < arg.dMetric)
				return -1;
			
			return 0;
		}
		
	}

}
