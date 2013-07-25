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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.AffineTransformation;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.ControlPointManager;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.TransformationResult;
import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TransformationException;

/**
 * Servlet for converting KML files from geo- to map-coordinates.
 *
 * @author Rainer Simon
 */
@SuppressWarnings("serial")
public class KMLConverterServlet extends HttpServlet {
	
	/**
	 * Outlier threshold in pixel
	 */
	private int outlierThreshold = 1000;
	 
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Params: URL of the base map into which KML shall be transformed, URL of the KML  
		String mapUrl = request.getParameter("map");
		String kmlUrl = request.getParameter("kml");
		
		if ((mapUrl != null) && (kmlUrl != null)) {
			// Get KML source file via HTTP
			GetMethod kmlRequest = new GetMethod(kmlUrl);
			int responseCode = new HttpClient().executeMethod(kmlRequest);	
			if (responseCode == 200) {
				try {
					// Parse KML DOM
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			        DocumentBuilder builder = factory.newDocumentBuilder();
			        Document kml = builder.parse(kmlRequest.getResponseBodyAsStream());
			        
					// Instantiate Interpolator
					ControlPointManager cpm = 
						new ControlPointManager(request, response, mapUrl);
					AffineTransformation ip = new AffineTransformation(cpm.getControlPoints());	
			        
			        // Convert coordinates in DOM
					kml = convert(kml, ip);
					
			        // Serialize KML result DOM and return
			        Transformer tf = TransformerFactory.newInstance().newTransformer();
			        Source source = new DOMSource(kml);
			        
			        Result result = new StreamResult(response.getOutputStream());
			        tf.transform(source, result);
				} catch (Exception e) {
					response.sendError(500, e.getMessage());
					return;
				}
			} else {
				response.sendError(500, "KML not found. (Server returned error " + responseCode + ")");
				return;
			}
			
			response.setContentType("application/vnd.google-earth.kml+xml");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("done");
		} else {
			response.sendError(404);
		}
	}
	
	private Document convert(Document kml, AffineTransformation interpolator) {
		// Get all 'coordinates' nodes
		NodeList coordNodes = kml.getElementsByTagName("coordinates");
		
		StringBuffer sb;
		String[] coords;
		String[] lonlat;
		TransformationResult ir;
		for (int i=0; i<coordNodes.getLength(); i++) {
			// Construct result coordinate string
			sb = new StringBuffer();
			
			// Original WGS84 coordinate set: 'lon,lat,alt lon,lat,alt lon,lat,alt ...'
			Node n = coordNodes.item(i);
			
			// Split into individual WGS84 coordinates: 'lon,lat,alt'
			coords = n.getTextContent().trim().split(" ");
			
			int lastX = 0;
			int lastY = 0;
			double dist = 0;
			for (int j=0; j<coords.length; j++) {
				// Split into coordinate values
				lonlat = coords[j].split(",");
				if (lonlat.length > 1) {
					try {
						// Interpolate
						ir = interpolator.getXYFromKnownLatLon(new WGS84Coordinate(
							Double.parseDouble(lonlat[1]),
							Double.parseDouble(lonlat[0])
						));
						
						// Threshold check: does the distance between this point and 
						// the last one exceed the threshold? --> Outlier! Don't add
						dist = Math.sqrt(Math.pow((ir.xy.x - lastX), 2) +  Math.pow((ir.xy.y - lastY), 2));
						if ((j == 0) || (dist < outlierThreshold)) {
							sb.append(ir.xy.x + "," + ir.xy.y + "\n");
							lastX = ir.xy.x;
							lastY = ir.xy.y;
						} 
					} catch (TransformationException e) {
						// Cannot happen unless interpolator is invalid
						throw new RuntimeException(e);
					}
				}
			}
			
			// Replace node
			n.setTextContent(sb.toString());
		}
		return kml;
	}

}
