package gr.ntua.ivml.mint.xml.transform;

import gr.ntua.ivml.mint.xml.transform.XSLUtils.Variables;
import gr.ntua.ivml.mint.xml.transform.XSLUtils.Parameters;
import gr.ntua.ivml.mint.mapping.MappingSummary;
import gr.ntua.ivml.mint.mapping.model.Condition;
import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.Function;
import gr.ntua.ivml.mint.mapping.model.MappingCase;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.mapping.model.SimpleMapping;
import gr.ntua.ivml.mint.util.Config;
import gr.ntua.ivml.mint.util.StringUtils;
import gr.ntua.ivml.mint.util.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import net.minidev.json.*;
import net.minidev.json.parser.ParseException;

public class XSLTGenerator {
	public static final Logger log = Logger.getLogger(XSLTGenerator.class);
	
	public static final String OPTION_ADD_COMMENTS = "xsl.generator.addComments";
	public static final String OPTION_ADD_XSL_DEBUG_COMMENTS = "xsl.generator.addXslDebugComments";

	public static final String OPTION_SKIP_CHECK_FOR_MISSING_MANDATORY_MAPPINGS = "xsl.generator.skipCheckForMissingMandatoryMappings";
	public static final String OPTION_COMPUTE_ITEM_XPATH_IF_NOT_SET = "xsl.generator.computeItemXPathIfNotSet";
	public static final String OPTION_OMIT_XML_DECLARATION = "xsl.generator.omitXMLDeclaration";

	private String root = null;
	public Variables variables = new Variables();
	public Parameters parameters = new Parameters();		
	private String detectedMappingVersion = "1.0";
	private HashMap<String, Boolean> options = new HashMap<String, Boolean>();
	private Stack<String> xpathPrefix = new Stack<String>();
	private Map<String, String> importNamespaces;
	
	public XSLTGenerator() {
		this.setOption(OPTION_ADD_COMMENTS, Config.getBoolean(OPTION_ADD_COMMENTS, true));
		this.setOption(OPTION_ADD_XSL_DEBUG_COMMENTS, Config.getBoolean(OPTION_ADD_XSL_DEBUG_COMMENTS, false));
		this.setOption(OPTION_COMPUTE_ITEM_XPATH_IF_NOT_SET, Config.getBoolean(OPTION_COMPUTE_ITEM_XPATH_IF_NOT_SET, false));
		this.setOption(OPTION_SKIP_CHECK_FOR_MISSING_MANDATORY_MAPPINGS, Config.getBoolean(OPTION_SKIP_CHECK_FOR_MISSING_MANDATORY_MAPPINGS));
		log.debug(this.options);
	}
	
	public void setDetectedMappingVersion(String version) {
		this.detectedMappingVersion = version;
	}
	
	public String getDetectedMappingVersion() {
		return this.detectedMappingVersion;
	}
	
	
	/**
	 * Set an option for this generator.
	 * @param option Option name
	 * @param value Option value (boolean)
	 */
	public void setOption(String option, Boolean value) {
		this.options.put(option, value);
	}
	
	/**
	 * Get option value of this generator.
	 * @param option Option name. 
	 */
	public boolean getOption(String option) {
		Boolean result = this.options.get(option);
		if(result == null) return false;
		else return result.booleanValue();
	}
	
	/**
	 * Sets the XPath that represents that item's top element. This will be used as the match xpath for the template that will handle the transformation.
	 * @param xpath
	 */
	public void setItemXPath(String xpath) {
		this.root = xpath;
	}

	public String getItemXPath() {
		return this.root;
	}
	
	/**
	 * Generate XSL stylesheet based on a file with json contents based on mapping format.
	 * @param filename mapping file name.
	 * @return XSL stylesheet in a string.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String generateFromFile(String filename) throws IOException, ParseException {
		File file = new File(filename);
		return generateFromString(StringUtils.fileContents(file, true).toString());
	}
	
	/**
	 * Generate XSL stylesheet from contents of string based on mapping format.
	 * @param string mapping in string format.
	 * @return XSL stylesheet in a string, or null if string is not a valid json.
	 * @throws ParseException 
	 */
	public String generateFromString(String string) throws ParseException {
		return generateFromMappings(new Mappings(string));
	}
	
	/**
	 * Generate XSL stylesheet from a json object based on mapping format.
	 * @param mapping json object in mapping format.
	 * @return XSL stylesheet in a string.
	 */
	public String generateFromMappings(Mappings mappings) {
		variables.reset();
		parameters.reset();
		
		String version = mappings.getVersion();
		if(version != null) this.setDetectedMappingVersion(version);

		// Detect and compute stylesheet namespaces.
		
		String stylesheetNamespace = "";
		StringBuilder sb = new StringBuilder();
		
		if(mappings.has("parameters")) {
			parameters.setDefaults((JSONObject) mappings.asJSONObject().get("parameters"));
		}

		JSONObject namespaces = new JSONObject();
		if(mappings.has("namespaces")) {			
			namespaces = (JSONObject) mappings.asJSONObject().get("namespaces");
			
			for(Object o: namespaces.keySet()) {
				String key = (String) o;
				String value = namespaces.get(key).toString();
				
				sb.append("xmlns:" + key + "=\"" + value + "\" ");
			}
			
		}
		
		String excludeNamespaces = "";

		if(this.importNamespaces != null) {
			Iterator<String> i = this.importNamespaces.keySet().iterator();
			while(i.hasNext()) {
				String key = i.next();
				String value = this.importNamespaces.get(key);
				
				// stored differently than in json mapping -> key is value
				if(!namespaces.containsKey(value)) {
					sb.append("xmlns:" + value + "=\"" + key + "\" ");
					if(excludeNamespaces.length() > 0) {
						excludeNamespaces += " ";
					}
					excludeNamespaces += value;
				}				
			}
		}
				
		stylesheetNamespace = sb.toString();

		/*
		 *  Construct basic templates and stylesheet
		 */
		StringBuffer content = new StringBuffer();
		Element mappingsTemplate = mappings.getTemplate();
		
		// compute the item xpath from the common root of all mapped xpaths, if not already set.
		String match = this.getItemXPath();
		if(match == null) {
			if(getOption(OPTION_COMPUTE_ITEM_XPATH_IF_NOT_SET)) {
				match = XMLUtils.commonRoot(MappingSummary.getAllMappedXPaths(mappingsTemplate));
			} else {
				match = "";
			}
		}

		// generate main mapping template. This will also populate variables, parameters and the itemXpath if not already set.
		String template = this.generateTemplate(mappingsTemplate, match);
		
		// add optional omit-xsl-declaration that skips the xml declaration in the output
		if(this.getOption(OPTION_OMIT_XML_DECLARATION)) content.append(XSLUtils.OMIT_XML_DECLARATION);
		
		// compute the default template that will call out generated template
		// Generate default template with optional wrapping element, that calls main mapping template
		String defaultTemplate = XSLUtils.xslApplyTemplates(match);
		// TODO: convert json calls to model calls
		if(mappings.asJSONObject().containsKey("wrap") && ((JSONObject) mappings.asJSONObject().get("wrap")).containsKey("element")) {
			String wrap = (((JSONObject) (mappings.asJSONObject().get("wrap"))).containsKey("prefix")?((JSONObject) mappings.asJSONObject().get("wrap")).get("prefix").toString() + ":":"") + ((JSONObject) mappings.asJSONObject().get("wrap")).get("element").toString();
			defaultTemplate = XSLUtils.element(wrap, defaultTemplate);
		}
		
		// create stylesheet content by appending defaultTemplate, variable declarations, parameters and main template (in that order).
		content.append(variables.toString());
		content.append(parameters.toString());
		content.append(XSLUtils.xslTemplate("/", defaultTemplate));
		content.append(template);

		String result = XSLUtils.xslStylesheet(stylesheetNamespace, excludeNamespaces, content.toString());
		log.debug(result);
		
		return result;
	}
	
	/**
	 * Generate XSL template based on mapping json object.
	 * If no item xpath is set on this XSLTGenerator, the common root of all elements mapped xpaths is used as
	 * the match for this template. Otherwise, the specified item xpath is used as match.
	 * @param template
	 * @return XSL template
	 */
	public String generateTemplate(Element template) {
		String match = this.getItemXPath();
		if(match == null) match = XMLUtils.commonRoot(MappingSummary.getAllMappedXPaths(template));

		return generateTemplate(template, match);
	}
	
	public String generateTemplate(Element template, String match) {
		String result;
				
		if(template != null && template.has("name")) {
			xpathPrefix.push(match);						
			result = generate(template);
			xpathPrefix.pop();
		} else result = comment("warning: template json has no name set");

		return XSLUtils.xslTemplate(match, result);
	}
	
	/**
	 * Generates the XSL code related to an element and its descendants.
	 * @param item Mapping handler for a mapping element.
	 * @return XSL code.
	 */
	public String generate(Element item) {
		String result = this.comment(item.getFullName() + ", id: " + item.getId());
		
		/*
		 * If the element or any of its descendants do not have any mappings,
		 * then we do not have to generate anything for this element 
		 */
		if(!item.hasMappingsRecursive()) return "";
		
		/*
		 * If this element has any mappings, generate the code that applies the mappings,
		 * otherwise just generate the element and proceed to its children.   
		 */
		
		if(item.isVersion10()) {
			if(item.hasMappings()) {
				if(item.isStructural()) {
					result += generateMappingsAsStructural(item);
				} else {
					result += generateMappings(item);
				}
			} else {	
				result += generateWithNoMappings(item, null);
			}
		} else {
			result += generateStructural(item);			
		}		
		
		/*
		 * The OPTION_SKIP_CHECK_FOR_MISSING_MANDATORY_MAPPINGS adds an extra XSLT test
		 * to prevent the creation of this element if any of its mandatory mappings do not exist in the input,
		 * in order to prevent elements that have missing mandatory descendants.
		 */
		if(!this.getOption(OPTION_SKIP_CHECK_FOR_MISSING_MANDATORY_MAPPINGS)) {
			List<String> mandatoryMappings = item.mandatoryMappings(Element.RECURSE_ATTRIBUTES);
			if(mandatoryMappings.size() > 0) {
				String conditionTest = "";
				for(String xpath: mandatoryMappings) {
					if(conditionTest.length() > 0) conditionTest += " and ";
					conditionTest += normaliseXPath(xpath);
				}
				
				result = comment("Check for mandatory elements on " + item.getFullName()) + XSLUtils.xslIf(conditionTest, result);
			}
		}
				
		return result;
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	private String generateMappings(Element item) {
		String result = "";

		if(item.hasMappings()) {
			if(item.hasMappingCases()) {
				ArrayList<MappingCase> mappingCases = item.getMappingCases();
				if(mappingCases.size() > 1) {
					for(MappingCase aCase: mappingCases) {
						String generatedMappings = generateWithMappings(item, aCase);
							
						String test = conditionTest(aCase.getCondition());
						if(test != null && test.length() > 0) {
							result += XSLUtils.xslWhen(test, generatedMappings);
						} else {
							result += XSLUtils.xslOtherwise(generatedMappings);
							break; // if we encounter a case without a condition, then we don't have to proccess the rest.
						}
					}
					
					if(result.length() > 0) {
						result = XSLUtils.xslChoose(result);
					}
				} else if(mappingCases.size() == 1) {
					result += generateSingleCaseMapping(item, item.getMappingCase(0));
				}
			} else {
				result = generateSingleCaseMapping(item, item.asMappingCase());			
			}
		} else {
			result = generateWithNoMappings(item, null);
		}
		
		return result;
	}
	
	private String generateSingleCaseMapping(Element item, MappingCase aCase) {
		return XSLUtils.xslIfOptional(conditionTest(aCase.getCondition()), generateWithMappings(item, aCase));		
	}
	
	private String generateStructural(Element item) {
		String result = "";
		
		if(item.hasStructural() && item.getStructural().hasMappings()) {
			for(SimpleMapping mapping: item.getStructural().getMappings()) {
				if(mapping.isXPath()) {
					String value = mapping.getValue();
					Condition condition = item.getStructural().getCondition();
					
					xpathPrefix.push(value);

					result = this.generateMappings(item);
					result = XSLUtils.xslIfOptional(conditionTest(condition), result);
					
					xpathPrefix.pop();
					
					result = XSLUtils.xslForEach(normaliseXPath(value), result);
				}
			}
		} else {
			result = this.generateMappings(item);
		}

		return result;
	}
	
	/**
	 * Method that returns XSL code that implements structural checking with the initial data structure.
	 * This should apply if no mapping cases exists, the item is a structural element and an xpath mapping in the "mappings" array.
	 * @param item
	 * @return
	 */
	private String generateMappingsAsStructural(Element item) {
		String result = "";
		
		if(item.hasStructural() && item.getStructural().hasMappings()) {
			for(SimpleMapping mapping: item.getStructural().getMappings()) {
				if(mapping.isXPath()) {
					String value = mapping.getValue();
					Condition condition = item.getCondition();
					String name = item.getFullName(false);
					
					xpathPrefix.push(value);
		
					result = XSLUtils.element(name, generateChildren(item), generateAttributes(item));
					result = XSLUtils.xslIfOptional(conditionTest(condition), result);
					result = XSLUtils.xslForEach(normaliseXPath(value), result);
				
					xpathPrefix.pop();				
				}
			}
		}

		return result;
	}
	
	private String generateChildren(Element item) {
		StringBuffer result = new StringBuffer();

		for(Element child: item.getChildren()) {
			result.append(generate(child));
		}

		return result.toString();
	}
	
	private String generateAttributes(Element item) {
		StringBuffer result = new StringBuffer();

		for(Element attribute: item.getAttributes()) {
			result.append(generateAttribute(attribute));
		}

		return result.toString();
	}
	
	private String generateSingleCaseAttributeMapping(Element item, MappingCase aCase) {
		return XSLUtils.xslIfOptional(conditionTest(aCase.getCondition()), generateAttributeMappings(item, aCase));		
	}
	
	private String generateAttribute(Element item) {
		String result = "";
		
		String name = item.getFullName(false);			
		if(item.hasMappings()) {
			if(item.hasMappingCases()) {
				ArrayList<MappingCase> mappingCases = item.getMappingCases();
				if(mappingCases.size() > 1) {
					for(MappingCase aCase: mappingCases) {
						String generatedMappings = generateAttributeMappings(item, aCase);
						
						String test = conditionTest(aCase.getCondition());
						if(test != null && test.length() > 0) {
							result += XSLUtils.xslWhen(test, generatedMappings);
						} else {
							result += XSLUtils.xslOtherwise(generatedMappings);
							break; // if we encounter a case without a condition, then we don't have to proccess the rest.
						}	
					}
					
					if(result.length() > 0) {
						result = XSLUtils.xslChoose(result);
					}
				} else if(mappingCases.size() == 1) {
					result = generateSingleCaseAttributeMapping(item, item.getMappingCase(0));
				}
			} else {
				result = generateSingleCaseAttributeMapping(item, item.asMappingCase());
			}

		} else if(item.hasDefault()) {
			result += XSLUtils.attribute(name, item.getDefault());
		}
		
		return result;
	}
	
	/**
	 * Generate XSL for an element without mappings.
	 * @param item Mapping Handler for the element.
	 * @return XSL code.
	 */
	private String generateWithNoMappings(Element item, String attributeNormalizeBy) {
		return XSLUtils.element(item.getFullName(false), generateChildren(item), generateAttributes(item));		
	}

	private String generateWithMappings(Element item, MappingCase aCase) {	
		String result = "";
		
		String name = item.getFullName(false);
		JSONArray enumerations = item.getEnumerations();
		JSONObject thesaurus = item.getThesaurus();
		ArrayList<SimpleMapping> mappings = aCase.getMappings();

		/*
		 * If more than one mappings exist, then apply concatenation.
		 */
		if(mappings.size() > 0) {
			if(mappings.size() > 1) { 
				return generateWithMappingsConcat(item, aCase);
			} else {
				SimpleMapping simpleMapping = aCase.getMapping(0);
				String value = simpleMapping.getValue();
				
				if(simpleMapping.isXPath()) {
					xpathPrefix.push(value);
					
					String attributes = generateAttributes(item);
					String content = XSLUtils.function(simpleMapping.getFunction());
					String element = XSLUtils.element(name, content, attributes);

					xpathPrefix.pop();

					boolean hasValueMappings = simpleMapping.hasValueMappings();
					if(hasValueMappings) {
						Variables.VariableSet set = variables.addValueMapping(simpleMapping.getValueMappings());
						String whenValueExists = XSLUtils.element(name, set.getValueOf(), attributes );
						whenValueExists += this.debugComment(XSLUtils.xslValueOf("$" + set.index));
						String otherwise = (thesaurus != null) ? element : "";
						result += set.valueMapping(whenValueExists, otherwise);
					} else {
						result = element;
						
						if(enumerations != null) {
							Variables.VariableSet set = variables.addEnumeration(enumerations);
							result = set.enumeration(result); 
						}
					}
						
										
					if(!item.isRepeatable()) result = XSLUtils.xslIfFirst(result);
					
					// repeat for each instance of input element. Check also for tokenize function.
					String select = "";
					// tokenize function block start
					Function function = simpleMapping.getFunction();
					if(function != null && function.isCall(Function.FUNCTION_CALL_TOKENIZE)) {
						String delimeter = ",";
						if(function.hasArguments() && function.getArguments().size() > 0) {
							delimeter = XSLUtils.escapeConstant(function.getArgument(0));
						}
						
						select = normaliseXPath(value, aCase.getCondition());
						select = "tokenize(" + select + "[1],'" + delimeter + "')";
						// tokenize function block end
					} else {
						select = normaliseXPath(value, aCase.getCondition());	
					}

					result = XSLUtils.xslForEach(select, result);
				} else if(simpleMapping.isConstant()) {
					result += XSLUtils.element(name, XSLUtils.constant(value), generateAttributes(item));
				} else if(simpleMapping.isParameter()) {
					parameters.add(value);
					result += XSLUtils.element(name, XSLUtils.parameterValue(value), generateAttributes(item));
				}
			}
		}

		return result;
	}
	
	private String generateAttributeMappings(Element item, MappingCase aCase) {
		String result = "";
		
		boolean needsCheckIfEmpty = true;
		String check = "";

		for(SimpleMapping mapping: aCase.getMappings()) {
			String value = mapping.getValue();
			String select = normaliseXPath(value);	
			boolean hasValueMappings = mapping.hasValueMappings();

			
			if(mapping.isXPath()) {
				xpathPrefix.push(value);

				String content = XSLUtils.function(mapping.getFunction());
				
				if(hasValueMappings) {
					// used to have XSLUtils.element in when result - have to check
					Variables.VariableSet set = variables.addValueMapping(mapping.getValueMappings());
					content = set.valueMapping(set.getValueOf() + debugComment(XSLUtils.xslValueOf("$" + set.index)), content);
				}

				content = XSLUtils.xslIfFirst(content);
								
				// add xpath to check
				if(check.length() > 0) check += " or ";
				check += select;
				content = XSLUtils.xslForEach(select, content);
				result += content;
				
				xpathPrefix.pop();
			} else if(mapping.isConstant()) {
				needsCheckIfEmpty = false;
				result += XSLUtils.constant(value);
			} else if(mapping.isParameter()) {
				parameters.add(value);
				result += XSLUtils.parameterValue(value);
			}
		}
		
		result = XSLUtils.attribute(item.getFullName(false), result);
		
		if(needsCheckIfEmpty) {
			result = XSLUtils.xslIfOptional(check, result);
		}

		return result;
	}
	

	private String generateWithMappingsConcat(Element item, MappingCase aCase) {
		String concatenation = "";
		
		String name = item.getFullName(false);
		for(SimpleMapping mapping: aCase.getMappings()) {
			String result = "";
			String value = mapping.getValue();
			
			if(mapping.isXPath()) {
				String normalised = normaliseXPath(value, aCase.getCondition());
				xpathPrefix.push(value);
								
				if(mapping.hasValueMappings()) {
					Variables.VariableSet set = variables.addValueMapping(mapping.getValueMappings());
					result = set.valueMapping(set.getValueOf() + debugComment(XSLUtils.xslValueOf("$" + set.index)), "");
				} else {
					result = XSLUtils.function(mapping.getFunction());
				}
				
				result = XSLUtils.xslForEach(normalised, result);
								
				xpathPrefix.pop();
			} else if(mapping.isConstant()) {
				result = XSLUtils.constant(value);
			} else if(mapping.isParameter()) {
				parameters.add(value);
				result = XSLUtils.parameterValue(value);
			}
			
			concatenation += result;
		}
		
		return XSLUtils.element(name, concatenation, generateAttributes(item));
	}

	
	public void setImportNamespaces(Map<String, String> namespaces) {
		this.importNamespaces = namespaces;		
	}
	
	private String getNormalisationPrefix() {
		if(!xpathPrefix.empty()) {
			return xpathPrefix.peek();
		}
		
		return null;
	}
	
	
	/*
	 * XSLUtils wrappers
	 */
	
	private String normaliseXPath(String xpath) {
		return XSLUtils.normaliseXPath(xpath, null, getNormalisationPrefix());
	}
	
	private String normaliseXPath(String xpath, Condition condition) {
		return XSLUtils.normaliseXPath(xpath, condition, getNormalisationPrefix());
	}

	private String conditionTest(Condition condition) {
		return XSLUtils.conditionTest(condition, getNormalisationPrefix());
	}
	
	private String debugComment(String comment) {
		String result = "";

		if(this.getOption(XSLTGenerator.OPTION_ADD_XSL_DEBUG_COMMENTS)) {
			return XSLUtils.xslComment(comment);
		}
		
		return result;
	}

	private String comment(String comment) {
		String result = "";
		
		if(this.getOption(XSLTGenerator.OPTION_ADD_COMMENTS)) {
			return XSLUtils.comment(comment);
		}
		
		return result;
	}
}
