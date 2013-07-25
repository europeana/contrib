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

package at.ait.dme.yuma.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.client.server.exception.AuthenticationException;
import at.ait.dme.yuma.client.user.User;
import at.ait.dme.yuma.server.auth.AuthenticationServiceImpl;

public class AuthenticationTest {

	private static Key externalAppPublicKey  = null;
	private static Key externalAppPrivateKey  = null;
	
	private static Key myPublicKey  = null;
	private static Key myPrivateKey  = null;
	
	private static final String VALID_AUTH_TOKEN = "{\"app\": \"someapp\", " +
			"\"user\": \"csa\", \"pwd\" : \"pass\", \"admin\": \"false\"" +
			", \"roles\" : [\"r1\",\"r2\"], \"permissions\" : [\"p1\",\"p2\"]}";
	
	private static final String INVALID_AUTH_TOKEN = "{\"app\": \"wrongapp\", "
			+ "\"user\": \"csa\", \"pwd\" : \"pass\", \"admin\": \"false\""
			+ ", \"roles\" : [\"r1\",\"r2\"], \"permissions\" : [\"p1\",\"p2\"]}";

	@BeforeClass
	public static void setUp() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair keypair = keyGen.genKeyPair();
		myPrivateKey = keypair.getPrivate();
		myPublicKey = keypair.getPublic();

		keypair = keyGen.genKeyPair();
		externalAppPrivateKey = keypair.getPrivate();
		externalAppPublicKey = keypair.getPublic();
	}
	
	@Test
	public void testAuthentication_validToken_validSignature() throws Exception {
		JSONObject jsonAuthToken=(JSONObject)JSONValue.parse(VALID_AUTH_TOKEN);
   		
   		String authToken=encrypt(VALID_AUTH_TOKEN, myPublicKey);
   		String signature = encrypt(
   				"{\"app\": \"someapp\", \"appSign\": \"csa_" + new Date().getTime()+ "\"}",
   				externalAppPrivateKey);
   		
   		AuthenticationServiceImpl as = new AuthenticationServiceImpl();
   		as.addKey("private", myPrivateKey.getEncoded());
   		as.addKey("someapp", externalAppPublicKey.getEncoded());
   		
   		User user = as.authenticate(authToken, signature);
		assertEquals(user.getApp(),(String)jsonAuthToken.get("app"));
		assertEquals(user.getName(),(String)jsonAuthToken.get("user"));
		assertEquals(user.getPassword(),(String)jsonAuthToken.get("pwd"));
		assertEquals(user.isAdmin(),Boolean.valueOf((String)jsonAuthToken.get("admin")));		
	}
	
	@Test
	public void testAuthentication_validToken_invalidSignature() throws Exception {
		AuthenticationServiceImpl as = new AuthenticationServiceImpl();
   		as.addKey("private", myPrivateKey.getEncoded());
   		as.addKey("someapp", externalAppPublicKey.getEncoded());
   		
   		String authToken = encrypt(VALID_AUTH_TOKEN, myPublicKey);
   		
   		// wrong key used
   		String signature = encrypt("{\"app\": \"someotherapp\", \"appSign\": \"csa_"
				+ new Date().getTime() + "\"}", myPublicKey);   		   	
   		try {
   			as.authenticate(authToken, signature);
   	   		fail("should throw AuthenticationException");
   		} catch(AuthenticationException ae) {}
   		
   		// wrong app name
		signature = encrypt("{\"app\": \"someotherapp\", \"appSign\": \"csa_"
				+ new Date().getTime() + "\"}", externalAppPrivateKey);   		   	
   		try {
   			as.authenticate(authToken, signature);
   	   		fail("should throw AuthenticationException");
   		} catch(AuthenticationException ae) {}
   		
   		// wrong user
   		signature = encrypt("{\"app\": \"someapp\", \"appSign\": \"wronguser_"
				+ new Date().getTime() + "\"}", externalAppPrivateKey);   		
   		try {
   			as.authenticate(authToken, signature);
   	   		fail("should throw AuthenticationException");
   		} catch(AuthenticationException ae) {}
   		
   		// wrong signature
   		signature = encrypt("{\"app\": \"someapp\", \"appSign\": \"user_\"}", externalAppPrivateKey);   		
   		try {
   			as.authenticate(authToken, signature);
   	   		fail("should throw AuthenticationException");
   		} catch(AuthenticationException ae) {}
   		
   		// expired timestamp
   		signature = encrypt("{\"app\": \"someapp\", \"appSign\": \"wronguser_"
				+ (new Date().getTime()+28800000l) + "\"}", externalAppPrivateKey);   						
   		try {
   			as.authenticate(authToken, signature);
   	   		fail("should throw AuthenticationException");
   		} catch(AuthenticationException ae) {}   		 
	}
	
	@Test
	public void testAuthentication_invalidToken_validSignature() throws Exception {
		AuthenticationServiceImpl as = new AuthenticationServiceImpl();
   		as.addKey("private", myPrivateKey.getEncoded());
   		as.addKey("someapp", externalAppPublicKey.getEncoded());
   		
   		//token with invalid application
   		String authToken = encrypt(INVALID_AUTH_TOKEN, myPublicKey);
   		String signature = encrypt(
   				"{\"app\": \"someapp\", \"appSign\": \"csa_" + new Date().getTime()+ "\"}",
   				externalAppPrivateKey);
   		try {
   			as.authenticate(authToken, signature);
   	   		fail("should throw AuthenticationException");
   		} catch(AuthenticationException ae) {}   			   		
	}		
	
	private String encrypt(String plainText, Key key) throws NoSuchAlgorithmException, 
		NoSuchPaddingException, InvalidKeyException, ShortBufferException, 
		IllegalBlockSizeException, BadPaddingException {
		
		byte[] plainTextBytes = plainText.getBytes();
		byte[] encText = new byte[(((plainTextBytes.length - 1) / 100 + 1)) * 128];

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		for (int inOffset = 0, outOffset = 0, inLen = 0; (inLen = plainTextBytes.length - inOffset) > 0; inOffset += 100) {
			outOffset += cipher.doFinal(plainTextBytes, inOffset, Math.min(100, inLen), encText,
					outOffset);
		}

		// also test serialization
		BigInteger bi = new BigInteger(encText);
		return bi.toString(16);
	}
}
