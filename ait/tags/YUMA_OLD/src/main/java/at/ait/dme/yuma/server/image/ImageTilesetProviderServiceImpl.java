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

package at.ait.dme.yuma.server.image;

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

import at.ait.dme.yuma.client.map.Tileset;
import at.ait.dme.yuma.client.server.ImageTilesetProviderService;
import at.ait.dme.yuma.client.server.exception.TilesetGenerationException;
import at.ait.dme.yuma.client.server.exception.TilesetNotFoundException;
import at.ait.dme.yuma.server.util.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap.EvictionPolicy;

/**
 * Implementation of the image tileset provider service.
 * 
 * @author Christian Sadilek
 */
public class ImageTilesetProviderServiceImpl extends RemoteServiceServlet implements
		ImageTilesetProviderService {

	private static final long serialVersionUID = -9050121495027034814L;
	private static final int MAX_TASKS = 100;
	
	private static Logger logger = Logger.getLogger(ImageTilesetProviderServiceImpl.class);	
	
	private ExecutorService executor = Executors.newCachedThreadPool();	
	private ConcurrentLinkedHashMap<String, Future<Tileset>> tasks = 
		ConcurrentLinkedHashMap.create(EvictionPolicy.LRU, MAX_TASKS); 

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		Config config = new Config(servletConfig, 
				getClass().getResourceAsStream("image-tileset-provider-service.properties"));
		ImageTilesetGenerator.init(config.getStringProperty("tiles.root.path") ,
				config.getStringProperty("tiles.generation.command"));		
	}
	
	@Override
	public void generateTileset(final String imageUrl) throws TilesetGenerationException {	
		if (tasks.get(imageUrl) != null) return;
		
		FutureTask<Tileset> future = new FutureTask<Tileset>(new Callable<Tileset>() {
			public Tileset call() throws TilesetGenerationException {
				return new ImageTilesetGenerator().generateTilesetFromUrl(imageUrl);
			}
		});
				
		if(tasks.putIfAbsent(imageUrl, future)==null) {
			executor.execute(future);
		} 				
	}

	@Override
	public Tileset pollForTileset(String imageUrl) throws TilesetGenerationException  {
		Tileset tileset = null;
		
		try {
			Future<Tileset> task = tasks.get(imageUrl);
			if(task==null) 
				throw new TilesetGenerationException("no task found. call generateTileset()");
			tileset=task.get(100, TimeUnit.MILLISECONDS);			
		} catch(ExecutionException e) {
			logger.fatal("failed to generate tiles", e);
			throw new TilesetGenerationException(e);		
		} catch (TimeoutException e) {
			// result not yet available, no action required
		} catch (InterruptedException e) {
			logger.info("interrupted while waiting for result", e);
		}
		
		return tileset;
	}
	
	@Override
	public Tileset retrieveTileset(String imageUrl) throws TilesetNotFoundException, 
			TilesetGenerationException {
		
		if (tasks.get(imageUrl) != null || !ImageTilesetGenerator.tilesetExists(imageUrl))
			throw new TilesetNotFoundException();

		return new ImageTilesetGenerator().generateTilesetFromUrl(imageUrl);
	}		
}
