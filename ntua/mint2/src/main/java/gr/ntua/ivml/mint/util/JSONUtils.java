package gr.ntua.ivml.mint.util;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import gr.ntua.ivml.mint.mapping.model.Element;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONStyler;
import net.minidev.json.parser.JSONParser;

/**
 * Utility class to convert data to JSON format
 * @author Fotis Xenikoudakis
 */
public class JSONUtils {
	
	/**
	 * Convert a list of map to a JSON array with JSON objects for each map.
	 * Map keys are used as JSON keys.
	 * 
	 * @param list the map list
	 * @return
	 */
	public static JSONArray toJSON(List<Map<String, String>> list) {
		JSONArray array = new JSONArray();
		for(Map<String, String> map: list) {
			JSONObject object = new JSONObject();
			object.putAll(map);
			array.add(object);
		}
		
		return array;
	}
	
	/**
	 * Convert a list of strings to a JSON array.
	 * 
	 * @param list the strings list
	 * @return
	 */
	public static JSONArray jsonList(List<String> list) {
		JSONArray result = new JSONArray();
		
		for(String item: list) {
			result.add(item);
		}
		
		return result;
	}
	
	/**
	 * Convert a collection of elements to a JSON array.
	 * 
	 * @param list the strings list
	 * @return
	 */
	public static JSONArray jsonList(Collection<? extends JSONHandler> list) {
		JSONArray result = new JSONArray();
		
		for(JSONHandler item: list) {
			result.add(item.asJSONObject());
		}
		
		return result;
	}

	/**
	 * Convert a json object to a Map of json fields with their values. Works for string values only.
	 * @param parameters
	 * @return
	 */
	public static Map<String, String> toMap(JSONObject object) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(Object o: object.keySet()) {
			String key = (String) o;
			map.put(key, object.get(key).toString());
		}
		
		return map;
	}
	
	/**
	 * Handles dates saved in json format, from various states of Mint project.
	 * Possible formats are:
	 *  - json date : {"date":1,"day":5,"hours":13,"minutes":50,"month":1,"seconds":54,"time":1359719454516,"timezoneOffset":-120,"year":113}
	 *  - String formats based on "EEE MMM d HH:mm:ss zzz yyyy"
	 *  - unix timestamp, milliseconds since 1 Jan 1970. This should be the final version of how dates should be saved in json strings.
	 * @param date
	 * @return
	 */
	
	public static Date toDate(Object date) {
		if(date instanceof String) {
			SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
			try {
				return parserSDF.parse((String) date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if(date instanceof Long) {
			return new Date(((Long) date).longValue());
		} else if(date instanceof JSONObject) { // old method, from net.sf.json handling of dates.
			JSONObject jsondate = (JSONObject) date;
			if(jsondate.containsKey("time")) return new Date(Long.parseLong(jsondate.get("time").toString()));
		}

		return null;
	}

	@Deprecated
	public static net.sf.json.JSONObject toNetSfJSONObject(JSONObject object) {
		return (net.sf.json.JSONObject) net.sf.json.JSONSerializer.toJSON(object.toString());
	}

	@Deprecated
	public static net.sf.json.JSONArray toNetSfJSONArray(JSONArray object) {
		return (net.sf.json.JSONArray) net.sf.json.JSONSerializer.toJSON(object.toString());
	}

	public static JSONArray toArray(Collection<String> array) {
		JSONArray result = new JSONArray();
		result.addAll(array);
		return result;
	}
	
	/**
	 * Get a json-smart parser with default mode.
	 * @return
	 */
	public static JSONParser getParser() {
		return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
	}

	/**
	 * Parse stream that contains a json object.
	 * @param input
	 * @return
	 * @throws net.minidev.json.parser.ParseException 
	 */
	public static JSONObject parse(InputStream stream) throws net.minidev.json.parser.ParseException {
		return (JSONObject) JSONUtils.getParser().parse(stream);
	}
	
	/**
	 * Parse string that contains a json object.
	 * @param input
	 * @return
	 * @throws ParseException 
	 * @throws net.minidev.json.parser.ParseException 
	 */
	public static JSONObject parse(String input) throws net.minidev.json.parser.ParseException {
		return (JSONObject) JSONUtils.getParser().parse(input);
	}
	
	/**
	 * Parse stream that contains a json array.
	 * @param input
	 * @return
	 * @throws net.minidev.json.parser.ParseException 
	 */
	public static JSONArray parseArray(InputStream stream) throws net.minidev.json.parser.ParseException {
		return (JSONArray) JSONUtils.getParser().parse(stream);
	}
	
	/**
	 * Parse string that contains a json array.
	 * @param input
	 * @return
	 * @throws ParseException 
	 * @throws net.minidev.json.parser.ParseException 
	 */
	public static JSONArray parseArray(String input) throws net.minidev.json.parser.ParseException {
		return (JSONArray) JSONUtils.getParser().parse(input);
	}

	private static JSONStyler defaultStyler = null;
	public static JSONStyle getJSONStyler() {
		if(defaultStyler == null) {
			defaultStyler = new JSONStyler();
			defaultStyler.setIdentLevel(2);
		}
		return defaultStyler;
	}
	

	public static String toString(JSONHandler handler) {
		return JSONUtils.toString(handler.asJSONObject());
	}

	public static String toString(JSONObject object) {
		return object.toString(JSONUtils.getJSONStyler());
	}
}
