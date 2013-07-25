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

package at.ait.dme.yuma.server.test;

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
import org.junit.Ignore;
import org.junit.Test;

import at.ait.dme.yuma.client.map.Tileset;
import at.ait.dme.yuma.client.server.exception.TilesetNotFoundException;
import at.ait.dme.yuma.server.image.ImageTilesetGenerator;
import at.ait.dme.yuma.server.image.ImageTilesetProviderServiceImpl;

/**
 * tests the image tileset provider service
 * 
 * @author Christian Sadilek
 */
@Ignore
public class ImageTilesetProviderTest {
	private String testImageUrl = "http://localhost:8887/test.tif";
	private boolean failed = false;
	
	class ImageServer extends Acme.Serve.Serve {
		private static final long serialVersionUID = 1L;

		public ImageServer(Map arguments, PrintStream logStream) {
			super(arguments, logStream);
		}
		
		public void setMappingTable(PathTreeDictionary mappingtable) {
			super.setMappingTable(mappingtable);
		}

		public void addWarDeployer(String deployerFactory, String throttles) {
			super.addWarDeployer(deployerFactory, throttles);
		}
	};

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
		ImageTilesetProviderServiceImpl itps = new ImageTilesetProviderServiceImpl();
		itps.init(null);

		String imageDir = ImageTilesetGenerator.getRootPath() + "/"
				+ ImageTilesetGenerator.createPathForImage(testImageUrl);
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
		ImageTilesetProviderServiceImpl itps = new ImageTilesetProviderServiceImpl();
		itps.init(null);
		
		try {
			itps.retrieveTileset(testImageUrl);
			fail("expected TilesetNotFoundException");
		} catch(TilesetNotFoundException expected) {
			// expected TilesetNotFoundException
		}
		
		itps.generateTileset(testImageUrl);
		
		Tileset result = null;
		while((result=itps.pollForTileset(testImageUrl))==null) {
			System.out.println(".");
		}
		
		assertNotNull(result);
		assertEquals(result.getZoomLevels(), 4);
		assertEquals(result.getWidth(), 1414);
		assertEquals(result.getHeight(), 1100);				
		assertEquals(result.getUrl(), "tiles/"+
				ImageTilesetGenerator.createPathForImage(testImageUrl)+"/");
				
	}
	
	@Test
	public void testConcurrentGenerateTileset() throws Exception {
		int threads = 3;
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(threads);			
		final ImageTilesetProviderServiceImpl itps = new ImageTilesetProviderServiceImpl();
		itps.init(null);
	
		for (int i = 0; i < threads; i++) {
			final int index = i;
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						startGate.await();										
						try {
							itps.generateTileset(testImageUrl);
							Tileset result = null;
							while((result=itps.pollForTileset(testImageUrl))==null) {
								System.out.println("thread "+index+": polling");
								Thread.sleep(100*index);
							}
							System.out.println("thread "+index+": received result");
							assertNotNull(result);
							assertEquals(result.getZoomLevels(), 4);
							assertEquals(result.getWidth(), 1414);
							assertEquals(result.getHeight(), 1100);				
							assertEquals(result.getUrl(), "tiles/"+
									ImageTilesetGenerator.createPathForImage(testImageUrl)+"/");						
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
