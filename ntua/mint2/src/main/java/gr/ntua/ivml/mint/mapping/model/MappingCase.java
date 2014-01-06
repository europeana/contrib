package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import gr.ntua.ivml.mint.mapping.JSONMappingHandler;
import gr.ntua.ivml.mint.mapping.MappingIndex;

import java.util.ArrayList;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class MappingCase extends JSONHandler {
	/** 
	 * Create a mapping case handler with an empty condition and mappings.
	 */
	public MappingCase() {
		super(new JSONObject());
		this.object.put(ELEMENT_CONDITION, new JSONObject());
		this.object.put(ELEMENT_MAPPINGS, new JSONArray());
	}
	
	/**
	 * Create a mapping case handler with contents of provided mapping case json object.
	 * @param condition mapping case json object.
	 */
	public MappingCase(JSONObject aCase) {
		super(aCase);
	}
	
	/**
	 * Create new mapping case handler with specified condition and mappings.
	 * @param condition mapping case json object.
	 */
	public MappingCase(JSONObject condition, JSONArray mappings) {
		super(new JSONObject());
		this.object.put(ELEMENT_CONDITION, condition);
		this.object.put(ELEMENT_MAPPINGS, mappings);
	}

	/**
	 * Create new mapping case handler with specified condition and mappings.
	 * @param condition mapping case json object.
	 */
	public MappingCase(Condition condition, JSONArray mappings) {
		super(new JSONObject());
		this.object.put(ELEMENT_CONDITION, condition.asJSONObject());
		this.object.put(ELEMENT_MAPPINGS, mappings);
	}
	
	public boolean hasCondition() {
		return this.has(ELEMENT_CONDITION); 
	}

	public Condition getCondition() {
		if(this.has(ELEMENT_CONDITION)) return new Condition(this.getObject(ELEMENT_CONDITION));
		else return null;
	}
	
	public void setCondition(Condition condition) {
		if(condition == null) this.object.remove(ELEMENT_CONDITION);
		else this.object.put(ELEMENT_CONDITION, condition.asJSONObject());
	}

	public boolean hasMappings() {
		return (this.has(ELEMENT_MAPPINGS) && this.getArray(ELEMENT_MAPPINGS).size() > 0);
	}
	
	public ArrayList<SimpleMapping> getMappings() {
		return this.getMappings(null);
	}
	
	public ArrayList<SimpleMapping> getMappings(String type) {
		ArrayList<SimpleMapping> result = new ArrayList<SimpleMapping>();
		if(this.has(ELEMENT_MAPPINGS)) {
			for(Object o: this.getArray(ELEMENT_MAPPINGS)) {
				SimpleMapping mapping = new SimpleMapping((JSONObject) o);
				if(type == null || mapping.isType(type)) result.add(mapping);
			}
		}
		return result;
	}
	
	public SimpleMapping getMapping(int index) {
		if(this.has(ELEMENT_MAPPINGS) && this.getArray(ELEMENT_MAPPINGS).size() > index) {
			return new SimpleMapping((JSONObject) this.getArray(ELEMENT_MAPPINGS).get(index));
		}
		else return null;
	}
	
	public SimpleMapping getMapping(MappingIndex index) {
		return this.getMapping(index.getIndex());
	}
	
	public MappingCase addMapping(SimpleMapping mapping) {
		if(!this.has(ELEMENT_MAPPINGS)) {
			this.object.put(ELEMENT_MAPPINGS, new JSONArray());
		}
		
		this.getArray(ELEMENT_MAPPINGS).add(mapping.asJSONObject());
		
		return this;
	}
	
	/**
	 * Remove mapping at the specified index.
	 * @param index
	 */
	public MappingCase removeMapping(MappingIndex index) {
		return this.removeMapping(index.getIndex());
	}
	
	/**
	 * Remove mapping at the specified index.
	 * @param index
	 */
	public MappingCase removeMapping(int index) {
		JSONArray mappings = this.getArray(JSONMappingHandler.ELEMENT_MAPPINGS);
		
		if(mappings != null && index > -1) {
			mappings.remove(index);
		}
		
		return this;
	}
	
	public MappingCase addMapping(int index, SimpleMapping mapping) {
		if(!this.has(ELEMENT_MAPPINGS)) {
			this.object.put(ELEMENT_MAPPINGS, new JSONArray());
		}
		
		this.getArray(ELEMENT_MAPPINGS).add(index, mapping.asJSONObject());
		
		return this;		
	}
	
	public void setMapping(MappingIndex index, SimpleMapping mapping) {
		this.setMapping(index.getIndex(), mapping.getType(), mapping.getValue(), mapping.getAnnotation());
	}
	
	public void setMapping(int index, SimpleMapping mapping) {
		this.setMapping(index, mapping.getType(), mapping.getValue(), mapping.getAnnotation());
	}

	public void setMapping(int index, String type, String value, String annotation) {
		int size = 0;
		if(this.has(ELEMENT_MAPPINGS)) size = this.getArray(ELEMENT_MAPPINGS).size();
		
		if(index == -1 && size > 0) index = 0;
		if(index > -1 && index < size) {
			SimpleMapping mapping = this.getMapping(index);
			mapping.setType(type);
			if(value != null) mapping.setValue(value);
			if(annotation != null) mapping.setAnnotation(annotation);
		} else {
			this.addMapping(new SimpleMapping(type, value, annotation));
		}
	}
	
	public void clearCondition() {
		this.setCondition(new Condition());
	}

	public MappingCase clearMappings() {
		if(this.has(ELEMENT_MAPPINGS)) {
			this.getArray(ELEMENT_MAPPINGS).clear();
		}
		
		return this;
	}
}
