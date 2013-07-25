package gr.ntua.ivml.awareness.persistent;
import gr.ntua.ivml.awareness.security.InheritableRole;
import gr.ntua.ivml.awareness.security.InheritableRoleHolder;
import gr.ntua.ivml.awareness.security.SecurityRole;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.google.code.morphia.annotations.*;
import com.google.code.morphia.utils.IndexDirection;

import java.nio.charset.Charset;
import java.security.MessageDigest;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;



import be.objectify.deadbolt.models.*;

/* author Anna */

@Entity
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE)
@JsonSerialize(include = Inclusion.NON_NULL)
public class User implements InheritableRoleHolder{
		//available roles : admin, moderator, contributor,editor
		
 	    @Id
	    public ObjectId id;

	    /*@Indexed(value=IndexDirection.ASC, name="username", unique=true, dropDups=false) 
	    public String username;
       */
       
	    @Indexed(value=IndexDirection.ASC, name="email", unique=true, dropDups=false) 
	    public String email;
	    
	    public String username;
	    
	    @JsonIgnore
	    public String md5Password;
		public boolean accountActive;
		public Date passwordExpires;
		public Date accountCreated;
	    public String role;
	    public String address;
	    public String town;
	    public String country;	    
	  //  private enum Gender { M, F };	
	    public String age;	
	   // private Gender gender;
	    
	    @JsonIgnore
	    public List<SecurityRole> roles;
	    
	    
	    @JsonIgnore
	    public ObjectId getObjectId() {
			return id;
		}
	    
        
	    public String getId(){
	    	return id.toString();
	    }
	    
	    public boolean getAccountActive(){
	    	return accountActive;
	    }
		
	    public void setAccountActive(boolean active){
	    	accountActive=active;
	    }
	    
	    public void setId(ObjectId id) {
			this.id = id;
		}
		
	    @JsonIgnore
		private String getMd5Password() {
			return md5Password;
		}
		private void setMd5Password(String md5Password) {
			this.md5Password = md5Password;
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		
		public void setAddress(String address){
			this.address=address;
			
		}
		
		public Date getAccountCreated(){
			return this.accountCreated;
		}
		
		public String getAddress(){
			return this.address;
		}
		
		public void setTown(String town){
			this.town=town;
			
		}
		
		public String getTown(){
			return this.town;
		}
		
		public void setCountry(String country){
			this.country=country;
			
		}
		
		public String getCountry(){
			return this.country;
		}
		
		/* public String getGender()               
		 {  try{
		        return gender.toString(); 
		    }catch (NullPointerException ex){return "";}
		    
		 }
		    
		 public void setGender(String value)     
		 { try{
		   gender = Gender.valueOf(value);
		  }catch (NullPointerException ex){}
		  }
		*/
		
		
		public void setAge(String age){
			this.age=age;
		}
		
		public String getAge(){
			return this.age;
		}
		
		public boolean checkPassword( String password ) {
			StringBuffer sb = encrypt( username, password );
			if( md5Password.equals(sb.toString()))
				return true;
			else
				return false;
		}
		
		public void setRole(String role){
			this.role=role;
		}
		
		public String getRole(){
			return this.role;
			
		}
		/**
		 * Works only when login is already set!!
		 * @param password
		 */
		public void setNewPassword( String password ) {
			//log.debug("setNewPassword called"); 
			if( username == null ) 
				throw new Error( "Need login to be set" );
			StringBuffer sb = encrypt( username, password);
			setMd5Password( sb.toString());
		}
		
		/*public void encryptAndSetUsernamePassword( String username, String password ) {
			StringBuffer sb = encrypt( username, password);
			setMd5Password( sb.toString());
			setUsername( username );
		} */
		
		public void encryptAndSetEmailPassword( String email, String password ) {
			StringBuffer sb = encrypt( email, password);
			setMd5Password( sb.toString());
			setEmail( email );
		} 
		
		private StringBuffer encrypt( String salt, String password ) {
			StringBuffer sb = new StringBuffer();
			
			try {
				MessageDigest md = MessageDigest.getInstance( "MD5");
				md.update( salt.getBytes( Charset.forName( "UTF-8")));
				md.update( password.getBytes( Charset.forName( "UTF-8")));
				byte[] md5 = md.digest();
				for( byte b: md5 ) {
					int i = (b&0xff);
					if( i < 16 )
						sb.append( "0" );
					sb.append( Integer.toHexString(i));
				}
			} catch( Exception e ) {
				e.printStackTrace();
				throw new Error( "Cant recover ",e);
			}
			return sb;
		}
		
		@PrePersist
		public void prePersist() {
		 this.accountCreated = (accountCreated == null) ? new Date() : accountCreated;
		
		}

		

		
		@JsonIgnore
		public List<? extends Permission> getPermissions() {
			// TODO Auto-generated method stub
			return null;
		}


		
		@JsonIgnore
		public List<? extends Role> getRoles() {
			roles=new ArrayList<SecurityRole>();
			roles.add(new SecurityRole(this.getRole()));
			return roles;
		}
				
		@JsonIgnore
		public List<? extends InheritableRole> getInheritableRoles()
        {
		    
			return new SecurityRole(this.role).getInheritedRoles();
		}
		
		

}