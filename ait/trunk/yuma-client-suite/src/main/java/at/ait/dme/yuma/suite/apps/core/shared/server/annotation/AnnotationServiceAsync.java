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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interface to the annotation service.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 * @see AnnotationService
 */
public interface AnnotationServiceAsync {
	
	public void createAnnotation(Annotation annotation, 
			AsyncCallback<Annotation> callback);

	public void updateAnnotation(String id, Annotation annotation, 
			AsyncCallback<Annotation> callback);

	public void deleteAnnotation(String annotationId, 
			AsyncCallback<Void> callback);

	public void listAnnotations(String objectId,
			AsyncCallback<Collection<Annotation>> callback);
	
	public void listAnnotations(String objectId, Set<String> shapeTypes,
			AsyncCallback<Collection<Annotation>> callback);
	
}
