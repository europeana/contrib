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

import com.google.gwt.i18n.client.Constants;

/**
 * Internationalized text labels
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public interface I18NConstants extends Constants {
	
	/**
	 * AnnotationPanel
	 */
	public String annotate();
	public String annotateFragment();
	
	/**
	 * ImageAnnotationTreeNode
	 */
	public String reply();
	public String replyWithFragment();
	public String edit();
	public String delete();

	/**
	 * ImageAnnotationEditForm
	 */
	public String title();
	public String text();
	public String addTag();
	public String add();
	public String browse();
	public String save();
	public String cancel();
	
	/**
	 * Vocabulary browser
	 */
	public String vocabularyBrowser();
	
	/**
	 * Google Map Popup
	 */
	public String map();
	
	public String annotation();
	public String annotations();
	public String addAnnotation();
	public String helpGeoreference();
	public String helpGeoreferenceHint();
	public String actionCreateCP();
	public String addAnnotationHint();
	public String annotationReplyTitlePrefix();
	public String annotationLinks();
	public String annotationSuggestedLinks();
	public String annotationSearch();
	public String annotationCreator();
	public String annotationImage();
	public String annotationCreationDate();	
	public String annotationSearchResults();	
	public String searching();
	public String noresults();
	public String publicScope();
	public String privateScope();	
	public String scope();
	public String help();	
	public String searchLabel();
	public String searchButton();
	public String tabAnnotations();
	public String tabGeoReferencing();
	public String tabExploration();
	public String aboutThisMap();
	public String placeSearch();
	public String dataOverlay();
	public String kmlExport();
}
