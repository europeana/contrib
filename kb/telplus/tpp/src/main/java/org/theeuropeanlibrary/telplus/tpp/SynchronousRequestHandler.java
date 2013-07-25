package org.theeuropeanlibrary.telplus.tpp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 * This request handler deals with requests synchronously: blocks while
 * retrieving the document and then returns it.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 8 mei 2008
 */
public class SynchronousRequestHandler {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public HTTPResponse getResponse(String url, String method, String postData) 
											throws TPPRequestException {
		return getResponse(url, method, null, postData);
	}
	
	public HTTPResponse getResponse(String url, String method, 
								Map<String, List<String>> requestHeader,
								String postData) 
										throws TPPRequestException {
		// get the document and return it
		HTTPConnectionManager mgr = new HTTPConnectionManager();
		try {
			return mgr.getResponse(url, method, requestHeader, postData);
		} catch (IOException e) {
			throw new TPPRequestException("Error getting document " + url, e);
		}		
		
	}
	
	
	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
