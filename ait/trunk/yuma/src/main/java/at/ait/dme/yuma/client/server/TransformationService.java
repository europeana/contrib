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

package at.ait.dme.yuma.client.server;

import java.util.List;

import at.ait.dme.yuma.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.client.map.annotation.XYCoordinate;
import at.ait.dme.yuma.client.server.exception.TransformationException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * interface to the transformation service
 * 
 * @author Christian Sadilek
 */
@RemoteServiceRelativePath("transformation")
public interface TransformationService extends RemoteService {

	/**
	 * converts a list of pixel coordinates of a map to latlng coordinates
	 * 
	 * @param europeanaUri
	 * @param mapUrl
	 * @param xyCoord
	 * @return latlng coord
	 * @throws TransformationException
	 */
	List<WGS84Coordinate> transformCoordinates(String europeanaUri, String mapUrl, 
			List<XYCoordinate> xyCoords) 
		throws TransformationException;
}
