package gr.ntua.ivml.mint.persistent;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.mapping.JSONMappingHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class Mapping implements Lockable {
	Long dbID;
	
	String name;
	Date creationDate;
	Organization organization;
	String jsonString;

	// This should be an object, but name will do
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

	@Override
	public String getLockname() {
		return "Mapping " + name ;
	}
	
	//Arne check if this is correct
	public boolean isLocked( User u, String sessionId ) {
		return !DB.getLockManager().canAccess( u, sessionId, this );
	}
	
	public JSONMappingHandler getMappingHandler() {
		JSONObject object = (JSONObject) JSONSerializer.toJSON(this.jsonString);
		JSONMappingHandler handler = new JSONMappingHandler(object);
		
		return handler;
	}
	
	public void applyAutomaticMappings(DataUpload du) {
		List<XpathHolder> input = du.getRootXpath().getChildrenRecursive();
		JSONMappingHandler topHandler = this.getMappingHandler();

		for(XpathHolder xpath: input) {
			String name = xpath.getName();
			String uri = xpath.getUri();
			String prefix =  null;
			if(uri != null) prefix = topHandler.getNamespacePrefix(uri); 
			XpathHolder text = xpath.getTextNode();
			
			if(text != null) {
				ArrayList<JSONMappingHandler> handlers = topHandler.getHandlersForPrefixAndName(prefix, name);
				System.out.println(prefix + " : "  + name + " : text = " + text + " : handlers = " + handlers);				
				if(handlers.size() > 0) {
					for(JSONMappingHandler handler: handlers) {
						handler.addXPathMapping(xpath.getXpathWithPrefix(true));
					
						// ignore namespace for attributes
						List<? extends XpathHolder> attributes = xpath.getAttributes();
						for(XpathHolder attribute : attributes) {
							String aname = attribute.getName();
							System.out.println("ATTR: " + aname);
							
							JSONMappingHandler ahandler = handler.getAttributeByName(aname);
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
}
