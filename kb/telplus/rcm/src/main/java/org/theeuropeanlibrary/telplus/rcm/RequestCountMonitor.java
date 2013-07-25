package org.theeuropeanlibrary.telplus.rcm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * 
 * Class that keeps track of mean time between requests coming from an IP address. 
 *  
 * @author Michel Koppelaar
 *
 * Created on: 15 okt 2008
 * 
 * $Id: RequestCountMonitor.java 3182 2009-03-04 13:34:36Z michel $
 */
public class RequestCountMonitor {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// weight used in EMA calculation
	private static final float WEIGHT = 0.9f;

	
	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private HashMap<String, Statistics> perIpAddrStats = new HashMap<String, Statistics>();
	private int initialSampleSize;

	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public RequestCountMonitor() {
	}
	
	/**
	 * 
	 * @param initialSampleSize the initial sample size paramater determines 
	 * how many values we require in order to calculate the initial average. If 
	 * this number is n, then the first n calls to getRequestsPerSecond will 
	 * return 0.
	 */
	public RequestCountMonitor(int initialSampleSize) {
		this.initialSampleSize = initialSampleSize;
	}

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * This method returns the number of requests per second coming from the 
	 * supplied IP address. It is assumed that the call to this method is made
	 * "immediately" after the most recent request from the IP address.
	 * 
	 * @param ipAddress IP address
	 * 
	 * @return the (moving) average number of requests per second coming
	 * from the supplied IP address 
	 */
	public synchronized float getRequestsPerSecond(String ipAddress) {
		if (!perIpAddrStats.containsKey(ipAddress)) {
			Statistics s = new Statistics();
			s.setTimeOfLastRequest(System.currentTimeMillis());
			perIpAddrStats.put(ipAddress, s);
			return 0;
		} else {
			Statistics s = perIpAddrStats.get(ipAddress);
			updateStatistics(s);
			// return the reciprocal of the mean time between requests
			// i.e. the number of requests per second
			float meanTimeBetweenRequests = s.getMeanTimeBetweenRequests();
			if (meanTimeBetweenRequests == 0) {
				// if the mean time has been rounded to zero,
				// we take the ceiling of its original value
				meanTimeBetweenRequests = 1;
			}
			if (s.getSampleSize() > initialSampleSize) { 
				return (1000 / meanTimeBetweenRequests);
			} else {
				return 0;
			}
		}
	}
	

	public String getVersion() {
		String version = "Unknown";
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF"));
			if (prop.containsKey("Specification-Version")) {
				version = (String)prop.get("Specification-Version");
			}
		} catch (IOException e) {
			// just ignore it
		}
		return version;
	}
	
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/*
	 * calculate exponential moving average of the size of the 
	 * interval between requests 
	 */
	private void updateStatistics(Statistics s) {
		long timeOfLastRequest = s.getTimeOfLastRequest();
		long timeOfCurrentRequest = System.currentTimeMillis();
		long interval = timeOfCurrentRequest - timeOfLastRequest;
		float oldMeanTimeBetweenRequests = s.getMeanTimeBetweenRequests();
		float meanTimeBetweenRequests;
		// initially the mean time is -1, so the average is just our
		// first data point
		if (oldMeanTimeBetweenRequests == -1) {
			meanTimeBetweenRequests = interval;
		} else {
			meanTimeBetweenRequests = WEIGHT * oldMeanTimeBetweenRequests
											+
									(1 - WEIGHT) * interval;	
		}			
		s.setTimeOfLastRequest(timeOfCurrentRequest);
		s.setMeanTimeBetweenRequests(meanTimeBetweenRequests);
		s.incrementSampleSize();
	}
	
	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
