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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import at.ait.dme.magicktiler.MagickTiler;
import at.ait.dme.magicktiler.tms.TMSTiler;
import at.ait.dme.yuma.suite.apps.map.shared.Tileset;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TilingException;

/**
 * This class uses MagickTiler to generate TMS tiles for
 * images on the fly.
 * 
 * @author Christian Sadilek
 */
public class TilesetGenerator {

	private static Logger logger = Logger.getLogger(TilesetGenerator.class);

	private static String rootPath = null;

	public static void init(String tilesRootPath) {
		rootPath = tilesRootPath;
	}
		
	/**
	 * Generates TMS tiles for the image with the given URL using
	 * MagickTiler (http://code.google.com/p/magicktiler).
	 * 
	 * @param url the image URL
	 * @return tileset the tileset
	 * @throws TilingException if anything goes wrong
	 */
	public Tileset renderTiles(String url) throws TilingException {
		try {
			String tilesDir = rootPath + "/"+ createTilesetPath(url);
			
			if (new File(tilesDir).mkdir()) {
				String imageFile = downloadImage(tilesDir, url);
				MagickTiler tiler = new TMSTiler();
				tiler.convert(new File(imageFile), new File(tilesDir));							
			}
	
			FileReader reader = new FileReader(new File(tilesDir + "/tilemapresource.xml"));
			return new TileschemeParser()
				.parseTMSDescriptor("tilesets/" + createTilesetPath(url) + "/", reader);
		} catch (Throwable t) {
			logger.fatal(t.getMessage(), t);
			throw new TilingException(t);
		}			
	}
		
	private String downloadImage(String dir, String url) throws TilingException {
		InputStream is = null;
		OutputStream os = null;
		
		try {
			GetMethod getImageMethod = new GetMethod(url);
			int statusCode=new HttpClient().executeMethod(getImageMethod);
			if (statusCode  != HttpStatus.SC_OK)
				throw new TilingException("GET " + url + " returned status code:" + statusCode);
			
			is = getImageMethod.getResponseBodyAsStream();			
			File imageFile=new File(dir+"/"+url.substring(url.lastIndexOf("/")).replace("%", "-"));
		    os=new FileOutputStream(imageFile);
		    
		    byte buf[]=new byte[1024];
		    int len;
		    while((len=is.read(buf))>0)
		    	os.write(buf,0,len);
		    
		    return imageFile.getAbsolutePath();
		} catch (Throwable t) {
			logger.error("failed to store image from url:"+url,t);
			throw new TilingException(t);
		} finally {
			try {
				if (os != null) os.close();
				if (is != null) is.close();
			} catch (IOException e) {
				logger.error("failed to close streams");
			}
		  				    
		}
	}
		
	public static boolean tilesetExists(String imageUrl) {
		return new File(rootPath + "/"  + 
				TilesetGenerator.createTilesetPath(imageUrl)).exists();
	}
	
	public static String createTilesetPath(String imageUrl) {		
		String path = imageUrl.replaceAll("[^(A-Za-z0-9)]+", "-");
		if (path.length() > 255)
			path = path.substring(path.length()-255);
		return path;
	}

	public static String getRootPath() {
		return rootPath;
	}
	
}
