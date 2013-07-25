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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.ait.dme.yuma.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.client.map.annotation.XYCoordinate;

/**
 * Implements an affine transformation that converts between two cartesion
 * coordinates (map image coordinates and geo-coordinates) based on 
 * Control Points. 
 *
 * @author Rainer Simon
 */
public class AffineTransformation extends CoordinateTransformation {
	
	public AffineTransformation(List<ControlPoint> controlPoints) {
		super(controlPoints);
	}
	
	/**
	 * LAT/LON and X/Y are assumed to be related via an affine transform.
	 * An affine transform takes the form
	 * 
	 *   x = a * u + b * v + c
	 *   y = d * u + e * v + f
	 * 
	 * where x and y are the point's unknown X/Y coordinates (image space);
	 * and u and v are the point's known geographical coordinates (u = lat,
	 * v = lon). Coefficients a, b, c, d, e, f can be computed if three
	 * control points are known (in image space and geo-coordinate space).
	 * 
	 * The known coordinates of the three control points are denoted as
	 * 
	 * CP1: x1/y1 (image space) u1/v1 (lat/lon)
	 * CP2: x2/y2               u2/v2
	 * CP3: x3/y3               u3/v3 
	 * 
	 * @param c the coordinate with known lat/lon, but unknown x/y
	 * @return the point with x/y coordinates
	 * @throws TransformationException if something goes wrong
	 */
	public TransformationResult getXYFromKnownLatLon(WGS84Coordinate coordinate) throws TransformationException {
		return getXYFromKnownLatLon(coordinate, true);
	}
	
	public TransformationResult getXYFromKnownLatLon(WGS84Coordinate coordinate, boolean useMedianSmoothing) throws TransformationException {
		if (useMedianSmoothing) return getXYFromKnownLatLon_withMediaSmoothing(coordinate);
		
		// Get three nearest neighbour control points
		List<ControlPoint> nn = getNNearestNeighbours(coordinate, 3);
		
		// Compute and return
		return new TransformationResult(
				computeXYFromKnownLatLon(coordinate, new ControlPoint[] {nn.get(0), nn.get(1), nn.get(2)}),
				coordinate,
				nn
		);
	}
	
	private TransformationResult getXYFromKnownLatLon_withMediaSmoothing(WGS84Coordinate coordinate) throws TransformationException {
		// Get four nearest neighbour control points
		List<ControlPoint> nn = getNNearestNeighbours(coordinate, 4);
		
		// Compute four times
		XYCoordinate[] results = new XYCoordinate[] {
				computeXYFromKnownLatLon(coordinate, new ControlPoint[] {nn.get(0), nn.get(1), nn.get(2)}),
				computeXYFromKnownLatLon(coordinate, new ControlPoint[] {nn.get(1), nn.get(2), nn.get(3)}),
				computeXYFromKnownLatLon(coordinate, new ControlPoint[] {nn.get(2), nn.get(3), nn.get(0)}),
				computeXYFromKnownLatLon(coordinate, new ControlPoint[] {nn.get(3), nn.get(0), nn.get(1)})
		};
		
		// Compute Median by sorting X and Y individually and average two middle values
		ArrayList<Float> medianX = new ArrayList<Float>();
		ArrayList<Float> medianY = new ArrayList<Float>();		
		for (int i=0; i<4; i++) {
			medianX.add(new Float(results[i].x));
			medianY.add(new Float(results[i].y));
		}
		Collections.sort(medianX);
		Collections.sort(medianY);
		
		// Compute and return
		return new TransformationResult(
				new XYCoordinate(
						(int) ((medianX.get(1) + medianX.get(2)) / 2),
						(int) ((medianY.get(1) + medianY.get(2)) / 2)
				),
				coordinate,
				nn
		);
	}
	
	public TransformationResult getLatLonFromKnownXY(XYCoordinate xy) throws TransformationException {
		return getLatLonFromKnownXY(xy, true);
	}
	
	private TransformationResult getLatLonFromKnownXY(XYCoordinate xy, boolean useMedianSmoothing) throws TransformationException {
		if (useMedianSmoothing) return getLatLonFromKnownXY_withMedianSmoothing(xy);
		
		// Get three nearest neighbour control points
		List<ControlPoint> nn = getNNearestNeighbours(xy, 3);

		// Compute and return
		return new TransformationResult(
				xy,
				computeLatLonFromKnownXY(xy, new ControlPoint[] {nn.get(0), nn.get(1), nn.get(2)}),
				nn
		);
	}
	
	public TransformationResult getLatLonFromKnownXY_withMedianSmoothing(XYCoordinate xy) throws TransformationException {
		// Get four nearest neighbour control points
		List<ControlPoint> nn = getNNearestNeighbours(xy, 4);
		
		// Compute four times
		WGS84Coordinate[] results = new WGS84Coordinate[] {
			computeLatLonFromKnownXY(xy, new ControlPoint[] {nn.get(0), nn.get(1), nn.get(2)}),
			computeLatLonFromKnownXY(xy, new ControlPoint[] {nn.get(1), nn.get(2), nn.get(3)}),
			computeLatLonFromKnownXY(xy, new ControlPoint[] {nn.get(2), nn.get(3), nn.get(0)}),
			computeLatLonFromKnownXY(xy, new ControlPoint[] {nn.get(3), nn.get(0), nn.get(1)})
		};
		
		// Compute Median by sorting Lat and Lon individually and average two middle values
		ArrayList<Float> medianLat = new ArrayList<Float>();
		ArrayList<Float> medianLon = new ArrayList<Float>();		
		for (int i=0; i<4; i++) {
			medianLat.add(new Float(results[i].lat));
			medianLon.add(new Float(results[i].lon));
		}
		Collections.sort(medianLat);
		Collections.sort(medianLon);
		
		// Compute and return
		return new TransformationResult(
				xy,
				new WGS84Coordinate(
						(medianLat.get(1) + medianLat.get(2)) / 2,
						(medianLon.get(1) + medianLon.get(2)) / 2
				),
				nn
		);
	}


	private XYCoordinate computeXYFromKnownLatLon(WGS84Coordinate coordinate, ControlPoint[] cp) throws TransformationException {
		if (cp.length != 3)
			throw new TransformationException("Computation not possible with " + cp.length + " control points!");

		// Compute matrix coefficients
		double x1 = cp[0].getXY().x;
		double y1 = cp[0].getXY().y;
		double x2 = cp[1].getXY().x;
		double y2 = cp[1].getXY().y;
		double x3 = cp[2].getXY().x;
		double y3 = cp[2].getXY().y;
		
		double u1 = cp[0].getLatLon().lat; 
		double v1 = cp[0].getLatLon().lon;
		double u2 = cp[1].getLatLon().lat;
		double v2 = cp[1].getLatLon().lon;
		double u3 = cp[2].getLatLon().lat;
		double v3 = cp[2].getLatLon().lon;
		
		double a = (x2 * (v3 - v1) - x3 * (v2 - v1) - x1 * (v3 - v2))/((u2 - u1) * (v3 - v1) - (u3 - u1) * (v2 - v1));
		double b = (x2 * (u3 - u1) - x3 * (u2 - u1) - x1 * (u3 - u2))/((v2 - v1) * (u3 - u1) - (v3 - v1) * (u2 - u1));
		double c = x1 - a * u1 - b * v1;
		
		double d = (y2 * (v3 - v1) - y3 * (v2 - v1) - y1 * (v3 - v2))/((u2 - u1) * (v3 - v1) - (u3 - u1) * (v2 - v1));
		double e = (y2 * (u3 - u1) - y3 * (u2 - u1) - y1 * (u3 - u2))/((v2 - v1) * (u3 - u1) - (v3 - v1) * (u2 - u1));
		double f = y1 - d * u1 - e * v1;
		
		// Compute transform
		double x = a * coordinate.lat + b * coordinate.lon + c;
		double y = d * coordinate.lat + e * coordinate.lon + f;
		
		return new XYCoordinate((int) x, (int) y);
	}
	
	private WGS84Coordinate computeLatLonFromKnownXY(XYCoordinate xy, ControlPoint[] cp) throws TransformationException {
		if (cp.length != 3)
			throw new TransformationException("Computation not possible with " + cp.length + " control points!");
		
		// Compute matrix coefficients
		double x1 = cp[0].getXY().x;
		double y1 = cp[0].getXY().y;
		double x2 = cp[1].getXY().x;
		double y2 = cp[1].getXY().y;
		double x3 = cp[2].getXY().x;
		double y3 = cp[2].getXY().y;
		
		double u1 = cp[0].getLatLon().lat; 
		double v1 = cp[0].getLatLon().lon;
		double u2 = cp[1].getLatLon().lat;
		double v2 = cp[1].getLatLon().lon;
		double u3 = cp[2].getLatLon().lat;
		double v3 = cp[2].getLatLon().lon;
		
		double a = (u2 * (y3 - y1) - u3 * (y2 - y1) - u1 * (y3 - y2))/((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));		
		double b = (u2 * (x3 - x1) - u3 * (x2 - x1) - u1 * (x3 - x2))/((y2 - y1) * (x3 - x1) - (y3 - y1) * (x2 - x1));
		double c = u1 - a * x1 - b * y1;
		
		double d = (v2 * (y3 - y1) - v3 * (y2 - y1) - v1 * (y3 - y2))/((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
		double e = (v2 * (x3 - x1) - v3 * (x2 - x1) - v1 * (x3 - x2))/((y2 - y1) * (x3 - x1) - (y3 - y1) * (x2 - x1));			
		double f = v1 - d * x1 - e * y1;
		
		// Compute transform
		double u = a * xy.x + b * xy.y + c;
		double v = d * xy.x + e * xy.y + f;
		
		return new WGS84Coordinate(u, v);		
	}


}
