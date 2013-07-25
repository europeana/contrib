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

package at.ait.dme.yuma.suite.apps.map.client.widgets;

import at.ait.dme.gwt.openlayers.client.LonLat;
import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.Pixel;
import at.ait.dme.gwt.openlayers.client.event.EventListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;

/**
 * A map popup window anchored to a LonLat location
 * on the OpenLayers map.
 *
 * @author Rainer Simon
 */
public class MapPopup extends PopupPanel {
	
	/**
	 * The OpenLayers map
	 */
	private Map map;
	
	/**
	 * The popup anchor position
	 */
	private LonLat lonlat;
	
	/**
	 * This popup's map move listener
	 */
	private EventListener moveListener;
	
	public MapPopup(String title, String html, LonLat lonlat, Map map, ClickHandler closeHandler) {
		this.lonlat = lonlat;
		this.map = map;
		this.setStyleName("mapPopup");
		
		FlowPanel header = new FlowPanel();
		header.setStyleName("title");
		header.add(new InlineHTML(title));
		
		PushButton closeButton = new PushButton("X");
		closeButton.setStyleName("close");
		closeButton.addClickHandler(closeHandler);
		header.add(closeButton);
		
		FlowPanel inner = new FlowPanel();
		inner.setStyleName("inner");
		inner.add(new InlineHTML(html));
		
		moveListener = new EventListener() {
			@Override
			public void onEvent(LonLat ll, Pixel p, String tagname) {
				updatePosition();
			}
		};
		map.registerMapEventListener(moveListener, Map.EVENT_MOVE);
		
		FlowPanel panel = new FlowPanel();
		panel.add(header);
		panel.add(inner);

		this.setWidget(panel);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updatePosition();			
			}
		});
	}
	
	private void updatePosition() {
		Pixel xy = map.getViewPortPxFromLonLat(lonlat);
		setPopupPosition((int)xy.getX(), (int)xy.getY() - getOffsetHeight());
	}
	
	public void destroy() {
		map.unregisterMapEventListener(moveListener, Map.EVENT_MOVE);
		hide();
		clear();
		removeFromParent();
	}
	
}
