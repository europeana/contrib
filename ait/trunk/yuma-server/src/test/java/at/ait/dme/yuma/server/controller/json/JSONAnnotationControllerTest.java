package at.ait.dme.yuma.server.controller.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.config.Config;

/**
 * Tests for the AnnoteaAnnotationController
 *  
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class JSONAnnotationControllerTest {
	private static final String JSON_ANNOTATION_CONTROLLER_BASE_URL = 
		"http://localhost:8080/yuma-server/api/annotation";

	private static final String ACCEPT_HEADER = "Accept";
	private static final String LOCATION_HEADER = "Location";
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String ENCODING = "UTF-8";
	
	@BeforeClass
	public static void setUp() throws Exception {
		Setup.buildConfiguration();
		Setup.startEmbeddedJaxrsServer(JSONAnnotationController.class);
	}

	@AfterClass
	public static void tearDown() throws Exception {	
		Config.getInstance().getAnnotationDatabase().shutdown();			
	}
		
	@Test
	public void testCreateUpdateDeleteAnnotation() throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
			
		// Create
		StringEntity createEntity = new StringEntity(Data.ANNOTATION_JSON_ORIGINAL, ENCODING);
		createEntity.setContentType(CONTENT_TYPE_JSON);
		HttpPost createMethod = new HttpPost(JSON_ANNOTATION_CONTROLLER_BASE_URL);		
		createMethod.setEntity(createEntity);
		
		HttpResponse response = httpClient.execute(createMethod);
		assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		Header location = response.getHeaders(LOCATION_HEADER)[0];						
		String createdAnnotationUrl = location.getValue();
		assertNotNull(createdAnnotationUrl);
		response.getEntity().consumeContent();
		
		// Read
		HttpGet findByIdMethod = new HttpGet(createdAnnotationUrl);
		findByIdMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		
		response = httpClient.execute(findByIdMethod);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
		
		// Update
		StringEntity putEntity = new StringEntity(Data.ANNOTATION_JSON_UPDATE, ENCODING);
		putEntity.setContentType(CONTENT_TYPE_JSON);
		HttpPut updateMethod = new HttpPut(createdAnnotationUrl);
		updateMethod.setEntity(putEntity);
		
		response = httpClient.execute(updateMethod);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		location = response.getHeaders(LOCATION_HEADER)[0];						
		String updatedAnnotationUrl = location.getValue();
		assertNotNull(updatedAnnotationUrl);
		response.getEntity().consumeContent();
		
		// Delete
		HttpDelete deleteMethod = new HttpDelete(updatedAnnotationUrl);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		
		response = httpClient.execute(deleteMethod);
		assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testReplyFunctionality() throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		
		// root #1
		StringEntity createEntity = new StringEntity(Data.ROOT_JSON, ENCODING);
		createEntity.setContentType(CONTENT_TYPE_JSON);
		HttpPost createMethod = new HttpPost(JSON_ANNOTATION_CONTROLLER_BASE_URL);		
		createMethod.setEntity(createEntity);		
		
		HttpResponse response = httpClient.execute(createMethod);
		assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		Header location = response.getHeaders(LOCATION_HEADER)[0];						
		String root1 = location.getValue();
		root1 = root1.substring(root1.lastIndexOf("/") + 1);
		response.getEntity().consumeContent();
		
		// root #2
		response = httpClient.execute(createMethod);
		response.getEntity().consumeContent();
		
		assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		location = response.getHeaders(LOCATION_HEADER)[0];						
		String root2 = location.getValue();
		root2 = root2.substring(root2.lastIndexOf("/") + 1);
		
		// reply #1
		StringEntity replyEntity = new  StringEntity(Data.reply(root1, root1), ENCODING);
		replyEntity.setContentType(CONTENT_TYPE_JSON);
		createMethod = new HttpPost(JSON_ANNOTATION_CONTROLLER_BASE_URL);		
		createMethod.setEntity(replyEntity);			
		
		response = httpClient.execute(createMethod);	
		assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		location = response.getHeaders(LOCATION_HEADER)[0];						
		String reply1 = location.getValue();
		assertNotNull(reply1);
		response.getEntity().consumeContent();	
		
		// Delete
		HttpDelete deleteMethod = new HttpDelete(reply1);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		response = httpClient.execute(deleteMethod);
		assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
		
		deleteMethod = new HttpDelete(JSON_ANNOTATION_CONTROLLER_BASE_URL + "/" + root1);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		response = httpClient.execute(deleteMethod);
		assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
		
		deleteMethod = new HttpDelete(JSON_ANNOTATION_CONTROLLER_BASE_URL + "/" + root2);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		response = httpClient.execute(deleteMethod);
		assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
	}
	
}
