package controllers;


import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import gr.ntua.ivml.awareness.persistent.DigitalStory;
import gr.ntua.ivml.awareness.persistent.StoryImage;
import gr.ntua.ivml.awareness.persistent.StoryObject;
import gr.ntua.ivml.awareness.persistent.Theme;
import gr.ntua.ivml.awareness.persistent.User;


import gr.ntua.ivml.awareness.util.MongoDB;



import org.bson.types.ObjectId;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;


import com.google.code.morphia.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;


import be.objectify.deadbolt.actions.Dynamic;



import play.i18n.Messages;
import play.mvc.*;




public class Admin extends Controller {
	 static ObjectMapper mapper = new ObjectMapper( );
	
		

	@Dynamic(value = "minimumLevelRequired", meta="admin")
	public static Result deleteObject(){
		try{
			
				if(request().queryString().size() == 0) {
					return badRequest();
				}else if(request().queryString().get("id") == null){
					return badRequest();
				}else {
					String id = request().queryString().get("id")[0];
					/*only allowed if no stories have been added to this theme*/
					StoryObject so=MongoDB.getStoryObjectDAO().get(new ObjectId(id));
					if(so==null){
						
						//for orphans just remove object from all stories and check whether they are still publishable
						//MongoDB.getDigitalStoryDAO().deleteStoryObjectFromStories(id);
						//now delete the story object
						//MongoDB.getStoryObjectDAO().deleteById(new ObjectId(id));
					
						return notFound();
					}
					//if this is an object created from an uploaded image then delete the image and all story objects generated from it
					if(so.getSosource().equalsIgnoreCase("local") && so.getType().equalsIgnoreCase("image") && so.getStoryImage()!=null){
						MongoDB.getStoryImageDAO().remove(id);/* also updates the stories that used the object */
						
					}
					else{ 
					   //just remove object from all stories and check whether they are still publishable
						MongoDB.getDigitalStoryDAO().deleteStoryObjectFromStories(id);
						//now delete the story object
						MongoDB.getStoryObjectDAO().deleteById(new ObjectId(id));
					
					}
					
					
					
					response().setContentType("application/json");
					return ok();
				   
				}
				
		}
	   catch (Exception ex){
			  	  System.out.println(ex.getMessage());
			  	  response().setContentType("application/json");
			  	  
			  	  return internalServerError();
			    }
	}

	@Dynamic(value = "minimumLevelRequired", meta="admin")
	public static Result deleteUser(){
		try{
			response().setContentType("application/json");
			Map<String,String[]> params = request().body().asFormUrlEncoded();
			
			BasicDBObject obj = new BasicDBObject();
			
			    
				if(params == null) {
					obj.put("Result", "ERROR");
					obj.put("Message", "No parameters");
		        	return ok(com.mongodb.util.JSON.serialize(obj));
				}
				else {
					String id = params.get("id")[0];
					ObjectId userId = new ObjectId(id);
					/*delete his stories, image uploads & story objects*/					
					
					List<Key<StoryImage>> storyimages=MongoDB.getStoryImageDAO().createQuery().field("userid").equal(id).asKeyList();
					 /* call remove function that removes it from all stories and story objects as well as GridFS
		     		  * 
		     		  */
		     	 
					for(Key<StoryImage> key:storyimages){MongoDB.getStoryImageDAO().remove(key.getId().toString());}
			    	
					MongoDB.getDS().delete(MongoDB.getDS().createQuery(StoryObject.class).filter("creator", id));
					MongoDB.getDS().delete(MongoDB.getDS().createQuery(DigitalStory.class).filter("creator", id));
					
					
					MongoDB.getUserDAO().deleteById(userId);
					
					response().setContentType("application/json");
					obj.put("Result", "OK");
					obj.put("Message", "User deleted");
					return ok(com.mongodb.util.JSON.serialize(obj));
				}
			
				
		}
	   catch (Exception ex){
			  	  System.out.println(ex.getMessage());
			  	  response().setContentType("application/json");
			  	  return internalServerError(Messages.get("apperror"));
			    }
	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin")
	public static Result updateUser(){
		response().setContentType("application/json");	
     try{
    	 /*take out SSL check for now
    	 if (request().getHeader("x-forwarded-proto") != null) {
    	      if (request().getHeader("x-forwarded-proto").indexOf("https") != 0) {
    	    	  return badRequest("Expecting secure request");
    	      }}*/
    	
		Map<String,String[]> params = request().body().asFormUrlEncoded();
		
		BasicDBObject obj = new BasicDBObject();
		if(params == null) {
			obj.put("Result", "ERROR");
			obj.put("Message", "No form data");
        	return ok(com.mongodb.util.JSON.serialize(obj));
		}
		else {
		   /*check json object*/ 
		    String id=null;
		    if(params.get("id")!=null){
		    	id=params.get("id")[0];
		    }
		    
		        String missingFields="";
		        if(id==null){
		        	obj.put("Result", "ERROR");
		        	obj.put("Message", "Missing user id");
		        	return ok(com.mongodb.util.JSON.serialize(obj));
		        }
		        	
		        
		        if(params.get("username")==null || params.get("username")[0].length()==0){
		        	missingFields+="Username cannot be empty\r\n";
		        	obj.put("Result", "ERROR");
		        	obj.put("Message", missingFields);
		        	return ok(com.mongodb.util.JSON.serialize(obj));


		        }
		        if(params.get("email")==null || params.get("email")[0].length()==0){
		        	missingFields+="Email cannot be empty\r\n";
		        	obj.put("Result", "ERROR");
		        	obj.put("Message", missingFields);
		        	return ok(com.mongodb.util.JSON.serialize(obj));

		        }
			        if(params.get("password")!=null && params.get("passwordConf")==null){
			        	missingFields+="Both password and password confirmation must be provided\r\n";

			        }
			        
			        if(params.get("password")==null && params.get("passwordConf")!=null){
			        	missingFields+="Both password and password confirmation must be provided\r\n";

			        }
	      		  
			        if(params.get("password")!=null && params.get("passwordConf")!=null
			        		&&	!params.get("password")[0].equalsIgnoreCase(params.get("passwordConf")[0])) {
				    	missingFields+=Messages.get("passnomatch")+"\r\n";

					    } 
	                if(missingFields.length()>0){			    
					      //return badRequest(missingFields);
	                	obj.put("Result", "ERROR");
			        	obj.put("Message", missingFields);
			        	return ok(com.mongodb.util.JSON.serialize(obj));
					  	
	                }
	                
	                
	                
	      		    
	      		    	//for existing users
	      		    User u=MongoDB.getUserDAO().get(new ObjectId(id));
	      		    
	      		    if(!params.get("username")[0].equalsIgnoreCase(u.getUsername()))
    		    	{
    		    		//username has changed
    		    		if(MongoDB.getUserDAO().getByUsername(params.get("username")[0])!=null){
    		    			obj.put("Result", "ERROR");
    			        	obj.put("Message", Messages.get("exists")+" [username]="+params.get("username")[0]);
    			        	return ok(com.mongodb.util.JSON.serialize(obj));
    		    			
    		    	    }
    		    		
    		    	}
    		    	if(!params.get("email")[0].equalsIgnoreCase(u.getEmail())){
	      		      //email changed	
    		    	  if(MongoDB.getUserDAO().getByEmail(params.get("email")[0])!=null){
    		    		  obj.put("Result", "ERROR");
  			        	  obj.put("Message", Messages.get("exists")+" [email]="+params.get("email")[0]);
  			        	  return ok(com.mongodb.util.JSON.serialize(obj));
  		    			
    		    		
    		    	  }	
    		    	  if(params.get("password")[0]==null || params.get("password")[0].length()==0){
    		    		  obj.put("Result", "ERROR");
  			        	  obj.put("Message", "If email is altered password must be reset");
  			        	  return ok(com.mongodb.util.JSON.serialize(obj));
  		    			
    		    	  }
    		    	}
	      		    
	      		    u.setUsername(params.get("username")[0]);
	      		    u.setEmail(params.get("email")[0]);
	      		    
	      		    u.setRole(params.get("role")[0]);
	      		    u.setAccountActive(new Boolean(params.get("accountActive")[0]));
	      		  
	      		     response().setContentType("application/json");
	    			 
			         
			  		 obj.put("Result", "OK");		
			  		 DBObject tmpObj = MongoDB.getMorphia().toDBObject(u); 
			  		 tmpObj.removeField("md5Password");
			  		 tmpObj.removeField("_id");
			  		 SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd");
		    		 Date ac=u.getAccountCreated();
		    		
		    		 tmpObj.put("id", u.getId());
		    		 tmpObj.put("accountCreated","/Date("+ac.getTime()+")/");
			  		 
		    		 
			  		 obj.put("Record", tmpObj);	    
	      		  //check if password exists in request, if yes then encrypt 
				    if(params.get("password")[0]!=null && params.get("password")[0].length()>0){
				    	u.encryptAndSetEmailPassword(u.getEmail(),params.get("password")[0]);
				    }
				          
		    
		          MongoDB.getUserDAO().save(u);
		          
		     
			return ok(com.mongodb.util.JSON.serialize(obj));
			
		}
	   }
	    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }
	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin")
	public static Result saveUser(){
		response().setContentType("application/json");	
     try{
    	 
    	
		Map<String,String[]> params = request().body().asFormUrlEncoded();
		
		BasicDBObject obj = new BasicDBObject();
		if(params == null) {
			obj.put("Result", "ERROR");
			obj.put("Message", "No form data");
        	return ok(com.mongodb.util.JSON.serialize(obj));
		}
		else {
		    
		        String missingFields="";
		        	
		        
		        if(params.get("username")==null || params.get("username")[0].length()==0){
		        	missingFields+="Username cannot be empty\r\n";
		        	obj.put("Result", "ERROR");
		        	obj.put("Message", missingFields);
		        	return ok(com.mongodb.util.JSON.serialize(obj));


		        }
		        if(params.get("email")==null || params.get("email")[0].length()==0){
		        	missingFields+="Email cannot be empty\r\n";
		        	obj.put("Result", "ERROR");
		        	obj.put("Message", missingFields);
		        	return ok(com.mongodb.util.JSON.serialize(obj));

		        }
			        if(params.get("password")!=null && params.get("passwordConf")==null){
			        	missingFields+="Both password and password confirmation must be provided\r\n";

			        }
			        
			        if(params.get("password")==null && params.get("passwordConf")!=null){
			        	missingFields+="Both password and password confirmation must be provided\r\n";

			        }
	      		  
			        if(params.get("password")!=null && params.get("passwordConf")!=null
			        		&&	!params.get("password")[0].equalsIgnoreCase(params.get("passwordConf")[0])) {
				    	missingFields+=Messages.get("passnomatch")+"\r\n";

					    } 
	                if(missingFields.length()>0){			    
					      //return badRequest(missingFields);
	                	obj.put("Result", "ERROR");
			        	obj.put("Message", missingFields);
			        	return ok(com.mongodb.util.JSON.serialize(obj));
					  	
	                }
	                
		            	if(MongoDB.getUserDAO().getByUsername(params.get("username")[0])!=null){
			    			obj.put("Result", "ERROR");
				        	obj.put("Message", Messages.get("exists")+" [username]="+params.get("username")[0]);
				        	return ok(com.mongodb.util.JSON.serialize(obj));
			    			
			    	    }
			    	
	                
		          	  if(MongoDB.getUserDAO().getByEmail(params.get("email")[0])!=null){
			    		  obj.put("Result", "ERROR");
				        	  obj.put("Message", Messages.get("exists")+" [email]="+params.get("email")[0]);
				        	  return ok(com.mongodb.util.JSON.serialize(obj));
			    			
			    		
			    	  }	
			        
	      		    	//for existing users
	      		    User u=new User();
	      		    
	      		    
	      		    u.setUsername(params.get("username")[0]);
	      		    u.setEmail(params.get("email")[0]);
	      		    
	      		    u.setRole(params.get("role")[0]);
	      		    u.setAccountActive(new Boolean(params.get("accountActive")[0]));
	      		  
	      		    if(params.get("password")[0]!=null && params.get("password")[0].length()>0){
				    	u.encryptAndSetEmailPassword(u.getEmail(),params.get("password")[0]);
				    }
				    
	      		     response().setContentType("application/json");
	    			 
			         
			  		 obj.put("Result", "OK");		
			  		 DBObject tmpObj = MongoDB.getMorphia().toDBObject(u); 
			  		 tmpObj.removeField("md5Password");
			  		 tmpObj.removeField("_id");
			  		  
		    		
			  		 
			  		 MongoDB.getUserDAO().save(u);
			  		 tmpObj.put("id", u.getId());
			  		 SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd");
		    		 Date ac=u.getAccountCreated();
		    		
		    		 tmpObj.put("accountCreated","/Date("+ac.getTime()+")/");
		    		 
		    		 obj.put("Record", tmpObj);	    
		      		  
		          
		     
			return ok(com.mongodb.util.JSON.serialize(obj));
			
		}
	   }
	    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }
	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result Usersearch(){
	try{
		response().setContentType("application/json");
		
		int size = Integer.parseInt(request().queryString().get("jtPageSize")[0]);
		int from = Integer.parseInt(request().queryString().get("jtStartIndex")[0]);
		String sortby=request().queryString().get("jtSorting")[0];
		String searchterm=request().queryString().get("searchterm")[0];
		
		String[] tokens = sortby.split(" ");
		sortby=tokens[0];		
		if(tokens[1].equalsIgnoreCase("desc")){
			sortby="-"+sortby;
		}
		ArrayList<DBObject> users = MongoDB.getUserDAO().getUsersBySearch(from, size,sortby,searchterm);
		long total = users.size();
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
	
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result Themesearch(){
	try{
		response().setContentType("application/json");
		
		int size = Integer.parseInt(request().queryString().get("jtPageSize")[0]);
		int from = Integer.parseInt(request().queryString().get("jtStartIndex")[0]);
		String sortby=request().queryString().get("jtSorting")[0];
		String searchterm=request().queryString().get("searchterm")[0];
		
		String[] tokens = sortby.split(" ");
		sortby=tokens[0];		
		if(tokens[1].equalsIgnoreCase("desc")){
			sortby="-"+sortby;
		}
		ArrayList<DBObject> themes = MongoDB.getThemeDAO().getThemesBySearch(from, size,sortby,searchterm);
		long total = themes.size();
		//ObjectNode obj = Json.newObject();
		BasicDBObject obj = new BasicDBObject();
		obj.put("Result", "OK");		
		
		obj.put("Records", themes);
		obj.put("TotalRecordCount", total);
		
		return ok(com.mongodb.util.JSON.serialize(obj));
		//return ok(gson.toJson(obj));
	 }
     catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  return internalServerError(Messages.get("apperror"));
		    }
	}

	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result Themelist(){
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
		ArrayList<DBObject> themes = MongoDB.getThemeDAO().getThemesByOrder(from, size,sortby);
		long total = MongoDB.getThemeDAO().count();
		//ObjectNode obj = Json.newObject();
		BasicDBObject obj = new BasicDBObject();
		obj.put("Result", "OK");		
		
		obj.put("Records", themes);
		obj.put("TotalRecordCount", total);
		
		return ok(com.mongodb.util.JSON.serialize(obj));
		//return ok(gson.toJson(obj));
	 }
     catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  return internalServerError(Messages.get("apperror"));
		    }
	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin")
	public static Result getTheme(String id){
		try{
			
			Theme th=MongoDB.getThemeDAO().get(new ObjectId(id));
			if(th==null){
				return notFound();
			}
			String wallpaperid=th.getWallpaper();
			GridFSDBFile wallpaperFile=MongoDB.getGridFS().findOne(new ObjectId(wallpaperid));
			String wallpaperName=wallpaperFile.getFilename();
			
			String bannerid=th.getBanner();
			GridFSDBFile bannerFile=MongoDB.getGridFS().findOne(new ObjectId(bannerid));
			String bannerName=bannerFile.getFilename();
			
			String minibannerid=th.getMinibanner();
			GridFSDBFile minibannerFile=MongoDB.getGridFS().findOne(new ObjectId(minibannerid));
			String minibannerName=minibannerFile.getFilename();
			BasicDBObject obj = new BasicDBObject();
			obj.put("id", th.getId());
			obj.put("title", th.getTitle());
			obj.put("description", th.getDescription());
			obj.put("wallpaperName", wallpaperName);
			obj.put("bannerName", bannerName);
			obj.put("minibannerName",minibannerName);
			obj.put("background", th.getBackground());
			obj.put("wallpaper", th.getWallpaper());
			obj.put("banner", th.getBanner());
			obj.put("minibanner", th.getMinibanner());
			response().setContentType("application/json");
			return ok(mapper.writeValueAsString(th));
		   }
		catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }

	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json") 
	public static Result updateTheme(){
		response().setContentType("application/json");	
     try{
    	JsonNode json = request().body().asJson();
		Theme th = new Theme();
		
		if(json == null) {
		    return badRequest("Expecting Json data");
		}
		else {
		   /*check json object*/ 
		    
		      //check if all mandatory fields are present
                 String missingFields="";
                 
	        	   th = mapper.readValue(json, Theme.class);
				   
				    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
				    Validator validator= factory.getValidator();
				    Set<ConstraintViolation<Theme>> violations = validator.validate(th);
				    
				    for(ConstraintViolation<Theme> cv:violations){
				    	missingFields+="["+cv.getPropertyPath()+"]"+" "+cv.getMessage()+"\r\n";
					      
				    }
				    if(missingFields.length()>0){			    
					      return badRequest(missingFields);
					  	
	                }
				    String wallpaper;
				    String banner;
				    String minibanner;
				    /*find out which images changed*/
				    if(th.getWallpaper()!=null && !json.findValue("originalwallpaper").getTextValue().equalsIgnoreCase(th.getWallpaper())){
				    	/*need to replace wallpaper file */
				    	File wallpaperFile=new File(System.getProperty("java.io.tmpdir"),json.findValue("wallpaper").asText());
		        		wallpaper = MongoDB.getGridFSDAO().saveFile(new FileInputStream(wallpaperFile), json.findValue("wallpaperName").asText());
		        		MongoDB.getGridFS().remove(json.findValue("originalwallpaper").getTextValue());
		        		th.setWallpaper(wallpaper);
				    }
				    
				    if(th.getBanner()!=null && !json.findValue("originalbanner").getTextValue().equalsIgnoreCase(th.getBanner())){
				    	/*need to replace wallpaper file */
				    	File bannerFile=new File(System.getProperty("java.io.tmpdir"),json.findValue("banner").asText());
		        		banner = MongoDB.getGridFSDAO().saveFile(new FileInputStream(bannerFile), json.findValue("bannerName").asText());
		        		MongoDB.getGridFS().remove(json.findValue("originalbanner").getTextValue());
		        		th.setBanner(banner);
				    }
				    if(th.getMinibanner()!=null && !json.findValue("originalminibanner").getTextValue().equalsIgnoreCase(th.getMinibanner())){
				    	/*need to replace wallpaper file */
				    	File minibannerFile=new File(System.getProperty("java.io.tmpdir"),json.findValue("minibanner").asText());
		        		minibanner = MongoDB.getGridFSDAO().saveFile(new FileInputStream(minibannerFile), json.findValue("minibannerName").asText());
		        		MongoDB.getGridFS().remove(json.findValue("originalminibanner").getTextValue());
		        		th.setMinibanner(minibanner);
		        		
				    }
				    
				       
				    MongoDB.getThemeDAO().save(th);
				    
					
					 
					return ok(mapper.writeValueAsString(th));
			
		}
	   }
	    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }
	}
	

	@Dynamic(value = "minimumLevelRequired", meta="admin")
	public static Result deleteTheme(){
		try{
			response().setContentType("application/json");
			Map<String,String[]> params = request().body().asFormUrlEncoded();
			
			BasicDBObject obj = new BasicDBObject();
			
			    
				if(params == null) {
					obj.put("Result", "ERROR");
					obj.put("Message", "No parameters");
		        	return ok(com.mongodb.util.JSON.serialize(obj));
				}
				else {
					String id = params.get("id")[0];
					ObjectId userId = new ObjectId(id);
					/*delete his stories, image uploads & story objects*/					
					
					int storiesontheme = MongoDB.getDigitalStoryDAO().hasDigitalStoriesTheme(id);
					
					if(storiesontheme == 0)
					{
						/*delete images from this theme*/
						
					    Theme th=MongoDB.getThemeDAO().get(new ObjectId(id));
					    MongoDB.getGridFS().remove(th.getWallpaper());
					    MongoDB.getGridFS().remove(th.getBanner());
					    MongoDB.getGridFS().remove(th.getMinibanner());
			            MongoDB.getThemeDAO().deleteById(new ObjectId(id));
			        
			            response().setContentType("application/json");
						obj.put("Result", "OK");
						obj.put("Message", "Theme deleted");
						return ok(com.mongodb.util.JSON.serialize(obj));
				   }
					else{
						/*400 code*/
						obj.put("Result", "ERROR");
						obj.put("Message", Messages.get("themedelerror"));
			        	return ok(com.mongodb.util.JSON.serialize(obj));
						
					}
					
				}
			
				
		}
	   catch (Exception ex){
			  	  System.out.println(ex.getMessage());
			  	  response().setContentType("application/json");
			  	  return internalServerError(Messages.get("apperror"));
			    }
	}
	
	public static Result totalThemeStories(String id){
		response().setContentType("application/json");
		try{
			
				String res = "{}";
				BasicDBObject obj = new BasicDBObject();

				long total=MongoDB.getDigitalStoryDAO().numberStoriesByTheme(id);
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
		
		
		
}
