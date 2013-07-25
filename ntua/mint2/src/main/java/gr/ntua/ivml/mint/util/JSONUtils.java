package gr.ntua.ivml.mint.util;

import net.sf.json.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			object.accumulateAll(map);
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
	 * Convert a json object to a Map of json fields with their values. Works for string values only.
	 * @param parameters
	 * @return
	 */
	public static Map<String, String> toMap(JSONObject object) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(Object o: object.keySet()) {
			String key = (String) o;
			map.put(key, object.getString(key));
		}
		
		return map;
	}
	
	/**
	 * Convert a date json object to Date java object, based on time field.
	 * example date json: {"date":1,"day":5,"hours":13,"minutes":50,"month":1,"seconds":54,"time":1359719454516,"timezoneOffset":-120,"year":113}
	 * @param date
	 * @return
	 */
	
	public static Date toDate(JSONObject date) {
		if(date.has("time")) return new Date(date.getLong("time"));
		else return null;
	}
}
