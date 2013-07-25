package eu.europeana.repox2sip;

import eu.europeana.repox2sip.Repox2SipException;

/**
 * This exception is thrown when an invalid value is found
 * 
 * @author Cesare Concordia   <cesare.concordia@isti.cnr.it>
 */
public class InvalidValueException extends Repox2SipException {
	
	/**
	 *  Constructs an InvalidValueException object with no detail message.
	 */
	
	public InvalidValueException() {
		super();
		
	}

	/**
	 *  Constructs an InvalidValueException object with a detail message
	 *  
	 *  @param message String the description of the error 
	 */
	public InvalidValueException(String message) {
		super(message);
		
	}

}
