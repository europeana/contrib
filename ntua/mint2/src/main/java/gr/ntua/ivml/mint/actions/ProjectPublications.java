


package gr.ntua.ivml.mint.actions;


import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.util.Config;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({ @Result(name = "error", location = "projectpublished.jsp"),//projectstatistics.jsp
		@Result(name = "success", location = "projectpublished.jsp") })//projectstatistics.jsp
public class ProjectPublications extends GeneralAction {

	protected final static Logger log = Logger.getLogger(Stats.class);

	long organizationId;
	 
	
	
	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public String getName() {
		
		return Config.get("mint.title");
		
		
	}

	
	@Action(value = "ProjectPublications")
	public String execute() throws Exception {
		return SUCCESS;
	}

}
