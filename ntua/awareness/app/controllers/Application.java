package controllers;


import java.io.IOException;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;


import gr.ntua.ivml.awareness.persistent.User;
import gr.ntua.ivml.awareness.play.CorsHeaders;
import gr.ntua.ivml.awareness.util.MongoDB;
import play.Play;
import play.mvc.*;


import views.html.*;


public class Application extends Controller {
	
 
 public static final String DOMAINS = Play.application().configuration().getString("domains");

		
  
  public static Result index() {
    return ok(index.render("Your new application is ready."));
  }
  
  public static Result options(String url) {
	    if(request().getHeader("Origin")!=null && (DOMAINS!=null) && (DOMAINS.indexOf(request().getHeader("Origin"))>-1 || DOMAINS.indexOf("*")==0)){
	    	response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request().getHeader("Origin"));
	    }
	 
		response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS");
		response().setHeader(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version");
		
		response().setHeader(CorsHeaders.ACCESS_CONTROL_MAX_AGE,"86400");
	    return ok("");
		    
	  }
  
  
  public static Result sessionExists() {
	  String uid=session().get("uid");
	  if(uid!=null){
	        User u=MongoDB.getUserDAO().get(new ObjectId(uid));
	        
	        try {
				return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(u)));
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	  return notFound();
	  }
}