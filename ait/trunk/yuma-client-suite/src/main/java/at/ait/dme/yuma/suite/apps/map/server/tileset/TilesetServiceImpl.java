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

package at.ait.dme.yuma.suite.apps.map.server.tileset;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import at.ait.dme.yuma.suite.apps.core.server.Config;
import at.ait.dme.yuma.suite.apps.map.shared.Tileset;
import at.ait.dme.yuma.suite.apps.map.shared.server.TilesetService;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TilesetNotAvailableException;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TilingException;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.UnsupportedTileSchemeException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap.EvictionPolicy;

/**
 * Implementation of the tileset service. The tileset service will
 * either generate a Tileset object based on an existing Zoomify or
 * TMS descriptor, or generate a TMS tileset on the fly from an image
 * file.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class TilesetServiceImpl extends RemoteServiceServlet implements TilesetService {

	private static final long serialVersionUID = -9050121495027034814L;
	private static final int MAX_TASKS = 100;
	
	private static Logger logger = Logger.getLogger(TilesetServiceImpl.class);	
	
	private ExecutorService executor = Executors.newCachedThreadPool();	
	private ConcurrentLinkedHashMap<String, Future<Tileset>> tasks = 
		new ConcurrentLinkedHashMap<String, Future<Tileset>>(EvictionPolicy.LRU, MAX_TASKS); 

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		Config config = new Config(servletConfig);
		TilesetGenerator.init(config.getStringProperty("tiles.root.path"));		
	}
	
	@Override
	public Tileset getTileset(String url) throws TilesetNotAvailableException, UnsupportedTileSchemeException {
		url = normalize(url);

		if (url.toLowerCase().endsWith("xml")) {
			// Remote tileset
			if (url.toLowerCase().endsWith("tilemapresource.xml")) {
				try {
					return new TileschemeParser().parseTMSDescriptor(
							url.substring(0, url.lastIndexOf('/') + 1),
							new InputStreamReader(new URL(url).openStream()));
				} catch (Exception e) {
					throw new TilesetNotAvailableException(e);
				}
			}
	  	
			if (url.toLowerCase().endsWith("imageproperties.xml")) {
				try {
					return new TileschemeParser().parseZoomifyDescriptor(
							url.substring(0, url.lastIndexOf('/') + 1),
							new InputStreamReader(new URL(url).openStream()));
				} catch (Exception e) {
					throw new TilesetNotAvailableException(e);
				}
			}

			throw new UnsupportedTileSchemeException();
		} else {		
			// Generate on-the-fly
			if (tasks.get(url) != null || !TilesetGenerator.tilesetExists(url))
				throw new TilesetNotAvailableException();
	
			try {
				return new TilesetGenerator().renderTiles(url);
			} catch (TilingException e) {
				// Note: this can only happen in case there was a
				// problem with a previous tiling attempt!
				throw new TilesetNotAvailableException(e);
			}
		}
	}		
	
	
	@Override
	public void startOnTheFlyTiler(String imageUrl) throws TilingException {	
		final String url = normalize(imageUrl);
		
		if (tasks.get(url) != null) return;
		
		FutureTask<Tileset> future = new FutureTask<Tileset>(new Callable<Tileset>() {
			public Tileset call() throws TilingException {
				return new TilesetGenerator().renderTiles(url);
			}
		});
				
		if(tasks.putIfAbsent(url, future)==null) {
			executor.execute(future);
		} 				
	}

	@Override
	public Tileset pollOnTheFlyTiler(String imageUrl) throws TilingException  {
		imageUrl = normalize(imageUrl);
		Tileset tileset = null;
		
		try {
			Future<Tileset> task = tasks.get(imageUrl);
			
			if (task == null) 
				throw new TilingException("no task found. call generateTileset()");
			
			tileset = task.get(100, TimeUnit.MILLISECONDS);			
		} catch(ExecutionException e) {
			logger.fatal("Failed to generate tileset", e);
			throw new TilingException(e);		
		} catch (TimeoutException e) {
			// result not yet available, no action required
		} catch (InterruptedException e) {
			logger.info("interrupted while waiting for result", e);
		}
		
		return tileset;
	}
	
	private String normalize(String url) {
		return url.replace(" ", "%20");
	}
	
}
