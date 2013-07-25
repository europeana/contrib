package at.ait.dme.dbpedix.maintenance.indexing;

import java.net.URI;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

import at.ait.dme.dbpedix.IndexFields;

/**
 * A utility class for creating DBpedix-specific Lucene {@link Document}s.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class DBpediaDocumentBuilder {
	
	private Document document;
	
	/**
	 * Wraps an existing {@link Document}.
	 * @param document the Document
	 */
	public DBpediaDocumentBuilder(Document document) {
		this.document = document;
	}
	
	/**
	 * Creates a new, empty {@link Document} for the resource with
	 * the specified URI
	 * @param uri the URI
	 */
	public DBpediaDocumentBuilder(URI uri) {
		document = new Document();
		document.add(new Field(
				IndexFields.URI,
				uri.toString(),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
	}
	
	/**
	 * Sets the label for the specified language (replacing
	 * a previously existing label in this language, if any)
	 * @param label the label
	 * @param lang the language code
	 */
	public void setLabel(String label, String lang) {
		document.removeFields(IndexFields.LABEL + lang);
		document.add(new Field(
				IndexFields.LABEL + lang,
				label,
				Field.Store.YES,
				Field.Index.ANALYZED));
	}
	
	/**
	 * Sets the short abstract for the specified language 
	 * (replacing a previously existing short abstract in this
	 * language, if any)
	 * @param shortAbstract the short abstract
	 * @param lang the language code
	 */	
	public void setShortAbstract(String shortAbstract, String lang) {
		document.removeFields(IndexFields.SHORT_ABSTRACT);
		document.add(new Field(
				IndexFields.SHORT_ABSTRACT + lang,
				shortAbstract,
				Field.Store.YES,
				Field.Index.NOT_ANALYZED));
	}
	
	/**
	 * Sets the latitude of this resource
	 * @param lat the latitude
	 */
	public void setLat(double lat) {
		setNumericValue(IndexFields.LAT, lat);
	}
	
	/**
	 * Sets the longitude of this resource
	 * @param lon the longitude
	 */
	public void setLon(double lon) {
		setNumericValue(IndexFields.LON, lon);
	}
	
	private void setNumericValue(String name, double value) {
		NumericField f = new NumericField(name, Field.Store.YES, false);
		f.setDoubleValue(value);
		document.removeFields(name);
		document.add(f);
	}
	
	/**
	 * Return the Lucene {@link Document} wrapped by this DocumentBuilder
	 * @return the Document
	 */
	public Document getDocument() {
		return document;
	}

}
