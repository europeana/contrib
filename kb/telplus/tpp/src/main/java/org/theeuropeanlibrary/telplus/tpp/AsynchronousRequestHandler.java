package org.theeuropeanlibrary.telplus.tpp;

import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;



/**
 * 
 * This class deals with requests asynchronously. It implements the APP protocol
 * as specified in appendix A of the requirements document for the TPP project.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 8 mei 2008
 */
public class AsynchronousRequestHandler {



	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private AsynchronousSessionManager asm;
	private boolean isNewSession;
	private static Logger logger = Logger.getLogger(AsynchronousRequestHandler.class);

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AsynchronousRequestHandler(AsynchronousSessionManager asm, 
											boolean isNewSession) {
		BasicConfigurator.configure();
		this.asm = asm;
		this.isNewSession = isNewSession;
	}

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public HTTPResponse getResponse(String reference, String url,
									String method, String sessionId, String postData) 
									throws TPPRequestException,
										UnknownSessionIdException,
										NoPendingRequestsException,
										InterruptedException,
										BlockingSessionExistsException,
										TimeOutException {

		return getResponse(reference, url, method, sessionId, null, postData);
	}
	
	public HTTPResponse getResponse(String sessionId) throws TPPRequestException,
														UnknownSessionIdException,
														NoPendingRequestsException,
														InterruptedException,
														BlockingSessionExistsException,
														TimeOutException {
		return getResponse(null, null, null, sessionId, null, null);
	}

	
	public HTTPResponse getResponse(String reference, String url,
										String method, String sessionId,
										Map<String, List<String>> requestHeader,
										String postData) 
										throws TPPRequestException,
											UnknownSessionIdException,
											NoPendingRequestsException,
											InterruptedException,
											BlockingSessionExistsException,
											TimeOutException {

		HTTPResponse response;
		boolean emptyRequestMessage = reference == null && url == null;
		if (isNewSession) {
			asm.createSession(sessionId);
			isNewSession = false;
		}

		if (!emptyRequestMessage)
			asm.submitRequest(sessionId, reference, url, method, requestHeader, postData);
		if (asm.sessionIsBlocking(sessionId)) {
			if (emptyRequestMessage) {
				throw new BlockingSessionExistsException();
			}
			// construct empty response
			response = new HTTPResponse();
			response.addHeaderField("X-Reference", "");
		} else {
			if (emptyRequestMessage) {
				logger.debug("Received empty request message");
				if (asm.getNumberOfPendingRequests(sessionId) > 0) {
					response = asm.getAvailableResponse(sessionId,
											HTTPConnectionManager.TIMEOUT);
				} else {
					throw new NoPendingRequestsException();
				}
			} else {
				response = asm.getAvailableResponse(sessionId,
											HTTPConnectionManager.TIMEOUT);
			}
			
			logger.debug("Returning response for reference " + 
					response.getHeaderFieldMap().get("X-Reference").get(0));
		}
		response.addHeaderField("X-Pending", 
										Integer
											.toString(asm
													.getNumberOfPendingRequests(
															sessionId)));
		logger.debug("Number of pending requests: " 
									+ asm.getNumberOfPendingRequests(sessionId));
		return response;
	}

	/**
	 * abort all requests for the session corresponding with the 
	 * supplied session id
	 */
	public HTTPResponse abort(String sessionId) throws UnknownSessionIdException,
												TPPRequestException {
		logger.debug("Aborting requests for session " + sessionId);
		asm.abort(sessionId);
		
		int numberOfPendingRequests = asm.getNumberOfPendingRequests(sessionId);
		if (numberOfPendingRequests > 0) {
			throw new TPPRequestException("Number of pending requests " +
									"unexpectedly not zero: " + numberOfPendingRequests);
		}
		// construct empty response
		HTTPResponse response = new HTTPResponse();
		response.addHeaderField("X-Reference", "");
		response.addHeaderField("X-Pending", "0");
		
		return response;
	}
	
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
