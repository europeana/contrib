package controllers;

import gr.ntua.ivml.awareness.persistent.DigitalStory;
import gr.ntua.ivml.awareness.persistent.StoryObjectPlaceHolder;

import gr.ntua.ivml.awareness.util.MongoDB;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;



public class DigitalStories extends Controller {

	
	public static Result save(){
		response().setContentType("application/json");
     try{
		ObjectMapper mapper = new ObjectMapper(  );
		JsonNode json = request().body().asJson();
		if(json == null) {
		    return badRequest("Expecting Json data");
		}
		else {
		 	String bodyValue = mapper.writerWithDefaultPrettyPrinter(  ).writeValueAsString(json);
			BasicDBObject toSave = (BasicDBObject) com.mongodb.util.JSON.parse(bodyValue);
			DigitalStory story = MongoDB.getMorphia().fromDBObject(DigitalStory.class, toSave);
			story.updateCoverImage();
			
			MongoDB.getDigitalStoryDAO().save(story);
			if(story.getUrlImage()!=null){
				story.coverImage=story.getUrlImage();
			}
			return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(story)));
		}
	   }
	    catch (Exception ex){
	  	  ex.printStackTrace();
	  	  
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
		DigitalStory story = MongoDB.getDigitalStoryDAO().get(assetId);
		if(story == null){
			return notFound();
		}
		if(story.coverImage!=null){
		story.coverImage=story.getUrlImage();}
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
					MongoDB.getDigitalStoryDAO().deleteById(assetId);
					
					return ok();
				}
		}catch (Exception ex){
			
			return internalServerError(Messages.get("apperror"));
		}
	}
	
	public static Result getObjectsByStoryID(String id){
		response().setContentType("application/json");
		String res = "{}";

				DigitalStory story = MongoDB.getDigitalStoryDAO().get(new ObjectId(id));
				if(story == null){
					return badRequest();
				}else{
					ArrayList<StoryObjectPlaceHolder> tmp = (ArrayList<StoryObjectPlaceHolder>) story.getStoryObjects();
					ArrayList<DBObject> storyObj = MongoDB.getStoryObjectDAO().getDBObjectsByPlaceHolders(tmp);
					BasicDBObject obj = new BasicDBObject();
					obj.put("storyObjects", storyObj);
					res = com.mongodb.util.JSON.serialize(obj);
				}
		return ok(res);
	}
	
	public static Result getComments(String id){
		response().setContentType("application/json");
		try{
			String res = "{}";
			if(request().queryString().get("to") == null && request().queryString().get("from") == null){
				ArrayList<DBObject> ids = MongoDB.getScDAO().getStoryComments(0, 100, id);
				long total = MongoDB.getScDAO().getCommentsTotalNumber(id);
				BasicDBObject obj = new BasicDBObject();
				obj.put("totalSize", total);
				obj.put("start", 0);
				obj.put("to", ids.size());
				obj.put("values", ids);
				res = com.mongodb.util.JSON.serialize(obj);
			}else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
					int to = Integer.parseInt(request().queryString().get("to")[0]);
					ArrayList<DBObject> ids = MongoDB.getScDAO().getStoryComments(0, to, id);
					long total = MongoDB.getScDAO().getCommentsTotalNumber(id);
					BasicDBObject obj = new BasicDBObject();
					obj.put("totalSize", total);
					obj.put("start", 0);
					obj.put("to", ids.size());
					obj.put("values", ids);
					res = com.mongodb.util.JSON.serialize(obj);
				
			}else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
					int to = Integer.parseInt(request().queryString().get("to")[0]);
					int from = Integer.parseInt(request().queryString().get("from")[0]);
					ArrayList<DBObject> ids = MongoDB.getScDAO().getStoryComments(from, to, id);
					long total = MongoDB.getScDAO().getCommentsTotalNumber(id);
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
	
	
	public static Result list(){
		response().setContentType("application/json");
	try{
		String res = "{}";
		if(request().queryString().get("to") == null && request().queryString().get("from") == null){
			ArrayList<String> ids = MongoDB.getDigitalStoryDAO().getDigitalStories(0, 100);
			long total = MongoDB.getDigitalStoryDAO().count();
			BasicDBObject obj = new BasicDBObject();
			obj.put("totalSize", total);
			obj.put("start", 0);
			obj.put("to", ids.size());
			obj.put("values", ids);
			res = com.mongodb.util.JSON.serialize(obj);
		}else if(request().queryString().get("to") != null && request().queryString().get("from") == null){
			
				int to = Integer.parseInt(request().queryString().get("to")[0]);
				ArrayList<String> ids = MongoDB.getDigitalStoryDAO().getDigitalStories(0, to);
				long total = MongoDB.getDigitalStoryDAO().count();
				BasicDBObject obj = new BasicDBObject();
				obj.put("totalSize", total);
				obj.put("start", 0);
				obj.put("to", ids.size());
				obj.put("values", ids);
				res = com.mongodb.util.JSON.serialize(obj);
			
		}else if(request().queryString().get("to") != null || request().queryString().get("from") != null){
				int to = Integer.parseInt(request().queryString().get("to")[0]);
				int from = Integer.parseInt(request().queryString().get("from")[0]);
				ArrayList<String> ids = MongoDB.getDigitalStoryDAO().getDigitalStories(from, to);
				long total = MongoDB.getDigitalStoryDAO().count();
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
	
}
