package at.ait.dme.yuma.server.model.tag;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.MapKeys;
import at.ait.dme.yuma.server.model.geo.GeoLocation;

/**
 * A semantic tag which is part of an annotation. A semantic
 * tag is a link to a data resource (denoted by a dereferencable
 * URI), plus various metadata about his resource (such as title,
 * alternative labels, description, etc.)
 * 
 * @author Rainer Simon
 */
public class SemanticTag implements Serializable {

	private static final long serialVersionUID = 9073244308940077921L;

	/**
	 * The tag URI
	 * 
	 * MANDATORY
	 */
	private URI uri;

	/**
	 * The tag's primary label
	 * 
	 * MANDATORY
	 */
	private String primaryLabel;

	/**
	 * The primary description/abstract of this tag
	 * 
	 * MANDATORY
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
	 * 
	 * OPTIONAL
	 */
	private String type;
	
	/**
	 * The typed relation between annotated item and tag
	 * 
	 * OPTIONAL
	 */
	private SemanticRelation relation;

	/**
	 * Alternative labels in other languages in the form <lang, label>
	 * 
	 * OPTIONAL
	 */
	private List<PlainLiteral> altLabels;
	
	/**
	 * Alternative descriptions in other languages in the form <lang, label>
	 * 
	 * OPTIONAL
	 */
	private List<PlainLiteral> altDescriptions;
	
	/**
	 * Geographical location of the real-world entity represented by this semantic tag
	 * 
	 * OPTIONAL
	 */
	private GeoLocation geolocation;
	
	/**
	 * Creates a semantic tag based on it's mandatory properties.
	 * @param uri the URI
	 * @param primaryLabel the primary label
	 * @param primaryDescription the primary description
	 */
	public SemanticTag(URI uri, String primaryLabel, String primaryDescription) {
		this.uri = uri;
		this.primaryLabel = primaryLabel;
		this.primaryDescription = primaryDescription;
		
		this.altLabels = new ArrayList<PlainLiteral>();
		this.altDescriptions = new ArrayList<PlainLiteral>();
	}
	
	/**
	 * Creates a semantic tag with the specified properties.
	 * Mandatory properties must be set in the map. Otherwise
	 * an InvalidAnnotationException will be thrown.
	 * @param map the semantic tag's properties
	 */
	@SuppressWarnings("unchecked")
	public SemanticTag(Map<String, Object> map) throws InvalidAnnotationException {
		try {
			this.uri = (URI) map.get(MapKeys.TAG_URI);
			this.primaryLabel = (String) map.get(MapKeys.TAG_LABEL);
			this.primaryDescription = (String) map.get(MapKeys.TAG_DESCRIPTION);
			this.primaryLang = (String) map.get(MapKeys.TAG_LANG);
			this.type = (String) map.get(MapKeys.TAG_TYPE);
			this.relation = (SemanticRelation) map.get(MapKeys.TAG_RELATION);
			this.altLabels = (ArrayList<PlainLiteral>) map.get(MapKeys.TAG_ALT_LABELS);
			if (this.altLabels == null)
				this.altLabels = new ArrayList<PlainLiteral>();
			this.altDescriptions = (ArrayList<PlainLiteral>) map.get(MapKeys.TAG_ALT_DESCRIPTIONS);
			if (this.altDescriptions == null)
				this.altDescriptions = new ArrayList<PlainLiteral>();
		} catch (Throwable t) {
			throw new InvalidAnnotationException(t.getMessage());
		}
		
		// Verify mandatory properties are set
		if (this.getURI() == null)
			throw new InvalidAnnotationException("Semantic tag URI may not be null");

		if (this.getPrimaryLabel() == null)
			throw new InvalidAnnotationException("Semantic tag label may not be null");
		
		if (this.getType() == null)
			throw new InvalidAnnotationException("Semantic tag type may not be null");
	}
		
	public URI getURI() {
		return uri;
	}

	public String getPrimaryLabel() {
		return primaryLabel;
	}

	public String getPrimaryDescription() {
		return primaryDescription;
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
	
	public SemanticRelation getRelation() {
		return relation;
	}
	
	public void setSemanticRelation(SemanticRelation relation) {
		this.relation = relation;
	}

	public List<PlainLiteral> getAlternativeLabels() {
		return altLabels;
	}
	
	public void setAlternativeLabels(List<PlainLiteral> altLabels) {
		this.altLabels = altLabels;
	}
	
	public void addAlternativeLabel(PlainLiteral altLabel) {
		altLabels.add(altLabel);
	}
	
	public String getAlternativeLabel(String language) {
		return getPlainLiteral(language, altLabels);
	}

	public List<PlainLiteral> getAlternativeDescriptions() {
		return altDescriptions;
	}
	
	public void setAlternativeDescriptions(List<PlainLiteral> altDescriptions) {
		this.altDescriptions = altDescriptions;
	}
		
	public void addAlternativeDescription(PlainLiteral altDescription) {
		altDescriptions.add(altDescription);
	}
	
	public String getAlternativeDescription(String language) {
		return getPlainLiteral(language, altDescriptions);
	}
	
	public void setGeolocation(GeoLocation geolocation) {
		this.geolocation = geolocation;
	}

	public GeoLocation getGeolocation() {
		return geolocation;
	}
	
	public Map<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put(MapKeys.TAG_URI, uri);
		map.put(MapKeys.TAG_LABEL, primaryLabel);
		map.put(MapKeys.TAG_DESCRIPTION, primaryDescription);
		map.put(MapKeys.TAG_LANG, primaryLang);
		map.put(MapKeys.TAG_TYPE, type);
		map.put(MapKeys.TAG_RELATION, relation);
		map.put(MapKeys.TAG_ALT_LABELS, altLabels);
		map.put(MapKeys.TAG_ALT_DESCRIPTIONS, altDescriptions);
		
		return map;
	}
	
	private String getPlainLiteral(String language, List<PlainLiteral> literals) {
		for (PlainLiteral l : literals) {
			if (l.getLanguage().equalsIgnoreCase(language))
				return l.getValue();
		}
		
		return null;
	}

	@Override
	public boolean equals(Object other) {
		// TODO revise!
		if (!(other instanceof SemanticTag))
			return false;
		
		SemanticTag t = (SemanticTag) other;
	
		// Compare mandatory properties
		if (!t.getURI().equals(this.getURI()))
			return false;
		
		if (!t.getPrimaryLabel().equals(this.getPrimaryLabel()))
			return false;
		
		if (!t.getType().equals(this.getType()))
			return false;
		
		// Compare optional properties (may be null!)
		if (!equalsNullable(t.getPrimaryLanguage(), this.getPrimaryLanguage()))
			return false;
			
		if (!equalsNullable(t.getPrimaryDescription(), this.getPrimaryDescription()))
			return false;
		
		if (!equals(t.altLabels, this.altLabels))
			return false;

		if (!equals(t.altDescriptions, this.altDescriptions))
			return false;

		return true;
	}

	private boolean equals(List<PlainLiteral> a, List<PlainLiteral> b) {
		if (a.size() != b.size())
			return false;
		
		if (!a.containsAll(b))
			return false;
		
		if (!b.containsAll(a))
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
