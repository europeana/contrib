package at.ait.dme.yuma.server;

import java.net.URI;
import java.net.URISyntaxException;

import at.ait.dme.yuma.server.config.Config;

/**
 * Utility class for building annotation URIs.
 * 
 * @author Rainer Simon
 */
public class URIBuilder {
	
	private static final String ERROR = "Could not build URI for ID ";
	private static final String PATH_TO_ANNOTATION = "api/annotation/";
	
	public static URI toURI(String annotationID) {
		try {
			return new URI(Config.getInstance().getServerBaseUrl() + PATH_TO_ANNOTATION + annotationID);
		} catch (URISyntaxException e) {
			// Should never happen
			throw new RuntimeException(ERROR + annotationID);
		}
	}
	
	public static String toID(String uri) throws URISyntaxException {
		if (!uri.contains(PATH_TO_ANNOTATION)) {
			throw new URISyntaxException(uri, "Not a valid annotation uri");
		}
		return uri.substring(uri.lastIndexOf("/") + 1);
	}
	
}
