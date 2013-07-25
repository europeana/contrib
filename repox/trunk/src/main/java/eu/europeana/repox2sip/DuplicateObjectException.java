package eu.europeana.repox2sip;

/**A DuplicateObjectException is thrown if an attempt is made to 
 * insert an object and the object already exists in the data store.
 * 
 * @author Cesare Concordia   <cesare.concordia@isti.cnr.it>
 */

public class DuplicateObjectException extends Repox2SipException{
	private long id;

	/**
	 *  Constructs an DuplicateObjectException object with no detail message.
	 */
	
	public DuplicateObjectException() {
		super();
		
	}

	/**
	 *  Constructs an DuplicateObjectException object with a detail message
	 *  
	 *  @param message String the description of the error
	 */
	public DuplicateObjectException(String message) {
		super(message);
		
	}
	
	/**
	 *  Constructs an ObjectNotFoundException object with a detail message
	 *  and the identifier of the existing object
	 */
	public DuplicateObjectException(String message, long id) {
		super(message);
		this.id=id;
		
	}
	
	
	/**
     * Get the id of the duplicate object
     *
     * @return id long the id of the duplicate object
     */
	public long getDuplicateId(){
		return id;
	}


}
