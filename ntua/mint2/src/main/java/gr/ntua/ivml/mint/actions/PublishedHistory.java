package gr.ntua.ivml.mint.actions;


import gr.ntua.ivml.mint.db.DB;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({ @Result(name = "error", location = "publishedhistory.jsp"),//organizationstatistics2.jsp
		@Result(name = "success", location = "publishedhistory.jsp") })//organizationstatistics2.jsp
public class PublishedHistory extends GeneralAction {

	protected final static Logger log = Logger.getLogger(Stats.class);

	long organizationId;
	 
	
	
	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public String getName() {
		if (DB.getOrganizationDAO().getById(organizationId,false)!=null){
		return DB.getOrganizationDAO().getById(organizationId, false).getEnglishName();
		}
		else return "";
	}

	
	@Action(value = "PublishedHistory")
	public String execute() throws Exception {
		return SUCCESS;
	}

}
