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

package at.ait.dme.yuma.suite.test.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ait.dme.yuma.suite.apps.map.server.tileset.TilesetGenerator;
import at.ait.dme.yuma.suite.apps.map.server.tileset.TilesetServiceImpl;
import at.ait.dme.yuma.suite.apps.map.shared.Tileset;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TilesetNotAvailableException;

/**
 * tests the image tileset provider service
 * 
 * @author Christian Sadilek
 */
public class ImageTilesetProviderTest {
	private String testImageUrl = "http://localhost:8887/test.tif";
	private boolean failed = false;
	
	class ImageServer extends Acme.Serve.Serve {
		private static final long serialVersionUID = 1L;

		public ImageServer(@SuppressWarnings("rawtypes") Map arguments, PrintStream logStream) {
			super(arguments, logStream);
		}
		
		public void setMappingTable(PathTreeDictionary mappingtable) {
			super.setMappingTable(mappingtable);
		}

		public void addWarDeployer(String deployerFactory, String throttles) {
			super.addWarDeployer(deployerFactory, throttles);
		}
	};

	@SuppressWarnings("rawtypes")
	final ImageServer imageServer = new ImageServer(new HashMap(), new PrintStream(new PipedOutputStream()));
	    
	@Before
	public void setUp() throws Exception {
		Acme.Serve.Serve.PathTreeDictionary aliases = new Acme.Serve.Serve.PathTreeDictionary();
		URL localImage = this.getClass().getResource("test.tif");
		File image = new File(localImage.toURI());
		aliases.put("/", image.getParentFile());
        imageServer.setMappingTable(aliases);
		java.util.Properties properties = new java.util.Properties();
		properties.put("port", 8887);
		imageServer.arguments = properties;
		imageServer.addDefaultServlets(null);
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				imageServer.serve();				
			}
		};
		thread.start();	
	}
	
	@After
	public void cleanUp() throws Exception {	
		TilesetServiceImpl itps = new TilesetServiceImpl();
		itps.init(null);

		String imageDir = TilesetGenerator.getRootPath() + "/"
				+ TilesetGenerator.createTilesetPath(testImageUrl);
		deleteDir(new File(imageDir));
			
		imageServer.notifyStop();		
		imageServer.destroyAllServlets();
		
	}
	
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		} 
		return dir.delete();
	}

	
	@Test
	public void testRetrieveTileset() throws Exception {
		TilesetServiceImpl itps = new TilesetServiceImpl();
		itps.init(null);
		
		try {
			itps.getTileset(testImageUrl);
			fail("expected TilesetNotFoundException");
		} catch(TilesetNotAvailableException expected) {
			// expected TilesetNotFoundException
		}
		
		itps.startOnTheFlyTiler(testImageUrl);
		
		Tileset result = null;
		while((result=itps.pollOnTheFlyTiler(testImageUrl))==null) {
			System.out.println(".");
		}
		
		assertNotNull(result);
		assertEquals(result.getZoomLevels(), 4);
		assertEquals(result.getWidth(), 1414);
		assertEquals(result.getHeight(), 1100);				
		assertEquals(result.getUrl(), "tilesets/"+
				TilesetGenerator.createTilesetPath(testImageUrl)+"/");
				
	}
	
	@Test
	public void testConcurrentGenerateTileset() throws Exception {
		int threads = 3;
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(threads);			
		final TilesetServiceImpl itps = new TilesetServiceImpl();
		itps.init(null);
	
		for (int i = 0; i < threads; i++) {
			final int index = i;
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						startGate.await();										
						try {
							itps.startOnTheFlyTiler(testImageUrl);
							Tileset result = null;
							while((result=itps.pollOnTheFlyTiler(testImageUrl))==null) {
								System.out.println("thread "+index+": polling");
								Thread.sleep(100*index);
							}
							System.out.println("thread "+index+": received result");
							assertNotNull(result);
							assertEquals(result.getZoomLevels(), 4);
							assertEquals(result.getWidth(), 1414);
							assertEquals(result.getHeight(), 1100);				
							assertEquals(result.getUrl(), "tilesets/"+
									TilesetGenerator.createTilesetPath(testImageUrl)+"/");						
						} catch(Throwable t) {
							failed=true;
						} finally {
							endGate.countDown();							
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			};
			thread.start();
		}		
		startGate.countDown();
		endGate.await();
		assertFalse(failed);
	}	
}
