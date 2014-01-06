package gr.ntua.ivml.mint.mapping.model;

import gr.ntua.ivml.mint.mapping.JSONHandler;
import gr.ntua.ivml.mint.util.Config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jfree.util.Log;

import net.minidev.json.JSONObject;

public class SchemaConfiguration extends JSONHandler {
	public static final String CONFIGURATION_VIEWS = "views";
	public static final String CONFIGURATION_TYPES = "types";
	public static final String CONFIGURATION_VIEWS_TYPE = "type";
	public static final String CONFIGURATION_VIEWS_NAME = "name";
	public static final String CONFIGURATION_VIEWS_DESCRIPTION = "description";
	public static final String CONFIGURATION_VIEWS_LABEL = "label";
	public static final String CONFIGURATION_VIEWS_XPATH = "xpath";
	public static final String CONFIGURATION_VIEWS_CONTENTS = "contents";
	
	public static class Views extends JSONHandler {
		public static class Type extends Views {
			public Type(JSONObject type) {
				super(type);
			}

			public String getName() { return this.getOptString(CONFIGURATION_VIEWS_NAME); }
		}

		public static class View extends JSONHandler {
			public View(JSONObject view) {
				super(view);
			}
			
			public String getType() { return this.getOptString(CONFIGURATION_VIEWS_TYPE); }
			public String getName() { return this.getOptString(CONFIGURATION_VIEWS_NAME); }
			public String getDescription() { return this.getOptString(CONFIGURATION_VIEWS_DESCRIPTION); }
			public String getXPath() { return this.getOptString(CONFIGURATION_VIEWS_XPATH); }
			public String getLabel() {
				if(this.has(CONFIGURATION_VIEWS_LABEL)) return this.getString(CONFIGURATION_VIEWS_LABEL);
				else return this.getOptString(CONFIGURATION_VIEWS_NAME);
			}
			
			public List<View> getContents() {
				return this.getList(CONFIGURATION_VIEWS_CONTENTS, View.class);
			}
			
			public void expand(Map<String, View> types) {
				String type = this.getType();
				
				if(types.containsKey(type)) {
					View typeView = types.get(type);
					System.out.println("expanding " + this.getLabel() + " with type " + typeView.getLabel());
					this.merge(typeView);
				}
				
				List<View> contents = this.getContents();
				for(View view: contents) {
					view.expand(types);
				}
			}
		}
		
		public Views(JSONObject views) {
			super(views);
		}
		
		public List<View> getViews() {
			return this.getList(CONFIGURATION_VIEWS, View.class);
		}
		
		public Map<String, View> getTypes() {
			return this.getMap(CONFIGURATION_TYPES, View.class);
		}
		
		public void expand() {
			List<View> views = this.getViews();
			Map<String, View> types = this.getTypes();
			
			for(View view: views) {
				view.expand(types);
			}
		}
	}
	
	public SchemaConfiguration() {
		super(new JSONObject());
	}
	
	public SchemaConfiguration(JSONObject configuration) {
		super(configuration);
	}
	
	public Views getViews() {
		Views views = null;
		
		try {
			views = new Views(this.getObjectFromFileOrDirect(CONFIGURATION_VIEWS, Config.getViewsDir()));
			views.expand();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return views;
	}
}
