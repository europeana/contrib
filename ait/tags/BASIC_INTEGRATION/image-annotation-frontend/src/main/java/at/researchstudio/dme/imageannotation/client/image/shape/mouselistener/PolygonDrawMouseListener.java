package at.researchstudio.dme.imageannotation.client.image.shape.mouselistener;

import at.researchstudio.dme.imageannotation.client.image.shape.Point;
import at.researchstudio.dme.imageannotation.client.image.shape.Polygon;
import at.researchstudio.dme.imageannotation.client.image.shape.Shape;
import at.researchstudio.dme.imageannotation.client.image.shape.ShapePanel;

import com.google.gwt.event.dom.client.MouseDownEvent;

/**
 * mouselistener used to draw a polygon.
 * 
 * @author Christian Sadilek
 *
 */
public class PolygonDrawMouseListener extends ShapeMouseListenerAdapter {	
	private Polygon polygon = null;
	private ShapePanel panel = null;

	private Point start = null;
	private Point current = null;
	
	public PolygonDrawMouseListener(Polygon polygon, ShapePanel panel) {
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
