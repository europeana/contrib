package at.researchstudio.dme.imageannotation.server.util;

import java.io.UnsupportedEncodingException;

/**
 * Encodes a URL conforming to http://www.ietf.org/rfc/rfc3986.txt.
 * See Section 2.4: % => %25
 * 
 * @author Christian Sadilek
 */
public class URLEncoder {

	public static String encode(String url) throws UnsupportedEncodingException {
		// yes, this will always be UTF-8.
		return java.net.URLEncoder.encode(url,"UTF-8").replace("%", "%25");
	}
}
