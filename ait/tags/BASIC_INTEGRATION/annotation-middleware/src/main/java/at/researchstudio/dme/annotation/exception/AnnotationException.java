package at.researchstudio.dme.annotation.exception;

/**
 * This Exception indicates a problem when parsing/processing an annotation.
 * 
 * @author Christian Sadilek
 */
public class AnnotationException extends Exception {
	private static final long serialVersionUID = -6104005265727225396L;

	public AnnotationException() {
		super();
	}

	public AnnotationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationException(String message) {
		super(message);
	}

	public AnnotationException(Throwable cause) {
		super(cause);
	}
}
