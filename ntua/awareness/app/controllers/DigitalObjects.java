package controllers;

import gr.ntua.ivml.awareness.persistent.DigitalStory;
import gr.ntua.ivml.awareness.persistent.EuropeanaImage;
import gr.ntua.ivml.awareness.persistent.StoryImage;
import gr.ntua.ivml.awareness.persistent.StoryObject;
import gr.ntua.ivml.awareness.persistent.StoryObjectPlaceHolder;

import gr.ntua.ivml.awareness.search.SearchServiceAccess;
import gr.ntua.ivml.awareness.util.ImageHelper;
import gr.ntua.ivml.awareness.util.MongoDB;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import play.i18n.*;
import play.mvc.Controller;
import play.mvc.Result;


public class DigitalObjects  extends Controller {

	private Lang lang = (Lang) Lang.defaultLang();
	
	public static Result save(){
		try{
			response().setContentType("application/json");
			ObjectMapper mapper = new ObjectMapper(  );
			JsonNode json = request().body().asJson();
			if(json == null) {
			    return badRequest("Expecting Json data");
			}
			else {
			   String bodyValue = mapper.writerWithDefaultPrettyPrinter(  ).writeValueAsString(json);
				BasicDBObject toSave = (BasicDBObject) com.mongodb.util.JSON.parse(bodyValue);
				StoryObject story = MongoDB.getMorphia().fromDBObject(StoryObject.class, toSave);
				/*check that object doesn't already exist in user's lib */ 
				if(story.getCreator()==null)
					  story.setCreator(session().get("uid"));
					
				if (MongoDB.getStoryObjectDAO().checkUnique(story)==false){
					return badRequest("Item already in library");
				}
				
				BasicDBObject res;
				if(story.getSosource().equalsIgnoreCase("europeana")){
					SearchServiceAccess acc = new SearchServiceAccess();
					String europeanaid = json.findPath("europeanaid").getTextValue();
					String coverImage=null; 
					String thumbImage=null;
					
					//as test use String europeanaid="/91639/F697F1C055A62F2F45F143CC07D7CA93EF4A6409";
					try{ 
					if(europeanaid!=null){   
						
						URL europeana_imURL = new URL(story.url);
						
						if(europeana_imURL!=null){
						  BufferedImage buffImage = ImageIO.read(europeana_imURL);
						  if(buffImage.getWidth()>=200){
						
							  coverImage=story.url;
							  thumbImage=story.url;
							  EuropeanaImage ei=new EuropeanaImage();
				        		ei.setCoverImage(coverImage);
				        		ei.setThumbnail(thumbImage);
				        		MongoDB.getEuropenaImageDAO().save(ei);
				        		story.setStoryImage(ei.getId());
				        		
						  }
						  else{
							
						
						
							   res = acc.searchEuropeanaRecord(europeanaid);
							   if(res!=null){
								URL imageURL = new URL(res.get("europeana_image").toString());
								BufferedImage bufferedImage = ImageIO.read(imageURL);
								if(bufferedImage.getWidth()>=200){
									/*image bigger than 70% of final result, usable*/
									/*generate thumbnail and preview/cover image*/
									ImageHelper ihelper=new ImageHelper(bufferedImage);
									
									BufferedImage coverIm=ihelper.getScaledWidth(350);
									coverIm=ImageHelper.cropToHeight(coverIm, 420);
					        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
					        		ImageIO.write( coverIm, "png", baos );
					        		baos.flush();
					        		byte[] imageInByte = baos.toByteArray();
					        		baos.close();
					        		ByteArrayInputStream bis=new ByteArrayInputStream(imageInByte);
					        		coverImage=MongoDB.getGridFSDAO().saveFile(bis, json.findValue("europeanaid").asText()+"_preview");
					        	
					        		BufferedImage thumb=ihelper.getScaledWidth(65);
					        		baos = new ByteArrayOutputStream();
					        		ImageIO.write( thumb, "png", baos );
					        		baos.flush();
					        		imageInByte = baos.toByteArray();
					        		baos.close();
					        		bis=new ByteArrayInputStream(imageInByte);
					        		thumbImage=MongoDB.getGridFSDAO().saveFile(bis, json.findValue("europeanaid").asText()+"_thumbnail");
					        	
					        		EuropeanaImage ei=new EuropeanaImage();
					        		ei.setCoverImage(coverImage);
					        		ei.setThumbnail(thumbImage);
					        		MongoDB.getEuropenaImageDAO().save(ei);
					        		story.setStoryImage(ei.getId());	        	   	
									}
									
				 
								    }//if res!=null
						  }
						  
					  }
					}
					}
			        catch (Exception ex){
			        	System.out.println(ex.getMessage());
				  	}
					
				}
				MongoDB.getStoryObjectDAO().save(story);
				
				story.getThumbnail();
				story.getUrl();
				return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(story)));
			}
		   }
		    catch (Exception ex){
		  	  System.out.println(ex.getMessage());
		  	  response().setContentType("application/json");
		  	  return internalServerError(Messages.get("apperror"));
		    }
	}
	
	public static Result get(String id){
		response().setContentType("application/json");
		ObjectId assetId = null;
		try{
			assetId = new ObjectId(id);
		}catch(Exception e){
			return badRequest();
		}
		StoryObject story = MongoDB.getStoryObjectDAO().get(assetId);
		
		if(story == null){
			return notFound();
		}
		story.getThumbnail();
		story.getUrl();
		return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(story)));
	}
	
	public static Result delete(){
		
		response().setContentType("application/json");
		try{
			if(request().queryString().size() == 0) {
				return badRequest();
			}else if(request().queryString().get("id") == null){
				return badRequest();
			}else {
				String id = request().queryString().get("id")[0];
				ObjectId assetId = new ObjectId(id);
				StoryObject so=MongoDB.getStoryObjectDAO().get(assetId);
				if(so.getSosource().equalsIgnoreCase("local")){
					/* check if the user owns it, if yes then delete the associated story image if it exists and remove from all stories*/
					if(so.getStoryImage()!=null){
						StoryImage si=MongoDB.getStoryImageDAO().get(new ObjectId(so.getStoryImage()));
						if (si.userid.equalsIgnoreCase(session().get("uid"))){
							MongoDB.getStoryImageDAO().remove(si.getId());
							return ok();
						}
					}
				}
				MongoDB.getDigitalStoryDAO().deleteStoryObjectFromStories(id);
				MongoDB.getStoryObjectDAO().deleteById(assetId);
				return ok();
			}
		}catch (Exception ex){
			
			return internalServerError(Messages.get("apperror"));
		}
	}
	
	public static Result getObjectsByStoryID(){
		response().setContentType("application/json");
		String res = "{}";
		if(request().queryString().size() == 0 ||  request().queryString().size() > 1 ){
			return badRequest();
		}else{
			if(request().queryString().get("storyId") == null){
				return badRequest();
			}else{
				String storyId = request().queryString().get("storyId")[0];
				DigitalStory story = MongoDB.getDigitalStoryDAO().get(new ObjectId(storyId));
				if(story == null){
					return badRequest();
				}else{
					ArrayList<StoryObjectPlaceHolder> tmp = (ArrayList<StoryObjectPlaceHolder>) story.getStoryObjects();
					ArrayList<DBObject> storyObj = MongoDB.getStoryObjectDAO().getDBObjectsByPlaceHolders(tmp);
					BasicDBObject obj = new BasicDBObject();
					obj.put("storyObjects", storyObj);
					res = com.mongodb.util.JSON.serialize(obj);
				}
			}
		}
		return ok(res);
	}
	
	public static Result getObjectsByStoryID(String id){
		response().setContentType("application/json");
		String res = "{}";
		if(request().queryString().size() == 0 ||  request().queryString().size() > 1 ){
			return badRequest();
		}else{
			if(request().queryString().get("storyId") == null){
				return badRequest();
			}else{
				String storyId = request().queryString().get("storyId")[0];
				DigitalStory story = MongoDB.getDigitalStoryDAO().get(new ObjectId(storyId));
				if(story == null){
					return badRequest();
				}else{
					ArrayList<StoryObjectPlaceHolder> tmp = (ArrayList<StoryObjectPlaceHolder>) story.getStoryObjects();
					ArrayList<DBObject> storyObj = MongoDB.getStoryObjectDAO().getDBObjectsByPlaceHolders(tmp);
					BasicDBObject obj = new BasicDBObject();
					obj.put("storyObjects", storyObj);
					res = com.mongodb.util.JSON.serialize(obj);
				}
			}
		}
		return ok(res);
	}
	
	
	public static Result list(){
		response().setContentType("application/json");
	 try{
		String res = "{}";
		if(request().queryString().get("to") == null && request().queryString().get("from") == null){
			ArrayList<String> ids = MongoDB.getStoryObjectDAO().getStoryObjects(0, 100);
			long total = MongoDB.getStoryObjectDAO().count();
			BasicDBObject obj = new BasicDBObject();
			obj.put("totalSize", total);
			obj.put("start", 0);
			obj.put("to", ids.size());
			obj.put("values", ids);
			res = com.mongodb.util.JSON.serialize(obj);
		}else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
			
				int to = Integer.parseInt(request().queryString().get("to")[0]);
				ArrayList<String> ids = MongoDB.getStoryObjectDAO().getStoryObjects(0, to);
				long total = MongoDB.getStoryObjectDAO().count();
				BasicDBObject obj = new BasicDBObject();
				obj.put("totalSize", total);
				obj.put("start", 0);
				obj.put("to", ids.size());
				obj.put("values", ids);
				res = com.mongodb.util.JSON.serialize(obj);
			
		}else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
			
				int to = Integer.parseInt(request().queryString().get("to")[0]);
				int from = Integer.parseInt(request().queryString().get("from")[0]);
				ArrayList<String> ids = MongoDB.getStoryObjectDAO().getStoryObjects(from, to);
				long total = MongoDB.getStoryObjectDAO().count();
				BasicDBObject obj = new BasicDBObject();
				obj.put("totalSize", total);
				obj.put("start", from);
				obj.put("to", to);
				obj.put("values", ids);
				res = com.mongodb.util.JSON.serialize(obj);
			
		}
		else{
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
	
}
