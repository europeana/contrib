package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.Function;
import gr.ntua.ivml.mint.mapping.model.MappingCase;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.mapping.model.SimpleMapping;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.JSONUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

public abstract class AbstractMappingManager {	
	protected static final Logger log = Logger.getLogger( AbstractMappingManager.class );
	public final static String PREFERENCES = "mappings"; 
	
	protected XmlSchema outputSchema;

	Mappings mappings = null;
	JSONObject targetDefinition = null;
	JSONObject schemaConfiguration = null;
	public JSONObject getConfiguration() {
		return this.schemaConfiguration;
	}

	MappingCache cache = null;
	
	public AbstractMappingManager() {
	}
	
	public abstract void init(String id);
	
	public void init(Mappings mappings, XmlSchema schema) {
		this.outputSchema = schema;
		if(this.cache != null) this.cache.reset();

		this.mappings = mappings;
		this.targetDefinition = mappings.asJSONObject();
		this.schemaConfiguration = new JSONObject();
		try {
			schemaConfiguration = JSONUtils.parse(this.outputSchema.getJsonConfig());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schemaConfiguration.put("schemaId", this.outputSchema.getDbID());

		// cache template
		log.debug("cache elements");
		this.cache = new MappingCache(mappings.getTemplate());
	}
	
	public JSONObject getMetadata() {
		return new JSONObject();
	}
	
	public JSONArray getBookmarks() {
		if(targetDefinition == null) return new JSONArray();
		else if(targetDefinition.containsKey("bookmarks")) return (JSONArray) targetDefinition.get("bookmarks");
		else {
			JSONArray bookmarks = new JSONArray();
			targetDefinition.put("bookmarks", bookmarks);
			return (JSONArray) targetDefinition.get("bookmarks");
		}
	}
	
	public JSONArray addBookmark(String title, String id) {
		return this.addBookmark(MappingPrimitives.bookmark(title, id));
	}
	
	public JSONArray addBookmark(JSONObject bookmark) {
		JSONArray bookmarks = this.getBookmarks();
		if(!bookmarks.contains(bookmark))
			bookmarks.add(bookmark);
		this.save();
		
		return bookmarks;
	}
	
	
	public JSONObject getBookmark(String id) {
		JSONArray bookmarks = this.getBookmarks();
		JSONObject bookmark = null;
		
		Iterator<?> i = bookmarks.iterator();
		while(i.hasNext()) {
			JSONObject next = (JSONObject) i.next();
			if(next.containsKey("id") && next.get("id").toString().equals(id)) {
				bookmark = next;
				break;
			}
			
		}
		
		return bookmark;
	}
	
	public void removeBookmarkRecursive(JSONObject object) {
		if(object.containsKey("id")) {
			String id = object.get("id").toString();
			this.removeBookmark(id);
		}
		
		if(object.containsKey("children")) {
			JSONArray children = (JSONArray) object.get("children");
			Iterator<?> it = children.iterator();
			while(it.hasNext()) {
				JSONObject child = (JSONObject) it.next();
				this.removeBookmarkRecursive(child);
			}
		}

		if(object.containsKey("attributes")) {
			JSONArray attributes = (JSONArray) object.get("attributes");
			Iterator<?> it = attributes.iterator();
			while(it.hasNext()) {
				JSONObject attribute = (JSONObject) it.next();
				this.removeBookmarkRecursive(attribute);
			}
		}		
	}
	
	public JSONArray renameBookmark(String newTitle, String id) {
		JSONObject bookmark = this.getBookmark(id);
		bookmark.put("title", newTitle);
		this.save();
		
		return this.getBookmarks();
	}
	
	public JSONArray removeBookmark(String id) {
		JSONArray bookmarks = this.getBookmarks();
		JSONObject bookmark = null;
		
		Iterator<?> i = bookmarks.iterator();
		while(i.hasNext()) {
			JSONObject next = (JSONObject) i.next();
			if(next.containsKey("id") && next.get("id").toString().equals(id)) {
				bookmark = next;
				break;
			}
			
		}
		
		if(bookmark != null) {
			bookmarks.remove(bookmark);
			this.save();
		}
		
		return bookmarks;
	}
		
	/**
	 * Get json object of element with specified id
	 * @param id element's id
	 * @return
	 */
	public JSONObject getElement(String id) {
		return this.getElement(id, false);
	}
	
	/**
	 * Get json mapping handler for element with specified id
	 * @param id element's id
	 * @return
	 */
	public JSONMappingHandler getElementHandler(String id) {
		JSONObject element = this.getElement(id);
		if(element != null) return new JSONMappingHandler(element);
		return null;
	}
	
	/**
	 * Get json object of element with specified id
	 * @param id element's id
	 * @param strip if true, element will contain only id, prefix and name
	 * @return
	 */
	public JSONObject getElement(String id, boolean strip) {
		JSONObject target = this.cache.getElement(id);
		
		if(strip) {
			JSONObject stripped = new JSONObject();
			if(target.containsKey("id")) stripped.put("id", target.get("id").toString());
			if(target.containsKey("name")) stripped.put("name", target.get("name").toString());
			if(target.containsKey("prefix")) stripped.put("prefix", target.get("prefix").toString());
			if(target.containsKey("minOccurs")) stripped.put("minOccurs", target.get("minOccurs"));
			if(target.containsKey("maxOccurs")) stripped.put("maxOccurs", target.get("maxOccurs"));
			
			return stripped;
		}
		
		if(this.cache.getParent(id) != null) {
			target.put("parent", this.getElement(this.cache.getParent(id).get("id").toString(), true));
		}
		
		return target;
	}
	
	/**
	 * Get json object of element with specified id with contents of children and attributes.
	 * @param id
	 * @return
	 */
	public JSONObject getElementStripped(String id) {
		JSONObject target = this.cache.getElement(id);
		
		// TODO: strip element;
		
		return target;
	}
	
	public JSONObject getTargetDefinition() {
		if(this.cache != null && this.cache.hasTemplate()) {
			this.targetDefinition.put(JSONMappingHandler.TEMPLATE_TEMPLATE, this.cache.getTemplate());
		}
		
		return this.targetDefinition;
	}
	
	public JSONObject setStructuralMapping(MappingIndex index, String xpath) {
		return this.setXPathMapping(index, xpath, true);
	}
	
	public JSONObject setXPathMapping(MappingIndex index, String xpath) {
		return this.setXPathMapping(index, xpath, false);
	}

	public JSONObject setXPathMapping(MappingIndex index, String xpath, boolean structural) {
		Element element = this.cache.getElementHandler(index);
		
		setXPathMapping(index, xpath, element, structural);
		save();
		
		return element.asJSONObject();
	}
	
	public void setXPathMapping(MappingIndex index, String xpath, Element element, boolean structural) {
		if(structural) {
			element.setStructuralMapping(xpath);
		} else {
			SimpleMapping mapping = new SimpleMapping(MappingPrimitives.mapping(SimpleMapping.MAPPING_XPATH, xpath));
			element.addMapping(index, mapping);
		}
	}

	public JSONObject setXPathFunction(String id, int index, String call, String[] args) {
		return this.setXPathFunction(new MappingIndex(id, index), call, args);
	}
	
	public JSONObject setXPathFunction(MappingIndex index, String call, String[] arguments) {
		Element element = this.cache.getElementHandler(index);
		Function function = new Function(call, arguments);
		SimpleMapping mapping = element.getMapping(index);
		if(mapping != null) mapping.setFunction(function);
		
		save();
		
		return element.asJSONObject();
	}
	
	@Deprecated
	public JSONObject clearXPathFunction(String id, int index) {
		return this.clearXPathFunction(new MappingIndex(id, index));
	}
		
	public JSONObject clearXPathFunction(MappingIndex index) {
		return this.setXPathFunction(index, null, null);
	}
	

	@Deprecated
	public JSONObject setValueMapping(String input, String output, String target, int index) {
		return this.setValueMapping(new MappingIndex(target, index, -1), input, output);
	}
	
	public JSONObject setValueMapping(MappingIndex index, String input, String output) {
		Element element = this.cache.getElementHandler(index.getId());
		setValueMapping(index, input, output, element);
		save();
		
		return element.asJSONObject();		
	}
	
	public void setValueMapping(MappingIndex index, String input, String output, Element element) {
		SimpleMapping mapping = element.getMapping(index);
		if(mapping != null) mapping.setValueMapping(input, output);
	}
	
	@Deprecated
	public JSONObject removeValueMapping(String input, String target, int index) {
		return this.removeValueMapping(new MappingIndex(target, index, -1), input);
	}
	
	public JSONObject removeValueMapping(MappingIndex index, String input) {
		Element element = this.cache.getElementHandler(index);
		this.removeValueMapping(index, input, element);
		save();
		
		return element.asJSONObject();
	}
	
	public void removeValueMapping(MappingIndex index, String input, Element element) {
		SimpleMapping mapping = element.getMapping(index);
		if(mapping != null) mapping.removeValueMapping(input);
	}
	
	@Deprecated
	public JSONObject setConstantValueMapping(String target, String value, int index) {
		return setConstantValueMapping(new MappingIndex(target, index, -1), value, null);
	}

	@Deprecated
	public JSONObject setConstantValueMapping(String target, String value, String annotation, int index) {
		return setConstantValueMapping(new MappingIndex(target, index, -1), value, annotation);
	}
	
	public JSONObject setConstantValueMapping(MappingIndex index, String value, String annotation) {
		Element element = this.cache.getElementHandler(index);
		setConstantValueMapping(index, element, value, annotation);
		
		save();
		
		return element.asJSONObject();
	}
	
	public void setConstantValueMapping(MappingIndex index, Element element, String value) {
		this.setConstantValueMapping(index, element, value, null);
	}

	public void setConstantValueMapping(MappingIndex index, Element element, String value, String annotation) {
		MappingCase aCase = element.getMappingCase(index);
		aCase.setMapping(index, new SimpleMapping(SimpleMapping.MAPPING_TYPE_CONSTANT, value, annotation));
	}
	
	@Deprecated
	public JSONObject setParameterMapping(String target, String parameter, int index) {
		return this.setParameterMapping(new MappingIndex(target, index), parameter);
	}
	
	public JSONObject setParameterMapping(MappingIndex index, String parameter) {
		Element element = this.cache.getElementHandler(index.getId());

		setParameterMapping(index, element, parameter);
		save();
		
		return element.asJSONObject();
	}
	
	public void setParameterMapping(MappingIndex index, Element element, String parameter) {
		MappingCase aCase = element.getMappingCase(index);
		aCase.setMapping(index, new SimpleMapping(SimpleMapping.MAPPING_TYPE_PARAMETER, parameter, null));
	}
	


	@Deprecated
	public JSONObject removeMappings(String target, int index) {
		return this.removeMapping(new MappingIndex(target, index));
	}
		
	public JSONObject removeMapping(MappingIndex index) {
		JSONObject element = this.cache.getElement(index.getId());

		removeMapping(index, element);		
		save();
		
		return element;
	}
	
	public JSONObject addMappingCase(MappingIndex index) {
		Element element = this.cache.getElementHandler(index);

		element.addMappingCase(index);
		save();
		
		return element.asJSONObject();
	}
	
	public JSONObject removeMappingCase(MappingIndex index) {
		Element element = this.cache.getElementHandler(index);

		element.removeMappingCase(index);
		save();
		
		return element.asJSONObject();
	}
	
	@Deprecated
	public JSONObject removeStructuralMapping(String target) {
		return this.removeStructuralMapping(new MappingIndex(target));
	}
	
	public JSONObject removeStructuralMapping(MappingIndex index) {
		Element element = this.cache.getElementHandler(index);

		element.removeStructuralMapping();
		save();
		
		return element.asJSONObject();
	}
	
	@Deprecated
	public void  removeMappings(JSONObject target, int index) {
		this.removeMapping(new MappingIndex(null, index), target);
	}
	
	public void  removeMapping(MappingIndex index, JSONObject target) {
		new Element(target).removeMapping(index);
	}
		
	public JSONObject additionalMappings(MappingIndex index) {
		Element element = this.cache.getElementHandler(index.getId());
		MappingCase aCase = element.getMappingCase(index);
		
		if(aCase != null && index.getIndex() > -1) {
				SimpleMapping mapping = new SimpleMapping(MappingPrimitives.mapping(SimpleMapping.MAPPING_TYPE_EMPTY, ""));
				aCase.addMapping(index.getIndex() + 1, mapping);
		}
		
		save();
		
		return element.asJSONObject();
	}
	
	public JSONObject objectForTargetXPath(String xpath) {
		//System.out.println("objectForTargetXPath: " + xpath);

		if(xpath.startsWith("/")) { xpath = xpath.replaceFirst("/", ""); }
		String[] tokens = xpath.split("/");
		if(tokens.length > 0) {
			JSONObject result = null;
			JSONObject group = this.cache.getGroup(tokens[0]);
			System.out.println("objectForTargetXPath token: " + tokens[0]);

			if(group != null) {
//				System.out.println("group: " + group.getString("name"));
//				JSONObject content = group.getJSONObject("contents");
				result = this.objectForTargetXPath(group, xpath);
				if(result != null) return result;
			}
		}

		return null;
	}
	
	public JSONObject objectForTargetXPath(JSONArray array, String xpath) {
		Iterator<?> i = array.iterator();
		while(i.hasNext()) {
			JSONObject object = (JSONObject) i.next();
			JSONObject result = this.objectForTargetXPath(object, xpath);
			if(result != null) return result;
		}
		return null;
	}
	
	public JSONObject objectForTargetXPath(JSONObject object, String xpath) {
		System.out.println("objectForTargetXPath: " + object.get("name").toString() + " - "  + xpath);

		if(xpath.startsWith("/")) { xpath = xpath.replaceFirst("/", ""); }
		String[] tokens = xpath.split("/");
		if(tokens.length > 0) {
			log.debug("looking path:" + xpath + " in object:" + object);
			if(object.containsKey("name")) {
				if(tokens[0].equals(object.get("name").toString())) {
					if(tokens.length == 1) {
						return object;
					} else {
						String path = tokens[1];
						for(int i = 2; i < tokens.length; i++) {
							path += "/" + tokens[i];
						}
	
						if(path.startsWith("@")) {
							if(object.containsKey("attributes")) {
								return this.objectForTargetXPath((JSONArray) object.get("attributes"), path);
							}
						} else {
							if(object.containsKey("children")) {
								return this.objectForTargetXPath((JSONArray) object.get("children"), path);
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public JSONObject duplicateObjectWithXPath(String xpath) {
		System.out.println("duplicate object: " + xpath);
		JSONObject result = null;
		JSONObject object = this.objectForTargetXPath(xpath);

		if(object != null) {
			result = this.duplicateNode(object.get("id").toString());
			result = this.cache.getElement(((JSONObject) result.get("duplicate")).get("id").toString());
		}
		
		return result;
	}

	public JSONObject duplicateNode(MappingIndex index) {
		return this.duplicateNode(index.getId());
	}

	public JSONObject duplicateNode(String id) {
		Element parent = this.cache.getParentHandler(id);
		Element duplicate = this.cache.duplicate(id);
		if(duplicate != null) duplicate.setRemovable(true);

		this.save();

		JSONObject result = new JSONObject();
		result.put("parent", parent.getId());
		result.put("original", id);
		result.put("duplicate", duplicate.asJSONObject());
		return result;
	}
	
	public JSONObject removeNode(MappingIndex index) {
		return this.removeNode(index.getId());
	}
	
	
	public JSONObject removeNode(String id) {
		JSONObject result = new JSONObject();
		
		Element parent = this.cache.getParentHandler(id);

		Element child = parent.removeChild(id);
		if(child != null) {
			this.removeBookmarkRecursive(child.asJSONObject());
			this.cache.removeElement(id);
			result.put("id", id);
			result.put("parent", parent.getString("id"));
		} else {
			result.put("error", "could not find target element");
		}
				
		save();
		
		return result;
	}
	
	private JSONObject duplicateJSONObject(JSONObject source) {
		String json = source.toString();
		JSONObject out = null;
		
		try {
			out = JSONUtils.parse(json);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		out.put("duplicate", "");
		new Element(out).clearMappingsRecursive();

		return out;
	}
	
	@Deprecated
	public JSONObject addConditionClause(String id, String path, boolean complex) {
		MappingIndex index = new MappingIndex(id, -1, 0);
		index.setPath(path);
		return this.addConditionClause(index, complex);
	}
	
	public JSONObject addConditionClause(MappingIndex index, boolean complex) {
		Element handler = this.cache.getElementHandler(index);
		handler.addConditionClause(index, complex);
		
		save();
		
		return handler.getCondition(index).asJSONObject();
	}
		
	@Deprecated
	public JSONObject removeConditionClause(String id, String path) {
		MappingIndex index = new MappingIndex(id);
		index.setPath(path);

		return this.removeConditionClause(index);
	}

	public JSONObject removeConditionClause(MappingIndex index) {
		Element element = this.cache.getElementHandler(index);
		element.removeConditionClause(index);
		
		save();
		
		return element.getCondition(index).asJSONObject();
	}
	
	public JSONObject setConditionClauseKey(MappingIndex index, String key, String value) {
		Element element = this.cache.getElementHandler(index.getId());
		element.setConditionClauseKey(index, key, value);
		
		save();
		
		return element.getCondition(index).asJSONObject();
	}

	public JSONObject setConditionClauseXPath(MappingIndex index, String xpath) {
		return this.setConditionClauseKey(index, "xpath", xpath);
	}
		
	public JSONObject removeConditionClauseKey(MappingIndex index, String key) {
		JSONObject result = new JSONObject();
		Element element = this.cache.getElementHandler(index);
		element.removeConditionClauseKey(index, key);
		result = element.getCondition(index).asJSONObject();
		
		save();
		
		return result;
	}
	
	public JSONObject getValidationReport() {
		JSONObject result = new JSONObject();
		
		JSONArray mapped = new JSONArray();
		JSONArray missing = new JSONArray();
//		JSONArray normal = new JSONArray();
		
		JSONArray mapped_attributes = new JSONArray();
		JSONArray missing_attributes = new JSONArray();
//		JSONArray normal_attributes = new JSONArray();
		
		if(mappings != null) {
			mapped.addAll(MappingSummary.getIdsForElementsWithMappingsInside(mappings));
			missing.addAll(MappingSummary.getMissingMappingsIds(mappings));
	//		normal.addAll(MappingSummary.getIdsForElementsWithNoMappingsInside(targetDefinition));
			
			mapped_attributes.addAll(MappingSummary.getIdsForElementsWithMappedAttributes(mappings));
			missing_attributes.addAll(MappingSummary.getIdsForElementsWithMissingAttributes(mappings));
	//		normal_attributes.addAll(MappingSummary.getIdsForElementsWithNoMappedAttributes(targetDefinition));
			
			Collection<String> mandatory = mappings.getIdsForExplicitMandatoryXPaths();
			for(String id: mandatory) {
				if(!mapped.contains(id) && !missing.contains(id)) {
					missing.add(id);
				}
			}
		} else {
			result.put("error", "targetDefinition is not initialised yet!");
		}
					
		result.put("mapped", mapped);
		result.put("missing", missing);
		result.put("mapped_attributes", mapped_attributes);
		result.put("missing_attributes", missing_attributes);
		
		JSONArray warnings = new JSONArray();
		for(Object i: missing) {
			String id = (String) i;
			JSONObject warning = new JSONObject();
			JSONObject element = this.cache.getElement(id);
			if(element != null) {
				warning.put("id", id);
				warning.put("name", element.get("name").toString());
				if(element.containsKey("prefix")) {
					warning.put("prefix", element.get("prefix").toString());
				}
				
				if(element.containsKey("children") && !((JSONArray) element.get("children")).isEmpty()) {
					warning.put("type", "structural");
				} else {
					warning.put("type", "unmapped");
				}
			}
			
			warnings.add(warning);
		}
		
		for(Object i: missing_attributes) {
			String id = (String) i;
			JSONObject warning = new JSONObject();
			JSONObject element = this.cache.getElement(id);
			if(element != null) {
				warning.put("id", id);
				warning.put("name", element.get("name").toString());
				if(element.containsKey("prefix")) warning.put("prefix", element.get("prefix").toString());
				
				warning.put("type", "attribute");
			}
			
			warnings.add(warning);
		}
		result.put("warnings", warnings);

		return result;
	}
	
	public HashMap<String, String> getXPathsUsedInMapping() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		Mappings mappings = new Mappings(this.getTargetDefinition());
		if(mappings != null) {
			map = MappingSummary.getMapped(mappings);
		}
		return map; 
	}
	
	public String getXpathForElement(String id) {
		Element element = this.cache.getElementHandler(id);
		Element parent = this.cache.getParentHandler(id);
		
		if(element == null) return null;
		
		String localName = "/" + element.getFullName(false);
		localName = "/" + localName;
		
		
		if(parent == null) {
			return localName;
		} else {
			return this.getXpathForElement(parent.getId()) + localName;
		}
	}
	
	private JSONArray getXpathAsList(String id) {
		JSONArray list = new JSONArray();

		Element element = this.cache.getElementHandler(id);
		Element parent = this.cache.getParentHandler(id);
		
		if(element == null) return list;
		
		if(parent != null) {
			list = this.getXpathAsList(parent.getId());
		}
		
		JSONObject item = new JSONObject();
		item.put("name", element.getFullName(false));
		item.put("id", id);
		list.add(item);
		
		return list;
	}
	
	public JSONArray getSearchResults(String term) {
		return getSearchResults(term, false);
	}
	
	public JSONArray getSearchResults(String term, boolean caseSensitive) {
		JSONArray results = new JSONArray();
		
		if(term != null) {
			JSONObject report = this.getValidationReport();

			Iterator<JSONObject> it = this.cache.getElements().values().iterator();
			while(it.hasNext()) {
				JSONObject element = it.next();
				
				boolean foundString = false;
				if(caseSensitive) foundString = (element.get("name").toString().indexOf(term) > -1);
				else foundString = (element.get("name").toString().toLowerCase().indexOf(term.toLowerCase()) > -1);
				
				if(foundString) {
					JSONObject result = new JSONObject();

					String id = element.get("id").toString();
					String name = element.get("name").toString();

					JSONObject parent = this.cache.getParent(id);
					
					result.put("id", id);
					if(parent != null) result.put("parent", parent.get("id").toString());
					result.put("name", name);
					
					if(element.containsKey("prefix")) result.put("prefix", element.get("prefix").toString());
					result.put("xpath", this.getXpathForElement(id));
					result.put("paths", this.getXpathAsList(id));
					
					if(name.startsWith("@")) {
						if(((JSONArray) report.get("mapped_attributes")).contains(id)) {
							result.put("mapped", true);
						} else if(((JSONArray) report.get("missing")).contains(id)) {
							result.put("missing_attributes", true);
						}
					} else {
						if(((JSONArray) report.get("mapped")).contains(id)) {
							result.put("mapped", true);
						} else if(((JSONArray) report.get("missing")).contains(id)) {
							result.put("missing", true);
						}
					}
					
					results.add(result);
				}
			}
		}
		
		return results;
	}
	
	protected abstract void save();
}