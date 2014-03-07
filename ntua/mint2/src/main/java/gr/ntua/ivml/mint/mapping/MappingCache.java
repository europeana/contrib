package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.Mappings;

import java.util.HashMap;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Cache for elements inside a mapping.
 * 
 * @author Fotis Xenikoudakis
 */
public class MappingCache {
	private int elementid = 0;
	private JSONObject template = null;
	private HashMap<String, JSONObject> elements = new HashMap<String, JSONObject>();
	private HashMap<String, JSONObject> parents = new HashMap<String, JSONObject>();
	private HashMap<String, JSONObject> groups = new HashMap<String, JSONObject>();
	

	protected String generateUniqueId() {
		String key = "" + elementid;
		while(this.elements.containsKey(key)) {
			elementid++;
			key = "" + elementid;
		}
		
		return key;
	}	

	public MappingCache() {
		this.reset();
	}
    
	public MappingCache(Mappings mappings) {
		this(mappings.getTemplate());
	}
	
	public MappingCache(Element template) {
		this.load(template.asJSONObject());
	}

	public MappingCache(JSONObject template) {
		this.load(template);
	}
	
	/**
	 * Load a new mapping handler and cache it's elements
	 * @param mapping JSONMappingHandler of the mapping 
	 */
	public void load(JSONObject template) {
		this.reset();
		this.cacheElement(template);
		this.setTemplate(template);
	}
	
	/**
	 * Reset the contents of this cache. 
	 */
	public void reset()
	{
		this.elementid = 0;

		this.setTemplate(null);

		this.groups.clear();
		this.elements.clear();
		this.parents.clear();
	}
		
	/**
	 * Cache element and descendants without assigning to parent cache.
	 * @param element the element.
	 */	
	public void cacheElement(JSONObject element) {
		this.cacheElement(element, null);
	}
	
	public void cacheElement(Element element) {
		this.cacheElement(element.asJSONObject());
	}
	
	/**
	 * Cache element and descendants and assign to parent cache.
	 * @param element the element.
	 * @param parent the element's parent.
	 */	
	public void cacheElement(JSONObject element, JSONObject parent) {
		this.cacheElementRecursive(element);
		this.fillEmptyIdsRecursive(element);
		this.cacheParentsRecursive(element);
		if(parent != null) this.parents.put(element.get(JSONMappingHandler.ELEMENT_ID).toString(), parent);
	}
	
	public void cacheElement(Element element, Element parent) {
		this.cacheElement(element.asJSONObject(), parent.asJSONObject());
	}
		
	protected void cacheElementRecursive(JSONObject object) {
		if(object.containsKey(JSONMappingHandler.ELEMENT_ID) && object.get(JSONMappingHandler.ELEMENT_ID).toString().length() > 0) {
			String id = object.get(JSONMappingHandler.ELEMENT_ID).toString();
			this.elements.put(id, object);
		}

		if(object.containsKey(JSONMappingHandler.ELEMENT_ATTRIBUTES)) {
			JSONArray attributes = (JSONArray) object.get(JSONMappingHandler.ELEMENT_ATTRIBUTES);
			for(int i = 0; i < attributes.size(); i++) {
				JSONObject a = (JSONObject) attributes.get(i);
				this.cacheElementRecursive(a);
				this.parents.put(a.get(JSONMappingHandler.ELEMENT_ID).toString(), object);
			}
		}
		
		if(object.containsKey(JSONMappingHandler.ELEMENT_CHILDREN)) {
			JSONArray children = (JSONArray) object.get(JSONMappingHandler.ELEMENT_CHILDREN);
			for(int i = 0; i < children.size(); i++) {		
				JSONObject a = (JSONObject) children.get(i);
				this.cacheElementRecursive(a);
				this.parents.put(a.get(JSONMappingHandler.ELEMENT_ID).toString(), object);
			}
		}
	}

	public void fillEmptyIdsRecursive(Element object) {
		this.fillEmptyIdsRecursive(object.asJSONObject(), false);
	}

	private void fillEmptyIdsRecursive(JSONObject object) {
		this.fillEmptyIdsRecursive(object, false);		
	}
	
	public void fillEmptyIdsRecursive(Element element, boolean force) {
		this.fillEmptyIdsRecursive(element.asJSONObject(), force);
	}

	private void fillEmptyIdsRecursive(JSONObject object, boolean force) {
		if(force || !object.containsKey(JSONMappingHandler.ELEMENT_ID) || object.get(JSONMappingHandler.ELEMENT_ID).toString().length() == 0) {
			String id = this.generateUniqueId();
			object.put(JSONMappingHandler.ELEMENT_ID, id);
			this.elements.put(id, object);
			
			if(object.containsKey(JSONMappingHandler.ELEMENT_ATTRIBUTES)) {
				JSONArray attributes = (JSONArray) object.get(JSONMappingHandler.ELEMENT_ATTRIBUTES);
				for(int i = 0; i < attributes.size(); i++) {
					JSONObject a = (JSONObject) attributes.get(i);
					this.fillEmptyIdsRecursive(a, force);
				}
			}
			
			if(object.containsKey(JSONMappingHandler.ELEMENT_CHILDREN)) {
				JSONArray children = (JSONArray) object.get(JSONMappingHandler.ELEMENT_CHILDREN);
				for(int i = 0; i < children.size(); i++) {		
					JSONObject a = (JSONObject) children.get(i);
					this.fillEmptyIdsRecursive(a, force);
				}
			}
		}
	}

	protected void cacheParentsRecursive(JSONObject object) {
		if(object.containsKey(JSONMappingHandler.ELEMENT_ATTRIBUTES)) {
			JSONArray attributes = (JSONArray) object.get(JSONMappingHandler.ELEMENT_ATTRIBUTES);
			for(int i = 0; i < attributes.size(); i++) {
				JSONObject a = (JSONObject) attributes.get(i);
				this.cacheParentsRecursive(a);
				this.parents.put(a.get(JSONMappingHandler.ELEMENT_ID).toString(), object);
			}
		}
		
		if(object.containsKey(JSONMappingHandler.ELEMENT_CHILDREN)) {
			JSONArray children = (JSONArray) object.get(JSONMappingHandler.ELEMENT_CHILDREN);
			for(int i = 0; i < children.size(); i++) {		
				JSONObject a = (JSONObject) children.get(i);
				this.cacheParentsRecursive(a);
				this.parents.put(a.get(JSONMappingHandler.ELEMENT_ID).toString(), object);
			}
		}
	}

	public JSONObject getTemplate() {
		return template;
	}

	public void setTemplate(JSONObject template) {
		this.template = template;
	}
	
	public HashMap<String, JSONObject> getElements() {
		return this.elements;
	}
	
	public JSONObject getElement(String id) {
		return this.elements.get(id);
	}
	
	public JSONObject getElement(MappingIndex index) {
		return this.getElement(index.getId());
	}

	public Element getElementHandler(String id) {
		JSONObject element = this.getElement(id);
		if(element != null) return new Element(element);
		else return null;
	}
	
	public Element getElementHandler(MappingIndex index) {
		return this.getElementHandler(index.getId());
	}
	
	public HashMap<String, JSONObject> getParents() {
		return this.parents;
	}
	
	public JSONObject getParent(String id) {
		return this.parents.get(id);
	}
	
	public Element getParentHandler(String id) {
		JSONObject parent = this.getParent(id);
		if(parent != null) return new Element(parent);
		else return null;
	}	
	
	public HashMap<String, JSONObject> getGroups() {
		return this.groups;
	}
	
	public JSONObject getGroup(String id) {
		return this.groups.get(id);
	}
	
	/**
	 * Remove element from cache.
	 * @param id the element's id.
	 * @return The removed element.
	 */
	public JSONObject removeElement(String id) {
		JSONObject element = this.elements.get(id);
		if(element != null) {
			this.elements.remove(id);
			this.parents.remove(id);
		}
		
		return element;
	}

	public boolean hasTemplate() {
		if(template != null) return true;
		return false;
	}

	/**
	 * Convenience method to duplicate an element within this cache. Duplicate element is also added to the cache.
	 * @param id of the original element.
	 * @return Element handler of the duplicate.
	 */
	public Element duplicate(String id) {
		Element parent = this.getParentHandler(id);
		Element original = new Element(null);
		int index = parent.getByIndex(Element.ELEMENT_CHILDREN, id, original);
		
		if(index >= 0) {
			Element duplicate = original.duplicate();
			duplicate.setRemovable(true);
			parent.addChild(index, duplicate);
			this.fillEmptyIdsRecursive(duplicate, true);
			this.cacheElement(duplicate, parent);
			return duplicate;
		}
		
		return null;
	}
}
