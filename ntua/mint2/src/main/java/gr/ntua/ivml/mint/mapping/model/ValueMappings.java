package gr.ntua.ivml.mint.mapping.model;

import java.util.Iterator;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class ValueMappings extends JSONHandler {
	/** 
	 * Create an empty value mappings handler.
	 */
	public ValueMappings() {
		super(new JSONArray());
	}
	
	/**
	 * Create a value mappings handler with contents of provided value mappings json object.
	 * @param mappings value mappings json object.
	 */
	public ValueMappings(JSONArray mappings) {
		super(mappings);
	}

	public static final String VALUE_MAPPINGS_INPUT = "input";
	public static final String VALUE_MAPPINGS_OUTPUT = "output";
	
	public void set(String input, String output) {
		JSONObject map = null;
		
		Iterator<?> i = this.array.iterator();
		while(i.hasNext()) {
			JSONObject m = (JSONObject) i.next();
			if(m.get("input").toString().equals(input)) {
				map = m;
				break;
			}
		}
		
		if(map == null) {
			map = new JSONObject();
			map.put("input", input);
			map.put("output", output);
			this.array.add(map);
		} else {
			map.put("output", output);
		}
	}

	public void remove(String input) {
		JSONObject map = null;
		Iterator<?> i = this.array.iterator();
		while(i.hasNext()) {
			JSONObject m = (JSONObject) i.next();
			if(m.get("input").toString().equals(input)) {
				map = m;
				break;
			}
		}
		
		if(map != null) {
			this.array.remove(map);
		}
	}
}