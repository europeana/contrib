package controllers;


import gr.ntua.ivml.awareness.persistent.Theme;
import gr.ntua.ivml.awareness.util.MongoDB;


import java.io.File;
import java.io.FileInputStream;

import java.util.Iterator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import org.codehaus.jackson.map.ObjectMapper;

import org.codehaus.jackson.node.ObjectNode;
import be.objectify.deadbolt.actions.Dynamic;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;


import play.i18n.Messages;
import play.mvc.*;




public class Themes extends Controller {
	 static ObjectMapper mapper = new ObjectMapper( );
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json") 
	public static Result save(){
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
				    ObjectNode themejson=(ObjectNode)json;
				    /*save all images to gridfs to get their ids*/
	                File wallpaperFile=new File(System.getProperty("java.io.tmpdir"),json.findValue("wallpaper").asText());
	        		String wallpaper = MongoDB.getGridFSDAO().saveFile(new FileInputStream(wallpaperFile), json.findValue("wallpaperName").asText());
		            
	        		File bannerFile=new File(System.getProperty("java.io.tmpdir"),json.findValue("banner").asText());
	        		String banner = MongoDB.getGridFSDAO().saveFile(new FileInputStream(bannerFile), json.findValue("bannerName").asText());
		            
	        		File minibannerFile=new File(System.getProperty("java.io.tmpdir"),json.findValue("minibanner").asText());
	        		String minibanner = MongoDB.getGridFSDAO().saveFile(new FileInputStream(minibannerFile), json.findValue("minibannerName").asText());
		            
	        		/*Small banner
					H 123 W 671
					
					BG image
					H 850	W 1200*/

	        		themejson.remove("wallpaper");
	        		themejson.remove("banner");
	        		themejson.remove("minibannerName");
	        		themejson.put("wallpaper",wallpaper);
	        		themejson.put("minibanner",minibanner);
	        		themejson.put("banner",banner);
	        		
	        		th = mapper.readValue(themejson, Theme.class);
					   
				    MongoDB.getThemeDAO().save(th);
				    
					
					 
					return ok(mapper.writeValueAsString(th));
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
			
			Theme th=MongoDB.getThemeDAO().get(new ObjectId(id));
			if(th==null){
				return notFound();
			}
			response().setContentType("application/json");
			return ok(mapper.writeValueAsString(th));
		   }
		catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }

	}
	
	public static Result getDefault(){
		try{
			Theme th=MongoDB.getThemeDAO().getDefault();
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
	public static Result delete(){
		try{
			
				if(request().queryString().size() == 0) {
					return badRequest();
				}else if(request().queryString().get("id") == null){
					return badRequest();
				}else {
					String id = request().queryString().get("id")[0];
					/*only allowed if no stories have been added to this theme*/
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
					    return ok();
				   }
					else{
						/*400 code*/
						return badRequest(Messages.get("themedelerror"));
					}
				}
				
		}
	   catch (Exception ex){
			  	  System.out.println(ex.getMessage());
			  	  response().setContentType("application/json");
			  	  
			  	  return internalServerError(Messages.get("apperror"));
			    }
	}
	
	public static Result list(){
	try{
		response().setContentType("application/json");
		String res = "{}";		
		Iterable<Theme> themes = MongoDB.getThemeDAO().find().fetch();
	    	
		
		Iterator<Theme> it=themes.iterator();
		BasicDBObject obj = new BasicDBObject();
		BasicDBList list=new BasicDBList();
		while(it.hasNext()){
			obj = new BasicDBObject();
			Theme th=(Theme)it.next();
			String id=th.getId();
		    obj.put("id", id);
		    obj.put("title", th.getTitle());
		    
			list.add(obj);
		}
	  	
	    	obj = new BasicDBObject();
			obj.put("totalSize", list.size());
			
			obj.put("values", list);
			res = com.mongodb.util.JSON.serialize(obj);
	    	
			

			return ok(res);
			
		 }
	     catch (Exception ex){
			  	  System.out.println(ex.getMessage());
			  	  response().setContentType("application/json");
			  	  return internalServerError(Messages.get("apperror"));
			    }
    }
	
	
	public static Result listStories(String id){
		response().setContentType("application/json");
		try{
			
				String res = "{}";
				BasicDBList storiesontheme = new BasicDBList();
				BasicDBObject obj = new BasicDBObject();
				
				if(request().queryString().get("to") == null && request().queryString().get("from") == null){
	               //fetch all themes
					storiesontheme = MongoDB.getDigitalStoryDAO().getDigitalStoriesByTheme(id,-1,-1);
					obj.put("from", 0);
					obj.put("to", storiesontheme.size());
				    	
				}
				else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
					    int to = Integer.parseInt(request().queryString().get("to")[0]);
						obj.put("from", 0);
						obj.put("to", to);
						storiesontheme = MongoDB.getDigitalStoryDAO().getDigitalStoriesByTheme(id,0,to);
					
				}
				else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
					int to = Integer.parseInt(request().queryString().get("to")[0]);
					int from = Integer.parseInt(request().queryString().get("from")[0]);
					storiesontheme = MongoDB.getDigitalStoryDAO().getDigitalStoriesByTheme(id,from,to);
					obj.put("from", from);
					obj.put("to", to);
				}
				else {
					return badRequest();
				}

				long total=MongoDB.getDigitalStoryDAO().getAllStoriesByTheme(id);
		    	obj.put("totalSize", total);
				
				obj.put("stories", storiesontheme);
				res = com.mongodb.util.JSON.serialize(obj);
	
				return ok(res);
			
			 }
		     catch (Exception ex){
				  	  System.out.println(ex.getMessage());
				  	  response().setContentType("application/json");
				  	  return internalServerError(Messages.get("apperror"));
				    }
	    }
		
	public static Result totalStories(String id){
		response().setContentType("application/json");
		try{
			
				String res = "{}";
				BasicDBObject obj = new BasicDBObject();

				long total=MongoDB.getDigitalStoryDAO().getAllStoriesByTheme(id);
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
