package controllers;

import gr.ntua.ivml.awareness.persistent.DigitalStoryComment;
import gr.ntua.ivml.awareness.util.MongoDB;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;

public class DigitalStoriesComments extends Controller {
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
			DigitalStoryComment comment = MongoDB.getMorphia().fromDBObject(DigitalStoryComment.class, toSave);
			
			MongoDB.getScDAO().save(comment);
			
			return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(comment)));
		}
	   }
	    catch (Exception ex){
	  	  System.out.println(ex.getMessage());
	  	  
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
		DigitalStoryComment comment = MongoDB.getScDAO().get(assetId);
		if(comment == null){
			return notFound();
		}
		
		return ok(com.mongodb.util.JSON.serialize(MongoDB.getMorphia().toDBObject(comment)));
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
					MongoDB.getScDAO().deleteById(assetId);
					
					return ok();
				}
		}catch (Exception ex){
			
			return internalServerError(Messages.get("apperror"));
		}
	}
}
