package at.researchstudio.dme.annotation.exception;

/**
 * This exception indicates that an annotation cannot be changed because
 * it has already been referenced.
 * 
 * @author Christian Sadilek
 *
 */
public class AnnotationAlreadyReferencedException extends Exception {
	private static final long serialVersionUID = 3843311695698544428L;

	public AnnotationAlreadyReferencedException() {		
		super("annotation already referenced");
	}

	public AnnotationAlreadyReferencedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationAlreadyReferencedException(String message) {
		super(message);
	}

	public AnnotationAlreadyReferencedException(Throwable cause) {
		super(cause);
	}
}
