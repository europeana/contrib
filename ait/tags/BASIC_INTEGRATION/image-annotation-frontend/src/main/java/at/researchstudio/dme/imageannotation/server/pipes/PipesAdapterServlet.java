package at.researchstudio.dme.imageannotation.server.pipes;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import at.researchstudio.dme.annotation.client.AnnotationService;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.server.annotation.builder.RdfXmlAnnotationBuilder;
import at.researchstudio.dme.imageannotation.server.util.W3CDateTimeParser;

public class PipesAdapterServlet extends HttpServlet {
	private static final long serialVersionUID = -136699636736249926L;
	private static Logger logger = Logger.getLogger(PipesAdapterServlet.class);
	
	private static String JSON_TEST = 
		"{\"items\":[" +
			 "{\"scope:scope\":\"PUBLIC\"," +
			  "\"rdf:type\":[{\"rdf:resource\":\"http://lemo.mminf.univie.ac.at/annotation-core#Annotation\"}," +
			  				"{\"rdf:resource\":\"http://www.w3.org/2000/10/annotationType#Comment\"}]," +
			  "\"rel:isLinkedTo\":\"http://www.james.rcpt.to/wedding/img/AUS_Perth_Milner_Swan_River.jpg\"," +
			  "\"a:author\":\"csa1980\"," +
			  "\"ann:body\":{\"rdf:Description\":{\"rdf:about\":\"http://dme.arcs.ac.at/annotation-middleware/Annotation/body/65d263412333b4b7491a0ce4c3a4\"," +
			  "				\"h:ContentType\":\"text/html\"," +
			  "				\"h:ContentLength\":\"92\"," +
			  "				\"h:Body\":{\"rdf:parseType\":\"Literal\",\"html\":{\"body\":\"perth+in+australia+near+fremantle\",\"head\":{\"title\":\"perth\"}}}}}," +
			  "\"a:label\":\"perth+in+australia+near+fremantle\"," +
			  "\"a:created\":\"2009-08-19T19:29:24+02:00\"," +
			  "\"dc:title\":{\"content\":\"perth\",\"xmlns:dc\":\"http://purl.org/dc/elements/1.1/\"}," +
			  "\"dc:date\":{\"content\":\"2009-08-19T19:29:24+02:00\",\"xmlns:dc\":\"http://purl.org/dc/elements/1.1/\"}," +
			  "\"a:annotates\":{\"rdf:resource\":\"http://www.james.rcpt.to/wedding/img/AUS_Perth_Milner_Swan_River.jpg\"}," +
			  "\"dc:creator\":{\"content\":\"csa1980\",\"xmlns:dc\":\"http://purl.org/dc/elements/1.1/\"}," +
			  "\"dc:format\":{\"content\":\"image/jpeg\",\"xmlns:dc\":\"http://purl.org/dc/elements/1.1/\"}," +
			  "\"rdf:about\":\"http://dme.arcs.ac.at/annotation-middleware/Annotation/65d263412333b4b7491a0ce4c3a4\"," +
			  "\"a:modified\":\"2009-08-19T19:29:24+02:00\"," +
			  "\"links\":[{\"categorys\":\"PLACE\",\"types\":\"shortcuts:/us/instance/place/au/suburb\"," +
			  				"\"predictedCategory\":\"PLACE\"," +
			  				"\"y:location\":{\"country\":\"Australia\"," +
			  								"\"city\":\"Fremantle\"," +
			  								"\"lat\":\"-32.052872\"," +
			  								"\"lon\":\"115.74824\"," +
			  								"\"quality\":\"50\"," +
			  								"\"state\":\"Western+Australia\"}," +
			  				"\"seeAlso\":[{\"content\":\"http://sws.geonames.org/2071223/\"}]," +
			  				"\"predictionProbability\":\"0.812236\"," +
			  				"\"relevanceScore\":\"87.0294\"," +
			  				"\"metaSet\":{\"value\":{\"visible\":\"true\",\"geoPlaceType\":\"Suburb\",\"geoTown\":\"Fremantle\"," +
			  							"\"geoState\":\"Western+Australia\",\"geoArea\":\"5.51542\",\"geoCountry\":\"Australia\"," +
			  							"\"geoStateCode\":\"WA\",\"geoZip\":\"6160\",\"geoIsoCountryCode\":\"AU\"," +
			  							"\"geoName\":\"Fremantle\",\"geoLocation\":\"(115.74824,+-32.052872)\"," +
			  							"\"type\":\"shortcuts:/us/instance/place/au/suburb\"}}," +
			  							"\"text\":{\"startchar\":\"24\",\"content\":\"fremantle\",\"endchar\":\"32\"," +
			  							"\"extended\":\"0\",\"end\":\"32\",\"start\":\"24\"},\"weight\":\"0.300073\"," +
			  							"\"context\":\"perth+in+australia+near+fremantle\",\"title\":\"fremantle\"}]}]}";
	
	private static String JSON_TEST_2 = 
		"{\"items\":[" +
			"{\"scope:scope\":\"PUBLIC\"," +
			"\"rdf:type\":[{\"rdf:resource\":\"http://www.w3.org/2000/10/annotationType#Comment\"}," +
						  "{\"rdf:resource\":\"http://lemo.mminf.univie.ac.at/annotation-core#Annotation\"}]," +
			"\"a:author\":\"test\"," +
			"\"a:label\":\"Perth was founded on 12 June 1829 by Captain James Stirling as the political centre of the free settler Swan River Colony. It has continued to serve as the seat of Government for Western Australia to the present day. Its port, Fremantle is a city in its own right and slightly older than Perth. \"," +
			"\"a:created\":\"2009-08-21T14:57:17+02:00\"," +
			"\"dc:title\":\"perth\"," +
			"\"ann:body\":{\"rdf:Description\":{\"rdf:about\":\"http://dme.arcs.ac.at/annotation-middleware/Annotation/body/14cugdof7x1\"," +
			"			    \"h:ContentType\":\"text/html\"," +
			"				\"h:ContentLength\":\"985\"," +
			"				\"h:Body\":{\"rdf:parseType\":\"Literal\",\"html\":{\"body\":\"Perth was founded on 12 June 1829 by Captain James Stirling as the political centre of the free settler Swan River Colony. It has continued to serve as the seat of Government for Western Australia to the present day. Its port, Fremantle is a city in its own right and slightly older than Perth.\",\"head\":{\"title\":\"perth\"}}}}}," +
			"\"dc:date\":\"2009-08-21T14:57:17+02:00\"," +
			"\"a:annotates\":{\"rdf:resource\":\"http://upload.wikimedia.org/wikipedia/commons/3/3f/Perth_Montage.png\"}," +
			"\"dc:creator\":\"test\"," +
			"\"dc:format\":\"image/png\"," +
			"\"a:modified\":\"2009-08-21T14:57:17+02:00\"," +
			"\"rdf:about\":\"http://dme.arcs.ac.at/annotation-middleware/Annotation/14cugdof7x1\"," +
			"\"links\":[{\"categorys\":\"PLACE\",\"types\":[\"shortcuts:/us/instance/place/au/suburb\",\"shortcuts:/us/instance/place/destination\"]," +
				"\"predictedCategory\":\"PLACE\",\"y:location\":{\"country\":\"Australia\",\"city\":\"Fremantle\",\"lat\":\"-32.052872\",\"lon\":\"115.74824\"," +
				"\"quality\":\"50\",\"state\":\"Western Australia\"},\"seeAlso\":[{\"content\":\"http://sws.geonames.org/2071223/\"}]," +
				"\"predictionProbability\":\"0.660319\",\"relevanceScore\":\"15.0346\",\"metaSet\":{\"value\":{\"visible\":\"true\"," +
				"\"geoPlaceType\":\"Suburb\",\"geoTown\":\"Fremantle\",\"geoState\":\"Western Australia\",\"geoArea\":\"5.51542\"," +
				"\"geoCountry\":\"Australia\",\"geoStateCode\":\"WA\",\"geoZip\":\"6160\",\"geoIsoCountryCode\":\"AU\"," +
				"\"geoName\":\"Fremantle\",\"geoLocation\":\"(115.74824, -32.052872)\",\"type\":\"shortcuts:/us/instance/place/au/suburb\"}}," +
				"\"text\":{\"startchar\":\"227\",\"content\":\"Fremantle\",\"endchar\":\"235\",\"extended\":\"0\",\"end\":\"235\"," +
				"\"start\":\"227\"},\"weight\":\"0.466403\"," +
				"\"context\":\"Government for Western Australia to the present day Its port Fremantle is a city in its own right and slightly older\"," +
				"\"title\":\"Fremantle\"}]}]}";

	private static String JSON_TEST_3 = "{\"items\":[{\"rel:isLinkedTo\":\"http://upload.wikimedia.org/wikipedia/commons/3/3f/Perth_Montage.png\",\"scope:scope\":\"PUBLIC\",\"rdf:type\":[{\"rdf:resource\":\"http://www.w3.org/2000/10/annotationType#Comment\"},{\"rdf:resource\":\"http://lemo.mminf.univie.ac.at/annotation-core#Annotation\"}],\"a:author\":\"test\",\"a:label\":\"fremantle in australia 1\",\"a:created\":\"2009-08-21T15:54:54+02:00\",\"dc:title\":\"perth\",\"ann:body\":{\"rdf:Description\":{\"rdf:about\":\"http://dme.arcs.ac.at/annotation-middleware/Annotation/body/14cugdof7x7\",\"h:ContentType\":\"text/html\",\"h:ContentLength\":\"83\",\"h:Body\":{\"rdf:parseType\":\"Literal\",\"html\":{\"body\":\"fremantle in australia 1\",\"head\":{\"title\":\"perth\"}}}}},\"dc:date\":\"2009-08-21T15:54:58+02:00\",\"a:annotates\":{\"rdf:resource\":\"http://upload.wikimedia.org/wikipedia/commons/3/3f/Perth_Montage.png\"},\"dc:creator\":\"test\",\"dc:format\":\"image/png\",\"a:modified\":\"2009-08-21T15:54:58+02:00\",\"rdf:about\":\"http://dme.arcs.ac.at/annotation-middleware/Annotation/14cugdof7x7\",\"links\":[]}]}";
	
	public static void main(String args[]) {
		createAnnotations(JSON_TEST_3);
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);

	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String data=(String)request.getParameter("data");
			
		try {
			logger.info(data);
			for(ImageAnnotation annotation : createAnnotations(data)) {
				logger.info(annotation.toString());
				ClientResponse<String> resp = getAnnotationService().
					updateAnnotation(URLEncoder.encode(annotation.getId(),"UTF-8"),
						RdfXmlAnnotationBuilder.toRdfXml(annotation));
				logger.info("response code:"+resp.getStatus());								
			}
		} catch (Throwable t) {
			logger.error(t);
		}		
	}
	
	private AnnotationService getAnnotationService() {				
		return ProxyFactory.create(AnnotationService.class, "http://dme.arcs.ac.at");		
	}
	
	private static ArrayList<ImageAnnotation> createAnnotations(String jsonBody) {
		ArrayList<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();
		JSONObject json=(JSONObject)JSONValue.parse(jsonBody);
		JSONArray items = (JSONArray)json.get("items");
		
		for(Object obj : items) {
			JSONObject jsonObj = (JSONObject)obj;
			ImageAnnotation annotation = new ImageAnnotation();
			
			String id = (String)jsonObj.get("rdf:about");
			if(id!=null) annotation.setId(id);
			
			String scope = (String)jsonObj.get("scope:scope");
			if(scope!=null) annotation.setScopeFromString(scope);
			
			String objectId = (String)((JSONObject)jsonObj.get("a:annotates")).get("rdf:resource");
			if(objectId!=null) annotation.setObjectId(objectId);

			String externalObjectId = (String)jsonObj.get("rel:isLinkedTo");
			if(externalObjectId!=null) annotation.setExternalObjectId(externalObjectId);
			
			String parentId = (String)jsonObj.get("tr:inReplyTo");
			if(parentId!=null) annotation.setParentId(parentId);
		
			String rootId = (String)jsonObj.get("tr:root");
			if(rootId!=null) annotation.setRootId(rootId);
		
			String title = (String)jsonObj.get("dc:title");
			if(title!=null) annotation.setTitle(title);
			
			String text = (String)jsonObj.get("a:label");
			if(text!=null) annotation.setText(text);
			
			String format = (String)jsonObj.get("dc:format");
			if(format!=null) annotation.setMimeType(format);
		
			String createdBy = (String)jsonObj.get("a:author");
			if(createdBy!=null) annotation.setCreatedBy(createdBy);			

			String created = (String)jsonObj.get("a:created");
			if(created!=null) annotation.setCreated(W3CDateTimeParser.parseW3CDateTime(created));
			
			JSONArray links = (JSONArray)jsonObj.get("links");
			if(links!=null) {
				for(Object link : links) {
					JSONArray seeAlsos = (JSONArray)((JSONObject)link).get("seeAlso");
					for(Object seeAlso : seeAlsos) {
						String seeAlsoUrl=(String)((JSONObject)seeAlso).get("content");
						annotation.addLink(seeAlsoUrl);
					}
				}
			}
			
			annotation.setModified(new Date());
			annotations.add(annotation);			
		}
		return annotations;
	}

	
}
