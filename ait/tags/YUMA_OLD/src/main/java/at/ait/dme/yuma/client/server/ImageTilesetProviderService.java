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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import at.ait.dme.yuma.client.map.Tileset;
import at.ait.dme.yuma.client.server.exception.TilesetGenerationException;
import at.ait.dme.yuma.client.server.exception.TilesetNotFoundException;

/**
 * interface to the image tiles provider service
 *
 * @author Christian Sadilek
 */
@RemoteServiceRelativePath("tileset")
public interface ImageTilesetProviderService extends RemoteService {

	/**
	 * retrieves the tileset for the given image
	 * 
	 * @param imageUrl
	 * @return tile set
	 * @throws TilesetGenerationException in case of a problem with the existing tileset
	 * @throws TilesNotFoundException in case tiles have not yet been generated
	 */
	public Tileset retrieveTileset(String imageUrl) throws TilesetNotFoundException, 
		TilesetGenerationException;
	
	/**
	 * polls for the result of a tileset generation task
	 * 
	 * @param imageUrl
	 * @return tileset if task has completed, otherwise null
	 * @throws TilesetGenerationException in case the of a problem during tileset generation 
	 */
	public Tileset pollForTileset(String imageUrl) throws TilesetGenerationException;
	
	/**
	 * starts a tileset generation. this method returns immediately after the generation
	 * task has been started. use <code>pollForTileset()<code> for result polling.
	 * 
	 * @param imageUrl
	 * @throws TilesetGenerationException 
	 */
	public void generateTileset(String imageUrl) throws TilesetGenerationException;	
}
