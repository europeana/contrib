package at.researchstudio.dme.imageannotation.client.image.shape.mouselistener;

import at.researchstudio.dme.imageannotation.client.image.shape.Shape;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

/**
 * base class for shape mouse listeners
 * 
 * @author Christian Sadilek
 */
public abstract class ShapeMouseListenerAdapter implements MouseDownHandler, MouseMoveHandler {
	/**
	 * returns the created shape.
	 * 
	 * @return shape
	 */
	public abstract Shape getShape();
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {};
	
	@Override
	public void onMouseDown(MouseDownEvent event) {};
}
