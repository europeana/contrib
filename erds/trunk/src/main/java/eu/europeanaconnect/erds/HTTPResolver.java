/**********************************************************************
 * Class HTTPResolver
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

/* ********************************************************************
 * CHANGELOG:
 * 
 * 2010-05-05 Refactoring + new package structure by Karaca Kocer
 * 2010-04-25 Code formatting, comments and license text by Karaca Kocer
 * Created on 2010-03-17 by Jürgen Kett
 ********************************************************************/

package eu.europeanaconnect.erds;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import eu.europeanaconnect.erds.ResolverException.ResolverExceptionCode;

/**
 * Simple Resolver that queries a remote server via HTTP.
 * It fetches the URL by reading the header of the
 * redirect-response of the remote resolver
 * (DEPRECATED! Please use the Multi-Threaded-Version)
 * 
 * @author Jürgen Kett (Original code)
 * @author Kadir Karaca Kocer (Refactoring, license text, comments & JavaDoc)
 * @author Nuno Freire (Refactoring)
 */

public class HTTPResolver implements DataProvider<ResolverResponse> {
	private final static Logger logger = Logger.getLogger(HTTPResolver.class);
    protected String requestUrlPattern;
    protected String id;
    protected String label;
    protected List<String> supportedNamespaces;
    protected String identifierPattern;
    protected DefaultHttpClient httpClient = null;

    /**
     * Fetches the URL out of the header of the redirect response
     * of the remote resolver using the "Location" attribute
     * 
     * @see DataProvider#getResponse(ResolverRequest)
     * @since 17.03.2010
     */
    @Override
    public ResolverResponse getResponse(ResolverRequest resolverRequest) throws ResolverException {
    	this.httpClient = new DefaultHttpClient();
        ResolverResponse resolverResponse = new ResolverResponse();
        HttpResponse httpResponse = null; 
        String url = getRequestUrl(resolverRequest);
        logger.debug("URL = " + url);
        HttpGet getMethod = new HttpGet(url);
        this.httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
        this.httpClient.getParams().setBooleanParameter(ClientPNames.REJECT_RELATIVE_REDIRECT, false);
 
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
        	logger.debug("Analyzing HTTP Header");
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
        	//It should be a redirect but it is NOT! Analyse the Response
            handleHttpErrorCodes(statusCode);
        }
        
        //TODO: find a better way
        this.httpClient.getConnectionManager().closeExpiredConnections();
        this.httpClient.getConnectionManager().closeIdleConnections(5, TimeUnit.SECONDS);
        return resolverResponse;
    }
    
    /**
     * Handles the HTTP status codes that are different than redirect (3xx)
     * @param statusCode HTTP Status code (See RFC 2616)
     * {@link "http://www.w3.org/Protocols/rfc2616/rfc2616.html"}
     * @throws ResolverException
     */
    public void handleHttpErrorCodes(int statusCode) throws ResolverException {
    	//TODO: make this better!
    	//See: http://www.w3.org/Protocols/rfc2616/rfc2616.html
        if (statusCode >= 500) {
            throw new ResolverException(this.id, ResolverExceptionCode.REMOTE_RESOLVER_ERROR);
        }
        if (statusCode >= 400) {
        	throw new ResolverException(this.id, ResolverExceptionCode.INVALID_IDENTIFIER);
        }
        //It is not a server error (5xx), not a client error (4xx), not a redirect (3xx)
        //what to do?
    }

    /**
     * @return The label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label Set the label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return Returns the HTTP Client that connects to the server.
     * @see org.apache.http.client.HttpClient
     */
    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    /**
     * @param resolverRequest
     * @return The URL of the request.
     */
    public String getRequestUrl(ResolverRequest resolverRequest) {
        return this.requestUrlPattern.replace("$identifier", resolverRequest.getIdentifier());
    }

    /**
     * @return The pattern of the request.
     */
    public String getRequestUrlPattern() {
        return this.requestUrlPattern;
    }

    /**
     * @param requestUrlPattern
     */
    public void setRequestUrlPattern(String requestUrlPattern) {
        this.requestUrlPattern = requestUrlPattern;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public List<String> getSupportedNamespaces() {
        return this.supportedNamespaces;
    }

    /**
     * @param supportedNamespaces
     */
    public void setSupportedNamespaces(List<String> supportedNamespaces) {
        this.supportedNamespaces = supportedNamespaces;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getIdentifierPattern() {
        return this.identifierPattern;
    }

    /**
     * @param identifierPattern
     */
    public void setIdentifierPattern(String identifierPattern) {
        this.identifierPattern = identifierPattern;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }


}
