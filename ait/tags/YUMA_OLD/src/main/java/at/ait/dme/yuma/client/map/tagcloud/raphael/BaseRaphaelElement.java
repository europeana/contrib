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

package at.ait.dme.yuma.client.map.tagcloud.raphael;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Base class for Raphael vector drawing elements.
 * 
 * @author Rainer Simon
 */
public class BaseRaphaelElement extends JavaScriptObject {
	
	protected BaseRaphaelElement() {}

	public final native void show() /*-{
		this.show();
	}-*/;

	public final native void hide() /*-{
		this.hide();
	}-*/;
	
	public final native void toBack() /*-{
		this.toBack();
	}-*/;
	
	public final native void toFront() /*-{
		this.toFront();
	}-*/;
	
	public final native void remove() /*-{
		this.remove();
	}-*/;

	public final native void setCursorStyle(String style) /*-{
		this.node.style.cursor = style;
	}-*/;
	
	public final native void setFillColor(String color) /*-{
		this.attr({fill:color});
	}-*/;
	
	public final native void translate(int dx, int dy) /*-{
		this.translate(dx, dy);
	}-*/;
	
	public final native void fade(float opacity, int duration, int delay) /*-{
		this.animate({translate:'0,0'}, delay, function() {
				this.animate({opacity:opacity}, duration);
		});
	}-*/;
	
	public final native void fade(float opacity, int duration, int delay, AnimationEventHandler handler) /*-{
		this.animate({translate:'0,0'}, delay, function() {
				this.animate({opacity:opacity}, duration, function() {
					handler.@at.ait.dme.yuma.client.map.tagcloud.raphael.AnimationEventHandler::onAnimationComplete()();
				});
		});
	}-*/;
	
}
