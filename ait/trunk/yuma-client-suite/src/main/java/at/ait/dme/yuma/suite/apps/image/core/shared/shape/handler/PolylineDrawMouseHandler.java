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

package at.ait.dme.yuma.suite.apps.image.core.shared.shape.handler;

import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Polyline;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Shape;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.ShapePanel;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;

/**
 * mouselistener used to draw a polyline. 
 * 
 * @author Christian Sadilek
 *
 */
public class PolylineDrawMouseHandler extends ShapeMouseListenerAdapter {

	private Polyline polyline = null;
	private ShapePanel panel = null;

	private Point start = null;
	private Point current = null;
	
	public PolylineDrawMouseHandler(Polyline polyline, ShapePanel panel) {
		this.polyline = polyline;
		this.panel = panel;
	}

	/**
	 * first click starts the draw mode, the second click ends it
	 */
	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (start == null) {
            start = new Point(event.getX(), event.getY());
            current = start;
        } else {
            panel.endDraw();
            return;
        }        
	}
	/**
	 * on mouse move a line is drawn and the new point collected
	 */
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(current!=null) {
			panel.drawLine(current.getX(), current.getY() , event.getX() - 1, event.getY() - 1);
			current = new Point(event.getX()-1, event.getY()-1);
			polyline.addPoint(current);
		}
	}

	@Override
	public Shape getShape() {
		return polyline;
	}
}
