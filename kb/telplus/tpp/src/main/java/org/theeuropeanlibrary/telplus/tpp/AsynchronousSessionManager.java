package org.theeuropeanlibrary.telplus.tpp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * 
 * This class handles the actual asynchronous stuff for the short-lived
 * AsynchronousRequestHandler instances.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 21 aug 2008
 * 
 * $Id: AsynchronousSessionManager.java 3367 2009-08-03 15:46:37Z michel $
 */
public class AsynchronousSessionManager {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private ExecutorService executorService;
	private HashMap<String, AsynchronousSession> sessions;

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static Logger logger = Logger.getLogger(AsynchronousRequestHandler.class);
	private static long MAX_SESSION_IDLE_TIME = 120000; // 2 minutes
	
	
	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AsynchronousSessionManager() {
		this.executorService = Executors.newCachedThreadPool();
		this.sessions = new HashMap<String, AsynchronousSession>();
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * creates a new {@link AsynchronousSession} and since this method is called
	 * relatively rarely, we also use it to remove stale sessions
	 */
	public void createSession(String sessionId) {
		synchronized (sessions) {
			if (sessions.containsKey(sessionId)) {
				// clean up before overwriting the session 
				sessions.get(sessionId).clearPendingResponses();
			}
			sessions.put(sessionId, new AsynchronousSession());
		}
		// remove stale sessions
		for (String sId : sessions.keySet()) {
			AsynchronousSession session = sessions.get(sId);
			long now = System.currentTimeMillis();
			long lastModified = session.getLastModified().getTime();
			if (now - lastModified > MAX_SESSION_IDLE_TIME) {
				session.clearPendingResponses();
				sessions.remove(sId);
				logger.debug("Removed stale session " + sId);
			}
		}
	}
	
	public void submitRequest(String sessionId, String reference, String url, 
										String method, 
										Map<String, List<String>> requestHeader,
										String postData) {
		
		synchronized (sessions) {
			if (!sessions.containsKey(sessionId)) {
				sessions.put(sessionId, new AsynchronousSession());
			}
		}
		AsynchronousSession session = sessions.get(sessionId);
		// TODO use upper bound for number of pending requests per session
		session.incrementRequestCount();
		
		// submit request task and return
		RequestTask task = new RequestTask(reference, url, method, requestHeader, 
																postData, session);
		executorService.submit(task);
		
	}
	
	public void submitRequest(String sessionId, String reference, String url, 
													String method, String postData) {
		submitRequest(sessionId, reference, url, method, null, postData);
	}
	
	/**
	 * This method blocks until a response becomes available for this session or
	 * the specified time-out (in seconds) is reached. In the latter case an 
	 * {@link TimeOutException} is thrown. (Note that timeouts can also occur in individual
	 * request threads, but they are handled differently, see {@link RequestTask}). In the first 
	 * case the available {@link HTTPResponse} instance is returned.
	 * 
	 * 
	 * @param sessionId
	 * @param timeOut
	 * @return
	 * @throws UnknownSessionIdException
	 */
	public HTTPResponse getAvailableResponse(String sessionId, int timeOut) 
													throws UnknownSessionIdException,
														InterruptedException,
														TimeOutException {
		if (!sessions.containsKey(sessionId))
			throw new UnknownSessionIdException(sessionId);
		AsynchronousSession session = sessions.get(sessionId);
		session.setBlocking(true);
		HTTPResponse response;
		try {
			response = session.getFirstAvailableResponse(timeOut);
		} catch (TimeOutException e) {
			// make sure we unblock the session
			session.setBlocking(false);
			throw e;
		}
		session.decrementRequestCount();
		session.setBlocking(false);
		return response;
	}
	
	// TODO check the integrity of request count
	// TODO think about ways to detect denial of service
	
	public int getNumberOfPendingRequests(String sessionId)
												throws UnknownSessionIdException {
		if (!sessions.containsKey(sessionId))
			throw new UnknownSessionIdException(sessionId);

		return sessions.get(sessionId).getPendingRequestCount();
	}
	
	public boolean sessionIsBlocking(String sessionId) 
												throws UnknownSessionIdException {
		if (!sessions.containsKey(sessionId))
			throw new UnknownSessionIdException(sessionId);

		return sessions.get(sessionId).isBlocking();
	}
	
	
	/**
	 * remove all pending responses for the session 
	 * corresponding to the supplie session id
	 * @param sessionId
	 */
	public void abort(String sessionId) throws UnknownSessionIdException {
		if (!sessions.containsKey(sessionId))
			throw new UnknownSessionIdException(sessionId);

		AsynchronousSession session = sessions.get(sessionId);
		session.clearPendingResponses();
	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
