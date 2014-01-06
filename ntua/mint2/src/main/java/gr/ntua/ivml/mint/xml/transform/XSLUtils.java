package gr.ntua.ivml.mint.xml.transform;

import gr.ntua.ivml.mint.mapping.model.Condition;
import gr.ntua.ivml.mint.mapping.model.Function;
import gr.ntua.ivml.mint.mapping.model.ValueMappings;
import gr.ntua.ivml.mint.util.XMLUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;

public class XSLUtils {
	public static final String OMIT_XML_DECLARATION = "<xsl:output omit-xml-declaration=\"yes\" />";
	
	public static String xslStylesheet(String namespace, String excludeNamespaces, String content) {
		if(excludeNamespaces != null && excludeNamespaces.length() > 0) excludeNamespaces = "exclude-result-prefixes=\"" + excludeNamespaces + "\"";

		return XMLUtils.XML_HEADER +
				"<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" " +
				"xmlns:xalan=\"http://xml.apache.org/xalan\" " + namespace + " " + excludeNamespaces + ">" +
				content +
				"</xsl:stylesheet>";
	}
	
	public static String xslTemplate(String match, String content) {
		return  "<xsl:template match=\"" + match + "\">" + content + "</xsl:template>";				
	}
	
	public static String xslApplyTemplates(String select) {
		return "<xsl:apply-templates select=\"" + select + "\"/>";
	}
	
	public static String element(String name, String content, String attributes) {
		String result = "<" + name + ">";
		if(attributes != null) result += attributes;
		if(content != null) result += content;
		result += "</" + name + ">";
		
		return result;
	}
	
	public static String element(String name, String content) {
		return XSLUtils.element(name, content, null);
	}
	
	public static String attribute(String name, String content) {
		return "<xsl:attribute name=\"" + name + "\">" + content + "</xsl:attribute>";
	}
	
	public static String xslForEach(String select, String content) {
		return "<xsl:for-each select=\"" + select + "\">" + content + "</xsl:for-each>";
	}
	
	
	/**
	 * Returns an XSL test as described by a JSONObject based on a mapping's condition format.
	 * @param condition condition handler.
	 * @param normaliseBy normalise XPaths in this condition by the specified string. Skip normalisation if set to null.
	 * @return XSL test
	 */
	public static String conditionTest(Condition condition, String normaliseBy) {
		if(condition == null) return "";
		else return XSLUtils.conditionTest(condition.asJSONObject(), normaliseBy);
	}
	
	private static String conditionTest(JSONObject condition, String normaliseBy) {
		String result = "";
		if(condition != null) {
			if(condition.containsKey("clauses") && condition.containsKey("logicalop")) {
				String logicalop = condition.get("logicalop").toString();
				String clauseTest = "";
				JSONArray clauses = (JSONArray) condition.get("clauses");
				Iterator<?> i = clauses.iterator();
				while(i.hasNext()) {
					JSONObject clause = (JSONObject) i.next();
					String test = XSLUtils.conditionTest(clause, normaliseBy);
					if(test.length() > 0) {
						if(clauseTest.length() > 0) {
							clauseTest += " " + logicalOpXSLTRepresentation(logicalop) + " ";
						}
	
						clauseTest += "(" + test + ")";
					}
				}
				
				result += clauseTest;
			} else {
				String relationalOp = "EQ";
				String conditionOp = "=";
				
				if(condition.containsKey("relationalop")) {
					relationalOp = condition.get("relationalop").toString();
					conditionOp = relationalOpXSLTRepresentation(relationalOp);
				}
				
				if(isUnaryOperator(relationalOp)) {
					if(condition.containsKey("xpath")) {
						String conditionXPath = condition.get("xpath").toString();
						if(conditionXPath.length() > 0) {
							String testXPath = XSLUtils.normaliseXPath(conditionXPath, normaliseBy);
							if(relationalOp.equals("EXISTS")) {
								result += testXPath;								
							} else if (relationalOp.equals("NOTEXISTS")) {
								result += "not(" + testXPath + ")";
							} 
						}
					}
				} else if(isFunctionOperator(relationalOp)) {
					if(condition.containsKey("xpath") && condition.containsKey("value")) {
						String conditionXPath = condition.get("xpath").toString();
						String conditionValue = condition.get("value").toString();
						if(conditionXPath.length() > 0) {
							String testXPath = XSLUtils.normaliseXPath(conditionXPath, normaliseBy);
							if(relationalOp.equals("CONTAINS")) {
								result = testXPath + "[contains(., '" + XSLUtils.escapeConstant(conditionValue) + "')]";
							} else if(relationalOp.equals("NOTCONTAINS")) {
								result = testXPath + "[not(contains(., '" + XSLUtils.escapeConstant(conditionValue) + "'))]";
							} else if(relationalOp.equals("STARTSWITH")) {
								result = testXPath + "[starts-with(., '" + XSLUtils.escapeConstant(conditionValue) + "')]";
							} else if(relationalOp.equals("NOTSTARTSWITH")) {
								result = testXPath + "[not(starts-with(., '" + XSLUtils.escapeConstant(conditionValue) + "'))]";
							} else if(relationalOp.equals("ENDSWITH")) {
								result = testXPath + "[ends-width(., '" + XSLUtils.escapeConstant(conditionValue) + "')]";
							} else if(relationalOp.equals("NOTENDSWITH")) {
								result = testXPath + "[not(ends-width(., '" + XSLUtils.escapeConstant(conditionValue) + "'))]";
							}
						}
					}
				} else {
					if(condition.containsKey("xpath") && condition.containsKey("value")) {
						String conditionXPath = condition.get("xpath").toString();
						String conditionValue = condition.get("value").toString();
						if(conditionXPath.length() > 0) {
							String testXPath = XSLUtils.normaliseXPath(conditionXPath, normaliseBy);
							result += testXPath + " " + conditionOp + " '" + XSLUtils.escapeConstant(conditionValue) + "'";
						}
					}
				}
			}
		}		

		return result;
	}
	
	private static String logicalOpXSLTRepresentation(String logicalop) {
		if(logicalop != null) {
			if(logicalop.equalsIgnoreCase("AND")) {
				return "and";
			} else if(logicalop.equalsIgnoreCase("OR")) {
				return "or";
			}
		}
		
		 return "and";
	}
	
	private static boolean isUnaryOperator(String operator) {
		if(operator.equals("EXISTS") || operator.equals("NOTEXISTS")) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean isFunctionOperator(String operator) {
		if(operator.equals("CONTAINS") || operator.equals("NOTCONTAINS") ||
				operator.equals("STARTSWITH") || operator.equals("NOTSTARTSWITH") ||
				operator.equals("ENDSWITH") || operator.equals("NOTSTARTSWITH")) {
			return true;
		} else {
			return false;
		}
	}
	
	private static String relationalOpXSLTRepresentation(String relationalop) {
		if(relationalop != null) {
			if(relationalop.equalsIgnoreCase("EQ")) {
				return "=";
			} else if(relationalop.equalsIgnoreCase("NEQ")) {
				return "!=";
			}
		}
		
		return "=";
	}
	
	public static String xslValueOf(String select) {
		if(select == null) {
			return "<xsl:value-of select=\".\"/>";
		} else {
			return "<xsl:value-of select=\"" + select + "\"/>";
		}
	}
	
	public static String function(Function function) {
		String result = XSLUtils.xslValueOf(null);
		
		if(function != null && function.hasCall() && function.hasArguments()) {
			String call = function.getCall();
			JSONArray args = function.getArguments();
			
			// create array of arguments with escaped values
			JSONArray arguments = new JSONArray();
			for(int a = 0; a < args.size(); a++) {
				arguments.add(XSLUtils.escapeConstant(args.get(a).toString()));
			}
			
			if(call.equalsIgnoreCase(Function.FUNCTION_CALL_SUBSTRING)) {
				result = XSLUtils.xslValueOf("substring(.," + arguments.get(0).toString() + ((arguments.get(1) !=null && arguments.get(1).toString().length() > 0)?"," + arguments.get(1).toString():"") + ")");
			} else if(call.equalsIgnoreCase(Function.FUNCTION_CALL_SUBSTRING_AFTER)) {
				result = XSLUtils.xslValueOf("substring-after(.,'" + arguments.get(0).toString() +  "')");
			} else if(call.equalsIgnoreCase(Function.FUNCTION_CALL_SUBSTRING_BEFORE)) {
				result = XSLUtils.xslValueOf("substring-before(.,'" + arguments.get(0).toString() +  "')");
			} else if(call.equalsIgnoreCase(Function.FUNCTION_CALL_SUBSTRING_BETWEEN)) {
				result = XSLUtils.xslValueOf("substring-before(substring-after(.,'" + arguments.get(0).toString() +  "'), '" + arguments.get(1).toString() + "')");
			} else if(call.equalsIgnoreCase(Function.FUNCTION_CALL_REPLACE)) {
				result = XSLUtils.xslValueOf("replace(., '" + arguments.get(0).toString() + "', '" + arguments.get(1).toString() + "')");
			} else if(call.equalsIgnoreCase(Function.FUNCTION_CALL_SPLIT)) {
				// how can you split in xsl ???
				String varname = "split";
				result = "<xsl:variable name=\"" + varname + "\" select=\"tokenize(.,'" + arguments.get(0).toString() + "')\"/>";
				result += "<xsl:value-of select=\"$" + varname + "[" + arguments.get(1).toString() +"]\"/>";
			} else if(call.equalsIgnoreCase(Function.FUNCTION_CALL_CUSTOM)) {
				result = XSLUtils.xslValueOf(arguments.get(0).toString());
			} else {
				result = XSLUtils.xslValueOf(null);;
			}
			
		}

		return result;
	}
	
	public static String xslIf(String test, String content) {
		return "<xsl:if test=\"" + test + "\">" + content + "</xsl:if>";
	}
	
	/**
	 * Wrap content in an xsl:if statement only if test is not null or empty.
	 * @param test condition test.
	 * @param content "then" part of xsl:if statement.
	 * @return
	 */
	public static String xslIfOptional(String test, String content) {
		if(test != null && test.length() > 0) return XSLUtils.xslIf(test, content);
		else return content;
	}
	
	/**
	 * Wraps content in an xsl:if statement that checks if current selection is the first in its sequence, to limit execution to only the first possible element. 
	 * @param content "then" part of xsl:if statement.
	 * @return
	 */
	public static String xslIfFirst(String content) {
		return XSLUtils.xslIf("position() = 1", content);
	}
	
	public static String xslWhen(String test, String content) {
		return "<xsl:when test=\"" + test + "\">" + content + "</xsl:when>";
	}
	
	public static String xslOtherwise(String content) {
		return "<xsl:otherwise>" + content + "</xsl:otherwise>";
	}
	
	public static String xslChoose(String content) {
		return "<xsl:choose>" + content + "</xsl:choose>";
	}
	
	public static String escapeConstant(String c) {
		String result = c;
		result = result.replace("'", "''");
		result = result.replace("\\", "\\\\");
		result = StringEscapeUtils.escapeXml(result);
		result = result.replace("*", "\\*");
		result = result.replace("|", "\\|");
		result = result.replace("^", "\\^");
		result = result.replace("$", "\\$");
		result = result.replace("(", "\\(");
		result = result.replace(")", "\\)");
		result = result.replace("[", "\\[");
		result = result.replace("]", "\\]");
		return result;
	}
	
	public static String xslComment(String comment) {
		return "<xsl:comment>" + comment + "</xsl:comment>";
	}
	
	/**
	 * Generates an XML comment if the xsl.generator.addComments parameter is set.
	 * @param comment Contents of the comment. Contents are not escaped. 
	 * @return
	 */
	public static String comment(String comment) {
		return "<!-- " + XSLUtils.escapeConstant(comment) + " -->";
	}
	
	public static String normaliseXPath(String string, String prefix) {
		String result = string;

		if(prefix != null) {
			if(result.indexOf(prefix) == 0) {
				result = result.replaceFirst(prefix, "");
				
				if(result.startsWith("/")) {
					result = result.replaceFirst("/", "");
				}
				
				if(result.length() == 0) {
					result = ".";
				}
			} else {
				String[] tokens1 = string.split("/"); 
				String[] tokens2 = prefix.split("/"); 

				int commonStartIndex = -1;
				for(int i = 0; i < tokens1.length; i++) {
					if(tokens2.length > i) {
						if(tokens1[i].equals(tokens2[i])) {
							commonStartIndex++;
						} else break;
					}
				}
				
				if(commonStartIndex >= 0) {
					result = "";
					for(int i = 0; i < tokens2.length - commonStartIndex - 1; i++) {
						if(result.length() > 0 && !result.endsWith("/")) { result += "/"; }
						result += "..";
					}
					
					for(int i = commonStartIndex + 1; i < tokens1.length; i++) {
						if(result.length() > 0 && !result.endsWith("/")) { result += "/"; }
						result += tokens1[i];
					}
				}
			}
		}
		
		return result;
	}
	
	public static String normaliseXPath(String xpath, Condition condition, String prefix) {
		String normalised = XSLUtils.normaliseXPath(xpath, prefix);

		if(condition != null) {
			String test = XSLUtils.conditionTest(condition, xpath);
			if(test.length() > 0) { normalised += "[" + test + "]"; }
		}
		
		return normalised;
	}
	
	public static String constant(String value) {
		String result = "";
		result += "<xsl:text>";
		String textValue = StringEscapeUtils.escapeXml(value);
		if(textValue.trim().length() == 0) {
			textValue = textValue.replaceAll(" ", "&#160;");
		}
		result += textValue;					
		result += "</xsl:text>";
		
		return result;
	}
	
	public static String xslVariable(String name, String select) {
		return "<xsl:variable name=\"" + name + "\" select=\"" + select +"\"/>";
	}
	
	public static String parameterValue(String parameterName) {
		return XSLUtils.xslValueOf("$" + parameterName);
	}
	
	/**
	 * Class that manages declaration of parameters
	 * @autor Fotis Xenikoudakis
	 */
	public static class Parameters {
		private JSONObject parameterDefaults; 
		private Set<String> addedParameters = new HashSet<String>();
		private StringBuffer parameters = new StringBuffer();

		public String toString() {
			return parameters.toString();
		}
		
		public void reset() {
			parameterDefaults = null;
			addedParameters.clear();
			parameters = new StringBuffer();
		}
		
		public void setDefaults(JSONObject defaults) {
			parameterDefaults = defaults;
		}
		
		public void add(String name) {
			if(!addedParameters.contains(name)) {
				addedParameters.add(name);

				String defaultValue = "";
				if(parameterDefaults != null && parameterDefaults.containsKey(name)) {
					 JSONObject parameter = (JSONObject) parameterDefaults.get(name);
					 if(parameter.containsKey("value")) defaultValue = parameter.get("value").toString();
				}
				parameters.append("<xsl:param name=\"" + name +"\">" + defaultValue + "</xsl:param>");
			}
		}
	}
	
	/**
	 * Class that manages declaration of variables
	 * @author Fotis Xenikoudakis
	 */
	public static class Variables {
		private StringBuffer variables = new StringBuffer();
		private int count = 0;
		
		public String toString() {
			return variables.toString();
		}
		
		public void reset() {
			variables = new StringBuffer();
			count = 0;
		}
		
		public int getCount() {
			return this.count;
		}
		
		public String getNextName(String prefix) {
			String name = "" + (this.count++);
			if(prefix != null) name = prefix + name;
			return name;
		}
		
		public class VariableSet {
			String index;
			String map;
			
			public VariableSet(String map, String index) {
				this.index = index;
				this.map = map;
			}
			
			public VariableSet(String map) {
				this.map = map;
			}
			
			public String getIndex() { return this.index; }
			public String getMap() { return this.map; }

			public String getIndexOfTest() {
				return "index-of($" + this.map + "/item, replace(.,'^\\s*(.+?)\\s*$', '$1')) &gt; 0";
			}
			
			public String getValueOf() {
				return "<xsl:value-of select=\"$" + this.map + "/map[$" + this.index + "]/@value\"/>";
			}
			
			public String getIndexOfVariable() {
				return XSLUtils.xslVariable(index, "index-of($" + map + "/map, replace(.,'^\\s*(.+?)\\s*$', '$1'))");					
			}
			
			public String valueMapping(String whenValueExists, String otherwise) {
				String variable = this.getIndexOfVariable();
				String whenTest = "$" + index + " &gt; 0";
				String chooseContent = XSLUtils.xslWhen(whenTest, whenValueExists);
				if(otherwise != null && otherwise.length() > 0) chooseContent += XSLUtils.xslOtherwise(otherwise);

				return variable + XSLUtils.xslChoose(chooseContent);				
			}
			
			public String enumeration(String ifValueExists) {
				return XSLUtils.xslIf("index-of($" + this.map + "/item, replace(.,'^\\s*(.+?)\\s*$', '$1')) &gt; 0", ifValueExists);
			}
		}

		
		/**
		 * Creates variables related to a new value mapping section as described by valuemap parameter. Returns index variable name that holds the index value of the 
		 * @param valuemap
		 * @return
		 */
		public VariableSet addValueMapping(ValueMappings valueMappings) {
			// create index and map variable names
			String map = this.getNextName("map");
			String index = this.getNextName("idx");

			// create map variable
			variables.append("<xsl:variable name=\"" + map + "\">");
			Iterator<?> i = valueMappings.asJSONArray().iterator();
			while(i.hasNext()) {
				JSONObject vm = (JSONObject) i.next();
				if(vm.containsKey(ValueMappings.VALUE_MAPPINGS_INPUT) && vm.containsKey(ValueMappings.VALUE_MAPPINGS_OUTPUT)) {
					String input = StringEscapeUtils.escapeXml(vm.get(ValueMappings.VALUE_MAPPINGS_INPUT).toString());
					String output = StringEscapeUtils.escapeXml(vm.get(ValueMappings.VALUE_MAPPINGS_OUTPUT).toString());
					variables.append("<map value=\"" + output + "\">" + input.trim() + "</map>");
				}
			}
			variables.append("</xsl:variable>");
						
			return new VariableSet(map, index);
		}		
		
		public VariableSet addEnumeration(JSONArray enumerations) {
			String values = this.getNextName("var");
			variables.append("<xsl:variable name=\"" + values + "\">");
			Iterator<?> i = enumerations.iterator();
			while(i.hasNext()) {
				String e = (String) i.next();
				e = StringEscapeUtils.escapeXml(e);
				variables.append("<item>" + e + "</item>");
			}
			variables.append("</xsl:variable>");
			
			return new VariableSet(values);
		}
	}
}
