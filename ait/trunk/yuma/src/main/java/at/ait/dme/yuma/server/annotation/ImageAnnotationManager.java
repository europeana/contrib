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

package at.ait.dme.yuma.server.annotation;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import at.ait.dme.yuma.client.image.ImageFragment;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.server.ImageAnnotationService;
import at.ait.dme.yuma.client.server.exception.AnnotationServiceException;
import at.ait.dme.yuma.server.annotation.builder.RdfXmlAnnotationBuilder;
import at.ait.dme.yuma.server.util.Config;
import at.ait.dme.yuma.server.util.URLEncoder;
import at.ait.dme.yuma.server.util.XMLUtil;

/**
 * This class contains all actions on annotations.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationManager implements ImageAnnotationService {
	private static Logger logger = Logger.getLogger(ImageAnnotationManager.class);
	
	private static final String ANNOTATION_SERVICE_URL_PATH = "/api/annotation/";
	private static final String ANNOTATION_SERVICE_URL_PROPERTY = "annotation.service.base.url";
	private static final String FAILED_TO_PARSE_ANNOTATION = "failed to parse anntotation";
	
	private static String annotationServiceBaseUrl = null;
	private HttpServletRequest clientRequest = null;
	private HttpServletResponse clientResponse = null;

	public static void init(Config config) throws ServletException {
		annotationServiceBaseUrl = config.getStringProperty(ANNOTATION_SERVICE_URL_PROPERTY);
	}

	public ImageAnnotationManager(HttpServletRequest clientRequest, 
			HttpServletResponse clientResponse) {
		this.clientRequest = clientRequest;
		this.clientResponse = clientResponse;
	}
	
	@Override
	public ImageAnnotation createAnnotation(ImageAnnotation annotation, Boolean reply) 
			throws AnnotationServiceException {

		ClientResponse<String> response = null;
		try {										
	
			// call the europeana annotation service
			if(!reply) {				
				response = getAnnotationService().createAnnotation(
						URLEncoder.encode(annotation.getExternalObjectId()),
						RdfXmlAnnotationBuilder.toRdfXml(annotation));
			} else {
				response = getAnnotationService().createAnnotationReply(
						annotation.getParentId().replace(
								annotationServiceBaseUrl+ANNOTATION_SERVICE_URL_PATH, ""),
						RdfXmlAnnotationBuilder.toRdfXml(annotation));
			}						
			
			// check the response
			if(response.getStatus()!=HttpResponseCodes.SC_CREATED)
				throw new AnnotationServiceException(response.getStatus());

			annotation.setId(annotationServiceBaseUrl+ANNOTATION_SERVICE_URL_PATH
					+response.getEntity());
						
            return annotation;
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} finally {
			if(response!=null) copyResponseHeaders(response.getHeaders());
		}
	}		


	@Override
	public ImageAnnotation updateAnnotation(ImageAnnotation annotation)
			throws AnnotationServiceException {

		ClientResponse<String> response = null;
		try {					
			// call the europeana annotation service			
			response = getAnnotationService().updateAnnotation(annotation.getId().replace(
						annotationServiceBaseUrl+ANNOTATION_SERVICE_URL_PATH, ""),
						RdfXmlAnnotationBuilder.toRdfXml(annotation));
						
			// check the response			
			if(response.getStatus()!=HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());
		
			annotation.setId(annotationServiceBaseUrl+ANNOTATION_SERVICE_URL_PATH
					+response.getEntity());
						
            return annotation;
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} finally {
			if(response!=null) copyResponseHeaders(response.getHeaders());
		}
	}
	
	@Override
	public void deleteAnnotation(String annotationId) throws AnnotationServiceException {
		
		ClientResponse<String> response = null;
		try {					
			// call the europeana annotation service		
			response = getAnnotationService().deleteAnnotation(annotationId.replace(
						annotationServiceBaseUrl+ANNOTATION_SERVICE_URL_PATH, ""));					
			
			// check the response			
			if(response.getStatus()!=HttpResponseCodes.SC_OK&&
					response.getStatus()!=HttpResponseCodes.SC_NO_CONTENT)
				throw new AnnotationServiceException(response.getStatus());
						
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} finally {
			if(response!=null) copyResponseHeaders(response.getHeaders());
		} 		
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String europeanaUri, String imageUrl) 
			throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();	
		ClientResponse<String> resp = null;
		try {
			// call the europeana annotation service 			
			resp=getAnnotationService().listAnnotations(URLEncoder.encode(europeanaUri));	
						
			// check the response status
			if(resp.getStatus()!=HttpResponseCodes.SC_OK && 
					resp.getStatus()!=HttpResponseCodes.SC_NOT_FOUND)
				throw new AnnotationServiceException(resp.getStatus());		
				
			// parse the ids and fetch each annotation, we then group them together 
			// as a single rdf:RDF to reuse our existing rdfxml handler
			// TODO we should add some paging here in the future, should be simple
			// the way we have designed the API in Europeana
			// TODO we should also consider to use an alternative to RDF/XML						
			Document mainDoc = null; 
			String ids = resp.getEntity();			
			for(String id : ids.split("\\n")) {
				if(!id.isEmpty()) {
					String annId = annotationServiceBaseUrl+ANNOTATION_SERVICE_URL_PATH+id;		    
					resp = getAnnotationService(resp.getHeaders()).findAnnotationById(id);
					if(resp.getStatus()!=HttpResponseCodes.SC_OK)
						throw new AnnotationServiceException(resp.getStatus());
					
					Document doc = new SAXBuilder().build(new StringReader(resp.getEntity()));
					Element rdfDescription=doc.getRootElement().
						getChild("Description", doc.getRootElement().getNamespace());								  
					rdfDescription.setAttribute("about", 
						annId, doc.getRootElement().getNamespace());
					if(mainDoc==null) {
						mainDoc = doc;
					} else {
						mainDoc.getRootElement().addContent((Element)rdfDescription.clone());								
					}										
				}
			}				
			// parse the response
			if(mainDoc!=null)
				annotations = RdfXmlAnnotationBuilder.fromRdfXml(XMLUtil.xmlToString(mainDoc));
			
			// an annotation in europeana points to an europeanaUri, there could be 
			// more than one image for that URI.
			// so we remove the annotations that address a different image
			Collection<ImageAnnotation> notNeeded = new ArrayList<ImageAnnotation>();
			for(ImageAnnotation annotation : annotations) {
				if(!annotation.getImageUrl().equals(imageUrl))
					notNeeded.add(annotation);
			}
			annotations.removeAll(notNeeded);
			
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;			
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} finally {
			if(resp!=null) copyResponseHeaders(resp.getHeaders());			
		}
		return annotations;
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String europeanaUri, String imageUrl,
			Set<String> shapeTypes) throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>(); 
		
		// list all annotations of this object and keep only those that have
		// a fragment with a shape of one of the given types
		for(ImageAnnotation annotation : listAnnotations(europeanaUri, imageUrl)) {
			ImageFragment fragment = annotation.getFragment();
			if(fragment!=null && fragment.getShape() != null && shapeTypes!=null &&
					shapeTypes.contains(fragment.getShape().getClass().getName())) {
				annotations.add(annotation);
			}
		}

		return annotations;
	}
	
	private EuropeanaAnnotationService getAnnotationService() {
		return getAnnotationService(null);
	}
	
	private EuropeanaAnnotationService getAnnotationService(
			MultivaluedMap<String, String> headers) {		
		
		List<String> cookieHeaders = (headers != null) ? headers
				.get("Set-Cookie") : new ArrayList<String>();

		HttpClient client = new HttpClient();
		// make sure to forward all cookies 				
		javax.servlet.http.Cookie[] cookies = clientRequest.getCookies();
		for(javax.servlet.http.Cookie c : cookies) {			
			c.setDomain(clientRequest.getServerName());
			c.setPath("/");
			
			String value = c.getValue();
			for(String cookieHeader : cookieHeaders) {
				if(cookieHeader.startsWith(c.getName())) {
					String cookieHeaderParts[] = cookieHeader.split("=");
					if(cookieHeaderParts.length >= 2)
						value = cookieHeaderParts[1];
				}
			}
			Cookie apacheCookie = new Cookie(c.getDomain(), c.getName(), value,
						c.getPath(), c.getMaxAge(), c.getSecure());
			client.getState().addCookie(apacheCookie);			
		}
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
				
		return ProxyFactory.create(EuropeanaAnnotationService.class, annotationServiceBaseUrl,
				new ApacheHttpClientExecutor(client));		
	}
	
	private void copyResponseHeaders(MultivaluedMap<String, String> headers) {
		if (headers==null) return;
		//  copy response headers
		for(String key : headers.keySet()) {
			for (String value : headers.get(key)) {						
				clientResponse.addHeader(key, value);
			}
		}
	}
}
