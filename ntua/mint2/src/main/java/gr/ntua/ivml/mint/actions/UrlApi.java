package gr.ntua.ivml.mint.actions;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Mapping;
import gr.ntua.ivml.mint.persistent.Organization;
import gr.ntua.ivml.mint.persistent.Transformation;
import gr.ntua.ivml.mint.persistent.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionContext;

@Results({
	@Result(name= "json", location="json.jsp" )
})

public class UrlApi extends GeneralAction  implements ServletRequestAware{
	private String action;
	private String type;
	
	private String organizationId;
	
	private String datasetId;
	
	private int maxResults = -1;
	private int start = 0;
	private JSONObject json=null;
	
	private HttpServletRequest request;
	
	@Action( value="UrlApi"	)
	public String execute() {
//		if( !isApi()) return NONE;
		if( action.equals( "list" )) {
			
				if( type.equals( "Dataset")) {
					listDatasets();
				} else if( type.equals( "Mapping")) {
					listMappings();
				} else if( type.equals( "User")) {
					listUsers();
				} else if ( type.equals( "Organization")) {
					listOrganizations();
				} else if( type.equals( "DataUpload")) {
					listDataUploads();
				} else if ( type.equals("Transformation")){
				listTransformations();
				} else if ( type.equals("Publication")){
					listPublications();
				} else if ( type.equals("Derivative")){
					listDerivatives();
				}
				
			
		} else {
			json = new JSONObject()
				.element( "error", "unknown action" );
		}
		
		json.element("user", user.toJSON());
		
		return "json";
		
	}
	
	public User getUser() {
		if(user == null) {
			Map<String, Object> session = ActionContext.getContext().getSession();
			user = (User) session.get("user");
		}
		
		return user;
	}


	public  JSONObject listTransformations() {
		List<Transformation> res = DB.getTransformationDAO().findAll();	
		JSONArray arr = new JSONArray();
		for( Transformation ts: res ) {
			if(this.organizationId == null || ts.getParentDataset().getOrganization().getDbID()==Long.parseLong(organizationId)) {
				arr.add( ts.toJSON());
			}
		}
		json = new JSONObject()
			.element( "result", arr );
		return json;
	}		
	
	public JSONObject listDatasets() {
	List<Dataset> res = DB.getDatasetDAO().pageAll(start, maxResults);
		
		JSONArray arr = new JSONArray();
		for( Dataset ds: res ) {
			if(this.organizationId == null || ds.getOrganization().getDbID()==Long.parseLong(organizationId)){				
				arr.add( ds.toJSON());
			}
		}
		json = new JSONObject()
			.element( "result", arr );
		
		return json;
	}
	
	public JSONObject listDerivatives() {		
		JSONArray arr = new JSONArray();
		if (DB.getDatasetDAO().getById(Long.parseLong(this.datasetId), false) != null){
			Dataset dataset = DB.getDatasetDAO().getById(Long.parseLong(this.datasetId), false);
			Collection<Dataset> der= dataset.getDerived();						
			for( Dataset ds: der ) {
				if(this.organizationId == null || ds.getOrganization().getDbID()==Long.parseLong(organizationId)){				
					arr.add( ds.toJSON());
				}
			}
			
		}
		json = new JSONObject()
			.element( "result", arr );
		return json;
	} 
	
	public JSONObject listMappings() {
		JSONArray arr = new JSONArray();
		List<Mapping> res = null;
		
		if(getUser().hasRight(User.SUPER_USER)) {
			res = DB.getMappingDAO().pageAll( start, maxResults );
		} else {
			Organization organization = getUser().getOrganization();
			res = DB.getMappingDAO().findByOrganization(organization);
		}
		
		if(res != null) {
			for( Mapping m: res ) {
				if (this.organizationId == null || m.getOrganization().getDbID()==Long.parseLong(organizationId)){
					
					arr.add( m.toJSON());
				}
			}

			json = new JSONObject()
			.element( "result", arr );
		}
		return json;

	}
	
	public JSONObject listUsers() {
		List<User> res = DB.getUserDAO().pageAll( start, maxResults );
		
		JSONArray arr = new JSONArray();
		for( User u: res ) {
			if (this.organizationId==null || u.getOrganization().getDbID()==Long.parseLong(organizationId)){				
				arr.add( u.toJSON());
			}
		}		
		json = new JSONObject()
			.element( "result", arr );
		
		return json;

	}

	public JSONObject listOrganizations() {
		List<Organization> res = DB.getOrganizationDAO().pageAll( start, maxResults );
		JSONArray arr = new JSONArray();
		for( Organization org: res ){
			if (this.organizationId == null || org.getDbID()==Long.parseLong(organizationId)){			
				arr.add( org.toJSON());
			}
			
		}
		
		json = new JSONObject()
			.element( "result", arr );
	
		return json;

	}
	
	public JSONObject listDataUploads() {
		List<DataUpload> res = DB.getDataUploadDAO().pageAll( start, maxResults );
		JSONArray arr = new JSONArray();
		for( DataUpload du: res ){
			if ( this.organizationId==null || du.getOrganization().getDbID()==Long.parseLong(organizationId)){				
				arr.add( du.toJSON());
			}
		}
		json = new JSONObject()
			.element( "result", arr );
		
		return json;
		
	}
	
	public JSONObject listPublications() {
		List<DataUpload> res = DB.getDataUploadDAO().pageAll( start, maxResults );
		JSONArray arr = new JSONArray();
		for( DataUpload du: res ){
			if (du.getPublicationStatus().equals("OK")){
				if ( this.organizationId==null || du.getOrganization().getDbID()==Long.parseLong(organizationId)){				
					arr.add( du.toJSON());
				}
			}
		}
		json = new JSONObject()
			.element( "result", arr );
		
		return json;
		
	}
	
	public void setAction( String action) {
		this.action = action;
	}
	
	public void setType( String type ) {
		this.type = type;
	}
	
	public void setStart( int start ) {
		this.start = start;
	}
	
	public void setMaxResults( int max ) {
		this.maxResults = max;
	}
	
	public JSONObject getJson() {
		return json;
	}
	
	public String getDatasetId() {
		return datasetId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public void setOrganizationId(String orgId) {
		this.organizationId = orgId;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public HttpServletRequest getServletRequest() {
		return this.request;
	}
}
