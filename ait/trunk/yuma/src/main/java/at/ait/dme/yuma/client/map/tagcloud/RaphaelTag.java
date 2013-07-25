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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.map.tagcloud.raphael.AnimationEventHandler;
import at.ait.dme.yuma.client.map.tagcloud.raphael.BBox;
import at.ait.dme.yuma.client.map.tagcloud.raphael.Image;
import at.ait.dme.yuma.client.map.tagcloud.raphael.MouseEventHandler;
import at.ait.dme.yuma.client.map.tagcloud.raphael.Raphael;
import at.ait.dme.yuma.client.map.tagcloud.raphael.Rect;
import at.ait.dme.yuma.client.map.tagcloud.raphael.Text;

/**
 * The visual representation of a SemanticTag on the
 * Raphael drawing canvas.
 * 
 * @author Rainer Simon
 */
public class RaphaelTag {

	/**
	 * The semantic tag
	 */
	private SemanticTag tag;
	
	/**
	 * The tag's fontsize
	 */
	private int fontsize;
	
	/**
	 * The tag's hilite color
	 */
	private String hiliteCol;
	
	/**
	 * The Raphael drawing elements
	 */
	private Rect backgroundEl;
	private Text textEl;
	private Rect foregroundEl;
	private Image iconEl;
	
	/**
	 * This tag's tooltip
	 */
	private PopupPanel tooltip;
	
	/**
	 * This tag's dimensions
	 */
	private BBox bbox;
	
	/**
	 * Flag indicating whether tag is currently selected
	 */
	boolean isSelected = false;

	/**
	 * The tag selection listener
	 */
	private TagSelectionListener listener;
	
	public RaphaelTag(SemanticTag tag, int x, int y, int fontsize, String hiliteColor, String hAlign, String vAlign, Raphael paper, TagSelectionListener listener) {
		this.tag = tag;
		this.fontsize = fontsize;
		this.hiliteCol = hiliteColor;
		this.listener = listener;
		
		// Text
		textEl = paper.text(x, y, tag.getTitle(), TagCloud.FONT, fontsize, hAlign, TagCloud.TAG_TEXT_COLOR, 0);
		bbox = textEl.getBBox();
		
		// Background
		backgroundEl = paper.rect(
			(int)bbox.getX() - 5,
			(int)bbox.getY(),
			(int)bbox.getWidth() + 10,
			(int)bbox.getHeight(),
			TagCloud.TAG_CORNER_RADIUS,
			TagCloud.TAG_BACKGROUND_COLOR,
			TagCloud.TAG_BACKGROUND_COLOR,
			0
		); 
		backgroundEl.toBack(); 
		
		// Foreground (invisible - only for catching mouse events!)
		foregroundEl = paper.rect(
			(int)bbox.getX(),
			(int)bbox.getY(),
			(int)bbox.getWidth(),
			(int)bbox.getHeight(),
			0,
			TagCloud.TAG_TEXT_COLOR,
			TagCloud.TAG_TEXT_COLOR,
			0,
			new MouseEventHandler() {
				@Override
				public void onMouseOver(NativeEvent event) {
					textEl.setFillColor(hiliteCol);
					tooltip.setPopupPosition(event.getClientX() + 15, event.getClientY());
					tooltip.show();
				}
				
				@Override
				public void onMouseMove(NativeEvent event) {
					tooltip.setPopupPosition(event.getClientX() + 15, event.getClientY());				
				}
				
				@Override
				public void onMouseOut(NativeEvent event) {
					textEl.setFillColor(TagCloud.TAG_TEXT_COLOR);
					tooltip.hide();
				}
				
				@Override
				public void onClick(NativeEvent event) {
					setSelected(!isSelected);
				}
			}
		);
		foregroundEl.setCursorStyle("pointer");
		foregroundEl.toFront(); 
		
		// 'Selected' icon
		iconEl = paper.image(
			TagCloud.TAG_SELECTED_ICON,
			(int)bbox.getX() + (int)bbox.getWidth() - 15,
			(int)bbox.getY() + (int)bbox.getHeight() - 30,
			TagCloud.TAG_SELECTED_ICON_WIDTH,
			TagCloud.TAG_SELECTED_ICON_HEIGHT
		);
		iconEl.hide();
		
		// Adjust vertical alignment
		int dy;
		if (vAlign.equals(TagCloud.VALIGN_BOTTOM)) {
			dy = -(int)bbox.getHeight()/2;
			textEl.translate(0, dy);
			backgroundEl.translate(0, dy);
			foregroundEl.translate(0, dy);
			iconEl.translate(0, dy);
		} else if (vAlign.equals(TagCloud.VALIGN_TOP)) {
			dy = (int)bbox.getHeight()/2;
			textEl.translate(0, dy);
			backgroundEl.translate(0, dy);
			foregroundEl.translate(0, dy);
			iconEl.translate(0, dy);
		}
		
		// Tooltip
		tooltip = new PopupPanel();
		tooltip.setStyleName("tooltip");
		tooltip.setWidget(new HTML(tag.getDescription() + "<br/><b>" + tag.getURI()));
		tooltip.hide();
	}
	
	public SemanticTag getTag() {
		return tag;
	}
	
	public int getFontsize() {
		return fontsize;
	}
	
	public String getHiliteColor() {
		return hiliteCol;
	}
	
	public void setSelected(boolean selected) {
		isSelected = selected;
		if (selected) {
			iconEl.show();
		} else {
			iconEl.hide();
		}
		
		if (listener != null) listener.onTagSelected(tag, selected);
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public int getWidth() {
		return (int)bbox.getWidth();
	}
	
	public int getHeight() {
		return (int)bbox.getHeight();
	}

	public void translate(int dx, int dy) {
		backgroundEl.translate(dx, dy);
		textEl.translate(dx, dy);
		foregroundEl.translate(dx, dy);
		iconEl.translate(dx, dy);
	}
	
	public void fade(float opacity, int duration, int delay) {
		fade(opacity, duration, delay, null);
	}
	
	public void fade(float opacity, int duration, int delay, AnimationEventHandler handler) {
		if (handler != null) {
			// Attach handler to background ONLY (so that it is only called once for entire RaphaelTag)
			backgroundEl.fade(opacity * TagCloud.TAG_BACKGROUND_OPACITY, duration, delay, handler);
		} else {
			backgroundEl.fade(opacity * TagCloud.TAG_BACKGROUND_OPACITY, duration, delay);
		}
		textEl.fade(opacity, duration, delay);
		iconEl.fade(opacity, duration, delay);
	}
	
	public void remove() {
		backgroundEl.remove();
		textEl.remove();
		foregroundEl.remove();
		iconEl.remove();
	}
}
