package at.researchstudio.dme.imageannotation.client.server.exception;

import java.io.Serializable;

public class InvalidImageException extends Exception implements Serializable {
	private static final long serialVersionUID = -1021896871854802248L;

	public InvalidImageException() {
		
	}
	
	public InvalidImageException(String message) {
		super(message);
	}
}
