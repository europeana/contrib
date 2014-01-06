package gr.ntua.ivml.mint.actions;

import net.minidev.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	@Result(name="error", location="json.jsp"),
	@Result(name="success", location="json.jsp")
})

public class Preferences extends GeneralAction {

	public static final Logger log = Logger.getLogger(Preferences.class ); 
	public JSONObject json;
	public String key;

	@Action(value="Preferences")
	public String execute() {
		json = new JSONObject();
		
		try {
			JSONObject preferences = gr.ntua.ivml.mint.util.Preferences.getJSON(getUser(), getKey());			
			json.put("preferences", preferences); 
		} catch( Exception e ) {
			json.put( "error", e.getMessage());
		}
		
		return SUCCESS;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public JSONObject getJson() {
		return json;
	}
}