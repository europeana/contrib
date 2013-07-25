package at.researchstudio.dme.annotation.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.researchstudio.dme.annotation.Data;
import at.researchstudio.dme.annotation.Setup;
import at.researchstudio.dme.annotation.config.Config;

/**
 * Tests for the RdfAnnotationController
 *  
 * @author Christian Sadilek
 */
public class RdfAnnotationControllerTest {
	private static final String RDF_ANNOTATION_CONTROLLER_BASE_URL = 
		"http://localhost:8081/annotation-middleware/annotations/";

	private static final String ACCEPT_HEADER = "Accept";
	private static final String LOCATION_HEADER = "Location";
	private static final String CONTENT_TYPE_RDFXML = "application/rdf+xml";	
	private static final String ENCODING = "UTF-8";

    @BeforeClass
	public static void setUp() throws Exception {
        Setup.buildSesameNativeConfiguration();
        Setup.startEmbeddedJaxrsServer(RdfAnnotationController.class);
	}

	@AfterClass
	public static void tearDown() throws Exception {	
		Config.getInstance().getAnnotationDatabase().shutdown();			
	}
	
	@Test
	public void testCreateUpdateDeleteAnnotation() throws Exception {
		HttpClient httpClient = new HttpClient();
		
		PostMethod createMethod = new PostMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL);		
		createMethod.setRequestEntity(new StringRequestEntity(Data.LEMO_ANNOTATION, 
				CONTENT_TYPE_RDFXML, ENCODING));						
		assertEquals(httpClient.executeMethod(createMethod), HttpStatus.SC_CREATED);
		Header location = createMethod.getResponseHeader(LOCATION_HEADER);						
		String createdAnnotationUrl = location.getValue();
		assertNotNull(createdAnnotationUrl);
		
		PutMethod updateMethod = new PutMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL+
				"annotation/"+id(createdAnnotationUrl));
		updateMethod.setRequestEntity(new StringRequestEntity(Data.LEMO_ANNOTATION, 
				CONTENT_TYPE_RDFXML, ENCODING));						
		assertEquals(httpClient.executeMethod(updateMethod), HttpStatus.SC_OK);
		location = updateMethod.getResponseHeader(LOCATION_HEADER);						
		String updatedAnnotationUrl = location.getValue();
		assertNotNull(updatedAnnotationUrl);
	
		DeleteMethod deleteMethod = new DeleteMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL+
				"annotation/"+id(createdAnnotationUrl));
		deleteMethod.addRequestHeader(ACCEPT_HEADER, CONTENT_TYPE_RDFXML);
		assertEquals(httpClient.executeMethod(deleteMethod), HttpStatus.SC_NO_CONTENT);
		
		deleteMethod = new DeleteMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL+
				"annotation/"+id(updatedAnnotationUrl));
		deleteMethod.addRequestHeader(ACCEPT_HEADER, CONTENT_TYPE_RDFXML);
		assertEquals(httpClient.executeMethod(deleteMethod), HttpStatus.SC_NO_CONTENT);
	}
	
	@Test
	public void testCreateFindDeleteAnnotation() throws Exception {
		HttpClient httpClient = new HttpClient();
		
		PostMethod createMethod = new PostMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL);		
		createMethod.setRequestEntity(new StringRequestEntity(Data.LEMO_ANNOTATION, 
				CONTENT_TYPE_RDFXML, ENCODING));						
		assertEquals(httpClient.executeMethod(createMethod), HttpStatus.SC_CREATED);
		Header location = createMethod.getResponseHeader(LOCATION_HEADER);						
		String createdAnnotationUrl = location.getValue();
		assertNotNull(createdAnnotationUrl);
		
		GetMethod findByIdMethod = new GetMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL+
				"annotation/"+id(createdAnnotationUrl));
		findByIdMethod.addRequestHeader(ACCEPT_HEADER, CONTENT_TYPE_RDFXML);
		assertEquals(httpClient.executeMethod(findByIdMethod), HttpStatus.SC_OK);

		GetMethod findMethod = new GetMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL+"search");
		findMethod.setQueryString("q=text");
		findMethod.addRequestHeader(ACCEPT_HEADER, CONTENT_TYPE_RDFXML);
		assertEquals(httpClient.executeMethod(findMethod), HttpStatus.SC_OK);
		assertNotNull(findMethod.getResponseBodyAsString());
		assertFalse(findMethod.getResponseBodyAsString().isEmpty());
		
		GetMethod listMethod = new GetMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL+
				"http%253A%252F%252Fupload.wikimedia.org%252Fwikipedia%252Fcommons%252F7%252F77%252FLissabon.jpg");
		listMethod.addRequestHeader(ACCEPT_HEADER, CONTENT_TYPE_RDFXML);
		assertEquals(httpClient.executeMethod(listMethod), HttpStatus.SC_OK);
		
		DeleteMethod deleteMethod = new DeleteMethod(RDF_ANNOTATION_CONTROLLER_BASE_URL+
				"annotation/"+id(createdAnnotationUrl));
		deleteMethod.addRequestHeader(ACCEPT_HEADER, CONTENT_TYPE_RDFXML);
		assertEquals(httpClient.executeMethod(deleteMethod), HttpStatus.SC_NO_CONTENT);
	}
	private String id(String annotationUrl) {
		String[] splits=annotationUrl.split("/");
		return splits[splits.length-1];
	}
}
