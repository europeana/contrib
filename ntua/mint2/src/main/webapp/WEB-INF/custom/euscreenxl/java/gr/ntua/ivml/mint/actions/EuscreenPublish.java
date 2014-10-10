package gr.ntua.ivml.mint.actions;

import gr.ntua.ivml.mint.BasePublication;
import gr.ntua.ivml.mint.concurrent.Queues;
import gr.ntua.ivml.mint.concurrent.Solarizer;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.persistent.PublicationRecord;
import gr.ntua.ivml.mint.util.ApplyI;
import gr.ntua.ivml.mint.util.Config;
import gr.ntua.ivml.mint.util.PublishQueue;
import gr.ntua.ivml.mint.util.StringUtils;

import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.opensymphony.xwork2.util.TextParseUtil;

/**
 * All activities around publishing in Euscreenxl should happen here.
 * publish on portal, unpublish portal,
 * publish / unpublish europeana
 * item production status change 
 * 
 * @author Arne Stabenau 
 *
 */
@Results({	  
	  @Result(name="input", location="/WEB-INF/custom/euscreenxl/jsp/publishResponse.jsp"),
	  @Result(name="error", location="/WEB-INF/custom/euscreenxl/jsp/publishResponse.jsp"),
	  @Result(name="success", location="/WEB-INF/custom/euscreenxl/jsp/publishResponse.jsp")

})



public class EuscreenPublish extends GeneralAction {
	public static final String EUSCREEN_DEFAULT_ID = "EUS_00000000000000000000000000000000";
	
	public static class PortalPublish implements Runnable {
		private Dataset ds;
		private boolean publish;
		public PublicationRecord pr;
		
		private int problemCounter = 3;
		
		public PortalPublish( Dataset ds, boolean publish, PublicationRecord pr ) {
			this.ds = ds;
			this.publish =  publish;
			this.pr = pr;
		}

		public void run() {
			try {
				DB.getSession().beginTransaction();
				DB.getStatelessSession().beginTransaction();

				ds = DB.getDatasetDAO().getById(ds.getDbID(), false);
				pr = DB.getPublicationRecordDAO().getById(pr.getDbID(), false);

				if( publish ) {
					// iterate over items, 
					// if they don't have eus id, make one
					// if they do, compare it and see if its still ok
					// make a note in log if there has been a change
					ds.logEvent((publish?"Publish":"Unpublish") + " started" );
					DB.commit();
					ApplyI<Item> modifyId = new ApplyI<Item>() {
						@Override
						public void apply(Item item) throws Exception {
							String id = item.getValue("//*[local-name()='AdministrativeMetadata']/*[local-name()='identifier']");
							if( id != null ) {
								// create the id.
								String provider = item.getValue("//*[local-name()='AdministrativeMetadata']/*[local-name()='provider']");
								String originalId = item.getValue("//*[local-name()='AdministrativeMetadata']/*[local-name()='originalIdentifier']");
								if( StringUtils.empty(originalId) || StringUtils.empty( provider)) {
									// bad, will be bad EUS id, need to ignore this and warn here
									// set item to invalid and not publish
									item.setValid(false);
									if( problemCounter > 0 ) {
										problemCounter--;
										ds.logEvent("Provider or originalId not set!", "In Item["+item.getDbID()+"] " + item.getLabel());
									}
									
								} else {
									String eusId = "EUS_"+StringUtils.md5Utf8(StringUtils.join( provider,":",originalId));
									if( !id.equals(eusId )) {
										// for the time being only adjust if the value is initial
										if( id.equals( EUSCREEN_DEFAULT_ID)) {
											// modify the xml
											item.setValue("//*[local-name()='AdministrativeMetadata']/*[local-name()='identifier']", eusId );
											item.setPersistentId(eusId );
										} else {
											log.info( "Item [" + item.getDbID() + "] tried change id from " + id + " to " + eusId + ". NOT DONE!"  );
										}
									}
								}
							} else {
								// warn the id field is empty
								if( problemCounter > 0 ) {
									problemCounter--;
									ds.logEvent("ID field not preset during publish!", "In Item["+item.getDbID()+"] " + item.getLabel());
								}
							}
						}
					};

					ds.processAllValidItems( modifyId, true );
				}
				
				Solarizer si = new Solarizer( ds );
				si.modifier = new ApplyI<SolrInputDocument>() {
					public void apply(SolrInputDocument sid) throws Exception {
						if( publish ) {
							sid.addField("published", "yes");
						} else {
							sid.removeField("published");
						}
					}
				};
				si.runInThread();
				
				// and now for the queueing 
				ApplyI<Item> publishOnQueue = new ApplyI<Item>() {
					public void apply(Item item) throws Exception {
						PublishQueue.queueItem(item, !publish);
					}
				};
		
				ds.processAllValidItems( publishOnQueue, false );
				
				ds.logEvent("Finished " + (publish?"publishing to":"unpublishing from") + " portal", ds.getValidItemCount()+" items queued for process at portal.");
				if( publish ) {
					pr.setEndDate(new Date());
					pr.setPublishedItemCount(ds.getValidItemCount());
					pr.setStatus(Dataset.PUBLICATION_OK);
					DB.commit();
				} else {
					DB.getPublicationRecordDAO().makeTransient(pr);
					DB.commit();
				}
			} catch( Exception e ) {
				ds.logEvent("Error while publishing/unpublishing", e.getMessage());
				if( pr != null ) {
					pr.setEndDate(new Date());
					pr.setStatus(Dataset.PUBLICATION_FAILED);
				}
				DB.commit();
			} finally {
				DB.closeSession();
				DB.closeStatelessSession();
			}
		}
	}
	
	// which id should be scheduled for publish
	private static final Logger log = Logger.getLogger( EuscreenPublish.class );
	
	String datasetId;
	String cmd;
	
	String title;
	String message;
	
	private Dataset ds;
	
	@Action( value="EuscreenPublish" )
	public String execute() {
		log.debug( "cmd="+cmd+" datasetId="+datasetId);
		try {
			ds = DB.getDatasetDAO().getById(Long.parseLong(datasetId), false);
			// TODO: rights check
		} catch( Exception e ) {
			log.error( e );
		}
		if( "europeanaPublish".equals( cmd ))
			return europeanaPublish();
		else if( "europeanaUnpublish".equals( cmd )) 
			return europeanaUnpublish();
		else if( "portalPublish".equals( cmd ))
			return portalPublish();
		else if( "portalUnpublish".equals( cmd )) 
			return portalUnpublish();
		else {
			log.error( "cmd="+cmd+" not supported.");
			title = "Error";
			message = "Not implemented yet";
			
		}
		return SUCCESS;
	}

	private String portalUnpublish() {
		// is it successfully published on portal?
		// send of runnable that reindexes all published items from dataset
		// field published removed
		Dataset origin = ds.getOrigin();
		Set<String> schemas= TextParseUtil.commaDelimitedStringToSet(Config.get("euscreen.portal.schema"));
		Dataset published = null;
		for( String schemaName: schemas ) {
			published = ds.getBySchemaName(schemaName);
			if( published != null ) break;
		}
		
		if(( origin == null) || ( published == null )) {
			title = "Error";
			message = "Can't find Dataset to publish! Program Bug!";
			return ERROR;
		}

		PublicationRecord pr = published.getPublicationRecord();
		
		if( pr == null ) {
			title = "Error";
			message = "Is not published on portal. Possible program bug!";
			return ERROR;
		}
		if(( pr != null ) && Dataset.PUBLICATION_RUNNING.equals( pr.getStatus())) {
			title = "Busy";
			message = "There is a publishing/unpublishing still running!";
			return SUCCESS;		
		}

		pr.setStatus(Dataset.PUBLICATION_RUNNING);
		DB.commit();
		PortalPublish pp = new PortalPublish( published, false, pr );
		Queues.queue(pp, "db" );
		message= "The removal from the portal was queued!";
		title="Portal Removal";
		return SUCCESS;
	}

	private String portalPublish() {
		Dataset origin = ds.getOrigin();
		
		Set<String> schemas= TextParseUtil.commaDelimitedStringToSet(Config.get("euscreen.portal.schema"));
		Dataset published = null;
		for( String schemaName: schemas ) {
			published = ds.getBySchemaName(schemaName);
			if( published != null ) break;
		}
		
		if(( origin == null) || ( published == null )) {
			title = "Error";
			message = "Can\\'t find Dataset to publish! Program Bug!";
			return ERROR;
		}
		PublicationRecord pr = published.getPublicationRecord();
		
		// is it not allready publishing?
		if(( pr != null ) && Dataset.PUBLICATION_RUNNING.equals( pr.getStatus())) {
			title = "Error";
			message = "There is a publishing/unpublishing still running!";
			return ERROR;
		}
		if(( pr != null ) && Dataset.PUBLICATION_OK.equals( pr.getStatus())) { 
			title = "Error";
			message = "Shouldn't get here, is already published!";
			return ERROR;
		}
		if( pr != null ) {
			DB.getPublicationRecordDAO().makeTransient(pr);
		}
		
		// check correct schema only on UI is not enough
		// get origin and actual published dataset
		if(( origin == null) || ( published == null )) {
			title = "Error";
			message = "Can't find Dataset to publish! Program Bug!";
			return ERROR;
		}
				
		// runnable that reindexes all valid items from dataset with field published=yes set
		pr = new PublicationRecord();

		pr.setStartDate(new Date());
		pr.setOrganization(ds.getOrganization());
		pr.setPublisher(getUser());
		pr.setStatus(Dataset.PUBLICATION_RUNNING);
		pr.setPublishedDataset(published);
		pr.setOriginalDataset(origin);
		DB.getPublicationRecordDAO().makePersistent(pr);
		DB.commit();
		PortalPublish pp = new PortalPublish( published, true, pr );
		Queues.queue(pp, "db" );
		message= "Your publication to the portal successfully queued!";
		title="Portal Publication";
		return SUCCESS;
	}

	private String europeanaUnpublish() {
		Dataset origin = ds.getOrigin();
		Dataset published = ds.getBySchemaName(Config.get("euscreen.aggregate.schema"));

		PublicationRecord pr = published.getPublicationRecord();
		
		// is it not allready publishing?
		if(( pr != null ) && Dataset.PUBLICATION_RUNNING.equals( pr.getStatus())) {
			title="Wait please.";
			message = "There is already a publication running!";
			return "success";
		}
		
		if( pr != null ) {
			DB.getPublicationRecordDAO().makeTransient(pr);
		} else {
			title = "Error";
			message = "Dataset is not published on Europeana!";
			return ERROR;
		}
		
		BasePublication bp = new BasePublication(ds.getOrganization());
		bp.unpublish(ds);
		// TODO Auto-generated method stub
		title = "Success";
		message = "Your dataset was unpublished.";
		return SUCCESS;
	}

	private String europeanaPublish() {
		Dataset origin = ds.getOrigin();
		Dataset published = ds.getBySchemaName(Config.get("euscreen.aggregate.schema"));

		PublicationRecord pr = published.getPublicationRecord();
		
		// is it not allready publishing?
		if(( pr != null ) && Dataset.PUBLICATION_RUNNING.equals( pr.getStatus())) {
			title="Wait please.";
			message = "There is already a publication running!";
			return "success";
			
		}
		if(( pr != null ) && Dataset.PUBLICATION_OK.equals( pr.getStatus())) {
			title = "Already published!";
			message = "Please unpublish before you republish.";
			return SUCCESS;
		}
		if( pr != null ) {
			DB.getPublicationRecordDAO().makeTransient(pr);
		}
		
		// check correct schema only on UI is not enough
		if(( published.getSchema() == null) || !published.getSchema().getName().equals( Config.get( "euscreen.aggregate.schema" ))
				|| published.getValidItemCount()<=0 ) {
			// cant publish this
			title = "Error";
			message = "Shouldn't have gotten here, UI should filter this";
			log.warn( "Europeana publish called without precondition being true.");
			return ERROR;
		}
		
		
		
		pr = new PublicationRecord();

		pr.setStartDate(new Date());
		pr.setOrganization(ds.getOrganization());
		pr.setPublisher(getUser());
		pr.setStatus(Dataset.PUBLICATION_RUNNING);
		pr.setPublishedDataset(published);
		pr.setOriginalDataset(origin);
		DB.getPublicationRecordDAO().makePersistent(pr);
		DB.commit();
		
		// Here we use the BasePublication publish strategy
		BasePublication bp = new BasePublication(ds.getOrganization());
		bp.publish(origin, getUser());
		
		message= "Your publication to Europeana successfully queued!";
		title="Europeana Publication";
		return SUCCESS;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}

