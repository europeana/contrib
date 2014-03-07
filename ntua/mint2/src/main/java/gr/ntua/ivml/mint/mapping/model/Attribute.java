package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.util.XMLUtils;

import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

public class Attribute extends Element {
	public Attribute(JSONObject attribute) {
		super(attribute);
	}
	
	/**
	 * Generate an XML Attribute with the contents of this handler. 
	 * @return
	 */
	public nu.xom.Attribute toAttribute(Map<String, String> namespaceDeclarations) {
		nu.xom.Attribute attribute = null;
		String value = null;
		
		if(this.hasMappings()) {
			List<SimpleMapping> mappings = this.getAllMappings(SimpleMapping.MAPPING_TYPE_CONSTANT);
			if(mappings.size() > 0) value = mappings.get(0).getValue();
		}
		
		if(value != null) {
			String uri = null;
			if(this.getPrefix() != null && namespaceDeclarations != null && namespaceDeclarations.containsKey(this.getPrefix())) {
				uri = namespaceDeclarations.get(this.getPrefix());
			}
			if(this.getPrefix() != null && this.getPrefix().equalsIgnoreCase("xml")) uri = XMLUtils.XML_NAMESPACE;
			
			//System.out.println(this.getFullName() + " - " + uri);
			if(uri != null) {
				attribute = new nu.xom.Attribute(this.getFullName(false), uri, value);
			} else {
				//System.out.println(this.getName() + " - " + value);
				attribute = new nu.xom.Attribute(this.getFullName(false), value);
			}
		}
		
		return attribute;
	}
}
