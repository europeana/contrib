package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import gr.ntua.ivml.mint.mapping.MappingPrimitives;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class SimpleMapping extends JSONHandler {
	/** 
	 * Create a condition handler with an empty condition.
	 */
	public SimpleMapping() {
		super(new JSONObject());
	}
	
	/**
	 * Create a condition handler with contents of provided condition.
	 * @param condition condition json object.
	 */
	public SimpleMapping(JSONObject mapping) {
		super(mapping);
	}
	
	public SimpleMapping(String type, String value, String annotation) {
		this(MappingPrimitives.mapping(type, value, annotation));
	}

	public SimpleMapping(String type, String value) {
		this(MappingPrimitives.mapping(type,  value, null));
	}

	/*
	 * These constants are now delegated to JSONHandler constants.
	 * They should be held here but might require modification of various groovy scripts.
	 */
	public static final String MAPPING_TYPE = JSONHandler.MAPPING_TYPE;
	public static final String MAPPING_VALUE = JSONHandler.MAPPING_VALUE;
	public static final String MAPPING_ANNOTATION = JSONHandler.MAPPING_ANNOTATION;
	public static final String MAPPING_TYPE_EMPTY = "empty";
	public static final String MAPPING_TYPE_CONSTANT = JSONHandler.MAPPING_CONSTANT;
	public static final String MAPPING_TYPE_XPATH = JSONHandler.MAPPING_XPATH;
	public static final String MAPPING_TYPE_PARAMETER = JSONHandler.MAPPING_PARAMETER;
	public static final String MAPPING_FUNCTION = JSONHandler.MAPPING_FUNCTION;
	public static final String MAPPING_VALUE_MAPPINGS = JSONHandler.MAPPING_VALUE_MAPPINGS;

	public String getType() {
		return this.getStringOrNull(MAPPING_TYPE);
	}
	
	public void setType(String type) {
		this.object.put(MAPPING_TYPE, type);
	}

	public String getValue() {
		return this.getStringOrNull(MAPPING_VALUE);
	}
	
	public void setValue(String value) {
		this.object.put(MAPPING_VALUE, value);
	}

	public String getAnnotation() {
		return this.getStringOrNull(MAPPING_ANNOTATION);
	}
	
	public void setAnnotation(String annotation) {
		this.object.put(MAPPING_ANNOTATION, annotation);
	}
	
	public boolean isType(String type) {
		return this.getOptString(MAPPING_TYPE).equals(type);
	}
	
	public boolean isConstant() {
		return this.isType(MAPPING_TYPE_CONSTANT);
	}
	
	public boolean isXPath() {
		return this.isType(MAPPING_TYPE_XPATH);
	}
	
	public boolean isParameter() {
		return this.isType(MAPPING_TYPE_PARAMETER);
	}
	
	public boolean hasFunction() {
		return this.has(MAPPING_FUNCTION);
	}
	
	public Function getFunction() {
		if(this.hasFunction()) return new Function(this.getObject(MAPPING_FUNCTION));
		else return null;
	}
	
	public boolean hasValueMappings() {
		return this.has(MAPPING_VALUE_MAPPINGS) && this.getArray(MAPPING_VALUE_MAPPINGS).size() > 0;
	}
	
	public SimpleMapping setValueMappings(JSONArray array) {
		if(array == null) this.object.remove(MAPPING_VALUE_MAPPINGS);
		else this.object.put(MAPPING_VALUE_MAPPINGS, array);
		
		return this;
	}

	public SimpleMapping setValueMappings(ValueMappings mappings) {
		return this.setValueMappings(mappings.asJSONArray());
	}
	
	public ValueMappings getValueMappings() {
		if(!this.has(MAPPING_VALUE_MAPPINGS)) {
			this.setValueMappings(new JSONArray());
		}
		
		return new ValueMappings(this.getArray(MAPPING_VALUE_MAPPINGS));
	}

	public SimpleMapping setFunction(Function function) {
		if(function == null || function.getCall() == null) this.object.remove(MAPPING_FUNCTION);
		else this.object.put(MAPPING_FUNCTION, function.asJSONObject());
		
		return this;
	}

	public void setValueMapping(String input, String output) {
		this.getValueMappings().set(input, output);
	}

	public void removeValueMapping(String input) {
		this.getValueMappings().remove(input);
	}
}