package at.researchstudio.dme.imageannotation.client.server;

import at.researchstudio.dme.imageannotation.client.user.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * asynchronous interface to the authentication service
 * 
 * @author Christian Sadilek
 * @see AuthenticationService
 */
public interface AuthenticationServiceAsync {
	public void authenticate(String authToken, String signature, AsyncCallback<User> callback);
}
