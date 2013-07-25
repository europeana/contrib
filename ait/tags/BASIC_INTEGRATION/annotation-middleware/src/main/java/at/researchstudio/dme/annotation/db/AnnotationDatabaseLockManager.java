package at.researchstudio.dme.annotation.db;

import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationLockedException;

/**
 * Used by annotation databases to implement optimistic locking on a per annotation base.
 * 
 * @author Christian Sadilek
 */
public interface AnnotationDatabaseLockManager {
	
	/**
	 * acquire lock for the given annotation
	 * 
	 * @param annotationId
	 * @param timeout in ms
	 * @return the locked annotation id
	 * @throws AnnotationDatabaseException in case of an unexpected error
	 * @throws AnnotationLockedException in case annotation lock could not be acquired within
	 * 	the given time.
	 */
	public String acquireLock(String annotationId, long timeout) throws AnnotationDatabaseException, 
			AnnotationLockedException;
	
	/**
	 * acquire lock for the given annotation
	 * 
	 * @param annotationId
	 * @return true in case lock was created, otherwise false
	 */	
	public boolean tryAcquireLock(String annotationId);
		
	/**
	 * release lock for the given annotation
	 * 
	 * @param annotationId
	 * @throws AnnotationDatabaseException 
	 */
	public void releaseLock(String annotationId) throws AnnotationDatabaseException;
	
	/**
	 * set the connection this instance is working on.
	 * 
	 * @param connection
	 * @throws AnnotationDatanaseException
	 */
	public void setConnection(Object connection) throws AnnotationDatabaseException;
	
	/**
	 * close the underlying connection
	 * 
	 * @throws AnnotationDatabaseException
	 */
	public void closeConnection() throws AnnotationDatabaseException;
}
