package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.mapping.model.MappingCase;

import net.minidev.json.JSONObject;

/**
 * 
 * Class wraps a mapping JSONObject and handles operations on it.
 *
 */
public class JSONMappingHandler extends JSONHandler {	
	public JSONMappingHandler(JSONObject o) {
		super(o);
	}

	/**
	 * @return true if handler handles the whole mapping object
	 */
	public boolean isTopLevelMapping()
	{
		if(object.containsKey(TEMPLATE_TEMPLATE)) {
			return true;
		}
		return false;
	}

	/**
	 *  @return true if handler handles an element
	 */
	public boolean isElement()
	{
		if(this.has("name")) {
			if(!this.getOptString("name").startsWith("@")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 *  @return true if handler handles an attribute
	 */
	public boolean isAttribute()
	{
		if(this.has("name")) {
			if(this.getOptString("name").startsWith("@")) {
				return true;
			}
		}
		
		return false;
	}
	
	public MappingCase asMappingCase() {
		return new MappingCase(this.object);
	}
	
	/**
	 * @param key mapping key
	 * @return handler for requested key
	 */
	public JSONMappingHandler getHandler(String key) {
		if(this.has(key)) {
			return new JSONMappingHandler(this.getObject(key));
		}
		
		return null;
	}
}
