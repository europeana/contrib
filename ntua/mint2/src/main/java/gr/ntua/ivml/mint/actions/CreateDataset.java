
package gr.ntua.ivml.mint.actions;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Organization;
import gr.ntua.ivml.mint.persistent.User;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	  @Result(name="input", location="createDataset.jsp"),
	  @Result(name="error", location="createDataset.jsp"),
	  @Result(name="success", location="successimport.jsp" )
	})

public class CreateDataset extends GeneralAction  {

	protected final Logger log = Logger.getLogger(getClass());
	public String nameDataset;
	public String schemaId;
	public String orgId;
	
	@Action("CreateDataset")
    public String execute() throws Exception {
    	log.info( "CreateDataset action");
 
    	// check permits
    	// created DataUpload empty object
    	try {
    		
    		Dataset ds = new Dataset();
    		Organization creatorOrg = null;
    		XmlSchema schema = null;

    		if( !StringUtils.empty( orgId )) {
    			long orgIdL = Long.parseLong(orgId);
    			creatorOrg = DB.getOrganizationDAO().getById(orgIdL, false);
    			// should check if this is allowed for user
    			boolean accessAllowed = false;
    			for( Organization o: user.getAccessibleOrganizations()) {
    				if( orgIdL == o.getDbID()) accessAllowed = true;
    			}
    			if( !accessAllowed ) {
    				throw new IllegalAccessException("Parameter manipulation");    				
    			}
    		} else {
    			// need to tell the workspace which organization to show
    			setOrgId( Long.toString( user.getOrganization().getDbID()));
    		}
    	
    	   	if((user.getOrganization()==null || user.getAccessibleOrganizations().size()>1 ) && creatorOrg == null ) {
        		addActionError("Choose the organization you are creating for!");
    			return ERROR;
        	}

    		if( StringUtils.empty( schemaId ) || schemaId.equals( "0")) {
    			addActionError( "You have to select a schema!");
    			return ERROR;
    		} else {
    			long schemaIdL = Long.parseLong(schemaId);
    			schema = DB.getXmlSchemaDAO().getById(schemaIdL, false);
    			if( schema == null ) {
    				addActionError( "Invalid schema .. should nnot happen, report bug!");
    			}
    		}
    		
    		ds.init( user );
    		if( creatorOrg != null ) {
    			ds.setOrganization( creatorOrg );
    		}
    		if( !user.can( "change data", ds.getOrganization() )) { 
    			throw new IllegalAccessException("Parameter manipulation");
    		}

    		ds.setItemizerStatus(Dataset.ITEMS_OK);
    		ds.setSchema(schema);
    		ds.setName(nameDataset);
    		ds.setSchemaStatus(Dataset.SCHEMA_OK);
    		ds.setStatisticStatus(Dataset.STATS_NOT_APPLICABLE);
    		ds.setItemCount(0);
    		ds.setValidItemCount(0);
    		DB.getDatasetDAO().makePersistent(ds);
    		DB.commit();
    		return SUCCESS;
    	} catch( Exception e ) {
    		addActionError( "There was a problem!\n" + e.getMessage());
    	}
    	return ERROR;
    }

	
	
	
	@Action("CreateDataset_input")
	@Override
	public String input() throws Exception {
    	if( user.getOrganization() == null && !user.hasRight(User.SUPER_USER)) {
    		throw new IllegalAccessException( "No import rights!" );
    	}

		return super.input();
	}

	
	//
	// Getters and setters
	//
	
	public String getNameDataset() {
		return nameDataset;
	}

	public void setNameDataset(String nameDataset) {
		this.nameDataset = nameDataset;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}	
}