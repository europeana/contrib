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
import java.util.List;

import at.ait.dme.yuma.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.client.map.annotation.XYCoordinate;
import at.ait.dme.yuma.client.server.TransformationService;
import at.ait.dme.yuma.client.server.exception.TransformationException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * the implementation of the transformation service
 * 
 * @author Christian Sadilek
 */
public class TransformationServiceImpl extends RemoteServiceServlet implements TransformationService {
	private static final long serialVersionUID = 1496807932649222393L;

	@Override
	public List<WGS84Coordinate> transformCoordinates(String europeanaUri, String mapUrl, List<XYCoordinate> xyCoords)
			throws TransformationException {
		
		try {
			List<WGS84Coordinate> coords = new ArrayList<WGS84Coordinate>();
			
			ControlPointManager cpm = new ControlPointManager(getThreadLocalRequest(), 
					getThreadLocalResponse(), 
					europeanaUri, 
					mapUrl);
			CoordinateTransformation interpolator = new AffineTransformation(cpm.getControlPoints());
			
			for(XYCoordinate xyCoord : xyCoords) {
				coords.add(interpolator.getLatLonFromKnownXY(xyCoord).latlon);					
			}
			return coords;
		} catch (Exception e) {
			throw new TransformationException(e);
		} 
	}
}