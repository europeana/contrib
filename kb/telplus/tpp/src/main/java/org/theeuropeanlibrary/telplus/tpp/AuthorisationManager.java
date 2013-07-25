package org.theeuropeanlibrary.telplus.tpp;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This class handles authorisation: checks whether we can proxy for the
 * given URL.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 15 mei 2008
 */
public class AuthorisationManager {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final int ALLOWED = 0;
	public static final int DENIED = 1;
	public static final int ALLOWED_CONDITIONALLY = 2;

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private Collection<String> allowedBaseUrls;
	private Collection<String> deniedDomains;
	private Collection<String> deniedNetworks;

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AuthorisationManager(Collection<String> allowedBaseUrls,
								Collection<String> deniedDomains,
								Collection<String> deniedNetworks) {
		this.allowedBaseUrls = allowedBaseUrls;
		this.deniedDomains = deniedDomains;
		this.deniedNetworks = deniedNetworks;
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * This method implements the authorisation rules as per the configuration file
	 * in the following way:
	 * a. if the given URL has a corresponding base URL in the "allowed" list we 
	 * return ALLOWED;
	 * b. if the given URL does not have a corresponding base URL in the "allowed" list 
	 * we have two possible situations:
	 * 	i) the URL belongs to a denied domain/network, in which case access is DENIED;
	 * 	ii) the URL does not belong to a denied domain/network, in which case access is 
	 * ALLOWED_CONDITIONALLY; 
	 */
	public int isAllowedUrl(String url) throws MalformedURLException {
		for (String baseUrl : allowedBaseUrls) {
			if (url.startsWith(baseUrl)) { 
				return ALLOWED;
			} else {
				// perhaps we didn't match because of explicit default port numbers?
				String portlessUrl = url.replaceFirst(":(80|443)", "");
				String portlessBaseUrl = baseUrl.replaceFirst(":(80|443)", "");
				if (portlessUrl.startsWith(portlessBaseUrl)) { 
					return ALLOWED;
				} 
			} 
		} 
			
		// perhaps we can still allow conditionally?
		String host = getHostFromUrl(url);
		if (isIpAddress(host)) {
			for (String network : deniedNetworks) {
				if (host.startsWith(network))
					return DENIED;
			}
		} else {
			for (String domain : deniedDomains) {
				if (host.endsWith(domain))
					return DENIED;
			}
		}
		return ALLOWED_CONDITIONALLY;

	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private String getHostFromUrl(String url) throws MalformedURLException {
		String host;
		Matcher m = Pattern.compile("^[^:]+:\\/\\/(.*?@)?([^:/]+)").matcher(url);
		if (m.find()) {
			host = m.group(2);
		} else {
			throw new MalformedURLException("Malformed URL: " + url);
		}
		
		// disallow dotless IP addresses
		m = Pattern.compile("^[0-9A-Fa-f]+$").matcher(host);
		if (m.find())
			throw new MalformedURLException("Dotless IP addresses are not allowed!");
		
		return host;
	}
	
	private boolean isIpAddress(String host) {
		Matcher m = Pattern.
						compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").
								matcher(host);
		return m.find();
	}

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
