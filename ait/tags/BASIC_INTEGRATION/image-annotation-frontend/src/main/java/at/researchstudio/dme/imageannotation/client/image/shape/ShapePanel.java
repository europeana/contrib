package at.researchstudio.dme.imageannotation.client.image.shape;

import java.util.ArrayList;
import java.util.Collection;

import org.gwtwidgets.client.style.Color;
import org.gwtwidgets.client.style.Coords;
import org.gwtwidgets.client.wrap.JsGraphicsPanel;

import at.researchstudio.dme.imageannotation.client.dnd.Resizable;
import at.researchstudio.dme.imageannotation.client.dnd.ResizeDragController;
import at.researchstudio.dme.imageannotation.client.image.ImageFragmentContainer;
import at.researchstudio.dme.imageannotation.client.image.ImageFragmentSelectionListener;
import at.researchstudio.dme.imageannotation.client.image.shape.mouselistener.PolygonDrawMouseListener;
import at.researchstudio.dme.imageannotation.client.image.shape.mouselistener.PolylineDrawMouseListener;
import at.researchstudio.dme.imageannotation.client.image.shape.mouselistener.ShapeMouseListenerAdapter;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * adapted from fred sauer's dnd samples
 * wraps a JsGraphicsPanel and makes it draggable and resizable
 * 
 * @author Christian Sadilek
 */
public class ShapePanel extends AbsolutePanel implements Resizable, ImageFragmentSelectionListener {	
	private static final int MARKER_RECT_IMAGE_SIZE = 8;
	private static final int SHAPE_MIN_WIDTH = 10;
	private static final int SHAPE_MIN_HEIGHT = 10;

	// the color to use when selecting a shape (on mouse over)
	private static final Color SELECTION_COLOR = Color.ORANGE;
	
	// counter is used to generate unique ids for the shapes
	private static int counter = 0;
	
	// describes the boundary for dragging, it has to be the parent of this panel
	private AbsolutePanel boundaryPanel;

	// contains the shape (JsGraphicsPanel) and makes it draggable
	private FocusPanel dragPanel = new FocusPanel();

	// the shape, a JsGraphicsPanel is always embedded in a <div>
	private JsGraphicsPanel graphicsPanel;
	private static HTML graphicsDiv;

	// this widget my be passed to the constructor and will be repositioned when the
	// panel is dragged or resized
	private PopupPanel shapeControlPanel;	
	
	// the shape
	private Shape shape;	
	private ShapeMouseListenerAdapter shapeMouseListener = null;
	private HandlerRegistration mouseDownRegistration = null;
	private HandlerRegistration mouseMoveRegistration = null;
	
	// draw mode active
	private boolean drawing = false;	

	// the images to resize the shape
	private Image northWestImage = new Image("images/marker_rect.gif");
	private Image northEastImage = new Image("images/marker_rect.gif");
	private Image southWestImage = new Image("images/marker_rect.gif");
	private Image southEastImage = new Image("images/marker_rect.gif");
	
	// the drag controller
	private PickupDragController pickupDragController;
	private ResizeDragController resizeDragController;

	/**
	 * construct a new shape panel.
	 * 
	 * @param boundaryPanel defines the boundary for dragging
	 * @param shape the shape to display
	 * @param draggable true if draggable, otherwise false
	 * @param resizable true if resizable, otherwise false
	 */
	public ShapePanel(final AbsolutePanel boundaryPanel, final Shape shape, 
			boolean draggable, boolean resizable) {
		this.shape = shape;
		this.boundaryPanel = boundaryPanel;		
		this.add(dragPanel);
		createShape();
		
		if(draggable) makeDraggable();						
		if(resizable) makeResizable();		
	}	
	
	/**
	 * make the shape panel draggable
	 */
	private void makeDraggable() {
		pickupDragController = new PickupDragController(boundaryPanel, true);
		pickupDragController.setBehaviorConstrainedToBoundaryPanel(true);
		pickupDragController.makeDraggable(this, dragPanel);
		pickupDragController.registerDropController(
				new AbsolutePositionDropController(boundaryPanel) {
			public void onMove(DragContext context) {
				super.onMove(context);		
			
				if(shapeControlPanel!=null) {
					shapeControlPanel.setPopupPosition(
							Math.max(boundaryPanel.getAbsoluteLeft(), getAbsoluteLeft()), 
							getAbsoluteTop() + getShape().getHeight() + 10);
				}
				
				int top=Math.min(boundaryPanel.getOffsetHeight()-shape.getHeight(),
						context.desiredDraggableY-boundaryPanel.getAbsoluteTop());			
				int left=Math.min(boundaryPanel.getOffsetWidth()-shape.getWidth(),
						context.desiredDraggableX-boundaryPanel.getAbsoluteLeft());
				
				// -1 px for the boundary panel's border
				shape.setLeft(Math.max(left-1,0));
				shape.setTop(Math.max(top-1,0));
			}
		});
	}
	
	/**
	 * make the shape panel resizable
	 */
	private void makeResizable() {
		resizeDragController = new ResizeDragController(boundaryPanel, this);
		resizeDragController.setBehaviorConstrainedToBoundaryPanel(true);
		createResizeMarkers(shape.getWidth(), shape.getHeight());
	}
	
	/**
	 * create the resize markers.
	 * 
	 * @param width
	 * @param height
	 */
	private void createResizeMarkers(int width, int height) {
		this.add(northWestImage, 0, 0);
		resizeDragController.makeDraggable(northWestImage, ResizeDragController.NORTH_WEST);
		DOM.setStyleAttribute(northWestImage.getElement(), ResizeDragController.CSS_CURSOR, 
				ResizeDragController.NORTH_WEST.directionLetters + 
				ResizeDragController.CSS_CURSOR_RESIZE);

		this.add(northEastImage, width - MARKER_RECT_IMAGE_SIZE, 0);
		resizeDragController.makeDraggable(northEastImage, ResizeDragController.NORTH_EAST);
		DOM.setStyleAttribute(northEastImage.getElement(), ResizeDragController.CSS_CURSOR, 
				ResizeDragController.NORTH_EAST.directionLetters + 
				ResizeDragController.CSS_CURSOR_RESIZE);

		this.add(southWestImage, 0, height - MARKER_RECT_IMAGE_SIZE);
		resizeDragController.makeDraggable(southWestImage, ResizeDragController.SOUTH_WEST);
		DOM.setStyleAttribute(southWestImage.getElement(), ResizeDragController.CSS_CURSOR, 
				ResizeDragController.SOUTH_WEST.directionLetters + 
				ResizeDragController.CSS_CURSOR_RESIZE);

		this.add(southEastImage, width - MARKER_RECT_IMAGE_SIZE, height - MARKER_RECT_IMAGE_SIZE);
		resizeDragController.makeDraggable(southEastImage, ResizeDragController.SOUTH_EAST);
		DOM.setStyleAttribute(southEastImage.getElement(), ResizeDragController.CSS_CURSOR, 
				ResizeDragController.SOUTH_EAST.directionLetters + 
				ResizeDragController.CSS_CURSOR_RESIZE);
	}

	/**
	 * reposition the resize markers after the panel has been moved or resized
	 */
	private void repositionResizeMarkers() {
		northWestImage.setVisible(true);
		northEastImage.setVisible(true);
		southWestImage.setVisible(true);
		southEastImage.setVisible(true);
		
		setWidgetPosition(northWestImage, 0, 0);
		setWidgetPosition(northEastImage, shape.getWidth() - MARKER_RECT_IMAGE_SIZE, 0);
		setWidgetPosition(southWestImage, 0, shape.getHeight() - MARKER_RECT_IMAGE_SIZE);
		setWidgetPosition(southEastImage, shape.getWidth() - MARKER_RECT_IMAGE_SIZE, 
				shape.getHeight() - MARKER_RECT_IMAGE_SIZE);
	}
	
	/**
	 * hide the resize markers
	 */
	private void hideResizeMarkers() {
		northWestImage.setVisible(false);
		northEastImage.setVisible(false);
		southWestImage.setVisible(false);
		southEastImage.setVisible(false);
	}
	
	/**
	 * create the shape
	 */
	private void createShape() {
		int width = shape.getWidth();
		int height = shape.getHeight();		
		
		resetGraphicsPanel(width, height);
		drawShape();
		
		DOM.setStyleAttribute(dragPanel.getElement(), 
				"background", "transparent url(images/empty.gif)"); 
		dragPanel.add(graphicsPanel);
		dragPanel.setPixelSize(width, height);
		
		setPixelSize(width + shape.getStrokeWidth(), height + shape.getStrokeWidth());
	}	
	
	/**
	 * draw the shape
	 */
	private void drawShape() {
		if (shape instanceof Ellipse) {
			graphicsPanel.drawEllipse(0, 0, shape.getWidth(), shape.getHeight());
		} else if (shape instanceof Rectangle) {
			graphicsPanel.drawRect(0, 0, shape.getWidth(), shape.getHeight());
		} else if (shape instanceof Cross) {
			for(Line line : ((Cross)shape).getLines()) {
				graphicsPanel.drawLine(line.getStart().getX(), line.getStart().getY(), 
						line.getEnd().getX(), line.getEnd().getY());
			}
		} else if (shape instanceof Polyline) {
			Collection<Point> points = ((Polyline)shape).getPoints();
			if(!points.isEmpty()) {
				int xPoints[] = new int[points.size()];
				int yPoints[] = new int[points.size()];

				int i = 0;				
				for(Point point : points) {
					xPoints[i] = point.getX()-((Polyline)shape).getRelativeLeft(); 
					yPoints[i] = point.getY()-((Polyline)shape).getRelativeTop(); 
					i++;
				}
				graphicsPanel.drawPolyline(xPoints,yPoints);
			}
		} else if (shape instanceof Polygon) {
			Collection<Point> points = ((Polygon)shape).getPoints();
			ArrayList<Coords> coords = new ArrayList<Coords>();
			if(!points.isEmpty()) {
				for(Point point : points) {
					coords.add(new Coords(point.getX()-((Polygon)shape).getRelativeLeft(), 
							point.getY()-((Polygon)shape).getRelativeTop()));
				}
				graphicsPanel.drawPolygon(coords);
			}
		}
		graphicsPanel.paint();
	}
	
	/**
	 * draw a line.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {				
		graphicsPanel.drawLine(x1, y1, x2, y2);
		graphicsPanel.paint();
	}
	
	/**
	 * activate the draw mode for a polygon.
	 * 
	 * @param polygon
	 */
	public void startDrawPolygon(Polygon polygon) {
		shapeMouseListener = new PolygonDrawMouseListener(polygon, this);
		startDraw();
	}

	/**
	 * activate the draw mode for a polyline.
	 * 
	 * @param polygon
	 */
	public void startDrawPolyline(Polyline polyline) {
		shapeMouseListener = new PolylineDrawMouseListener(polyline, this);
		startDraw();
	}


	/**
	 * start the draw mode
	 */
	private void startDraw() {
		if(drawing) endDraw();		
		drawing = true;
		
		dragPanel.remove(graphicsPanel);
		resetGraphicsPanel(boundaryPanel.getOffsetWidth(), boundaryPanel.getOffsetHeight());

		pickupDragController.makeNotDraggable(this);

		mouseDownRegistration=dragPanel.addMouseDownHandler(shapeMouseListener);
		mouseMoveRegistration=dragPanel.addMouseMoveHandler(shapeMouseListener);							
		dragPanel.add(graphicsPanel);	
		setSize(boundaryPanel.getOffsetWidth(), boundaryPanel.getOffsetHeight());
		moveBy(1+boundaryPanel.getWidgetLeft(this)*-1, 1+boundaryPanel.getWidgetTop(this)*-1);
		hideResizeMarkers();
		
		DOM.setStyleAttribute(getElement(), ResizeDragController.CSS_CURSOR, 
				ResizeDragController.CSS_CURSOR_DEFAULT);
	}
	
	/**
	 * end the draw mode
	 */
	public void endDraw() {
		dragPanel.remove(graphicsPanel);

		mouseDownRegistration.removeHandler();
		mouseMoveRegistration.removeHandler();
		shape=shapeMouseListener.getShape();
		createShape();
		pickupDragController.makeDraggable(this, dragPanel);
		
		moveBy(((Polygon)shape).getRelativeLeft(), ((Polygon)shape).getRelativeTop());
		
		int newLeft = Math.max(boundaryPanel.getAbsoluteLeft(), getAbsoluteLeft());				
		if(shapeControlPanel!=null) {
			shapeControlPanel.setPopupPosition(newLeft, getAbsoluteTop() + 
					getShape().getHeight() + 10);
		}
		repositionResizeMarkers();

		drawing = false;
	}
	
	/**
	 * reset the js graphics panel to redraw the shape.
	 * 
	 * @param width
	 * @param height
	 */
	private void resetGraphicsPanel(int width, int height) {
		String graphicsId = "graphic"+new Integer(counter++).toString();
		if(dragPanel.getWidget()!=null) dragPanel.remove(graphicsPanel);				
		
		graphicsDiv = new HTML("<div id=\"" + graphicsId +"\"></div>");
		boundaryPanel.add(graphicsDiv);
		
		graphicsPanel = new JsGraphicsPanel(graphicsId);		
		graphicsPanel.setPixelSize(width, height);
		graphicsPanel.setColor(new Color(shape.getColor().getR(),
				shape.getColor().getG(),shape.getColor().getB()));
		graphicsPanel.setStrokeWidth(shape.getStrokeWidth());
	}
	
	/**
	 * set the shape.
	 * 
	 * @param shape
	 */
	public void setShape(Shape shape) {		
		if(drawing) endDraw();
		
		if(pickupDragController!=null)
			pickupDragController.makeNotDraggable(this);
		
		this.shape = shape;		
		createShape();
		
		makeDraggable();
	}		
	
	/**
	 * mark the shape as selected.
	 */	
	public void onSelectImageFragment(ImageFragmentContainer container) {
		graphicsPanel.setColor(SELECTION_COLOR);
		drawShape();
	}
	
	/**
	 * deselect the shape.
	 */	
	public void onDeselectImageFragment(ImageFragmentContainer container) {
		graphicsPanel.setColor(new Color(shape.getColor().getR(),
				shape.getColor().getG(),shape.getColor().getB()));
		drawShape();
	}
	
	/**
	 * move the shape.
	 * 
	 * @param right
	 * @param down
	 */
	public void moveBy(int right, int down) {		
		// -1 px for the boundary panel's border
		int newLeft=boundaryPanel.getWidgetLeft(this)-1+right;
		int newTop=boundaryPanel.getWidgetTop(this)-1+down;
		shape.setLeft(newLeft);
		shape.setTop(newTop);
		
		// avoid the widget to be positioned statically
		if(newLeft!=-1||newTop!=-1)
			boundaryPanel.setWidgetPosition(this, newLeft, newTop);
				 						
		if(shapeControlPanel!=null) {
			shapeControlPanel.setPopupPosition(
					shapeControlPanel.getPopupLeft()+right, 
					shapeControlPanel.getPopupTop()+down);
		}		
	}

	/**
	 * resize the shape.
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		int heightDiff = Math.max(height, SHAPE_MIN_HEIGHT) - this.shape.getHeight();
		int newWidth=Math.max(width, SHAPE_MIN_WIDTH);
		int newHeight=Math.max(height, SHAPE_MIN_WIDTH);
		
		if(shape instanceof Polygon) ((Polygon)shape).resize(newWidth, newHeight);
		shape.setWidth(newWidth);
		shape.setHeight(newHeight);		
		
		createShape();
		if(resizeDragController!=null) repositionResizeMarkers();
				
		if(shapeControlPanel!=null) {
			shapeControlPanel.setPopupPosition(shapeControlPanel.getPopupLeft(),
					shapeControlPanel.getPopupTop() + heightDiff);
		}
	}
	
	/**
	 * returns the width of the shape.
	 * 
	 * @return width
	 */
	public int getWidth() {
		return shape.getWidth();
	}
	
	/**
	 * returns the height of the shape.
	 * 
	 * @return height
	 */
	public int getHeight() {
		return shape.getHeight();
	}
	
	/**
	 * returns the shape.
	 * 
	 * @return shape
	 */
	public Shape getShape() {
		return shape;
	}	
	
	/**
	 * set the shape control panel.
	 * 
	 * @param shapeControlPanel
	 * @see ShapeControlPanel
	 */
	public void setShapeControlPanel(PopupPanel shapeControlPanel) {
		this.shapeControlPanel = shapeControlPanel;
	}

	/**
	 * returns the drag panel to attach mouse listeners.
	 *  
	 * @return drag panel
	 */
	public FocusPanel getDragPanel() {
		return dragPanel;
	}

	/**
	 * check if the drawing mode is on
	 * 
	 * @return true if drawing mode is on, otherwise false
	 */
	public boolean isDrawing() {
		return drawing;
	}
	
	/**
	 * get the area of the shape panel
	 * @return area
	 */
	public int getArea() {		
		return getHeight() * getWidth();
	}
	
	@Override
	public int getRelativeLeft() {
		return boundaryPanel.getWidgetLeft(this);
	}

	@Override
	public int getRelativeTop() {
		return boundaryPanel.getWidgetTop(this);
	}
}