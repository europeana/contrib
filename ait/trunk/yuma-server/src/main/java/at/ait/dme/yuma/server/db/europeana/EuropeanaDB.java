package at.ait.dme.yuma.server.db.europeana;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.log4j.Logger;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.util.HttpResponseCodes;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.rdf.lemo.LEMOFormatHandler;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationHasReplyException;
import at.ait.dme.yuma.server.exception.AnnotationModifiedException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.AnnotationTree;

/**
 * Adapter class that implements an AbstractAnnotationDB
 * front-end to the remote Europeana Annotation API.
 * 
 * Please note that, compared to the annotation server in general,
 * the Europeana API supports only a bare minimum of features.
 * This implementation makes available those features that the
 * API supports, and throws RuntimeExceptions for all others. 
 * 
 * @author Rainer Simon
 */
public class EuropeanaDB extends AbstractAnnotationDB {
	
	/**
	 * String constants
	 */
	private static final String UTF8 = "UTF-8";
	private static final String SORRY_NOT_SUPPORTED = "Sorry - not supported by the Europeana API";

	/**
	 * The URL to the Europeana Annotation API
	 */
	private static String EUROPEANA_API_URL;
	
	/**
	 * The calling Servlet request
	 */
	private HttpServletRequest request = null;
	
	/**
	 * The response to the calling Servlet request
	 */
	private HttpServletResponse response = null;
	
	/**
	 * Log4J logger
	 */
	private Logger log = Logger.getLogger(EuropeanaDB.class);
	
	@Override
	public void init() throws AnnotationDatabaseException {
		final Config config = Config.getInstance();
		EUROPEANA_API_URL = "http://" + config.getDbHost() + ":" + config.getDbPort() + "/";
	}

	@Override
	public void shutdown() {
		// Nothing to do
	}

	@Override
	public void connect(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@Override
	public void disconnect() {
		// Nothing to do
	}

	@Override
	public void commit() {
		// Nothing to do
	}

	@Override
	public void rollback() {
		throw new RuntimeException(SORRY_NOT_SUPPORTED);
	}

	@Override
	public String createAnnotation(Annotation annotation)
			throws AnnotationDatabaseException, AnnotationModifiedException,
			InvalidAnnotationException {
		
		EuropeanaAnnotationAPI api = getAPI();       
        ClientResponse<String> response = null;
        try {
        	LEMOFormatHandler lemoFormat = new LEMOFormatHandler();
        	
        	if (annotation.getParentId() == null) {
        		response = api.createAnnotation(
        				encode(getEuropeanaURI(annotation.getObjectUri())),
        				lemoFormat.serialize(annotation));
        	} else {
        		response = api.createAnnotationReply(
        				encode(getEuropeanaURI(annotation.getParentId())),
        				lemoFormat.serialize(annotation));
        	}
        	
			if (response.getStatus() != HttpResponseCodes.SC_CREATED)
				throw new AnnotationDatabaseException("Error " + response.getStatus());

			return response.getEntity();
        } catch (Exception e) {
        	throw new AnnotationDatabaseException(e);
        } finally {
        	forwardResponseHeaders(response.getHeaders());
        }
	}

	@Override
	public String updateAnnotation(String annotationId, Annotation annotation)
			throws AnnotationDatabaseException, AnnotationNotFoundException,
			AnnotationHasReplyException, InvalidAnnotationException {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAnnotation(String annotationId)
			throws AnnotationDatabaseException, AnnotationNotFoundException,
			AnnotationHasReplyException {
		
		// TODO Auto-generated method stub
	}

	@Override
	public AnnotationTree findAnnotationsForObject(String objectUri)
			throws AnnotationDatabaseException {
		
		LEMOFormatHandler lemoFormat = new LEMOFormatHandler();
		List<Annotation> annotations = new ArrayList<Annotation>();	
		ClientResponse<String> response = null;
		try {	
			response = getAPI().listAnnotations(encode(getEuropeanaURI(objectUri)));	
			
			if (response.getStatus() != HttpResponseCodes.SC_OK && 
					response.getStatus() != HttpResponseCodes.SC_NOT_FOUND)				
				throw new AnnotationDatabaseException("Error " + response.getStatus());		
				
			
			// Parse the IDs and fetch each annotation
			String[] ids = response.getEntity().split("\\n");			
			for (String id : ids) {
				if (!id.isEmpty()) {
					response = getAPI(response.getHeaders()).findAnnotationById(id);

					if (response.getStatus() != HttpResponseCodes.SC_OK)
						throw new AnnotationDatabaseException("Error " + response.getStatus());
					
					annotations.add(lemoFormat.parse(response.getEntity()));									
				}
			}
					
			/* Annotations in Europeana point to europeanaURIs! So we need
			// to remove the annotations that address a different image
			List<Annotation> notNeeded = new ArrayList<Annotation>();
			for (Annotation annotation : annotations) {
				if(!annotation.getObjectUri().equals(objectUri))
					notNeeded.add(annotation);
			}
			annotations.removeAll(notNeeded);
			*/
		} catch (AnnotationDatabaseException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null)
				forwardResponseHeaders(response.getHeaders());			
		}
		
		return new AnnotationTree(annotations);
	}

	@Override
	public long countAnnotationsForObject(String objectUri)
			throws AnnotationDatabaseException {

		AnnotationTree tree = findAnnotationsForObject(objectUri);
		return tree.asFlatList().size();
	}

	@Override
	public List<Annotation> findAnnotationsForUser(String username)
			throws AnnotationDatabaseException {

		throw new RuntimeException(SORRY_NOT_SUPPORTED);
	}

	@Override
	public Annotation findAnnotationById(String annotationId)
			throws AnnotationDatabaseException, AnnotationNotFoundException {

		ClientResponse<String> response = null;
		try {
			EuropeanaAnnotationAPI api = getAPI();
			response = api.findAnnotationById(annotationId);		
			
			int status = response.getStatus();
			if (status == HttpResponseCodes.SC_OK) {
				try {
					return new LEMOFormatHandler().parse(response.getEntity());
				} catch (InvalidAnnotationException e) {
					throw new AnnotationDatabaseException(e);
				}
			} else if (status == HttpResponseCodes.SC_NOT_FOUND) {
				throw new AnnotationNotFoundException();
			} else {
				throw new AnnotationDatabaseException("Error " + response.getStatus() + "\n"
						+ response.getEntity());
			}
		} finally {
			if (response != null)
				forwardResponseHeaders(response.getHeaders());
		}
	}

	@Override
	public long countReplies(String annotationId)
			throws AnnotationDatabaseException {

		throw new RuntimeException(SORRY_NOT_SUPPORTED);
	}

	@Override
	public AnnotationTree getReplies(String annotationId)
			throws AnnotationDatabaseException, AnnotationNotFoundException {

		throw new RuntimeException(SORRY_NOT_SUPPORTED);
	}

	@Override
	public List<Annotation> getMostRecent(int n, boolean publicOnly)
			throws AnnotationDatabaseException {

		throw new RuntimeException(SORRY_NOT_SUPPORTED);
	}

	@Override
	public List<Annotation> findAnnotations(String query)
			throws AnnotationDatabaseException {
		
		throw new RuntimeException(SORRY_NOT_SUPPORTED);
	}

	private EuropeanaAnnotationAPI getAPI() {
		return getAPI(null);
	}
	
	private EuropeanaAnnotationAPI getAPI(MultivaluedMap<String, String> headers) {
		HttpClient client = new HttpClient();
		List<String> cookieHeaders = (headers != null) ? headers
				.get("Set-Cookie") : new ArrayList<String>();
		
		// Forward all cookies from the calling request
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {			
			c.setDomain(request.getServerName());
			c.setPath("/");

			String value = c.getValue();
			for (String cookieHeader : cookieHeaders) {
				if (cookieHeader.startsWith(c.getName())) {
					String cookieHeaderParts[] = cookieHeader.split("=");
					if(cookieHeaderParts.length >= 2)
						value = cookieHeaderParts[1];
				}
			}
			org.apache.commons.httpclient.Cookie apacheCookie =
				new org.apache.commons.httpclient.Cookie(
						c.getDomain(), c.getName(), value,
						c.getPath(), c.getMaxAge(), c.getSecure());

			client.getState().addCookie(apacheCookie);			
		}
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
				
		return ProxyFactory.create(EuropeanaAnnotationAPI.class, EUROPEANA_API_URL,
				new ApacheHttpClientExecutor(client));		
	}

	private void forwardResponseHeaders(MultivaluedMap<String, String> headers) {
		if (headers==null) return;
		
		for (String key : headers.keySet()) {
			for (String value : headers.get(key)) {						
				response.addHeader(key, value);
			}
		}
	}
	
	private String getEuropeanaURI(String objectURI) {
		/*
		String europeanaUri
			= objectURI.substring(objectURI.lastIndexOf("europeanaURI") + 11);
		
		if (europeanaUri.lastIndexOf('&') > -1)
			europeanaUri = europeanaUri.substring(0, europeanaUri.lastIndexOf('&'));
		
		return europeanaUri;*/
		return "http://www.europeana.eu/resolve/record/92001/9C1BB12A62C6908E10DF37101DF222FBFFD1168C";
	}
	
	private String encode(String s) {
		try {
			return URLEncoder.encode(s, UTF8).replace("%", "%25");
		} catch (UnsupportedEncodingException e) {
			log.fatal(e);
			return s;
		}
	}

}
