package at.ait.dme.dbpedix;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;

/**
 * The central DBpedix 'domain object' - the DBpedia resource.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class DBpediaResource {
	
	private Document document;
	
	DBpediaResource(Document document) {
		this.document = document;
	}
	
	/**
	 * Returns the URI of this resource
	 * @return the URI
	 */
	public URI getURI() {
		try {
			return new URI(document.get(IndexFields.URI));
		} catch (URISyntaxException e) {
			// Can never happen
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the label in the specified language, or <code>null</code>
	 * @param lang the language code
	 * @return the label (if available), or null (if not)
	 */
	public String getLabel(String lang) {
		return document.get(IndexFields.LABEL + lang);
	}
	
	/**
	 * Returns all labels for this resource, in the form of a 
	 * map (language code) -> (label) 
	 * @return a map in the form lang -> label
	 */
	public Map<String, String> listLabels() {
		return listPlainLiterals(IndexFields.LABEL);
	}
	
	/**
	 * Returns the short abstract in the specified language,
	 * or <code>null</code>
	 * @param lang the language code
	 * @return the short abstract, or null
	 */
	public String getShortAbstract(String lang) {
		return document.get(IndexFields.SHORT_ABSTRACT + lang);
	}
	
	/**
	 * Returns all short abstracts for this resource, in the
	 * form of a map (language code) -> (short abstract) 
	 * @return a map in the form lang -> short abstract
	 */
	public Map<String, String> listShortAbstracts() {
		return listPlainLiterals(IndexFields.SHORT_ABSTRACT);
	}
	
	/**
	 * Returns the geo-coordinates for this resource, or null.
	 * @return the geo-coordinates or null
	 */
	public LatLon getGeoCoordinates() {
		NumericField fLat = (NumericField) document.getFieldable(IndexFields.LAT);
		NumericField fLon = (NumericField) document.getFieldable(IndexFields.LON);
		
		if (fLat == null || fLon == null)
			return null;
		
		return new LatLon(fLat.getNumericValue().doubleValue(), fLon.getNumericValue().doubleValue());
	}
	
	private Map<String, String> listPlainLiterals(String fieldPrefix) {
		HashMap<String, String> literals = new HashMap<String, String>();
		for (Fieldable f : document.getFields()) {
			if (f.name().startsWith(fieldPrefix)) {
				literals.put(
					f.name().substring(fieldPrefix.length()),
					f.stringValue());
			}
		}
		return literals;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getURI().toString() + "\n");
		
		Map<String, String> labels = listLabels();
		for (String lang : labels.keySet()) {
			sb.append("LABEL " + lang + ": " + labels.get(lang) + "\n");
		}
		
		Map<String, String> shortAbstracts = listShortAbstracts();
		for (String lang : shortAbstracts.keySet()) {
			sb.append("SHORT ABSTRACT " + lang + ": " + shortAbstracts.get(lang) + "\n");
		}
		
		LatLon ll = getGeoCoordinates();
		if (ll != null)
			sb.append("LAT/LON: " + ll.toString());
		
		return sb.toString();
	}

}
