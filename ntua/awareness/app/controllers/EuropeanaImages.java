package controllers;


import gr.ntua.ivml.awareness.persistent.StoryImage;
import gr.ntua.ivml.awareness.persistent.StoryObject;
import gr.ntua.ivml.awareness.util.ImageHelper;
import gr.ntua.ivml.awareness.util.MongoDB;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;


import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import org.codehaus.jackson.map.ObjectMapper;

import org.codehaus.jackson.node.ObjectNode;

import be.objectify.deadbolt.actions.Dynamic;



import play.i18n.Messages;
import play.mvc.*;
import java.awt.image.*;




public class EuropeanaImages extends Controller {
	 static ObjectMapper mapper = new ObjectMapper( );
	
	@Dynamic(value = "minimumLevelRequired", meta="contributor") 
	public static Result save(){
		response().setContentType("application/json");
		String coverImage=null;
		String thumbImage=null;
		String cubeImage=null;
		StoryImage si = new StoryImage();
		StoryObject so=new StoryObject();
     try{
    	JsonNode json = request().body().asJson();
		
		
		if(json == null) {
		    return badRequest("Expecting Json data");
		}
		else {
		   /*check json object*/ 
		    
		      //check if all mandatory fields are present
                    String missingFields="";
                 
	        	    si = mapper.readValue(json, StoryImage.class);
				   
	        	    
	        	    String title = json.findPath("title").getTextValue();
				    String description = json.findPath("description").getTextValue();
				    if(title == null && description==null || (title.isEmpty() && description.isEmpty())) {
				      return badRequest("one of 'title' and/or 'description' fields must be filled");
				      
				    }
				    String originaltemp=json.findPath("original").getTextValue();
				    if(originaltemp == null || originaltemp.isEmpty()) {
					      return badRequest("You must upload an image file");
					      
					    }
					    
				    
				    if(missingFields.length()>0){			    
					      return badRequest(missingFields);
					  	
	                }
				    ObjectNode storyimagejson=(ObjectNode)json;
				    /*save all images to gridfs to get their ids*/
	                File originalFile=new File(System.getProperty("java.io.tmpdir"),originaltemp);
	                 
	        		String original = MongoDB.getGridFSDAO().saveFile(new FileInputStream(originalFile), json.findValue("originalName").asText());
		            
	        		/* create the required transformations */
	        		
	        		
	        		ImageHelper ihelper=new ImageHelper(originalFile);
	        	    
	        		
	        		/*resize to coverimage*/
	        		BufferedImage coverIm=ihelper.getScaledWidth(350);
	        		coverIm=ImageHelper.cropToHeight(coverIm, 420);
	        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        		ImageIO.write( coverIm, "png", baos );
	        		baos.flush();
	        		byte[] imageInByte = baos.toByteArray();
	        		baos.close();
	        		ByteArrayInputStream bis=new ByteArrayInputStream(imageInByte);
	        		coverImage=MongoDB.getGridFSDAO().saveFile(bis, json.findValue("originalName").asText());
	        			        		
	        		/*resize to preview, no dimensions given so use the cover for now*/
	        		
	        		
	        		/* map to cube*/
	        		
	        		BufferedImage cubeIm=ImageHelper.shadedHexagon(ihelper.getOriginal());
	        		baos = new ByteArrayOutputStream();
	        		ImageIO.write( cubeIm, "png", baos );
	        		baos.flush();
	        		imageInByte = baos.toByteArray();
	        		baos.close();
	        		bis=new ByteArrayInputStream(imageInByte);
	        		cubeImage=MongoDB.getGridFSDAO().saveFile(bis, json.findValue("originalName").asText());
	        		
	        		
	        		/*resize to thumbnail*/
	        		BufferedImage thumbIm=ihelper.getScaledWidth(65);
	        		baos = new ByteArrayOutputStream();
	        		ImageIO.write( thumbIm, "png", baos );
	        		baos.flush();
	        		imageInByte = baos.toByteArray();
	        		baos.close();
	        		bis=new ByteArrayInputStream(imageInByte);
	        		thumbImage=MongoDB.getGridFSDAO().saveFile(bis, json.findValue("originalName").asText());
	        		
	        		
	        		storyimagejson.remove("original");
	        		storyimagejson.put("original",original);
	        		
	        		storyimagejson.put("coverImage",coverImage);
	        		storyimagejson.put("objectPreview",coverImage);
	        		storyimagejson.put("objectThumbnail",cubeImage);
	        		
	        		storyimagejson.put("thumbnail",thumbImage);
	        		storyimagejson.put("userid", session().get("uid"));
	        		
	        		si = mapper.readValue(storyimagejson, StoryImage.class);
	        		MongoDB.getStoryImageDAO().save(si);
				    
	        		//now save the story object and return that
	        	
	        		so.setCreator(session().get("uid"));
	        		so.setTitle(si.getTitle());
	        		so.setDescription(si.getDescription());
	        		so.setSosource("local");
	        		so.setThumbnail(si.getObjectThumbnail());
	        		
	        		so.setLanguage("en");
	        		so.setSource("localhost");
	        		so.setUrl(coverImage);
	        		so.setType("image");
	        		so.setLicense(null);
	        		so.setStoryImage(si.getId());
	        		MongoDB.getStoryObjectDAO().save(so);
	        		return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(so)));
					
			//return ok(user.toString());
		}
	   }
	    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  /*delete all images saved in gridfs
	  	   *
	  	   */
	  	  try{
	  	  if(coverImage!=null){
	  		  MongoDB.getGridFS().remove(coverImage);
	  	  }
	  	  if(thumbImage!=null){
	  		  MongoDB.getGridFS().remove(thumbImage);
	  	  }
	  	  if(cubeImage!=null){
	  		  MongoDB.getGridFS().remove(cubeImage);
	  	  }
	  	  if(si.getId()!=null){
	  		  MongoDB.getStoryImageDAO().delete(si);
	  	  }
	  	  if(so.getId()!=null){
	  		  MongoDB.getStoryObjectDAO().delete(so);
	  	  }
	  	  }catch(Exception e){}
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }
	}
	
	
	
	public static Result get(String id){
		try{
			
			StoryImage si=MongoDB.getStoryImageDAO().get(new ObjectId(id));
			if(si==null){
				return notFound();
			}
			response().setContentType("application/json");
			return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(si)));
		   }
		catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  response().setContentType("application/json");
	  	  return internalServerError(Messages.get("apperror"));
	    }

	}
	

	@Dynamic(value = "minimumLevelRequired", meta="contributor")
	public static Result delete(){
		try{
			
				if(request().queryString().size() == 0) {
					return badRequest();
				}else if(request().queryString().get("id") == null){
					return badRequest();
				}else {
					String id = request().queryString().get("id")[0];
					/*only allowed if no stories have been added to this theme*/
					
					
					MongoDB.getStoryImageDAO().remove(id); 
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
		
}
