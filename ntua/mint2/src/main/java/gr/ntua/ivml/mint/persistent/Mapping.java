package gr.ntua.ivml.mint.persistent;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.db.Meta;
import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.util.JSONUtils;
import gr.ntua.ivml.mint.util.StringUtils;
import gr.ntua.ivml.mint.util.Tuple;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Mapping implements Lockable {
	public static final Logger log = Logger.getLogger(Mapping.class );
	public static final String META_RECENT_DATASETS = "recent.datasets";
	public static final String META_REVERT_VERSION = "revert.version";

	Long dbID;
	
	String name;
	Date creationDate;
	Date lastModified;
	
	Organization organization;
	String jsonString;
	String xsl;
	
	XmlSchema targetSchema;
	
	boolean shared;
	boolean finished;
	
	
	
	
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public Long getDbID() {
		return dbID;
	}
	public void setDbID(Long dbId) {
		this.dbID = dbId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	public String getXsl() {
		return xsl;
	}
	public void setXsl(String xsl) {
		this.xsl = xsl;
	}
	
	public boolean isXsl() {
		return (this.xsl != null);
	}
	
	public XmlSchema getTargetSchema() {
		return targetSchema;
	}
	public void setTargetSchema(XmlSchema targetSchema) {
		this.targetSchema = targetSchema;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	/**
	 * Return a Mappings json handler that represents contents of this mapping.
	 * @return a Mappings json handler or null if jsonString is null or empty.
	 */
	public Mappings getMappings() {
		Mappings mappings = null;
		
		if(this.jsonString != null && this.jsonString.length() > 0) {
			try {
				mappings = new Mappings(this.jsonString);
			} catch (ParseException e) {
				log.error("Could not parse mappings in this.jsonString");
				e.printStackTrace();
			}
		}
		
		return mappings;
	}
	
	public void applyAutomaticMappings(Dataset ds) {
		this.applyBackwardsXpathMappings(ds);
	}
	
	private ArrayList<Element> selectNameHandlers(XpathHolder xpath, Mappings topHandler) {
		String name = xpath.getName();
		String uri = xpath.getUri();
		String prefix =  null;
		if(uri != null) prefix = topHandler.getNamespacePrefix(uri); 

		ArrayList<Element> handlers = topHandler.getHandlersForPrefixAndName(prefix, name);
		
		return handlers;
	}
	
	private ArrayList<Element> filterBackwardsXpathHandlers(XpathHolder xpath, Mappings topHandler, ArrayList<Element> list, int levels) {
//		System.out.println("filter handlers level: " + levels);
		ArrayList<Element> handlers = new ArrayList<Element>();
		
		XpathHolder parent = xpath;
		for(int i = 0; i < levels; i++) {
			if(parent != null) {
				parent = parent.getParent();
			}
		}
		
		if(parent != null) {
			String name = parent.getName();
			String uri = parent.getUri();
			String prefix =  null;
			if(uri != null) prefix = topHandler.getNamespacePrefix(uri);
//			System.out.println("parent xpath = " + prefix + ":" + name);
			for(Element handler: list) {
				Element parentHandler = handler;
				for(int i = 0; i < levels; i++) {
					if(parentHandler != null) {
//						System.out.println("handler level " + i + " = " + parentHandler.getFullName());
						parentHandler = parentHandler.getParent();
//						if(parentHandler != null) System.out.println("parent level " + i + " = " + parentHandler.getFullName());
					}
				}
				

				if(parentHandler != null) {
					if(parentHandler.checkPrefixAndName(prefix, name)) handlers.add(handler);
				}
			}
		}
		
		if(handlers.size() > 0) {
			ArrayList<Element> filtered = filterBackwardsXpathHandlers(xpath, topHandler, handlers, levels+1);
			if(filtered.size() > 0) handlers = filtered;
		}
		
		if(handlers.size() == 0) handlers = list;
		
		return handlers;
	}
	
	private ArrayList<Element> selectBackwardsXpathHandlers(XpathHolder xpath, Mappings topHandler) {
		ArrayList<Element> handlers = selectNameHandlers(xpath, topHandler);

		if(handlers.size() > 0) {
//			System.out.println("selected handlers: " + handlers);
			ArrayList<Element> filtered = filterBackwardsXpathHandlers(xpath, topHandler, handlers, 1);
			if(filtered.size() > 0) {
				handlers = filtered;
			}
		}
		
		return handlers;
	}
	
	public void applyBackwardsXpathMappings(Dataset du) {
		List<XpathHolder> input = du.getItemRootXpath().getChildrenRecursive();
		Mappings topHandler = this.getMappings();

		for(XpathHolder xpath: input) {
//			XpathHolder text = xpath.getTextNode();
//			
			if(!xpath.isAttributeNode()) {
				ArrayList<Element> handlers = selectBackwardsXpathHandlers(xpath, topHandler);
				//System.out.println(prefix + " : "  + name + " : text = " + text + " : handlers = " + handlers);
				if(handlers.size() > 0) {
					Element handler = handlers.get(0);
					handler.addXPathMapping(xpath.getXpathWithPrefix(true));
					
					// ignore namespace for attributes
					List<? extends XpathHolder> attributes = xpath.getAttributes();
					for(XpathHolder attribute : attributes) {
						String aname = attribute.getName();
						//System.out.println("ATTR: " + aname);
						
						Element ahandler = handler.getAttributeByName(aname);
						if(ahandler != null) {
							ahandler.addXPathMapping(attribute.getXpathWithPrefix(true));
						}
					}
				}
			}
		}
		
		this.setJsonString(topHandler.toString());
	}

	public void applyNameMappings(Dataset ds) {
		List<XpathHolder> input = ds.getItemRootXpath().getChildrenRecursive();
		Mappings topHandler = this.getMappings();

		for(XpathHolder xpath: input) {
			XpathHolder text = xpath.getTextNode();
			
			if(text != null) {
				ArrayList<Element> handlers = selectNameHandlers(xpath, topHandler);

				if(handlers.size() > 0) {
					for(Element handler: handlers) {
						handler.addXPathMapping(xpath.getXpathWithPrefix(true));
					
						// ignore namespace for attributes
						List<? extends XpathHolder> attributes = xpath.getAttributes();
						for(XpathHolder attribute : attributes) {
							String aname = attribute.getName();
							//System.out.println("ATTR: " + aname);
							
							Element ahandler = handler.getAttributeByName(aname);
							if(ahandler != null) {
								ahandler.addXPathMapping(attribute.getXpathWithPrefix(true));
							}
						}
					}					
				}
			}
		}
		
		this.setJsonString(topHandler.toString());
	}

	@Override
	public String getLockname() {
		return "Mapping " + name ;
	}
	
	//Arne check if this is correct
	public boolean isLocked( User u, String sessionId ) {
		return !DB.getLockManager().canAccess( u, sessionId, this );
	}
	
	public void applyConfigurationAutomaticMappings(Dataset ds) {
		XmlSchema schema = this.getTargetSchema();

		if(schema != null) {
			XpathHolder nativeIdXpath = ds.getItemNativeIdXpath();
			XpathHolder labelXpath = ds.getItemLabelXpath();
			XpathHolder levelXpath = ds.getItemRootXpath();
			
			JSONObject automaticMappings = schema.getAutomaticMappings();
			if(automaticMappings != null && !automaticMappings.keySet().isEmpty()) {
				Mappings topHandler = this.getMappings();

				for(Object key: automaticMappings.keySet()) {
					String path = (String) key;
					
					JSONArray mappings = (JSONArray) automaticMappings.get(path);

					ArrayList<Element> handlers = topHandler.getHandlersForPath(path);
					if(handlers != null && !handlers.isEmpty()) {
						for(Element handler: handlers) {
							for(Object o: mappings) {								
								if(o instanceof String) {
									String constant = (String) o;
									handler.addConstantMapping(constant);
								} else if(o instanceof JSONObject) {
									JSONObject mapping = (JSONObject) o;
									if(mapping.containsKey("type")) {
										String type = mapping.get("type").toString();
										if(type.equals("level") && levelXpath != null) {
											handler.addXPathMapping(levelXpath.getXpathWithPrefix(true));
										} else if(type.equals("label") && labelXpath != null) {
											handler.addXPathMapping(labelXpath.getXpathWithPrefix(true));
										} else if(type.equals("id") && nativeIdXpath != null) {
											handler.addXPathMapping(nativeIdXpath.getXpathWithPrefix(true));
										} else if(type.equals("parameter") && mapping.containsKey("name")) {
											Map<String, XmlSchema.Parameter> parameters = schema.getParameters();
											if(parameters != null) {
												XmlSchema.Parameter parameter = parameters.get(mapping.get("name").toString());
												handler.addParameterMapping(parameter.getName(), parameter.getValue());
											}
										}
									}
								}
							}
						}
					}
				}
				
				this.setJsonString(topHandler.toString());
			}
		}		
	}
		
	public List<Dataset> getRecentDatasets() {
		List<Dataset> results = new ArrayList<Dataset>();
		JSONArray recent = Meta.getArray(this, Mapping.META_RECENT_DATASETS);

		Iterator<?> i = recent.iterator();
		while(i.hasNext()) {
			JSONObject entry = (JSONObject) i.next();
			if(entry.containsKey("dataset")) {
				Dataset dataset = DB.getDatasetDAO().findById(Long.parseLong(entry.get("dataset").toString()), false);
				results.add(dataset);
			}
		}
		return results;
	}
		
	public void addRecentDataset(Dataset dataset) {
		JSONArray recent = Meta.getArray(this, Mapping.META_RECENT_DATASETS);
		
		JSONObject newEntry = new JSONObject();
		newEntry.put("timestamp", new Date().getTime());
		newEntry.put("dataset", dataset.getDbID());
		
		Iterator<?> i = recent.iterator();
		while(i.hasNext()) {
			JSONObject entry = (JSONObject) i.next();
			if(entry.containsKey("dataset") && entry.get("dataset").toString().equals(newEntry.get("dataset").toString())) {
				recent.remove(entry);
				break;
			}
		}
		recent.add(0, newEntry);
		Meta.put(this, Mapping.META_RECENT_DATASETS, recent.toString());
	}
	
	public static List<Mapping> getRecentMappings(Dataset dataset) {
		return Mapping.getRecentMappings(dataset, DB.getMappingDAO().findAll());
	}
	
	public static List<Mapping> getRecentMappings(Dataset dataset, List<Mapping> mappings) {
		List<Mapping> result = new ArrayList<Mapping>();
		List<Tuple<Date, Mapping>> recent = new ArrayList<Tuple<Date, Mapping>>();
		
		for(Mapping mapping: mappings) {
			JSONArray info = Meta.getArray(mapping, Mapping.META_RECENT_DATASETS);
			
			Iterator<?> i = info.iterator();
			while(i.hasNext()) {
				JSONObject entry = (JSONObject) i.next();
				long ds = Long.parseLong(entry.get("dataset").toString());
				if(ds == dataset.getDbID()) {
					log.debug(entry);
					Object timestamp = entry.get("timestamp");

					Date date = JSONUtils.toDate(timestamp);
					if(date != null) recent.add(new Tuple<Date, Mapping>(date, mapping));
				}
			}
		}
		
		if(recent.size() > 1) Collections.sort(recent, new Comparator<Tuple<Date, Mapping>>() {
			@Override
			public int compare(Tuple<Date, Mapping> o1, Tuple<Date, Mapping> o2) {
				return o2.u.compareTo(o1.u);
			}
		});
		
		if(recent.size() > 0) {
			for(Tuple<Date, Mapping> tuple: recent) {
				result.add(tuple.v);
			}
		}
		
		System.out.println(result);
		return result;
	}
	
	public List<String> getStoredVersions() {
		return Meta.getAllPropertyKeys(this, Mapping.META_REVERT_VERSION);
	}
	
	public String getVersionKey(String version) {
		return Mapping.META_REVERT_VERSION + "." + version;
	}

	/**
	 * Store current mapping JSON string in meta table with specified version identifier, in order to be able to revert to it later.
	 * @param version identifier for stored version.
	 */
	public void storeAsVersion(String version) {
		Meta.put(this, this.getVersionKey(version), this.getJsonString());
	}
	
	/**
	 * Get JSON string of stored mapping version
	 * @param version identifier of stored version
	 * @return JSON string of stored mapping version or null if stored version does not exist
	 */
	public String getVersion(String version) {
		String stored = Meta.get(this, this.getVersionKey(version));
		return stored;
	}
	
	/**
	 * Reverts to a stored version, if version exists. Otherwise do nothing.  
	 * @param version identifier of stored version
	 * @return true if version exists and mapping is reverted.
	 */
	public boolean revertToVersion(String version) {
		String reverted = this.getVersion(version);
		if(reverted != null) {
			this.setJsonString(reverted);
			return true;
		}
		
		return false;
	}
	
	public JSONObject toJSON() {
		JSONObject res = new JSONObject();	
		res.put( "dbID", getDbID());
		res.put( "name", getName());
		res.put( "date", StringUtils.isoTime(creationDate));
		res.put("lastModified",StringUtils.isoTime(lastModified));
		if( getOrganization() != null ) {
			JSONObject org = new JSONObject();
			org.put( "name", getOrganization().getName());
			org.put( "dbId", getOrganization().getDbID());	
			res.put( "organization", org);
		}
		
		if ( getTargetSchema() != null) {
			JSONObject schema = new JSONObject();
			schema.put("name", getTargetSchema().getName());
			schema.put("dbId", getTargetSchema().getDbID());
			res.put( "Schema", schema);
		}
		return res;
	}
}
