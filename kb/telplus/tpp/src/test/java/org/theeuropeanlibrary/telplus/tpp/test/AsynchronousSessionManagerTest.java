package org.theeuropeanlibrary.telplus.tpp.test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.theeuropeanlibrary.telplus.tpp.AuthorisationManager;
import org.theeuropeanlibrary.telplus.tpp.HTTPConnectionManager;
import org.theeuropeanlibrary.telplus.tpp.HTTPResponse;
import org.theeuropeanlibrary.telplus.tpp.AsynchronousSessionManager;
import org.theeuropeanlibrary.telplus.tpp.TPPRequestException;

/**
 * 
 * Test class for AuthorisationManager
 * 
 * @author Michel Koppelaar
 *
 * Created on: 15 mei 2008
 */
public class AsynchronousSessionManagerTest {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AsynchronousSessionManagerTest() {
	
		BasicConfigurator.configure();
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Test(groups="unit")
	public void testGetAvailableResponse() {
		String sessionId = "test";
		String reference = "test";
		String url = "http://www.kb.nl/";
		String method = HTTPConnectionManager.GET;
		int timeOut = 20;
		try {
			AsynchronousSessionManager rqm = new AsynchronousSessionManager();
			rqm.submitRequest(sessionId, reference, url, method, null);
			HTTPResponse response = rqm.getAvailableResponse(sessionId, timeOut);
			Assert.assertEquals(response.getStatus(), HttpURLConnection.HTTP_OK);
			Assert.assertTrue(response.getBody().length > 0);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
