package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.mapping.JSONMappingHandler;
import gr.ntua.ivml.mint.mapping.MappingCache;
import gr.ntua.ivml.mint.mapping.MappingConverter;
import gr.ntua.ivml.mint.mapping.MappingIndex;
import gr.ntua.ivml.mint.mapping.MappingPrimitives;
import gr.ntua.ivml.mint.util.JSONUtils;
import gr.ntua.ivml.mint.util.XMLUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class Element extends JSONMappingHandler {
	private Element parent;

	public Element(JSONObject element) {
		super(element);
	}
	
	public Element(String name, String prefix, String id) {
		super(MappingPrimitives.element(name, prefix, id));
	}
		
	/**
	 * Get index of attribute in the attributes array.
	 * @param id of the attribute element. 
	 * @return index of the attribute in the attributes array. -1 if child not found.
	 */
	public int getAttributeIndex(String id) {
		JSONArray attributes = this.getArray(Element.ELEMENT_ATTRIBUTES);
		if(attributes != null) {
			for(int i = 0; i < attributes.size(); i++) {
				JSONObject object = (JSONObject) attributes.get(i);
				Element attribute = new Element(object);
				if(attribute.has(ELEMENT_ID) && attribute.getId().equals(id)) {
					return i;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Get a handler for an attribute.
	 * @param attribute name (optionally starting with @)
	 * @return the attribute handler or null if no attribute was found.
	 */
	public Attribute getAttribute(String attributeName) {
		/*
		 * TODO: check code behaviour here
		 */
		if(attributeName.startsWith("@")) attributeName = attributeName.substring(1);
		String name = attributeName;
		String prefix = "";
		
		if(attributeName.contains(":")) {
			String[] tokens = attributeName.split(":");
			if(tokens.length > 2) return null;
			else if(tokens.length > 1) {
				prefix = tokens[0];
				name = tokens[1];
			}
		}
		
		/*
		 * TODO: update code here
		 */
		Collection<Element> attributes = this.getAttributes();
		for(Element o: attributes) {
			JSONObject a = o.asJSONObject();
			if(a.containsKey(ELEMENT_NAME) && a.get(ELEMENT_NAME).toString().equals("@" + name)) {
				if(a.containsKey(ELEMENT_PREFIX) && a.get(ELEMENT_PREFIX).toString().equals(prefix)) {
					return new Attribute(a);
				}
			}
		}
		
		return null;
	}

	/**
	 * Get a handler for an attribute ignoring namespace.
	 * @param attribute attribute name optionally starting with @
	 * @return the attribute handler or null if no attribute was found.
	 */
	public Attribute getAttributeByName(String attribute) {
		/*
		 * TODO: Check code behaviour here
		 */
		if(attribute.startsWith("@")) attribute = attribute.substring(1);
		String name = attribute;
		//String prefix = "";
		
		if(attribute.contains(":")) {
			String[] tokens = attribute.split(":");
			if(tokens.length > 2) return null;
			else if(tokens.length > 1) {
				//prefix = tokens[0];
				name = tokens[1];
			}
		}
		
		/*
		 * TODO: Update code behaviour here
		 */
		Collection<Element> attributes = this.getAttributes();
		for(Element o: attributes) {
			JSONObject a = o.asJSONObject();
			if(a.containsKey(ELEMENT_NAME) && a.get(ELEMENT_NAME).toString().equals("@" + name)) {
				return new Attribute(a);
			}
	}

		return null;
	}
	
	/**
	 * Get index of child in the child array.
	 * @param id of the child element. 
	 * @return index of the child in the child array. -1 if child not found.
	 */
	public int getChildIndex(String id) {
		JSONArray children = this.getArray(Element.ELEMENT_CHILDREN);
		if(children != null) {
			for(int i = 0; i < children.size(); i++) {
				JSONObject object = (JSONObject) children.get(i);
				Element child = new Element(object);
				if(child.has(ELEMENT_ID) && child.getId().equals(id)) {
					return i;
				}
			}
		}
		
		return -1;
	}

	/**
	 * Get a handler for a child.
	 * @param child name 
	 * @return the child handler or null if no child was found.
	 */
	public Element getChild(String child) {
		/*
		 * TODO: Check behaviour
		 */
		String name = child;
		String prefix = "";
		
		if(child.contains(":")) {
			String[] tokens = child.split(":");
			if(tokens.length > 2) return null;
			else if(tokens.length > 1) {
				prefix = tokens[0];
				name = tokens[1];
			}
		}
		
		/*
		 * TODO: update code here
		 */
		Collection<Element> children = this.getChildren();
		for(Element o: children) {
			JSONObject c = o.asJSONObject();
			if(c.containsKey(ELEMENT_NAME) && c.get(ELEMENT_NAME).toString().equals(name)) {
				if(c.containsKey(ELEMENT_PREFIX) && c.get(ELEMENT_PREFIX).toString().equals(prefix)) {
					return new Element(c);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Check if this element has any children
	 */
	public boolean hasChildren() {
		return (this.has(ELEMENT_CHILDREN) && this.getArray(ELEMENT_CHILDREN).size() > 0);
	}

	/**
	 * @return list of element's children
	 */
	public List<Element> getChildren() {
		if(this.hasChildren()) {
			return jsonArrayToElements(this.getArray(ELEMENT_CHILDREN));
		}
		
		return new ArrayList<Element>();
	}
	
	protected static ArrayList<Element> jsonArrayToElements(JSONArray array) {
		ArrayList<Element> results = new ArrayList<Element>();
		Iterator<?> i = array.iterator();
		while(i.hasNext()) {
			results.add(new Element((JSONObject) i.next()));
		}
		return results;
	}
	
	/**
	 * @return list of element's attributes.
	 */
	public List<Element> getAttributes() {
		if(this.has(ELEMENT_ATTRIBUTES)) {
			return jsonArrayToElements(this.getArray(ELEMENT_ATTRIBUTES));
		}
		
		return new ArrayList<Element>();
	}

	/**
	 * @return JSONArray of handler's mappings
	 */
	public ArrayList<SimpleMapping> getMappings() {
		return this.asMappingCase().getMappings();
	}
	
	public ArrayList<SimpleMapping> getMappings(MappingIndex index) {
		MappingCase aCase = this.getMappingCase(index);
		if(aCase != null) return aCase.getMappings();
		else return null;
	}
	
	/**
	 * Get a JSONArray of strings representing the handler's enumerations.
	 * @return JSONArray of handler's enumerations or null if no enumerations exist.
	 */
	public JSONArray getEnumerations() {
		if(this.has(ELEMENT_ENUMERATIONS)) {
			return this.getArray(ELEMENT_ENUMERATIONS);
		}
		
		return null;
	}

	/**
	 * Removes all handler's enumerations.
	 */
	public Element removeEnumerations() {
		if(this.has(ELEMENT_ENUMERATIONS)) {
			this.remove(ELEMENT_ENUMERATIONS);
		}
		
		return this;
	}
	
	/**
	 * Adds an enumeration.
	 * 
	 * @param enumeration the enumeration to be added.
	 */
	public Element addEnumeration(String enumeration) {
		if(!this.has(ELEMENT_ENUMERATIONS)) {
			this.put(ELEMENT_ENUMERATIONS, new JSONArray());
		}
		
		JSONArray enumerations = this.getEnumerations();
		enumerations.add(enumeration);
		
		return this;
	}
	
	/**
	 * Adds an enumeration with a label.
	 * 
	 * @param value the enumeration value to be added.
	 * @param label the label to be used.
	 */
	public Element addEnumeration(String value, String label) {
		if(!this.has(ELEMENT_ENUMERATIONS)) {
			this.put(ELEMENT_ENUMERATIONS, new JSONArray());
		}
		
		JSONArray enumerations = this.getEnumerations();
		JSONObject enumeration = new JSONObject();
		enumeration.put("label", label);
		enumeration.put("value", value);
		enumerations.add(enumeration);
		
		return this;
	}
	
	public Element addMapping(String type, String value) {
		this.addMapping(type, value, null);
		return this;
	}
		
	public Element addMapping(String type, String value, String annotation) {
		this.asMappingCase().addMapping(new SimpleMapping(type, value, annotation));
		return this;
	}

	/**
	 * Adds a constant mapping with specified value
	 * @param value constant value
	 */
	public Element addConstantMapping(String value) {
		this.addMapping(MAPPING_CONSTANT, value);
		return this;
	}
	
	/**
	 * Adds a constant mapping with specified value and annotation
	 * @param value constant value
	 */
	public Element addConstantMapping(String value, String annotation) {
		this.addMapping(MAPPING_CONSTANT, value, annotation);
		return this;
	}

	/**
	 * Sets a constant value mapping at the specified index. If index is -1, it will add a new constant value mapping
	 * if no mappings exist or replace the first existing mapping.
	 * @param value value of the constant mapping
	 * @param annotation annotation of the mapping, used as a label. (optional, set to null if no annotation exists).
	 * @param index index of the constant value mapping.
	 */
	public Element setConstantValueMapping(String value, String annotation, int index) {
		JSONObject target = this.object;
		JSONArray mappings = (JSONArray) target.get("mappings");
		JSONObject mapping = null;

		// if no mapping was added using -1 index, subsequent calls should edit the first entry (previously created)
		if(index == -1 && mappings.size() > 0) index = 0;
		if(index > -1) {
			mapping = (JSONObject) mappings.get(index);
			mapping.put("type", Element.MAPPING_CONSTANT);
			mapping.put("value", value);
			if(annotation != null) mapping.put(MAPPING_ANNOTATION, annotation);
		} else {
			mapping = new JSONObject();
			mapping.put("type", Element.MAPPING_CONSTANT);
			mapping.put("value", value);
			if(annotation != null) mapping.put(MAPPING_ANNOTATION, annotation);
			mappings.add(mapping);
		}
		
		return this;
	}
	
	/**
	 * Remove all mappings
	 */
	public Element removeMappings() {
		JSONArray mappings = this.getArray(Element.ELEMENT_MAPPINGS);

		mappings.clear();
		
		return this;
	}
	
	public Element removeMapping(MappingIndex index) {
		MappingCase aCase = this.getMappingCase(index);
		aCase.removeMapping(index);

		return this;
	}
	
	/**
	 * Adds an xpath mapping with specified value
	 * @param xpath the xpath mapping
	 */
	public Element addXPathMapping(String xpath) {
		this.addMapping(MAPPING_XPATH, xpath);
		return this;
	}
	
	/**
	 * Adds a parameter mapping with specified parameter
	 * @param xpath the xpath mapping
	 * @param annotation mapping annotation
	 */
	public Element addParameterMapping(String parameter, String annotation) {
		this.addMapping(MAPPING_PARAMETER, parameter, annotation);
		return this;
	}
	
	/**
	 * @return true if mapping is fixed inside the mapping editor.
	 */
	public boolean isFixed() {
		return this.has(ELEMENT_FIXED);
	}

	/**
	 * Sets the fixed property of a mapping. Fixed mappings cannot change using the mapping editor.
	 * @param f fixed property
	 */
	public Element setFixed(boolean f) {
		this.setTag(ELEMENT_FIXED, f);
		return this;
	}

	/**
	 * @return true if mapping is hidden inside the mapping editor.
	 */
	public boolean isHidden() {
		return this.has(ELEMENT_HIDDEN);
	}

	/**
	 * Sets the hidden property of a mapping. Hidden mappings are not shown in the mapping editor.
	 * @param h hidden property
	 */
	public Element setHidden(boolean h) {
		this.setTag(ELEMENT_HIDDEN, h);
		return this;
	}

	/**
	 * @return true if mapping is weak inside the mapping editor. Descendants with only weak mappings are not created.
	 */
	public boolean isWeak() {
		return this.has(ELEMENT_WEAK);
	}

	/**
	 * Sets the weak property of a mapping. Descendants with only weak mappings are not created.
	 * @param f weak property
	 */
	public Element setWeak(boolean w) {
		this.setTag(ELEMENT_WEAK, w);
		return this;
	}
	
	/**
	 * @return true if mapping is forced as mandatory.
	 */
	public boolean isMandatory() {
		return this.has(ELEMENT_MANDATORY);
	}
	
	/**
	 * @return true if mapping is forced as mandatory or has minOccurs > 0.
	 */
	public boolean isRequired() {
		return this.isMandatory() || this.getMinOccurs() > 0;
	}
	
	/**
	 * @return minOccurs of this handler. Returns 0 if minOccurs is not set;
	 */
	public int getMinOccurs() {
		int value = 0;
		if(this.has(ELEMENT_MINOCCURS)) {
			String mo = this.getString(ELEMENT_MINOCCURS);
			if(mo != null && mo.length() > 0);
			try {
				value = Integer.parseInt(mo);
			} catch(Exception e) {
			}
		}
		
		return value;
	}
	/**
	 * @return maxOccurs of this handler. Returns -1 if maxOccurs is not set;
	 */
	public int getMaxOccurs() {
		int value = -1;
		if (this.has(ELEMENT_MAXOCCURS)) {
			String mo = this.getString(ELEMENT_MAXOCCURS);
			if (mo != null && mo.length() > 0) {
				try {
					value = Integer.parseInt(mo);
				} catch(Exception e) {
				}
			}
		}
		return value;
	}
	
	/**
	 * Sets the mandatory property of a mapping forcing the mapping editor to consider it as mandatory.
	 * @param m mandatory property
	 */
	public Element setMandatory(boolean m) {
		this.setTag(ELEMENT_MANDATORY, m);
		return this;
	}
	
	/**
	 * True if element can be removed from the mapping editor.
	 * @return true if element can be removed from the mapping editor.
	 */
	public boolean isRemovable() {
		return this.has(ELEMENT_REMOVABLE);
	}
	
	/**
	 * Set removable state of this element. Removable elements can be removed by the user using the mapping editor.
	 * @param r removable state.
	 */
	public Element setRemovable(boolean r) {
		this.setTag(ELEMENT_REMOVABLE, r);
		return this;
	}

	/**
	 * True if handler is repeatable (ie. maxOccurs == unbounded).
	 * @return true if handler is repeatable, false otherwise. 
	 */
	public boolean isRepeatable() {
		if(this.has(ELEMENT_MAXOCCURS)) {
			int maxOccurs = Integer.parseInt(this.getString(ELEMENT_MAXOCCURS));
			if(!this.isAttribute() && maxOccurs < 0) return true;
		}
		
		return false;
	}

	/**
	 * Check if this element has a custom label set.
	 */
	public boolean hasLabel() {
		return this.has(ELEMENT_LABEL);
	}
	
	/**
	 * Gets a custom label set for this element.
	 * @return the custom label or null if none is set.
	 */
	public String getLabel() {
		if(this.has(ELEMENT_LABEL)) return this.getString(ELEMENT_LABEL);
		return null;
	}
	
	/**
	 * Sets a custom label for this element. Set to null to remove the custom label.
	 * @param label the custom label or null if the label is to be removed.
	 */
	public Element setLabel(String label) {
		if(label == null) {
			if(this.has(ELEMENT_LABEL)) this.remove(ELEMENT_LABEL);
		} else {
			this.put(ELEMENT_LABEL, label);
		}
		
		return this;
	}
 
	/**
	 * Gets a mapping handler for requested path.
	 * Path is relative to the mapping handler. If mapping handler is top level handler then searches
	 * are performed inside each group.
	 * Use if only one instance of this path exists or if you want the first.
	 *  
	 * @param path the requested path
	 * @return the handler or null if not found
	 */
	public Element getHandlerForPath(String path) {
		ArrayList<Element> handlers = this.getHandlersForPath(path);
		if(handlers.size() > 0) return handlers.get(0);
		return null;
	}
	
	/**
	 * Gets a list of mapping handlers for requested path.
	 * Path is relative to the mapping handler. If mapping handler is top level handler then searches
	 * are performed inside each group.
	 *  
	 * @param path the requested path
	 * @return the list of handlers found
	 */
	public ArrayList<Element> getHandlersForPath(String path) {
		return Element.getHandlersForPath(object, path);
	}

	protected static ArrayList<Element> getHandlersForPath(JSONObject object, String path) {
		ArrayList<Element> result = new ArrayList<Element>();
		
		if(path.startsWith("/")) { path = path.replaceFirst("/", ""); }
		String[] tokens = path.split("/", 2);
		
		if(tokens.length > 0) {
			if(object.containsKey(ELEMENT_NAME)) {
				if(tokens[0].equals(object.get(ELEMENT_NAME).toString())) {
					if(tokens.length == 1) {
						result.add(new Element(object));
					} else {
						String tail = tokens[1];
						if(tail.startsWith("@")) {
							if(object.containsKey(ELEMENT_ATTRIBUTES)) {
								return Element.getHandlersForPath((JSONArray) object.get(ELEMENT_ATTRIBUTES), tail);
							}
						} else {
							if(object.containsKey(ELEMENT_CHILDREN)) {
								return Element.getHandlersForPath((JSONArray) object.get(ELEMENT_CHILDREN), tail);
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	private static ArrayList<Element> getHandlersForPath(JSONArray array, String path) {
		ArrayList<Element> result = new ArrayList<Element>();

		Iterator<?> i = array.iterator();
		while(i.hasNext()) {
			JSONObject o = (JSONObject) i.next();
			result.addAll(Element.getHandlersForPath(o, path));
		}
		return result;
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

		if(this.getOptString(ELEMENT_NAME).compareTo(name) == 0) {
			result.add(this);
		}

		result.addAll(Element.getHandlersForName(this.getAttributes(), name));
		result.addAll(Element.getHandlersForName(this.getChildren(), name));

		return result;	
	}
	
	public boolean checkPrefixAndName(Element element) {
		return this.checkPrefixAndName(element.getPrefix(), element.getName());
	}
	
	public boolean checkPrefixAndName(String prefix, String name) {
		if((this.has("prefix") && prefix != null && this.getOptString("prefix") != null && this.getOptString("prefix").compareTo(prefix) == 0) || (!this.has("prefix") && prefix != null)) {
			if(this.getOptString("name").compareTo(name) == 0 && this.has("prefix") && this.getOptString("prefix").compareTo(prefix) == 0) {
				return true;
			}				
		}

		return false;
	}
	
	/**
	 * Gets a mapping handler for requested name.
	 * Searches are relative to the handler and return all requested elements/attributes regardless of path.
	 * Use if only one instance of this name exists or if you want the first.
	 *  
	 * @param path the requested path
	 * @return the handler or null if not found
	 */
	public Element getHandlerForName(String name) {
		ArrayList<Element> handlers = this.getHandlersForName(name);
		if(handlers.size() > 0) return handlers.get(0);
		return null;
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

		result.addAll(Element.getHandlersForPrefixAndName(this.getAttributes(), prefix, name));
		result.addAll(Element.getHandlersForPrefixAndName(this.getChildren(), prefix, name));

		if(this.getChildren() != null && !this.isAttribute()) {
			for(Element handler: result) {
				if(handler.getParent() == null) {
					handler.setParent(this);
				} else {
					Element parent = handler;
					while(parent.getParent() != null) {
						parent = parent.getParent();
					}
					
					if(this != parent) parent.setParent(this);
				}
			}
		}
		
		if(checkPrefixAndName(prefix, name)) result.add(this);

		return result;	
	}

	private static ArrayList<Element> getHandlersForPrefixAndName(Collection<Element> list, String prefix, String name) {
		ArrayList<Element> result = new ArrayList<Element>();
		for(Element e: list) {
			result.addAll(Element.getHandlersForPrefixAndName(e.asJSONObject(), prefix, name));
		}		
		return result;
	}
	
	/**
	 * Gets a mapping handler for requested prefix:name.
	 * Searches are relative to the handler and return all requested elements/attributes regardless of path.
	 * Use if only one instance of this name exists or if you want the first.
	 *  
	 * @param prefix the requested prefix
	 * @param name the requested name
	 * @return the handler or null if not found
	 */
	public Element getHandlerForPrefixAndName(String prefix, String name) {
		ArrayList<Element> handlers = this.getHandlersForPrefixAndName(prefix, name);
		if(handlers.size() > 0) return handlers.get(0);
		return null;
	}
	
	public Element getParent() {
		return this.parent;
	}

	private Element setParent(Element element) {
		this.parent = element;
		return this;
	}

	protected static ArrayList<Element> getHandlersForPrefixAndName(JSONObject object, String prefix, String name) {
		return new Element(object).getHandlersForPrefixAndName(prefix, name);
	}
	
	protected static ArrayList<Element> getHandlersForName(JSONObject object, String name) {
		return new Element(object).getHandlersForName(name);
	}
	
	private static ArrayList<Element> getHandlersForName(Collection<Element> list, String name) {
		ArrayList<Element> result = new ArrayList<Element>();

		for(Element o: list) {
			result.addAll(Element.getHandlersForName(o.asJSONObject(), name));			
		}
		
		return result;
	}
	
	@Deprecated
	/**
	 * Duplicates an element for the given path.
	 * Duplicate element is placed after original element to preserve element order.
	 * All ids will be removed from duplicate element and it's descendants.
	 * @param path the path of the element to be duplicated, relative to the handler
	 * @return handler for the created element or null if path was not found 
	 */
	public Element duplicatePath(String path) {
		return this.duplicatePath(path, null);
	}
	
	@Deprecated
	/**
	 * Duplicates an element for the given path.
	 * Duplicate element is placed after original element to preserve element order.
	 * Provided cache will be used to assign ids to duplicate element and it's descendants.
	 * @param path the path of the element to be duplicated, relative to the handler.
	 * @param cache Cache that will handle ids for generated elements.
	 * @return handler for the created element or null if path was not found 
	 */
	public Element duplicatePath(String path, MappingCache cache) {
		if(!path.startsWith("/")) path = "/" + path;

		String[] parts = path.split("/");
		StringBuffer buffer = new StringBuffer();
		
		// if path is not a child of this handler delegate duplication to the appropriate child
		if(parts.length > 3) {
			for(int i = 1; i < parts.length - 1; i++) {
				buffer.append("/" + parts[i]);
			}
			Element child = this.getHandlerForPath(buffer.toString());
			return child.duplicatePath("/" + parts[parts.length - 2] + "/" + parts[parts.length - 1], cache);
		// else duplicate child, add to children and return
		} else {
			Element original = this.getHandlerForPath(path);
			if(!original.isAttribute()) {
				try {
					JSONObject duplicate = JSONUtils.parse(original.toString());
					duplicate.put("__duplicate", "");
					
					int originalIndex = -1;
					int i = 0;
					Collection<Element> children = this.getChildren();
					for(Element child: children) {
						i++;
						if(child.checkPrefixAndName(original)) {
							originalIndex = i;
						}
					}
					
					this.addChild(originalIndex, new Element(duplicate));
	
					for(Object o: children) {
						JSONObject c = (JSONObject) o;
						if(c.containsKey("__duplicate")) {
							c.remove("__duplicate");
	
							System.out.println(c);
							Element.stripIds(c);
							
							System.out.println(cache);
							System.out.println(c);
							if(cache != null) {
								cache.cacheElement(c, this.object);
							}
							System.out.println(c);
							return new Element(c);											
						}
					}
				} catch (net.minidev.json.parser.ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public Element addChild(int index, Element child) {
		this.addAtIndex(index, ELEMENT_CHILDREN, child);
		return this;
	}

	/**
	 * Strip all ids from element and descendants
	 * @param element
	 */
	public static void stripIds(JSONObject element) {
		element.put(ELEMENT_ID, "");
		if(element.containsKey(ELEMENT_CHILDREN)) {
			JSONArray children = (JSONArray) element.get(ELEMENT_CHILDREN);
			for(int i = 0; i < children.size(); i++) {
				Element.stripIds((JSONObject) children.get(i));
			}
		}

		if(element.containsKey(ELEMENT_ATTRIBUTES)) {
			JSONArray attributes = (JSONArray) element.get(ELEMENT_ATTRIBUTES);
			for(int i = 0; i < attributes.size(); i++) {
				Element.stripIds((JSONObject) attributes.get(i));
			}
		}
	}
	
	/**
	 * Set a property to a value recursively for this element and its descendants.
	 */
	public Element setRecursive(String key, Object value) {
		this.put(key, value);
		for(Element all: this.getChildrenAndAttributes()) {
			all.setRecursive(key, value);
		}
		
		return this;
	}
	
	/** 
	 * Remove all mappings for this element and it's descendants.
	 */
	public void clearMappingsRecursive() {
		if(this.getMappings() != null) {
			this.asMappingCase().clearMappings();
		}
		
		for(MappingCase aCase: this.getMappingCases()) {
			aCase.clearMappings();
		}
		
		for(Element e: this.getChildrenAndAttributes()) {
			e.clearMappingsRecursive();
		}
	}
	
	public ArrayList<Element> getChildrenAndAttributes() {
		ArrayList<Element> results = new ArrayList<Element>();
		results.addAll(this.getChildren());
		results.addAll(this.getAttributes());
		return results;
	}

	/**
	 * Add a empty mapping case 
	 * @param index. Order of the new mapping case in the cases array. Uses the caseIndex index field.
	 */
	public Element addMappingCase(MappingIndex index) {
		return this.addMappingCase(index.getCaseIndex());
	}

	/**
	 * Add a empty mapping case at the specified index.
	 * @param index. Order of the new mapping case in the cases array.
	 */
	public Element addMappingCase(int index) {
		JSONArray cases = this.getArray(ELEMENT_CASES, true);
		
		MappingCase aCase = new MappingCase();
		int i = index + 1;
		
		if(cases.size() > i) {
			cases.add(i, aCase.asJSONObject());
		} else cases.add(aCase.asJSONObject());
		
		return this;
	}
	
	/**
	 * Add a mapping case at the end of this element's mapping case list.
	 * @param aCase mapping case to add.
	 * @return this element.
	 */
	public Element addMappingCase(MappingCase aCase) {
		JSONArray cases = this.getArray(ELEMENT_CASES, true);
		cases.add(aCase.asJSONObject());
		return this;
	}
	
	/**
	 * Add a collection of mapping cases at the end of this element's mapping case list.
	 * @param cases collection of mapping cases to add.
	 * @return this element.
	 */
	public Element addMappingCases(Collection<MappingCase> cases) {
		for(MappingCase aCase: cases) {
			this.addMappingCase(aCase);
		}
		return this;
	}

	/**
	 * Remove a mapping case 
	 * @param index. Order of the mapping case that should be removed. Uses the caseIndex index field.
	 */
	public Element removeMappingCase(MappingIndex index) {
		return this.removeMappingCase(index.getCaseIndex());
	}

	/**
	 * Remove a mapping case at the specified index.
	 * @param index. Order of the mapping case that should be removed.
	 */
	public Element removeMappingCase(int index) {
		if(this.has(ELEMENT_CASES)) {
			this.getArray(ELEMENT_CASES).remove(index);
		}
		return this;
	}

	/**
	 * Get a default mapped value that should be used if no mappings are set on this handler.
	 * @return get default value assigned for this handler or null if it is not set.
	 */
	public String getDefault() {
		return this.getString(ELEMENT_DEFAULT);
	}
	
	/**
	 * Check if this handler has a default value assigned.
	 * @return true if this handler has a default value assigned
	 */
	public boolean hasDefault() {
		return this.has(ELEMENT_DEFAULT);
	}
	
	/**
	 * Generate an XML Element with the contents of this handler.
	 * @return
	 */
	public nu.xom.Element toElement(Map<String, String> namespaceDeclarations) {
		nu.xom.Element root = null;
		String uri = null;
		if(this.getPrefix() != null && namespaceDeclarations != null && namespaceDeclarations.containsKey(this.getPrefix())) {
			uri = namespaceDeclarations.get(this.getPrefix());
		}		
		if(this.getPrefix() != null && this.getPrefix().equalsIgnoreCase("xml")) uri = XMLUtils.XML_NAMESPACE;

		if(uri != null) {
			root = new nu.xom.Element(this.getFullName(), uri);
		} else {
			root = new nu.xom.Element(this.getName());			
		}
				
//		System.out.println("URI: " + this.getPrefix() + " = " + uri);

		if(namespaceDeclarations != null) {
			for(String prefix: namespaceDeclarations.keySet()) {
				root.addNamespaceDeclaration(prefix, namespaceDeclarations.get(prefix));
			}
		}
		if(this.hasMappingsRecursive()) {
			if(this.hasMappings()) {
				List<SimpleMapping> mappings = this.getAllMappings(SimpleMapping.MAPPING_TYPE_CONSTANT);
				if(mappings.size() > 0) root.appendChild(mappings.get(0).getValue());
			}
			
			for(Element element: this.getChildren()) {
				if(element.hasMappingsRecursive()) root.appendChild(element.toElement(namespaceDeclarations));
			}
			
			for(Element attribute: this.getAttributes()) {
				if(attribute.hasMappingsRecursive()) {
					nu.xom.Attribute at = attribute.asAttribute().toAttribute(namespaceDeclarations);
					if(at != null) root.addAttribute(at);				
				}
			}			
		}

		return root;
	}
	
	private Attribute asAttribute() {
		return new Attribute(this.asJSONObject());
	}

	/**
	 * Assing a thesaurus to this mapping element.
	 * @param thesaurus the thesaurus uri
	 */
	public void setThesaurus(JSONObject thesaurus) {
		this.setObject(ELEMENT_THESAURUS, thesaurus);
	}
	
	/**
	 * Get the thesaurus object.
	 * @return
	 */
	public JSONObject getThesaurus() {
		return this.getObject(ELEMENT_THESAURUS);
	}
	
	/**
	 * Remove thesaurus reference.
	 */
	public void removeThesaurus() {
		this.remove(ELEMENT_THESAURUS);
	}
	
	/**
	 * Get handler's version 2.0- mapping condition object
	 * @return the condition JSONObject or null if it does not exist.
	 */
	public Condition getCondition() {
		if(this.has(ELEMENT_CONDITION)) {
			return new Condition(this.getObject(ELEMENT_CONDITION));
		}
		return null;
	}
	

	public Condition getCondition(int index) {
		MappingCase aCase = this.getMappingCase(index);
		if(aCase != null) return aCase.getCondition();
		return null;
	}
	
	public Condition getCondition(MappingIndex index) {
		MappingCase aCase = this.getMappingCase(index);
		if(aCase != null) return aCase.getCondition();

		return this.getCondition(index.getCaseIndex());
	}

	/**
	 * Generic method that sets a handler's version 2.0- mapping condition. Condition is removed if null value is passed.
	 * @param condition the condition JSONObject
	 */
	public Element setCondition(JSONObject condition) {
		if(condition != null) {
			this.put(ELEMENT_CONDITION, condition);
		} else {
			this.object.remove(ELEMENT_CONDITION);
		}

		return this;
	}
	
	/**
	 * Generic method that sets a mapping case's condition. Condition is removed if null value is passed.
	 * @param condition the condition JSONObject
	 */
	public Element setCondition(MappingIndex index, Condition condition) {
		MappingCase aCase = this.getMappingCase(index);
		aCase.setCondition(condition);

		return this;
	}
	
	public boolean initComplexCondition(MappingIndex index) {
		Condition condition = this.getCondition(index);
		if(condition.initComplexCondition()) {
			this.setCondition(index, condition);
			return true;
		}
		
		return false;
	}

	/**
	 * Set a clause key/value pair on a condition specified by index. 
	 * @param index. Set index caseId and path to access the clause.
	 * @param key. clause key to be set.
	 * @param value. value of clause key.
	 * @return self
	 */
	public Element setConditionClauseKey(MappingIndex index, String key, String value) {
		String path = index.getPath();
		Condition condition = null;
		
		if(path.startsWith(ELEMENT_STRUCTURAL)) {
			condition = this.getStructuralCondition();
			path = path.replaceFirst(ELEMENT_STRUCTURAL + ".", "");
		}
		else condition = this.getCondition(index);
		
		if(condition != null) condition.setClauseKey(path, key, value);
		
		return this;
	}
	
	public Element removeConditionClauseKey(MappingIndex index, String key) {
		Condition condition = this.getCondition(index);
		condition.removeClauseKey(index.getPath(), key);
		return this;
	}


	/**
	 * Remove a condition clause based on index.
	 * @param index. Set caseId and path to specify condition/clause.
	 * @return self
	 */
	public Element removeConditionClause(MappingIndex index) {
		Condition condition = this.getCondition(index);
		condition.removeClause(index.getPath());

		return this;
	}
	
	/**
	 * Remove a condition clause based on index.
	 * @param index. Set caseId and path to specify condition/clause.
	 * @param complex. True if clause is complex (i.e has subclauses joined with logical operator)
	 * @return self
	 */
	public Element addConditionClause(MappingIndex index, boolean complex) {
		Condition condition = this.getCondition(index);
		condition.addClause(index.getPath(), complex);
		
		return this;
	}
	
	
	/**
	 * Get element's name
	 * @return element's name.
	 */
	public String getName() {
		return this.getString(ELEMENT_NAME);
	}

	/**
	 * Get element's prefix
	 * @return element's prefix.
	 */
	public String getPrefix() {
		return this.getString(ELEMENT_PREFIX);
	}
	
	/**
	 * Get handler's element full name. Delegates to getFullName(true).
	 * @see getFullName
	 */
	public String getFullName() {
		return this.getFullName(true);
	}

	/**
	 * Get handler's element full name. Full name contains element's name with the element's prefix
	 * if exists.
	 * @param useAttributeCharacter Adds '@' on attribute names.
	 * @return element full name.
	 */
	public String getFullName(boolean useAttributeCharacter) {
		String name = null;
		String prefix = null;
		
		if(this.has(ELEMENT_NAME)) name = this.getString(ELEMENT_NAME).replace("@", "");
		if(this.has(ELEMENT_PREFIX)) prefix = this.getString(ELEMENT_PREFIX);
		
		String label = ((this.isAttribute() && useAttributeCharacter)?"@":"") + ((prefix != null)?prefix+":":"") + name;

		return label;
	}
	
	/**
	 * Check if this mapping has any mappings
	 * @return true if this mapping has any mappings.
	 */
	public boolean hasMappings() {
		if(this.hasMappingCases()) {
			for(MappingCase aCase: this.getMappingCases()) {
				if(aCase.hasMappings()) return true;
			}
		} else if(this.hasArrayWithContents(ELEMENT_MAPPINGS)) return true;

		return false;
	}
	
	/**
	 * Check if this mapping or any descendants have mappings.
	 * @return true if this mapping or its descendants have mappings.
	 */
	public boolean hasMappingsRecursive() {
		if(this.hasMappings()) return true;
		ArrayList<Element> all = this.getChildrenAndAttributes();
		for(Element child: all) {
			if(child.hasMappingsRecursive()) return true;
		}
		
		return false;
	}

	public static final int RECURSE_NONE = 0;
	public static final int RECURSE_ALL = 1;
	public static final int RECURSE_CHILDREN = 2;
	public static final int RECURSE_ATTRIBUTES = 4;
	public List<String> mandatoryMappings() {
		return this.mandatoryMappings(RECURSE_ALL);
	}
	
	public List<String> mandatoryMappings(int recurseOption) {
		ArrayList<String> results = new ArrayList<String>();
		
		if(this.isRequired()) {
			if(this.hasMappingCases()) {
				for(MappingCase aCase: this.getMappingCases()) {
					for(SimpleMapping mapping: aCase.getMappings()) {
						if(mapping.isXPath()) results.add(mapping.getValue());
					}				
				}
			} else {
				for(SimpleMapping mapping: this.asMappingCase().getMappings()) {
					if(mapping.isXPath()) results.add(mapping.getValue());
				}								
			}
		}
		
		if(recurseOption != RECURSE_NONE) {
			if(recurseOption != RECURSE_ATTRIBUTES) {
				for(Element element: this.getChildren()) {
					results.addAll(element.mandatoryMappings());
				}
			}
	
			if(recurseOption != RECURSE_CHILDREN) {
				for(Element element: this.getAttributes()) {
					results.addAll(element.mandatoryMappings());
				}
			}
		}
		
		return results;
	}
		
	/**
	 * Return true if this element has any mapping cases defined.
	 * @return
	 */
	public boolean hasMappingCases() {
		return (this.has(ELEMENT_CASES) && this.getArray(ELEMENT_CASES).size() > 0);
	}
	
	/**
	 * Get json array of mapping cases.
	 * @return JSONArray of mapping cases (element key: ELEMENT_CASES).
	 */
	public ArrayList<MappingCase> getMappingCases() {
		ArrayList<MappingCase> result = new ArrayList<MappingCase>();
		if(this.hasMappingCases()) {
			for(Object o: this.getArray(ELEMENT_CASES)) {
				result.add(new MappingCase((JSONObject) o));
			}
		}
		
		return result;
	}	
	
	/**
	 * Replace this element's mapping cases with cases in input list
	 * @parameter input list of mapping cases.
	 * @return this element.
	 */
	public Element setMappingCases(List<MappingCase> input) {
		this.put(ELEMENT_CASES, JSONUtils.jsonList(input));
		return this;
	}
	
	/**
	 * Return MappingCase of this element: a group of mappings and an optional condition.
	 * @param index
	 * @return
	 */
	public MappingCase getMappingCase(int index) {
		return this.getMappingCase(index, false);
	}

	/**
	 * Return MappingCase of this element:: a group of mappings and an optional condition.
	 * @param index
	 * @param create create mapping case if it does not exist.
	 * @return
	 */
	public MappingCase getMappingCase(int index, boolean create) {
		MappingCase aCase = null;
		
		if(index == -1) return this.asMappingCase();
		else if(this.hasMappingCases()) {
			JSONArray cases = this.getArray(ELEMENT_CASES);
			
			if(index < cases.size()) {
				JSONObject c = (JSONObject) cases.get(index);
				return new MappingCase(c);
			}
			
		}
		
		if(aCase == null && create) {
			// if mapping case does not exist create it at specified index
			if(index == -1) index = 0;
			this.addMappingCase(index);
			return this.getMappingCase(index);
		}
				
		return aCase;
	}
	
	/**
	 * Return JSONObject representing a mapping-case: a group of mappings and an optional condition.
	 * @param index
	 * @return
	 */
	public MappingCase getMappingCase(MappingIndex index) {
		/*
		 * TODO: generalize for any key (now works for structural)
		 */
		if(index.getKey() != null && index.getKey().equals(ELEMENT_STRUCTURAL)) {
			return this.getStructural();
		}
		
		return this.getMappingCase(index.getCaseIndex());
	}

	public boolean isStructural() {
		return this.hasChildren();
	}

	public Element setStructuralMapping(String xpath) {
		MappingCase structural = this.getStructural();
		if(structural == null) {
			this.setStructural(new MappingCase());
			structural = this.getStructural();
		}
		
		structural.clearMappings();
		structural.addMapping(new SimpleMapping(SimpleMapping.MAPPING_TYPE_XPATH, xpath));
		
		return this;
	}

	public Element addMapping(MappingIndex index, SimpleMapping mapping) {
		MappingCase aCase = this.getMappingCase(index);
		if(aCase != null) {
			aCase.setMapping(index, mapping);
		}
		return this;
	}

	public SimpleMapping getMapping(MappingIndex index) {
		MappingCase aCase = null;
		if(index.getCaseIndex() == -1) aCase = this.asMappingCase();
		else aCase = this.getMappingCase(index);
		
		if(aCase != null) {
			return aCase.getMapping(index);
		}
		
		return null;
	}

	public Element removeChild(String id) {
		if(!this.has(ELEMENT_CHILDREN)) return null;
		Element element = null;
		
		int targetIndex = -1;
		JSONArray children = this.getArray(ELEMENT_CHILDREN);
		for(int i = 0; i < children.size(); i++) {
			JSONObject child = (JSONObject) children.get(i);
			if(child.get("id").toString().equals(id)) {
				targetIndex = i;
				element = new Element(child);
			}
		}

		if(targetIndex >= 0) {
			children.remove(targetIndex);
	    }
		
		return element;
	}
	
	public void removeChildren(String name) {
		if (this.has(ELEMENT_CHILDREN)) {
			int targetIndex = -1;
			JSONArray children = this.getArray(ELEMENT_CHILDREN);
			for(int i = 0; i < children.size(); i++) {
				JSONObject child = (JSONObject) children.get(i);
				if(child.get("name").toString().equals(name)) {
					targetIndex = i;
					children.remove(targetIndex);
				}
			}
		}
	}

	public Element removeStructuralMapping() {
		MappingCase structural = this.getStructural();
		if(structural != null) {
			structural.clearMappings();
		}
		
		return this;
	}
	
	public boolean hasStructural() {
		return this.has(ELEMENT_STRUCTURAL); 
	}

	public MappingCase getStructural() {
		if(this.has(ELEMENT_STRUCTURAL)) {
			return new MappingCase(this.getObject(ELEMENT_STRUCTURAL));
		}
		return null;
	}
	
	public Element setStructural(MappingCase structural) {
		this.put(ELEMENT_STRUCTURAL, structural.asJSONObject());
		return this;
	}

	public Condition getStructuralCondition() {
		MappingCase structural = this.getStructural();
		if(structural != null) return structural.getCondition();
		else return null;
	}
	
	public Element setStructuralCondition(Condition condition) {
		if(!this.has(ELEMENT_STRUCTURAL)) {
			this.setStructural(new MappingCase());
		}
		
		this.getStructural().setCondition(condition);
		
		return this;
	}

	public Element setStructuralMapping(SimpleMapping mapping) {
		if(!this.has(ELEMENT_STRUCTURAL)) {
			this.setStructural(new MappingCase());
		}
		
		this.getStructural().addMapping(mapping);
		
		return this;
	}

	/**
	 * Check if this elements conforms to version 1.0 data structure, for backwords compatibility.
	 * @return
	 */
	public boolean isVersion10() {
		return (!this.has(ELEMENT_CASES));
	}

	/**
	 * Get all mappings from all mapping cases;
	 * @return
	 */
	public ArrayList<SimpleMapping> getAllMappings() {
		return this.getAllMappings(null);
	}
	
	/**
	 * Get all mappings of specified type from all mapping cases;
	 * @param type mapping type;
	 * @return
	 */
	public ArrayList<SimpleMapping> getAllMappings(String type) {
		ArrayList<SimpleMapping> mappings = new ArrayList<SimpleMapping>();
		
		if(this.hasMappingCases()) {
			for(MappingCase aCase: this.getMappingCases()) {
				mappings.addAll(aCase.getMappings(type));
			}
		} else if(this.hasMappings()) {
			return this.asMappingCase().getMappings(type);
		}
		
		return mappings;
	}

	/**
	 * Check if this element has any attributes
	 */
	public boolean hasAttributes() {
		return (this.has(ELEMENT_ATTRIBUTES) && this.getArray(ELEMENT_ATTRIBUTES).size() > 0);
	}

	/**
	 * Check if this element is missing. Missing elements are required, have no mappings and have no default value.
	 * @return true if this element is required, has no mappings and no default value.
	 */
	public boolean isMissing() {
		return this.isRequired() && !this.hasMappingsRecursive() && !this.hasDefault();
	}
	
	/**
	 * Get ids of specified xpath relative to this element.
	 * @param xpath
	 * @return
	 */
	public Collection<String> getIdsForXPath(String xpath) {
		Collection<String> result = new ArrayList<String>();
		
		String[] tokens = xpath.split("/", 3);
		if(tokens.length > 1) {
			String theName = tokens[1];
			String name = this.getName();
			if(name != null) {
				String id = this.getId();
				if(name.equals(theName)) {
					result.add(id);
					
					if(tokens.length > 2) {
						String tail = tokens[2];
						Collection<Element> children = null;
						if(tail.startsWith("@")) {
							children = this.getAttributes();
						} else {
							children = this.getChildren();
						}
						
						for(Element child: children) {
							Collection<String> childIds = child.getIdsForXPath("/" + tail);
							if(!childIds.isEmpty()) {
								result.addAll(childIds);
								break;
							}							
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Create an exact copy of this element and return it.
	 * @return an exact copy of this element.
	 */
	public Element duplicate() {
		Element duplicate = new Element(super.duplicate().asJSONObject());
		duplicate.setRecursive(ELEMENT_ID, "");
		return duplicate;
	}

	/**
	 * Append every element in input's descentants list to this element's hierarchy by preserving structure.
	 * Elements in input that do not correspond to the original hierarchy, are omitted.
	 * Since this involves creation of new elements, all element ids of this mapping are removed and they should be recached.
	 * @param input
	 */
	public void expand(Element input) {
		if(input.hasMappingCases()) {
			this.setMappingCases(input.getMappingCases());
		}

		if(this.hasChildren()) {			
			expand(this.getArray(ELEMENT_CHILDREN), input.getChildren());
		}
		
		if(this.hasAttributes()) {
			expand(this.getArray(ELEMENT_ATTRIBUTES), input.getAttributes());
		}
		
		this.setId("");
	}
	
	private void expand(JSONArray children, Collection<Element> inputChildren) {
		// get a map of elements with all children names as keys.
		HashMap<String, List<Element>> inputMap = MappingConverter.elementMap(inputChildren);
		
		// get all original children as a JSONArray. We want to modify this array directly
		for(int i = 0; i < children.size(); i++) {
			Element child = new Element((JSONObject) children.get(i));
			String fullname = child.getFullName();
			
			// proceed only if input has elements with this name.
			if(inputMap.containsKey(fullname)) {
				List<Element> inputElements = inputMap.get(fullname);
				
				if(inputElements.size() == 1) { // if only one element exists with this name, expand original with this input.
					child.expand(inputElements.get(0));
				} else { // else create a copy for each input element, expand with child element and add.
					for(Element inputChild: inputMap.get(fullname)) {						
	 					Element copy = child.duplicate();
	 					copy.expand(inputChild);
						children.add(i, copy.asJSONObject()); // add child
						i++; // increase counter
					} 						
				}					
			}
		}
	}
	
	/*
	 *	XPath access methods. 
	 */
	
	/**
	 * Find all elements in this mapping that correspond to specified xpath.
	 * 
	 * <ul>
	 *  <li>'//' searches the same xpath in all descendants.</li>
	 *  <li>'/' and '..' are not supported, since there parent information might be missing.</li>
	 *  <li>simple predicates that check the existence of a descendant like [element],[element/subelement],[@attribute] are supported.</li>
	 *  <li>wildcard '*' is supported.</li>
	 * </ul>
	 * 
	 * @param xpath
	 * @return list of elements that match the xpath.
	 */
	public List<Element> find(String xpath) {
		ArrayList<Element> result = new ArrayList<Element>();
		
		boolean searchAll = false;
		boolean isAbsolute = false;
		if(xpath.startsWith("//")) searchAll = true;
		else if(xpath.startsWith("/")) isAbsolute = true;

		String searchXPath = xpath;
		if(xpath.startsWith("/")) searchXPath = xpath.replaceFirst("[/]*", "");

		String first = searchXPath;
		String predicate = null;
		String rest = null;
		int slashIndex = searchXPath.indexOf("/");
		if(slashIndex > 0) {
			first = searchXPath.substring(0, slashIndex);
			rest = searchXPath.substring(slashIndex+1);
			if(rest.startsWith("/")) rest = "/" + rest;
		}

		int predicateIndex = first.indexOf("[");
		if(predicateIndex > 0) {
			if(first.lastIndexOf("]") > 0) predicate = first.substring(predicateIndex+1, first.lastIndexOf("]"));
			else System.err.println("Warning: syntax error, expected ']' at component: " + first);
			first = first.substring(0, predicateIndex);
		}
		//System.out.println("element " + this.getFullName() + ": '" + first + "' - '" + predicate + "' - '" + rest + "'");

		if(first.equals(".")) {
			if(rest == null) return Collections.singletonList(this);
			else return this.find(rest);
		} else if(first.equals("..")) {
			System.err.println("Warning: '..' is not supported as xpath component in Element.find(): " + xpath);
		} else if(isAbsolute) {
			System.err.println("Warning: Absolute xpaths are not supported in Element.find(): " + xpath);
		} else if(first.startsWith("@") && rest == null) {
			boolean matchAll = first.equals("@*");
			for(Element attribute: this.getAttributes()) {
				//System.out.println(" - check: '" + attribute.getFullName() + "'");		
				if(matchAll || attribute.getFullName().equals(first)) {
					result.add(attribute);
				}
			}
		} else {
			boolean matchAll = first.equals("*");
			for(Element child: this.getChildren()) {
				//System.out.println(" - check: '" + child.getFullName() + "'");		
				if(matchAll || child.getFullName().equals(first)) {
					if(predicate == null || child.match(predicate)) {
						if(rest == null) result.add(child);
						else result.addAll(child.find(rest));
					}
				}
			}
		}
		
		if(searchAll) {
			for(Element all: this.getChildrenAndAttributes()) {
				result.addAll(all.find(xpath));
			}
		}
		
		return result;
	}
	
	/**
	 * Get the first result that matches the xpath.
	 * @param xpath
	 * @return First result that matches the xpath or null if no mathces where found.
	 */
	public Element findFirst(String xpath) {
		List<Element> result = this.find(xpath);
		if(result.size() > 0) return result.get(0);
		else return null;
	}
	
	/**
	 * Check if this xpath has any matches for this element.
	 * @param xpath
	 * @return true if any matching descendant is found.
	 */
	public boolean match(String xpath) {
		return this.find(xpath).size() > 0;	
	}
}
