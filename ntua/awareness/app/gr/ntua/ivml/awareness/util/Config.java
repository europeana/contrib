package gr.ntua.ivml.awareness.util;

import java.util.Properties;

import play.Play;

public class Config {
	public final static String PROPERTIES = "pi.properties";
	public static Properties properties = new Properties(System.getProperties());
	
	public static String get( String key ) {
		String result = Play.application().configuration().getString(key);
		return result;
	}
	
	public static boolean getBoolean( String key ) {
		String result = Config.get(key);
		
		if(result != null) {
			if(result.equalsIgnoreCase("true") || result.equalsIgnoreCase("yes") || result.equalsIgnoreCase("1")) {
				return true;
			}
		}
		
		return false;
	}
	
	public static int getInt( String key ) {
		String result = Config.get(key);
		
		if(result != null) {
			int i = Integer.parseInt(result);
			return i;
		}
		
		return 0;
	}
	
	public static boolean has( String key ) {
		String result = Play.application().configuration().getString(key);
		return (result != null);
	}
	
	public static String get( String key, String defaultValue ) {
		String result = Play.application().configuration().getString(key);
		return (result==null?defaultValue:result);
	}
	

}
