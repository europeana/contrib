package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class Function extends JSONHandler {
	/** 
	 * Create a condition handler with an empty condition.
	 */
	public Function() {
		super(new JSONObject());
	}
	
	/**
	 * Create a function handler with contents of provided function json object.
	 * @param function function json object.
	 */
	public Function(JSONObject function) {
		super(function);
	}
	
	/**
	 * Create a function handler with specified function call and arguments
	 */
	public Function(String call, String[] arguments) {
		this();
		if(call != null) this.setCall(call);
		if(arguments != null) this.setArguments(arguments);
	}

	/*
	 * These constants are now delegated to JSONHandler constants.
	 * They should be held here but might require modification of various groovy scripts.
	 */
	public static final String FUNCTION_CALL = JSONHandler.MAPPING_FUNCTION_CALL;
	public static final String FUNCTION_ARGUMENTS = JSONHandler.MAPPING_FUNCTION_ARGUMENTS;
	
	public static final String FUNCTION_CALL_SUBSTRING = "substring";
	public static final String FUNCTION_CALL_SUBSTRING_AFTER = "substring-after";
	public static final String FUNCTION_CALL_SUBSTRING_BEFORE = "substring-before";
	public static final String FUNCTION_CALL_SUBSTRING_BETWEEN = "substring-between";
	public static final String FUNCTION_CALL_REPLACE = "replace";
	public static final String FUNCTION_CALL_SPLIT = "split";
	public static final String FUNCTION_CALL_CUSTOM = "custom";
	public static final String FUNCTION_CALL_TOKENIZE = "tokenize";
	
	public boolean hasCall() {
		return this.has(FUNCTION_CALL);
	}
	
	public boolean isCall(String call) {
		return this.hasCall() && this.getCall().equals(call);
	}
	
	public String getCall() {
		return this.getString(FUNCTION_CALL); 
	}
	
	public void setCall(String call) {
		this.object.put(FUNCTION_CALL, call);
	}

	public boolean hasArguments() {
		return this.has(FUNCTION_ARGUMENTS);
	}
	
	public JSONArray getArguments() {
		return this.getArray(FUNCTION_ARGUMENTS); 
	}
	
	public void getArguments(JSONArray arguments) {
		this.object.put(FUNCTION_ARGUMENTS, arguments);
	}
	
	public void setArguments(String[] arguments) {
		JSONArray args = new JSONArray();
		for(String a: arguments) args.add(a);
		this.object.put(FUNCTION_ARGUMENTS, args);
	}
	
	public String getArgument(int index) {
		if(this.hasArguments()) {
			Object argument = this.getArguments().get(index);
			return argument.toString();
		}
		return null;
	}
}