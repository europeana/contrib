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

package at.ait.dme.yuma.server.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import at.ait.dme.yuma.client.server.AuthenticationService;
import at.ait.dme.yuma.client.server.exception.AuthenticationException;
import at.ait.dme.yuma.client.user.User;
import at.ait.dme.yuma.server.util.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * implementation of the authentication service
 * 
 * @author Christian Sadilek
 * @see AuthenticationService
 */
public class AuthenticationServiceImpl extends RemoteServiceServlet implements
		AuthenticationService {
	private static final long serialVersionUID = 4125068959701420344L;

	private static Logger logger = Logger.getLogger(AuthenticationServiceImpl.class);
	private static final String KEYSTORE_DIRECTORY_PROPERTY = "authentication.keystore.dir";
	private static final String SIGN_VALIDITY_PERIOD_PROPERTY = "sign.validity.period.seconds";
	private static final String ADMIN_PAGE_PROPERTY = "admin.page";
	
	private static final String MY_PRIVATE_KEY_NAME = "private";
	private static final int KEY_SIZE = 128; 
	private static final String AUTH_TOKEN_STRING_ENCODING ="UTF-8";
	private static final String ALGORITHM ="RSA";
	
	private static int signValidityPeriodInSeconds = 30; 	
	private static String keyStoreDirectory = null;
	private static boolean adminPage = false;
	private ConcurrentHashMap<String, byte[]> keys = new ConcurrentHashMap<String, byte[]>();

	/**
	 * reads the key store directory from the servlet context. in case it's not
	 * found there it tries to read it from the property file.
	 */
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		Config config = new Config(servletConfig, 
				getClass().getResourceAsStream("authentication-service.properties"));
		adminPage=config.getBooleanProperty(ADMIN_PAGE_PROPERTY);
		signValidityPeriodInSeconds=config.getIntegerProperty(SIGN_VALIDITY_PERIOD_PROPERTY);
		keyStoreDirectory=config.getStringProperty(KEYSTORE_DIRECTORY_PROPERTY);

	}

	@Override
	public User authenticate(String authToken, String signature) throws AuthenticationException {		
		try {
			User user = authenticateAdmin();
			if(user!=null) return user;
			
			// decrypt auth token and create user
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);			
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(getKey(MY_PRIVATE_KEY_NAME));
		    PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);
		    		    
            String plainAuthToken = decrypt(authToken, privateKey);
            user = createUser(plainAuthToken);
            
            // validate signature
            boolean appSignValid = false;
            
    		// decrypt the signature
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(getKey(user.getApp()));
            PublicKey appKey = keyFactory.generatePublic(pubKeySpec);
            String plainAppSign = decrypt(signature, appKey); 
            
            JSONObject jsonAppSign=(JSONObject)JSONValue.parse(plainAppSign);
    		// the app name in the signature has to conform to the app name in the authToken
            if(user.getApp().equals((String)jsonAppSign.get("app"))) {
	    		String appSign=(String)jsonAppSign.get("appSign");	            
	            // the appSign consists of username and timestamp
	            String[] appSignParts=appSign.split("_");
	            if(appSignParts!=null&&appSignParts.length==2) {
	                // the user name in the signature has to conform to the user name in the 
	            	// auth token
	                if(appSignParts[0].equals(user.getName())) {
	            		// the timestamp must not be expired
	                	Calendar expiry = Calendar.getInstance();
	            		expiry.setTimeInMillis(new Long(appSignParts[1]));            		
	            		expiry.add(Calendar.SECOND, signValidityPeriodInSeconds);
	            		if(Calendar.getInstance().before(expiry))
	            			appSignValid=true;            		
	            	}
	            }
    		}
            if(!appSignValid)
            	throw new AuthenticationException("invalid signature");
			
			// TODO we bind the user to the session. later when we deactivate
            // anonymous and unsafe access we can check for the user
            // in a servlet filter.
            bindUserToSession(user);
    		return user;
		} catch (GeneralSecurityException gse) {
			logger.info("failed to generate key", gse);
			throw new AuthenticationException(gse);
		} catch (Throwable t) {
			logger.fatal(t.getMessage(), t);
			throw new AuthenticationException(t);
		}
	}

	private User authenticateAdmin() {
		User user = null;
		if(adminPage) {
			user = new User("admin", true);
			bindUserToSession(user);
		}
		return user;
	}
	
	private void bindUserToSession(User user) {
		if(getThreadLocalRequest()!=null)
        	getThreadLocalRequest().getSession().setAttribute("user", user);
	}
	
	/**
	 * add a key 
	 * 
	 * @param name of the key
	 * @param key
	 */
	public void addKey(String name, byte[] key) {
		keys.putIfAbsent(name, key);
	}
	
	/**
	 * reads the key either from cache or from file if it was never read. this
	 * allows for hot deployment of new keys.
	 * 
	 * @param keyName
	 * @return key
	 * @throws IOException
	 */
	private byte[] getKey(String keyName) throws IOException {
		keyName = keyName.toLowerCase();

		FileInputStream pkIn = null;
		byte[] key = null;

		try {
			if ((key = keys.get(keyName)) == null) {
				pkIn = new FileInputStream(keyStoreDirectory + "/" + keyName);
				key = new byte[pkIn.available()];
				pkIn.read(key);
				keys.putIfAbsent(keyName, key);
			}
		} finally {
			if (pkIn != null)
				pkIn.close();
		}

		return key;
	}
	
	/**
	 * decrypt the token with the given key
	 * 
	 * @param token
	 * @param key
	 * @return decrypted token
	 * 
	 * @throws Exception
	 */
	private String decrypt(String token, Key key) throws Exception {
		byte[] cipherText = new BigInteger(token, 16).toByteArray();
		byte[] plainText = new byte[cipherText.length];

		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		int outOffset = 0;		
		for (int inOffset=0,inLen=0; (inLen=cipherText.length-inOffset)>0; inOffset += KEY_SIZE) {			
			outOffset += cipher.doFinal(cipherText, inOffset, Math.min(KEY_SIZE, inLen), 
					plainText, outOffset);
		}
		
		return new String(plainText,0,outOffset,AUTH_TOKEN_STRING_ENCODING);
	}
	
	/**
	 * creates a user representation based on the json structure
	 * 
	 * @param decrypted authToken
	 * @return user
	 */
	private User createUser(String authToken) {
		JSONObject jsonAuthToken=(JSONObject)JSONValue.parse(authToken);
   		String name=(String)jsonAuthToken.get("user");
   		String password=(String)jsonAuthToken.get("pwd");   		
   		String app=(String)jsonAuthToken.get("app");        
   		Boolean admin=Boolean.valueOf((String)jsonAuthToken.get("admin"));

   		User user = new User(name,password,app,admin);
   		
   		JSONArray jsonRoles=(JSONArray)jsonAuthToken.get("roles");		
   		if(jsonRoles!=null) {
   			for(Object jsonRole : jsonRoles) {
   				user.addRole((String)jsonRole);
   			}
   		}
		
   		JSONArray jsonPermissions=(JSONArray)jsonAuthToken.get("permissions");		
   		if(jsonRoles!=null) {
   			for(Object jsonPermission : jsonPermissions) {
   				user.addPermission((String)jsonPermission);
   			}
   		}   		   		      
   		return user;
	}
}
