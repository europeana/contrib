/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved 
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 *  
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under 
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of 
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under 
 *  the Licence.
 */
package eu.europeana;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * 
 *         Nov 25, 2013
 */
public class GraphConstructor extends DefaultHandler {

	private static final String DB_PATH = "/home/geomark/Software/neo4j-community-1.9.5/data/graph.db";
	
	private String currentID;
	private String currentType;
	GraphDatabaseService graphDb;
	StringBuffer xmlbuffer;
	HTreeMap<String, Map<String,String>> map;
	private static Index<Node> nodeIndex;
	
	
	/**
     * 
     */
	GraphConstructor() {
		DB db = DBMaker.newMemoryDB().make();
		map = DBMaker.newTempHashMap();
		xmlbuffer = new StringBuffer();
		graphDb = new RestGraphDatabase("http://localhost:7474/db/data");
		nodeIndex = graphDb.index().forNodes("nodes");
	}

	
    /**
     * @author Georgios Markakis (gwarkx@hotmail.com)
     *
     * Nov 29, 2013
     */
    private class  RelType implements RelationshipType{
    	private String name;
    	
    	/**
    	 * @param name
    	 */
    	public RelType(String name){
    		this.name = name;
    	}
    	
		@Override
		public String name() {
			return name;
		}

    }
	
	
	/**
	 * @param xml
	 */
	public void parseXML(String xml) {
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(new InputSource(new StringReader(xml)), this);
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * @param xml
	 * @throws FileNotFoundException
	 */
	public void parseFile(String file) throws FileNotFoundException {
		BufferedReader inputStream = new BufferedReader(new FileReader(file));

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(new InputSource(inputStream), this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {

		if (qName.equalsIgnoreCase("edm:Proxy")
				|| qName.equalsIgnoreCase("ore:Proxy")
				|| qName.equalsIgnoreCase("edm:ProvidedCHO")
				|| qName.equalsIgnoreCase("edm:WebResource")
				|| qName.equalsIgnoreCase("edm:Agent")
				|| qName.equalsIgnoreCase("edm:Place")
				|| qName.equalsIgnoreCase("edm:TimeSpan")
				|| qName.equalsIgnoreCase("skos:Concept")
				|| qName.equalsIgnoreCase("ore:Aggregation")) {

			currentID = attrs.getValue("rdf:about");
			currentType = qName;

		}

		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware

		xmlbuffer.append("<");
		xmlbuffer.append(eName);

		if (attrs != null) {

			Map<String,String> referencelist = map.get(currentID);
			if (referencelist == null) {
				referencelist = new HashMap<String,String>();
				
				if(currentID != null){
					map.put(currentID, referencelist);
				}
				
			}

			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name

				if (aName.equals("rdf:resource")) {
					referencelist.put(attrs.getValue(i),eName);
				}

				if ("".equals(aName))
					aName = attrs.getQName(i);

				xmlbuffer.append(" ");
				xmlbuffer.append("=\"");
				xmlbuffer.append(attrs.getValue(i));
				xmlbuffer.append("\"");
			}
			
			if(currentID != null){
				map.put(currentID, referencelist);
			}
		}
		xmlbuffer.append(">");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) throws SAXException {
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware
		xmlbuffer.append("</");
		xmlbuffer.append(eName);
		xmlbuffer.append(">");

		if (qName.equalsIgnoreCase("edm:Proxy")
				|| qName.equalsIgnoreCase("ore:Proxy")
				|| qName.equalsIgnoreCase("edm:ProvidedCHO")
				|| qName.equalsIgnoreCase("edm:WebResource")
				|| qName.equalsIgnoreCase("edm:Agent")
				|| qName.equalsIgnoreCase("edm:Place")
				|| qName.equalsIgnoreCase("edm:TimeSpan")
				|| qName.equalsIgnoreCase("skos:Concept")
				|| qName.equalsIgnoreCase("ore:Aggregation")) {

			System.out.println(xmlbuffer.toString());
			System.out.println(currentID);
			System.out.println(currentType);
			
			Map<String,String> referencelist = map.get(currentID);
			
			Iterator<String> it = referencelist.keySet().iterator();
			
			while(it.hasNext()){
				String key = it.next();
				String value = referencelist.get(key);
				System.out.println(key);
				System.out.println(value);
			}

			generateNode();
			
			xmlbuffer = new StringBuffer();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException {
		String s = new String(buf, offset, len);
		if (xmlbuffer == null) {
			xmlbuffer = new StringBuffer(s);
		} else {
			xmlbuffer.append(s);
		}
	}
	
	@Override
	public void  endDocument(){
		generateNodeLinks();
	}
	
	/**
	 * 
	 */
	private void generateNode(){
        Transaction tx = graphDb.beginTx();
        try
        {
		Node thenode = graphDb.createNode();
		thenode.setProperty( "type", currentType);
		thenode.setProperty( "xml", xmlbuffer.toString());
		thenode.setProperty( "rdf:about", currentID);
		nodeIndex.add( thenode, "type", currentType );
		nodeIndex.add( thenode, "rdf:about", currentID );
        tx.success();
      }
      finally
      {
          tx.finish();
      }
	}
	
	/**
	 * 
	 */
	private void generateNodeLinks(){
		
		Iterator<String> it = map.keySet().iterator();
		
		while(it.hasNext()){
			
			String id = it.next();
			
			Node nd = nodeIndex.get("rdf:about", id).getSingle();
			
			Map<String, String> values = map.get(id);
			
			Set<String> set = values.keySet();
			
			Iterator<String> it2 = set.iterator();
			
			while(it2.hasNext()){
				String reference = it2.next();
				String linkname = values.get(reference);
				
		        Transaction tx = graphDb.beginTx();
		        try
		        {
				Node ndref = nodeIndex.get("rdf:about", reference).getSingle();		
				if(ndref != null){
					Relationship relationship = nd.createRelationshipTo( ndref, new RelType(linkname));
		            tx.success();
				}

		        }
		        finally
		        {
		            tx.finish();
		        }
			}	
		}	
	}
	
	
    /**
     * 
     */
    public void clearDb()
    {
        try
        {
            FileUtils.deleteRecursively( new File( DB_PATH ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
	
}
