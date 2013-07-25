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

package at.ait.dme.yuma.client.map.explore;


import at.ait.dme.yuma.client.AnnotationConstants;
import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.map.TiledImageComposite;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.StackLayoutPanel;

/**
 * The container composite for the main tool window's 'Exploration' tab.
 * Contains a StackLayoutPanel with one panel for each Exploration feature.
 * Currently, there are two features: place search (see {@link SearchPanel})
 * and KML overlay (see {@link KMLOverlayPanel}).
 * 
 * @author Rainer Simon
 */
public class ExplorationComposite extends Composite {
	
	/**
	 * The root container panel
	 */
	private StackLayoutPanel containerPanel;
	
	public ExplorationComposite(TiledImageComposite imageComposite) {		
		// Container stack panel
		containerPanel = new StackLayoutPanel(Unit.EM);
		
		AnnotationConstants i18n = Application.getConstants();
		
		// Map metadata
		containerPanel.add(new HTML("Map thumbnail and metadata will be shown here..."), i18n.aboutThisMap(), 3);
		
		// Search panel
		containerPanel.add(new SearchPanel(imageComposite), i18n.placeSearch(), 3);

		// Vector overlay panel
		containerPanel.add(new KMLOverlayPanel(imageComposite), i18n.dataOverlay(), 3);
		containerPanel.showWidget(0);
		
		initWidget(containerPanel);
	}

}
