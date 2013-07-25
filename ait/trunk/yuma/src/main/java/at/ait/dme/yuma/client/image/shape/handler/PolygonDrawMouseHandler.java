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

package at.ait.dme.yuma.client.image.shape.handler;

import at.ait.dme.yuma.client.image.shape.Point;
import at.ait.dme.yuma.client.image.shape.Polygon;
import at.ait.dme.yuma.client.image.shape.Shape;
import at.ait.dme.yuma.client.image.shape.ShapePanel;

import com.google.gwt.event.dom.client.MouseDownEvent;

/**
 * mouselistener used to draw a polygon.
 * 
 * @author Christian Sadilek
 *
 */
public class PolygonDrawMouseHandler extends ShapeMouseListenerAdapter {	
	private Polygon polygon = null;
	private ShapePanel panel = null;

	private Point start = null;
	private Point current = null;
	
	public PolygonDrawMouseHandler(Polygon polygon, ShapePanel panel) {
		this.polygon = polygon;
		this.panel = panel;
	}

	/**
	 * on each click we collect the point and draw a line from the previous point.
	 * if the new point is closer than 10 pixels to the first point we will close the polygon.
	 */
	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (start == null) {
            start = new Point(event.getX(), event.getY());
            current = start;
        } else if (Math.abs(start.getX() - event.getX()) < 10 && 
        		Math.abs(start.getY() - event.getY()) < 10) {
           panel.endDraw();
           return;
        }        
		
		if(current!=null) {		
			panel.drawLine(current.getX(), current.getY() , event.getX() - 1, event.getY() - 1);
			current = new Point(event.getX()-1,event.getY()-1);
			polygon.addPoint(current);
		}
	}

	public Shape getShape() {
		return polygon;
	}
}
