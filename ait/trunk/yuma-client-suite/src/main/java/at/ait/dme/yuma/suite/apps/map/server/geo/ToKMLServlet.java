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

package at.ait.dme.yuma.suite.apps.map.server.geo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationServiceException;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.AffineTransformation;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.ControlPointManager;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.CoordinateTransformation;
import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;
import at.ait.dme.yuma.suite.apps.map.shared.geo.XYCoordinate;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TransformationException;

/**
 * Servlet for converting a set of shapes (in request query String) to KML.
 *
 * @author Rainer Simon
 * @author Christian Sadilek
 */
@SuppressWarnings("serial")
public class ToKMLServlet extends HttpServlet {
		
	private static final String KML_START = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" >\n" + 
		"<Document>\n" +
		"  <name>AIT Annotation Demo</name>\n" +
		"  <Style id=\"default\">\n" + 
		"    <LineStyle>\n" +
		"      <color>ff00C8ff</color>\n" +
		"      <width>2</width>\n" +
		"    </LineStyle>\n" +
		"    <PolyStyle>\n" +
		"      <color>9900C8ff</color>\n" +
		"    </PolyStyle>\n" +
		"  </Style>\n";
	
	private static final String KML_PLACEMARK_START =
		"  <Placemark>\n" +
		"    <name>@name@</name>\n" +
		"    <gx:balloonVisibility>1</gx:balloonVisibility>\n" +
		"    <styleUrl>#default</styleUrl>\n" +
		"    <description>@description@</description>\n" + 
		"    <Polygon>\n" +
		"      <tessellate>1</tessellate>\n" +
		"      <outerBoundaryIs>\n" + 
	    "        <LinearRing>\n" +		
		"          <coordinates>\n";
	
	private static final String KML_PLACEMARK_END =
		"          </coordinates>\n" +
		"        </LinearRing>\n" + 
		"      </outerBoundaryIs>\n" +
		"    </Polygon>\n" +
	    "  </Placemark>\n";
	
	private static final String KML_END =
		"</Document>\n" +
		"</kml>\n";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mapUrl = request.getParameter("url");
		String a = request.getParameter("a");
		
		StringBuffer kml = new StringBuffer();
		kml.append(KML_START);
		
		if (mapUrl != null) {
			// 1. Instantiate Interpolator for the specified map
			ControlPointManager cpm;
			try {
				cpm = new ControlPointManager(request, response, mapUrl);
			} catch (AnnotationServiceException ase) {
				response.sendError(500, "failed to load control points");
				return;
			}
			CoordinateTransformation interpolator = new AffineTransformation(cpm.getControlPoints());
			
			// 2. Parse shape coordinates and interpolate each one			
			if ((a != null) && (a.trim().length() > 0)) {
				String[] shapes = a.split(";");
				String thisTitle;
				String thisText;
				String thisShape;
				String[] points;
				String[] xy;
				WGS84Coordinate latlon;
				for (int i=0; i<shapes.length; i++) {
					thisTitle = shapes[i].substring(shapes[i].indexOf("@") + 1, shapes[i].lastIndexOf("@"));
					thisText = shapes[i].substring(shapes[i].lastIndexOf("@") + 1);
					thisShape = shapes[i].substring(0, shapes[i].indexOf("@"));
					thisShape = thisShape.substring(thisShape.lastIndexOf("(") + 1, thisShape.indexOf(")"));
					points = thisShape.split(",");
					
					kml.append(KML_PLACEMARK_START.replace("@name@", thisTitle).replace("@description@", thisText));
					for (int j=0; j<points.length; j++) {
						xy = points[j].split(" ");
						if (xy.length == 2) {
							try {
								float x = Float.parseFloat(xy[0]);
								float y = Float.parseFloat(xy[1]);
								latlon = interpolator.getLatLonFromKnownXY(new XYCoordinate((int) x, (int) y)).latlon;
								kml.append(latlon.lon + "," + latlon.lat + " ");
							} catch (NumberFormatException e) {
								// Bad luck -> ignore this point
							} catch (TransformationException e) {
								// Bad luck -> ignore this point
							}
						}
					}
					kml.append(KML_PLACEMARK_END);
				}
			}
		}		

		kml.append(KML_END);
		
		response.setContentType("application/vnd.google-earth.kml+xml");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(kml.toString());
	}
}
