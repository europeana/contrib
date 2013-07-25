package at.ait.dme.yuma.suite.apps.core.shared.model;

import java.io.Serializable;

/**
 * A simple class representing a 'plain literal', 
 * i.e. a String with an optional language code.
 * 
 * @author Rainer Simon
 */
public class PlainLiteral implements Serializable {

	private static final long serialVersionUID = 7489497930474128538L;

	/**
	 * The alternative label
	 */
	private String value;
	
	/**
	 * The optional language
	 */
	private String lang = null;
	
	public PlainLiteral() { 
		// Required for GWT serialization
	}
	
	public PlainLiteral(String value) {
		this.value = value;
	}
	
	public PlainLiteral(String altLabel, String lang) {
		this.value = altLabel;
		this.lang = lang;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getLanguage() {
		return lang;
	}
	
}
