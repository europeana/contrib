package eu.europeana.repox2sip;

/**This exception is thrown when an object is not found in the 
 * data store.
 * 
 * @author Cesare Concordia   <cesare.concordia@isti.cnr.it>
 */

public class ObjectNotFoundException extends Repox2SipException{

	
	/**
	 *  Constructs an ObjectNotFoundException object with no detail message.
	 */
	
	public ObjectNotFoundException() {
		super();
		
	}

	/**
	 *  Constructs an ObjectNotFoundException object with a detail message
	 *  
	 *  @param message String the description of the error 
	 */
	public ObjectNotFoundException(String message) {
		super(message);
		
	}
	
	

}
