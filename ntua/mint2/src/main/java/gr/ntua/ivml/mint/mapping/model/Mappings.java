package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.mapping.JSONMappingHandler;
import gr.ntua.ivml.mint.mapping.MappingPrimitives;
import gr.ntua.ivml.mint.util.JSONUtils;
import gr.ntua.ivml.mint.util.StringUtils;
import gr.ntua.ivml.mint.util.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Text;
import nu.xom.ValidityException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Mappings extends JSONMappingHandler {
	public Mappings() {
		super(new JSONObject());
	}
	
	public Mappings(JSONObject mappings) {
		super(mappings);
	}
	
	public Mappings(String string) throws ParseException {
		super(JSONUtils.parse(string));
	}

	public Mappings(File file) throws IOException, ParseException {		
		this(StringUtils.fileContents(file).toString());
	}
	

	/**
	 * Set mapping data structure version
	 */
	public void setVersion(String version) {
		object.put(TEMPLATE_VERSION, version);
	}
	
	/**
	 * Get version of mapping data structure:
	 * <ul>
	 * 	<li>1.0: Original version of mint2 mappings</li>
	 *  <li>2.1: Mapping/Condition pairs are moved into cases array, to support if/then/else</li>
	 * </ul>
	 * @return version of mapping data structure
	 */
	public String getVersion() {
		return this.getString(TEMPLATE_VERSION);
	}
	
	/** 
	 * Check if mapping data structure is at latest version
	 * @return true if version is as shown in JSONMappingHandler.VERSION_LATEST constant
	 */
	public boolean isLatestVersion() {
		String version = this.getVersion();
		if(version != null && version.equalsIgnoreCase(JSONMappingHandler.VERSION_LATEST)) return true;
		return false;
	}
	
	/**
	 * Get handler for the template group.
	 * 
	 * @return handler for the template group or null if it does not exist.
	 */
	public Element getTemplate() {
		if(this.has(TEMPLATE_TEMPLATE)) {
			return new Element(this.getObject(TEMPLATE_TEMPLATE));
		}

		return null;
	}
	
	public void setTemplate(JSONObject template) {
		this.put(TEMPLATE_TEMPLATE, template);
	}
	
	public void setTemplate(Element template) {
		this.put(TEMPLATE_TEMPLATE, template.asJSONObject());
	}
	
	public void setNamespaces(JSONObject namespaces) {
		this.put(TEMPLATE_NAMESPACES, namespaces);
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.put(TEMPLATE_NAMESPACES, namespaces);
	}

	/**
	 * Generate a basic mapping template from the specified xml string.
	 * @param xml the xml string
	 * @return handler for the generated template
	 * @throws SAXException
	 * @throws ValidityException
	 * @throws ParsingException
	 * @throws IOException
	 */
	public static Mappings mappingsFromXML(String xml) throws SAXException, ValidityException, ParsingException, IOException {
		XMLReader parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader(); 
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Builder builder = new Builder(parser);
		Document document = builder.build( xml, null );
		
		JSONObject template = Mappings.jsonFromElement(document.getRootElement());
		Map<String, String> namespaces = XMLUtils.getNamespaceDeclarations(document); 

		Mappings mappings = new Mappings();
		mappings.setTemplate(template);
		mappings.setNamespaces(namespaces);
		
		return mappings;
	}
	
	private static JSONObject jsonFromElement(nu.xom.Element element) {
		JSONObject object = MappingPrimitives.element(element.getLocalName(), element.getNamespacePrefix(), null);

		Elements elements = element.getChildElements();
		JSONArray children = new JSONArray();
		
		for(int i = 0; i < element.getChildCount(); i++) {
			Node node = element.getChild(i);
			if(node.getClass().equals(Text.class)) {
				String value = node.getValue().trim();
				if(value.length() > 0) {
					Element e = new Element(object);
					e.addMappingCase(0);
					e.getMappingCase(0).addMapping(new SimpleMapping(SimpleMapping.MAPPING_TYPE_CONSTANT, node.getValue()));
				}
			}
		}
		
		JSONArray attributes = new JSONArray();
		for(int i = 0; i < element.getAttributeCount(); i++) {
			nu.xom.Attribute attribute = element.getAttribute(i);
			JSONObject aObject = MappingPrimitives.element("@" + attribute.getLocalName(), attribute.getNamespacePrefix(), null);

			String value = attribute.getValue();
			if(value.trim().length() > 0) {
				Attribute a = new Attribute(aObject);
				a.addMappingCase(0);
				a.getMappingCase(0).addMapping(new SimpleMapping(SimpleMapping.MAPPING_TYPE_CONSTANT, value));
				attributes.add(aObject);
			}
		}
		object.put(ELEMENT_ATTRIBUTES, attributes);
		
		for(int i = 0; i < elements.size(); i++) {
			JSONObject child = Mappings.jsonFromElement(elements.get(i));
			children.add(child);
		}
		object.put(ELEMENT_CHILDREN, children);
		
		return object;
	}
	
	/**
	 * Get the namespaces JSONObject.
	 * Keys of this object are the namespaces prefixes and values are the namespace URLs.
	 * 
	 * @return the namespaces JSONObject or null if it does not exist;
	 */
	public JSONObject getNamespaces() {
		if(this.has(TEMPLATE_NAMESPACES)) {
			//System.out.println("NAMESPACES: " + this.object.get(TEMPLATE_NAMESPACES));
			return this.getObject(TEMPLATE_NAMESPACES);
		}
		
		return null;
	}

	/**
	 * Get the namespace prefix for a namespace uri, if it exists in this mapping.
	 * 
	 * @param uri the requested uri
	 * @return the prefix or null if uri does not exist;
	 */
	public String getNamespacePrefix(String uri) {
		JSONObject namespaces = this.getNamespaces();
		Iterator<?> keys = namespaces.keySet().iterator();
		while(keys.hasNext()) {
			String key = (String) keys.next();
			if(namespaces.get(key).toString().compareTo(uri) == 0) {
				return key;
			}
		}
		return null;
	}
	
	public Element getHandlerForPath(String path) {
		ArrayList<Element> results = this.getHandlersForPath(path);
		if(results.size() > 0) return results.get(0);
		
		return null;
	}
	
	public ArrayList<Element> getHandlersForPath(String path) {
		if(this.hasGroups()) {
			if(path.startsWith("/")) { path = path.replaceFirst("/", ""); }
			String[] tokens = path.split("/", 2);

			if(tokens.length > 0) {
				JSONObject group = this.getGroup(tokens[0]);
				if(group != null) {
					JSONObject contents = (JSONObject) group.get("contents");
					return Element.getHandlersForPath(contents, path);
				}
			}			

		}
		
		return this.getTemplate().getHandlersForPath(path);
	}
	
	/**
	 * Gets a list of mapping handlers for requested element/attribute name.
	 * Searches are relative to the handler and return all requested elements/attributes regardless of path.
	 *  
	 * @param name the requested element/attribute name. Attribute names should begin with '@'.
	 * @return the list of handlers found
	 */
	public ArrayList<Element> getHandlersForName(String name) {
		ArrayList<Element> result = new ArrayList<Element>();

		if(this.has(TEMPLATE_TEMPLATE)) {
			JSONObject template = this.getObject(TEMPLATE_TEMPLATE);
			result.addAll(Element.getHandlersForName(template, name));
		}

		return result;	
	}
	
	/**
	 * Gets a list of mapping handlers for requested element/attribute prefix:name.
	 * Searches are relative to the handler and return all requested elements/attributes regardless of path.
	 *  
	 * @param prefix the requested element/attribute prefix.
	 * @param name the requested element/attribute name. Attribute names should begin with '@'.
	 * @return the list of handlers found
	 */
	public ArrayList<Element> getHandlersForPrefixAndName(String prefix, String name) {
		ArrayList<Element> result = new ArrayList<Element>();

		if(this.has(TEMPLATE_TEMPLATE)) {
			JSONObject template = this.getObject(TEMPLATE_TEMPLATE);
			result.addAll(Element.getHandlersForPrefixAndName(template, prefix, name));
		}

		return result;	
	}

	
	
	/*
	 * Old definitions for backwards compatibility
	 */
	
	/**
	 * Check if current mappings definition contains group data structures.
	 */
	public boolean hasGroups() {
		return this.has(TEMPLATE_GROUPS);
	}
	
	/**
	 * @param name element name of a group from configuration's "groups" array
	 * @return JSONObject for the requested group
	 */
	public JSONObject getGroup(String name) {
		if(this.has(TEMPLATE_GROUPS)) {
			JSONArray groups = this.getArray(TEMPLATE_GROUPS);
			Iterator<?> i = groups.iterator();
			while(i.hasNext()) {
				JSONObject group = (JSONObject) i.next();
				if(group.containsKey("element")) {
					if(group.get("element").toString().compareTo(name) == 0) {
						return group;
					}
				}
			}
		}

		return null;
	}

	/**
	 * @param name group name from configuration "groups" array
	 * @return handler for the requested group
	 */
	public JSONMappingHandler getGroupHandler(String name) {
		JSONObject group = this.getGroup(name);
		if(group != null) {
			JSONObject contents = (JSONObject) group.get("contents");
			return new JSONMappingHandler(contents);
		}
		
		return null;
	}
	
	/**
	 * Get a map of handlers for all mapping's groups.
	 * Key is the group's element.
	 * Must be called on top level mapping handler.
	 * @return
	 */
	public Map<String, JSONMappingHandler> getGroupHandlers() {
		HashMap<String, JSONMappingHandler> map = new HashMap<String, JSONMappingHandler>();

		if(this.has(TEMPLATE_GROUPS)) {
			JSONArray groups = this.getArray(TEMPLATE_GROUPS);
			for(int i = 0; i < groups.size(); i ++) {
				JSONObject group = (JSONObject) groups.get(i);
				JSONMappingHandler handler = new JSONMappingHandler(group);
				map.put(group.get("element").toString(), handler);
			}
		}
		
		return map;
	}
	
	public JSONArray getBookmarks() {
		if(this.has(TEMPLATE_BOOKMARKS)) return this.getArray(TEMPLATE_BOOKMARKS);
		else if(this.isTopLevelMapping()) {
			JSONArray bookmarks = new JSONArray();
			this.object.put(TEMPLATE_BOOKMARKS, bookmarks);
			return this.getBookmarks();
		} else return null;
	}
	
	/**
	 * Add bookmark of specified element to mapping object.
	 * Works only for top level mapping object.
	 * @param title the bookmark title
	 * @param id the element id
	 * @return self
	 */
	public JSONMappingHandler addBookmark(String title, String id) {
		if(!this.isTopLevelMapping()) return this;
		
		this.getBookmarks().add(MappingPrimitives.bookmark(title, id));
		
		return this;
	}
	
	/**
	 * Add bookmark of specified handler to mapping object.
	 * Works only for top level mapping object.
	 * @param title the bookmark title
	 * @param handler the element handler
	 */
	public JSONMappingHandler addBookmark(String title, Element handler) {
		return this.addBookmark(title, handler.getId());
	}
	
	/**
	 * Add bookmark of element with specified xpath to mapping object.
	 * Works only for top level mapping object.
	 * @param title the bookmark title
	 * @param xpath the element xpath
	 */
	public void addBookmarkForXpath(String title, String xpath) {
		Element element = this.getHandlerForPath(xpath);
		if(element != null && element.hasId()) {
			this.addBookmark(title, element.getId());
		}
	}

	public boolean hasMandatoryXPaths() {
		return this.has(ELEMENT_MANDATORY) && this.getArray(ELEMENT_MANDATORY).size() > 0;
	}
	
	public Mappings addMandatoryXPath(String string) {
		if(!this.has(ELEMENT_MANDATORY)) {
			this.put(ELEMENT_MANDATORY, new JSONArray());
		}
		
		this.getArray(ELEMENT_MANDATORY).add(string);
		
		return this;
	}

	public ArrayList<String> getMandatoryXPaths() {
		ArrayList<String> result = new ArrayList<String>();
		
		if(this.has(ELEMENT_MANDATORY)) {
			for(Object o: this.getArray(ELEMENT_MANDATORY)) {
				result.add((String) o);
			}
		}
		
		return result;
	}
	
	/**
	 * Generate a basic mapping template from the specified xml string.
	 * @param xml the xml string
	 * @return handler for the generated template
	 * @throws SAXException
	 * @throws ValidityException
	 * @throws ParsingException
	 * @throws IOException
	 */
	public static Mappings templateFromXML(String xml) throws SAXException, ValidityException, ParsingException, IOException {
		XMLReader parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader(); 
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Builder builder = new Builder(parser);
		Document document = builder.build( xml, null );
		
		JSONObject template = Mappings.jsonFromElement(document.getRootElement());
		Map<String, String> namespaces = XMLUtils.getNamespaceDeclarations(document); 

		JSONObject root = new JSONObject();
		root.put(JSONMappingHandler.TEMPLATE_TEMPLATE, template);
		root.put(JSONMappingHandler.TEMPLATE_NAMESPACES, namespaces);
		
		return new Mappings(root);
	}
	
	/**
	 * Get ids of elements of specified xpath, within this mappings object
	 * @param mappings
	 * @param xpath
	 * @return
	 */
	public Collection<String> getIdsForXPath(String xpath) {
		return this.getTemplate().getIdsForXPath(xpath);
	}

	/**
	 * Get ids of elements explicitly defined as mandatory, within this mappings object
	 * @return
	 */
	public Collection<String> getIdsForExplicitMandatoryXPaths() {
		Collection<String> result = new ArrayList<String>();
		Collection<String> mandatoryXPaths = this.getMandatoryXPaths();
		
		for(String mandatoryXPath: mandatoryXPaths) {
			result.addAll(this.getIdsForXPath(mandatoryXPath));
		}
		
		return result;
	}

	/*
	 * Conversion methods.
	 */
	
	/**
	 * Append every element in input mappings to this mappings' element by preserving hierarchy.
	 * Elements in input mapping that do not correspond to the original hierarchy, are omitted.
	 * Since this involves creation of new elements, all element ids of this mapping are removed and they should be recached.
	 * @param input
	 */
	public Mappings expand(Mappings input) {
		this.getTemplate().expand(input.getTemplate());
		return this;
	}

	/**
	 * Find all elements in this mapping that correspond to specified xpath.
	 * 
	 * <ul>
	 *  <li>'//' searches the same xpath in all descendants.</li>
	 *  <li>'/' at the beginning is ignored since all queries consider this Mappings as the root with it's Template as the only child.</li>
	 *  <li>'..' is not supported anywhere in the xpath.</li>
	 *  <li>simple predicates that check the existence of a descendant like [element],[element/subelement],[@attribute] are supported.</li>
	 *  <li>wildcard '*' is supported.</li>
	 * </ul>
	 * @param xpath
	 * @return list of elements that match the xpath.
	 */
	public List<Element> find(String xpath) {		
		boolean searchAll = false;
		if(xpath.startsWith("//")) searchAll = true;

		String searchXPath = xpath;
		if(xpath.startsWith("/")) searchXPath = xpath.replaceFirst("[/]*", "");

		String first = searchXPath;
		String rest = null;
		int slashIndex = searchXPath.indexOf("/");
		if(slashIndex > 0) {
			first = searchXPath.substring(0, slashIndex);
			rest = searchXPath.substring(slashIndex+1);
			if(rest.startsWith("/")) rest = "/" + rest;
		}

		//System.out.println("mapping: '" + first + "' - '" + rest + "'");
		//System.out.println(" - check: '" + this.getTemplate().getFullName() + "'");		

		ArrayList<Element> result = new ArrayList<Element>();
		if(this.getTemplate().getFullName().equals(first)) {
			if(rest != null) result.addAll(this.getTemplate().find(rest));
			else result.addAll(Collections.singletonList(this.getTemplate()));
		}
		
		if(searchAll) {
			result.addAll(this.getTemplate().find(xpath));
		}
		
		return result;
	}
	
	/**
	 * Get the first result that matches the xpath.
	 * @param xpath
	 * @return First result that matches the xpath or null if no matches where found.
	 */
	public Element findFirst(String xpath) {
		List<Element> result = this.find(xpath);
		if(result.size() > 0) return result.get(0);
		else return null;
	}
	
	/**
	 * Check if this xpath exists in this mapping.
	 * @param xpath
	 * @return true if any matching descendant is found.
	 */
	public boolean match(String xpath) {
		return this.find(xpath).size() > 0;	
	}
}
