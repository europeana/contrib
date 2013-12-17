package gr.ntua.ivml.mint.actions;

import gr.ntua.ivml.mint.db.Meta;
import gr.ntua.ivml.mint.util.PublishQueue;
import gr.ntua.ivml.mint.util.StringUtils;
import gr.ntua.ivml.mint.util.Tuple;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

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
			String res = Meta.get("ItemOnPortal["+id+"]");
			if( "true".equals( res )) {
				PublishQueue.queueUpdate(id);
				return SUCCESS;
			} else {
				log.debug( "id["+id+"] is not published!");
				return ERROR;
			}
		}
	}

	private StringBuilder allIds() {
		// find from meta table everything that is published
		// ItemOnPortal[...] = true
		
		try {
			int counter = 0;
			Pattern p = Pattern.compile("EUS_[0-9a-fA-F]{32}");
			List<Tuple<String,String>> eusIds = Meta.getLike("ItemOnPortal[%" );
			for( Tuple<String,String> metaEntry:eusIds ) {
				if( metaEntry.second().equals("true")) {
					// extract the euscreenid
					String key = metaEntry.first();
					Matcher m = p.matcher(key);
					if( m.find()) {
						String eusId = key.substring(m.start(),m.end());
						output.append( eusId + "\n");
						counter++;
					} else {
						log.warn( "Invalid entry in meta '" + key +"'");
					}
				}
			}
			log.info( "Reported " + counter + " ids as published.");
		} catch( Exception e ) {
			log.debug( "Do nothing?", e  );
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
