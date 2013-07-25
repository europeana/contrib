package controllers;


import gr.ntua.ivml.awareness.util.ImageHelper;
import gr.ntua.ivml.awareness.util.MongoDB;

import java.awt.image.BufferedImage;
import java.io.File;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.bson.types.ObjectId;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import be.objectify.deadbolt.actions.Dynamic;


import com.mongodb.gridfs.GridFSDBFile;

import play.libs.Json;
import play.mvc.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.RawBuffer;
import play.mvc.Http.RequestBody;

/*author Anna
ajax file upload , copy to temp location*/

public class FileReader extends Controller {

	
	public static Result save(){
		response().setContentType("application/json");
		if(request().getHeader(HttpHeaders.USER_AGENT).indexOf("MSIE")>-1){
        	response().setContentType("text/html");
        }
	   
	    FileOutputStream fos = null;
	    FileInputStream fin=null;
		try{ 
			File uploadedFile=null;
			if(request().getHeader(HttpHeaders.USER_AGENT).indexOf("MSIE")>-1){
				
				 MultipartFormData body = request().body().asMultipartFormData();
				 FilePart picture = body.getFile("qqfile");
				  if (picture != null) {
					  uploadedFile = picture.getFile();
				  }
			}
			else{
				RawBuffer rw=request().body().asRaw();
				uploadedFile = rw.asFile();
			    
			}
			    
			    File newTmpFile = File.createTempFile("Awareness", "cpy.png");
	            String fname=newTmpFile.getName();
	            fos = new FileOutputStream( newTmpFile );
	            fin=new FileInputStream(uploadedFile);
	            IOUtils.copy(fin, fos);
	            ObjectNode jsono = Json.newObject();
	            jsono.put("success", true);
	            jsono.put("fname", fname);
	            return ok(jsono);
	            
			  
		}catch(Exception e){
			
			return badRequest("{success: false}");
		}
		finally {
            try {
            	if(fos!=null)
                fos.close();
            	if(fin!=null)
                    fin.close();
                	
            } catch (IOException ignored) {
            }
        }
	}
	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result uploadTheme(){
		response().setContentType("application/json");
		if(request().getHeader(HttpHeaders.USER_AGENT).indexOf("MSIE")>-1){
        	response().setContentType("text/plain");
        }
	    if(request().queryString().size() == 0) {
	    	return internalServerError("Missing file");
			
		}else if(request().queryString().get("id") == null){
			return internalServerError("Missing file");
		}else {
	        
		    FileOutputStream fos = null;
		    FileInputStream fin=null;
		   
		
		try{ 
			     
			GridFSDBFile pictureFile=MongoDB.getGridFS().findOne(new ObjectId(request().queryString().get("id")[0]));
			
		    
			if (pictureFile != null) {
				String fileName=pictureFile.getFilename();
				if(fileName==null || fileName.length()==0){
					fileName=request().queryString().get("id")[0];
				}
				  InputStream infile = pictureFile.getInputStream();
				  ArrayNode imarray=new ArrayNode(JsonNodeFactory.instance);  
			    
			    
			    
			    File newTmpFile = File.createTempFile(fileName, ".png");
	            String fname=newTmpFile.getName();
	            fos = new FileOutputStream( newTmpFile );
	            IOUtils.copy(infile, fos);
	            ObjectNode jsono = Json.newObject();
	            
	            jsono.put("name", fileName);
	            jsono.put("dspname", fname);
	            jsono.put("size", pictureFile.getLength());
	            jsono.put("url", "http://"+request().getHeader("Host")+"/awareness/FileReader/showupload?getfile=" + fname);
	            jsono.put("thumbnail_url", "http://"+request().getHeader("Host")+"/awareness/FileReader/showupload?getthumb=" + fname);
	            jsono.put("delete_url", "http://"+request().getHeader("Host")+"/awareness/FileReader/showupload?delfile=" + fname);
	            jsono.put("delete_type", "GET");
	            
                imarray.add(jsono);
                jsono=Json.newObject();
                jsono.put("files", imarray);
                
	            return ok(jsono);	
			    
			  }
			 
			else{
                ObjectNode jsono = Json.newObject();
	            
                
	            return internalServerError("Missing file");	}
			  
		}catch(Exception e){
		    
		    return internalServerError(e.getMessage());
            
            
		}
		finally {
            try {
            	if(fos!=null)
                fos.close();
            	if(fin!=null)
                    fin.close();
                	
            } catch (IOException ignored) {
            }
            
        }
		}
	}	

	
	@Dynamic(value = "minimumLevelRequired", meta="admin",content="json")
	public static Result upload(){
		response().setContentType("application/json");
		if(request().getHeader(HttpHeaders.USER_AGENT).indexOf("MSIE")>-1){
        	response().setContentType("text/plain");
        }
	    FileOutputStream fos = null;
	    FileInputStream fin=null;
		
		
		try{ 
			     
			  MultipartFormData body = request().body().asMultipartFormData();
			 FilePart picture = body.getFile("files[]");
			  if (picture != null) {
				ArrayNode imarray=new ArrayNode(JsonNodeFactory.instance);  
			    String fileName = picture.getFilename();
			    if(fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png") || fileName.toLowerCase().endsWith(".gif") )
			    {
			    	
			    
			    String contentType = picture.getContentType(); 
			    File file = picture.getFile();
			    File newTmpFile = File.createTempFile("Awareness", "cpy.png");
	            String fname=newTmpFile.getName();
	            fos = new FileOutputStream( newTmpFile );
	            fin=new FileInputStream(file);
	            IOUtils.copy(fin, fos);
	            ObjectNode jsono = Json.newObject();
	            
	            jsono.put("name", fileName);
	            jsono.put("dspname", fname);
	            jsono.put("size", newTmpFile.length());
	            jsono.put("url", "http://"+request().getHeader("Host")+"/awareness/FileReader/showupload?getfile=" + fname);
	            jsono.put("thumbnail_url", "http://"+request().getHeader("Host")+"/awareness/FileReader/showupload?getthumb=" + fname);
	            jsono.put("delete_url", "http://"+request().getHeader("Host")+"/awareness/FileReader/showupload?delfile=" + fname);
	            jsono.put("delete_type", "GET");
	            
                imarray.add(jsono);
                jsono=Json.newObject();
                jsono.put("files", imarray);
                String ua=request().getHeader(HttpHeaders.USER_AGENT);
                return ok(jsono);	
			   }
			    else {
			    	return internalServerError("Wrong file type. Only jpeg and png files accepted.");
			    	} 
			  }
			  return internalServerError("Missing file");
		    	
				  
			  
		}catch(Exception e){
		    return internalServerError("error", e.getMessage());
		}
		finally {
            try {
            	if(fos!=null)
                fos.close();
            	if(fin!=null)
                    fin.close();
                	
            } catch (IOException ignored) {
            }
            
        }
	}	
	
	public static Result fileOptions(){
	  try{	
		if (request().queryString().get("getfile")!= null
                && !request().queryString().get("getfile")[0].isEmpty()) {
			File file=new File(System.getProperty("java.io.tmpdir"),request().queryString().get("getfile")[0]);
            
            if (file.exists()) {
                int bytes = 0;
                   response().setHeader(CONTENT_LENGTH, (int)file.length()+"");
                
                response().setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );


                return ok(file); 
            }
        } else if (request().queryString().get("delfile") != null && !request().queryString().get("delfile")[0].isEmpty()) {
            
            File file=new File(System.getProperty("java.io.tmpdir"),request().queryString().get("delfile")[0]);
            if (file.exists()) {
                file.delete(); 
                return ok();
            }
        } else if (request().queryString().get("getthumb")!= null && !request().queryString().get("getthumb")[0].isEmpty()) {
        	File file=new File(System.getProperty("java.io.tmpdir"),request().queryString().get("getthumb")[0]);
            
                if (file.exists()) {
                    
                    
                    	
                    	ImageHelper ihelper=new ImageHelper(file);
    	        	    
    	        		
    	        		/*resize to coverimage*/
    	        		 BufferedImage im=ihelper.getScaledWidth(75);
    	        		 response().setHeader(CONTENT_LENGTH, (int)file.length()+"");
    	                 
    	                 response().setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );
    	                 
    	                 
    	                 ByteArrayOutputStream os = new ByteArrayOutputStream();
    	                 ImageIO.write(im, "png", os);
    	                 InputStream is = new ByteArrayInputStream(os.toByteArray());
    	                 
    	                 return ok(is); 
                    
            } else{
            	return internalServerError("file not found");
            }
        }
        	return badRequest("send multipart parameters");
        	
	
	
	}catch(Exception e){
		
		return badRequest("{success: false}");
	}
	
 }

}