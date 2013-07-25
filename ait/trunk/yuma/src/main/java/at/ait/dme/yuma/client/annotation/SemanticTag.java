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

package at.ait.dme.yuma.client.annotation;

import java.io.Serializable;

/**
 * A 'Semantic Tag' - i.e. a tag which represents a resource
 * on the linked data Web.
 * 
 * @author Rainer Simon
 */
public class SemanticTag implements Serializable {
	private static final long serialVersionUID = 948700343852610581L;

	/**
	 * The tag title
	 */
	private String title;
	
	/**
	 * The type
	 */
	private String type;
	
	/**
	 * Language(s)
	 */
	private String lang;
	
	/**
	 * The description/abstract for this tag
	 */
	private String description;
	
	/**
	 * The tag URI
	 */
	private String uri;
	
	public SemanticTag() {
		// Required for GWT serialization
	}
	
	public SemanticTag(String title, String type, String lang, String description, String uri) {
		this.title = title;
		this.type = type;
		this.lang = lang;
		this.description = description;
		this.uri = uri;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getType() {
		return type;
	}
	
	public String getLang() {
		return lang;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getURI() {
		return uri;
	}
	
	@Override
	public boolean equals(Object tag) {
		if (!(tag instanceof SemanticTag)) return false; 
		SemanticTag s = (SemanticTag) tag;
		return this.uri.equals(s.uri) && this.title.equals(s.title);
	}

}
