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

import java.io.IOException;

import at.ait.dme.yuma.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.client.server.exception.FindPlaceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("findplace")
public interface FindPlaceService extends RemoteService {

	/**
	 * Find a place on the map by place name
	 * @param europeanaUri
	 * @param mapUrl the URL of the map image
	 * @param query the place name query
	 * @return the place information
	 * @throws IOException if anything goes wrong
	 */
	FindPlaceResponse findPlace(String europeanaUri, String mapUrl, String query) 
		throws FindPlaceException;
	
	/**
	 * Find a place on the map by geo-coordinates
	 * @param europeanaUri
	 * @param mapUrl the URL of the map image
	 * @param latlon the geo-coordinates
	 * @return the place information
	 * @throws IOException if anything goes wrong
	 */
	FindPlaceResponse findPlace(String europeanaUri, String mapUrl, WGS84Coordinate latlon) 
		throws FindPlaceException;
	
}
