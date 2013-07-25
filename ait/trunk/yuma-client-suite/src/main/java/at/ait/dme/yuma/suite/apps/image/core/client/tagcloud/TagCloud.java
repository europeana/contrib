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

import org.gwt.mosaic.ui.client.InfoPanel;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.raphael.AnimationEventHandler;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.raphael.Raphael;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * The tag cloud class. Mostly an umbrella which provides the appropriate methods
 * for using the tag cloud + various constants for configuring it's visual
 * appearance. All the work is done in the {@link TagCloudQuadrant} class.
 * 
 * @author Rainer Simon
 */
public class TagCloud {
	
	/**
	 * Tag cloud font
	 */
	public static final String FONT = "Trebuchet MS";
	
	/**
	 * Color constants
	 */
	public static final String TAG_TEXT_COLOR = "#ffffff";
	public static final String DEFAULT_TEXT_HILITE_COLOR = "#EC786A";
	public static final String TAG_BACKGROUND_COLOR = "#000000";
	
	/**
	 * Tag background opacity
	 */
	public static final float TAG_BACKGROUND_OPACITY = 0.5f;
	
	/**
	 * Tag background corner radius
	 */
	public static final int TAG_CORNER_RADIUS = 5;
	
	/**
	 * 'Selected' icon properties
	 */
	public static final String TAG_SELECTED_ICON = "images/checkmark-small.png";
	public static final int TAG_SELECTED_ICON_WIDTH = 30;
	public static final int TAG_SELECTED_ICON_HEIGHT = 32;	
	
	/**
	 * Fade duration times (in milliseconds)
	 */
	public static final int FADE_IN_TIME = 800;
	public static final int FADE_OUT_TIME = 300;
	
	/**
	 * Empty 'buffer' space between tagcloud layout quadrants
	 */
	public static final int TAG_BUFFER_X = 14;
	public static final int TAG_BUFFER_Y = 3;
	
	/**
	 * Horizontal and vertical alignment constants - do not change! (Raphael-specific!)
	 */
	public static final String VALIGN_TOP = "top";
	public static final String VALIGN_BOTTOM = "bottom";
	public static final String HALIGN_START = "start";
	public static final String HALIGN_END = "end";
	
	/**
	 * The tag cloud DOM node
	 */
	private Element domNode;
	
	/**
	 * Raphael drawing panel
	 */
	private Raphael paper;
	
	/**
	 * Tag cloud center coordinates
	 */
	private int halfWidth;
	private int halfHeight;
	
	/**
	 * The tag cloud layout quadrants
	 */
	private TagCloudQuadrant[] quadrants;
	
	/**
	 * Visibility flag;
	 */
	private boolean visible = false;
	
	/**
	 * Creates a new tag cloud with the specified canvas width and height.
	 * @param width the width of the tagcloud canvas
	 * @param height the height of the tagcloud canvas
	 * @param parentNode a DOM node to attach the tag cloud DIV to 
	 */
	public TagCloud(int width, int height, Element parentNode) {
		this(width, height, parentNode, null);
	}	
	
	public TagCloud(int width, int height, Element parentNode, TagSelectionListener listener) {
		this.domNode = DOM.createDiv();
		parentNode.appendChild(domNode);
		_init(domNode);
		paper = Raphael.create(domNode, width, height);
		
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		
		quadrants = new TagCloudQuadrant[]{
				new TagCloudQuadrant(halfWidth - TAG_BUFFER_X / 2, halfHeight - TAG_BUFFER_Y / 2 - 1, halfWidth - TAG_BUFFER_X / 2, "end", "bottom", paper, listener), // top left
				new TagCloudQuadrant(halfWidth + TAG_BUFFER_X / 2, halfHeight - TAG_BUFFER_Y / 2 - 1, halfWidth - TAG_BUFFER_X / 2, "start", "bottom", paper, listener), // top right
				new TagCloudQuadrant(halfWidth - TAG_BUFFER_X / 2, halfHeight + TAG_BUFFER_Y / 2, halfWidth - TAG_BUFFER_X / 2, "end", "top", paper, listener), // bottom left
				new TagCloudQuadrant(halfWidth + TAG_BUFFER_X / 2, halfHeight + TAG_BUFFER_Y / 2, halfWidth - TAG_BUFFER_X / 2, "start", "top", paper, listener) // bottom right
		};		

		Event.addNativePreviewHandler(new NativePreviewHandler() {
			
			private boolean keyIsUp = true;
			
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONKEYDOWN) {
					if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ALT) {
						if (keyIsUp) { // Prevent re-firing of event on pressed keys
							keyIsUp = false;
							InfoPanel.show("Tag Cloud Hidden", "Release ALT key to show the tag cloud again.");
							_hide(domNode);							
						}
					}
				} else if (event.getTypeInt() == Event.ONKEYUP) {
					if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ALT) {
						_show(domNode);
						keyIsUp = true;
					}					
				}
			}
		});
	}
	
	private static native void _init(JavaScriptObject domNode) /*-{	
		domNode.style.position = 'absolute';
		domNode.style.zIndex = 999;
		domNode.style.backgroundImage = 'url("img/blank.gif")';
	}-*/;
	
	/**
	 * Show the tag cloud.
	 */
	public final void show() {
		visible = true;
		_show(domNode);
	}
	
	private final native void _show(JavaScriptObject domNode) /*-{
		domNode.style.visibility = 'visible';
	}-*/;
	
	/**
	 * Hides the tag cloud.
	 */
	public final void hide() {
		visible = false;
		_hide(domNode);
	}
	
	private final native void _hide(JavaScriptObject domNode) /*-{
		domNode.style.visibility = 'hidden';		
	}-*/;
	
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Moves the center of the tag cloud to the specified viewport coordinates.
	 * @param x the x viewport coordinate
	 * @param y the y viewport coordinate
	 */
	public void moveTo(int x, int y) {
		_moveTo(x - halfWidth, y - halfHeight, domNode);
	}
	
	private final native void _moveTo(int x, int y, JavaScriptObject domNode) /*-{
		domNode.style.left = x + 'px';
		domNode.style.top = y + 'px';
	}-*/;
	
	/**
	 * Clears the tag cloud 'gracefully' - i.e. fades out the tags first and
	 * then removes them from the DOM and hides the tag cloud DIV.
	 */
	public void fadeoutAndClear() {
		for (int i=0; i<quadrants.length; i++) {
			quadrants[i].fadeoutAndClear(new AnimationEventHandler() {
				@Override
				public void onAnimationComplete() {
					_hide(domNode);
				}
			});
		}
	}
	
	private boolean contains(SemanticTag tag) {
		for (int i=0; i<quadrants.length; i++) {
			if (quadrants[i].contains(tag)) return true;
		}
		return false;
	}
	
	
	private TagCloudQuadrant getLeastUsed() {
		TagCloudQuadrant leastUsed = quadrants[0];
		for (int i=1; i<quadrants.length; i++) {
			if (quadrants[i].size() < leastUsed.size()) leastUsed = quadrants[i];
		}
		return leastUsed;
	}
	
	/**
	 * Adds the tag to the tag cloud, unless it already exists.
	 * @param tag the tag
	 * @param fontsize the fontsize
	 */
	public void addTag(SemanticTag tag, int fontsize) {
		addTag(tag, fontsize, DEFAULT_TEXT_HILITE_COLOR);
	}
	
	/**
	 * Adds the tag to the tag cloud, unless it already exists.
	 * @param tag the tag
	 * @param fontsize the fontsize
	 * @param hiliteColor the hilite color (i.e. text color on mouse-over) 
	 */
	public void addTag(SemanticTag tag, int fontsize, String hiliteColor) {
		if (!this.contains(tag)) {	
			getLeastUsed().addTag(tag, fontsize, hiliteColor);
		}
	}
	
	/**
	 * Removes the tag from the tag cloud.
	 * @param tag the tag
	 */
	public void removeTag(SemanticTag tag) {
		// No need to check whether tag exists in a 
		// quadrant - quadrants will handle themselves
		for (int i=0; i<quadrants.length; i++) {
			quadrants[i].removeTag(tag);
		}
	}
	
	public void removeTags(SemanticTag[] tags) {
		// No need to check whether tag exists in a 
		// quadrant - quadrants will handle themselves
		for (int i=0; i<quadrants.length; i++) {
			quadrants[i].removeTags(tags);
		}
	}
	
}
