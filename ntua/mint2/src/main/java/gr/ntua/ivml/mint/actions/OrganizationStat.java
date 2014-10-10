package gr.ntua.ivml.mint.actions;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.db.Meta;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.persistent.Organization;
import gr.ntua.ivml.mint.persistent.PublicationRecord;
import gr.ntua.ivml.mint.persistent.Transformation;
import gr.ntua.ivml.mint.util.Config;
import gr.ntua.ivml.mint.util.Tuple;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	@Result(name="error", location="json.jsp"),
	@Result(name="success", location="json.jsp")
})

public class OrganizationStat extends GeneralAction {

	public static final Logger log = Logger.getLogger(Stats.class ); 
	public JSONObject json;
	public String organizationId;
	
	
	@Action(value="OrganizationStat")
	public String execute() throws ParseException  {
		String str = Config.get("orgstatsignore");
		json = new JSONObject();
		

		if (this.organizationId != null) {
			Long orgid = Long.parseLong(organizationId);
			json.put("result", listOrganizationOverall(orgid));
		} else {
			JSONArray array = new JSONArray();
			List<Organization> orgs = DB.getOrganizationDAO().findAll();
			List<String> ignoreList = null ;
			if (Config.has("orgtargets")) {
				net.minidev.json.JSONArray jsontargets = getGoalTargets();
				if (jsontargets != null) {
					Iterator it = jsontargets.iterator();

					while (it.hasNext()) {
						JSONObject orgtarget = (JSONObject) it.next();
						Integer orgid = (Integer) orgtarget.get("orgID");
						array.add(listOrganizationOverall(orgid.longValue()));

					}
				}
			}

			else {
				if (Config.has("orgstatsignore")) {
					ignoreList = Arrays.asList(str.split(","));
					for (Organization org : orgs) {
						if (ignoreList.indexOf(Long.toString(org.getDbID())) == -1) {
							array.add(listOrganizationOverall(org.getDbID()));
						}
					}
				}
				
				else if (!Config.has("orgstatsignore")) {

					List<Organization> orglist = new ArrayList<Organization>();
					for (Organization org : orgs) {

						String targetJson = Meta.get(org, "targets");
						if (targetJson != null) {
							orglist.add(org);
							// list with orgs with target meta json iterate the
							// list .

						}
					}
					if (orglist.isEmpty()) {
						for (Organization org : orgs) {
							array.add(listOrganizationOverall(org.getDbID()));
						}
					} else {
						for (Organization org : orglist) {
							array.add(listOrganizationOverall(org.getDbID()));
						}
					}

				}
			}
			json.put("result", array);

		}

		return SUCCESS;
	}
	
	
	
	public JSONObject listOrganizationOverall(Long organizationId) throws ParseException {
		List<DataUpload> res;

		res = DB.getDataUploadDAO().findByOrganization(
				DB.getOrganizationDAO().getById(organizationId, false));

		long itemsimported = 0;
		long itemstransformed = 0;
		long itemspublished = 0;
		/*long itemspublishedtocoreschema = 0;
		long itemspublishedtoedmschema = 0;
		long itemspublishedtoitemclipv2chema = 0;
		long itemspublishedtoseriesv2 = 0;*/
		List<Transformation> transformations = new ArrayList<Transformation>();
		for (DataUpload du : res) {
			if (du.getItemCount() != -1) {
				itemsimported += du.getItemCount();
			}
			transformations = du.getTransformations();
			for (Transformation t : transformations) {
				if (t.getValidItemCount() != -1) {
					itemstransformed += t.getValidItemCount();
				}
			}

		}
		
		Map<String, Integer> countedItemsperSchema = new HashMap<String, Integer>();
		if (Config.has("schema.filter")) {
			String schemas = Config.get("schema.filter");
			List<String> schemaList = Arrays.asList(schemas.split(","));
			for (String str:schemaList){
				countedItemsperSchema.put(str, 0);
			}
		
		}

		List<PublicationRecord> prs = new ArrayList<PublicationRecord>();
		;
		prs = DB.getPublicationRecordDAO().findByOrganization(
				DB.getOrganizationDAO().getById(organizationId, false));
		for (PublicationRecord pr : prs) {
			if (pr.getPublishedDataset().isOk()) {
				itemspublished += pr.getPublishedDataset().getValidItemCount();
				String targetSchemaName = "";
				if (pr.getPublishedDataset().getSchema() != null) {
					targetSchemaName = pr.getPublishedDataset().getSchema()
							.getName();
					
					if (countedItemsperSchema.containsKey(targetSchemaName)){
						countedItemsperSchema.put(targetSchemaName, countedItemsperSchema.get(targetSchemaName)+pr.getPublishedItemCount());
					}
					
					/*if (targetSchemaName.equals("EUscreenXL-Core")) {
						itemspublishedtocoreschema += pr
								.getPublishedItemCount();
					} else if (targetSchemaName.equals("EUscreenXL-EDM")) {
						itemspublishedtoedmschema += pr.getPublishedItemCount();
					} else if (targetSchemaName
							.equals("EUscreenXL ITEM/CLIP v2")) {
						itemspublishedtoitemclipv2chema += pr
								.getPublishedItemCount();
					} else if (targetSchemaName.equals("EUscreenXL Series v2")) {
						itemspublishedtoseriesv2 += pr.getPublishedItemCount();
					}*/

				}

			}

		}
		


		JSONObject ajson = new JSONObject();
	//	ajson.put("organizationId", organizationId);

		/*ajson.put("Name", DB.getOrganizationDAO()
				.getById(organizationId, false).getEnglishName());
		ajson.put("Country",
				DB.getOrganizationDAO().getById(organizationId, false)
						.getCountry());
		ajson.put("Imported", itemsimported);
		ajson.put("Transformed", itemstransformed);
		
		ajson.put("Published", itemspublished);*/

		
		Map<String, String> basicinfo = new HashMap<String, String>();
		Map<String, Long> basic_counts = new HashMap<String, Long>();
		basicinfo.put("Name", DB.getOrganizationDAO().getById(organizationId, false).getEnglishName());
		basicinfo.put("Country",DB.getOrganizationDAO().getById(organizationId, false).getCountry());
		basic_counts.put("Imported", itemsimported);
		basic_counts.put("Transformed", itemstransformed);
		basic_counts.put("Published", itemspublished);
		ajson.putAll(basicinfo);
		ajson.putAll(basic_counts);
		
		if (!countedItemsperSchema.isEmpty()){
			Set<String> schemaKeys = countedItemsperSchema.keySet();
			Iterator sit = schemaKeys.iterator() ;
			while (sit.hasNext()){
				String schema = (String) sit.next();
				ajson.put("Published to "+ schema, countedItemsperSchema.get(schema));
			}
		}
		
		
		/*ajson.put("PublishedtoEDM", itemspublishedtoedmschema);
		ajson.put("PublishedtoCore", itemspublishedtocoreschema);
		ajson.put("PublishedtoItemClipv2", itemspublishedtoitemclipv2chema);
		ajson.put("PublishedtoSeriesv2", itemspublishedtoseriesv2);*/
		
		//Custom.organizationStats(ajson);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Organization org  = DB.getOrganizationDAO().getById(organizationId, false);
		if (org.getFinalTarget()!=null){
			Tuple<Date,Integer> ftp =  org.getFinalTarget();
			ajson.put("Final Target" + sdf.format(ftp.u), ftp.v);
		}
		if (org.getCurrentTarget()!=null){
			Tuple<Date,Integer> ftp =  org.getCurrentTarget();
			ajson.put("Current Target" + sdf.format(ftp.u), ftp.v);
		}
		
		net.minidev.json.JSONArray jsontargets  = getGoalTargets();
		if (jsontargets != null) {
			Iterator it = jsontargets.iterator();

			while (it.hasNext()) {
				JSONObject orgtarget = (JSONObject) it.next();
				Integer orgid = (Integer) orgtarget.get("orgID");
				ajson.put("AggregationTarget", 0);
				ajson.put("CoreTarget", 0);

				if (orgid.longValue() == organizationId) {
					System.out.println("Matched :" + orgid);
					System.out.println("aggregation : "
							+ orgtarget.get("aggregation"));
					System.out.println("core : " + orgtarget.get("core"));
					ajson.put("AggregationTarget", orgtarget.get("aggregation"));
					ajson.put("CoreTarget", orgtarget.get("core"));
					break;
				}

			}
		}
		
		return ajson;
	}
	
	
	
	public net.minidev.json.JSONArray getGoalTargets() throws ParseException{
	
		net.minidev.json.JSONArray jsontargets = null;

		if (Config.has("orgtargets")) {

			String targets = Config.get("orgtargets");

			JSONObject thejson = null;

			thejson = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE)
					.parse(targets);
			jsontargets = (net.minidev.json.JSONArray) thejson
					.get("targetorglist");

		}
		return jsontargets;
	}
	
	
	public String getOrganizationId() {
		return organizationId;
	}


	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}


	public void setJson(JSONObject json) {
		this.json = json;
	}

	public JSONObject getJson() {
		return json;
	}
	
	
}