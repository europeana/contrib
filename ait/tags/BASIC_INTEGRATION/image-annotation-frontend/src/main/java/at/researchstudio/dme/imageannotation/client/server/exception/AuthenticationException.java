package at.researchstudio.dme.imageannotation.client.server.exception;

import java.io.Serializable;

public class AuthenticationException extends Exception implements Serializable {
	private static final long serialVersionUID = 436546556735034687L;

	public AuthenticationException() {
		
	}
	
	public AuthenticationException(String message) {
		super(message);
	}	
	
	public AuthenticationException(Throwable cause) {
		super(cause);
	}
}
