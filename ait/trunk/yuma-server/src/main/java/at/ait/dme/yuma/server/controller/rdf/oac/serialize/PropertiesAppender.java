package at.ait.dme.yuma.server.controller.rdf.oac.serialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ait.dme.yuma.server.model.Annotation;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The purpose of this abstract class is to add RDF properties to a resource. THe
 * properties are held in a map that is populated by the subclasses of this class.
 * 
 * @author Christian Mader
 */
abstract class PropertiesAppender {
	
	private Map<Property, List<Object>> properties = new HashMap<Property, List<Object>>();
	private Resource resource;
	
	PropertiesAppender(Resource resource) {
		this.resource = resource;
	}

	public void appendProperties(Annotation annotation) {
		populatePropertiesMap(annotation);
		appendPropertiesFromMap();
	}
	
	private void appendPropertiesFromMap() {
		for (Property property : properties.keySet()) {
			List<Object> valueList = properties.get(property);
			for (Object value : valueList) {
				if (value instanceof String) {
					resource.addProperty(property, (String) value);
				}
				else if (value instanceof Resource) {
					resource.addProperty(property, (Resource) value);
				}
			}
		}
	}
	
	protected void addProperty(Property property, Object value) {
		List<Object> valueList = properties.get(property);
		if (valueList == null) {
			valueList = new ArrayList<Object>();
			properties.put(property, valueList);
		}
		valueList.add(value);
	}
	
	abstract void populatePropertiesMap(Annotation annotation);
}
