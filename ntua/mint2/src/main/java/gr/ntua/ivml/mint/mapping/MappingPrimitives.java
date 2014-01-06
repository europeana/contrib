package gr.ntua.ivml.mint.mapping;

import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class MappingPrimitives {
	public static JSONObject element(String name, String prefix, String id) {
		if(name == null) return null;
		JSONObject element = new JSONObject();
		
		element.put(JSONHandler.ELEMENT_NAME, name!=null?name:"");
		if(prefix != null && prefix.length() > 0) element.put(JSONHandler.ELEMENT_PREFIX, prefix);
		element.put(JSONHandler.ELEMENT_CASES, new JSONArray());
		element.put(JSONHandler.ELEMENT_ID, id!=null?id:"");
		
		return element;
	}
	
	public static JSONObject mapping(String type, String value, String annotation) {
		JSONObject mapping = new JSONObject();
		mapping.put(JSONHandler.MAPPING_TYPE, type);
		mapping.put(JSONHandler.MAPPING_VALUE, value);
		if(annotation != null) mapping.put(JSONHandler.MAPPING_ANNOTATION, annotation);
		
		return mapping;
	}
		
	public static JSONObject mapping(String type, String value) {
		return MappingPrimitives.mapping(type, value, null);
	}
		
	/**
	 * Static method that creates and returns a bookmark object. It does not add it to any bookmark list 
	 * @param title The bookmark title
	 * @param id The element's id
	 * @return the bookmark json object
	 */
	public static JSONObject bookmark(String title, String id) {
		JSONObject bookmark = new JSONObject();
		bookmark.put("title", title);
		bookmark.put("id", id);
		
		return bookmark;
	}
	
	/**
	 * Static method that creates and returns a thesaurus reference object.
	 * @param conceptScheme the concept scheme URI.
	 * @param collections a list of collections with desired concepts.
	 * @param repository The repository that contains the skos thesaurus. Set to null if the default mint repository is used.
	 * @param property Property name that should be queried. Set to null to query the uri of the concept.
	 * @return the thesaurus json object
	 */
	public static JSONObject thesaurus(String conceptScheme, String[] collections, String repository, String property) {
		JSONObject thesaurus = new JSONObject();
		thesaurus.put("conceptScheme", conceptScheme);
		if(collections != null && collections.length > 0) {
			thesaurus.put("collections", collections);
		}
		if(repository != null) { thesaurus.put("repository", repository); }
		if(property != null) { thesaurus.put("property", property); }
		
		return thesaurus;
	}
	
	/**
	 * Static method that creates and returns a thesaurus reference object.
	 * @param conceptScheme the concept scheme URI.
	 * @param collections a list of collections with desired concepts.
	 * @param repository The repository that contains the skos thesaurus. Set to null if the default mint repository is used.
	 * @return the thesaurus json object
	 */
	public static JSONObject thesaurus(String conceptScheme, String[] collections, String repository) {
		JSONObject thesaurus = new JSONObject();
		thesaurus.put("conceptScheme", conceptScheme);
		if(collections != null && collections.length > 0) {
			thesaurus.put("collections", collections);
		}
		if(repository != null) { thesaurus.put("repository", repository); }
		
		return thesaurus;
	}
	
	/**
	 * Static method that creates and returns a thesaurus reference object.
	 * @param conceptScheme the concept scheme URI.
	 * @param collections a list of collections with desired concepts.
	 * @param repository The repository that contains the skos thesaurus. Set to null if the default mint repository is used.
	 * @return the thesaurus json object
	 */
	public static JSONObject thesaurus(String conceptScheme, List<String> collections, String repository) {
		String[] array = null;
		if(collections != null) {
			array = new String[collections.size()];
			for(int i = 0; i < collections.size(); i++) {
				array[i] = collections.get(i);
			}
		}
		return MappingPrimitives.thesaurus(conceptScheme, array, repository);
	}
	
	/**
	 * Static method that creates and returns a thesaurus reference object for a conceptScheme in the default mint repository.
	 * @param conceptScheme the concept scheme URI.
	 * @return the thesaurus json object
	 */
	public static JSONObject thesaurus(String conceptScheme) {
		return MappingPrimitives.thesaurus(conceptScheme, (List<String>) null, null);
	}

	/**
	 * Static method that creates and returns a thesaurus reference object.
	 * @param conceptScheme the concept scheme URI.
	 * @param repository The repository that contains the skos thesaurus. Set to null if the default mint repository is used.
	 * @return the thesaurus json object
	 */
	public static JSONObject thesaurus(String conceptScheme, String repository) {
		return MappingPrimitives.thesaurus(conceptScheme, (List<String>) null, repository);
	}
	
	/**
	 * Static method that creates and returns a thesaurus reference object for a conceptScheme in the default mint repository.
	 * @param conceptScheme the concept scheme URI.
	 * @param collections a list of collections with desired concepts.
	 * @return the thesaurus json object
	 */
	public static JSONObject thesaurus(String conceptScheme, String[] collections) {
		return MappingPrimitives.thesaurus(conceptScheme, collections, null);
	}
	
	/**
	 * Static method that creates and returns a thesaurus reference object for a conceptScheme in the default mint repository.
	 * @param conceptScheme the concept scheme URI.
	 * @param collections a list of collections with desired concepts.
	 * @return the thesaurus json object
	 */
	public static JSONObject thesaurus(String conceptScheme, List<String> collections) {
		String[] array = null;
		if(collections != null) {
			array = new String[collections.size()];
			for(int i = 0; i < collections.size(); i++) {
				array[i] = collections.get(i);
			}
		}
		return MappingPrimitives.thesaurus(conceptScheme, array, null);
	}
	
	/**
	 * Static method that creates and returns a vocabulary reference object.
	 * @param conceptScheme the concept scheme URI of the vocabulary.
	 * @param property property name of vocabulary concept that should be retrieved.
	 * @return the vocabulary json object
	 */
	public static JSONObject vocabulary(String conceptScheme, String property) {
		JSONObject thesaurus = MappingPrimitives.thesaurus(conceptScheme, (String[]) null, null, property);
		thesaurus.put("type", "vocabulary");
		return thesaurus;
	}
	
	/**
	 * Static method that creates and returns a vocabulary reference object. The default concept property will be used for queries.
	 * @param conceptScheme the concept scheme URI of the vocabulary.
	 * @return the vocabulary json object
	 */
	public static JSONObject vocabulary(String conceptScheme) {
		return MappingPrimitives.vocabulary(conceptScheme, null);
	}
}
