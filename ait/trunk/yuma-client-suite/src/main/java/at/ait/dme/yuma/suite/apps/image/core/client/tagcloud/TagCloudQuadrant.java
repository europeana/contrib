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

package at.ait.dme.yuma.suite.apps.image.core.client.tagcloud;

import java.util.ArrayList;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.raphael.AnimationEventHandler;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.raphael.Raphael;

import com.google.gwt.user.client.Random;

/**
 * A tag cloud quadrant. Each tag cloud consists of four quadrants
 * arranged as a 2x2 matrix. This class does all the actual work 
 * of the tag cloud (drawing tags on the canvas, fading, handling
 * mouse events, etc.)
 * 
 * @author Rainer Simon
 */
public class TagCloudQuadrant {
	
	/**
	 * Reference to the Raphael drawing canvas
	 */
	private Raphael paper;
	
	/**
	 * The coordinates of this quadrant's origin on the canvas
	 */
	private int originX;
	private int originY;
	
	/**
	 * The width of the quadrant's share of the canvas
	 */
	private int maxWidth;

	/**
	 * Horizontal and vertical alignment of this quadrant 
	 */
	private String hAlign;
	private String vAlign;
	
	/**
	 * X/Y cursor directions 
	 */
	private int dirX;
	private int dirY;
	
	/**
	 * Current cursor position on the canvas
	 */
	private int offsetX = 0;
	private int offsetY = 0;
	
	/**
	 * Current line height
	 */
	private int lineHeight = 0;
	
	/**
	 * The tags in this quadrant
	 */
	private ArrayList<RaphaelTag> tagList = new ArrayList<RaphaelTag>();
	
	/**
	 * Counter for tracking whether all tags have been faded out
	 */
	private int fadeCtr = 0;
	
	/**
	 * The tag selection listener
	 */
	private TagSelectionListener listener;
	
	public TagCloudQuadrant(int originX, int originY, int maxWidth, String hAlign, String vAlign, Raphael paper, TagSelectionListener listener) {
		this.originX = originX;
		this.originY = originY;
		
		this.maxWidth = maxWidth;
		
		this.hAlign = hAlign;
		this.vAlign = vAlign;
		
		if (hAlign.equalsIgnoreCase("end")) {
			dirX = -1;
		} else {
			dirX = 1;
		}
		
		if (vAlign.equalsIgnoreCase("top")) {
			dirY = 1;
		} else {
			dirY = -1;
		}
		
		this.paper = paper;
		
		this.listener = listener;
	}
	
	public void addTag(SemanticTag tag, int fontsize, String hiliteColor) {
		boolean firstTagInLine = false;	
		RaphaelTag rTag = new RaphaelTag(
			tag,
			originX + offsetX,
			originY + offsetY,
			fontsize,
			hiliteColor,
			hAlign,
			vAlign,
			paper,
			listener
		);
		
		if (offsetX == 0) {
			lineHeight = rTag.getHeight() + TagCloud.TAG_BUFFER_Y; // First tag in line
			firstTagInLine = true;
		}
		
		offsetX += dirX * (rTag.getWidth() + TagCloud.TAG_BUFFER_X);
		
		if ((!firstTagInLine) && (Math.abs(offsetX) > maxWidth)) {	
			rTag.translate(dirX * (rTag.getWidth() + TagCloud.TAG_BUFFER_X) - offsetX, lineHeight * dirY);
			offsetX = dirX * (rTag.getWidth() + TagCloud.TAG_BUFFER_X);
			offsetY += dirY * lineHeight;
			lineHeight = rTag.getHeight() + TagCloud.TAG_BUFFER_Y;
		}
		
		rTag.fade(1, TagCloud.FADE_IN_TIME, Random.nextInt(20) * 100);
		tagList.add(rTag);
	}
	
	public void fadeoutAndClear(final AnimationEventHandler handler) {
		// Reset tag cloud properties
		offsetX = 0;
		offsetY = 0;
		lineHeight = 0;

		// Fade out all tags on the canvas
		final int limit = tagList.size();	
		if (limit == 0 && handler != null) handler.onAnimationComplete();
		
		for (RaphaelTag tag : tagList) {			
			tag.fade(0, TagCloud.FADE_OUT_TIME, Random.nextInt(20) * 50, new AnimationEventHandler() {
				@Override
				public void onAnimationComplete() {
					fadeCtr++;
					if (fadeCtr == limit) {
						fadeCtr = 0;
						if (handler != null) handler.onAnimationComplete();
					}
				}
			});
		}
		
		// Clear the tag list
		tagList.clear();
	}
	
	private int indexOf(SemanticTag t) {
		return indexOf(t, tagList);
	}
	
	private int indexOf(SemanticTag t, ArrayList<RaphaelTag> list) {
		for (int i=0; i<list.size(); i++) {
			if (list.get(i).getTag().equals(t)) return i;
		}
		return -1;	
	}
	
	public boolean contains(SemanticTag t) {
		return (indexOf(t) > -1);
	}
	
	public void removeTag(SemanticTag tag) {
		removeTags(new SemanticTag[] { tag });
	}
	
	@SuppressWarnings("unchecked")
	public void removeTags(SemanticTag[] tags) {
		// 1. Create a new list, with unwanted tags removed
		ArrayList<RaphaelTag> newList = (ArrayList<RaphaelTag>) tagList.clone();
		
		int idx;
		for (int i=0; i<tags.length; i++) {
			idx = indexOf(tags[i], newList);
			if (idx > -1) {
				newList.remove(idx);
			}
		}
		
		// No action if no tags removed
		if (newList.size() < tagList.size()) {
			// 2. List complete - clear canvas
			fadeoutAndClear(null);
			
			// 3. Re-draw canvas with new list of tags
			for (RaphaelTag r : newList) {
				addTag(r.getTag(), r.getFontsize(), r.getHiliteColor());
			}
		}
	}
	
	public int size() {
		return tagList.size();
	}

}
