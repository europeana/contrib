package gr.ntua.ivml.mint.mapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.util.JSONUtils;
import gr.ntua.ivml.mint.util.StringUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

public class JSONHandler {
	public static final String VERSION_LATEST = "2.1";
	
	public static final String TEMPLATE_NAMESPACES = "namespaces";
	public static final String TEMPLATE_TEMPLATE = "template";
	public static final String TEMPLATE_GROUPS = "groups";
	public static final String TEMPLATE_BOOKMARKS = "bookmarks";
	public static final String TEMPLATE_VERSION = "version";
	
	public static final String ELEMENT_ID = "id";
	public static final String ELEMENT_NAME = "name";
	public static final String ELEMENT_PREFIX = "prefix";
	public static final String ELEMENT_MINOCCURS = "minOccurs";
	public static final String ELEMENT_MAXOCCURS = "maxOccurs";	

	public static final String ELEMENT_STRUCTURAL = "structural";
	public static final String ELEMENT_FIXED = "fixed";
	public static final String ELEMENT_HIDDEN = "hidden";
	public static final String ELEMENT_WEAK = "weak";
	public static final String ELEMENT_DEFAULT = "default";
	public static final String ELEMENT_MANDATORY = "mandatory";
	public static final String ELEMENT_LABEL = "label";
	public static final String ELEMENT_CONDITION = "condition";
	public static final String ELEMENT_MAPPINGS = "mappings";
	public static final String ELEMENT_CASES = "mapping-cases";
	public static final String ELEMENT_CHILDREN = "children";
	public static final String ELEMENT_ATTRIBUTES = "attributes";
	public static final String ELEMENT_ENUMERATIONS = "enumerations";
	public static final String ELEMENT_THESAURUS = "thesaurus";
	public static final String ELEMENT_REMOVABLE = "duplicate";
	
	public static final String MAPPING_TYPE = "type";
	public static final String MAPPING_VALUE = "value";
	public static final String MAPPING_ANNOTATION = "annotation";
	public static final String MAPPING_CONSTANT = "constant";
	public static final String MAPPING_XPATH = "xpath";
	public static final String MAPPING_FUNCTION = "func";
	public static final String MAPPING_FUNCTION_CALL = "call";
	public static final String MAPPING_FUNCTION_ARGUMENTS = "arguments";
	public static final String MAPPING_PARAMETER = "parameter";
	public static final String MAPPING_VALUE_MAPPINGS = "valuemap";
	
	protected JSONObject object = null;
	protected JSONArray array = null;
	
	protected JSONHandler() {		
	}
	
	public JSONHandler(JSONObject o) {
		this.object = o;
	}

	public JSONHandler(JSONArray a) {
		this.array = a;
	}
	
	public JSONHandler set(JSONObject o) {
		this.object = o;
		return this;
	}
	
	public JSONHandler set(JSONArray a) {
		this.array = a;
		return this;
	}
	
	public String toString() {
		return this.toString(0);
	}

	public String toString(int i) {
		// TODO: implement indentation
		if(object != null) return object.toString();
		if(array != null) return array.toString();
		return "[JSONHandler: empty]";
	}
	
	/**
	 * @return the json object handled by this handler
	 */
	public JSONObject asJSONObject() {
		return this.object;
	}
	
	/**
	 * @return the json array handled by this handler
	 */
	public JSONArray asJSONArray() {
		return this.array;
	}
	
	public JSONHandler put(String key, Object value) {
		this.object.put(key, value);
		return this;
	}
	
	public JSONHandler setString(String key, String value) {
		object.put(key, value);
		return this;
	}
	
	public JSONHandler setObject(String key, JSONObject value) {
		object.put(key, value);
		return this;
	}
	
	public JSONHandler setArray(String key, JSONArray value) {
		object.put(key, value);
		return this;
	}
	
	public JSONHandler setTag(String tag, boolean isOn) {
		if(isOn) {
			this.put(tag, "");
		} else {
			this.remove(tag);
		}
		
		return this;
	}

	public String getString(String key) {
		if(object.containsKey(key)) {
			return object.get(key).toString();
		}
		
		return null;
	}
	
	public String getOptString(String key) {
		String result = this.getString(key);
		if(result != null) return result;
		else return "";
	}

	public String getStringOrNull(String key) {
		String result = this.getString(key);
		if(result != null) return result;
		else return null;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getObject(String key) {
		if(object.containsKey(key)) {
			Object o = object.get(key);
			if(o instanceof JSONObject) return (JSONObject) o;
			else if(o instanceof HashMap) return new JSONObject((HashMap<String, Object>) o);
		}
		
		return null;
	}
	
	public void remove(String key) {
		if(object.containsKey(key)) {
			object.remove(key);
		}
	}
	
	public JSONArray getArray(String key) {
		return this.getArray(key, false);
	}
	
	public <T extends JSONHandler> List<T> getList(String key, Class<T> t) {
		ArrayList<T> result = new ArrayList<T>();
		
		if(this.has(key)) {
			try {
				JSONArray a = this.getArray(key);
				for(Object o: a) {
					T item = null;
					
					Constructor<T> c = t.getConstructor(JSONObject.class);
					item = (T) c.newInstance((JSONObject) o);
					
					if(item != null) result.add(item);
				}
			} catch(Exception e) {
				e.printStackTrace();			
			}
		}
		
		return result;
	}
	
	public <T extends JSONHandler> Map<String, T> getMap(String key, Class<T> t) {
		HashMap<String, T> result = new HashMap<String, T>();
		
		if(this.has(key)) {
			try {
				JSONObject o = this.getObject(key);
				for(String k: o.keySet()) {
					Object v = o.get(k);
					T item = null;
					
					if(v instanceof JSONArray) {
						Constructor<T> c = t.getConstructor(JSONArray.class);
						item = (T) c.newInstance((JSONArray) v);					
					} else if(v instanceof JSONObject) {
						Constructor<T> c = t.getConstructor(JSONObject.class);
						item = (T) c.newInstance((JSONObject) v);
					}
					
					if(item != null) result.put(k, item);
				}
			} catch(Exception e) {
				e.printStackTrace();			
			}
		}
		
		return result;
	}
	
	public JSONArray getArray(String key, boolean create) {
		if(!this.has(key) && create) {
			this.put(key, new JSONArray());
		}
		
		if(this.has(key)) {
			return (JSONArray) object.get(key);
		}
		
		return null;
	}
	
	public int getByIndex(String arrayKey, String id) {
		return this.getByIndex(arrayKey, id, null);
	}
	
	public int getByIndex(String arrayKey, String id, Element resultItem) {
		JSONArray array = this.getArray(arrayKey);
		if(array != null) {
			for(int i = 0; i < array.size(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				JSONHandler item = new JSONHandler(object);
				if(item.has(ELEMENT_ID) && item.getId().equals(id)) {
					if(resultItem != null) resultItem.set(item.asJSONObject());
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public JSONHandler addAtIndex(int index, String arrayKey, JSONHandler item) {
		if(!this.has(arrayKey)) {
			this.put(arrayKey, new JSONArray());
		}
		
		this.getArray(arrayKey).add(index, item.asJSONObject());
		
		return this;
	}
	
	/**
	 * Check if a key exists in the handler.
	 * @param key key name.
	 * @return true if key exists.
	 */
	public boolean has(String key) {
		return this.object.containsKey(key);
	}
	
	/**
	 * Check if handler has id set
	 */
	public boolean hasId() {
		return this.has(ELEMENT_ID);
	}
	
	/**
	 * Get the handler's id, if set.
	 * @return handler's id
	 */
	public String getId() {
		return this.getString(ELEMENT_ID);
	}
	
	/**
	 * Set the handler's id. Set to null to remove the id
	 * @return handler
	 */
	public JSONHandler setId(String id) {
		if(id == null) {
			if (this.has(ELEMENT_ID)) this.remove(ELEMENT_ID); 
		} else {
			this.put(ELEMENT_ID, id);
		}
		return this;
	}
	
	/**
	 * Check if an array with a specified key exists and is not empty.
	 * @param key array key.
	 * @return true if array exists and is not empty.
	 */
	public boolean hasArrayWithContents(String key) {
		return (this.object.containsKey(key) && ((JSONArray) this.object.get(key)).size() > 0);
	}

	/**
	 * Duplicate contents of this JSONHandler.
	 * @return
	 */
	public JSONHandler duplicate() {
		try {
			return new JSONHandler(JSONUtils.parse(this.object.toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	protected JSONObject getObjectFromFileOrDirect(String key, File directory) throws ParseException, IOException {
		JSONObject content = null;
		if(this.has(key)) {
			// check if key contains the json information directly.
			Object direct = this.object.get(key);

			if(direct instanceof String) {
				File file = new File(directory, direct.toString());
				if(file.exists()) { 
					content = JSONUtils.parse(StringUtils.fileContents(file).toString());
				}
			} else if(direct instanceof JSONObject){
				content = (JSONObject) direct;
			}
		}
		
		return content;
	}
	
	protected JSONArray getArrayFromFileOrDirect(String key, File directory) throws ParseException, IOException {
		JSONArray content = null;
		if(this.has(key)) {
			// check if key contains the json information directly.
			Object direct = this.object.get(key);

			System.out.println(direct.getClass());
			
			if(direct instanceof String) {
				File file = new File(directory, direct.toString());
				if(file.exists()) { 
					content = JSONUtils.parseArray(StringUtils.fileContents(file).toString());
				}
			} else if(direct instanceof JSONArray){
				content = (JSONArray) direct;
			}
		}
		
		return content;
	}
	
	public JSONHandler merge(JSONHandler handler) {
		for(String key: handler.asJSONObject().keySet()) {
			Object object = handler.asJSONObject().get(key);
			
			if(this.has(key)) {
				if(object instanceof String) {
					this.put(key, (String) object);
				} else if(object instanceof JSONObject) {
					this.getObject(key).putAll((JSONObject) object);
				} else if(object instanceof JSONArray) {
					this.getArray(key).addAll((JSONArray) object);
				}				
			} else {
				this.put(key, object);
			}
		}
		
		return this;
	}
	
	public static Set<String> getPropertySet(Collection<? extends JSONHandler> handlers, String key) {
		HashSet<String> result = new HashSet<String>();
		for(JSONHandler handler: handlers) {
			String property = handler.getStringOrNull(key);
			if(property != null) result.add(property);
		}
		return result;
	}
	
	public static List<String> getPropertyList(Collection<? extends JSONHandler> handlers, String key) {
		ArrayList<String> result = new ArrayList<String>();
		for(JSONHandler handler: handlers) {
			String property = handler.getStringOrNull(key);
			if(property != null) result.add(property);
		}
		return result;
	}
}
