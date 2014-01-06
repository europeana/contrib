package gr.ntua.ivml.mint.test;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import gr.ntua.ivml.mint.mapping.MappingConverter;
import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.util.JSONUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.minidev.json.parser.ParseException;

public class MappingsGetByXPath {
	public static String MAPPING_FILE = "/Users/mith/tmp/carare.mint";
	
	public static void main(String[] args) throws IOException, ParseException {
		timer();
		Mappings mappings = new Mappings(new File(MAPPING_FILE));
		MappingConverter.upgradeToLatest(mappings);
		timer();

		Element template = mappings.getTemplate();
		timer();
		
		
		System.out.println(" *** MAPPINGS ***");
		System.out.println(mappings.find("//edm:type"));
		Element type = mappings.findFirst("//edm:type");
		System.out.println(JSONUtils.toString(type.asJSONObject()));
		
//		timer();
//		countResults(mappings, "//@xml:lang");
//		countResults(mappings, "//@rdf:about"); 
//		countResults(mappings, "//@r:about");
//		countResults(mappings, "//edm:Agent");
//		countResults(mappings, "//skos:Concept/@rdf:about");
//
//		System.out.println(" *** TEMPLATE ***");
//		System.out.println(template.find("//edm:ProvidedCHO").get(0).getAttributes().size());		
//		countResults(template, "*/*/@xml:lang");
//		countResults(template, "*/*/*/@xml:lang");
//		countResults(template, "*/*/*/*/@xml:lang");
//		countResults(template, "*/*/*/*/*/@xml:lang");
//		countResults(template, "*/*/*/*/*/*/@xml:lang");
//		countResults(template, "*/*/*/*/*/*/*/@xml:lang");
//		countResults(template, "*/*/*/*/*/*/*/*/@xml:lang");
//		countResults(template, "*/*/*/*/*/*/*/*/*/@xml:lang");
//		countResults(template, "//@xml:lang");
//		countResults(template, "edm:ProvidedCHO//dc:title");
//		countResults(template, "ore:Aggregation//dc:title");
		
		//countResults(template, "/edm:ProvidedCHO/@rdf:about");
	}
	
	static long time = System.currentTimeMillis();
	public static void timer() {		
		System.out.println("time passed: " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
	}
	
	public static void countResults(Mappings mappings, String xpath) {
		Collection<Element> result = mappings.find(xpath);
		System.out.println(xpath + ": " + result.size());
		System.out.println(JSONHandler.getPropertyList(result, Element.ELEMENT_NAME));
		timer();
	}
	
	public static void countResults(Element element, String xpath) {
		Collection<Element> result = element.find(xpath);
		System.out.println(element.getFullName() + " - " + xpath + ": " + result.size());
		System.out.println(JSONHandler.getPropertySet(result, Element.ELEMENT_NAME));
		timer();
	}
}
