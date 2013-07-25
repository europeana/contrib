package at.researchstudio.dme.imageannotation.server;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.util.HttpResponseCodes;

import at.researchstudio.dme.annotation.client.AnnotationService;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.server.ImageAnnotationService;
import at.researchstudio.dme.imageannotation.client.server.exception.AnnotationServiceException;
import at.researchstudio.dme.imageannotation.server.annotation.builder.RdfXmlAnnotationBuilder;
import at.researchstudio.dme.imageannotation.server.util.URLEncoder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * the implementation of the image annotation service
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationServiceImpl extends RemoteServiceServlet 
		implements ImageAnnotationService {
	
	private static final long serialVersionUID = 7979737020415861621L;
	
	private static Logger logger = Logger.getLogger(ImageAnnotationServiceImpl.class);

	private static final String FAILED_TO_PARSE_ANNOTATION = "failed to parse anntotation";
	private static final String ANNOTATION_MIDDLEWARE_URL_PROPERTY = 
		"annotation.middleware.base.url";
	
	private String annotationMiddlewareBaseUrl = null;
	
	/**
	 * reads the configuration from the servlet context. in case it's not found
	 * there it tries to read it from the property file.
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	
		ServletContext application = config.getServletContext();	    		
	    annotationMiddlewareBaseUrl = 
	    	application.getInitParameter(ANNOTATION_MIDDLEWARE_URL_PROPERTY);
	    if(annotationMiddlewareBaseUrl==null) {
			try {
				Properties props = new Properties();
				props.load(getClass().getResourceAsStream("image-annotation-service.properties"));
				annotationMiddlewareBaseUrl = props.getProperty(ANNOTATION_MIDDLEWARE_URL_PROPERTY);
				if(annotationMiddlewareBaseUrl==null) throw new IOException();
			} catch (IOException e) {
		    	logger.fatal("parameter " + ANNOTATION_MIDDLEWARE_URL_PROPERTY + " not set");
		    	throw new ServletException("missing context parameter");
			}
	    }    
	}

	@Override
	public ImageAnnotation createAnnotation(ImageAnnotation annotation) 
			throws AnnotationServiceException {
		
		ImageAnnotation storedAnnotation = null;
		try {				
			// set the mime type of the annotated object
			annotation.setMimeType(getServletContext().getMimeType(annotation.getObjectId()));
			
			// call the annotation middleware
			ClientResponse<String> response = getAnnotationService().
				createAnnotation(RdfXmlAnnotationBuilder.toRdfXml(annotation));
			
			// check the response
			if(response.getStatus()!=HttpResponseCodes.SC_CREATED)
				throw new AnnotationServiceException(response.getStatus());
		
			// parse the response
			Collection<ImageAnnotation> annotations = 
				RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
			if(!annotations.isEmpty()) 
				storedAnnotation=(ImageAnnotation)annotations.iterator().next();
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} 
		return storedAnnotation;
	}		

	@Override
	public ImageAnnotation updateAnnotation(ImageAnnotation annotation)
			throws AnnotationServiceException {
		
		ImageAnnotation storedAnnotation = null;
		try {					
			// call the annotation middleware			
			ClientResponse<String> response = getAnnotationService().
				updateAnnotation(URLEncoder.encode(annotation.getId()),
						RdfXmlAnnotationBuilder.toRdfXml(annotation));
			
			// check the response			
			if(response.getStatus()!=HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());
		
			// parse the response			
			Collection<ImageAnnotation> annotations = 
				RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
			if(!annotations.isEmpty()) 
				storedAnnotation=(ImageAnnotation)annotations.iterator().next();
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} 
		return storedAnnotation;
	}
	
	@Override
	public void deleteAnnotation(String annotationId) throws AnnotationServiceException {
		try {					
			// call the annotation middleware			
			ClientResponse<String> response = getAnnotationService().
				deleteAnnotation(URLEncoder.encode(annotationId));
			
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
		} 		
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String objectId) 
			throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = null;			

		try {
			// call the annotation middleware			
			ClientResponse<String> response=getAnnotationService().
				listAnnotations(URLEncoder.encode(objectId));	
			
			// check the response
			if(response.getStatus()!=HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());
			
			// parse the response			
			annotations = RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;			
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		}
		
		return annotations;
	}
	
	@Override
	public Collection<ImageAnnotation> findAnnotations(String searchTerm) 
			throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = null;			

		try {
			// call the annotation middleware			
			ClientResponse<String> response=getAnnotationService().
				findAnnotations(URLEncoder.encode(searchTerm));	
			
			// check the response
			if(response.getStatus()!=HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());
			
			// parse the response			
			annotations = RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		}
		
		return annotations;
	}
	
	private AnnotationService getAnnotationService() {		
		HttpClient client = new HttpClient();
		/*Credentials defaultcreds = new UsernamePasswordCredentials("both", "tomcat");
		client.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, 
				AuthScope.ANY_PORT, AuthScope.ANY_REALM), defaultcreds);*/
		
		// make sure to forward all cookies to the middleware				
		javax.servlet.http.Cookie[] cookies = getThreadLocalRequest().getCookies();
		for(javax.servlet.http.Cookie c : cookies) {			
			c.setDomain(getThreadLocalRequest().getServerName());
			c.setPath("/");
			Cookie apacheCookie = new Cookie(c.getDomain(), c.getName(), c.getValue(),
						c.getPath(), c.getMaxAge(), c.getSecure());
			client.getState().addCookie(apacheCookie);			
		}
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
				
		return ProxyFactory.create(AnnotationService.class, annotationMiddlewareBaseUrl,
				new ApacheHttpClientExecutor(client));		
	}
}
