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

package at.ait.dme.yuma.suite.apps.core.client.widgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A PopupPanel which implements a simple AJAX 'Loading' indicator
 * at the center of the screen. 
 *
 * @author Rainer Simon
 */
public class LoadingPopup extends PopupPanel {
	
	public LoadingPopup(String label) {
		this.setStyleName("loadmask");

		FlowPanel inner = new FlowPanel();
		inner.setStyleName("inner");
		inner.add(new Image("images/ajax-loader-large.gif"));
		inner.add(new Label(label));
		
		this.setWidget(inner);
		center();
	}

}
