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

package at.ait.dme.yuma.suite.apps.map.shared;

import java.io.Serializable;

/**
 * A TMS tileset.
 *
 * @author Christian Sadilek
 */
public class Tileset implements Serializable {
	private static final long serialVersionUID = 3764748939466986680L;

	// the base url to the TMS tileset
	private String url;
	
	// the height of the original image
	private Integer height;
	
	// the width of the original image
	private Integer width;
	
	// the number of zoom levels used to create the tiles
	private Integer zoomLevels;
	
	// The tile image file format
	private String tileformat;
	
	// The tiling scheme (Zoomify or TMS)
	private String scheme;
	
	public Tileset() { }
	
	public Tileset(String url, Integer height, Integer width, Integer zoomLevels, String tileformat, String scheme) {
		this.url = url;
		this.height = height;
		this.width = width;
		this.zoomLevels = zoomLevels;
		this.tileformat = tileformat;
		this.scheme = scheme;
	}

	public String getUrl() {
		return url;
	}
	
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getZoomLevels() {
		return zoomLevels;
	}	
	
	public String getTileFormat() {
		return tileformat;
	}
	
	public String getTilingScheme() {
		return scheme;
	}
	
	public String toString() {
		return
			"URL: " + url + ";\n" + 
			"height: " + height + ";\n" + 
			"width: " + width + ";\n" +
			"zoomLevels: " + zoomLevels + ";\n" + 
			"tileformat: " + tileformat + ";\n" +
			"scheme: " + scheme + ";"; 
	}
	
}
