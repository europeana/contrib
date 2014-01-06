package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.User;
import gr.ntua.ivml.mint.util.JSONUtils;
import gr.ntua.ivml.mint.util.Preferences;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

public class MappingAjax {
	public static void execute(AbstractMappingManager mappings, HttpServletRequest request, JspWriter out) throws IOException {
		String command = request.getParameter("command");
		
		// get loggedin user
		User user= (User) request.getSession().getAttribute("user");
		if( user != null ) {
			user = DB.getUserDAO().findById(user.getDbID(), false );
		}

		System.err.println("COMMAND: " + command);
		
		if(command != null) {
			MappingIndex index = MappingAjax.indexFromRequest(request);

			if(command.equals("init")) {
				String mappingId = request.getParameter("mappingId");
				if(mappingId != null) {
					mappings.init(mappingId);
					JSONObject target = new JSONObject();
					target.put("targetDefinition", mappings.getTargetDefinition());
					target.put("configuration", mappings.getConfiguration());
					JSONObject preferences = new JSONObject();
					try {
						preferences = Preferences.getObject(user, AbstractMappingManager.PREFERENCES); 
					} catch (Exception e) {
						e.printStackTrace();
					}
					target.put("preferences", preferences);
					target.put("metadata", mappings.getMetadata());
					out.println(target);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("getTargetDefinition")) {
					out.println(mappings.getTargetDefinition());
			} else

			if(command.equals("getElement")) {
				if(index != null) {
					JSONObject element = mappings.getElement(index.getId());
					if(element != null) {
						out.println(element.toString());
					} else {
						out.println(new JSONObject().put("error", "element not fount"));					
					}
				} else {
					out.println(new JSONObject().put("error", "ajax command " + command + ": no id"));
				}
			} else
				
			if(command.equals("setStructuralMapping")) {
				String xpath = request.getParameter("xpath");
								
				if(xpath != null && index != null) {
					xpath = URLDecoder.decode(xpath, "UTF-8");
					JSONObject result = mappings.setStructuralMapping(index, xpath);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("setXPathMapping")) {
				String xpath = request.getParameter("xpath");
				
				if(xpath != null & index != null) {
					xpath = URLDecoder.decode(xpath, "UTF-8");
					JSONObject result = mappings.setXPathMapping(index, xpath);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
							
			if(command.equals("setValueMapping")) {
				String input = request.getParameter("input");
				String output = request.getParameter("output");
				
				System.out.println(input);
				System.out.println(output);
				System.out.println(index);
				if(input != null && output != null && index != null) {
					input = URLDecoder.decode(input, "UTF-8");
					output = URLDecoder.decode(output, "UTF-8");
					JSONObject result = mappings.setValueMapping(index, input, output);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("removeValueMapping")) {
				String input = request.getParameter("input");
				
				if(input != null && index != null) {
					input = URLDecoder.decode(input, "UTF-8");
					JSONObject result = mappings.removeValueMapping(index, input);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("removeStructuralMapping")) {
				if(index != null) {
					JSONObject result = mappings.removeStructuralMapping(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else

			if(command.equals("removeMapping")) {
				if(index != null) {
					JSONObject result = mappings.removeMapping(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("addMappingCase")) {
				if(index != null) {
					JSONObject result = mappings.addMappingCase(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else

			if(command.equals("removeMappingCase")) {
				if(index != null) {
					JSONObject result = mappings.removeMappingCase(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else

			if(command.equals("duplicateNode")) {
				if(index != null) {
					JSONObject result = mappings.duplicateNode(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("removeNode")) {
				if(index != null) {
					JSONObject result = mappings.removeNode(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("setConstantValueMapping")) {
				String value = request.getParameter("value");
				String annotation = request.getParameter("annotation");

				if(index != null) {
					if(value != null) value = URLDecoder.decode(value, "UTF-8");
					if(annotation != null) annotation = URLDecoder.decode(annotation, "UTF-8");
					JSONObject result = mappings.setConstantValueMapping(index, value, annotation);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else

			if(command.equals("setParameterMapping")) {
				String parameter = request.getParameter("parameter");

				if(index != null && parameter != null) {
					JSONObject result = mappings.setParameterMapping(index, parameter);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else

			if(command.equals("additionalMappings")) {
				if(index != null) {
					JSONObject result = mappings.additionalMappings(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
						
			if(command.equals("addConditionClause")) {
				JSONObject result = new JSONObject();
				String complex = request.getParameter("complex");
				boolean iscomplex = (complex != null);

				if(index != null) {
					result = mappings.addConditionClause(index, iscomplex);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("removeConditionClause")) {
				JSONObject result = new JSONObject();

				if(index != null) {
					result = mappings.removeConditionClause(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("setConditionClauseKey")) {
				JSONObject result = new JSONObject();
				String key = request.getParameter("key");
				String value = request.getParameter("value");

				if(index != null && key != null && value != null) {
					value = URLDecoder.decode(value, "UTF-8");
					result = mappings.setConditionClauseKey(index, key, value);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("setConditionClauseXPath")) {
				JSONObject result = new JSONObject();
				String xpath = request.getParameter("xpath");

				if(index != null) {
					result = mappings.setConditionClauseXPath(index, xpath);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("removeConditionClauseKey")) {
				JSONObject result = new JSONObject();
				String key = request.getParameter("key");

				if(index != null) {
					result = mappings.removeConditionClauseKey(index, key);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("setXPathFunction")) {
				String call = request.getParameter("data[call]");
				String[] args = request.getParameterValues("data[arguments][]");
				
				if(index != null) {
					if(args != null) {
						for(int i = 0; i < args.length; i++) {
							args[i] = URLDecoder.decode(args[i], "UTF-8");
						}
					}
					
					JSONObject result = null;
					if(call == null) {
						result = mappings.clearXPathFunction(index);						
					} else {
						result = mappings.setXPathFunction(index, call, args);												
					}
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
			
			if(command.equals("clearXPathFunction")) {
				if(index != null) {
					JSONObject result = mappings.clearXPathFunction(index);
					out.println(result);
				} else {
					out.println(new JSONObject().put("error", "error:" + command + ": argument missing"));
				}
			} else
				
			if(command.equals("getValidationReport")) {
				out.println(mappings.getValidationReport().toString());
			} else
				
			if(command.equals("getXPathsUsedInMapping")) {
				JSONObject result = new JSONObject();
				result.put("xpath", JSONUtils.toArray(mappings.getXPathsUsedInMapping()));
				out.println(result.toString());
			}
			
			if(command.equals("getSearchResults")) {
				String term = request.getParameter("term");
				out.println(mappings.getSearchResults(term));
			}
			
			if(command.equals("getBookmarks")) {
				out.println(mappings.getBookmarks());
			}
			
			if(command.equals("addBookmark")) {
				String title = request.getParameter("title");
				String id = request.getParameter("id");
				out.println(mappings.addBookmark(title, id));
			}

			if(command.equals("renameBookmark")) {
				String title = request.getParameter("title");
				String id = request.getParameter("id");
				if(title != null) title = URLDecoder.decode(title, "UTF-8");
				out.println(mappings.renameBookmark(title, id));
			}

			if(command.equals("removeBookmark")) {
				String id = request.getParameter("id");
				out.println(mappings.removeBookmark(id));
			}
			
			if(command.equals("setPreferences")) {
				String preferences = request.getParameter("preferences");
				Preferences.put(user, AbstractMappingManager.PREFERENCES, preferences);
				out.println(preferences);
			}
		} else {
			out.println(new JSONObject().put("error", "error: no command"));
		}
	}
	
	public static MappingIndex indexFromRequest(HttpServletRequest request) {
		MappingIndex index = null;
		
		System.out.println(request.getParameterMap());
		
		String id = request.getParameter("id");
		
		if(id != null) {
			index = new MappingIndex(id);

			if(request.getParameter("index[index]") != null) index.setIndex(Integer.parseInt(request.getParameter("index[index]")));
			else if(request.getParameter("index") != null) index.setIndex(Integer.parseInt(request.getParameter("index")));

			if(request.getParameter("index[case]") != null) index.setCaseIndex(Integer.parseInt(request.getParameter("index[case]")));
			else if(request.getParameter("case") != null) index.setCaseIndex(Integer.parseInt(request.getParameter("case")));

			if(request.getParameter("index[path]") != null) index.setPath(request.getParameter("index[path]"));
			else if(request.getParameter("path") != null) index.setPath(request.getParameter("path"));

			if(request.getParameter("index[key]") != null) index.setKey(request.getParameter("index[key]"));
			else if(request.getParameter("key") != null) index.setKey(request.getParameter("key"));
		}

		System.out.println(index);
				
		return index;
	}
}
