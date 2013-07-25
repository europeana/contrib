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

import java.io.Serializable;

import at.ait.dme.yuma.suite.apps.core.shared.server.RESTfulServiceException;

/**
 * A general-purpose exception thrown by the annotation service in case
 * of problems.
 * 
 * @author Rainer Simon
 */
public class AnnotationServiceException extends RESTfulServiceException implements Serializable {
	private static final long serialVersionUID = -1021896871854802248L;

	public AnnotationServiceException() {
	}
	
	public AnnotationServiceException(String message) {
		super(message);
	}
	
	public AnnotationServiceException(int statusCode) {
	    super(statusCode);
	}
}
