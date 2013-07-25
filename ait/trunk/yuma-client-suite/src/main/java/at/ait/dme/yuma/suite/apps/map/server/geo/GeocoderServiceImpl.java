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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import at.ait.dme.yuma.suite.apps.core.server.Config;
import at.ait.dme.yuma.suite.apps.core.shared.model.PlainLiteral;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.AffineTransformation;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.ControlPointManager;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.CoordinateTransformation;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.TransformationResult;
import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;
import at.ait.dme.yuma.suite.apps.map.shared.geo.XYCoordinate;
import at.ait.dme.yuma.suite.apps.map.shared.server.GeocoderService;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.GeocoderException;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TransformationException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for various geo-coding tasks such as: resolve string to
 * geo-coordinates (geo-coding); resolve map coordinates to place name
 * (reverse geo-coding); get Semantic Tags for a map region.
 *
 * @author Rainer Simon
 */
@SuppressWarnings("serial")
public class GeocoderServiceImpl extends RemoteServiceServlet  implements GeocoderService {	

	/**
	 * Properties
	 */
	private static final String GOOGLE_API_KEY_PROPERTY = "gmaps.api.key";
	
	/**
	 * Google API key
	 */
	private String googleApiKey;
	
	/**
	 * Request URL parts
	 */
	private static final String GEOCODER_URL_PT1 = "http://maps.google.com/maps/geo?q=";
	private static final String GEOCODER_URL_PT2 = "&output=xml&key=";
	private static final String REV_GEOCODER_URL_PT1 = "http://maps.google.com/maps/geo?ll=";
	private static final String REV_GEOCODER_URL_PT2 = "&output=xml&key=";
	
	private static final String GEONAMES_CITIES_QUERY = "http://ws.geonames.org/cities?";
	private static final String GEONAMES_SEARCH_QUERY = "http://ws.geonames.org/search?";
	private static final String GEONAMES_URI_TEMPLATE = "http://sws.geonames.org/@id@/";
	
	/**
	 * RegEx patterns
	 */
	private static final String REGEX_PATTERN_COORDINATE = "<coordinates>((.|\n)*?)</coordinates>";
	private static final String REGEX_PATTERN_COUNTRY = "<CountryName>((.|\n)*?)</CountryName>";
	private static final String REGEX_PATTERN_ADMINISTRATIVEAREA = "<AdministrativeAreaName>((.|\n)*?)</AdministrativeAreaName>";

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		Config config = new Config(servletConfig);
		googleApiKey = config.getStringProperty(GOOGLE_API_KEY_PROPERTY);
	}
	
	@Override
	public WGS84Coordinate getCoordinate(String query) throws GeocoderException {
		try {
			URL url = new URL(GEOCODER_URL_PT1 + URLEncoder.encode(query, "UTF-8") + GEOCODER_URL_PT2 + googleApiKey);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer s = new StringBuffer();
	        String line = null;
	        while ((line = br.readLine()) != null) {
	            s.append(line + "\n");
	        }
	        // System.out.println(s.toString());
			return parseGeocoderResponse(s);
		} catch (Exception e) {
			throw new GeocoderException(e);
		}
	}

	@Override
	public String getPlacename(String mapUrl, XYCoordinate c) throws GeocoderException {
		try {
			// Interpolate lat/lon
			ControlPointManager cpm =
				new ControlPointManager(getThreadLocalRequest(), getThreadLocalResponse(), mapUrl);
			CoordinateTransformation interpolator = new AffineTransformation(cpm.getControlPoints());
			TransformationResult ir = interpolator.getLatLonFromKnownXY(c);
			
			// Reverse-geocode via Google service
			URL url = new URL(REV_GEOCODER_URL_PT1 + ir.latlon.lat + "," + ir.latlon.lon + REV_GEOCODER_URL_PT2 + googleApiKey);		
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			StringBuffer s = new StringBuffer();
	        String line = null;
	        while ((line = br.readLine()) != null) {
	            s.append(line + "\n");
	        }
	        
			return parseReverseGeocoderResponse(s);		
		} catch (Exception e) {
			throw new GeocoderException(e);
		}
	}
	
	@Override
	public SemanticTag[] getTags(String mapUrl, XYCoordinate lowerLeft, XYCoordinate upperRight) throws GeocoderException, TransformationException {
		try {
			// Convert bounding box to geo-coordinates
			ControlPointManager cpm =
				new ControlPointManager(getThreadLocalRequest(), getThreadLocalResponse(), mapUrl);
			CoordinateTransformation interpolator = new AffineTransformation(cpm.getControlPoints());
			WGS84Coordinate ll = interpolator.getLatLonFromKnownXY(lowerLeft).latlon;
			WGS84Coordinate ur = interpolator.getLatLonFromKnownXY(upperRight).latlon;
			
			// Query Geonames 'cities' service
			URL url = new URL(GEONAMES_CITIES_QUERY + "north=" + ur.lat + "&south=" + ll.lat + "&east=" + ur.lon + "&west=" + ll.lon);
				
			// Parse response
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			StringBuffer s = new StringBuffer();
	        String line = null;
	        while ((line = br.readLine()) != null) {
	            s.append(line + "\n");
	        }

	        return parseGeonamesResponse(s);
		} catch (Throwable t) {
			if (t instanceof TransformationException)
				throw (TransformationException) t;
			
			throw new GeocoderException(t);
		}
	}
	
	private WGS84Coordinate parseGeocoderResponse(CharSequence xml) {
		WGS84Coordinate c = null;	
		
		Pattern coordinatePattern = Pattern.compile(REGEX_PATTERN_COORDINATE);
		Matcher m = coordinatePattern.matcher(xml);
		
		if (m.find()) {
			String coords[] = m.group(1).split(",");
			if (coords.length > 1) {
				try {
					c = new WGS84Coordinate(Double.parseDouble(coords[1].trim()), Double.parseDouble(coords[0].trim()));
				} catch (NumberFormatException e) {
					// Do nothing
				}
			}
		}
		
		return c;
	}
	
	private String parseReverseGeocoderResponse(CharSequence xml) {
		Pattern countryPattern = Pattern.compile(REGEX_PATTERN_COUNTRY);
		Pattern adminAreaPattern = Pattern.compile(REGEX_PATTERN_ADMINISTRATIVEAREA);
		
		String region = null;
		String country = null;
		
		// Country
		Matcher m = countryPattern.matcher(xml);
		if (m.find()) {
			country = m.group(1).trim();
		}

		// Administrative area
		m = adminAreaPattern.matcher(xml);
		if (m.find()) {
			region = m.group(1).trim();
		}
		
		if (country == null)
			return null;
		
		if (region == null)
			return country;
		
		if (region.length() > 3) {
			return region;
		} else {
			return country;
		}
	}
	
	private SemanticTag[] parseGeonamesResponse(CharSequence xml) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.toString().getBytes("UTF-8")));
        
        ArrayList<SemanticTag> tags = new ArrayList<SemanticTag>();
        NodeList nodes = doc.getElementsByTagName("geoname");

        NodeList children;
        ArrayList<String> countries = new ArrayList<String>();
        for (int i=0; i<nodes.getLength(); i++) {
            String toponymName = null;
            String geonameId = null;
            String country = null;
            String lat = null;
            String lon = null;
        	children = nodes.item(i).getChildNodes();
        	for (int j=0; j<children.getLength(); j++) {
        		Node child = children.item(j);
        		if (child.getNodeName().equals("toponymName")) {
        			toponymName = child.getTextContent();
        		} else if (child.getNodeName().equals("geonameId")) {
        			geonameId = child.getTextContent();
        		} else if (child.getNodeName().equals("countryName")) {
        			// Store country --> we'll retrieve their geonameIds later, too!
        			country = child.getTextContent();
        			if (!countries.contains(country)) countries.add(country);
        		} else if (child.getNodeName().equals("lat")) {
        			lat = child.getTextContent();
        		} else if (child.getNodeName().equals("lng")) {
        			lon = child.getTextContent();
        		}
        	}
        	if (toponymName != null && country != null && geonameId != null && lat != null && lon != null)
        		tags.add(new SemanticTag(toponymName, getAlternativePlacenames(toponymName), "Place", "en", toponymName + ", " + country + " (city) \nLatitude: " + lat + " \nLongitude: " + lon, GEONAMES_URI_TEMPLATE.replace("@id@", geonameId)));
        }
        
        for (SemanticTag countryTag : getCountries(countries)) {
        	tags.add(0, countryTag);
        }
		
		return tags.toArray(new SemanticTag[tags.size()]);
	}
	
	private List<SemanticTag> getCountries(List<String> countries) throws GeocoderException {
		try  {
			ArrayList<SemanticTag> tags = new ArrayList<SemanticTag>();
			for (String country : countries) {
				// Query Geonames 'search' service
				URL url = new URL(GEONAMES_SEARCH_QUERY + "q=" + URLEncoder.encode(country, "UTF-8"));
					
				// Parse response
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				StringBuffer s = new StringBuffer();
		        String line = null;
		        while ((line = br.readLine()) != null) {
		            s.append(line + "\n");
		        }
		        
		        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		        Document doc = builder.parse(new ByteArrayInputStream(s.toString().getBytes("UTF-8")));
		        NodeList nodes = doc.getElementsByTagName("geoname");

		        NodeList children;

		        if (nodes.getLength() > 0) {
		            String toponymName = null;
		            String geonameId = null;
		            children = nodes.item(0).getChildNodes();
		        	for (int j=0; j<children.getLength(); j++) {
		        		Node child = children.item(j);
		        		if (child.getNodeName().equals("toponymName")) {
		        			toponymName = child.getTextContent();
		        		} else if (child.getNodeName().equals("geonameId")) {
		        			geonameId = child.getTextContent();
		        		}
		        	}
		        	
		        	if (toponymName != null && geonameId != null)
		        		tags.add(new SemanticTag(country, getAlternativePlacenames(toponymName), "Place", "en", toponymName + " (country)", GEONAMES_URI_TEMPLATE.replace("@id@", geonameId)));
		        }
			}
			return tags;
		} catch (Exception e) {
			throw new GeocoderException(e);
		}
	}
	
	private ArrayList<PlainLiteral> getAlternativePlacenames(String placename) {		
        ArrayList<PlainLiteral> altLabels = new ArrayList<PlainLiteral>();

        try {
	        HttpClient client = new HttpClient();
	        HttpMethod get = new GetMethod("http://dbpedia.org/resource/" + URLEncoder.encode(placename, "UTF-8"));
	        get.setRequestHeader("Content-Type", "application/rdf+xml; charset=UTF-8");
	        get.setRequestHeader("Accept", "application/rdf+xml");
       
	        int statusCode = client.executeMethod(get);
	        if (statusCode == HttpStatus.SC_OK) {
	            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	            Document doc = builder.parse(
	            		new InputSource(new InputStreamReader(get.getResponseBodyAsStream(), "UTF-8"))
	            );
	            
	            NodeList labels = doc.getElementsByTagName("rdfs:label");
	            for (int i=0; i<labels.getLength(); i++) {
	            	altLabels.add(new PlainLiteral(labels.item(i).getTextContent()));
	            }
	        }
        } catch (Exception e) {
        	// Just ignore for now - doesn't have adverse effect on functionality
        }

		return altLabels;
	}
	
}
