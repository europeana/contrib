package gr.ntua.ivml.awareness.search;

import java.io.ByteArrayOutputStream;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class SearchServiceAccess {
	private HttpGet httpGet;
	private HttpClient httpClient;
	private HttpResponse httpRes;
	private HttpEntity httpEntity;
	
	public SearchServiceAccess(){
		httpClient = new DefaultHttpClient();
	}
	
	public BasicDBObject searchEuropeana(String originalTerm, String type, int startPage) throws Exception{
		String[] termsarray= originalTerm.split(" ");
		List terms=Arrays.asList(termsarray);
		URI uri = constructURI(terms,type, startPage-1);
		httpGet = new HttpGet(uri);
		BasicDBObject response = new BasicDBObject(); 
	
		httpRes = httpClient.execute(httpGet);
		httpEntity = httpRes.getEntity();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		httpEntity.writeTo(out);
		String resp = new String(out.toByteArray(), "UTF-8");
		BasicDBObject obj = (BasicDBObject) JSON.parse(resp);
		response.put("itemsCount", obj.getString("itemsCount"));
		response.put("totalResults", obj.getString("totalResults"));
		response.put("term", originalTerm);
		response.put("type", type);
		response.put("pageNumber", startPage);
		@SuppressWarnings("unchecked")
		ArrayList<Object> items = (ArrayList<Object>) obj.get("items");
		if(items!=null){
		Iterator<Object> it = items.iterator();
		ArrayList<BasicDBObject> responseItems = new ArrayList<BasicDBObject>();
		while(it.hasNext()){
			BasicDBObject tmp = (BasicDBObject) it.next();
			BasicDBObject re = new BasicDBObject();
			String recid=tmp.getString("id");
				
			re.put("europeanaid",recid);
			re.put("type",tmp.getString("type"));
			re.put("source", tmp.getString("guid"));
			BasicDBList titleslist=(BasicDBList)tmp.get("title");
			if(titleslist!=null)
			re.put("title", StringUtils.join(titleslist, ','));
			
			BasicDBList providerslist=(BasicDBList)tmp.get("dataProvider");
			if(providerslist!=null)
			re.put("dataProvider", providerslist.get(0));
			providerslist=(BasicDBList)tmp.get("provider");
			if(providerslist!=null)
			re.put("provider", providerslist.get(0));
            
			BasicDBList creatorlist=(BasicDBList)tmp.get("dcCreator");
			if(creatorlist!=null)
			re.put("dcCreator", creatorlist.get(0));
			
			
            BasicDBList languagelist=(BasicDBList)tmp.get("language");
			if(languagelist!=null)
			re.put("language", languagelist.get(0));
			
			BasicDBList thumbslist=(BasicDBList)tmp.get("edmPreview");
			if(thumbslist!=null)
			re.put("url", thumbslist.get(0));
			
			BasicDBList rightslist=(BasicDBList)tmp.get("rights");
			if(rightslist!=null)
				re.put("license", rightslist.get(0));
			
			
			responseItems.add(re);
			
		}
		response.put("items", responseItems);
		}
	    return response;
    }
	
	private URI constructURI(List<String> terms,String type, int startPage){
		URI uri = null;
		String delim = "+";
		Iterator<String> it = terms.iterator();
		String q = "";
		if(it.hasNext()){
			q = "\""+it.next()+"\"";
		}
		while(it.hasNext()){
			q += delim + "\""+it.next()+"\"";
		}
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("query",q));
		if(type!=null && type.length()>0){
			qparams.add(new BasicNameValuePair("qf","TYPE:"+type.toUpperCase()));
		}
		qparams.add(new BasicNameValuePair("profile","standard"));
		
		if(startPage==0)
			qparams.add(new BasicNameValuePair("start","1"));
		else	
		qparams.add(new BasicNameValuePair("start",Integer.toString(startPage*6)));
		qparams.add(new BasicNameValuePair("rows","6"));
		qparams.add(new BasicNameValuePair("wskey", "api2demo"));
		try {
			uri = URIUtils.createURI("http", "preview.europeana.eu", 80, "/api/v2/search.json", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	private URI constructURIRecord(String recid){
		URI uri = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("wskey", "api2demo"));
		try {
			uri = URIUtils.createURI("http", "preview.europeana.eu", 80, "/api/v2/record"+recid+".json", 
					URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	public BasicDBObject searchEuropeanaRecord(String recid) throws Exception{
		URI uri = constructURIRecord(recid);
		httpGet = new HttpGet(uri);
		 
	
		httpRes = httpClient.execute(httpGet);
		httpEntity = httpRes.getEntity();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		httpEntity.writeTo(out);
		String resprec = new String(out.toByteArray(), "UTF-8");
		BasicDBObject objrecord = (BasicDBObject) JSON.parse(resprec);
	
		BasicDBObject record = (BasicDBObject)objrecord.get("object");
		BasicDBObject re = new BasicDBObject();
		
		
		
		ArrayList<Object> items = (ArrayList<Object>) record.get("aggregations");
		Iterator<Object> it = items.iterator();
		while(it.hasNext()){
			BasicDBObject tmp = (BasicDBObject) it.next();
				Object edmObject= tmp.get("edmObject");
			if(edmObject!=null)
			re.put("europeana_image", edmObject.toString());
			
			
		}
		
		return re;
	
	}
}


