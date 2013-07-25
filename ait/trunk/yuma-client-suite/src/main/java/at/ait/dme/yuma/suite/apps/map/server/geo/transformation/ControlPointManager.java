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

package at.ait.dme.yuma.suite.apps.map.server.geo.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.ait.dme.yuma.suite.apps.core.server.annotation.AnnotationManager;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationServiceException;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageAnnotation;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.GeoPoint;

/**
 * Provides map control points
 * 
 * @author Rainer Simon
 * @author Christian Sadilek
 */
public class ControlPointManager {
	
	/**
	 * Control points
	 */
	private List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
	
	public ControlPointManager(HttpServletRequest request, HttpServletResponse response,
			String mapUrl) throws AnnotationServiceException {
		
		Collection<Annotation> annotations = new ArrayList<Annotation>();
		
		Set<String> shapeTypes = new HashSet<String>();
		shapeTypes.add(GeoPoint.class.getName());
		annotations = new AnnotationManager(request, response).listAnnotations(mapUrl,shapeTypes);
		
		for(Annotation annotation : annotations) {
			controlPoints.add(ControlPoint.fromAnnotation((ImageAnnotation) annotation));
		}
	}
	
	/**
	 * Returns the list of control points stored for the image
	 * with the specified URL.
	 * @param url the image URL
	 * @return the list of control points for the image
	 */
	public List<ControlPoint> getControlPoints() {
		return controlPoints;
	}
}
