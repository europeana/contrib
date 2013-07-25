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

import java.io.IOException;
import java.io.Reader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import at.ait.dme.yuma.suite.apps.map.shared.Tileset;

/**
 * A utility class for parsing tile scheme descriptor files.
 * Currently supports Zoomify and TMS tile schemes.
 * 
 * @author Rainer Simon
 */
public class TileschemeParser {

	private static final String ERROR_MSG = "Could not parse descriptor file";
		
	public Tileset parseTMSDescriptor(String tilesetUrl, Reader reader) throws JDOMException, IOException {
		Integer width = null;
		Integer height = null;
		Integer zoomlevel = null;
		String extension = "png";
	
		Document doc = new SAXBuilder().build(reader);
		Element root = doc.getRootElement();
		if(root!=null) {
			// Bounding box
			Element boundingBox=root.getChild("BoundingBox");
			if(boundingBox!=null){
				height=Math.round(Math.abs(boundingBox.getAttribute("minx").getFloatValue()));
				width=Math.round(Math.abs(boundingBox.getAttribute("maxy").getFloatValue()));				
			}
			// Zoom levels
			Element tileSets=root.getChild("TileSets");
			zoomlevel=tileSets.getChildren("TileSet").size();
			
			// Image format
			Element tileFormat=root.getChild("TileFormat");
			if (tileFormat!=null){
				extension = tileFormat.getAttribute("extension").getValue();
			}

			return new Tileset(tilesetUrl, height, width, zoomlevel, extension, "tms");		
		}
		
		throw new IOException(ERROR_MSG);
	}
	
	public Tileset parseZoomifyDescriptor(String tilesetUrl, Reader reader) throws JDOMException, IOException {
		double width;
		double height;
		int zoomlevel;
		
		Document doc = new SAXBuilder().build(reader);
		Element root = doc.getRootElement();
		if(root!=null) {
			width = Math.round(Math.abs(root.getAttribute("WIDTH").getFloatValue()));
			height = Math.round(Math.abs(root.getAttribute("HEIGHT").getFloatValue()));
			
			int tileX = (int) Math.ceil(width / 256);
			int tileY = (int) Math.ceil(height / 256);
			int dim = (tileX > tileY) ? tileX : tileY;
			zoomlevel = (int) Math.ceil(Math.log(dim) / Math.log(2)) + 1;

			return new Tileset(tilesetUrl, new Integer((int) height), new Integer((int) width), new Integer((int) zoomlevel), "jpg", "zoomify");
		}
		
		throw new IOException(ERROR_MSG);
	}
	
}
