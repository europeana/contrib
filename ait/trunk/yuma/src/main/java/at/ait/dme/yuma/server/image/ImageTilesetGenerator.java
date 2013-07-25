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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import at.ait.dme.yuma.client.map.Tileset;
import at.ait.dme.yuma.client.server.exception.TilesetGenerationException;

/**
 * This class uses gdal2tiles to generate TMS tiles for images
 * 
 * @author Christian Sadilek
 */
public class ImageTilesetGenerator {
	private static Logger logger = Logger.getLogger(ImageTilesetGenerator.class);

	private static String rootPath = null;
	private static String generationCommand = null;
	
	public static void init(String tilesRootPath, String tilesGenerationCommand) {
		rootPath=tilesRootPath;
		generationCommand=tilesGenerationCommand;
	}
	
	/**
	 * generates TMS tiles for the given image. it starts a separate process and executes
	 * the configured command (gdal2tiles).
	 * 
	 * @param url
	 * @return tileset
	 * @throws TilesetGenerationException
	 */
	public Tileset generateTilesetFromUrl(String url) throws TilesetGenerationException {
		try {
			String tilesDir = rootPath + "/"+ createPathForImage(url);
			if(new File(tilesDir).mkdir()) {
				String imageFile=storeImage(tilesDir, url);
				
				Process p = Runtime.getRuntime().exec(generationCommand+ " " + imageFile +
						" " + tilesDir);			
				
				// consume the input stream, so the process does not hang
				InputStream stderr = p.getInputStream();
	            InputStreamReader isr = new InputStreamReader(stderr);
	            BufferedReader br = new BufferedReader(isr);	            
	            while ((br.readLine()) != null);
	                
				if(p.waitFor()!=0) {
					String errorMessage = "";
					InputStream errorStream = p.getErrorStream();
					if(errorStream!=null) 
						errorMessage = IOUtils.toString(errorStream, "UTF-8");					
					
					throw new TilesetGenerationException("failed to create tiles: " + errorMessage);
				}								
			}
			return createTileset(url, tilesDir);
		} catch (Throwable t) {
			logger.fatal(t.getMessage(), t);
			throw new TilesetGenerationException(t);
		}			
	}
	
	private String storeImage(String dir, String url) throws TilesetGenerationException {
		InputStream is = null;
		OutputStream os = null;
		try {
			GetMethod getImageMethod = new GetMethod(url);
			int statusCode=new HttpClient().executeMethod(getImageMethod);
			if (statusCode  != HttpStatus.SC_OK)
				throw new TilesetGenerationException("GET " + url + " returned status code:"
						+ statusCode);
			
			is = getImageMethod.getResponseBodyAsStream();			
			File imageFile=new File(dir+"/"+url.substring(url.lastIndexOf("/")));
		    os=new FileOutputStream(imageFile);
		    
		    byte buf[]=new byte[1024];
		    int len;
		    while((len=is.read(buf))>0)
		    	os.write(buf,0,len);
		    
		    return imageFile.getAbsolutePath();
		} catch (Throwable t) {
			logger.error("failed to store image from url:"+url,t);
			throw new TilesetGenerationException(t);
		} finally {
			try {
				if (os != null) os.close();
				if (is != null) is.close();
			} catch (IOException e) {
				logger.error("failed to close streams");
			}
		  				    
		}
	}
	
	private Tileset createTileset(String url, String dir) throws JDOMException, IOException {		
		Integer width=null, height=null, zoomlevel=null;
		
		Document doc = new SAXBuilder().build(new File(dir+"/tilemapresource.xml"));
		Element root = doc.getRootElement();
		if(root!=null) {
			Element boundingBox=root.getChild("BoundingBox");
			if(boundingBox!=null){
				height=Math.round(Math.abs(boundingBox.getAttribute("minx").getFloatValue()));
				width=Math.round(Math.abs(boundingBox.getAttribute("maxy").getFloatValue()));				
			}
			Element tileSets=root.getChild("TileSets");
			zoomlevel=tileSets.getChildren("TileSet").size();
		}
		return new Tileset("tiles/"+createPathForImage(url)+"/", 
				height, width, zoomlevel);
	}
	
	/**
	 * check if tiles exist for the given image
	 * 
	 * @param imageUrl
	 * @return true, if tileset exists, otherwise false
	 * @throws UnsupportedEncodingException 
	 */
	public static boolean tilesetExists(String imageUrl) {
		return new File(rootPath + "/"  + 
				ImageTilesetGenerator.createPathForImage(imageUrl)).exists();
	}
	
	/**
	 * creates the path name for an image.
	 *  
	 * @param imageUrl
	 * @return
	 */
	public static String createPathForImage(String imageUrl) {		
		String path = imageUrl.replaceAll("[^(A-Za-z0-9)]+", "-");
		if (path.length() > 255)
			path = path.substring(path.length()-255);
		
		return path;
	}

	/**
	 * get root path of all tilesets
	 * 
	 * @return
	 */
	public static String getRootPath() {
		return rootPath;
	}
}
