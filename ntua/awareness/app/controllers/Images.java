package controllers;



import gr.ntua.ivml.awareness.util.MongoDB;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


import org.bson.types.ObjectId;

import com.mongodb.gridfs.GridFSDBFile;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class Images extends Controller {

	public static Result save(){
		try{ 
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");
			 String id = null; 
			if(picture != null){
				File file = picture.getFile();
				id = MongoDB.getGridFSDAO().saveFile(new FileInputStream(file), picture.getFilename());
	
			 }else{
				 return badRequest();
			 }
			
			  return ok(id);
		}catch(Exception e){
			return badRequest();
		}
	}
	
	 public static DateFormat htmlExpiresDateFormat() {
         DateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
         httpDateFormat.setTimeZone(TimeZone.getDefault());
         return httpDateFormat;
     }
	
	public static Result get(String id){
		try{ 
			GridFSDBFile pictureFile=MongoDB.getGridFS().findOne(new ObjectId(id));
			response().setContentType("image/png");
			int seconds=172801;
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.SECOND, seconds);
			//response().setHeader(Http.HeaderNames.CACHE_CONTROL, "PUBLIC, max-age=" + seconds + ", must-revalidate");
			response().setHeader(Http.HeaderNames.CACHE_CONTROL, "PUBLIC, max-age=" + seconds);
			
			response().setHeader(Http.HeaderNames.EXPIRES, htmlExpiresDateFormat().format(cal.getTime()));
			response().setHeader(Http.HeaderNames.LAST_MODIFIED, htmlExpiresDateFormat().format(pictureFile.getUploadDate()));
			return ok(pictureFile.getInputStream());
	
		}catch(Exception e){
			flash("error", "Missing image file");
			return internalServerError(Messages.get("imagenotfound"));
		}
	}
}
