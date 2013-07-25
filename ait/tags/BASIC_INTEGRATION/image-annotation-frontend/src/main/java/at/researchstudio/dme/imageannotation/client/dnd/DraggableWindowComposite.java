package at.researchstudio.dme.imageannotation.client.dnd;

import at.researchstudio.dme.imageannotation.client.Application;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * a resizable composite with a draggable window bar. designed for inheritance.
 * 
 * @author Christian Sadilek
 */
public abstract class DraggableWindowComposite extends Composite implements Resizable {	
	private Image north = new Image("images/blank.gif");	
	
	protected int intialHeight = 0;
	protected int intialWidth = 0;
	
	// used to enable absolute positioning within this composite
	protected AbsolutePanel basePanel = new AbsolutePanel();	
	protected AbsolutePanel windowBar = new AbsolutePanel();		
	
	// panel of this composite
	protected VerticalPanel compositePanel = new VerticalPanel();
	// window title label
	protected Label lbTitle = new Label();
	// image button to close window
	protected Image closeWindowImage = new Image("images/close.gif");
	// image button to minimize window
	protected Image minWindowImage = new Image("images/minimize.gif");
	// image button to maximize window
	protected Image maxWindowImage = new Image("images/maximize.gif");
	 
	public DraggableWindowComposite() {
		initWidget(basePanel);
				
		lbTitle.setStyleName("imageAnnotation-window-bar");
		windowBar.setStyleName("imageAnnotation-window-bar");		
		FocusPanel windowDragPanel = new FocusPanel();		
		windowDragPanel.add(lbTitle);
		
		minWindowImage.setStyleName("imageAnnotation-window-bar-minmax");
		minWindowImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DraggableWindowComposite.this.intialHeight=
					DraggableWindowComposite.this.getHeight();
				DraggableWindowComposite.this.intialWidth=
					DraggableWindowComposite.this.getWidth();
				DraggableWindowComposite.this.setHeight("20px");
				DraggableWindowComposite.this.setWidth(
						new Integer(DraggableWindowComposite.this.intialWidth)+"px");
				minWindowImage.setVisible(false);
				maxWindowImage.setVisible(true);
				windowBar.setStyleName("imageAnnotation-window-bar");
			}			
		});
		
		maxWindowImage.setStyleName("imageAnnotation-window-bar-minmax");
		maxWindowImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DraggableWindowComposite.this.setHeight(
						new Integer(DraggableWindowComposite.this.intialHeight)+"px");
				DraggableWindowComposite.this.setWidth(
						new Integer(DraggableWindowComposite.this.intialWidth)+"px");
				minWindowImage.setVisible(true);
				maxWindowImage.setVisible(false);
				windowBar.setStyleName("imageAnnotation-window-bar");
			}			
		});
		maxWindowImage.setVisible(false);
		
		closeWindowImage.setStyleName("imageAnnotation-window-bar-close");
		closeWindowImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DraggableWindowComposite.this.setVisible(false);
			}			
		});
		windowBar.add(windowDragPanel);				
		windowBar.add(minWindowImage);		
		windowBar.add(maxWindowImage);		
		windowBar.add(closeWindowImage);	
		
		PickupDragController pickupDragController = new PickupDragController(RootPanel.get(),true);
		pickupDragController.setBehaviorConstrainedToBoundaryPanel(true);
		pickupDragController.makeDraggable(this, windowDragPanel);		
		compositePanel.add(windowBar);	
		basePanel.add(compositePanel);				
	}
	
	public void makeNorthResizable() {
		ResizeDragController resizeDragController = 
			new ResizeDragController(RootPanel.get(), this);
		resizeDragController.setBehaviorConstrainedToBoundaryPanel(true);
		resizeDragController.makeDraggable(north, ResizeDragController.NORTH);				
		
		DOM.setStyleAttribute(north.getElement(), ResizeDragController.CSS_CURSOR, 
				ResizeDragController.NORTH.directionLetters + 
				ResizeDragController.CSS_CURSOR_RESIZE);
		
		north.setStyleName("imageAnnotation-window-bar");
		north.setHeight("2px");	
		basePanel.add(north,0,0);		
	}

	@Override
	public int getHeight() {		
		return basePanel.getOffsetHeight();
	}

	@Override
	public int getWidth() {				
		// TODO after hours of trying i could not find a better solution -> i leave it for now 
		return (Application.getUserAgent().contains("msie"))?
				basePanel.getOffsetWidth():compositePanel.getOffsetWidth();
	}

	@Override
	public int getRelativeLeft() {
		if(RootPanel.get().getWidgetIndex(this)==-1) return 0; 		
		return RootPanel.get().getWidgetLeft(this);
	}

	@Override
	public int getRelativeTop() {
		if(RootPanel.get().getWidgetIndex(this)==-1) return 0; 
		return RootPanel.get().getWidgetTop(this);
	}


	@Override
	public void moveBy(int right, int down) {	
		if(RootPanel.get().getWidgetIndex(this)==-1) return; 
		
		int newLeft=getAbsoluteLeft()+right;
		int newTop=getAbsoluteTop()+down;
		
		if(newLeft!=-1||newTop!=-1)			
			RootPanel.get().setWidgetPosition(this, newLeft, newTop);		
	}

	@Override
	public void setSize(int width, int height) {		
		this.setSize(new Integer(width).toString(), new Integer(height).toString());
	}
	
	public boolean isMinimized() {
		return maxWindowImage.isVisible();
	}
}
