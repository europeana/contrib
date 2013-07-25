package org.theeuropeanlibrary.telplus.tpp.test;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.theeuropeanlibrary.telplus.tpp.Configuration;

/**
 * 
 * Tests for class Configuration
 * @author Michel Koppelaar
 *
 * Created on: 15 mei 2008
 */
public class ConfigurationTest {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final String CONFIG_DIRECTORY = "./src/main/resources/";
	private static final String PROPERTIES_FILE = CONFIG_DIRECTORY + "tpp.properties";
	private static final String AUTHORISATION_FILE = CONFIG_DIRECTORY + "authorisation.xml";
	
	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private File propertiesFile;
	private File authorisationFile;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public ConfigurationTest() {
		BasicConfigurator.configure();
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@BeforeSuite
	public void setUp() {
		propertiesFile = new File(PROPERTIES_FILE);
		authorisationFile = new File(AUTHORISATION_FILE);
	}
	
	@Test(groups="unit")
	public void testConstructor() {
		try {
			Configuration c = new Configuration(propertiesFile, authorisationFile);
			Assert.assertTrue(c.getAllowedBaseUrls().size() > 0);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
