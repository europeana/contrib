package org.theeuropeanlibrary.telplus.rcm;

/**
 * 
 * This class holds per-address statistics.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 15 okt 2008
 * 
 * $Id: Statistics.java 3182 2009-03-04 13:34:36Z michel $
 */
public class Statistics {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private float meanTimeBetweenRequests;
	private long timeOfLastRequest;
	private long sampleSize;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public Statistics() {
		meanTimeBetweenRequests = -1;
		timeOfLastRequest = 0;
		sampleSize = 0;
	}
	
	public float getMeanTimeBetweenRequests() {
		return meanTimeBetweenRequests;
	}
	
	public long getTimeOfLastRequest() {
		return timeOfLastRequest;
	}
	
	public long getSampleSize() {
		return sampleSize;
	}
	
	public void setMeanTimeBetweenRequests(float meanTimeBetweenRequests) {
		this.meanTimeBetweenRequests = meanTimeBetweenRequests;
	}
	
	public void setTimeOfLastRequest(long timeOfLastRequest) {
		this.timeOfLastRequest = timeOfLastRequest;
	}
	
	public void incrementSampleSize() {
		sampleSize++;
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
