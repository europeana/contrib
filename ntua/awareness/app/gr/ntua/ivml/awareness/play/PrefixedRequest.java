package gr.ntua.ivml.awareness.play;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.i18n.Lang;
import play.mvc.Http.Cookies;
import play.mvc.Http.Request;
import play.mvc.Http.RequestBody;



public class PrefixedRequest extends Request
{

	 private Request req;
	 
	 
	  public PrefixedRequest(Request request) {
	        super();
	        req=request;
	        
	    }
    
	public JsonNode getJson(){
		ObjectMapper mapper = new ObjectMapper( );	
		JsonNode json = null;
	   	 if(req.getHeader("Content-type")==null){
	   	   ByteArrayInputStream bais=new ByteArrayInputStream(req.body().asRaw().asBytes());
	   	   try {
			json=mapper.readTree(bais);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
	   	 else{   
	   	   json = req.body().asJson();
	   	 }
	   	 return json;
	}

	@Override
	public RequestBody body() {
		return req.body();
	}

	@Override
	public List<String> accept() {
		return req.accept();
	}

	@Override
	public List<Lang> acceptLanguages() {
		 return req.acceptLanguages();
	}

	@Override
	public boolean accepts(String arg0) {
		 return req.accepts(arg0);
	}

	@Override
	public Cookies cookies() {
		// TODO Auto-generated method stub
		return req.cookies();
	}

	@Override
	public Map<String, String[]> headers() {
		// TODO Auto-generated method stub
		return req.headers();
	}

	@Override
	public String host() {
      return req.host();
	}

	@Override
	public String method() {
		// TODO Auto-generated method stub
		return req.method();
	}

	@Override
	public String path() {
		// TODO Auto-generated method stub
		return req.path();
	}

	@Override
	public Map<String, String[]> queryString() {
		// TODO Auto-generated method stub
		return req.queryString();
	}

	@Override
	public String remoteAddress() {
		// TODO Auto-generated method stub
		return req.remoteAddress();
	}

	@Override
	public String uri() {
		// TODO Auto-generated method stub
		return req.uri();
	}
	
	 
}