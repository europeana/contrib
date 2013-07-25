package at.ait.dme.yuma.server.model.tag;

import java.io.Serializable;

/**
 * A simple class representing a 'plain literal', 
 * i.e. a String with an optional language code.
 * 
 * @author Rainer Simon
 */
public class PlainLiteral implements Serializable {

	private static final long serialVersionUID = 1760402390634729635L;

	/**
	 * The alternative label
	 */
	private String value;
	
	/**
	 * The optional language
	 */
	private String lang = null;
	
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
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PlainLiteral))
			return false;
		
		PlainLiteral l = (PlainLiteral) other;
		
		if (!l.getValue().equals(this.getValue()))
			return false;

		if (!equalsNullable(this.getLanguage(), l.getLanguage()))
			return false;
			
		return true;
	}
	
	private boolean equalsNullable(Object a, Object b) {
		if (a == null)
			return b == null;
		
		if (b == null)
			return a == null;
		
		return a.equals(b);
	}
	
}
