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

import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.AffineTransformation;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.ControlPointManager;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.CoordinateTransformation;
import at.ait.dme.yuma.suite.apps.map.server.geo.transformation.TransformationResult;
import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;
import at.ait.dme.yuma.suite.apps.map.shared.server.FindPlaceResponse;
import at.ait.dme.yuma.suite.apps.map.shared.server.FindPlaceService;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.FindPlaceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for geo-coding/reverse geo-coding of place names.
 * 
 * TODO refactor/eliminate overlap with {@link GeocoderServiceImpl}
 *
 * @author Rainer Simon
 */
@SuppressWarnings("serial")
public class FindPlaceServiceImpl extends RemoteServiceServlet implements FindPlaceService {
	
	@Override
	public FindPlaceResponse findPlace(String mapUrl, String query) throws FindPlaceException {
		try {
			// Geocode query string
			GeocoderServiceImpl geocoder = new GeocoderServiceImpl();
			WGS84Coordinate latlon = geocoder.getCoordinate(query);
			
			// Interpolate latlon
			ControlPointManager cpm = 
				new ControlPointManager(getThreadLocalRequest(), getThreadLocalResponse(), mapUrl);
			CoordinateTransformation interpolator = new AffineTransformation(cpm.getControlPoints());
			TransformationResult result = interpolator.getXYFromKnownLatLon(latlon);
			return new FindPlaceResponse(query, result.xy, result.latlon, result.getNeighbors());
		} catch (Exception e) {
			throw new FindPlaceException(e);
		}
	}
	
	@Override
	public FindPlaceResponse findPlace(String mapUrl, WGS84Coordinate latlon) throws FindPlaceException {
		try {
			ControlPointManager cpm =
				new ControlPointManager(getThreadLocalRequest(), getThreadLocalResponse(), mapUrl);
			CoordinateTransformation interpolator = new AffineTransformation(cpm.getControlPoints());
			TransformationResult result = interpolator.getXYFromKnownLatLon(latlon);
			return new FindPlaceResponse("unknown", result.xy, result.latlon, result.getNeighbors());
		} catch (Exception e) {
			throw new FindPlaceException(e);
		}
	}

}
