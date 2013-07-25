package eu.europeana.repox2sip;

	/**This exception provides information on data store access errors or
	 * other errors related to interaction with the data store
	 * 
	 * @author Cesare Concordia   <cesare.concordia@isti.cnr.it>
	 */

public class DataStoreException extends Repox2SipException {

	private String errorCode;

	/**
	 *  Constructs an DataStoreException object; the reason field defaults to null
	 *  and the errorCode field defaults to null.
	 */

	public DataStoreException() {
		super();

	}
	/**
	 *  Constructs an DataStoreException object with a reason, 
	 *  the errorCode field defaults to null.
	 *  
	 *  @param reason String a description of the exception
	 */

	public DataStoreException(String reason) {
		super(reason);

	}

	/**
	 *  Constructs a fully-specified DataStoreException object.
	 *  
	 *  @param reason String a description of the exception
	 *  @param errorCode String a data store specific error code
	 */
	public DataStoreException(String reason, String errorCode) {
		super(reason);
		this.errorCode = errorCode;
		//
	}

	/**
     * Get the data store specific error code, can return null if not set
     *
     * @return String
     */

	public String getErrorCode() {
		return errorCode;
	}

}
