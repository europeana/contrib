package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.mapping.model.SimpleMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MappingSummary {
	/**
	 * Get recursively all mapping values of specified type, contained in this mappings object.
	 * @param mappings 
	 * @param type specified SimpleMapping type.
	 * @return collection with String values of all SimpleMapping values of specified type.
	 */
	public static HashMap<String, String> getAllMappingsOfType(Mappings mappings, String type) {
		Element template = mappings.getTemplate();
		
		if(template != null) {
			return getAllMappingsOfType(template, type); 
		}
		
		return new HashMap<String, String>();
	}
	
	/**
	 * Get recursively all mapping values of specified type, contained in this element and its descendants.
	 * @param element 
	 * @param type specified SimpleMapping type.
	 * @return collection with String values of all SimpleMapping values of specified type.
	 */
	public static HashMap<String, String> getAllMappingsOfType(Element element, String type) {
		HashMap<String, String> values = new HashMap<String, String>();

		ArrayList<SimpleMapping> mappings = element.getAllMappings(type);
		for(SimpleMapping mapping: mappings) {
			String value = mapping.getValue();
			if(value != null) 
				values.put(value, element.getId());
		}
				
		for(Element child: element.getChildrenAndAttributes()) {
			values.putAll(MappingSummary.getAllMappingsOfType(child, type));
		}
		
		return values;
	}
	
	/**
	 * Get recursively all xpath mappings, contained in this mappings object.
	 * @param mappings 
	 * @return collection of xpaths used in this mappings object.
	 */
	public static Collection<String> getAllMappedXPaths(Mappings mappings) {
		return MappingSummary.getAllMappingsOfType(mappings, SimpleMapping.MAPPING_TYPE_XPATH).keySet();
	}

	/**
	 * Get recursively all xpath mappings, contained in this element or its descendants.
	 * @param mappings 
	 * @return collection of xpaths used in this element or its descendants.
	 */
	public static Collection<String> getAllMappedXPaths(Element element) {
		return MappingSummary.getAllMappingsOfType(element, SimpleMapping.MAPPING_TYPE_XPATH).keySet();
	}
	
	/**
	 * Returns true if this mappings object has no missing mappings.
	 * @param mappings
	 * @return true if this mappings object has no missing mappings.
	 */
	public static boolean isComplete(Mappings mappings) {
		Collection<String> collection = MappingSummary.getMissingMappings(mappings);
		if(collection.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

//	public static Map<String, String> getUsedXPathMap(Mappings mappings) {
//		return MappingSummary.getUsedXPathMap(mappings.getTemplate());
//	}
//	
//	public static Map<String, String> getUsedXPathMap(Element element) {
//		Map<String, String> mappedItems = new Hashtable<String, String>();
//
//		String item = "/" + element.getFullName();
//		String value = null;
//		ArrayList<SimpleMapping> mappings = element.getAllMappings(SimpleMapping.MAPPING_TYPE_XPATH);
//		for(SimpleMapping mapping: mappings) {
//			if(mapping.isXPath()) {
//				if(value == null) value = mapping.getValue();
//				else value += ", " + mapping.getValue();
//			}
//		}
//				
//		mappedItems.put(item, value);
//	
//		for(Element child: element.getChildrenAndAttributes()) {
//			mappedItems.putAll(MappingSummary.getUsedXPathMap(child));
//		}
//		
//		return mappedItems;
//	}

	public static Collection<String> getXPathsWithMappingsOfType(Mappings mappings, String type) {
		return MappingSummary.getXPathsWithMappingsOfType(mappings.getTemplate(), type);
	}
	
	public static Collection<String> getXPathsWithMappingsOfType(Element element, String type) {
		ArrayList<String> xpaths = new ArrayList<String>();
	
		String xpath = "/" + element.getFullName();
		ArrayList<SimpleMapping> mappings = element.getAllMappings(type);
		if(mappings.size() > 0) xpaths.add(xpath);
					
		for(Element child: element.getChildrenAndAttributes()) {
			xpaths.addAll(MappingSummary.getXPathsWithMappingsOfType(child, type));
		}
		
		return xpaths;
	}

	public static Collection<Element> getElementsWithMappingsOfType(Mappings mappings, String type) {
		return MappingSummary.getElementsWithMappingsOfType(mappings.getTemplate(), type);
	}
	
	public static Collection<Element> getElementsWithMappingsOfType(Element element, String type) {
		ArrayList<Element> elements = new ArrayList<Element>();
	
		ArrayList<SimpleMapping> mappings = element.getAllMappings(type);
		if(mappings.size() > 0) elements.add(element);
	
		for(Element child: element.getChildrenAndAttributes()) {
			elements.addAll(MappingSummary.getElementsWithMappingsOfType(child, type));
		}
		
		return elements;
	}

	public static Collection<String> getMissingMappings(Mappings mappings) {
		Collection<String> mandatory = mappings.getMandatoryXPaths();
		
		// collect xpaths of elements that are required by schema and do not have any mappings.
		Collection<String> result = MappingSummary.getMissingMappings(mappings, mandatory);
		
		// collection xpaths of elements with xpath mappings and check if all explicit mandatory xpaths belong to them.
		Collection<String> map = MappingSummary.getXPathsWithMappingsOfType(mappings, SimpleMapping.MAPPING_TYPE_XPATH);
		result.addAll(MappingSummary.checkMandatory(map, mandatory));

		return result;
	}

	private static Collection<String> checkMandatory(Collection<String> map, Collection<String> mandatory) {
		Collection<String> result = new ArrayList<String>();
		
		for(String m: mandatory) {
			if(!map.contains(m)) {
				result.add(m);
			}
		}
		
		return result;
	}

	private static Collection<String> getMissingMappings(Mappings mappings, Collection<String> mandatory) {
		return MappingSummary.getMissingMappings(mappings.getTemplate(), mandatory);
	}
	
	private static Collection<String> getMissingMappings(Element element, Collection<String> mandatory) {
		ArrayList<String> missingMappings = new ArrayList<String>();
		
		boolean hasMappings = element.hasMappingsRecursive();
		boolean isRequired = element.isRequired();
		String item = "/" + element.getFullName();
		if( hasMappings || isRequired ) {
			for(Element child: element.getChildrenAndAttributes()) {
				String childName = item + "/" + child.getFullName();
				if(child.isMissing()) {
					if(child.isMandatory()) { childName += " (" + child.getString("mandatory") + ")"; }
					missingMappings.add(childName);
				}
				
				Collection<String> result = MappingSummary.getMissingMappings(child, mandatory);
				for(String s: result) {
					String it = item + s;
					if(!missingMappings.contains(it)) 
						missingMappings.add(it);
				}
			}
		}	

		return missingMappings;
	}

	public static Collection<String> getIdsForElementsWithMappingsInside(Mappings mappings) {
		return MappingSummary.getIdsForElementsWithMappingsInside(mappings.getTemplate());
	}
	
	public static Collection<String> getIdsForElementsWithMappingsInside(Element element) {
		ArrayList<String> ids = new ArrayList<String>();

		if(element.hasMappingsRecursive()) {
			ids.add(element.getId());
			for(Element child: element.getChildrenAndAttributes()) {
				Collection<String> result = MappingSummary.getIdsForElementsWithMappingsInside(child);
				ids.addAll(result);				
			}
		}
		
		return ids;
	}
	
	public static Collection<String> getIdsForElementsWithMappedAttributes(Mappings mappings) {
		return MappingSummary.getIdsForElementsWithMappedAttributes(mappings.getTemplate());		
	}


	public static Collection<String> getIdsForElementsWithMappedAttributes(Element element) {
		HashSet<String> ids = new HashSet<String>();

		boolean hasMapped = false;
		boolean hasMissing = false;
		for(Element attribute: element.getAttributes()) {
			if(attribute.hasMappings()) hasMapped = true;
			if(attribute.isMissing()) hasMissing = true;
		}
		
		if(hasMapped && !hasMissing) ids.add(element.getId());
		
		for(Element child: element.getChildren()) {
			Collection<String> result = MappingSummary.getIdsForElementsWithMappedAttributes(child);
			ids.addAll(result);
		}
		
		return ids;
	}
	
	public static Collection<String> getIdsForElementsWithMissingAttributes(Mappings mappings) {
		return MappingSummary.getIdsForElementsWithMissingAttributes(mappings.getTemplate());		
	}


	public static Collection<String> getIdsForElementsWithMissingAttributes(Element element) {
		HashSet<String> ids = new HashSet<String>();
		if(element.hasMappingsRecursive()) {
			boolean hasMissing = false;
			
			for(Element attribute: element.getAttributes()) {
				if(attribute.isMissing()) {
					hasMissing = true;
				}
			}
			
			if(hasMissing) ids.add(element.getId());
			
			for(Element child: element.getChildren()) {
				Collection<String> result = MappingSummary.getIdsForElementsWithMissingAttributes(child);
				ids.addAll(result);				
			}
		}

		return ids;		
	}

	public static Collection<String> getMissingMappingsIds(Mappings mappings) {
		return MappingSummary.getMissingMappingsIds(mappings.getTemplate());
	}

	public static Collection<String> getMissingMappingsIds(Element element) {
		Collection<String> missingMappings = new HashSet<String>();

		if(element.hasMappingsRecursive() || element.isRequired()) {
			
			for(Element child: element.getChildrenAndAttributes()) {
				if(child.isMissing()) {
					missingMappings.add(child.getId());
				}
				
				Collection<String> result = MappingSummary.getMissingMappingsIds(child);
				for(String id: result) {
					missingMappings.add(id);
				}
			}
						
			if(!missingMappings.isEmpty()) missingMappings.add(element.getId());
		}
		
		return missingMappings;
	}
}
