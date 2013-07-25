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

package at.ait.dme.yuma.client.map.tagcloud;

import java.util.ArrayList;

import org.gwt.mosaic.ui.client.InfoPanel;
import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.gwt.openlayers.client.Bounds;
import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.map.MapComponent;
import at.ait.dme.yuma.client.map.annotation.XYCoordinate;
import at.ait.dme.yuma.client.server.GeocoderService;
import at.ait.dme.yuma.client.server.GeocoderServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Implements 'high-level' methods to manipulate the tag cloud on
 * the OpenLayers map interface.
 *  
 * @author Rainer Simon
 */
public class TagCloudLayer {
	
	/**
	 * Tag cloud dimensions
	 */
	private static final int TAGCLOUD_WIDTH = 640;
	private static final int TAGCLOUD_HEIGHT = 550;
	
	/**
	 * We restrict to four font sizes...
	 */
	private static final int FONT_XL = 32;
	private static final int FONT_L = 24;
	private static final int FONT_M = 22;
	private static final int FONT_S = 18;
	
	/**
	 * The floating tag cloud
	 */
	private TagCloud tagCloud;
	
	/**
	 * Current list of tags
	 */
	private ArrayList<SemanticTag> geoTags = new ArrayList<SemanticTag>();	
	
	public TagCloudLayer(MapComponent mapComponent, TagSelectionListener listener) {
		tagCloud = new TagCloud(TAGCLOUD_WIDTH, TAGCLOUD_HEIGHT, mapComponent.getElement(), listener);
		tagCloud.hide();
	}
	
	public void centerAt(int x, int y) {
		tagCloud.moveTo(x, y);
	}
	
	public void addGeonamesSuggestions(Bounds bbox) {
		GeocoderServiceAsync service = (GeocoderServiceAsync) GWT.create(GeocoderService.class);
		service.getTags(Application.getExternalObjectId(), 
				Application.getImageUrl(), 
				new XYCoordinate((int)bbox.getLeft(), (int)bbox.getBottom()), 
				new XYCoordinate((int)bbox.getRight(), (int)bbox.getTop()), 
				new AsyncCallback<SemanticTag[]>(){
		    public void onSuccess(SemanticTag[] tags) {
	    		// 1. Remove all tags from the tag cloud which are NOT in this list
		    	for (int i=0; i<tags.length; i++) {
		    		geoTags.remove(tags[i]);
		    	}
	    		tagCloud.removeTags(geoTags.toArray(new SemanticTag[geoTags.size()]));
	    		
	    		// 2. Add the (new) tags
	    		geoTags.clear();
	    		int fontsize = FONT_XL;
	    		for (int i=0; i<tags.length; i++) {
	    			if (i > 1) fontsize = FONT_L;
	    			if (i > 3) fontsize = FONT_M;
	    			if (i > 5) fontsize = FONT_S;
	    			geoTags.add(tags[i]);
	    			tagCloud.addTag(tags[i], fontsize);
	    		}
	    		
	    		if (tags.length == 0) {
	    			InfoPanel.show("No Tag Suggestions Found", "There are no Geonames suggestions for this area. Try improving the geo-reference of your map by adding control points.");
	    		}
		    }
		    
			public void onFailure(Throwable t) {
				ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
				MessageBox.error(errorMessages.error(), t.getMessage());
			}
		});
	}
	
	public void addNERSuggestions(SemanticTag[] suggestions) {
		int fontsize = FONT_L;
		for (int i=0; i<suggestions.length; i++) {
			if (i > 4) fontsize = FONT_M;
			tagCloud.addTag(suggestions[i], fontsize, "#FFD77D");
		}
	}
	
	public void show() {
		tagCloud.show();
	}
	
	public void hide() {
		tagCloud.fadeoutAndClear();
	}

}
