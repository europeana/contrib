package controllers;




import gr.ntua.ivml.awareness.persistent.User;

import gr.ntua.ivml.awareness.security.SecurityRole;
import gr.ntua.ivml.awareness.util.MongoDB;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import org.codehaus.jackson.map.ObjectMapper;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import be.objectify.deadbolt.actions.Dynamic;

import play.i18n.Messages;
import play.libs.Json;
import play.mvc.*;




public class Users extends Controller {
	 static ObjectMapper mapper = new ObjectMapper( );
	
	 public static Result getUserObjects(String id){
		 response().setContentType("application/json");
		 try{
			String res = "{}";
			if(request().queryString().get("to") == null && request().queryString().get("from") == null){
				ArrayList<DBObject> ids = MongoDB.getStoryObjectDAO().getStoryObjects(0, 100, id);
				long total = MongoDB.getStoryObjectDAO().getDigitalObjectsByUserTotalNumber(id);
				BasicDBObject obj = new BasicDBObject();
				obj.put("totalSize", total);
				obj.put("start", 0);
				obj.put("to", ids.size());
				obj.put("values", ids);
				res = com.mongodb.util.JSON.serialize(obj);
			}else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
					int to = Integer.parseInt(request().queryString().get("to")[0]);
					ArrayList<DBObject> ids = MongoDB.getStoryObjectDAO().getStoryObjects(0, to, id);
					long total = MongoDB.getStoryObjectDAO().getDigitalObjectsByUserTotalNumber(id);
					BasicDBObject obj = new BasicDBObject();
					obj.put("totalSize", total);
					obj.put("start", 0);
					obj.put("to", to);
					obj.put("values", ids);
					res = com.mongodb.util.JSON.serialize(obj);
				
			}else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
					int to = Integer.parseInt(request().queryString().get("to")[0]);
					int from = Integer.parseInt(request().queryString().get("from")[0]);
					ArrayList<DBObject> ids = MongoDB.getStoryObjectDAO().getStoryObjects(from, to, id);
					long total = MongoDB.getStoryObjectDAO().getDigitalObjectsByUserTotalNumber(id);
					BasicDBObject obj = new BasicDBObject();
					obj.put("totalSize", total);
					obj.put("start", from);
					obj.put("to", to);
					obj.put("values", ids);
					res = com.mongodb.util.JSON.serialize(obj);
				
			}
			else {
				return badRequest();
			}
				
			
			
			return ok(res);
		
		}
	    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }
	 }
	 
	 public static Result getUserComments(String id){
			response().setContentType("application/json");
			try{
				String res = "{}";
				if(request().queryString().get("to") == null && request().queryString().get("from") == null){
					ArrayList<DBObject> ids = MongoDB.getScDAO().getStoryCommentsByUser(0, 100, id);
					long total = MongoDB.getScDAO().getUsersCommentsTotalNumber(id);
					BasicDBObject obj = new BasicDBObject();
					obj.put("totalSize", total);
					obj.put("start", 0);
					obj.put("to", ids.size());
					obj.put("values", ids);
					res = com.mongodb.util.JSON.serialize(obj);
				}else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
						int to = Integer.parseInt(request().queryString().get("to")[0]);
						ArrayList<DBObject> ids = MongoDB.getScDAO().getStoryCommentsByUser(0, to, id);
						long total = MongoDB.getScDAO().getUsersCommentsTotalNumber(id);
						BasicDBObject obj = new BasicDBObject();
						obj.put("totalSize", total);
						obj.put("start", 0);
						obj.put("to", ids.size());
						obj.put("values", ids);
						res = com.mongodb.util.JSON.serialize(obj);
					
				}else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
						int to = Integer.parseInt(request().queryString().get("to")[0]);
						int from = Integer.parseInt(request().queryString().get("from")[0]);
						ArrayList<DBObject> ids = MongoDB.getScDAO().getStoryCommentsByUser(from, to, id);
						long total = MongoDB.getScDAO().getUsersCommentsTotalNumber(id);
						BasicDBObject obj = new BasicDBObject();
						obj.put("totalSize", total);
						obj.put("start", from);
						obj.put("to", to);
						obj.put("values", ids);
						res = com.mongodb.util.JSON.serialize(obj);
					
				}
				else {
					return badRequest();
				}
					
				return ok(res);
			
			 }
		     catch (Exception ex){
				  	  System.out.println(ex.getMessage());
				  	  response().setContentType("application/json");
				  	  return internalServerError(Messages.get("apperror"));
				    }
	 }
	 	 
	 public static Result getTotalUserStories(String id){
			response().setContentType("application/json");
			try{
				String res = "{}";
					long total = MongoDB.getDigitalStoryDAO().getDigitalStoriesByUserTotalNumber(id);
					BasicDBObject obj = new BasicDBObject();
					obj.put("totalSize", total);
					res = com.mongodb.util.JSON.serialize(obj);
				
					
				return ok(res);
			
			 }
		     catch (Exception ex){
				  	  System.out.println(ex.getMessage());
				  	  response().setContentType("application/json");
				  	  return internalServerError(Messages.get("apperror"));
				    }
	 }
	 
	 public static Result getUserStories(String id){
			response().setContentType("application/json");
			try{
				String res = "{}";
				if(request().queryString().get("to") == null && request().queryString().get("from") == null){
					ArrayList<DBObject> ids = MongoDB.getDigitalStoryDAO().getDigitalStories(0, 100, id);
					long total = MongoDB.getDigitalStoryDAO().getDigitalStoriesByUserTotalNumber(id);
					BasicDBObject obj = new BasicDBObject();
					obj.put("totalSize", total);
					obj.put("start", 0);
					obj.put("to", ids.size());
					obj.put("values", ids);
					res = com.mongodb.util.JSON.serialize(obj);
				}else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
						int to = Integer.parseInt(request().queryString().get("to")[0]);
						ArrayList<DBObject> ids = MongoDB.getDigitalStoryDAO().getDigitalStories(0, to, id);
						long total = MongoDB.getDigitalStoryDAO().getDigitalStoriesByUserTotalNumber(id);
						BasicDBObject obj = new BasicDBObject();
						obj.put("totalSize", total);
						obj.put("start", 0);
						obj.put("to", ids.size());
						obj.put("values", ids);
						res = com.mongodb.util.JSON.serialize(obj);
					
				}else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
						int to = Integer.parseInt(request().queryString().get("to")[0]);
						int from = Integer.parseInt(request().queryString().get("from")[0]);
						ArrayList<DBObject> ids = MongoDB.getDigitalStoryDAO().getDigitalStories(from, to, id);
						long total = MongoDB.getDigitalStoryDAO().getDigitalStoriesByUserTotalNumber(id);
						BasicDBObject obj = new BasicDBObject();
						obj.put("totalSize", total);
						obj.put("start", from);
						obj.put("to", to);
						obj.put("values", ids);
						res = com.mongodb.util.JSON.serialize(obj);
					
				}
				else {
					return badRequest();
				}
					
				return ok(res);
			
			 }
		     catch (Exception ex){
				  	  System.out.println(ex.getMessage());
				  	  response().setContentType("application/json");
				  	  return internalServerError(Messages.get("apperror"));
				    }
	 }
	 
	public static Result login(){
	     try{
	    	 /*take out SSL check for now
	    	 if (request().getHeader("x-forwarded-proto") != null) {
	    	      if (request().getHeader("x-forwarded-proto").indexOf("https") != 0) {
	    	    	  return badRequest("Expecting secure request");
	    	      }}*/
	    	session().clear();
	    	JsonNode json = request().body().asJson(); 
	    	response().setContentType("application/json");
			if(json==null){
			    return badRequest("Expecting Json data");
			}
			else {
			   /*check json object*/ 
			    String email = json.findPath("email").getTextValue();
			    String password = json.findPath("password").getTextValue();
			    if(email == null) {
			      return badRequest(Messages.get("missing")+" [email]");      
			    }
			    else{
			      if(MongoDB.getUserDAO().getByEmail(email)==null){
					  return notFound("Email "+Messages.get("notfound"));
				  }
				}
			    if(password == null) {
				      return badRequest(Messages.get("missing")+" [password]");
				    }
			    else{
			    	User u=MongoDB.getUserDAO().getByEmailPassword(email,password);
					   
			       //User u=MongoDB.getUserDAO().getByUsernamePassword(uname,password);
				   if(u==null){
					    	return badRequest(Messages.get("errorsignin"));
					    }
				   else if(u.accountActive==false){
					 	return badRequest(Messages.get("accountinactive"));
						   
				   }
				    
			        else{
			    	 response().setContentType("application/json");
			    	 session("uid", u.getId());
			    	
			    	 String query = request().queryString().toString();
			         query = query == null ? "" : query.toLowerCase();
			         
			         
					 
					 return ok(mapper.writeValueAsString(u));
			       }
				  }	    
				
				
			}
		   }
		    catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  response().setContentType("application/json");
		  	  return internalServerError(Messages.get("apperror"));
		    }
		
		}
		
	
	public static Result logout(){
		try{
		    response().setContentType("application/json");	
		  	session().clear();
	    	return ok();
		}
    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }
  }
		
	
	
	public static Result save(){
		response().setContentType("application/json");	
     try{
    	 /*take out SSL check for now
    	 if (request().getHeader("x-forwarded-proto") != null) {
    	      if (request().getHeader("x-forwarded-proto").indexOf("https") != 0) {
    	    	  return badRequest("Expecting secure request");
    	      }}*/
    	
		JsonNode json = request().body().asJson();
		User user = new User();
		
		if(json == null) {
		    return badRequest("Expecting Json data");
		}
		else {
		   /*check json object*/ 
		    String id=null;
		    if(json.findValue("id")!=null){
		    	id=json.findValue("id").asText();
		    }
		    
		        String missingFields="";
                 
				    if(json.findValue("username") == null) {
				    	missingFields+=Messages.get("missing")+" [username]"+"\r\n";
				      
				    }
				    
				  
	      		  
				    if(json.findValue("password")==null) {
				    	missingFields+=Messages.get("missing")+" [password]"+"\r\n";
					    } 
				    if(json.findValue("password_confirmation")==null) {
				    	missingFields+=Messages.get("missing")+" [pasword confrmation]"+"\r\n";
					    }
				    
				    if(json.findValue("email")==null) {
				    	missingFields+=Messages.get("missing")+" [email]"+"\r\n";
					    } 
				    if(json.findValue("password")!=null && json.findValue("password_confirmation")!=null && 
     		    		!json.findValue("password").asText().equalsIgnoreCase(json.findValue("password_confirmation").asText())) {
				    	missingFields+=Messages.get("passnomatch")+"\r\n";

					    } 
	                if(missingFields.length()>0){			    
					      return badRequest(missingFields);
					  	
	                }
	                
	      		    if(id!=null){
	      		    	//for existing users
	      		    	User u=MongoDB.getUserDAO().get(new ObjectId(id));
	      		    	if(!json.findValue("username").asText().equalsIgnoreCase(u.getUsername()))
	      		    	{
	      		    		//username has changed
	      		    		if(MongoDB.getUserDAO().getByUsername(json.findValue("username").asText())!=null){
	      		    			return badRequest(Messages.get("exists")+" [username]="+json.findPath("username").getTextValue());
	      		    	    }
	      		    		
	      		    	}
	      		    	if(!json.findValue("email").asText().equalsIgnoreCase(u.getEmail())){
		      		      //email changed	
	      		    	  if(MongoDB.getUserDAO().getByEmail(json.findValue("email").asText())!=null){
	      		    		return badRequest(Messages.get("exists")+" [email]="+json.findPath("email").getTextValue());
	      		    		
	      		    	}
	      		    	if(json.findPath("password").isMissingNode()){
	    		    		  return badRequest("If email is altered password must be reset.");
	  		    			
	    		    	  }
	      		    	}
	      		    }
	      		    else{
		        	    //check if username is already used, check if email is used -- for new users only no id present
					    if(MongoDB.getUserDAO().getByUsername(json.findValue("username").asText())!=null){
					    	return badRequest(Messages.get("exists")+" [username]="+json.findPath("username").getTextValue());
					    }
					    if(MongoDB.getUserDAO().getByEmail(json.findValue("email").asText())!=null){
					    	return badRequest(Messages.get("exists")+" [email]="+json.findPath("email").getTextValue());
					    }
				    
	      		    }
				    user = mapper.readValue(json, User.class);
				  //check if password exists in request, if yes then encrypt 
				    if(!json.findPath("password").isMissingNode()){
				    	user.encryptAndSetEmailPassword(user.getEmail(),json.findPath("password").asText());
				    }
				  //if new user set role to editor
				    if(user.getRole()==null)
				    	user.setRole(SecurityRole.CONTRIBUTOR);
			        user.setAccountActive(true);
		          
		    
			        /*if(id != null && json.findPath("password").isMissingNode()){
			        /*id exists and password missing, dont do password reset
			        	User u=MongoDB.getUserDAO().get(new ObjectId(id));
			        	
			        	user.md5Password=u.getMd5Password();
				      }*/
			        
		    
		    MongoDB.getUserDAO().save(user);
		    response().setContentType("application/json");
			 
			return ok(mapper.writeValueAsString(user));
			//return ok(user.toString());
		}
	   }
	    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }
	}
	
	
	
	public static Result get(String id){
		try{
			
			User u=MongoDB.getUserDAO().get(new ObjectId(id));
			if(u==null){
				return notFound();
			}
			response().setContentType("application/json");
			return ok(mapper.writeValueAsString(u));
		   }
		catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }

	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result delete(){
		try{
			response().setContentType("application/json");
			
				if(request().queryString().size() == 0) {
					return badRequest();
				}else if(request().queryString().get("id") == null){
					return badRequest();
				}else {
					String id = request().queryString().get("id")[0];
					ObjectId userId = new ObjectId(id);
			        MongoDB.getUserDAO().deleteById(userId);
					
					response().setContentType("application/json");
					return ok();
				}
				
		}
	   catch (Exception ex){
			  	  System.out.println(ex.getMessage());
			  	  response().setContentType("application/json");
			  	  return internalServerError(Messages.get("apperror"));
			    }
	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result list(){
	try{
		response().setContentType("application/json");
		ObjectNode result = Json.newObject();
		if(request().queryString().get("to") == null && request().queryString().get("from") == null){
			int from=0;
			int to=(int)MongoDB.getUserDAO().count();
			ArrayList<String> ids = new ArrayList<String>();
			ids=MongoDB.getUserDAO().getUsersByRange(from, to);
			
			result.put("totalSize", ids.size());
			result.put("start", from);
			result.put("to", to);
			
			
			ArrayNode uarray=new ArrayNode(JsonNodeFactory.instance);
			for(String id:ids){
				uarray.add(id);
			}
			result.put("values", uarray);

		}else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
			int to = Integer.parseInt(request().queryString().get("to")[0]);
			int from = 0;
			ArrayList<String> ids = new ArrayList<String>();
			ids=MongoDB.getUserDAO().getUsersByRange(from, to);
			long total = MongoDB.getUserDAO().count();
			
			result.put("totalSize", total);
			result.put("start", from);
			result.put("to", to);
			ArrayNode uarray=new ArrayNode(JsonNodeFactory.instance);
			for(String id:ids){
				uarray.add(id);
			}
			result.put("values", uarray);
		}else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
			
				int to = Integer.parseInt(request().queryString().get("to")[0]);
				int from = Integer.parseInt(request().queryString().get("from")[0]);
				ArrayList<String> ids = new ArrayList<String>();
				ids=MongoDB.getUserDAO().getUsersByRange(from, to);
				long total = MongoDB.getUserDAO().count();
				
				result.put("totalSize", total);
				result.put("start", from);
				result.put("to", to);
				ArrayNode uarray=new ArrayNode(JsonNodeFactory.instance);
				for(String id:ids){
					uarray.add(id);
				}
				result.put("values", uarray);
				
	
		}
		else {
			return badRequest();
		}
		return ok(result);
	
	 }
     catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  return internalServerError(Messages.get("apperror"));
		    }
	}
	
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result Userlist(){
	try{
		response().setContentType("application/json");
		
		int size = Integer.parseInt(request().queryString().get("jtPageSize")[0]);
		int from = Integer.parseInt(request().queryString().get("jtStartIndex")[0]);
		String sortby=request().queryString().get("jtSorting")[0];
		String[] tokens = sortby.split(" ");
		sortby=tokens[0];		
		if(tokens[1].equalsIgnoreCase("desc")){
			sortby="-"+sortby;
		}
		ArrayList<DBObject> users = MongoDB.getUserDAO().getUsersByOrder(from, size,sortby);
		long total = MongoDB.getUserDAO().count();
		//ObjectNode obj = Json.newObject();
		BasicDBObject obj = new BasicDBObject();
		obj.put("Result", "OK");		
		
		obj.put("Records", users);
		obj.put("TotalRecordCount", total);
		
		return ok(com.mongodb.util.JSON.serialize(obj));
		//return ok(gson.toJson(obj));
	 }
     catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  return internalServerError(Messages.get("apperror"));
		    }
	}	
}
