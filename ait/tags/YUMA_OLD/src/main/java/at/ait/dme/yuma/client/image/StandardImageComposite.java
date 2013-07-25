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

package at.ait.dme.yuma.client.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionEvent;
import at.ait.dme.yuma.client.image.shape.Color;
import at.ait.dme.yuma.client.image.shape.Ellipse;
import at.ait.dme.yuma.client.image.shape.Shape;
import at.ait.dme.yuma.client.image.shape.ShapeControlPanel;
import at.ait.dme.yuma.client.image.shape.ShapePanel;
import at.ait.dme.yuma.client.server.ImageZoomService;
import at.ait.dme.yuma.client.server.ImageZoomServiceAsync;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.TolerantAbsolutePositionDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

/**
 * image composite that allows to zoom in and select a fragment of an image.
 * 
 * @author Christian Sadilek
 */
public class StandardImageComposite extends ImageComposite {
	private static final int IMAGE_OFFSET_LEFT = 11;
	private static final int IMAGE_OFFSET_TOP = 81;
	private static final String PX = "px";

	private static final int MAX_WIDTH = 1024;
	private static final int MAX_HEIGHT = 768;

	private static final String ZOOM_PATH = "zoom";
	private static final String ZOOM_IMAGEURL_PARAMETER_NAME = "imageURL";
	private static final String ZOOM_HEIGHT_PARAMETER_NAME = "height";
	private static final String ZOOM_WIDTH_PARAMETER_NAME = "width";

	// the url of the image
	private String imageUrl = null;

	// we embed the image in an absolute panel to allow absolute positioning
	private AbsolutePanel imagePanel = new AbsolutePanel();
	private Image image = null;
	private ImageRect imageRect = null;

	// the image fragment panels:
	// the active fragment panel is the panel that allows the user to select a fragment
	private ShapePanel activeFragmentPanel = null;
	// the panel allows the user to control the shape of the active fragment, its size and color
	private ShapeControlPanel activeFragmentControlPanel = null;
	// maps an image annotation to its corresponding (currently displayed) shape panel
	private Map<ImageAnnotation, ShapePanel> fragmentPanels = new HashMap<ImageAnnotation, ShapePanel>();

	// image buttons
	private Image zoomIn = new Image("images/plus.png");
	private Image zoomOut = new Image("images/minus.png");
	private Image fragment = new Image("images/fragment.png");
	
	// one zoom step in pixels.
	// we use a fixed value for the width and calculate the zoom step for the height based on 
	// the width to height ratio of the given image in order to keep its proportions.
	private float zoomStepWidth = 75.0f;
	private float zoomStepHeight;

	// width and height of the image before zooming
	private int originalImageWidth;
	private int originalImageHeight;

	// width and height of the image after initial zooming (adapt to screen)
	private int initialImageWidth;
	private int initialImageHeight;

	// a reference to the server side zoom service that is used to 
	// retrieve higher quality scaled images
	private ImageZoomServiceAsync imageZoomService = (ImageZoomServiceAsync) GWT
			.create(ImageZoomService.class);
	// the server side zoom will be disabled in case of an error
	private boolean serverSideZoomEnabled = false;

	// listeners on the image
	private LoadHandler imageLoadListener = new LoadHandler() {
		public void onLoad(LoadEvent event) {
			init();					
		}			
	};	
	private ErrorHandler imageErrorListener = new ErrorHandler() {
		public void onError(ErrorEvent event) {
			ErrorMessages errorMessages=(ErrorMessages) GWT.create(ErrorMessages.class);	
			Window.alert(errorMessages.imageNotFound());				
		}
	};
	private HandlerRegistration loadListenerRegistration = null;
	private HandlerRegistration errorListenerRegistration = null;
	
	/**
	 * constructs a new image composite for the provided image url.
	 * 
	 * @param imageUrl
	 */
	public StandardImageComposite(String imageUrl) {
		// set up the image and the image panel
		this.imageUrl = imageUrl;
		image = new Image(imageUrl);
		image.addMouseWheelHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent event) {
				zoom(event.isNorth());
			}			
		});	

		// in case the image is cached the load listener will never be called
		// so we have to call it manually. we can determine if the image is cached
		// by checking height or width > 0.
		// see also http://code.google.com/p/google-web-toolkit/issues/detail?id=2149
		if(image.getHeight()>0)  {
			imageLoadListener.onLoad(null);
		} else {
			loadListenerRegistration=image.addLoadHandler(imageLoadListener);	
			errorListenerRegistration=image.addErrorHandler(imageErrorListener);
		}

		// initialize the widget
		imagePanel.add(image);
		initWidget(imagePanel);
	}

	/**
	 * called by the load listener to initialize the widget after the image was
	 * loaded.
	 * 
	 * @see ImageComposite.LoadListener
	 */
	private void init() {
		originalImageHeight = image.getHeight();
		originalImageWidth = image.getWidth();

		// make the image fit the screen in case it's larger
		makeImageFitToScreen();

		DOM.setStyleAttribute(imagePanel.getElement(), "border", "1px solid gray");
		DOM.setStyleAttribute(imagePanel.getElement(), "backgroundColor", "#d3d3d3");

		// make the image draggable
		PickupDragController pickupDragController = new PickupDragController(imagePanel, false);
		pickupDragController.setBehaviorConstrainedToBoundaryPanel(false);
		pickupDragController.makeDraggable(image);
		pickupDragController.registerDropController(new TolerantAbsolutePositionDropController(
				imagePanel) {
			public void onDrop(DragContext context) {
				super.onDrop(context);

				imageRect.setLeft(image.getAbsoluteLeft() - IMAGE_OFFSET_LEFT);
				imageRect.setTop(image.getAbsoluteTop() - IMAGE_OFFSET_TOP);

				// redraw the fragments
				Collection<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();
				annotations.addAll(fragmentPanels.keySet());
				for (ImageAnnotation annotation : annotations) {
					showFragment(annotation);
				}
			}
		});

		// add the zoom icons and ensure they are always on top
		imagePanel.add(zoomIn, 5, 5);
		zoomIn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				zoom(true);
			}
		});
		imagePanel.add(zoomOut, 5, 25);
		zoomOut.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				zoom(false);
			}
		});
		DOM.setStyleAttribute(zoomIn.getElement(), "zIndex", "15");
		DOM.setStyleAttribute(zoomOut.getElement(), "zIndex", "15");

		// add the fragment icon and ensure its on top
		imagePanel.add(fragment, 5, 55);
		fragment.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showActiveFragmentPanel(null, false);
			}
		});
		DOM.setStyleAttribute(fragment.getElement(), "zIndex", "15");

		LoadEvent.fireNativeEvent(Document.get().createLoadEvent(), this);
	}

	/**
	 * make the image fit the screen.
	 */
	private void makeImageFitToScreen() {
		float widthToHeightRatio = (float) ((float) image.getWidth()) / ((float) image.getHeight());
		zoomStepHeight = zoomStepWidth / widthToHeightRatio;

		// we try the use the max width or height depending on the format (portrait, landscape)
		initialImageWidth = originalImageWidth;
		initialImageHeight = originalImageHeight;
		if (initialImageWidth > MAX_WIDTH || initialImageHeight > MAX_HEIGHT) {
			if (originalImageHeight > originalImageWidth) {
				initialImageHeight = MAX_HEIGHT;
				initialImageWidth = Math.round(MAX_HEIGHT * widthToHeightRatio);
			} else {
				initialImageWidth = MAX_WIDTH;
				initialImageHeight = Math.round(MAX_WIDTH / widthToHeightRatio);
			}
		}
		// in case the other side is still too large we zoom out
		while (initialImageWidth > MAX_WIDTH || initialImageHeight > MAX_HEIGHT) {
			initialImageWidth = Math.round(initialImageWidth - zoomStepWidth);
			initialImageHeight = Math.round(initialImageHeight - zoomStepHeight);
		}

		imageRect = new ImageRect(0, 0, initialImageWidth, initialImageHeight);
		image.setPixelSize(initialImageWidth, initialImageHeight);
		imagePanel.setSize(new Integer(initialImageWidth).toString() + PX, new Integer(
				initialImageHeight).toString() + PX);
	}

	/**
	 * shows a fragment and adds a mouse selection handler to highlight the
	 * fragment and the corresponding {@link ImageAnnotation}
	 * 
	 * @param image annotation
	 */
	@Override
	public void showFragment(final ImageAnnotation annotation) {
		if(!annotation.hasFragment()) return;
		ImageFragment fragment = annotation.getFragment();
		
		// create a copy so we can roll back changes if the user cancels the operation
		Shape shape = fragment.getShape().copy();

		// if the fragment is already displayed we have to remove it
		if (fragmentPanels.containsKey(annotation)) {
			hideFragment(annotation);
		}

		// create a new fragment panel
		final ShapePanel fragmentPanel = new ShapePanel(imagePanel, shape, false, false);
		imagePanel.add(fragmentPanel, shape.getLeft(), shape.getTop());
		fragmentPanels.put(annotation, fragmentPanel);
		setFragmentZIndices();

		// change zoom level of fragment to fit the current zoom level of the image
		ImageRect fragmentImageRect = fragment.getImageRect();
		float widthDiff = fragmentImageRect.getWidth() - imageRect.getWidth();
		float heightDiff = fragmentImageRect.getHeight() - imageRect.getHeight();
		zoomFragment(fragmentPanel, widthDiff < 0, fragmentImageRect, Math.abs(widthDiff), Math
				.abs(heightDiff));

		// mouse listeners for fragment selection
		fragmentPanel.getDragPanel().addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				fragmentPanel.markSelected(true);
				handlerManager.fireEvent(new ImageAnnotationSelectionEvent(annotation, true));
			}
		});
		fragmentPanel.getDragPanel().addMouseOutHandler(new MouseOutHandler() {			
			public void onMouseOut(MouseOutEvent event) {
				fragmentPanel.markSelected(false);
				handlerManager.fireEvent(new ImageAnnotationSelectionEvent(annotation, false));
			}
		});
	}
	
	/**
	 * marks a fragment as selected or deselected
	 */
	@Override
	public void selectFragment(ImageAnnotation annotation, boolean selected) {
		ShapePanel fragmentPanel = fragmentPanels.get(annotation);
		if(fragmentPanel!=null)
			fragmentPanel.markSelected(selected);
	}

	/**
	 * set the z-indices for all fragments based on their area. this is a simple
	 * solution to support the selection of embedded shapes (shapes in shapes).
	 */
	private void setFragmentZIndices() {
		List<ShapePanel> shapePanels = new ArrayList<ShapePanel>();
		for (ImageAnnotation annotation : fragmentPanels.keySet()) {
			shapePanels.add(fragmentPanels.get(annotation));
		}
		Collections.sort(shapePanels, new Comparator<ShapePanel>() {
			// sort descending by area
			public int compare(ShapePanel o1, ShapePanel o2) {
				return o2.getArea() - o1.getArea();
			}
		});

		int zIndex = 10;
		for (ShapePanel panel : shapePanels) {
			DOM.setStyleAttribute(panel.getElement(), "zIndex", new Integer(zIndex++).toString());
		}
	}

	/**
	 * hides a fragment
	 * 
	 * @param node
	 */
	@Override
	public void hideFragment(ImageAnnotation annotation) {
		ShapePanel fragmentPanel = fragmentPanels.remove(annotation);
		if (fragmentPanel != null) {
			imagePanel.remove(fragmentPanel);
		}
	}

	/**
	 * shows an active fragment (draggable, resizable and w/ a control panel).
	 * 
	 * @param shape
	 * @param forceVisible
	 */
	@Override
	public void showActiveFragmentPanel(ImageAnnotation annotation, boolean forceVisible) {
		ShapePanel fragmentPanel = fragmentPanels.get(annotation);
		Shape shape = (fragmentPanel == null) ? new Ellipse(100, 100, new Color(0, 0, 255), 2)
				: fragmentPanel.getShape();

		if (activeFragmentControlPanel == null) {
			activeFragmentPanel = new ShapePanel(imagePanel, shape, true, true);
			imagePanel.add(activeFragmentPanel, shape.getLeft(), shape.getTop());
			DOM.setStyleAttribute(activeFragmentPanel.getElement(), "zIndex", "100");

			activeFragmentControlPanel = 
				new ShapeControlPanel(imagePanel, activeFragmentPanel, shape);
			activeFragmentControlPanel.setPopupPosition(shape.getLeft() + IMAGE_OFFSET_LEFT, 
					shape.getTop() + shape.getHeight() + IMAGE_OFFSET_TOP + 10);
			activeFragmentControlPanel.show();
			activeFragmentPanel.setShapeControlPanel(activeFragmentControlPanel);
		} else {
			if (!forceVisible)
				hideActiveFragmentPanel();
		}
	}

	/**
	 * hide the active fragment
	 */
	@Override
	public void hideActiveFragmentPanel() {
		if (activeFragmentControlPanel != null) {
			imagePanel.remove(activeFragmentControlPanel.getShapePanel());
			activeFragmentControlPanel.hide();
			activeFragmentControlPanel = null;
			activeFragmentPanel = null;
		}
	}

	/**
	 * zooms the image and all visible fragments
	 * 
	 * @param in true to zoom in, otherwise false
	 */
	public void zoom(boolean in) {
		// save the current image rect
		ImageRect oldRect = new ImageRect(imageRect.getLeft(), imageRect.getTop(), 
				imageRect.getWidth(), imageRect.getHeight());

		// check if zoom out is still possible
		boolean zoomOutPossible = 
			oldRect.getWidth() > initialImageWidth || 
			oldRect.getHeight() > initialImageHeight;
		if (!in && !zoomOutPossible) return;

		// now zoom the image
		zoomImage(in);

		// zoom all visible fragments
		Collection<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();
		annotations.addAll(fragmentPanels.keySet());
		for (ImageAnnotation annotation : annotations) {
			// redraws the fragment an adapts the zoom level
			showFragment(annotation);
		}
		
		if (activeFragmentPanel != null && !activeFragmentPanel.isDrawing()) {
			zoomFragment(activeFragmentPanel, in, oldRect, zoomStepWidth, zoomStepHeight);
		}
	}

	/**
	 * zoom the image
	 * 
	 * @param in true to zoom in, otherwise false
	 */
	private void zoomImage(boolean in) {
		int left = imageRect.getLeft(), top = imageRect.getTop(), 
			width = imageRect.getWidth(), height = imageRect.getHeight();

		if (in) {
			width = Math.round(width + zoomStepWidth);
			height = Math.round(height + zoomStepHeight);

			// expand the panel if there is space left, otherwise move the image
			if (height <= MAX_HEIGHT) {
				imagePanel.setHeight(new Integer(height).toString()+PX);
			} else {
				imagePanel.setHeight(new Integer(MAX_HEIGHT).toString()+PX);
				top -= Math.round(zoomStepHeight / 2.0);
			}

			if (width <= MAX_WIDTH) {
				imagePanel.setWidth(new Integer(width).toString()+PX);
			} else {
				imagePanel.setWidth(new Integer(MAX_WIDTH).toString()+PX);
				left -= Math.round(zoomStepWidth / 2.0);
			}
		} else {
			width = Math.round(width - zoomStepWidth);
			height = Math.round(height - zoomStepHeight);
			if (height == initialImageHeight && width == initialImageWidth) left = top = 0;

			if (top < 0) top += Math.round(zoomStepHeight / 2.0);
			if (left < 0) left += Math.round(zoomStepWidth / 2.0);

			// shrink the panel back again to its initial size
			if (height >= initialImageHeight)
				imagePanel.setHeight(new Integer(Math.min(MAX_HEIGHT, height)).toString()+PX);

			if (width >= initialImageWidth)
				imagePanel.setWidth(new Integer(Math.min(MAX_WIDTH, width)).toString()+PX);

		}
		imageRect.setRect(left, top, width, height);
		image.setPixelSize(imageRect.getWidth(), imageRect.getHeight());
		imagePanel.setWidgetPosition(image, imageRect.getLeft(), imageRect.getTop());

		if(!serverSideZoomEnabled) return;		
		if(loadListenerRegistration!=null) loadListenerRegistration.removeHandler();
		if(errorListenerRegistration!=null) errorListenerRegistration.removeHandler();

		// now contact the server to retrieve a higher quality scaled image
		imageZoomService.prepareZoom(imageUrl, imageRect, new AsyncCallback<ImageRect>() {
			public void onFailure(Throwable caught) {
				serverSideZoomEnabled=false;
			}
			public void onSuccess(ImageRect result) {
				// if no change is detected, then it is assumed that the user has stopped zooming
				if (result.equals(imageRect)) {
					image.setUrl(GWT.getModuleBaseURL() + ZOOM_PATH + "?" + 
							ZOOM_IMAGEURL_PARAMETER_NAME + "="+ imageUrl + "&" + 
							ZOOM_WIDTH_PARAMETER_NAME+"=" + Math.round(imageRect.getWidth())+ "&" + 
							ZOOM_HEIGHT_PARAMETER_NAME+"=" + Math.round(imageRect.getHeight()));
				}				
			}
		});
	}

	/**
	 * zoom a fragment
	 * 
	 * @param shapePanel
	 * @param in true to zoom in, otherwise false
	 * @param oldRect the rect before zooming the image
	 * @param zoomStepWidth
	 * @param zoomStepHeight
	 */
	private void zoomFragment(ShapePanel shapePanel, boolean in, ImageRect oldRect,
			float zoomStepWidth, float zoomStepHeight) {
		Shape shape = shapePanel.getShape();

		float widthToLeftRatio = (float) oldRect.getWidth() / (shape.getLeft() - oldRect.getLeft());
		float heightToTopRatio = (float) oldRect.getHeight() / (shape.getTop() - oldRect.getTop());
		float widthChange = (zoomStepWidth > 0) ? (oldRect.getWidth() / zoomStepWidth) : 0.0f;
		float heightChange = (zoomStepHeight > 0) ? (oldRect.getHeight() / zoomStepHeight) : 0.0f;

		int newLeft = Math.round(imageRect.getWidth() / widthToLeftRatio) + imageRect.getLeft();
		int newTop = Math.round(imageRect.getHeight() / heightToTopRatio) + imageRect.getTop();

		shapePanel.moveBy(newLeft - shape.getLeft(), newTop - shape.getTop());
		if (widthChange > 0 && heightChange > 0) {
			if (in) {
				shapePanel.setSize(shape.getWidth() + Math.round(shape.getWidth() / widthChange),
						shape.getHeight() + Math.round(shape.getHeight() / heightChange));
			} else {
				shapePanel.setSize(shape.getWidth() - Math.round(shape.getWidth() / widthChange),
						shape.getHeight() - Math.round(shape.getHeight() / heightChange));
			}
		}
	}

	/**
	 * returns the current rect of the image for unit tests.
	 * 
	 * @return image rect
	 */
	public ImageRect getCurrentRect() {
		return imageRect;
	}

	/**
	 * returns the image rect.
	 * 
	 * @return image rect
	 */
	@Override
	public ImageRect getImageRect() {
		return new ImageRect(imagePanel.getWidgetLeft(image) - 1,
				imagePanel.getWidgetTop(image) - 1, image.getWidth(), image.getHeight());
	}

	/**
	 * returns the visible rect of the image.
	 * 
	 * @return image rect
	 */
	@Override
	public ImageRect getVisibleRect() {
		return new ImageRect(0, 0, imagePanel.getOffsetWidth(), imagePanel.getOffsetHeight());
	}

	/**
	 * returns true if the server side zoom is enabled, otherwise false used for
	 * unit tests.
	 * 
	 * @return boolean
	 */
	public boolean isServerSideZoomEnabled() {
		return serverSideZoomEnabled;
	}

	/**
	 * returns the zoom step width for unit tests.
	 * 
	 * @return zoom step width
	 */
	public float getZoomStepWidth() {
		return zoomStepWidth;
	}

	/**
	 * returns the zoom step height for unit tests.
	 * 
	 * @return zoom step height
	 */
	public float getZoomStepHeight() {
		return zoomStepHeight;
	}

	/**
	 * returns the active shape.
	 * 
	 * @return shape
	 */
	@Override
	public Shape getActiveShape() {
		if (activeFragmentControlPanel == null)
			return null;
		return activeFragmentControlPanel.getShapePanel().getShape();
	}

	/**
	 * @see HasLoadHandlers
	 */
	@Override
	public HandlerRegistration addLoadHandler(LoadHandler loadHandler) {
		if (loadHandler == null) return null;
		
		HandlerRegistration hr=addHandler(loadHandler, LoadEvent.getType());
		
		if (imageRect != null) {
			LoadEvent.fireNativeEvent(Document.get().createLoadEvent(), this);			
		}
		
		return hr;		
	}
}