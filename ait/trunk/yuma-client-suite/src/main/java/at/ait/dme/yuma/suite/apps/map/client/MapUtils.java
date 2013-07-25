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

package at.ait.dme.yuma.suite.apps.map.client;

import java.util.ArrayList;

import at.ait.dme.gwt.openlayers.client.geometry.LineString;
import at.ait.dme.gwt.openlayers.client.geometry.Point;
import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Color;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.GeoPoint;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Polygon;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Polyline;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Shape;

/**
 * Utility methods for converting between annotation shapes and OpenLayers
 * vector features.
 * 
 * TODO refactor/move this to a better place/rename!
 *
 * @author Rainer Simon
 */
public class MapUtils {
	
	public static VectorFeature shapeToVectorFeature(Shape shape) {
		if (shape instanceof Polyline) {
			// Linestring or Point
			ArrayList<Point> points = new ArrayList<Point>();
			for (at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point p :  ((Polyline) shape).getPoints()) {
				points.add(Point.create(p.getX() + shape.getLeft(), p.getY() + shape.getTop()));
			}
			
			if (points.size() == 1) {
				// Point
				return VectorFeature.create(points.get(0)); // , defaultStyle);
			} else {
				// Linestring
				return VectorFeature.create(LineString.create(points.toArray(new Point[points.size()]))); //, defaultStyle);
			}
		} else if (shape instanceof Polygon) {
			// Polygon
			ArrayList<Point> points = new ArrayList<Point>();			
			for (at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point p :  ((Polygon) shape).getPoints()) {
				points.add(Point.create(p.getX() + shape.getLeft(), p.getY() + shape.getTop()));
			}
			
			at.ait.dme.gwt.openlayers.client.geometry.Polygon poly = at.ait.dme.gwt.openlayers.client.geometry.Polygon.create(points.toArray(new Point[points.size()]));
			
			/*
			System.out.println("Created polygon vfeature:");
			for (Point p : points) {
				System.out.println(" " + p.getX() + "/" + p.getY());
			}
			*/
			return VectorFeature.create(poly); //, defaultStyle);
		} else if (shape instanceof GeoPoint) {
			// GeoPoint
			GeoPoint gp = (GeoPoint) shape;
			ArrayList<Point> points = new ArrayList<Point>();			
			points.add(Point.create(gp.getX(), gp.getY()));
			
			at.ait.dme.gwt.openlayers.client.geometry.Polygon poly = at.ait.dme.gwt.openlayers.client.geometry.Polygon.create(points.toArray(new Point[points.size()]));
			return VectorFeature.create(poly);
		}
		return null;
	}
	
	public static Shape vectorFeatureToShape(VectorFeature feature) {
		String js = feature.getGeometry().toString();
			
		if (js.startsWith("POLYGON")) {
			// Construct Polygon shape
			String[] s = js.substring(9, js.length() - 2).split(",");
			
			ArrayList<at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point> points = 
				new ArrayList<at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point>();

			int left = Integer.MAX_VALUE;
			int top = Integer.MAX_VALUE;
			for (int i=0; i<s.length; i++) {
				String[] xy = s[i].split(" ");
				if (xy.length == 2) {
					try {
						double x = Double.parseDouble(xy[0]);
						if (x < left)
							left = (int) x;
						
						double y = Double.parseDouble(xy[1]);
						if (y < top)
							top = (int) y;
						
						points.add(new at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point((int) x, (int) y));
					} catch (NumberFormatException e) {
						System.out.println("Error adding point: " + e.getMessage());
					}
				}
			}

			Polygon poly = new Polygon();
			poly.setColor(new Color("ff0000"));
			poly.setLeft(left);
			poly.setTop(top);
			for (at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point p : points) {	
				poly.addPoint(new at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point(p.getX() - left,p.getY() - top));
			}
			
			/*
			System.out.println("Created polygon shape: (" + poly.getLeft() + "/" + poly.getTop() +")");
			for (at.ait.dme.yuma.suite.core.client.image.shape.Point p : poly.getPoints()) {
				System.out.println(" " + p.getX() + "/" + p.getY());
			}
			*/
			return poly;
		} else if (js.startsWith("LINESTRING")) {
			// Construct Polyline shape
			String[] points = js.substring(11, js.length() - 1).split(",");

			Polyline pline = new Polyline();
			pline.setColor(new Color("ff0000"));
			for (int i=0; i<points.length; i++) {
				String[] xy = points[i].split(" ");
				if (xy.length == 2) {
					try {
						double x = Double.parseDouble(xy[0]);
						double y = Double.parseDouble(xy[1]);
						pline.addPoint(new at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point((int)x,(int)y));
					} catch (NumberFormatException e) {
						System.out.println("Error adding point: " + e.getMessage());
					}
				}
			}
			pline.setLeft(pline.getRelativeLeft());
			pline.setTop(pline.getRelativeTop());
			return pline;
		} else if (js.startsWith("POINT")) {
			// Points are no shapes -> workaround with a single-point-polyline
			String[] xy = js.substring(6, js.length() - 1).split(" ");
			if (xy.length == 2) {
				try {
					double x = Double.parseDouble(xy[0]);
					double y = Double.parseDouble(xy[1]);
					
					Polyline pline = new Polyline();
					pline.setColor(new Color("ff0000"));
					pline.addPoint(new at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point((int)x,(int)y));
					pline.setLeft(pline.getRelativeLeft());
					pline.setTop(pline.getRelativeTop());
					return pline;
				} catch (NumberFormatException e) {
					System.out.println("Error adding point: " + e.getMessage());
				}
			}
		}
		
		return null;
	}

}
