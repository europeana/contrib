package org.theeuropeanlibrary.telplus.tpp;

import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * SAX handler for parsing the authorisation file.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 1 okt 2008
 * 
 * $Id: AuthorisationFileHandler.java 2785 2008-10-03 10:25:53Z michel $
 */
public class AuthorisationFileHandler extends DefaultHandler {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static String NETWORK_ELEMENT = "network";
	private static String DOMAIN_ELEMENT = "domain";
	private static String BASE_URL_ELEMENT = "baseUrl";

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private ArrayList<String> allowedBaseUrls;
	private ArrayList<String> deniedNetworks;
	private ArrayList<String> deniedDomains;
	
	private boolean insideNetwork = false;
	private boolean insideDomain = false;
	private boolean insideBaseUrl = false;
	private String currentContent = "";
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AuthorisationFileHandler () {
		allowedBaseUrls = new ArrayList<String>();
		deniedNetworks = new ArrayList<String>();
		deniedDomains = new ArrayList<String>();
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
	
	/*
	 * SAX callbacks
	 */
	public void startElement(String uri, String name, String qName, Attributes atts) {
		if (name.equals(NETWORK_ELEMENT)) {
			insideNetwork = true;
		} else if (name.equals(DOMAIN_ELEMENT)) {
			insideDomain = true;
		} else if (name.equals(BASE_URL_ELEMENT)) {
			insideBaseUrl = true;
		}
	}
	
	public void endElement(String uri, String name, String qName) {
		if (insideNetwork) {
			deniedNetworks.add(currentContent.trim());
			insideNetwork = false;
		} else if (insideDomain) {
			deniedDomains.add(currentContent.trim());
			insideDomain = false;
		} else if (insideBaseUrl) {
			allowedBaseUrls.add(currentContent.trim());
			insideBaseUrl = false;
		}
		
		currentContent = "";
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		currentContent += new String(ch, start, length);
	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
