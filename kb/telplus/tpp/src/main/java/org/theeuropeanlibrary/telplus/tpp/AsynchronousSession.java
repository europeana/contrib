package org.theeuropeanlibrary.telplus.tpp;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Simple class used to store per-session info.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 25 aug 2008
 * 
 * $Id: AsynchronousSession.java 2771 2008-09-16 16:31:31Z michel $
 */
public class AsynchronousSession {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private LinkedBlockingQueue<HTTPResponse> responseQueue;
	private boolean blocking;
	private int requestCount;
	private Date lastModified;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public AsynchronousSession() {
		this.responseQueue = new LinkedBlockingQueue<HTTPResponse>();
		this.blocking = false;
		this.requestCount = 0;
		this.lastModified = new Date();
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public boolean isBlocking() {
		return this.blocking;
	}

	public synchronized void setBlocking(boolean waiting) {
		this.blocking = waiting;
	}
	
	public HTTPResponse getFirstAvailableResponse(int timeOut) throws 
											InterruptedException,
											TimeOutException {
		HTTPResponse response = this.responseQueue.poll(timeOut, TimeUnit.SECONDS); 
		
		if (response == null)
			throw new TimeOutException();
		
		return response;
	}
	
	public void addResponse(HTTPResponse response) {
		this.responseQueue.add(response);
	}
	
	public synchronized void incrementRequestCount() {
		this.requestCount++;
	}
	
	public synchronized void decrementRequestCount() {
		this.requestCount--;
	}
	
	public int getPendingRequestCount() {
		return this.requestCount;
	}
	
	/**
	 * updates last modified time
	 */
	public synchronized void touch() {
		// this is probably faster than "lastModified = new Date()"
		this.lastModified.setTime(System.currentTimeMillis());
	}
	
	/**
	 * 
	 * @return {@link Date} of last modification
	 */
	public Date getLastModified() {
		return this.lastModified;
	}
	
	/**
	 * removes pending responses from this session
	 */
	public synchronized void clearPendingResponses() {
		HTTPResponse response;
		while ((response = this.responseQueue.poll()) != null) {
			response = null;
		}
		this.requestCount = 0;
	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
