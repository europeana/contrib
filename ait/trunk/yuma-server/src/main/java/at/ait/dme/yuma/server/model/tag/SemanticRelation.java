package at.ait.dme.yuma.server.model.tag;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.MapKeys;

/**
 * A 'semantic relation', used to express a typed link between an 
 * annotated item and a semantic tag. 
 * 
 * @author Rainer Simon
 *
 */
public class SemanticRelation implements Serializable {

	private static final long serialVersionUID = 5426269503741863203L;

	/**
	 * Namespace URI of this relation
	 */
	private String namespace;
	
	/**
	 * Relation property
	 */
	private String property;
	
	public SemanticRelation(String namespace, String property) throws InvalidAnnotationException {
		this.namespace = namespace;
		this.property = property;
		validate();
	}
	
	public SemanticRelation(Map<String, String> map) throws InvalidAnnotationException {
		this.namespace = map.get(MapKeys.RELATION_NAMESPACE);		
		this.property = map.get(MapKeys.RELATION_PROPERTY);
		validate();
	}
	
	private void validate() throws InvalidAnnotationException {
		if (this.namespace == null)
			throw new InvalidAnnotationException("Semantic relation namespace may not be null");
		
		if (this.property == null)
			throw new InvalidAnnotationException("Semantic relation property may not be null!");
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getProperty() {
		return property;
	}
	
	public Map<String, String> toMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(MapKeys.RELATION_NAMESPACE, namespace);
		map.put(MapKeys.RELATION_PROPERTY, property);
		
		return map;
	}
	
}
