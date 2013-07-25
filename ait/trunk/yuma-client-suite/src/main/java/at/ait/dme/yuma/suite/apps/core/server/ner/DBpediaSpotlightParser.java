package at.ait.dme.yuma.suite.apps.core.server.ner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.NERServiceException;

/**
 * A simple response parser for DBpedia Spotlight.
 * 
 * @author Rainer Simon
 */
public class DBpediaSpotlightParser {
	
	/**
	 * String constants
	 */
	private static final String ERROR = "Error";
	private static final String ERROR_MSG = "@message";
	private static final String RESOURCES = "Resources";
	private static final String URI = "@URI";
	private static final String SURFACE_FORM = "@surfaceForm";
	private static final String TYPES = "@types";
	
	public List<SemanticTag> parseJsonResponse(String json) throws NERServiceException {
		return parseJsonResponse(json, true);
	}
	
	public List<SemanticTag> parseJsonResponse(String json, boolean dereferenceEntities)
		throws NERServiceException {

		JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
	        
		@SuppressWarnings("unchecked")
		Map<String, String> error = (Map<String, String>) jsonObj.get(ERROR);
		if (error != null)
			throw new NERServiceException(error.get(ERROR_MSG));
	                
	    List<SemanticTag> tags = new ArrayList<SemanticTag>();
		
		@SuppressWarnings("unchecked")
		List<Map<String, String>> resources = (List<Map<String, String>>) jsonObj.get(RESOURCES);
	    for (Map<String, String> resource : resources) {
	    	SemanticTag t;
	    	if (dereferenceEntities) {
	    		// Construct a complete SemanticTag by dereferencing the URI
	    		String uri = getIfNotNull(resource, URI);
	    		t = dereferenceEntity(uri);
	    		t.setURI(uri);
	    		t.setType(getIfNotNull(resource, TYPES));
	    	} else {
	    		// Construct a skeleton based on the Spotlight response only
		    	t = new SemanticTag();
		    	t.setURI(getIfNotNull(resource, URI));
		    	t.setPrimaryLabel(getIfNotNull(resource, SURFACE_FORM));
		    	t.setPrimaryDescription(getIfNotNull(resource, TYPES));
		    	t.setType(t.getPrimaryDescription());	    		
	    	}
	    	
	    	tags.add(t);
	    }
	
		return tags;
	}
	
	@SuppressWarnings("unchecked")
	private SemanticTag dereferenceEntity(String uri) throws NERServiceException {
        try {
        	// Dereference the resource
        	URL url = new URL(uri.replace("resource", "data") + ".json");
        	URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			// Read the response
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line);
			}

			// Parse JSON
			JSONObject jsonObj = (JSONObject) JSONValue.parse(response.toString());
			
			DBpediaEntityParser entityParser = new DBpediaEntityParser();
			Iterator<String> keys = jsonObj.keySet().iterator();
			while (keys.hasNext()) {
				if (keys.next().equals(uri)) {
					return entityParser.parseJsonEntity((JSONObject) jsonObj.get(uri));
				}
			}
			
			throw new NERServiceException();
		} catch (IOException e) {
			throw new NERServiceException(e.getMessage());
		}
	}
	
	private String getIfNotNull(Map<String, String> resource, String key) throws NERServiceException {
		String val = resource.get(key);
		if (val == null)
			throw new NERServiceException();
		
		return val;
	}

}
