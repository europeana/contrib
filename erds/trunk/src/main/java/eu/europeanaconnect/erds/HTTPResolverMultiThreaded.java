/**********************************************************************
 * Class HTTPResolverMultiThreaded
 * Copyright (c) 2010, German National Library / Deutsche Nationalbibliothek
 * Adickesallee 1, D-60322 Frankfurt am Main, Federal Republic of Germany 
 *
 * This program is free software.
 * For your convenience it is dual licensed.
 * You can redistribute it and/or modify it under the terms of
 * one of the following licenses:
 * 
 * 1.)
 * The GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * You should have received a copy of the GNU General Public License
 * along with this program (gpl-3.0.txt); if not please read
 * http://www.gnu.org/licenses/gpl.html
 * 
 * 2.)
 * The European Union Public Licence as published by
 * The European Commission (executive body of the European Union);
 * either version 1.1 of the License, or (at your option) any later version.
 * You should have received a copy of the European Union Public Licence
 * along with this program (eupl_v1.1_en.pdf); if not please read
 * http://www.osor.eu/eupl
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the above licenses for more details.
 * 
 * Jürgen Kett, Kadir Karaca Kocer -- German National Library
 * 
 **********************************************************************/
package eu.europeanaconnect.erds;

import java.io.IOException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import eu.europeanaconnect.erds.ResolverException.ResolverExceptionCode;

/**
 * A Resolver that queries a remote server via HTTP.
 * It fetches the URL by reading the header of the redirect-response of the remote resolver.
 * It supports multiple simultaneous connections and request retries on error.
 * 
 * @author Nuno Freire (Original code)
 * @author Karaca Kocer (Small corrections)
 */
public class HTTPResolverMultiThreaded extends HTTPResolver {
    private final static Logger logger = Logger.getLogger(HTTPResolverMultiThreaded.class);
    //Shall we take those constants into Spring configuration? (Karaca 20100805)
    private static final int MAX_CONNECTIONS         = 250;
    private static final int CONN_PER_ROUTE          = 250;
    private static final int MAXIMUM_REQUEST_RETRIES = 3;
    private static final int CONECTION_TIMEOUT       = 15000;
    private static final int SOCKET_TIMEOUT          = 15000;
    
    //string to use as label during connection
    protected String userAgentString = "The European Resolution Discovery Service";
    
    /**
     * TODO: ->Nuno: explain this part please
     */
    public HTTPResolverMultiThreaded() {
    	HttpParams params = new BasicHttpParams();
    	ConnManagerParams.setMaxTotalConnections(params, MAX_CONNECTIONS);
    	ConnPerRouteBean connPerRoute = new ConnPerRouteBean(CONN_PER_ROUTE);
    	ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

    	SchemeRegistry schemeRegistry = new SchemeRegistry();
    	schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    	schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

    	ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);

    	this.httpClient = new DefaultHttpClient(clientConnectionManager, params);
        HttpConnectionParams.setConnectionTimeout(this.httpClient.getParams(), CONECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(this.httpClient.getParams(), SOCKET_TIMEOUT);
        this.httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
        this.httpClient.getParams().setBooleanParameter(ClientPNames.REJECT_RELATIVE_REDIRECT, false);
        this.httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, this.userAgentString);        
        
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        	@Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= MAXIMUM_REQUEST_RETRIES) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    // Retry if the server dropped connection on us
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {
                    // Do not retry on SSL handshake exception
                    return false;
                }
                HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest); 
                if (idempotent) {
                    // Retry if the request is considered idempotent 
                    return true;
                }
                return false;
            }

        };
        this.httpClient.setHttpRequestRetryHandler(myRetryHandler);
    } 
    
    /**
     * Fetches the URL out of the header of the redirect response
     * of the remote resolver using the "Location" attribute
     * 
     * @see DataProvider#getResponse(ResolverRequest)
     * @since 17.03.2010
     */
    @Override
    public ResolverResponse getResponse(ResolverRequest resolverRequest) throws ResolverException {
        ResolverResponse resolverResponse = new ResolverResponse();
        HttpResponse httpResponse = null; 
        String url = getRequestUrl(resolverRequest);
        logger.debug("URL = " + url);
        HttpGet getMethod = new HttpGet(url);
 
        try {
        	httpResponse = this.httpClient.execute(getMethod);
            logger.debug("Operation GET successfull");
        } catch (ClientProtocolException e) {
        	logger.error("A ClientProtocolException occured!\nStacktrace:\n" + e.getMessage());
        	e.printStackTrace();
            throw new ResolverException(this.id, ResolverExceptionCode.HTTP_PROTOCOL_ERROR, e);
        } catch (IOException e) {
        	logger.error("An IOException occured!\nStacktrace:\n" + e.getMessage());
        	e.printStackTrace();
            throw new ResolverException(this.id, ResolverExceptionCode.IO_ERROR, e);
        } catch (RuntimeException e) {
        	logger.error("A RuntimeException occured!\nStacktrace:\n" + e.getMessage());
        	e.printStackTrace();
            throw new ResolverException(this.id, ResolverExceptionCode.SEVERE_RUNTIME_ERROR, e);
        } catch (Exception e) {
        	logger.error("An Exception occured!\nStacktrace:\n" + e.getMessage());
        	e.printStackTrace();
            throw new ResolverException(this.id, ResolverExceptionCode.UNKNOWN_ERROR, e);
        }
        
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        
        logger.debug("HTTP Status-code: " + statusCode);
        if ((statusCode > 299) && (statusCode < 400)) {
        	//It is a redirect See: http://www.w3.org/Protocols/rfc2616/rfc2616.html
        	logger.debug("Its a redirect: analyzing the HTTP Header");
        	//logger.debug(getMethod.)
        	if (!httpResponse.containsHeader("Location")) {
        		logger.error("Header does not contain Location attribute!");
                throw new ResolverException(this.id, ResolverExceptionCode.NO_REDIRECT_ERROR);
        	}
        	logger.debug("Analyzing redirect location");
            Header location = httpResponse.getFirstHeader("Location");
            if (location == null) {
            	logger.error("No redirect header for URL: " + url);
                throw new ResolverException(this.id, ResolverExceptionCode.NO_REDIRECT_ERROR);
            }
            logger.debug("Location: " + location.getValue());
            resolverResponse.setUrl(location.getValue());
        } else {
        	if (statusCode < 300) {
        		//that was not an identifier but a real link
        		resolverResponse.setUrl(url);
        	} else {
        		//server returned a 4xx or 5xx code -> handle the error
                handleHttpErrorCodes(statusCode);
        	}
        }

        return resolverResponse;
    }

	/**
	 * @return the userAgentString
	 */
	public String getUserAgentString() {
		return this.userAgentString;
	}

	/**
	 * @param userAgentString the userAgentString to set
	 */
	public void setUserAgentString(String userAgentString) {
		this.userAgentString = userAgentString;
	}
}
