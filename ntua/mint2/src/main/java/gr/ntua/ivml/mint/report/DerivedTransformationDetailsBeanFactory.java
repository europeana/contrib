
package gr.ntua.ivml.mint.report;


import gr.ntua.ivml.mint.actions.UrlApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DerivedTransformationDetailsBeanFactory {
	
	
	
	String organizationId = null;
	String datasetId = null;
	private Date startDate;
	private Date endDate;
	
	



	public DerivedTransformationDetailsBeanFactory(String organizationId, String datasetId,Date startDate,Date endDate) {
		super();
		
		this.organizationId = organizationId;
		this.datasetId = datasetId;
		if (startDate == null){
			this.startDate   = new Date(0);
		}
		else{
			this.startDate = startDate; 
		}
		if (endDate == null){
			this.endDate = new Date();
		}
		else{
			this.endDate = endDate;
		}
	}




	public List<TransformationDetailsBean> getTransformations(){
		UrlApi api = new UrlApi();
		api.setOrganizationId(organizationId);
		api.setDatasetId(datasetId);
		JSONObject json =  api.listDerivatives();
		return getTransformations(json);
	}
		
		public List<TransformationDetailsBean> getTransformations(JSONObject json){
		List<TransformationDetailsBean> transformations = new ArrayList<TransformationDetailsBean>();
		JSONArray result = (JSONArray) json.get("result");
		Iterator it = result.iterator();
		while (it.hasNext()){
			JSONObject jsonObject = (JSONObject) it.next();
			String name = jsonObject.get("name").toString();
			Date created = new Date(0);;
			Date lastModified = new Date(0);
			try {
				String dateCreated = (String) jsonObject.get("created");
//				created = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").parse(dateCreated); 
				created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(dateCreated);

				String dateModified = (String) jsonObject.get("lastModified");
//				lastModified = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").parse(dateModified);
				lastModified = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(dateModified);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!(created.compareTo(startDate)>0 && created.compareTo(endDate)<0 )||!(lastModified.compareTo(startDate)>0 &&lastModified.compareTo(endDate)<0 )){
				continue;
			}
			
			Integer  itemCount =  (Integer) jsonObject.get("itemCount");
			Integer  validItems =  (Integer) jsonObject.get("validItems");
			Integer  invalidItems =  (Integer) jsonObject.get("invalidItems");
			
			
			String itemizerStatus = jsonObject.get("itemizerStatus").toString();
		
				//	Integer.parseInt(jsonObject.get("itemCount").toString()) ;
			
			
			JSONObject creator = (JSONObject) jsonObject.get("creator");
			String creatorId = null;
			String creatorName = null;
			if (creator.has("dbID")) {
				creatorId = creator.getString("dbID").toString();
			}
			if (creator.has("name")) {
				creatorName = creator.getString("name").toString();
			}

			String organizationName = null;
			String organizationId = null;

			JSONObject org = (JSONObject) jsonObject.get("organization");
			if (org.has("dbID")) {
				organizationId = org.getString("dbID").toString();
			}
			if (org.has("name")) {
				organizationName = org.getString("name").toString();
			}
			
			
			String mappingUsed = null ;
			String targetSchema = null;
			String parentDataset = null;
			
			if (jsonObject.has("mappingUsed")) {
			mappingUsed = jsonObject.get("mappingUsed").toString();
			}
			if (jsonObject.has("targetSchema")) {
			targetSchema = jsonObject.get("targetSchema").toString();
			}
			if (jsonObject.has("parentDataset")) {
			parentDataset = jsonObject.get("parentDataset").toString();
			}
				
			
		TransformationDetailsBean transformationDetailsBean = new TransformationDetailsBean(name, created, lastModified, creatorId, creatorName, itemCount,validItems,invalidItems, itemizerStatus, organizationId, organizationName,mappingUsed,targetSchema,parentDataset);
		transformations.add(transformationDetailsBean);
		}
		
		
		
		

	return transformations;
	}
}
