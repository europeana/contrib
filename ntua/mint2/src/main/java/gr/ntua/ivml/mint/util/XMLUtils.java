package gr.ntua.ivml.mint.util;

import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.xml.transform.XMLFormatter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;

public class XMLUtils {
	public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/**
	 * Gets recursively all the namespace declarations (URI & prefix) of all element's contained in the document
	 * @param document
	 * @return a map of prefix/URI key/value pairs
	 */
	public static Map<String, String> getNamespaceDeclarations(Document document) {
		return XMLUtils.getNamespaceDeclarations(document.getRootElement());
	}
	
	/**
	 * Gets recursively all the namespace declarations (URI & prefix) of this element and its descendants (elements and attributes).
	 * @param element
	 * @return a map of prefix/URI key/value pairs
	 */
	public static Map<String, String> getNamespaceDeclarations(Element element) {
		HashMap<String, String> result = new HashMap<String, String>();
		
		for(int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
			String prefix = element.getNamespacePrefix(i);
			String uri = element.getNamespaceURI(prefix);
			result.put(prefix, uri);
		}

		for(int i = 0; i < element.getChildElements().size(); i++) {
			result.putAll(XMLUtils.getNamespaceDeclarations(element.getChildElements().get(i)));			
		}
		
		return result;
	}
	
	/**
	 * Format an XML document to split elements in different lines and use correct indentation.
	 * @param xml String representation of XML document.
	 * @return string representation of formatted XML document.
	 */
	public static String format(String xml) {
		return XMLFormatter.format(xml);
	}
	
	/**
	 * Generate an XML Document with the contents of a mappings handler.
	 * @return
	 */
	public static Document toDocument(Mappings mappings) {
		Document document = new Document(mappings.getTemplate().toElement(JSONUtils.toMap(mappings.getNamespaces())));
		return document;
	}

	/**
	 * Generate an xml string with the contents of a mappings handler.
	 * @return
	 */
	public static String toXML(Mappings mappings) {
		return XMLUtils.toDocument(mappings).toXML();
	}

	/**
	 * Compute the common root xpath for all provided xpaths
	 * @param xpaths list of xpaths
	 * @return common root of provided list
	 */
	public static String commonRoot(Collection<String> xpaths){

		String commonPath = "";
		String[][] folders = new String[xpaths.size()][];

		int c = 0;
		for(String xpath: xpaths) {
			folders[c++] = xpath.split("/"); //split on file separator
		}
		
		for(int j = 0; j < folders[0].length; j++){
			String thisFolder = folders[0][j]; //grab the next folder name in the first path
			boolean allMatched = true; //assume all have matched in case there are no more paths
			for(int i = 1; i < folders.length && allMatched; i++){ //look at the other paths
				if(folders[i].length < j){ //if there is no folder here
					allMatched = false; //no match
					break; //stop looking because we've gone as far as we can
				}
				//otherwise
				allMatched &= folders[i][j].equals(thisFolder); //check if it matched
			}
			if(allMatched){ //if they all matched this folder name
				commonPath += thisFolder + "/"; //add it to the answer
			}else{//otherwise
				break;//stop looking
			}
		}
		return commonPath;
	}
}
