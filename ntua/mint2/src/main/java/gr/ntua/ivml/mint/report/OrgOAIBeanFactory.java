package gr.ntua.ivml.mint.report;

import gr.ntua.ivml.mint.actions.UrlApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrgOAIBeanFactory {
	
	String organizationId ;
	String projectName;
	OaijsonFetcher oaijsonFetcher ;
	
	protected final Logger log = Logger.getLogger(getClass());
	
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public OaijsonFetcher getOaijsonFetcher() {
		return oaijsonFetcher;
	}
	public void setOaijsonFetcher(OaijsonFetcher oaijsonFetcher) {
		this.oaijsonFetcher = oaijsonFetcher;
	}
	
	public OrgOAIBeanFactory(String organizationId) {
		super();
		this.organizationId = organizationId;
		this.oaijsonFetcher = new OaijsonFetcher(this.organizationId);	
				
	}
	
	public OrgOAIBeanFactory(String organizationId,String projectName) {
		super();
		this.organizationId = organizationId;
		this.projectName = projectName;
		this.oaijsonFetcher = new OaijsonFetcher(this.projectName,this.organizationId);	
		
	}
	
	public ProjectOAIBean getProjectOAIBean(){
		List<String> namespaces = new ArrayList<String>(); 
		JSONObject object = oaijsonFetcher.getProjectJson();
		if (object==null){
			Integer unique = 0;
			Integer duplicates = 0;
			Integer publications = 0;
			ProjectOAIBean projectBean = new ProjectOAIBean(projectName, unique, duplicates,publications,namespaces);
			return projectBean;
			//return null;
		}
		JSONArray array = object.getJSONArray("namespaces");
		
		Iterator it = array.iterator();
		while (it.hasNext()){
			JSONObject item = (JSONObject) it.next();
			namespaces.add(item.getString("prefix"));		
		}

		Integer unique = Integer.parseInt(object.getString("unique"));
		Integer duplicates = Integer.parseInt(object.getString("duplicates"));
		Integer publications = Integer.parseInt(object.getString("publications"));
		ProjectOAIBean projectBean = new ProjectOAIBean(projectName, unique, duplicates,publications,namespaces);
		return projectBean;
	}
	
	
	
	public List<OrgOAIBean> getOrgOAIBeans(){
		List<OrgOAIBean> orgsoaibeans = new ArrayList<OrgOAIBean>();
		if (this.getProjectOAIBean() == null){
			return orgsoaibeans; 
		}
		else {ProjectOAIBean projectbean = this.getProjectOAIBean();
		
		List<String> listofnamespaces = projectbean.getTypes();
		Iterator<String> it = listofnamespaces.iterator();
		
		while (it.hasNext()){
			String namespace = (String) it.next();
			JSONObject object = oaijsonFetcher.getOrgJson(namespace);
			System.out.println(object.toString());
			log.error(object.toString());
			if (object.containsKey(this.organizationId)){
				Integer unique = object.getInt(this.organizationId);
				UrlApi api = new UrlApi();
				api.setOrganizationId(this.organizationId);		
				JSONObject json = api.listOrganizations();
				JSONArray result = (JSONArray) json.get("result");
				Iterator ait = result.iterator();
				String organizationName = null;
				while (ait.hasNext()){
					
					JSONObject jsonObject = (JSONObject) ait.next();
					organizationName =(String) jsonObject.get("englishName");
				}
				OrgOAIBean orgoaiBean = new OrgOAIBean(organizationName, namespace, this.organizationId, unique);
				orgsoaibeans.add(orgoaiBean);
			}
			else{
				Integer unique = 0;
				UrlApi api = new UrlApi();
				api.setOrganizationId(this.organizationId);		
				JSONObject json = api.listOrganizations();
				JSONArray result = (JSONArray) json.get("result");
				Iterator ait = result.iterator();
				String organizationName = null;
				while (ait.hasNext()){
					
					JSONObject jsonObject = (JSONObject) ait.next();
					organizationName =(String) jsonObject.get("englishName");
				}
				OrgOAIBean orgoaiBean = new OrgOAIBean(organizationName, namespace, this.organizationId, unique);
				orgsoaibeans.add(orgoaiBean);
				
			}
		}
		return orgsoaibeans;
		
	}
	}
	public Integer getItemCount(){
		Integer count=0;
		List<OrgOAIBean> orgoaibeans = this.getOrgOAIBeans();
		Iterator it = orgoaibeans.iterator();
		while (it.hasNext()){
			OrgOAIBean bean = (OrgOAIBean) it.next();
			if (bean.type.equals("rdf")){
				count+=bean.getUniqueItems();
			}
		}
		return count;
	}
	
	
	/*public List<OrgOAIBean> getOrgOAIBeans(String orgid){
		List<OrgOAIBean> orgsoaibeans = getOrgOAIBeans();
		List<OrgOAIBean> orgoaibeans = new ArrayList<OrgOAIBean>();
		Iterator<OrgOAIBean>  it = orgsoaibeans.iterator();
		while (it.hasNext()){
			OrgOAIBean bean = it.next();
			if (bean.organizationId.equals(orgid)){
				orgoaibeans.add(bean);
			}
		}
		
		return orgoaibeans;
		
	}
	*/

}
