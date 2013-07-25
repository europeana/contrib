package at.ait.dme.yuma.server.db;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.AnnotationTree;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationModifiedException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.exception.AnnotationHasReplyException;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;

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
 * updates/removals. If a user creates an annotation with a reference to
 * another annotation it has to be ensured that the referenced annotation has
 * not been changed in the time between the last read (see {@link AnnotationModifiedException}). 
 * Further, on update and delete it has to be ensured that the corresponding annotation 
 * is still unreferenced (see {@link AnnotationHasReplyException}). A referenced
 * annotation cannot be updated or deleted.
 *
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public abstract class AbstractAnnotationDB {	
	
	protected static final String UNEXPECTED_RESPONSE = "unexpected response";		
	protected static final String FAILED_TO_READ_ANNOTATION = "failed to read annotation";	
	protected static final String FAILED_TO_SAVE_ANNOTATION = "failed to save annotation";
	protected static final String FAILED_TO_DELETE_ANNOTATION = "failed to delete annotation";
	protected static final String FAILED_TO_PARSE_ANNOTATION = "failed to parse annotation";

	private boolean autoCommit = true;
	
	/**
	 * Check if auto commit is on
	 * @return true if auto commit is on
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}
	
	/**
	 * Set the auto commit mode
	 * @param autoCommit auto commit mode
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	
	/**
	 * Initialize 
	 * @throws AnnotationDabaseException if anything goes wrong
	 */
	public abstract void init()
		throws AnnotationDatabaseException;
	
	/**
	 * Shutdown 
	 */
	public abstract void shutdown();
	
	/**
	 * Connect to the DB
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public void connect()
		throws AnnotationDatabaseException {
		
		connect(null, null);
	}

	/**
	 * Connect to the DB and provide a request object to access
	 * parameters, cookies, etc.
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract void connect(HttpServletRequest request, HttpServletResponse response)
		throws AnnotationDatabaseException;
	
	/**
	 * Disconnect the DB 
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract void disconnect();

	/**
	 * Commit all changes
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract void commit()
		throws AnnotationDatabaseException;
	
	/**
	 * Roll back all changes
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract void rollback()
		throws AnnotationDatabaseException;
	
	/**
	 * Create a new annotation
	 * @param annotation the annotation
	 * @return the ID of the new annotation
	 * @throws AnnotationDatabaseException if anything goes wrong
	 * @throws AnnotationModifiedException if the parent annotation was modified in the mean time
	 */
	public abstract String createAnnotation(Annotation annotation) 
			throws AnnotationDatabaseException, AnnotationModifiedException, InvalidAnnotationException;

	/**
	 * Update an annotation
	 * @param annotationId the ID of the annotation
	 * @param annotation the annotation
	 * @return the (new) annotation ID after the update (may change depending on DB implementation!)
	 * @throws AnnotationDatabaseException if anything goes wrong
	 * @throws AnnotationNotFoundException if there is no annotation with the given ID
	 * @throws AnnotationHasReplyException if this annotation has already been replied to
	 */
	public abstract String updateAnnotation(String annotationId, Annotation annotation)
		throws AnnotationDatabaseException, AnnotationNotFoundException, AnnotationHasReplyException, InvalidAnnotationException;

	/**
	 * Delete an annotation
	 * @param annotationId the annotation ID
	 * @throws AnnotationDatabaseException  if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation does not exist in the DB
	 * @throws AnnotationHasReplyException if this annotation has already been replied to
	 */
	public abstract void deleteAnnotation(String annotationId)
		throws AnnotationDatabaseException, AnnotationNotFoundException, AnnotationHasReplyException;

	/**
	 * Returns all annotations for a given object
	 * @param objectUri the object URI
	 * @return the annotation tree for the object
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract AnnotationTree findAnnotationsForObject(String objectUri)
		throws AnnotationDatabaseException;

	/**
	 * Retrieves the number of annotations for the given object
	 * @param objectUri the object ID
	 * @return the number of annotations for this object
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract long countAnnotationsForObject(String objectUri)
		throws AnnotationDatabaseException; 
		
	/**
	 * Retrieves the annotations for the given user
	 * @param username the user name
	 * @return the annotations
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract List<Annotation> findAnnotationsForUser(String username)
		throws AnnotationDatabaseException;

	/**
	 * Retrieve an annotation by ID
	 * @param annotationId the annotation ID
	 * @return the annotation
	 * @throws AnnotationDatabaseException if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation was not found
	 */
	public abstract Annotation findAnnotationById(String annotationId)
		throws AnnotationDatabaseException, AnnotationNotFoundException;
	
	/**
	 * Retrieves the number of replies that exist for
	 * the given annotation
	 * @param annotationId the annotation ID
	 * @return the number of replies
	 * @throws AnnotationDatabaseException if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation was not found
	 */
	public abstract long countReplies(String annotationId)
		throws AnnotationDatabaseException;
	
	/**
	 * Retrieve the replies for the given annotation
	 * @param annotationId the annotation ID
	 * @return the replies
	 * @throws AnnotationDatabaseException if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation was not found
	 */
	public abstract AnnotationTree getReplies(String annotationId)
		throws AnnotationDatabaseException, AnnotationNotFoundException;

	/**
	/**
	 * Retrieves the N most recent annotations from the database.
	 * @param n the number of annotations to retrieve
	 * @param publicOnly if true, only annotations with public scope will be returned
	 * @return the annotations (as a flat list)
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract List<Annotation> getMostRecent(int n, boolean publicOnly)
		throws AnnotationDatabaseException;

	
	/**
	 * Find annotations that match the given search term
	 * @param query the query term
	 * @return the list of matching annotations
	 * @throws AnnotationDatabaseException if anything goes wrong
	 */
	public abstract List<Annotation> findAnnotations(String query) 
		throws AnnotationDatabaseException;
	
}