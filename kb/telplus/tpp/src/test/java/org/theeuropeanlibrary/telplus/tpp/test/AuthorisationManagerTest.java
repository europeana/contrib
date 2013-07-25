package org.theeuropeanlibrary.telplus.tpp.test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.theeuropeanlibrary.telplus.tpp.AuthorisationManager;

/**
 * 
 * Test class for AuthorisationManager
 * 
 * @author Michel Koppelaar
 *
 * Created on: 15 mei 2008
 */
public class AuthorisationManagerTest {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final String ALLOWED_URL = "http://www.theeuropeanlibrary.org";
	private static final String DENIED_URL = "http://www.kb.nl";
	private static final String CONDITIONALLY_ALLOWED_URL = "http://www.google.com";
	private static final String DENIED_DOMAIN = "kb.nl";
	private static final String URL_DOTLESS_HEX_IP = "http://c0571f41";
	private static final String URL_DOTLESS_DEC_IP = "http://3226935105";
	
	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private Collection<String> allowedBaseUrls = new ArrayList<String>();
	private Collection<String> deniedDomains = new ArrayList<String>();
	private Collection<String> deniedNetworks = new ArrayList<String>();
	private AuthorisationManager mgr;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AuthorisationManagerTest() {
		BasicConfigurator.configure();
	}

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@BeforeSuite
	public void setUp() {
		allowedBaseUrls.add(ALLOWED_URL);
		deniedDomains.add(DENIED_DOMAIN);
		mgr = new AuthorisationManager(allowedBaseUrls, deniedDomains, deniedNetworks);
	}
	
	@Test(groups="unit")
	public void testIsAllowedUrl() {
		try {
			Assert.assertTrue(mgr.isAllowedUrl(ALLOWED_URL) == 
														AuthorisationManager.ALLOWED);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(groups="unit")
	public void testIsNotAllowedUrl() {
		try {
			Assert.assertTrue(mgr.isAllowedUrl(DENIED_URL) ==
												AuthorisationManager.DENIED);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(groups="unit")
	public void testIsAllowedConditionally() {
		try {
			Assert.assertTrue(mgr.isAllowedUrl(CONDITIONALLY_ALLOWED_URL) ==
											AuthorisationManager.ALLOWED_CONDITIONALLY);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(groups="unit")
	public void testCatchDotlessIpAddress() {
		try {
			mgr.isAllowedUrl(URL_DOTLESS_HEX_IP);
			mgr.isAllowedUrl(URL_DOTLESS_DEC_IP);
		} catch (MalformedURLException e) {
			return;
		}
		Assert.fail("Expected exception: " + MalformedURLException.class.getCanonicalName());
	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
