package gr.ntua.ivml.mint.xml.transform;

import gr.ntua.ivml.mint.actions.ItemPreview.View;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.mapping.model.SchemaConfiguration;
import gr.ntua.ivml.mint.persistent.Crosswalk;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.Config;
import gr.ntua.ivml.mint.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class ChainTransform {
	protected static final Logger log = Logger.getLogger( ChainTransform.class);
	
	protected static final String TRANSFORM_CUSTOM = "custom";
	protected static final String TRANSFORM_XSL = "xsl";
	protected static final String TRANSFORM_HTML = "html";
	protected static final String TRANSFORM_JSP = "jsp";
	protected static final String TRANSFORM_RDF = "rdf";
	protected static final String TRANSFORM_TEXT = "text";
	
	public ChainTransform()
	{
	}
	
	public ArrayList<View> transform(String input, XmlSchema schema) throws Exception{
		SchemaConfiguration configuration = schema.getConfiguration();

		if(configuration.has("preview")) {
			return this.transform(input, configuration.getArray("preview"), schema);
		}
		
		return new ArrayList<View>();
	}
	
	public ArrayList<View> transform(String input, JSONArray previews) {
			return this.transform(input, previews, null);
	}
	
	public ArrayList<View> transform(String input, JSONArray previews, XmlSchema source) {
		ArrayList<View> result = new ArrayList<View>();

		Iterator<?> i = previews.iterator();
		while(i.hasNext()) {
			XmlSchema target = null;
			JSONObject preview = (JSONObject) i.next();
			
//			log.debug("getting preview for : " + preview);
			
			// default preview tab values;
			String type = TRANSFORM_CUSTOM;
			String output_type = View.TYPE_TEXT;
			String url = null;
			String output = null;
			String label = "Preview";
			ItemTransform transform = null;
			

			View view = null;

			// load preview tab values
			if(preview.containsKey("type")) {
				type = preview.get("type").toString();
			}

			if(preview.containsKey("label")) {
				label = preview.get("label").toString();
			}
			
			if(preview.containsKey("output")) {
				output_type = preview.get("output").toString();
			}
			
			// initialise preview transform
			if(preview.containsKey("xsl") || preview.containsKey("target")) {
				try {
					XSLTransform xslt = new XSLTransform();

					if(preview.containsKey("xsl")) {
						String xsl = preview.get("xsl").toString();
						File file = new File(Config.getXSLPath(xsl));	
						xsl = StringUtils.xmlContents(file);
						xslt.setXSL(xsl);						
					}
					
					if(preview.containsKey("target")) {
						String schema = preview.get("target").toString();
						target = DB.getXmlSchemaDAO().getByName(schema);
						if(!preview.containsKey("label")) label = target.getName();
						if(!preview.containsKey("xsl")) {
							Crosswalk crosswalk = new Crosswalk();
							crosswalk.setSourceSchema(source);
							crosswalk.setTargetSchema(target);
//							log.debug("Trying to fetch crosswalk: " + source + " -> " + target);
							List<Crosswalk> results = DB.getCrosswalkDAO().findBySourceAndTarget(source, target);
							if(!results.isEmpty()) {
								crosswalk = results.get(0);
//								log.debug("Use crosswalk for preview: " + crosswalk.getSourceSchema() + " -> " + crosswalk.getTargetSchema());
//								log.debug("-- with xsl: " + crosswalk.getXsl());
								xslt.setXSL(crosswalk.getXsl());								
							}
						}
					}

					if(preview.containsKey("parameters")) {
						JSONArray previewParameters = (JSONArray) preview.get("parameters");
						HashMap<String, String> parameters = new HashMap<String, String>();
						
						for(Object o: previewParameters) {
							JSONObject parameter = (JSONObject) o;
							if(parameter.containsKey("name") && parameter.containsKey("value")) {
								String name = parameter.get("name").toString();
								String value = parameter.get("value").toString();
								if(!parameter.containsKey("type") || parameter.get("type").toString().equalsIgnoreCase("constant")) {
									parameters.put(name, value);
								} else if(parameter.get("type").toString().equalsIgnoreCase("mint")) {
									parameters.put(name, Config.get(value));
								}								
							}
						}
						
						xslt.setParameters(parameters);
					}
					
					transform = xslt;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(preview.containsKey("jsp")) {
				String jsp = preview.get("jsp").toString();
				view = new View(View.key(View.GROUP_SCHEMA, label), label, View.TYPE_JSP);
				view.setContent(input);
				view.setUrl(jsp);
			} else if(preview.containsKey("transform")) {
				String cname = preview.get("transform").toString();
				try {
					Class c = ChainTransform.class.getClassLoader().loadClass(cname);
					transform = (ItemTransform) c.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			

			// if valid transform then generate tab
			if(transform != null) {
				view = new View(View.key(View.GROUP_SCHEMA, label), label, output_type);
				
				try {
					output = transform.transform(input);					
					view.setContent(output);
					view.setUrl(url);					
				} catch(Exception e) {
					view.setException(e);
					view.setType(View.TYPE_WARNING);
					view.setContent("Item view is empty.");					
				}
			}
			
			// if valid tab then add and check for next transformation.
			if(view != null) {
				if(target != null) {
					try {
						view.validate(target);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				result.add(view);
				
				if(preview.containsKey("preview")) {
					JSONArray p = (JSONArray) preview.get("preview");
					ArrayList<View> more = this.transform(output, p, target);
					result.addAll(more);
				}
			}
		}

		return result;
	}
	
	/**
	 * Get a list of item views defined in schema's configuration preview section.
	 * @param previews
	 * @return
	 */
	
	public static List<View> definedViews(XmlSchema schema) {
		return ChainTransform.definedViews(schema.getPreviews(), false);
	}
	
	public static List<View> definedViews(XmlSchema schema, boolean includeHidden) {
		return ChainTransform.definedViews(schema.getPreviews(), includeHidden);
	}

	public static List<View> definedViews(JSONArray previews) {
		return ChainTransform.definedViews(previews, false);
	}

	public static List<View> definedViews(JSONArray previews, boolean includeHidden) {
		List<View> result = new ArrayList<View>();
		
		Iterator<?> i = previews.iterator();
		while(i.hasNext()) {
			JSONObject preview = (JSONObject) i.next();
			
			// default preview tab values;
			String label = "Preview";
			String type = TRANSFORM_CUSTOM;
			String output = View.TYPE_TEXT;
			boolean hidden = false;
			
			// load preview tab values
			if(preview.containsKey("type"))
				type = preview.get("type").toString();

			if(preview.containsKey("target"))
				label = preview.get("target").toString();
			
			if(preview.containsKey("label"))
				label = preview.get("label").toString();
			
			if(preview.containsKey("output"))
				output = preview.get("output").toString();
			
			if(preview.containsKey("hide")) {
				hidden = true;
			}
			
			View view = new View(View.key(View.GROUP_SCHEMA, label), label, type, output);
			if(includeHidden || !hidden) result.add(view);
						
			if(preview.containsKey("preview")) {
				JSONArray p = (JSONArray) preview.get("preview");
				result.addAll(ChainTransform.definedViews(p));				
			}
		}

		return result;
	}
}
