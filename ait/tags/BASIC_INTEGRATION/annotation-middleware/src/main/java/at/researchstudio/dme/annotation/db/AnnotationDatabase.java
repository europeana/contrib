package at.researchstudio.dme.annotation.db;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
 * Base class for annotation databases.
 * <p>
 * Note on concurrent modifications: We use an optimistic locking strategy.
 * <p>
 * Since only the author of an annotation is allowed to update and delete we do
 * not have to handle concurrent modifications. If any client allowed users to
 * have more than one active session, updates could be lost but we do not
 * consider this to be a problem since the most recent update would be effective.
 * <p>
 * What we do have to handle is annotation creation with interfering/concurrent
 * updates/removals. If a user creates an annotation with an reference to
 * another annotation it has to be ensured that the referenced annotation has
 * not been changed in the time between the last read (see {@link AnnotationModifiedException}). 
 * Further, on update and delete it has to be ensured that the corresponding annotation 
 * is still unreferenced (see {@link AnnotationAlreadyReferencedException}). A referenced
 * annotation cannot be updated or deleted.
 *
 * @author Christian Sadilek
 */
public abstract class AnnotationDatabase {	
	protected static final String UNEXPECTED_RESPONSE = "unexpected response";		
	protected static final String FAILED_TO_READ_ANNOTATION = "failed to read annotation";	
	protected static final String FAILED_TO_SAVE_ANNOTATION = "failed to save annotation";
	protected static final String FAILED_TO_DELETE_ANNOTATION = "failed to delete annotation";
	protected static final String FAILED_TO_PARSE_ANNOTATION = "failed to parse annotation";

	private boolean autoCommit = true;
	
	/**
	 * check if auto commit is on
	 * 
	 * @return true if auto commit is on, otherwise false
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}
	
	/**
	 * set the auto commit mode
	 * 
	 * @param autoCommit
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	
	/**
	 * initialize 
	 *  
	 * @throws AnnotationDabaseException
	 */
	public abstract void init() throws AnnotationDatabaseException;
	
	/**
	 * shutdown 
	 *  
	 */
	public abstract void shutdown();
	
	/**
	 * connect to the db
	 * 
	 * @throws AnnotationDatabaseException
	 */
	public void connect() throws AnnotationDatabaseException {
		connect(null);
	}

	/**
	 * connect to the db and provide a request object to access
	 * parameters, cookies, etc.
	 * 
	 * @throws AnnotationDatabaseException
	 */
	public abstract void connect(HttpServletRequest request) throws AnnotationDatabaseException;
	
	/**
	 * disconnect from the db 
	 * 
	 * @throws AnnotationDatabaseException
	 */
	public abstract void disconnect();

	/**
	 * commit all changes
	 * 
	 * @throws AnnotationDatabaseException
	 */
	public abstract void commit() throws AnnotationDatabaseException;
	
	/**
	 * roll back all changes
	 * 
	 * @throws AnnotationDatabaseException
	 */
	public abstract void rollback() throws AnnotationDatabaseException;
	
	/**
	 * creates a new annotation
	 * 
	 * @param annotation representation
	 * @return id (URI) of the new annotation
	 * @throws AnnotationDatabaseException, AnnotationException, 
	 * 	AnnotationModifiedException
	 */
	public abstract URI createAnnotation(String annotation) 
			throws AnnotationDatabaseException, AnnotationException, AnnotationModifiedException;

	/**
	 * updates an annotation
	 * 
	 * @param id of the annotation
	 * @param annotation representation
	 * @return id (URI) of updated annotation. a back-end implementation might decide to change the 
	 * URI of an annotation when it is updated.
	 * @throws AnnotationDatabaseException, AnnotationException, 
	 * 	AnnotationAlreadyReferencedException
	 */
	public abstract URI updateAnnotation(String id, String annotation)
			throws AnnotationDatabaseException, AnnotationException,
			AnnotationAlreadyReferencedException;

	/**
	 * delete an annotation
	 * 
	 * @param id of the annotation to delete
	 * @throws AnnotationDatabaseException, AnnotationException, 
	 * 	AnnotationAlreadyReferencedException;
	 */
	public abstract void deleteAnnotation(String id) throws AnnotationDatabaseException,
			AnnotationException, AnnotationAlreadyReferencedException;

	/**
	 * lists all annotations for a given object id
	 * 
	 * @param objectId
	 * @return representation of the corresponding annotations
	 * @throws AnnotationDatabaseException, AnnotationException
	 */
	public abstract String listAnnotations(String objectId) throws AnnotationDatabaseException, 
			AnnotationException;

	/**
	 * retrieves the number of annotations for the given object id
	 * 
	 * @param objectId
	 * @return number of annotations
	 * @throws AnnotationDatabaseException, AnnotationException
	 */
	public abstract int countAnnotations(String objectId) throws AnnotationDatabaseException, 
			AnnotationException;
	
	/**
	 * lists all replies (the whole subtree) for the given root annotation
	 * 
	 * @param id annotation id
	 * @return representation of the corresponding annotations (replies)
	 * @throws AnnotationDatabaseException, AnnotationException
	 */
	public abstract String listAnnotationReplies(String id) throws AnnotationDatabaseException, 
			AnnotationException;

	/**
	 * list all linked annotations to the given object
	 * 
	 * @param linkedObjectId
	 * @return representation of the linked annotation
	 * @throws AnnotationDatabaseException
	 * @throws AnnotationException
	 */
	public abstract String listLinkedAnnotations(String linkedObjectId)
			throws AnnotationDatabaseException, AnnotationException;
	
	/**
	 * returns the number of all linked annotations to the given object
	 * 
	 * @param linkedObjectId
	 * @return number of annotations
	 * @throws AnnotationDatabaseException
	 * @throws AnnotationException
	 */
	public abstract int countLinkedAnnotations(String linkedObjectId) 
			throws AnnotationDatabaseException, AnnotationException;
	
	/**
	 * find an annotation by id
	 * 
	 * @param id
	 * @return representation of the corresponding annotation or null if not found
	 * @throws AnnotationDatabaseException, AnnotationException
	 */
	public abstract String findAnnotationById(String id) throws AnnotationDatabaseException, 
			AnnotationNotFoundException, AnnotationException;

	/**
	 * find an annotation body by id
	 * 
	 * @param id
	 * @return representation of the corresponding annotation body or null if not found
	 * @throws AnnotationDatabaseException, AnnotationException
	 */
	public abstract String findAnnotationBodyById(String id) throws AnnotationDatabaseException, 
			AnnotationNotFoundException, AnnotationException;
	
	/**
	 * find annotations that match the given search term
	 * 
	 * @param search term
	 * @return representation of the found annotations
	 * @throws AnnotationDatabaseException
	 */
	public abstract String findAnnotations(String term) throws AnnotationDatabaseException,
			AnnotationException;

}