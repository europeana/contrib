package at.ait.dme.yuma.suite.framework.auth;

import java.security.MessageDigest;

/**
 * Utility class that generates the hex format md5 of an input string, as required by
 * Gravatar (see http://de.gravatar.com/site/implement/images/java/).
 */
public class MD5Util {
	
	/**
	 * String constants
	 */
	private static final String MD5 = "MD5";
	private static final String CP1252 = "CP1252";

	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));        
		}
		return sb.toString();
	}
	
	public static String md5Hex(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance(MD5);
			return hex(md.digest(message.getBytes(CP1252)));
		} catch (Exception e) {
			//
		}
		return null;
	}
	
}
