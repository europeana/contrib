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

package at.ait.dme.yuma.suite.apps.map.shared.geo;

import java.io.Serializable;

/**
 * A makeshift 'metadata' class. Totally static right now and 
 * only supports author, date, description and thumbnail URL. 
 * But sufficient for now to demo the principle of the 'About 
 * this map' area in the exploration tab.
 * 
 * @author Rainer Simon
 */
public class MapMetadata implements Serializable {

	private static final long serialVersionUID = -8942620774010895534L;
	
	/**
	 * The map title - mandatory
	 */
	private String title;
	
	/**
	 * Map author - mandatory
	 */
	private String author;
	
	/**
	 * Date of the map (usually creation date) - mandatory
	 */
	private String date;
	
	/**
	 * The map source or provider name
	 */
	private String source;
	
	/**
	 * Some text description
	 */
	private String description;

	/**
	 * A thumbnail URL
	 */
	private String thumbnail;
	
	/**
	 * A URL pointing to further info, the Web page of the provider, etc.
	 */
	private String link;
	
	public MapMetadata() { }

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}
	
	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}
	
	/**
	 * Title, author and date are mandatory - i.e. if one of those is
	 * not set, the metadata object will be considered 'empty'.
	 */
	public boolean isEmpty() {
		if (title == null) return true;
		if (author == null) return true;
		if (date == null) return true;
		return false;
	}
	
}
