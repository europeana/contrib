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

package at.ait.dme.yuma.suite.apps.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

public class YUMACoreProperties {
	
	private static I18NConstants annotationConstants = null;
	
	public static String getObjectURI() {
		return getValue("objectURI");
	}
	
	public static String getBaseUrl() {
		return getValue("baseURL");	
	}
	
	public static String getFeedUrl() {
		return getValue("feedURL");
	}
	
	public static String getRDFBaseUrl() {
		return getValue("rdfBaseURL");
	}
	
	public static I18NConstants getConstants() {
		if (annotationConstants == null)
			annotationConstants = (I18NConstants) GWT.create(I18NConstants.class);
		
		return annotationConstants;
	}
	
	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;

	private static String getValue(String key) {
		String value;
		try {
			Dictionary params = Dictionary.getDictionary("parameters");
			value = params.get(key);
			if (value.equals("null"))
				value = null;
		} catch (Exception e) {
			return null;
		}
		return value;
	}
	
}
