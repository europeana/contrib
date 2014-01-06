package gr.ntua.ivml.mint.mapping;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.persistent.Mapping;
import gr.ntua.ivml.mint.util.Config;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.*;

import org.apache.log4j.Logger;

public class MappingManager extends AbstractMappingManager {	
	protected static final Logger log = Logger.getLogger( MappingManager.class);
	
	Mapping mapping = null;
	
	public MappingManager() {
	}
	
	public void init(String id) {
		long t = System.currentTimeMillis();
		Mapping m = DB.getMappingDAO().getById(Long.parseLong(id), false);
//		log.debug("ORGANIZATION: " + m.getOrganization().getName());
		this.init(m);
		t = System.currentTimeMillis() - t;
		
		log.debug("initialise mapping " + this.mapping.getTargetSchema().getName() + ":" + this.mapping.getName() + " in " + (t/1000.0) + " seconds");
	}
	
	public void init(Mapping mp) {
		log.debug("init mapping");
		this.mapping = mp;

		String savedMappings = null;
		if(mapping != null) {
//			log.debug("get saved mappings");
			savedMappings = this.mapping.getJsonString();
		} else {
			log.error("mapping object is null");
		}
		
		Mappings handler;
		try {
			handler = new Mappings(savedMappings);
			String version = handler.getVersion();

			boolean upgraded = false;
			if(!handler.isLatestVersion()) {
				upgraded = MappingConverter.upgradeToLatest(handler);
			}

			this.init(handler, this.mapping.getTargetSchema());

			if(upgraded) {
				log.debug("upgrade mapping");
				if(Config.getBoolean("mapping.backupOnUpgrade", true)) {
					log.debug("store backup for version: " + version);
					DB.getStatelessSession().beginTransaction();
					mapping.storeAsVersion(version);
				}
				
				this.save();
			}

			log.debug("UPGRADED MAPPING: " + targetDefinition.toString());

			
			log.debug("init complete");
		} catch (ParseException e) {
			log.error("could not parse saved mapping");
			e.printStackTrace();
			
			log.error("init failed");
		}
	}
	
	public JSONObject getMetadata() {
		JSONObject result = new JSONObject();
		
		result.put("name", this.mapping.getName());
		result.put("created", this.mapping.getCreationDate().toString());
		result.put("organization", this.mapping.getOrganization().getName());
		result.put("schema", this.mapping.getTargetSchema().getName());
		
		return result;
	}

	protected synchronized void save() {
		log.debug("SAVING");
		if(this.mapping != null) {
			this.mapping = (Mapping) DB.getSession().merge(this.mapping);
			String targetDefinitionString = this.getTargetDefinition().toString();			
			this.mapping.setJsonString(targetDefinitionString);
			DB.commit();
			log.debug("Mapping definition saved");
		} else {
			log.debug("No mapping object loaded");
		}
	}
}
