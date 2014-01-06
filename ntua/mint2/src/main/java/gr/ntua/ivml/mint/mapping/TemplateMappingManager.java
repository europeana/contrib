package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

import org.apache.log4j.Logger;

public class TemplateMappingManager extends AbstractMappingManager {	
	protected static final Logger log = Logger.getLogger( TemplateMappingManager.class);
	
	XmlSchema schema = null;
	
	public TemplateMappingManager() {
	}
	
	public void init(String id) {
		long t = System.currentTimeMillis();
		this.init(DB.getXmlSchemaDAO().getById(Long.parseLong(id), false));
		t = System.currentTimeMillis() - t;
		
		log.debug("initialise template mapping " + this.schema.getName() + " in " + (t/1000.0) + " seconds");
	}
	
	public void init(XmlSchema xmlsch) {
		log.debug("init template mapping");
		this.schema = xmlsch;

		Mappings mappingObject = null;
		if(schema != null) {
			log.debug("get saved mappings");
			String savedMappings = this.schema.getJsonTemplate();
			log.debug("serialize json mapping");
			try {
				mappingObject = new Mappings(savedMappings);
				this.init(mappingObject, this.schema);
				log.debug("init complete");
			} catch (ParseException e) {
				log.debug("Could not parse saved mapping");
				e.printStackTrace();
				log.debug("init failed");
			}
		} else {
			log.error("template mapping object is null");
		}

		
	}
	
	public JSONObject getMetadata() {
		JSONObject result = new JSONObject();
		
		result.put("name", this.schema.getName());
		result.put("created",this.schema.getCreated().toString());
		return result;
	}

	protected void save() {
		if(this.schema != null) {
			this.schema = (XmlSchema) DB.getSession().merge(this.schema);
			String targetDefinitionString = this.getTargetDefinition().toString();	
			this.schema.setJsonTemplate(targetDefinitionString);
			DB.commit();
			log.debug("Template Mapping definition saved");
		} else {
			log.debug("No template mapping object loaded");
		}
	}
}
