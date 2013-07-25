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

package at.ait.dme.yuma.suite.apps.core.server.annotation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.log4j.Logger;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.util.HttpResponseCodes;

import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap.EvictionPolicy;

import at.ait.dme.yuma.suite.apps.core.server.Config;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation.MediaType;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationService;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationServiceException;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageFragment;

/**
 * This class contains all actions on annotations.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class AnnotationManager implements AnnotationService {
	private static Logger logger = Logger.getLogger(AnnotationManager.class);

	private static final String ANNOTATION_SERVICE_URL_PROPERTY = "annotation.server.base.url";
	private static final String FAILED_TO_PARSE_ANNOTATION = "failed to parse anntotation";
	
	private static String annotationServerBaseUrl = null;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	
	// we will use a simple cache here for now.
	// TODO don't use this on a cluster
	private static final int MAX_SIZE_ANNOTATION_CACHE = 20;
	private static ConcurrentLinkedHashMap<String, Collection<Annotation>> annotationCache = 
		new ConcurrentLinkedHashMap<String, Collection<Annotation>>(EvictionPolicy.LRU, 
				MAX_SIZE_ANNOTATION_CACHE);

	public static void init(Config config) throws ServletException {
		annotationServerBaseUrl = config.getStringProperty(ANNOTATION_SERVICE_URL_PROPERTY);
		if (!annotationServerBaseUrl.endsWith("/"))
			annotationServerBaseUrl += "/";
		annotationServerBaseUrl += "api/";
	}

	public AnnotationManager(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	@Override
	public Annotation createAnnotation(Annotation annotation)
			throws AnnotationServiceException {
			
		ClientResponse<String> response = null;
		String annotationId = null;
		try {
			// Call the Annotation Server
			response = getAnnotationServer()
				.createAnnotation(JSONAnnotationHandler.serializeAnnotations(Arrays.asList(annotation)).toString());
			
			// Check response
			if (response.getStatus() != HttpResponseCodes.SC_CREATED)
				throw new AnnotationServiceException(response.getStatus());
			
			annotationId = response.getEntity();
			
			// Remove from cache
			annotationCache.remove(annotation.getObjectUri());
		} catch (AnnotationServiceException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} finally {
			// TODO make header forwarding optional (via web.xml init param)
			if(response != null)
				forwardResponseHeaders(response.getHeaders());
		}
		
		annotation.setId(annotationId);
		return annotation;
	}
	
	@Override
	public Annotation updateAnnotation(String id, Annotation annotation) 
			throws AnnotationServiceException {
		
		ClientResponse<String> response = null;
		String newId;
		try {
			// Call the Annotation Server
			response = getAnnotationServer()
				.updateAnnotation(encode(id), JSONAnnotationHandler.serializeAnnotations(Arrays.asList(annotation)).toString());
			
			// Check response			
			if(response.getStatus() != HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());	
			newId = response.getEntity();
			
			// Remove from cache
			annotationCache.remove(annotation.getObjectUri());
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} finally {
			// TODO make header forwarding optional (via web.xml init param)
			if(response != null)
				forwardResponseHeaders(response.getHeaders());
		}
		
		annotation.setId(newId);
		return annotation;
	}
	
	@Override
	public void deleteAnnotation(String annotationId) throws AnnotationServiceException {
		ClientResponse<String> response = null;
		try {					
			// Call the Annotation Server
			response = getAnnotationServer().
				deleteAnnotation(encode(annotationId));
			
			// Check response			
			if(response.getStatus() != HttpResponseCodes.SC_OK &&
					response.getStatus() != HttpResponseCodes.SC_NO_CONTENT)
				throw new AnnotationServiceException(response.getStatus());
			
			// Clear cache
			annotationCache.clear();
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} finally {
			// TODO make header forwarding optional (via web.xml init param)
			if(response != null)
				forwardResponseHeaders(response.getHeaders());
		} 		
	}
	
	@Override
	public Collection<Annotation> listAnnotations(String objectId) 
			throws AnnotationServiceException {
		
		ClientResponse<String> response = null;
		Collection<Annotation> annotations = null;			
		try {
			if ((annotations = annotationCache.get(objectId)) == null) {
				
				// Call the Annotation Server
				response = getAnnotationServer().
					getAnnotationTree(encode(objectId));	
				
				// Check response
				if (response.getStatus() != HttpResponseCodes.SC_OK)
					throw new AnnotationServiceException(response.getStatus());
				
				// Parse the response
				annotations = JSONAnnotationHandler.parseAnnotations(response.getEntity());
				
				// Cache the response
				annotationCache.putIfAbsent(objectId, annotations);
			}
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			ase.printStackTrace();
			throw ase;			
		} catch (Throwable t) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, t);
			t.printStackTrace();
			throw new AnnotationServiceException(t.getMessage());
		} finally {
			// TODO make header forwarding optional (via web.xml init param)
			// if(response != null)
				// forwardResponseHeaders(response.getHeaders());
		} 
		
		return annotations;
	}
	
	@Override
	public Collection<Annotation> listAnnotations(String objectId, Set<String> shapeTypes)
		throws AnnotationServiceException {
		
		Collection<Annotation> annotations = new ArrayList<Annotation>(); 
		
		// List all annotations of this object and keep only those that have
		// a fragment with a shape of one of the given types
		for (Annotation a : listAnnotations(objectId)) {
			if ((a.getMediaType() == MediaType.IMAGE) || (a.getMediaType() == MediaType.MAP)) {
				ImageFragment fragment = (ImageFragment) a.getFragment();
				if (fragment != null && fragment.getShape() != null && shapeTypes != null &&
						shapeTypes.contains(fragment.getShape().getClass().getName())) {
					annotations.add(a);
				}
			}
		}

		return annotations;
	}
	
	private RESTAnnotationServer getAnnotationServer() {
		HttpClient client = new HttpClient();

		// Forward all cookies from the calling request
		if (request != null) {
			
			javax.servlet.http.Cookie[] cookies =
				request.getCookies();
			
			if (cookies != null) {
				for (javax.servlet.http.Cookie c : cookies) {
					c.setDomain(request.getServerName());
					c.setPath("/");
					
					Cookie apacheCookie = new Cookie(c.getDomain(), c.getName(), c.getValue(), c
							.getPath(), c.getMaxAge(), c.getSecure());
					client.getState().addCookie(apacheCookie);
				}
				client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			}
		}

		return ProxyFactory.create(RESTAnnotationServer.class, annotationServerBaseUrl,
				new ApacheHttpClientExecutor(client));
	}
	
	private void forwardResponseHeaders(MultivaluedMap<String, String> headers) {
		if (headers==null) return;
		//  copy response headers
		for(String key : headers.keySet()) {
			for (String value : headers.get(key)) {						
				response.addHeader(key, value);
			}
		}
	}
	
	private String encode(String url) throws UnsupportedEncodingException {
		return java.net.URLEncoder.encode(url,"UTF-8").replace("%", "%25");
	}
	
}
