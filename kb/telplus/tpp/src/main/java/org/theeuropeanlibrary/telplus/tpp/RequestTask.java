package org.theeuropeanlibrary.telplus.tpp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.theeuropeanlibrary.telplus.tpp.web.TPPServlet;

public class RequestTask implements Runnable {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private String reference;
	private String url;
	private String method;
	private String postData;
	private Map<String, List<String>> requestHeader;
	private AsynchronousSession session;

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static Logger logger = Logger.getLogger(AsynchronousRequestHandler.class);
	
	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public RequestTask(String reference, String url, String method, 
								Map<String, List<String>> requestHeader, 
									String postData,
									AsynchronousSession session) {
		this.reference = reference;
		this.url = url;
		this.method = method;
		this.requestHeader = requestHeader;
		this.postData = postData;
		this.session = session;
	}

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void run() {
		HTTPResponse response;
		try {
			logger.debug("Sending request for reference " + reference + ", URL is " + url);

			response = new HTTPConnectionManager().
												getResponse(url, 
															method, 
															requestHeader,
															postData);
		} catch (SocketTimeoutException e) {

			response = new HTTPResponse("A timeout occurred".getBytes(), 
														HttpURLConnection.HTTP_GATEWAY_TIMEOUT);
			// show that this error originated at the proxy
			response.addHeaderField(TPPServlet.PROXY_STATUS_FIELD, TPPServlet.NON_ZERO_PROXY_STATUS);
		} catch (IOException e) {
			response = new HTTPResponse("I/O exception".getBytes(), 
														HttpURLConnection.HTTP_INTERNAL_ERROR);
			// show that this error originated at the proxy
			response.addHeaderField(TPPServlet.PROXY_STATUS_FIELD, TPPServlet.NON_ZERO_PROXY_STATUS);
		} catch (Throwable t) {
			response = new HTTPResponse("Unknown error".getBytes(), 
														HttpURLConnection.HTTP_INTERNAL_ERROR);
			// show that this error originated at the proxy
			response.addHeaderField(TPPServlet.PROXY_STATUS_FIELD, TPPServlet.NON_ZERO_PROXY_STATUS);			
		}
		response.addHeaderField("X-Reference", reference);
		session.addResponse(response);
		logger.debug("Adding response for reference " + reference + ", URL was " + url);

	}

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
