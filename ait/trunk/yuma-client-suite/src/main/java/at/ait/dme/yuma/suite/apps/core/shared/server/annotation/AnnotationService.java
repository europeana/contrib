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

package at.ait.dme.yuma.suite.apps.core.shared.server.annotation;

import java.util.Collection;
import java.util.Set;

import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The annotation service provides annotation CRUD operations
 * to the JS client. 
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
@RemoteServiceRelativePath("../annotation")
public interface AnnotationService extends RemoteService {
	
	/**
	 * Create a new annotation.
	 * @param annotation the annotation
	 * @return then new annotation
	 * @throws AnnotationServiceException if anything goes wrong
	 */
	public Annotation createAnnotation(Annotation annotation) 
		throws AnnotationServiceException;

	/**
	 * Update an existing annotation.
	 * @param id the id of the annotation to update
	 * @param annotation the new annotation
	 * @return the updated annotation
	 * @throws AnnotationServiceException if anything goes wrong
	 */
	public Annotation updateAnnotation(String id, Annotation annotation) 
		throws AnnotationServiceException;

	/**
	 * Delete an annotation.
	 * @param annotationId the annotation ID
	 * @throws AnnotationServiceException if anything goes wrong
	 */
	public void deleteAnnotation(String annotationId) 
		throws AnnotationServiceException;

	/**
	 * List annotations for the given object.
	 * @param objectId the object ID
	 * @return the annotations
	 * @throws AnnotationServiceException if anything goes wrong
	 */
	public Collection<Annotation> listAnnotations(String objectId) 
		throws AnnotationServiceException;
	
	/**
	 * List annotations for the given object, filtering results by shape type. 
	 * Use the class name as shape type e.g. <code>Ellipse.class.getName()<code>.
	 * 
	 * TODO ultimately we will no longer need this method!
	 * 
	 * @param objectId the object ID
	 * @param shapeTypes the list of acceptable shape types 
	 * @return the annotations
	 * @throws AnnotationServiceException if anything goes wrong
	 */
	public Collection<Annotation> listAnnotations(String objectId, Set<String> shapeTypes)
		throws AnnotationServiceException;
	
}
