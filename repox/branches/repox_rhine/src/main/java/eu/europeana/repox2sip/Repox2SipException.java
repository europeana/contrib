package eu.europeana.repox2sip;

/**This exception provides information on Repox2Sip bridging errors. This
 * class extends  <code>java.lang.Exception</code> and indicates 
 * error conditions that can occur when trying to store data in the shared data store.
 * 
 * @author Cesare Concordia   <cesare.concordia@isti.cnr.it>
 */


public class Repox2SipException extends Exception {

	/**
	 * Constructs a new exception with null as its detail message.
	 */
	public Repox2SipException() {
		
	}

	/**
	 * Constructs a new exception with  a detail message.
	 * 
	 * @param reason String the error message. This message is saved for later retrieval by the 
	 * Throwable.getMessage() method.
	 */
	public Repox2SipException(String reason) {
		super(reason);
		
	}

	/**
	 * Constructs a new exception with the cause 
	 * of the error.
	 * 
	 * @param cause Throwable (can be retrieved by the Trowable.getCause() method)
	 */
	
	public Repox2SipException(Throwable cause) {
		super(cause);
		
	}
	
	/**
	 * Constructs a new exception with  a detail message and the cause 
	 * of the error.
	 * 
	 * @param reason String the error message. This message is saved for later retrieval by the 
	 * Throwable.getMessage() method.
	 * 
	 * @param cause Throwable (can be retrieved by the Trowable.getCause() method)
	 */

	public Repox2SipException(String reason, Throwable cause) {
		super(reason, cause);
		
	}
}
