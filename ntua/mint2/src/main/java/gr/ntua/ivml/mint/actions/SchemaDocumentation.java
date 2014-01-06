package gr.ntua.ivml.mint.actions;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.JSONUtils;

import net.minidev.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	@Result(name="error", location="json.jsp"),
	@Result(name="success", location="json.jsp")
})

public class SchemaDocumentation extends GeneralAction {

	public static final Logger log = Logger.getLogger(Tree.class ); 
	public JSONObject json;
	public long schemaId;
	public String element;

	@Action(value="SchemaDocumentation")
	public String execute() {
		json = new JSONObject();

		try {
			XmlSchema schema = DB.getXmlSchemaDAO().findById(this.getSchemaId(), true);
			if(schema != null) {
				JSONObject documentation = JSONUtils.parse(schema.getDocumentation());
				String key = this.element;
				
				if(documentation.containsKey(key)) {
					json.put("documentation", documentation.get(key).toString());
				} else {
					json.put("error", "No documentation for '" + key + "'");
				}
				
				json.put("key", key);
				json.put("schemaId", this.getSchemaId());
			}
		} catch( Exception e ) {
			json.put( "error", e.getMessage());
			log.error( "No values", e );
		}

		return SUCCESS;
	}
	
	public long getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(long schemaId) {
		this.schemaId = schemaId;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public JSONObject getJson() {
		return json;
	}
	
	public String getElement() {
		return this.element;
	}
	
	public void setElement(String element) {
		this.element = element;
	}
}