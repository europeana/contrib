package at.researchstudio.dme.annotation.exception;

/**
 * This exception indicates that an annotation is locked by another process
 * 
 * @author Christian Sadilek
 *
 */
public class AnnotationLockedException extends Exception {	
	private static final long serialVersionUID = 255701822359859726L;

	public AnnotationLockedException() {
		super();
	}

	public AnnotationLockedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationLockedException(String message) {
		super(message);
	}

	public AnnotationLockedException(Throwable cause) {
		super(cause);
	}
}
