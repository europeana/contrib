package org.theeuropeanlibrary.telplus.tpp.test;

import java.net.HttpURLConnection;

import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.theeuropeanlibrary.telplus.tpp.HTTPConnectionManager;
import org.theeuropeanlibrary.telplus.tpp.HTTPResponse;
import org.theeuropeanlibrary.telplus.tpp.TPPRequestException;
import org.theeuropeanlibrary.telplus.tpp.SynchronousRequestHandler;

public class SynchronousRequestHandlerTest {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private SynchronousRequestHandler handler;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public SynchronousRequestHandlerTest() {
		BasicConfigurator.configure();
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@BeforeSuite
	public void setUp() {
		handler = new SynchronousRequestHandler();
	}
	
	@AfterSuite
	public void tearDown() {
		handler = null;
	}
	
	@Test(groups="unit")
	public void testGetDocument() {
		String url = "http://www.kb.nl";
		try {
			HTTPResponse response = handler.getResponse(url, 
												HTTPConnectionManager.GET, null);
			int status = response.getStatus();
			byte[] body = response.getBody();
			Assert.assertTrue(status == HttpURLConnection.HTTP_OK);
			Assert.assertTrue(body.length > 0);
		} catch (TPPRequestException e) {
			Assert.fail(e.getMessage(), e.getCause());
		}
	}
	
	//@Test(groups="unit")
	public void testPostDocument() {
		String url = "http://dev.theeuropeanlibrary.org/tpz/zoom";
		String postData = "img=http://upload.wikimedia.org/wikipedia/en/1/17/Georg_Cantor.jpg";
		try {
			HTTPResponse response = handler.getResponse(url, 
											HTTPConnectionManager.POST, postData);
			int status = response.getStatus();
			byte[] body = response.getBody();
			System.out.println(new String(body));
			Assert.assertTrue(status == HttpURLConnection.HTTP_OK);
			Assert.assertTrue(body.length > 0);
		} catch (TPPRequestException e) {
			Assert.fail(e.getMessage(), e.getCause());
		}
	}
	
	@Test(groups="unit")
	public void testHeadDocument() {
		//String url = "http://jsruo.kb.nl/sru/sru?query=title%20all%20%22test%22&x-collection=GGC";
		String url = "http://sru.kb.nl/sru/sru.pl?query=title%20all%20%22test%22&x-collection=GGC";
		try {
			HTTPResponse response = handler.getResponse(url, 
											HTTPConnectionManager.HEAD, null);
			int status = response.getStatus();
			byte[] body = response.getBody();
			Assert.assertTrue(status == HttpURLConnection.HTTP_OK);
			Assert.assertTrue(body.length == 0);
		} catch (TPPRequestException e) {
			Assert.fail(e.getMessage(), e.getCause());
		}
	}

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
