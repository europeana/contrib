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

package at.ait.dme.yuma.client.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * asynchronous interface of the image annotation service
 * 
 * @author Christian Sadilek
 * @see ImageAnnotationService
 */
public interface ImageAnnotationServiceAsync {
	public void createAnnotation(ImageAnnotation annotation, Boolean reply,  
			AsyncCallback<ImageAnnotation> callback);

	public void updateAnnotation(ImageAnnotation annotation, 
			AsyncCallback<ImageAnnotation> callback);

	public void deleteAnnotation(String annotationId, 
			AsyncCallback<Void> callback);

	public void listAnnotations(String europeanaUri, String imageUrl,
			AsyncCallback<ArrayList<ImageAnnotation>> callback);
	
	public void listAnnotations(String europeanaUri, String imageUrl, Set<String> shapeTypes,
			AsyncCallback<List<ImageAnnotation>> callback);
}
