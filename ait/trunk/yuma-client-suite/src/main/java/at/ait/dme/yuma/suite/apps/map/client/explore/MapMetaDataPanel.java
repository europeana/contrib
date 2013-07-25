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

package at.ait.dme.yuma.suite.apps.map.client.explore;

import at.ait.dme.yuma.suite.apps.map.shared.geo.MapMetadata;
import at.ait.dme.yuma.suite.apps.map.shared.server.MapMetadataService;
import at.ait.dme.yuma.suite.apps.map.shared.server.MapMetadataServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * Panel displaying the available map metadata (if any...)
 * 
 * @author Rainer Simon
 */
public class MapMetaDataPanel extends FlowPanel {
	
	/**
	 * Map metadata properties
	 */
	private Image thumbnail = new Image();
	private HTML title = new HTML();
	private HTML author = new HTML();
	private HTML date = new HTML();
	private HTML source = new HTML();
	private HTML description = new HTML();
	private HTML link = new HTML();
	
	public MapMetaDataPanel(String metadataURL) {
		this.setStyleName("explore-about");
		
		thumbnail.setStyleName("explore-aboutThumbnail");
		this.add(thumbnail);
		
		title.setStyleName("explore-aboutTitle");
		this.add(title);
		
		author.setStyleName("explore-aboutAuthor");
		this.add(author);
		
		date.setStyleName("explore-aboutDate");
		this.add(date);
		
		source.setStyleName("explore-aboutSource");
		this.add(source);
		
		description.setStyleName("explore-aboutDescription");
		this.add(description);
		
		link.setStyleName("explore-aboutLink");
		this.add(link);
		
		loadMetadataAsync(metadataURL);
	}
	
	private void setMetdata(MapMetadata md) {
		if (md.getThumbnail() != null)
			this.thumbnail.setUrl(md.getThumbnail()); // optional meta-data parameter
		
		this.title.setHTML(md.getTitle());
		this.author.setHTML(md.getAuthor());
		this.date.setHTML(md.getDate());
		
		if (md.getSource() != null)
			this.source.setHTML(md.getSource());
		
		if (md.getDescription() != null)
			this.description.setHTML(md.getDescription());
		
		if (md.getLink() != null)
			this.link.setHTML("<a href=\"" + md.getLink() + "\" target=\"_blank\">more...</a>");
	}
	
	private void loadMetadataAsync(String metadataURL) {
		MapMetadataServiceAsync service = (MapMetadataServiceAsync) GWT.create(MapMetadataService.class);
		service.getMetadata(metadataURL, new AsyncCallback<MapMetadata>() {
			@Override
			public void onSuccess(MapMetadata result) {
				if (result != null && !result.isEmpty()) setMetdata(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Do nothing
			}
		});
	}

}
