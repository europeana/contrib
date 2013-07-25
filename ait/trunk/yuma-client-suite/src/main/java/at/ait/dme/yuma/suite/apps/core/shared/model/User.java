/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.suite.apps.core.shared.model;

import java.io.Serializable;

/**
 * A YUMA User.
 * 
 * @author Rainer Simon
 */
public class User implements Serializable {

	private static final long serialVersionUID = -6027774847347220416L;
	
	/**
	 * Screen name of the anonymous user
	 */
	private static final String ANONYMOUS_NAME = "guest";
	
	/**
	 * An anonymous user
	 */
	public static final User ANONYMOUS = new User(null);
	
	/**
	 * The user 'singleton' user for this GWT app
	 */
	private static User instance = null;

	/**
	 * The username
	 */
	private String username = null;
	
	/**
	 * The user's Gravatar hash (if any)
	 */
	private String gravatarHash = null;
	
	/**
	 * A URI for this user (if any)
	 */
	private String uri = null;
	
	public User() {}
	
	public User(String username) {
		this.username = username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		if (username == null)
			return ANONYMOUS_NAME;
		
		return username;
	}

	public void setGravatarHash(String hash) {
		this.gravatarHash = hash;
	}
	
	public String getGravatarHash() {
		return gravatarHash;
	}

	public String getGravatarURL() {
		if (gravatarHash == null)
			return "http://www.gravatar.com/avatar/?s=20&d=mm";
			
		return "http://www.gravatar.com/avatar/"
			 + gravatarHash + "?s=20&d=mm";
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
	public boolean isAnonymous() {
		return username == null;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User))
			return false;
		
		User user = (User) other;
		
		if (!equalsNullable(this.username, user.username))
			return false;
		
		if (!equalsNullable(this.gravatarHash, user.gravatarHash))
			return false;
		
		if (!equalsNullable(this.uri, user.uri))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return username.hashCode();
	}
	
	protected boolean equalsNullable(Object a, Object b) {
		if (a == null)
			return b == null;
		
		if (b == null)
			return a == null;
		
		return a.equals(b);
	}
	
	public static void set(User user) {
		instance = user;
	}
	
	public static User get() {
		return instance;
	}
	
}
