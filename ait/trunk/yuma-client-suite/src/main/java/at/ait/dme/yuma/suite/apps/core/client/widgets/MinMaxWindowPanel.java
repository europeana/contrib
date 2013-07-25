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

import org.gwt.mosaic.core.client.Rectangle;
import org.gwt.mosaic.ui.client.Caption;
import org.gwt.mosaic.ui.client.Caption.CaptionRegion;
import org.gwt.mosaic.ui.client.ImageButton;
import org.gwt.mosaic.ui.client.WindowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * This class is used to add a minimize and maximize button to a GWT mosaic
 * window panel
 * 
 * @author Christian Sadilek
 */
public class MinMaxWindowPanel {
	private static int MINIMIZED_HEIGHT = 30;
	
	public static WindowPanel createMinMaxWindowPanel(int left, int top, int width, int height) {
		WindowPanel windowPanel = new WindowPanel();
		windowPanel.setPopupPosition(left, top);
		windowPanel.setPixelSize(width, height);
		windowPanel.setNormalBounds(new Rectangle(left, top, width, height));
		windowPanel.setHideContentOnMove(false);
		addMinMaxButtons(windowPanel, CaptionRegion.RIGHT);
		return windowPanel;
	}

	public static void addMinMaxButtons(final WindowPanel windowPanel, CaptionRegion captionRegion) {
		final ImageButton minMaxBtn = new ImageButton(Caption.IMAGES.windowMinimize());
		minMaxBtn.addClickHandler(new ClickHandler() {
			boolean minimized = false;
			final Rectangle r = windowPanel.getNormalBounds();
			@Override
			public void onClick(ClickEvent event) {
				if(minimized) {
					minMaxBtn.setImage(Caption.IMAGES.windowMinimize().createImage());
					windowPanel.setPixelSize(r.width, r.height);
				} else {
					minMaxBtn.setImage(Caption.IMAGES.windowMaximize().createImage());
					windowPanel.setPixelSize(windowPanel.getOffsetWidth(), MINIMIZED_HEIGHT);
				}
				windowPanel.onResize();
				minimized = !minimized;
			}
		});

		windowPanel.getHeader().add(minMaxBtn, captionRegion);
	}
}
