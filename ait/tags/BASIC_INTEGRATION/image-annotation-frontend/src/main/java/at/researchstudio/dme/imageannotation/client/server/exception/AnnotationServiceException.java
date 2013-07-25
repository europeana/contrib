package at.researchstudio.dme.imageannotation.client.server.exception;

import java.io.Serializable;

public class AnnotationServiceException extends Exception implements Serializable {
	private static final int HTTP_CONFLICT = 409;
	private static final long serialVersionUID = -1021896871854802248L;

	private int statusCode;
	
	public AnnotationServiceException() {
		
	}
	
	public AnnotationServiceException(int statusCode) {
		super("unexpected response:"+statusCode);
		this.statusCode=statusCode;		
	}
	
	public AnnotationServiceException(String message) {
		super(message);
	}
	
	public boolean isConflict() {
		return statusCode==HTTP_CONFLICT;
	}
}
