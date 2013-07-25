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

package at.ait.dme.yuma.suite.apps.map.shared.server;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import at.ait.dme.yuma.suite.apps.map.shared.Tileset;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TilesetNotAvailableException;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TilingException;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.UnsupportedTileSchemeException;

/**
 * Interface to the tileset provider service.
 *
 * @author Christian Sadilek
 * @author Rainer Simon
 */
@RemoteServiceRelativePath("../tileset")
public interface TilesetService extends RemoteService {

	/**
	 * Returns the tileset for the specified tileset or image URL.
	 * @param url the URL
	 * @return the tileset
	 * @throws TilesetNotAvailableException if the URL does not point to a supported
	 * tileset or (in case it's an image URL) there is no cached tileset for the image
	 */
	public Tileset getTileset(String url)
		throws TilesetNotAvailableException, UnsupportedTileSchemeException;
		
	/**
	 * Starts on-the-fly tileset generation. This method returns immediately after 
	 * the tiler task was started. use <code>pollForTileset()<code> for result polling.
	 * @param imageUrl the image url
	 * @throws TilingException in case of a problem during tileset generation
	 */
	public void startOnTheFlyTiler(String imageUrl)
		throws TilingException;	

	/**
	 * Polls for the result of an on-the-fly tileset generation task.
	 * @param imageUrl the image URL
	 * @return the tileset, if task has completed or null, if not
	 * @throws TilingException in case of a problem during tileset generation 
	 */
	public Tileset pollOnTheFlyTiler(String imageUrl)
		throws TilingException;

}
