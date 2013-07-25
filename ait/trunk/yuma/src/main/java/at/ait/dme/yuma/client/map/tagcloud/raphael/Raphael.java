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
 * Wrapper for the Raphael main class.
 *  
 * @author Rainer Simon
 */
public class Raphael extends JavaScriptObject {

	protected Raphael() {}
	
	public static native Raphael create(JavaScriptObject domNode, int width, int height) /*-{
		return new $wnd.Raphael(domNode, width, height);
	}-*/;
	
	public final native void clear() /*-{
		this.clear();
	}-*/;
	
	public final native Image image(String url, int x, int y, int width, int height) /*-{
		return this.image(url, x, y, width, height);
	}-*/;
	
	public final native Text text(int x, int y, String s, String font, int fontsize, String hAlign, String fill, float opacity) /*-{
		return this.text(x, y, s).attr({'font-family':font, 'font-size':fontsize+'px', fill:fill, opacity:opacity, 'text-anchor':hAlign});	
	}-*/;
	
	public final native Rect rect(int x, int y, int width, int height, int cornerRadius, String fill, String stroke, float opacity) /*-{
		return this.rect(x, y, width, height, cornerRadius).attr({fill:fill, stroke:stroke, opacity:opacity}); 
	}-*/;
	
	public final native Rect rect(int x, int y, int width, int height, int cornerRadius, String fill, String stroke, float opacity, MouseEventHandler handler) /*-{
		return this.rect(x, y, width, height, cornerRadius).attr({fill:fill, stroke:stroke, opacity:opacity}).mouseover(
			function(event) { handler.@at.ait.dme.yuma.client.map.tagcloud.raphael.MouseEventHandler::onMouseOver(Lcom/google/gwt/dom/client/NativeEvent;)(event); }
		).mousemove(
			function(event) { handler.@at.ait.dme.yuma.client.map.tagcloud.raphael.MouseEventHandler::onMouseMove(Lcom/google/gwt/dom/client/NativeEvent;)(event); }
		).mouseout(
			function(event) { handler.@at.ait.dme.yuma.client.map.tagcloud.raphael.MouseEventHandler::onMouseOut(Lcom/google/gwt/dom/client/NativeEvent;)(event); }
		).click(
			function(event) { handler.@at.ait.dme.yuma.client.map.tagcloud.raphael.MouseEventHandler::onClick(Lcom/google/gwt/dom/client/NativeEvent;)(event); }
		); 
	}-*/;
	
}
