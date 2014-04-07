import gr.ntua.ivml.mint.Custom;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.User;
import gr.ntua.ivml.mint.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


public class CustomBehaviour extends Custom {
	public static final Logger log = Logger.getLogger( CustomBehaviour.class);
	
	// login based on josso user principal
	public void customLogin( HttpServletRequest req ) {
		Object prince = req.getUserPrincipal();
		if( prince != null ) {
			String email = "";
			org.josso.gateway.identity.SSOUser ssoUser =
					(org.josso.gateway.identity.SSOUser) prince;
			for (int i = 0 ; i < ssoUser.getProperties().length ; i++) {
				if( "email".equals( ssoUser.getProperties()[i].getName()))
					email = ssoUser.getProperties()[i].getValue();
			}
			if( !StringUtils.empty(email)) {
				// retrieve user or make one, add it to session
				User user = DB.getUserDAO().getByEmail(email);
				if( user == null ) {
					// well, we make one
					user = new User();
			    	user.setAccountActive(true);
					user.setEmail(email);
					user.setFirstName("" );
					user.setLastName( email );
					user.encryptAndSetLoginPassword( email, "default" );
			        user.setJobRole( "" );	
			        user.setWorkTelephone("");
			        java.util.Date ucreated=new java.util.Date();
			        user.setAccountCreated(ucreated);
			        user.setMintRole("admin");
					DB.getUserDAO().makePersistent(user);
					DB.commit();
					log.debug( "SSO user generated [" + email + "]");
				}
				req.getSession().setAttribute("user", user);
			}
		}
	}
}
