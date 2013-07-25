package org.theeuropeanlibrary.telplus.tpp.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.theeuropeanlibrary.telplus.rcm.RequestCountMonitor;
import org.theeuropeanlibrary.telplus.tpp.AsynchronousRequestHandler;
import org.theeuropeanlibrary.telplus.tpp.AsynchronousSessionManager;
import org.theeuropeanlibrary.telplus.tpp.AuthorisationManager;
import org.theeuropeanlibrary.telplus.tpp.BlockingSessionExistsException;
import org.theeuropeanlibrary.telplus.tpp.Configuration;
import org.theeuropeanlibrary.telplus.tpp.HTTPConnectionManager;
import org.theeuropeanlibrary.telplus.tpp.HTTPResponse;
import org.theeuropeanlibrary.telplus.tpp.NoPendingRequestsException;
import org.theeuropeanlibrary.telplus.tpp.SynchronousRequestHandler;
import org.theeuropeanlibrary.telplus.tpp.TPPRequestException;
import org.theeuropeanlibrary.telplus.tpp.TimeOutException;
import org.theeuropeanlibrary.telplus.tpp.UnknownSessionIdException;

/**
 * 
 * Main class for the TPP project. (And what a fine mess it has become!)
 * 
 * @author Michel Koppelaar
 *
 * Created on: 15 mei 2008
 */
public class TPPServlet extends HttpServlet {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static final String PROPERTIES_FILE = "/conf/tpp/tpp.properties";
	private static final String AUTHORISATION_FILE = "/conf/tpp/authorisation.xml";
	
	private static final String LIABILITY_URI = "/liability";
	private static final String LIABILITY_COOKIE = "liability-token";
	private static final String LIABILITY_MSG_STYLESHEET = "liability.xsl";
	private static final int LIABILITY_COOKIE_TTL = 86400; // TTL: one day
	
	private static final String SESSION_COOKIE = "session-id";
	
	private static final String ASYNCHRONOUS_URI = "/req";
	
	public static final String PROXY_STATUS_FIELD = "X-Status";
	public static final String NON_ZERO_PROXY_STATUS = "1";
	
	private static final int MAX_NUMBER_REQUESTS_PER_SECOND = 50;
	
	// number of requests we collect per client before we decide
	// we've got enough data to calculate the average number of requests
	// per second
	private static final int INITIAL_RCM_SAMPLE_SIZE = 50;
	
	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static AuthorisationManager authMgr;
	private Configuration configuration;
	private AsynchronousSessionManager asm;
	private RequestCountMonitor rcm;

	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static Logger logger = Logger.getLogger(TPPServlet.class);

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public void init() throws ServletException {
		asm = new AsynchronousSessionManager();
		try {
			File configurationFile = new File(URLDecoder.decode(getClass().getResource(PROPERTIES_FILE).getPath(), 
													Charset.defaultCharset().name()));
			File authorisationFile = new File(URLDecoder.decode(getClass().getResource(AUTHORISATION_FILE).getPath(),
													Charset.defaultCharset().name()));

			configuration = new Configuration(configurationFile, authorisationFile);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		authMgr = new AuthorisationManager(configuration.getAllowedBaseUrls(),
												configuration.getDeniedDomains(),
												configuration.getDeniedNetworks());
		rcm = new RequestCountMonitor(INITIAL_RCM_SAMPLE_SIZE);
		logger.info(this.getClass().getCanonicalName() + " version " + 
													getVersion() + " initialised");
	}
	

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	protected void service(HttpServletRequest req, HttpServletResponse resp)
											throws IOException, ServletException {
		
		OutputStream outputStream = resp.getOutputStream();
		
		String remoteAddress = req.getRemoteAddr();
		String liabilityCookieString = createLiabilityCookie(req);
		String sessionCookieString = createSessionCookie(req);
		String method = req.getMethod();
		String sessionId;
		
		// prevent poor man's DOS
		float numberOfRequestsPerSecond = rcm.getRequestsPerSecond(remoteAddress);
		logger.debug("Number of requests per second from " + remoteAddress + ": " 
										+ numberOfRequestsPerSecond);
		if (numberOfRequestsPerSecond > MAX_NUMBER_REQUESTS_PER_SECOND) {
			resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
			resp.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
			outputStream.write("Client exceeded maximum number of requests per second".
															getBytes());
			logger.warn("Too many requests per second (" 
													+ numberOfRequestsPerSecond + 
													") from " + remoteAddress);
			return;
		}
				
		// is this an asynchronous request?
		boolean useAsyncProtocol = req.getServletPath().equals(ASYNCHRONOUS_URI);
		
		// is this an abort message?
		boolean abort = req.getParameter("abort") == null ? false : true;
		
		// is this a request for setting the liability cookie?
		if (req.getServletPath().equals(LIABILITY_URI)) {
			Cookie cookie = new Cookie(LIABILITY_COOKIE, liabilityCookieString);
			cookie.setMaxAge(LIABILITY_COOKIE_TTL);
			resp.addCookie(cookie);
			resp.setHeader("Location", req.getParameter("req"));
			resp.setStatus(HttpURLConnection.HTTP_MOVED_TEMP);
			return;
		}
		
		// check if a session has already been set up
		// and if not set one up now
		boolean isNewSession = true;
		Cookie biscuit = getCookie(req, SESSION_COOKIE);
		if (biscuit == null) {
			biscuit = new Cookie(SESSION_COOKIE, sessionCookieString);
			biscuit.setMaxAge(-1); // it's a session cookie
			resp.addCookie(biscuit);
		} else {
			// make sure this is not a session hijack attempt
			if (!biscuit.getValue().equals(sessionCookieString)) {
				resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
				resp.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
				outputStream.write("Session hijack attempt was detected".
																getBytes());
				logger.warn("Possible session hijack attempt from " + remoteAddress);
				return;
			}
			isNewSession = false;
		}
		sessionId = biscuit.getValue();
		
		// determine which request header fields MUST NOT be passed on
		// to the target system as per RFC 2616
		Map<String, List<String>> clientHeader = new HashMap<String, List<String>>();
		Enumeration<String> header = req.getHeaderNames();
		while (header.hasMoreElements()) {
			String name = header.nextElement();
			if (!(name.equalsIgnoreCase("Connection") || 
										name.equalsIgnoreCase("Host") ||
										name.equalsIgnoreCase("Keep-Alive") ||
										name.equalsIgnoreCase("Cookie"))) {		
				List<String> values = new ArrayList<String>();
				Enumeration<String> fieldValues = req.getHeaders(name);
				while (fieldValues.hasMoreElements()) {
					values.add(fieldValues.nextElement());
				}
				clientHeader.put(name, values);
			}
		}
		
		// create and add XFF header
		String receivedXFF = req.getHeader(HTTPConnectionManager.XFF);
		ArrayList<String> xffHeader = new ArrayList<String>();
		String xffString = "";
		if (receivedXFF != null) {
			xffString = receivedXFF + ", ";
		}
		xffString += remoteAddress;
		xffHeader.add(xffString);
		clientHeader.put(HTTPConnectionManager.XFF, xffHeader);
		
		HTTPResponse response;
		
		// ref field used in the async protocol
		String ref = req.getParameter("ref"); 
		String url = req.getParameter("url");
		String postData = "";
		if (url == null) {
			// in the asynchronous protocol url may be absent
			if (!useAsyncProtocol) {
				resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				outputStream.write("Parameter \"url\" is required".getBytes());
				return;
			}
		} else {
			// blindly add non-reserved parameters to the supplied URL or
			// to the POST body if this is a POST
			Map<String, String[]> parameters = req.getParameterMap();
			for (String key : parameters.keySet()) {
				if (!(key.equals("url") || (key.equals("ref") && useAsyncProtocol))) {
					for (String value : parameters.get(key)) {
						if (method.equalsIgnoreCase(HTTPConnectionManager.POST)) {
							postData += "&" + key + "=" + URLEncoder.encode(value, "UTF-8");
						} else {
							url += "&" + key + "=" + value;						
						}
					}
				}
			}
			logger.debug("Received URL: " + url);
			try {
				// create a properly encoded URL
				url = createEncodedUrl(url);
			} catch (URISyntaxException e) {
				resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
				resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
				outputStream.write(("URL " + url + " is not properly encoded").getBytes());
				return;
			}
			logger.debug("Encoded URL: " + url);
			// check if we're allowed to proxy for this URL
			int authorisationStatus = authMgr.isAllowedUrl(url);
			switch (authorisationStatus) {
				case AuthorisationManager.ALLOWED:
					break;
				case AuthorisationManager.DENIED:
					logger.warn("Attempt to access forbidden resource " + url +
									" by client at " + remoteAddress);
					resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
					resp.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
					resp.setContentType("text/plain");
					outputStream.write(("The resource " + url + 
													" may not be accessed").getBytes());
					return;
				case AuthorisationManager.ALLOWED_CONDITIONALLY:
					if (!liabilityCookieIsSet(req, liabilityCookieString)) {
						logger.warn("Attempt to access conditionally accessible " + 
								"resource " + url + " by client at " + remoteAddress);
						resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
						resp.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
						resp.setContentType("text/xml");
						outputStream.write(createLiabilityWarning(req).getBytes());
						return;
					}
					break;
				default:
					logger.fatal("Unknown authorisation status: " + authorisationStatus);		
			}
		}
		
		// now pass on the request to the appropriate handler
		try {
			if (useAsyncProtocol) {

				AsynchronousRequestHandler asyncHandler = 
									new AsynchronousRequestHandler(asm, isNewSession);
				if (abort) { // abort pending requests?
					response = asyncHandler.abort(sessionId);
				} else {
					response = asyncHandler.getResponse(ref, url, method, 
												sessionId, clientHeader, postData);
				}
			} else {
				SynchronousRequestHandler syncHandler = 
									new SynchronousRequestHandler();
				response = syncHandler.getResponse(url, method, clientHeader, postData);
			}
		} catch (UnknownSessionIdException e) {
			logger.error(e);
			resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
			resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			outputStream.write(("Unknown session id: " + e.getMessage()).
																	getBytes());
			return;
		} catch (NoPendingRequestsException e) {
			logger.error(e);
			resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
			resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			outputStream.write("No pending requests were found".getBytes());
			return;
		} catch (BlockingSessionExistsException e) {
			logger.error(e);
			resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
			resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			outputStream.write("Client already has a blocking request".getBytes());
			return;
		} catch (InterruptedException e) {
			logger.error(e);
			resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
			resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
			outputStream.write("".getBytes());
			return;
		} catch (TimeOutException e) {
			logger.error(e);
			resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
			resp.setStatus(HttpURLConnection.HTTP_GATEWAY_TIMEOUT);
			outputStream.write("A timeout occured".getBytes());
			return;
		} catch (TPPRequestException e) {
			logger.fatal(e);
			resp.addHeader(PROXY_STATUS_FIELD, NON_ZERO_PROXY_STATUS);
			resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
			outputStream.write("Internal server error".getBytes());
			return;
		}
		
		// TODO add VIA field
		
		// determine which response header fields MUST NOT be passed on
		// to the client as per RFC 2616
		Map<String, List<String>> headerFields = response.getHeaderFieldMap();
		headerFields.remove("Connection");
		headerFields.remove("Keep-Alive");
		
		// transfer encoding does not apply to our response
		headerFields.remove("Transfer-Encoding");
		
		// it doesn't make sense to use caching in the asynchronous
		// protocol
		if (useAsyncProtocol) {
			headerFields.remove("Cache-Control");
			List<String> values = new ArrayList<String>();
			values.add("no-cache");
			headerFields.put("Cache-Control", values);
		}
		
		// replace ill-supported application/rdf+xml content type with text/xml
		if (response.getContentType().equalsIgnoreCase("application/rdf+xml")) {
			headerFields.remove("Content-Type");
			List<String> values = new ArrayList<String>();
			values.add("text/xml");
			headerFields.put("Content-Type", values);			
		}
			
		
		// set response header fields
		resp.setStatus(response.getStatus());
		resp.setHeader(PROXY_STATUS_FIELD, "0");
		for (String field : headerFields.keySet()) {
			List<String> values = headerFields.get(field);
			if (values != null) {
				resp.setHeader(field, values.get(0));
				if (values.size() > 1) {
					for (int i = 1; i < values.size(); i++) {
						resp.addHeader(field, values.get(i));
						logger.debug("Header field " + field + ": " + values.get(i));
					}
				} else {
					logger.debug("Header field " + field + ": " + values.get(0));
				}
			}
		}
		
		outputStream.write(response.getBody());
	}
	
	

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/*
	 * use the URI class to do URL encoding (this is the approach
	 * recommended by SUN in the javadoc for java.net.URL)
	 */
	private String createEncodedUrl(String url) throws URISyntaxException,
													MalformedURLException {
			
		Matcher m = Pattern.
						compile("^([^:]+):\\/\\/([^:/]+)" + 
								"(:([0-9]+))?(([^?#]+)((\\?([^#]+))?)?" + 
								"(#(.*))?)?").matcher(url);
		if (m.find()) { 
			String scheme = m.group(1);	
			String host = m.group(2);
			int port = (scheme.equals("https")) ? 443 : 80;			
			if (m.group(4) != null)
				port = Integer.parseInt(m.group(4));
			String path = m.group(6);
			String query = m.group(9);
			String fragment = m.group(11);
			return new URI(scheme, null, host, 
					port, path, query, fragment).toASCIIString();
		} else {
			throw new MalformedURLException();
		}
	}
	

	/*
	 * create an XML message containing a warning about liability and a href
	 * to a page that sets a cookie and redirects to the original request
	 */
	private String createLiabilityWarning(HttpServletRequest req) throws ServletException {
		
		String request = req.getRequestURI() + "?" + req.getQueryString();
		try {
			request = URLEncoder.encode(request, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

		String msg = "<?xml-stylesheet type=\"text/xml\" href=\"" 
										+ LIABILITY_MSG_STYLESHEET + "\"?>";
		
		msg += "<TPPResponse>";
		msg += "<liabilityWarning acceptURL=\"" + req.getContextPath() + 
									LIABILITY_URI + "?req=" + 
									request + "\">";
		msg += "<message>" + configuration.getLiabilityWarning() + "</message>";
		msg += "</liabilityWarning>";
		msg += "</TPPResponse>";
		
		return msg;
	}

	/*
	 * check whether the liability cookie is set
	 */
	private boolean liabilityCookieIsSet(HttpServletRequest req, String cookieString) {
		Cookie biscuit = getCookie(req, LIABILITY_COOKIE);
		if (biscuit == null) {
			return false;
		} else {
			if (biscuit.getValue().equals(cookieString)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/*
	 * extract a cookie from the request
	 */
	private Cookie getCookie(HttpServletRequest req, String name) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie biscuit : cookies) {
				if (biscuit.getName().equals(name)) {
					return biscuit;
				}
			}
		}
		return null;
	}
	
	/*
	 * create a (nicely obfuscated) liability cookie value
	 */
	private String createLiabilityCookie(HttpServletRequest req) throws ServletException {
		String cookieValue = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException(e);
		}
		md.update((req.getContextPath() + req.getRemoteAddr()).getBytes());
		byte[] digest = md.digest();
		for (byte b : digest)
			cookieValue += Integer.toHexString(b & 0xff);
		
		return cookieValue;
	}
	
	/*
	 * create a session cookie based on the client IP address
	 * 
	 */
	private String createSessionCookie(HttpServletRequest req) throws 
															ServletException {
		String cookieValue = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException(e);
		}
		md.update((req.getRemoteAddr()).getBytes());
		byte[] digest = md.digest();
		for (byte b : digest)
			cookieValue += Integer.toHexString(b & 0xff);
		
		return cookieValue;
	}
	
	
	/* 
	 * extract version info from manifest
	 */
	private String getVersion() {
		String version = "Unknown";
		Properties prop = new Properties();
		try {
			prop.load(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
			if (prop.containsKey("Specification-Version")) {
				version = (String)prop.get("Specification-Version");
			}
		} catch (IOException e) {
			// just ignore it
		}
	
		return version;
	}
		
	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
