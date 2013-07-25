package org.theeuropeanlibrary.telplus.tpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * Class holding configuration information.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 8 mei 2008
 */
public class Configuration {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private Collection<String> allowedBaseUrls;
	private Collection<String> deniedNetworks;
	private Collection<String> deniedDomains;
	private String liabilityWarning;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public Configuration(File configurationFile, File authorisationFile) 
										throws IOException, SAXException {
		
		// parse general properties file
		if (configurationFile == null)
			throw new RuntimeException("Configuration file parameter was null");
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream(configurationFile));		
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can't find configuration file "
											+ configurationFile.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException("I/O exception while trying to read "
					+ "configuration file " + configurationFile.getAbsolutePath() + 
					", message was: " + e.getMessage());
		}
		
		liabilityWarning = p.getProperty("liabilityWarning");
		if (liabilityWarning == null) {
			throw new RuntimeException("No property \"liabilityWarning\" in " +
					"configuration file");
		}
		
		// parse authorisation file
		if (authorisationFile == null)
			throw new RuntimeException("Authorisation file parameter " +
														"was null");
		parseAuthorisationFile(authorisationFile);
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public Collection<String> getAllowedBaseUrls() {
		return allowedBaseUrls;
	}

	public Collection<String> getDeniedNetworks() {
		return deniedNetworks;
	}
	
	public Collection<String> getDeniedDomains() {
		return deniedDomains;
	}
	
	public String getLiabilityWarning() {
		return liabilityWarning;
	}

		
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	
	private void parseAuthorisationFile(File f) throws SAXException, IOException {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		AuthorisationFileHandler handler = new AuthorisationFileHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(new InputSource(new FileInputStream(f)));
		allowedBaseUrls = handler.getAllowedBaseUrls();
		deniedDomains = handler.getDeniedDomains();
		deniedNetworks = handler.getDeniedNetworks();
	}
	
	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
