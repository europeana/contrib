package at.researchstudio.dme.imageannotation.client.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * represents a user of the image annotation service as part
 * of the prototypical authentication service. we won't create
 * separate entities for role and permissions at the moment.
 * 
 * @author Christian Sadilek
 * @see AuthenticationService
 */
public class User implements Serializable {
	private static final long serialVersionUID = -6599850969311474363L;

	private String name = "";
	private String password = null;
	private String app = null;
	
	private boolean admin;
	
	private Collection<String> roles = null;	
	private Collection<String> permissions = null;
	
	public User() {}
	
	public User(String name) {
		this.name = name;
	}
	
	public User(String name, boolean admin) {
		this(name);
		this.admin = admin;
	}
	
	public User(String name, String password, boolean admin) {
		this(name, admin);
		this.password = password;
	}

	public User(String name, String password, String app, boolean admin) {
		this(name, password, admin);
		this.app = app;
	}

	public User(String name, String password, boolean admin, 
			Collection<String> roles, Collection<String> permissions) {
		this(name, password, admin);
		this.roles = roles;
		this.permissions = permissions;
	}

	/**
	 * returns the name of the user
	 * 
	 * @return user name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the user
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the users' password in plain text!
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * sets the password
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * returns the application that hosts this user or null if it is
	 * an internal user.
	 * 
	 * @return user
	 */
	public String getApp() {
		return app;
	}

	/**
	 * sets the application that hosts the user
	 * 
	 * @param app
	 */
	public void setApp(String app) {
		this.app = app;
	}	
	
	/**	 
	 * @return true if the user is an administrator, otherwise false
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * sets the admin flag of the user
	 * 
	 * @param admin
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * returns the roles of the user
	 * 
	 * @return roles
	 */
	public Collection<String> getRoles() {
		return roles;
	}

	/**
	 * adds the role
	 * 
	 * @param role
	 */
	public void addRole(String role) {
		if(roles==null) roles = new ArrayList<String>();
		roles.add(role);					
	}

	/**
	 * returns the permissions of the user
	 * 
	 * @return permissions
	 */
	public Collection<String> getPermissions() {
		return permissions;
	}

	/**
	 * adds the permissions
	 * 
	 * @param permission
	 */
	public void addPermission(String permission) {
		if(permissions==null) permissions = new ArrayList<String>();
		permissions.add(permission);
	}
}
