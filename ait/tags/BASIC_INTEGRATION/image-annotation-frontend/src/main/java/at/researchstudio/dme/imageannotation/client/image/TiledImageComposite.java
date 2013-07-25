package at.researchstudio.dme.imageannotation.client.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import at.researchstudio.dme.imageannotation.client.image.openlayers.Map;
import at.researchstudio.dme.imageannotation.client.image.openlayers.MapOptions;
import at.researchstudio.dme.imageannotation.client.image.openlayers.MapWidget;
import at.researchstudio.dme.imageannotation.client.image.openlayers.Style;
import at.researchstudio.dme.imageannotation.client.image.openlayers.StyleMap;
import at.researchstudio.dme.imageannotation.client.image.openlayers.controls.EditingToolbar;
import at.researchstudio.dme.imageannotation.client.image.openlayers.controls.ModifyFeature;
import at.researchstudio.dme.imageannotation.client.image.openlayers.controls.MouseDefaults;
import at.researchstudio.dme.imageannotation.client.image.openlayers.controls.MousePosition;
import at.researchstudio.dme.imageannotation.client.image.openlayers.controls.PanZoomBar;
import at.researchstudio.dme.imageannotation.client.image.openlayers.controls.SelectFeature;
import at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureAddedListener;
import at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureSelectedListener;
import at.researchstudio.dme.imageannotation.client.image.openlayers.event.VectorFeatureUnselectedListener;
import at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.LineString;
import at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.Point;
import at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.VectorFeature;
import at.researchstudio.dme.imageannotation.client.image.openlayers.layers.Vector;
import at.researchstudio.dme.imageannotation.client.image.openlayers.layers.Zoomify;
import at.researchstudio.dme.imageannotation.client.image.shape.Color;
import at.researchstudio.dme.imageannotation.client.image.shape.Polygon;
import at.researchstudio.dme.imageannotation.client.image.shape.Polyline;
import at.researchstudio.dme.imageannotation.client.image.shape.Shape;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Implements an image annotation frontend for Zoomify (http://www.zoomify.com/)
 * images, based on the tile-based OpenLayers JavaScript library.
 *  
 * @author Rainer Simon
 */
public class TiledImageComposite extends ImageComposite implements ImageFragmentSelectionListener {
	
	/**
	 * Image extent (x direction)
	 */
	private int xExtent;
	
	/**
	 * Image extent (y direction)
	 */
	private int yExtent;
	
	/**
	 * The map
	 */
	private Map map;
	
	/**
	 * The Vector layer for annotations
	 */
	private Vector annotationLayer;
	
	/**
	 * The Vector layer for editing an annotation
	 */
	private Vector editingLayer;
	
	/**
	 * Highlight-on-hover control
	 */
	private SelectFeature hilightControl = null;
	
	/**
	 * Flag indicating selection via mouse-hover on the annotation tree panel
	 * (Bit of a workaround -> OpenLayers will register both types of 
	 * selections - via tree panel and via mouseover on the OpenLayers panel -
	 * as selection events.)
	 */
	private boolean isExternalHilight;
	
	/**
	 * Select-for-modification control
	 */
	private SelectFeature selectControl = null;
	
	/**
	 * Modification control
	 */
	private ModifyFeature modifyControl = null;
	
	/**
	 * Vector layer editing toolbar
	 */
	private EditingToolbar editingToolbar;
	
	/**
	 * Currently highlighted geometry
	 */
	private VectorFeature hoverTarget = null;
	
	/**
	 * Currently edited geometry (if any)
	 */
	private VectorFeature editedFeature = null;
	
	/**
	 * Default vector feature style
	 */
	private Style defaultStyle = null;
	
	/**
	 * Vector feature "highlighted" style
	 */
	private Style hilightedStyle = null;
	
	/**
	 * HashMap that links all features on OpenLayers with their ImageFragmentContainers
	 */
	private HashMap<ImageFragmentContainer, VectorFeature> fragments = new HashMap<ImageFragmentContainer, VectorFeature>();
		
	/**
	 * Creates a new composite with a default size of 800x600 pixels.
	 * @param imageUrl the URL to the Zoomify image
	 */
	public TiledImageComposite(String imageUrl) {
		this(imageUrl, 800, 600);
	}

	/**
	 * Creates a new composite of configurable size.
	 * @param imageUrl the URL to the Zoomify image
	 * @param width the width of the composite
	 * @param height the height of the composite
	 */
	public TiledImageComposite(String imageUrl, int width, int height) {
		// Get tileset metadata for specified image URL (if tileset exists!)
		TiledImage ti = getTiledImageMetadata(imageUrl);
		if (ti == null)
			throw new RuntimeException(); // Currently: never happens
		
		this.xExtent = ti.width;
		this.yExtent = ti.height;
		
		AbsolutePanel panel = new AbsolutePanel();
		panel.setSize("98%", "85%");
		panel.add(createMap(ti.url, width, height));
		initWidget(panel); 
        DOM.setStyleAttribute(panel.getElement(), "zIndex", "1");
        DOM.setStyleAttribute(panel.getElement(), "border", "1px solid black");
        LoadEvent.fireNativeEvent(Document.get().createLoadEvent(), this);	
	}
	
	private MapWidget createMap(String tilesetURL, int width, int height) {
		// Create Zoomify layer
		Zoomify zoomifyLayer = Zoomify.create("zoomify", tilesetURL, xExtent, yExtent);
		
		// Create style definitions
		defaultStyle = new Style("default");
		defaultStyle.fillColor = "#0049D0";
		defaultStyle.strokeColor = "#0049D0";
		defaultStyle.fillOpacity = 0.6;
		defaultStyle.strokeWidth = 1;
		
		hilightedStyle = new Style("select");
		hilightedStyle.fillColor = "#ffC800";
		hilightedStyle.strokeColor = "#ffC800";
		hilightedStyle.strokeWidth = 4;
		
		// Create stylemap
		StyleMap annotationStylemap = StyleMap.create(new Style[] { defaultStyle, hilightedStyle });
		
		// Create annotation vector layer
		annotationLayer = createAnnotationLayer(annotationStylemap);
		
		// Create vector layer for editing
		editingLayer = createEditingLayer(); 
		
		// Create OpenLayers Map control 
		MapOptions mapOptions = MapOptions.create(xExtent, yExtent, (float) Math.pow(2, zoomifyLayer.numberOfTiers()-1), zoomifyLayer.numberOfTiers());
		MapWidget widget = new MapWidget(mapOptions);
		map = widget.getMap();
		
		// OpenLayers bug! Edited features get messed up
		// after zooming -> redraw 
		// Note: no longer needed for AIT wrapper (but you never know...)
		/*
		map.addMapZoomListener(new MapZoomListener() {
			public void onMapZoom(Map map, MapZoomEvent event) {
				if (editedFeature != null) {
					editingLayer.removeFeature(editedFeature);
					editingLayer.addFeature(editedFeature);
				}
			}
		});
		*/
		
		// Add layers to map
		map.addLayer(zoomifyLayer);
		map.addLayer(annotationLayer);
		map.addLayer(editingLayer);
				
		// Add default mouse controls
		map.addControl(PanZoomBar.create());
		map.addControl(MouseDefaults.create());
		map.addControl(MousePosition.create());
		
		// Add hilight-on-mouseover control to annotation layer
		hilightControl = SelectFeature.create(annotationLayer);
		hilightControl.setHover(true);
		map.addControl(hilightControl);
		hilightControl.activate();
		
		// Add select-control to editing layer
		// NOTE: selecting features by mouseclick is actually prevented on the GUI;
		// control still needed for programmatic selection though
		selectControl = SelectFeature.create(editingLayer);
		map.addControl(selectControl);
		selectControl.activate();
		
		// Add editing toolbar to editing layer
		editingToolbar = EditingToolbar.create(editingLayer);
		map.addControl(editingToolbar);
		editingToolbar.deactivate();
		
		// Add modify control to editing layer
		modifyControl = ModifyFeature.create(editingLayer);
		map.addControl(modifyControl);
		modifyControl.activate();

		return widget;
	}
	
	private Vector createAnnotationLayer(StyleMap stylemap) {
		final Vector annotationLayer = Vector.create("Annotation Layer", stylemap);
		
		// Select listener - will be called on mouse over, since selection control is in hover mode!
		annotationLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(VectorFeature feature) {
				// feature.setStyle(hilightedStyle);
				if (!isExternalHilight) { // Ignore if select was triggered externally, from the annotation tree panel
					hoverTarget = feature;
					ImageFragmentContainer container = findByVectorFeature(hoverTarget);
					if (container != null) {
						imageFragmentSelectionListener.onSelectImageFragment(container);
					}
				} else {
					isExternalHilight = false; // Reset for next event
				}
				// annotationLayer.refresh(); // Note: no longer needed for AIT wrapper
			}
		});
		
		// Unselect listener - will be called on mouse out, since selection control is in hover mode!
		annotationLayer.addVectorFeatureUnselectedListener(new VectorFeatureUnselectedListener() {
			public void onFeatureUnselected(VectorFeature feature) {
				// feature.setStyle(defaultStyle);
				if (!isExternalHilight) { // Ignore if select was triggered externally, from the annotation tree panel
					if (hoverTarget == feature) {
						ImageFragmentContainer container = findByVectorFeature(hoverTarget);
						if (container != null) {
							imageFragmentSelectionListener.onDeselectImageFragment(container);
						}
						hoverTarget = null;
					}
				} else {
					isExternalHilight = false; // Reset for next event
				}
				// annotationLayer.refresh(); // Note: no longer needed for AIT wrapper
			}
		});

		return annotationLayer;
	}
	
	private Vector createEditingLayer() {
		final Vector editingLayer = Vector.create("Editing Layer");
		
		// Called after new shape was created -> disallow any other editing! 
		editingLayer.addVectorFeatureAddedListener(new VectorFeatureAddedListener() {
			public void onFeatureAdded(VectorFeature feature) {
				if (editingLayer.isVisible()) {
					editingToolbar.deactivate();
					editedFeature = (VectorFeature) feature;
					selectControl.select(editedFeature);
				} else {
					editingLayer.removeFeature(feature);
				}
			}
		});
		
		// Disable any further selection! (Leaving edit mode only possible via annotation tree viewer)
		editingLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(VectorFeature event) {
				modifyControl.deactivate();
				selectControl.deactivate();
			}
		});
				
		// Editing layer not shown unless in editing mode
		editingLayer.setVisibility(false);
		return editingLayer;
	}
	
	@Override
	public HandlerRegistration addLoadHandler(LoadHandler loadHandler) {
	    if(loadHandler==null) return null;
	    HandlerRegistration hr = addHandler(loadHandler,LoadEvent.getType());
	    LoadEvent.fireNativeEvent(Document.get().createLoadEvent(), this);
	    return hr;
	}

	@Override
	public void showActiveFragmentPanel(ImageFragmentContainer container, boolean forceVisible) {
		if (editedFeature != null)
			hideActiveFragmentPanel();
		
		if (container == null) {
			// New annotation -> enable toolbar
			editingToolbar.activate();
		} else {
			// Edit existing feature -> add to editing layer
			VectorFeature f = shapeToVectorFeature(container.getImageFragment().getShape());
			editedFeature = f; 
			editingLayer.addFeature(f);
			selectControl.select(f);
		}	
		
		// Set editing layer to visible
		editingLayer.setVisibility(true);	
		
		// OpenLayers bug! Sometimes (?) only the editing handles
		// are drawn, not the shapes -> redraw
		// Note: no longer needed for AIT wrapper
		/* if (editedFeature != null) {
			editingLayer.removeFeature(editedFeature);
			editingLayer.addFeature(editedFeature);
		}*/
	}
	
	@Override
	public void hideActiveFragmentPanel() {
		// Disable editing toolbar
		editingToolbar.deactivate();
		
		// Remove features
		if (editedFeature != null) {
			editingLayer.removeFeature(editedFeature);
		}
		
		editingLayer.destroyFeatures();
		editedFeature = null;
		
		// Set editing layer to invisible
		editingLayer.setVisibility(false);
	}

	@Override
	public void showFragment(ImageFragmentContainer widget) {
		if (fragments.containsKey(widget)) {
			// Already on screen - remove first, then re-add (otherwise 
			// OpenLayers object reference gets lost for some reason...) 
			annotationLayer.removeFeature(fragments.get(widget));
			fragments.remove(widget);
		}
		
		VectorFeature f = shapeToVectorFeature(widget.getImageFragment().getShape());
		
		// Add feature to GUI
		annotationLayer.addFeature(f);
		
		// Add feature to map
		fragments.put(widget, f);
		
		widget.setImageFragmentSelectionListener(this);
	}
	
	@Override
	public void hideFragment(ImageFragmentContainer widget) {
		if (widget != null) {
			if (fragments.containsKey(widget)) {
				annotationLayer.removeFeature(fragments.get(widget));
				fragments.remove(widget);
			}
		}
	}

	@Override
	public Shape getActiveShape() {
		if (editedFeature != null) {
			return vectorFeatureToShape(editedFeature);
		} else {
			return null;
		}
	}
	
	public void onSelectImageFragment(ImageFragmentContainer container) {
		isExternalHilight = true;
		VectorFeature feature = fragments.get(container);
		if (feature != null) {
			hilightControl.select(feature);
		}
	}
	
	public void onDeselectImageFragment(ImageFragmentContainer container) {
		isExternalHilight = true;
		VectorFeature feature = fragments.get(container);
		if (feature != null) {
			hilightControl.unselect(feature);
		}
	}
	
	private VectorFeature shapeToVectorFeature(Shape shape) {
		if (shape instanceof Polyline) {
			// Linestring or Point
			ArrayList<Point> points = new ArrayList<Point>();
			for (at.researchstudio.dme.imageannotation.client.image.shape.Point p :  ((Polyline) shape).getPoints()) {
				points.add(Point.create(p.getX() + shape.getLeft(), p.getY() + shape.getTop()));
			}
			
			if (points.size() == 1) {
				// Point
				return VectorFeature.create(points.get(0)); // , defaultStyle);
			} else {
				// Linestring
				return VectorFeature.create(LineString.create(points.toArray(new Point[points.size()]))); //, defaultStyle);
			}
		} else if (shape instanceof Polygon) {
			// Polygon
			ArrayList<Point> points = new ArrayList<Point>();			
			for (at.researchstudio.dme.imageannotation.client.image.shape.Point p :  ((Polygon) shape).getPoints()) {
				points.add(Point.create(p.getX() + shape.getLeft(), p.getY() + shape.getTop()));
			}
			
			at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.Polygon poly = at.researchstudio.dme.imageannotation.client.image.openlayers.geometry.Polygon.create(points.toArray(new Point[points.size()]));
			return VectorFeature.create(poly); //, defaultStyle);
		} 
		
		return null;
	}
	
	private Shape vectorFeatureToShape(VectorFeature feature) {
		String js = feature.getGeometry().toString();
			
		if (js.startsWith("POLYGON")) {
			// Construct Polygon shape
			String[] points = js.substring(9, js.length() - 2).split(",");
			
			Polygon poly = new Polygon();
			poly.setColor(new Color("ff0000"));
			for (int i=0; i<points.length; i++) {
				String[] xy = points[i].split(" ");
				if (xy.length == 2) {
					try {
						double x = Double.parseDouble(xy[0]);
						double y = Double.parseDouble(xy[1]);
						poly.addPoint(new at.researchstudio.dme.imageannotation.client.image.shape.Point((int)x,(int)y));
					} catch (NumberFormatException e) {
						System.out.println("Error adding point: " + e.getMessage());
					}
				}
			}
			poly.setLeft(poly.getRelativeLeft());
			poly.setTop(poly.getRelativeTop());
			return poly;
		} else if (js.startsWith("LINESTRING")) {
			// Construct Polyline shape
			String[] points = js.substring(11, js.length() - 1).split(",");

			Polyline pline = new Polyline();
			pline.setColor(new Color("ff0000"));
			for (int i=0; i<points.length; i++) {
				String[] xy = points[i].split(" ");
				if (xy.length == 2) {
					try {
						double x = Double.parseDouble(xy[0]);
						double y = Double.parseDouble(xy[1]);
						pline.addPoint(new at.researchstudio.dme.imageannotation.client.image.shape.Point((int)x,(int)y));
					} catch (NumberFormatException e) {
						System.out.println("Error adding point: " + e.getMessage());
					}
				}
			}
			pline.setLeft(pline.getRelativeLeft());
			pline.setTop(pline.getRelativeTop());
			return pline;
		} else if (js.startsWith("POINT")) {
			// Points are no shapes -> workaround with a single-point-polyline
			String[] xy = js.substring(6, js.length() - 1).split(" ");
			if (xy.length == 2) {
				try {
					double x = Double.parseDouble(xy[0]);
					double y = Double.parseDouble(xy[1]);
					
					Polyline pline = new Polyline();
					pline.setColor(new Color("ff0000"));
					pline.addPoint(new at.researchstudio.dme.imageannotation.client.image.shape.Point((int)x,(int)y));
					pline.setLeft(pline.getRelativeLeft());
					pline.setTop(pline.getRelativeTop());
					return pline;
				} catch (NumberFormatException e) {
					System.out.println("Error adding point: " + e.getMessage());
				}
			}
		}
		
		return null;
	}
	
	private ImageFragmentContainer findByVectorFeature(VectorFeature feature) {
		Iterator<ImageFragmentContainer> keySet = fragments.keySet().iterator();
		
		ImageFragmentContainer i;
		VectorFeature f;
		while (keySet.hasNext()) {
			i = keySet.next();
			f = fragments.get(i);
			
			if (feature.getGeometry().toString().equals(f.getGeometry().toString())) {
				return i;
			}
		}
		
		return null;
	}
	
	@Override
	public ImageRect getVisibleRect() {
		return getImageRect();
	}

	@Override
	public ImageRect getImageRect() {
		return new ImageRect(0, 0, xExtent, yExtent);
	}
	
	/**
	 * Returns the metadata (tileset base URL, image width and height) for
	 * a tiled image stored at the (HTTP) tile or (IIPImage) image server, or
	 * <code>null</code> if there is no tileset/pyramid TIFF available for this
	 * image yet.
	 *  
	 * @param url the original image URL
	 * @return the metadata of the image at the HTTP/image server or <code>null</code>
	 */
	private TiledImage getTiledImageMetadata(String url) {
		// Dummy map storing image URL -> tileset metadata relations
		HashMap<String, TiledImage> cachedImageMap = new HashMap<String, TiledImage>();
		cachedImageMap.put("http://planets-project.arcs.ac.at/maps/americas/", new TiledImage("http://planets-project.arcs.ac.at/maps/americas/", 10492, 11908));
		cachedImageMap.put("http://planets-project.arcs.ac.at/maps/worldmap/", new TiledImage("http://planets-project.arcs.ac.at/maps/worldmap/", 17010, 9720));
		cachedImageMap.put("http://planets-project.arcs.ac.at/maps/hirschvogel-map-austria/", new TiledImage("http://planets-project.arcs.ac.at/maps/hirschvogel-map-austria/", 1414, 1100));
		cachedImageMap.put("http://planets-project.arcs.ac.at/maps/hajji-ahmed/", new TiledImage("http://planets-project.arcs.ac.at/maps/hajji-ahmed/", 14173, 15354));
		cachedImageMap.put("http://planets-project.arcs.ac.at/maps/onb-sample/", new TiledImage("http://planets-project.arcs.ac.at/maps/onb-sample/", 5144, 3543));
		
		TiledImage result = cachedImageMap.get(url);
		if (result == null) {
			// Return ONB sample per default
			result = new TiledImage("http://planets-project.arcs.ac.at/maps/worldmap/", 17010, 9720);
		}
			
		return result;
	}
	
	/**
	 * Simple wrapper class describing a 'tiled image' stored at the 
	 * (HTTP) tile or (IIPImage)image server.
	 *  
	 * @author Rainer Simon
	 */
	private class TiledImage {
		
		String url;
		
		int width;
		
		int height;
		
		
		TiledImage(String url, int width, int height) {
			this.url = url;
			this.width = width;
			this.height = height;
		}
	}

}