package org.theeuropeanlibrary.telplus.rcm.test;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.theeuropeanlibrary.telplus.rcm.RequestCountMonitor;

public class RequestCountMonitorTest {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Test(groups="unit")
	public void testGetRequestsPerSecond() {
		int numberOfRequests = 100;
		String ipAddress = "127.0.0.1";
		int requestsPerSecond = 30;
		float error = 0.1f;
		RequestCountMonitor rcm = new RequestCountMonitor(50);
		for (int i = 0; i < numberOfRequests; i++) {
			rcm.getRequestsPerSecond(ipAddress);
			try {
				Thread.sleep(Math.round((1000 / (float)requestsPerSecond)));
			} catch (InterruptedException e) {
				// ignore
			}
		}
		
		
		float lastNumberOfRequests = rcm.getRequestsPerSecond(ipAddress);
		System.out.println("req/s: " + lastNumberOfRequests);
		Assert.assertTrue(lastNumberOfRequests < 
												(1 + error) * requestsPerSecond);
		Assert.assertTrue(lastNumberOfRequests > 
												(1 - error) * requestsPerSecond);

		
	}

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
