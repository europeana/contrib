package at.ait.dme.yuma.server.exception;

/**
 * This exception indicates that an annotation has been 
 * concurrently modified and should be reread by
 * the client.
 * 
 * @author Christian Sadilek
 */
public class AnnotationModifiedException extends Exception {	
	
	private static final long serialVersionUID = 543957402328399775L;

	public AnnotationModifiedException() {
		super();
	}

	public AnnotationModifiedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationModifiedException(Long id) {
		super("annotation has beed modified:"+id);
	}
	
	public AnnotationModifiedException(String message) {
		super("annotation has beed modified:"+message);
	}

	public AnnotationModifiedException(Throwable cause) {
		super(cause);
	}
}
