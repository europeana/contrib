package gr.ntua.ivml.mint.actions;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.db.Meta;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.persistent.PublicationRecord;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.ApplyI;
import gr.ntua.ivml.mint.util.Config;
import gr.ntua.ivml.mint.util.PublishQueue;
import gr.ntua.ivml.mint.util.StringUtils;
import gr.ntua.ivml.mint.util.Tuple;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.opensymphony.xwork2.util.TextParseUtil;

/**
 * Use this as trigger for Noterick to start a publication on their server.
 * Trigger for our own backend to start publication on noterick server.
 * 
 * Trigger to delete from Noterick server.
 * 
 * @author Arne Stabenau 
 *
 */
@Results({
	@Result(name="list", type="stream", params={"inputName", "inputStream", "contentType", "text/plain",
			"contentCharSet", "UTF-8" }),
	@Result(name= "success", type="httpheader", params={"status","204" }),
	@Result(name="error", type="httpheader", params={"error", "404", 
			"errorMessage", "Internal Error"}) 
})



public class PortalService extends GeneralAction {
	// which id should be scheduled for publish
	private static final Logger log = Logger.getLogger( PortalService.class );
	String id;
	private StringBuilder output = new StringBuilder();
	final static Pattern p = Pattern.compile("EUS_[0-9a-fA-F]{32}");
	
	@Action( value="PortalService", interceptorRefs={
			@InterceptorRef(value="ipFilter", params={"configIpPatterns", "portal.ipPattern"}),
		    @InterceptorRef("myStack")
	  })	
	public String execute() {
		if( StringUtils.empty( id )) {
			allIds();
			return "list";
		} else {
			// lets check if this is published
			List<Object[]> isPublishedList = DB.getItemDAO().getIdPublished(id);
			if( !isPublishedList.isEmpty()) {
				PublishQueue.queueUpdate(id);
				return SUCCESS;
			} else {
				log.debug( "id["+id+"] is not published!");
				return ERROR;
			}
		}
	}

	private StringBuilder allIds() {
		// find everything that is valid and in a portal published dataset
		
		try {
			output.setLength(0);
			List<PublicationRecord> lpr = DB.getPublicationRecordDAO().findAll();
			// iterate and find pubished datasets with right schema
			Set<String> schemaNames = TextParseUtil.commaDelimitedStringToSet( Config.get( "euscreen.portal.schema"));
			for( PublicationRecord pr: lpr ) {
				if( !PublicationRecord.PUBLICATION_OK.equals( pr.getStatus())) continue;
				Dataset ds = pr.getPublishedDataset();
				if( ds != null ) {
					XmlSchema xs = ds.getSchema();
					if( xs != null ) {
						if( schemaNames.contains( xs.getName())) {

							ApplyI<Item> op = new ApplyI<Item>() {
								public void apply(Item obj) throws Exception {
									// TODO Auto-generated method stub
									String id = obj.getPersistentId();
									
									// check if thats a valid EUS_ id
									if( id == null ) return;
 									Matcher m = p.matcher( id );
									if( m.matches())
										output.append( id + "\n");
									
								}
							};
						
							ds.processAllValidItems(op, false);
						}
					}
				}
			}
		} catch( Exception e ) {
			log.error( "Request for published item id list failed!", e  );
		}
		return output;
	}
	
	
	//
	// Getter setter below
	//
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public InputStream getInputStream() {
		try {
			return new ByteArrayInputStream(output.toString().getBytes("UTF8"));
		} catch( Exception e ) {
			// encoding is there, stupid exception!
			return null;
		}
	}
}
