package at.ait.dme.yuma.server.db.hibernate.entities;

import javax.persistence.Embeddable;

import at.ait.dme.yuma.server.model.User;

@Embeddable
public class UserEntity {

	private String createdBy = null;
	
	private String createdByGravatarHash = null;
	
	private String createdByURI = null;
	
	public UserEntity() { }
	
	public UserEntity(User user) {
		this.setUsername(user.getUsername());
		this.setGravatarHash(user.getGravatarHash());
		this.setUri(user.getUri());
	}
	
	public User toUser() {
		User user = new User(createdBy);
		user.setGravatarHash(createdByGravatarHash);
		user.setUri(createdByURI);
		return user;
	}

	public void setUsername(String username) {
		this.createdBy = username;
	}

	public String getUsername() {
		return createdBy;
	}

	public void setGravatarHash(String gravatarHash) {
		this.createdByGravatarHash = gravatarHash;
	}

	public String getGravatarHash() {
		return createdByGravatarHash;
	}

	public void setUri(String uri) {
		this.createdByURI = uri;
	}

	public String getUri() {
		return createdByURI;
	}
	
}
