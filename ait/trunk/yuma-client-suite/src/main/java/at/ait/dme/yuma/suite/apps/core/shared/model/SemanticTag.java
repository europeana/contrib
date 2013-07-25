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

package at.ait.dme.yuma.suite.apps.core.shared.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A semantic tag which is part of an annotation. A semantic
 * tag is a link to a data resource (denoted by a dereferencable
 * URI), plus various metadata about his resource (such as title,
 * alternative labels, description, etc.)
 * 
 * @author Rainer Simon
 */
public class SemanticTag implements Serializable {
	
	private static final long serialVersionUID = 948700343852610581L;

	/**
	 * The tag URI
	 */
	private String uri;
	
	/**
	 * The tag's primary label
	 */
	private String primaryLabel;
	
	/**
	 * The primary description/abstract of this tag
	 */
	private String primaryDescription;
		
	/**
	 * The language of the primary label and description
	 * 
	 * OPTIONAL
	 */
	private String primaryLang;

	/**
	 * The tag type (freely definable)
	 */
	private String type;	
	
	/**
	 * Alternative labels
	 */
	private ArrayList<PlainLiteral> alternativeLabels = null;
				
	public SemanticTag() {
		// Required for GWT serialization
	}
	
	public SemanticTag(String primaryLabel, ArrayList<PlainLiteral> alternativeLabels, String type, String primaryLang, String primaryDescription , String uri) {
		this.primaryLabel = primaryLabel;
		this.alternativeLabels = alternativeLabels;
		this.type = type;
		this.primaryLang = primaryLang;
		this.primaryDescription = primaryDescription ;
		this.uri = uri;
	}
	
	public String getURI() {
		return uri;
	}
	
	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public String getPrimaryLabel() {
		return primaryLabel;
	}
	
	public void setPrimaryLabel(String label) {
		this.primaryLabel = label;
	}
	
	public String getPrimaryDescription() {
		return primaryDescription ;
	}
	
	public void setPrimaryDescription(String description) {
		this.primaryDescription  = description;
	}
	
	public String getPrimaryLanguage() {
		return primaryLang;
	}
	
	public void setPrimaryLanguage(String lang) {
		this.primaryLang = lang;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void addAlternativeLabel(PlainLiteral label) {
		if (alternativeLabels == null)
			alternativeLabels = new ArrayList<PlainLiteral>();
			
		alternativeLabels.add(label);
	}
	
	public boolean hasAltLabels() {
		if (alternativeLabels == null)
			return false;
		
		return alternativeLabels.size() > 0;
	}

	public ArrayList<PlainLiteral> getAlternativeLabels() {
		return alternativeLabels;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SemanticTag))
			return false; 
		
		SemanticTag tag = (SemanticTag) other;
		if (!this.uri.equals(tag.uri))
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}
	
}
