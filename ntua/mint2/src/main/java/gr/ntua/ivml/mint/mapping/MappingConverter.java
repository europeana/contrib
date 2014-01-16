package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.mapping.model.Condition;
import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.MappingCase;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.mapping.model.SimpleMapping;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.JSONUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.log4j.Logger;

public class MappingConverter {
	protected static final Logger log = Logger.getLogger( MappingConverter.class);

	/*
	 * Data structure methods
	 */
	
	/** 
	 * Update top level mapping handler to latest mapping data structure.
	 * Upgrades mapping from current version to the next recursively until it reaches the latest version.
	 * @param handler top level mapping handler.
	 * @return true if mapping was upgraded.
	 */
	public static boolean upgradeToLatest(Mappings handler) {
		String version = handler.getVersion();
		if(handler.getVersion() == null || handler.isLatestVersion()) {
			log.debug("Mapping already in latest version");
			return false;
		} else if(version.equalsIgnoreCase("2.0")) {
			// upgrade to 2.0
			MappingConverter.upgrade20To21(handler);
			MappingConverter.upgradeToLatest(handler);
			return true;
		} else if(version.equalsIgnoreCase("1.0")) {
			// upgrade to 2.1
			MappingConverter.upgrade10To20(handler);
			MappingConverter.upgradeToLatest(handler);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Upgrade a version 1.0 mapping to a version 2.0 mapping
	 * <b>Warning</b>: Not full conversion.
	 * @param handler top level mapping handler
	 * @return true
	 */
	public static boolean upgrade10To20(Mappings handler) {
		handler.setVersion("2.0");
		
		return true;
	}
	
	/**
	 * Upgrade a version 2.0 mapping to a version 2.1 mapping
	 * @param handler top level mapping handler
	 * @return true
	 */
	public static boolean upgrade20To21(Mappings handler) {
		handler.setVersion("2.1");
		MappingConverter.upgrade20MappingTo21(handler.getTemplate());
		
		return true;
	}
	
	/**
	 * Convert original condition/mapping pair to a structural mapping
	 * for structural elements, or to mapping cases for leaf elements.
	 * @param handler
	 * @return true if mapping was upgraded.
	 */
	protected static void upgrade20MappingTo21(Element handler) {
		JSONArray cases = new JSONArray();
		JSONObject object = handler.asJSONObject();
		
		Condition condition = handler.getCondition();
		
//		log.debug(handler.asJSONObject());
		JSONArray mappings = (JSONArray) handler.asJSONObject().get(Element.ELEMENT_MAPPINGS);
		
		if(handler.isAttribute()) {
//			log.debug("ATTRIBUTE: " + handler.asJSONObject());
		}

		if(handler.isStructural()) {
			if(mappings.size() > 0) {
				JSONObject firstMapping = (JSONObject) mappings.get(0);
				if(firstMapping.get(SimpleMapping.MAPPING_TYPE).toString().equals(SimpleMapping.MAPPING_XPATH));
				handler.setStructuralMapping(firstMapping.get(SimpleMapping.MAPPING_VALUE).toString());
			}

			if(condition != null) {
				handler.setStructuralCondition(condition);
			}
		} else {
			if(condition == null) condition = new Condition();
			if(mappings == null) mappings = new JSONArray();
			MappingCase aCase = new MappingCase(condition, mappings);

			cases.add(aCase.asJSONObject());				
		}
		
		object.remove(Element.ELEMENT_CONDITION);
		object.remove(Element.ELEMENT_MAPPINGS);
		object.put(Element.ELEMENT_CASES, cases);
		
		for(Element element: handler.getChildrenAndAttributes()) {
			upgrade20MappingTo21(element);
		}
		
		log.debug("CHILDREN AND ATRRIBUTES: " + handler.getChildrenAndAttributes());
	}
	
	private XmlSchema schema = null;
	private String template = null;
	private JSONObject iNamespaces = null;
	private JSONObject oNamespaces = null;
	
	/**
	 * Initialize a mapping converter that converts input mappings to mappings of provided schema.
	 * @param schema the provided schema.
	 */
	public MappingConverter(XmlSchema schema) {
		this.setSchema(schema);
	}
	
	/**
	 * Get the XmlSchema for this mapping converter.
	 * @return the XmlSchema.
	 */
	public XmlSchema getSchema() {
		return this.schema;
	}
	
	/**
	 * Set the schema for this mapping converter.
	 * @param schema
	 */
	public void setSchema(XmlSchema schema) {
		this.schema = schema;
		this.template = schema.getJsonTemplate();
	}
	
	/**
	 * Provide a mapping template directly.
	 * This operation will set schema to null. Use it only if template is not related to a schema.
	 * Use setSchema otherwise.
	 * @param template The template json string.
	 */
	public void setTemplate(String template) {
		this.schema = null;
		this.template = template;
	}
	
	/*
	 * Mapping expansion
	 */
	
	/**
	 * Expands a mapping by adding all missing siblings of each element based on this converter's schema.  
	 * @param mappings
	 * @return
	 */
	public Mappings expand(Mappings mappings) {
		Mappings output = this.getSchema().getTemplate();
		
		output.expand(mappings);
		
		return output;
	}		
	
	/*
	 * Mapping migration methods
	 */
	
	/**
	 * Convert mapping in Mappings object.
	 * @param mappings mappings object.
	 * @return Converted mapping.
	 */
	public Mappings convert(Mappings mappings) {
		Mappings input = mappings;
		Mappings output = this.getSchema().getTemplate();
		
		this.iNamespaces = input.getNamespaces();
		this.oNamespaces = output.getNamespaces();
		
		this.merge(input.getTemplate(), output.getTemplate());
		return input;
	}

	/**
	 * Merge contents of newElement into element.
	 * @param newElement a new element to be merged with element.
	 * @param element an existing mapping element. This element will be modified.
	 */
	public void merge(Element input, Element output) {
		log.debug("merging " + input.getFullName() + " to " + output.getFullName());

		if(this.sameName(input, output) && this.sameNamespace(input, output)) {
			// merge mappings
			log.debug("-- merge mappings");
			if(!output.isFixed() && input.hasMappings()) {
				Collection<MappingCase> iCases = input.getMappingCases();
				output.addMappingCases(iCases);
			}
			
			// apply to children
			log.debug("-- merge children");
			Collection<Element> children = this.merge(input.getChildren(), output.getChildren());
			input.put(Element.ELEMENT_CHILDREN, JSONUtils.jsonList(children));
			
			// apply to attributes
			log.debug("-- merge attributes");
			Collection<Element> attributes = this.merge(input.getAttributes(), output.getAttributes());
			input.put(Element.ELEMENT_ATTRIBUTES, JSONUtils.jsonList(attributes));
		}
	}
	
	public Collection<Element> merge(Collection<Element> inputs, Collection<Element> outputs) {
		if(inputs == null || inputs.size() == 0 || outputs == null || outputs.size() == 0) return outputs;
		
		ArrayList<Element> result = new ArrayList<Element>();
		result.addAll(outputs);
		
		HashMap<String, List<Element>> iElements = elementMap(inputs);
		HashMap<String, List<Element>> oElements = elementMap(outputs);
		List<Element> createdElements = new ArrayList<Element>();
		
		List<Element> oUsed = new ArrayList<Element>();
		
		Iterator<String> iKeys = iElements.keySet().iterator();
		while(iKeys.hasNext()) {
			String key = iKeys.next();
			if(oElements.containsKey(key)) {
				List<Element> iList = iElements.get(key);
				List<Element> oList = oElements.get(key);
				Element oFirst = oList.get(0);
				if(oFirst.isRepeatable()) {
					log.debug("  " + oFirst.getFullName() + " is repeatable");
					for(int i = 0; i < iList.size(); i++) {
						Element iElement = iList.get(i);
						if(iElement.hasMappingsRecursive()) {
							Element oElement = null;
							
							for(int j = 0; j < oList.size(); j++) {
								Element oe = oList.get(j);
								
								if(iElement.hasLabel()) {
									if(!oe.hasLabel() || !iElement.getLabel().equals(oe.getLabel())) continue;
								}
								
								if(!oUsed.contains(oe)) {
									oUsed.add(oe);
									oElement = oe;
									break;
								}
							}
							
							if(oElement == null && !iElement.hasLabel()) {
								oElement = oFirst.duplicate();
								createdElements.add(oElement);
							}
							
							this.merge(iElement, oElement);
						}
					}
				} else {
					log.debug("  " + oFirst.getFullName() + " is unique");
					this.merge(iList.get(0), oList.get(0));
				}
			} else {
				//TODO: notify element was not found in output schema
			}
		}
		
		result.addAll(createdElements);
		return result;
	}
	
	public static HashMap<String, List<Element>> elementMap(Collection<Element> elements) {
		HashMap<String, List<Element>> map = new HashMap<String, List<Element>>();
		
		for(Element element: elements) {
			String label = element.getFullName();
			List<Element> list = null;

			if(map.containsKey(label)) {
				list = map.get(label);
			} else {
				list = new ArrayList<Element>();
				map.put(label, list);
			}
			
			list.add(element);
		}
		
		return map;
	}
	
	
	private boolean sameName(Element one, Element other) {
		return (one.has("name") && other.has("name") &&
				one.getString("name").equalsIgnoreCase(other.getString("name")));
	}

	private boolean sameNamespace(JSONMappingHandler input, JSONMappingHandler output) {
		String iPrefix = input.getString("prefix");
		String oPrefix = output.getString("prefix");

		if(iPrefix == null && oPrefix == null) return true;
		if(iPrefix.length() == 0 && oPrefix.length() == 0) return true;

		if(!iNamespaces.containsKey(iPrefix) && !oNamespaces.containsKey(oPrefix) && iPrefix.equalsIgnoreCase(oPrefix)) return true;
		else if(iNamespaces.containsKey(iPrefix) && oNamespaces.containsKey(oPrefix)) {
			String iURL = iNamespaces.get(iPrefix).toString();
			String oURL = oNamespaces.get(oPrefix).toString();
			if(iURL.equalsIgnoreCase(oURL)) return true;
		}
		
		return false;
	}
}
