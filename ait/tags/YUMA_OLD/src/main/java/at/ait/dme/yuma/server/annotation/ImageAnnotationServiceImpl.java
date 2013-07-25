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

import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.server.ImageAnnotationService;
import at.ait.dme.yuma.client.server.exception.AnnotationServiceException;
import at.ait.dme.yuma.server.util.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * the implementation of the image annotation service
 * 
 * @author Christian Sadilek
 */

public class ImageAnnotationServiceImpl extends RemoteServiceServlet 
	implements ImageAnnotationService {
	
	private static final long serialVersionUID = 7979737020415861621L;
	
	/**
	 * reads the configuration from the servlet context. in case it's not found
	 * there it tries to read it from the property file.
	 */
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
	    super.init(servletConfig);	
	    Config config = new Config(servletConfig, 
				getClass().getResourceAsStream("image-annotation-service.properties"));
	    
	    ImageAnnotationManager.init(config);
	}

	@Override
	public ImageAnnotation createAnnotation(ImageAnnotation annotation, Boolean reply) 
			throws AnnotationServiceException {
		
		// set the mime type of the annotated object
		annotation.setMimeType(getServletContext().getMimeType(annotation.getImageUrl()));

		return new ImageAnnotationManager(getThreadLocalRequest(), getThreadLocalResponse()).
			createAnnotation(annotation, reply);
	}

	@Override
	public ImageAnnotation updateAnnotation(ImageAnnotation annotation) 
			throws AnnotationServiceException {

		return new ImageAnnotationManager(getThreadLocalRequest(), getThreadLocalResponse()).
			updateAnnotation(annotation);
	}
	
	@Override
	public void deleteAnnotation(String annotationId) throws AnnotationServiceException {
		
		new ImageAnnotationManager(getThreadLocalRequest(), getThreadLocalResponse()).
			deleteAnnotation(annotationId);
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String europeanaUri, String imageUrl) 
			throws AnnotationServiceException {
		
		return new ImageAnnotationManager(getThreadLocalRequest(), getThreadLocalResponse()).
			listAnnotations(europeanaUri, imageUrl);
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String europeanaUri, String imageUrl, 
			Set<String> shapeTypes) throws AnnotationServiceException {
		
		return new ImageAnnotationManager(getThreadLocalRequest(), getThreadLocalResponse()).
			listAnnotations(europeanaUri, imageUrl, shapeTypes);
	}
	
}
