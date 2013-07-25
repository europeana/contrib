package at.ait.dme.yuma.server.exception;

/**
 * This exception indicates that an annotation cannot be changed because
 * it has already been replied to.
 * 
 * @author Christian Sadilek
 *
 */
public class AnnotationHasReplyException extends Exception {
	private static final long serialVersionUID = 3843311695698544428L;

	public AnnotationHasReplyException() {		
		super("Annotation has replies");
	}

	public AnnotationHasReplyException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationHasReplyException(String message) {
		super(message);
	}

	public AnnotationHasReplyException(Throwable cause) {
		super(cause);
	}
}
