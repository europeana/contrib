package gr.ntua.ivml.mint.annotator;

import gr.ntua.ivml.mint.mapping.MappingCache;
import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.MappingCase;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.mapping.model.SimpleMapping;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.util.ApplyI;
import gr.ntua.ivml.mint.util.MethodCallable;
import gr.ntua.ivml.mint.util.XMLUtils;
import gr.ntua.ivml.mint.xml.transform.XMLFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

class ModifyItem implements ApplyI<Item> {
	
	protected final Logger log = Logger.getLogger(getClass());
	private String schemaJSON;
	private JSONArray actionList;
	private Mappings original;
	private Mappings output;
	private HashMap<String, MethodCallable<String, List<String>>> functions =
		new HashMap<String, MethodCallable<String, List<String>>>();
	protected int itemsChanged = 0;
	
	ModifyItem(JSONArray actionList, JSONObject schemaJSON) {
		this.schemaJSON = schemaJSON.toJSONString();
		this.actionList = actionList;
		initFunctions();
	}
	
	@Override
	public void apply(Item item) throws Exception {
		init(item);
		for (Object a: actionList) {
			JSONObject action = (JSONObject) a;
			applyAction(action, item);
		}
	}
	
	public void init(Item item) throws ValidityException, SAXException, ParsingException, IOException, ParseException {
		original = Mappings.templateFromXML(item.getXml());
        output = new Mappings(schemaJSON);
        output.getTemplate().expand(original.getTemplate());
	}
	
	public void initFunctions() {
		functions.put("extensionToLowerCase", 
			new MethodCallable<String, List<String>>() {
				public String call(List<String> input) {
					String[] value = input.get(0).split(".");
					String extension = value[1];
					return value[0] + "." + extension.toLowerCase();
					
			   }
			}
		);
		functions.put("extensionToUpperCase", 
			new MethodCallable<String, List<String>>() {
				public String call(List<String> input) {
					String[] value = input.get(0).split(".");
					String extension = value[1];
					return value[0] + "." + extension.toUpperCase();
				}
			}
		);
		functions.put("substringToUpperCase", 
			new MethodCallable<String, List<String>>() {
				public String call(List<String> input) {
					String value = input.get(0);
					String substring = input.get(1);
					String newValue = value.replaceFirst(substring, substring.toUpperCase());
					return newValue;
				}
			}
		);
		functions.put("substringToLowerCase", 
			new MethodCallable<String, List<String>>() {
				public String call(List<String> input) {
					String value = input.get(0);
					String substring = input.get(1);
					String newValue = value.replaceFirst(substring, substring.toLowerCase());
					return newValue;
				}
			}
		);
	}
	
	public void applyAction(JSONObject action, Item item) throws Exception {
		String type  = (String) action.get("type");
		JSONArray arguments = (JSONArray) action.get("arguments");
		String xpath = (String) action.get("xpath");
		if (type.equals("remove")) {
			removeElements(item, xpath, (String) arguments.get(0));
		}
		else {
			modifyItem(item, xpath, type, arguments);
		}
	}
	
	public void  modifyItem(Item item, String xpath, String type, JSONArray arguments) throws  ParsingException, IOException, SAXException, InterruptedException, ParseException {
		//List<Element> elementsInOriginal = findElements(original, xpath);
		List<Element> elementsInExpanded = findElements(output, xpath);
		if (elementsInExpanded.size() > 0) {
			//if > 1 elements match the xpath, we assume that
			//the request for modification only concerns the first element in the sequence
			Element element = elementsInExpanded.get(0);
			if (type.equals("duplicate")) {
					String value = (String) arguments.get(0);
					appendElement(element, value, output);
				}
			else if (type.equals("editValue")) {
					String newValue = (String) arguments.get(0);
					changeValue(element, newValue);
				}
			else if (type.equals("editValueIf")){
				String existingValue = (String) arguments.get(0);
				String newValue = (String) arguments.get(1);
				for (SimpleMapping map: element.getAllMappings()) {
					if (map.getValue() == existingValue) 
						changeValue(element, newValue);
				}
			}
			else {
				if (element.getAllMappings().size() > 1) {
					log.debug("Multiple mappings");
					throw new IllegalArgumentException("Element "+element.getName() + 
							" cannot be edited because it has multiple mappings");
				}
				else {
					SimpleMapping map = element.getAllMappings().get(0);
					String currentValue = map.getValue();
					List<String> input = new ArrayList();
					input.add(currentValue);
					for (Object a: arguments) {
						input.add((String) a);
					}
					String newValue = functions.get(type).call(input);
					changeValue(element, newValue);
				}
			}
		}
		String xml = XMLUtils.toXML(output);
		xml = XMLFormatter.format(xml);
		item.setXml(xml);
	}
	
	
	public void changeValue(Element element, String value) {
		//clear previous entries-values in mapping-cases
		element.clearMappingsRecursive();
	    MappingCase mapCase = element.getMappingCase(0, true);
	    //add new value to mapping-case
		mapCase.addMapping(new SimpleMapping(SimpleMapping.MAPPING_TYPE_CONSTANT, value));
		itemsChanged ++;
	}
	
	public void appendElement(Element el, String value, Mappings mp) {
		//append (as first child o parent) new element with specified value
		//resulting item may not validate (e.g., schema may not allow >1 children)?
		SimpleMapping newMapping = new SimpleMapping(SimpleMapping.MAPPING_TYPE_CONSTANT, value);
		MappingCache mapCache = new MappingCache(mp);
		/*
		Element parent = mapCache.getParentHandler((el.getId()));
		int numOfChildren = parent.find(el.getFullName()).size();
		if (el.getMaxOccurs() > numOfChildren) {
			//if attribute, then create and append parent
			if (el.isAttribute()) {
				Element newEl = mapCache.duplicate(parent.getId());
				newEl.clearMappingsRecursive();
				MappingCase mapCase = newEl.getAttributeByName(el.getFullName()).getMappingCase(0, true);
				mapCase.addMapping(newMapping);
			}
			else {
				Element newEl = mapCache.duplicate(el.getId());
				newEl.clearMappingsRecursive();
				MappingCase mapCase = newEl.getMappingCase(0, true);
				mapCase.addMapping(newMapping);
			}
			itemsChanged ++;
		}
		*/
		Element ancestor = mapCache.getParentHandler((el.getId()));
		//maxOccurs=-1 means unbounded for elements, but not for attributes!
		String xpath = "";
		int maxOccurs =  el.getMaxOccurs();
		if (el.isAttribute()) {
			maxOccurs = 1;
		}
		while (ancestor != null && maxOccurs != -1 &&
				maxOccurs <= ancestor.find(el.getFullName()).size()) {
			if (xpath.isEmpty())
				xpath = el.getFullName();
			else 
				xpath = el.getFullName() + "/" + xpath;
			el = ancestor;
			maxOccurs = el.getMaxOccurs();
			ancestor = mapCache.getParentHandler((el.getId()));
		}
		if (ancestor != null  && 
				(el.getMaxOccurs() == -1 || el.getMaxOccurs() > ancestor.find(el.getFullName()).size())) {
			Element newEl = mapCache.duplicate(el.getId());
			newEl.clearMappingsRecursive();
			MappingCase mapCase;
			if (xpath.isEmpty()) {
				mapCase = newEl.getMappingCase(0, true);
			}
			else 
				mapCase = newEl.find(xpath).get(0).getMappingCase(0, true);
			mapCase.addMapping(newMapping);
			itemsChanged ++;
		}
	}
	
	public List<Element> findElements(Mappings mappings, String xpath) {
		//TODO: support index (el[x]) at ANY point in path
		//put method in Mappings class?
		List<Element> els = mappings.find(xpath);
		return els;
	}
	
	public void removeElements(Item item, String xpath, String value) {
		List<Element> elementsInExpanded = findElements(output, xpath);
		if (elementsInExpanded.size() > 0) {
			MappingCache mapCache = new MappingCache(output);
			//remove ALL elements that correspond to xpath
			//if xpath ends with el[i], then only el[i]
			for (Element el: elementsInExpanded) {
				Element parent = mapCache.getParentHandler((el.getId()));
				//value="*" means remove, no matter what value
				if (value.equals("*")) {
					removeChild(el, parent, mapCache);
				}
				else {
					for (SimpleMapping map: el.getAllMappings()) {
						//only remove elements that have value value
						if (map.getValue().equals(value)) {
							removeChild(el, parent, mapCache);
						}
					}
				}
			}
	   }
	    String xml = XMLUtils.toXML(output);
	    xml = XMLFormatter.format(xml);
	    item.setXml(xml);
	    itemsChanged ++;
	}
			
	public void removeChild(Element el, Element parent, MappingCache mapCache) throws IllegalArgumentException {
		String elId = el.getId();
		int numOfChildren = parent.find(el.getFullName()).size();
		if(numOfChildren > el.getMinOccurs()) {
			Element child = parent.removeChild(elId);
			if(child != null) {
				mapCache.removeElement(elId);
			}
			else {
				throw new IllegalArgumentException();
			}
		} 
		else //was mandatory, cannot remove
			//find first parent that is mandatory and remove!
			removeChild(parent, mapCache.getParentHandler((parent.getId())), mapCache);
		/*else {
			el.clearMappingsRecursive();
		}*/
	}
	
	public int getItemsChanged() {
		return this.itemsChanged;
	}
}