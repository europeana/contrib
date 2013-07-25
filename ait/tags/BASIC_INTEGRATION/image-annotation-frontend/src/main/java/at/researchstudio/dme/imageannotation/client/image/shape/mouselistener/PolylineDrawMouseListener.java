package at.researchstudio.dme.imageannotation.client.image.shape.mouselistener;

import at.researchstudio.dme.imageannotation.client.image.shape.Point;
import at.researchstudio.dme.imageannotation.client.image.shape.Polyline;
import at.researchstudio.dme.imageannotation.client.image.shape.Shape;
import at.researchstudio.dme.imageannotation.client.image.shape.ShapePanel;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;

/**
 * mouselistener used to draw a polyline. 
 * 
 * @author Christian Sadilek
 *
 */
public class PolylineDrawMouseListener extends ShapeMouseListenerAdapter {

	private Polyline polyline = null;
	private ShapePanel panel = null;

	private Point start = null;
	private Point current = null;
	
	public PolylineDrawMouseListener(Polyline polyline, ShapePanel panel) {
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
