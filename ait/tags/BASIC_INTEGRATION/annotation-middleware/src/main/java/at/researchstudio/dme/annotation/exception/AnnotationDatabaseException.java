package at.researchstudio.dme.annotation.exception;

/**
 * This Exception indicates a problem with the actual database.
 * 
 * @author Christian Sadilek
 */
public class AnnotationDatabaseException extends Exception {
	private static final long serialVersionUID = 3328621278141052963L;

	public AnnotationDatabaseException() {
		super();
	}

	public AnnotationDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationDatabaseException(String message) {
		super(message);
	}

	public AnnotationDatabaseException(Throwable cause) {
		super(cause);
	}
}
