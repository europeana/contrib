package org.theeuropeanlibrary.telplus.tpp.test;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.theeuropeanlibrary.telplus.tpp.AsynchronousRequestHandler;
import org.theeuropeanlibrary.telplus.tpp.HTTPConnectionManager;
import org.theeuropeanlibrary.telplus.tpp.HTTPResponse;
import org.theeuropeanlibrary.telplus.tpp.AsynchronousSessionManager;
import org.theeuropeanlibrary.telplus.tpp.TPPRequestException;
/**
 * 
 * This class contains tests for {@link AsynchronousRequestHandler}. To understand
 * what's going on here, refer to the requirements document, appendix A.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 25 aug 2008
 * 
 * $Id: AsynchronousRequestHandlerTest.java 3384 2009-09-30 15:39:53Z michel $
 */
public class AsynchronousRequestHandlerTest {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AsynchronousRequestHandlerTest() {
		BasicConfigurator.configure();
	}

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// test general flow of the protocol
	@Test(groups="unit")
	public void testProtocolFlow() {
		final AsynchronousSessionManager asm = new AsynchronousSessionManager();
		final AsynchronousRequestHandler handler = 
										new AsynchronousRequestHandler(asm, true);
		final String sessionId = "test";
		final String method = HTTPConnectionManager.GET;
		final String reference1 = "test1";
		String reference2 = "test2";
		
		class ThreadForFirstRequest extends Thread {
			public void run() {
				//String url = "http://jsruo.kb.nl/sru/sru?query=test&x-collection=GGC";
				String url = "http://sru.kb.nl/sru/sru.pl?query=test&x-collection=GGC";
				try {
					handler.getResponse(reference1, url, method, sessionId, null);
				} catch (Exception e) {
					Assert.fail(e.getMessage());
				}
			}
		}
		// do a first request
		ThreadForFirstRequest firstRequest = new ThreadForFirstRequest();
		firstRequest.start();
		// make sure the first request has been done: sleep 30 ms
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			
		}
		
		// now do a concurrent second request to which we should receive an 
		// empty HTTP OK
		String url = "http://sru.kb.nl/sru/sru.pl";
		
		try {
			HTTPResponse response = handler.getResponse(reference2, url, method,
															sessionId, null);
			int status = response.getStatus();
			byte[] body = response.getBody();
			Assert.assertTrue(status == HttpURLConnection.HTTP_OK);
			Map<String, List<String>> extraHeaderFields = 
										response.getHeaderFieldMap();
			Assert.assertEquals(extraHeaderFields.get("X-Reference").get(0), "");
			Assert.assertEquals(extraHeaderFields.get("X-Pending").get(0), "2");
			Assert.assertTrue(body.length == 0);

		} catch (Exception e) {
			Assert.fail(e.getMessage(), e.getCause());
		}
		
		// finally get the pending response without submitting a
		// new request (i.e. send an empty request message)
		try {
			// wait for the first request to finish, this request
			// will probably have returned the response corresponding
			// to reference2 
			firstRequest.join();
			HTTPResponse response = handler.getResponse(null, null, method,
															sessionId, null);
			int status = response.getStatus();
			byte[] body = response.getBody();
			Assert.assertTrue(status == HttpURLConnection.HTTP_OK);
			Assert.assertTrue(body.length > 0);
			Map<String, List<String>> extraHeaderFields = 
										response.getHeaderFieldMap();
			// the first request will probably have returned the response for 
			// reference2 
			Assert.assertEquals(extraHeaderFields.get("X-Reference").get(0), reference1);
			Assert.assertEquals(extraHeaderFields.get("X-Pending").get(0), "0");

		} catch (Exception e) {
			Assert.fail(e.getMessage(), e.getCause());	
		}

	}
	
	// an empty request message should result in an error if there is no
	// pending request
	@Test(groups="unit")
	public void testBadRequest() {
		AsynchronousRequestHandler handler = new AsynchronousRequestHandler(new 
											AsynchronousSessionManager(), true);
		String method = HTTPConnectionManager.GET;
		String sessionId = "test";
		try {
			HTTPResponse response = handler.getResponse(null, null, method,
															sessionId, null);
			Assert.fail(TPPRequestException.class.getCanonicalName() + 
															" expected");
		} catch (Exception e) {
			// expected
		}

	}

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
