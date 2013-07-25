package controllers;

import gr.ntua.ivml.awareness.search.SearchServiceAccess;

import org.codehaus.jackson.JsonNode;

import org.codehaus.jackson.map.ObjectMapper;


import com.mongodb.BasicDBObject;


import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;



public class EuropeanaSearch extends Controller {
	 static ObjectMapper mapper = new ObjectMapper( );
	
	
	public static Result search(){
	     try{
	    	/*String user = session("connected");
	 		if(user == null) {
	 			  return unauthorized("Oops, you are not connected");
	 		  }else{System.out.println("authorized "+user );}
	 		*/
	    	 BasicDBObject res=new BasicDBObject();
	    	 JsonNode json = request().body().asJson();
	    	 response().setContentType("application/json");
			 if(json==null){
				    return badRequest("Expecting Json data");
				}
				else {
				   String term= json.findPath("term").getTextValue();
				   String type=null;
				   if(json.findPath("type")!=null ){
					   type=json.findPath("type").getTextValue();
					  
				   }
				  int page = -1;
					if(json.findPath("pageNumber").isMissingNode()){
						page = -1;
					}else{
						page = json.findPath("pageNumber").getIntValue();
					}
					if(term == null || page < 1){
						return notFound();
					}else if(term != null && page >=1){
						SearchServiceAccess acc = new SearchServiceAccess();
						res = acc.searchEuropeana(term,type, page);
							
						
					}
					 
					return ok(com.mongodb.util.JSON.serialize(res));
					
				}
	     }
	     catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  response().setContentType("application/json");
		  	  return internalServerError(Messages.get("apperror"));
		    }
	}
	
	public static Result record(){
	     try{
	    	/*String user = session("connected");
	 		if(user == null) {
	 			  return unauthorized("Oops, you are not connected");
	 		  }else{System.out.println("authorized "+user );}
	 		*/
	    	 BasicDBObject res=new BasicDBObject();
	    	 JsonNode json = request().body().asJson();
	    	 response().setContentType("application/json");
			 	if(json==null){
				    return badRequest("Expecting Json data");
				}
				else {
				   String recid= json.findPath("recid").getTextValue();
				  if(recid == null){
						return notFound();
					}else if(recid != null){
						SearchServiceAccess acc = new SearchServiceAccess();
						res = acc.searchEuropeanaRecord(recid);
							
						
					}
					 
					return ok(com.mongodb.util.JSON.serialize(res));
					
				}
	     }
	     catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  response().setContentType("application/json");
		  	  return internalServerError(Messages.get("apperror"));
		    }
	}
}