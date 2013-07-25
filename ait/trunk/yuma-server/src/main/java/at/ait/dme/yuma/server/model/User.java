package at.ait.dme.yuma.server.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import at.ait.dme.yuma.server.exception.InvalidAnnotationException;

public class User implements Serializable {
	
	private static final long serialVersionUID = 4246122693082846836L;

	private String username = null;
	
	private String gravatarHash = null;
	
	private String uri = null;
	
	public User(String username) {
		this.username = username;
	}
	
	public User(Map<String, String> map) throws InvalidAnnotationException {
		username = map.get(MapKeys.USER_NAME);
		if (username == null)
			throw new InvalidAnnotationException();
		
		gravatarHash = map.get(MapKeys.USER_GRAVATAR_HASH);
		uri = map.get(MapKeys.USER_URI);
	}

	public String getUsername() {
		return username;
	}

	public void setGravatarHash(String gravatarHash) {
		this.gravatarHash = gravatarHash;
	}

	public String getGravatarHash() {
		return gravatarHash;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
	public Map<String, String> toMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(MapKeys.USER_NAME, username);
		
		if (gravatarHash != null)
			map.put(MapKeys.USER_GRAVATAR_HASH, gravatarHash);
		
		if (uri != null)
			map.put(MapKeys.USER_URI, uri);
		
		return map;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User))
			return false;
		
		User user = (User) other;
		
		if (this.username != user.username)
			return false;
		
		if (!equalsNullable(this.uri, user.uri))
			return false;
		
		if (!equalsNullable(this.gravatarHash, user.gravatarHash))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.username.hashCode();
	}
	
	private boolean equalsNullable(Object a, Object b) {
		if (a == null)
			return b == null;
		
		if (b == null)
			return a == null;
		
		return a.equals(b);
	}

}
