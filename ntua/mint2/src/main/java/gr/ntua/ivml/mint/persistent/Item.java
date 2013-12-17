package gr.ntua.ivml.mint.persistent;

import gr.ntua.ivml.mint.db.DB;

import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.xml.sax.XMLReader;

public class Item {
	private static Builder builder;
	
	private final static Logger log = Logger.getLogger( Item.class );
	
	private Long dbID;
	// this one might be better lazy, so we can have lots of items
	// loaded 
	private byte[] gzippedXml;
	private Dataset dataset;
	private String persistentId;
	private Item sourceItem;
	private Date lastModified;
	private String label;
	private boolean valid = false;
	
	// transient
	private String xml = null;
	private Document dom;
	
	public List<Item> getDerived() {
		return DB.getItemDAO().getDerived(this);
	}
	
	/**
	 * Is there an item in the given dataset that is derived from this item?
	 * @param ds
	 * @return
	 */
	public Item getDerived( Dataset ds ) {
		return DB.getItemDAO().getDerived( this, ds );
	}
	
	/**
	 * Provide an empty list and retrieve all Items that are derived from this one.
	 * @param result
	 */
	public void getDerivedRecursive( List<Item> result ) {
		List<Item> l = getDerived();
		result.addAll(l);
		for( Item i: l ) i.getDerivedRecursive( result );
	}
	
	//
	// magic gzipping of xml strings
	//
	
	public void setXml( String xml) {
		ByteArrayOutputStream baos = null;
		GzipCompressorOutputStream gz = null;
		OutputStreamWriter osw = null;
		
		try {
			baos = new ByteArrayOutputStream();
			gz = new GzipCompressorOutputStream(baos);
			osw = new OutputStreamWriter( gz, "UTF8" );
			osw.append( xml );
			osw.flush();
			osw.close();
			setGzippedXml(baos.toByteArray());
		} catch( Exception e ) {
			// uhh shouldnt really happen!!
			log.error( "Unexpected Error on gzipping content", e );
		} finally {
			try {if( osw != null ) osw.close();} catch( Exception e ) {}
			try {if( gz!= null ) gz.close();} catch( Exception e ){}
			try {if( baos!= null ) baos.close();} catch( Exception e ){}
			
		}
		this.xml = xml;
	}
	
	public String getXml() {
		if( xml == null ) {
			xml = "";
			ByteArrayInputStream bais = null;
			GzipCompressorInputStream gz = null;
			StringWriter sw = new StringWriter();
			try {
				bais = new ByteArrayInputStream(getGzippedXml());
				gz = new GzipCompressorInputStream(bais);
				IOUtils.copy( gz, sw, "UTF8" );
				xml = sw.toString();
			} catch( Exception e ) {
				log.error( "Unexpected Error on gzipping content", e );		
			} finally {
				try { if( gz != null ) gz.close(); } catch( Exception e ) {}
				try { if( bais != null) bais.close(); } catch( Exception e ) {}
			}
		}
		return xml;
	}
	
	
	public Document getDocument() {
		if( dom == null ) {
			try {
				dom = getBuilder().build( getXml(), null );
			} catch(Exception e ) {
				log.warn( "Item " + getDbID() + " has problems!",e );
			}
		}
		return dom;
	}

	
	
	/**
	 * Gets the Value if there is one node as the query result. 
	 * Otherwise returns null.
	 * @param query
	 * @return
	 */
	public String getValue( String query ) {
		nu.xom.Nodes nodes = getDocument().query( query );
		if( nodes.size() != 1 ) return null;
		String res = nodes.get(0).getValue();
		if( res == null ) res = "";
		return res;
	}
	
 	/**
	 * Set node value to given. Returns false if there is not exactly one and does nothing then.
	 * @param query
	 * @param value
	 */
	public boolean setValue( String query, String value ) {
		nu.xom.Nodes nodes = getDocument().query( query );
		if( nodes.size() != 1 ) return false;
		Node modify = nodes.get(0);
		if( modify instanceof Element ) {
			Element elem = (Element) modify;
			// replace first Text node 
			// and delete the others
			int length = elem.getChildCount();
			int i=0;
			boolean replaced = false;
			while( length > 0 ) {
				Node n = elem.getChild(i);
				if( n instanceof Text ) {
					if( replaced ) {
						elem.removeChild(i);
						i--;
					} else {
						((Text) n).setValue(value);
						replaced = true;
					}
				}
				i++;
				length--;
			}
			if( ! replaced) elem.appendChild(value);
		} else if( modify instanceof Text ) {
			Text txt = (Text) modify;
			txt.setValue(value);
		} else if( modify instanceof Attribute ) {
			Attribute attr = (Attribute) modify;
			attr.setValue(value);
		} else { return false; }
		
		// update xml
		setXml( dom.toXML());
		return true;
	}
	
	private static Builder getBuilder() {
		if( builder == null ) {
			try {
				XMLReader parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader(); 
				parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

				builder = new Builder(parser);
			} catch( Exception e ) {
				log.error( "Cannot build xml parser.", e );
			}
		}
		return builder;
	}

	
	/**
	 * Gets the original item that produced this item, or "this" if it is an original item.
	 * Calls getSourceItem() recursively. 
	 * @return the original item that produced this item or the item itself, if it is an original item.
	 */
	public Item getImportItem() {
		Item source = this;
	
		while(source.getSourceItem() != null) {
			source = source.getSourceItem();
		}
		
		return source;
	}
	
	//
	// Boilerplate Getter and Setters (curse of java) 
	//
	

	public Long getDbID() {
		return dbID;
	}
	public void setDbID(Long dbID) {
		this.dbID = dbID;
	}
	public byte[] getGzippedXml() {
		return gzippedXml;
	}
	public void setGzippedXml(byte[] gzippedXml) {
		this.gzippedXml = gzippedXml;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset ds) {
		this.dataset = ds;
	}
	public String getPersistentId() {
		return persistentId;
	}
	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}
	public Item getSourceItem() {
		return sourceItem;
	}
	public void setSourceItem(Item sourceItem) {
		this.sourceItem = sourceItem;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	
}
