package at.ait.dme.yuma.server.exception;

/**
 * This Exception indicates a problem when parsing/processing an 
 * annotation, or is thrown in case an annotation is internally
 * inconsistent.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class InvalidAnnotationException extends Exception {
	
	private static final long serialVersionUID = -6104005265727225396L;

	public InvalidAnnotationException() {
		super();
	}

	public InvalidAnnotationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidAnnotationException(String message) {
		super(message);
	}

	public InvalidAnnotationException(Throwable cause) {
		super(cause);
	}
}
