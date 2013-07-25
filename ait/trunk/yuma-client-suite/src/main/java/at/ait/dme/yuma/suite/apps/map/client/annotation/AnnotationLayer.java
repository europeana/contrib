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

package at.ait.dme.yuma.suite.apps.map.client.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gwt.mosaic.ui.client.InfoPanel;
import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.gwt.openlayers.client.Bounds;
import at.ait.dme.gwt.openlayers.client.LonLat;
import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.Pixel;
import at.ait.dme.gwt.openlayers.client.Style;
import at.ait.dme.gwt.openlayers.client.StyleMap;
import at.ait.dme.gwt.openlayers.client.controls.Control;
import at.ait.dme.gwt.openlayers.client.controls.DrawFeature;
import at.ait.dme.gwt.openlayers.client.controls.ModifyFeature;
import at.ait.dme.gwt.openlayers.client.controls.SelectFeature;
import at.ait.dme.gwt.openlayers.client.event.LayerEventListener;
import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;
import at.ait.dme.gwt.openlayers.client.handler.Handler;
import at.ait.dme.gwt.openlayers.client.layers.Vector;
import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.TagCloud;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.TagSelectionListener;
import at.ait.dme.yuma.suite.apps.image.core.client.treeview.ImageAnnotationEditForm;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageAnnotation;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageFragment;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Shape;
import at.ait.dme.yuma.suite.apps.map.client.MapUtils;
import at.ait.dme.yuma.suite.apps.map.client.widgets.MapComponent;
import at.ait.dme.yuma.suite.apps.map.shared.geo.XYCoordinate;
import at.ait.dme.yuma.suite.apps.map.shared.server.GeocoderService;
import at.ait.dme.yuma.suite.apps.map.shared.server.GeocoderServiceAsync;
import at.ait.dme.yuma.suite.apps.map.shared.server.exception.TransformationException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Implements functionality to display, highlight and edit annotation fragments on
 * the OpenLayers map (including tag-cloud based smart tagging). 
 *  
 * @author Rainer Simon
 */
public class AnnotationLayer {
	
	/**
	 * Tag cloud dimensions
	 */
	private static final int TAGCLOUD_WIDTH = 640;
	private static final int TAGCLOUD_HEIGHT = 550;
	
	/**
	 * We restrict to four font sizes...
	 */
	private static final int FONT_XL = 32;
	private static final int FONT_L = 24;
	private static final int FONT_M = 22;
	private static final int FONT_S = 18;
	
	/**
	 * Reference to the map component
	 */
	protected MapComponent mapComponent;
	
	/**
	 * Current list of tags
	 */
	private ArrayList<SemanticTag> geoTags = new ArrayList<SemanticTag>();	
	
	/**
	 * Reference to the Annotation Form
	 */
	private ImageAnnotationEditForm annotationForm;
	
	/**
	 * OpenLayers vector layers
	 */
	protected Vector lAnnotations;
	protected Vector lAnnotationEditing;
	
	/**
	 * HashMap that links all features on the map with their ImageAnnotations
	 */
	protected HashMap<ImageAnnotation, VectorFeature> fragments = 
		new HashMap<ImageAnnotation, VectorFeature>();
	
	/**
	 * OpenLayers controls
	 */
	private SelectFeature hilightControl = null;
	private SelectFeature selectControl = null;
	protected ModifyFeature modifyControl = null;
	
	protected Control[] drawingTools = null;
	protected Control currentDrawingTool = null;
	
	/**
	 * Currently edited feature (if any)
	 */
	protected VectorFeature editedFeature = null;

	/**
	 * Flag indicating whether selection happend "externally" (via 
	 * annotation tree panel) or "internally" via OpenLayers mouseover
	 */
	private boolean isExternalHilight = false;
	
	/**
	 * The tag cloud layer
	 */
	private TagCloud tagCloud;
	
	/**
	 * handler manager provided by the TileImageComposite
	 */
	private HandlerManager handlerManager;
	
	public AnnotationLayer(String layerName, MapComponent mapComponent, HandlerManager handlerManager, boolean addTagCloud) {	
		this.mapComponent = mapComponent;
		this.handlerManager = handlerManager;

		Map map = mapComponent.getMap();
		
		// Create layers and add to map
		StyleMap[] styles = initStyles();

		if (styles.length > 0) {
			lAnnotations = createAnnotationLayer(layerName, styles[0]);
		} else {
			lAnnotations = createAnnotationLayer(layerName, null);
		}
		map.addLayer(lAnnotations);
		
		if (styles.length > 1) {
			lAnnotationEditing = createEditingLayer(layerName + "-edit", styles[1]);
		} else {
			lAnnotationEditing = createEditingLayer(layerName + "-edit", null);
		}
		map.addLayer(lAnnotationEditing);
		lAnnotationEditing.setVisibility(false);
		
		// Add controls
		hilightControl = SelectFeature.create(lAnnotations, true);
		map.addControl(hilightControl);
		hilightControl.activate();
		
		drawingTools = initDrawingTools();
		for (int i=0; i<drawingTools.length; i++) {
			map.addControl(drawingTools[i]);
		}
		if (drawingTools.length > 0) currentDrawingTool = drawingTools[0]; 
		
		selectControl = SelectFeature.create(lAnnotationEditing);
		map.addControl(selectControl);
		selectControl.activate();
		
		modifyControl = ModifyFeature.create(lAnnotationEditing);
		map.addControl(modifyControl);
		
		// Create tag cloud layer
		if (addTagCloud) {
			tagCloud = new TagCloud(TAGCLOUD_WIDTH, TAGCLOUD_HEIGHT, mapComponent.getElement(), new TagSelectionListener() {
				@Override
				public void onTagSelected(SemanticTag tag, boolean selected) {
					if (annotationForm != null) {
						if (selected) {
							annotationForm.addTag(tag);
						} else {
							annotationForm.removeTag(tag);
						}
					}
				}
			});
			tagCloud.hide();
		}
	}
	
	public TagCloud getTagCloud() {
		return tagCloud;
	}
	
	/**
	 * Override this method to provide your own styles on sub-classes of AnnotationLayer.
	 * If the array contains only one style map, this will be applied to the display
	 * layer; if the array contains two style maps, the second one will be applied
	 * to the editing layer.
	 * @return the StyleMaps for display layer and editing layer 
	 */
	protected StyleMap[] initStyles() {
		Style def = new Style("default");
		def.fillColor = "#0049D0";
		def.strokeColor = "#0049D0";
		def.fillOpacity = 0.6;
		def.strokeWidth = 1;
		def.pointRadius = 6;
		
		Style sel = new Style("select");
		sel.fillColor = "#ffC800";
		sel.strokeColor = "#ffC800";
		sel.fillOpacity = 0.6;
		sel.strokeWidth = 4;
		
		return new StyleMap[] { StyleMap.create(new Style[] { def, sel }) };
	}
	
	/**
	 * Override this method to provide your own set of drawing controls on
	 * sub-classes of AnnotationLayer.
	 * @return the controls
	 */
	protected Control[] initDrawingTools() {
		return new Control[] { DrawFeature.create(lAnnotationEditing, Handler.POLYGON) };
	}
	
	private Vector createAnnotationLayer(String layerName, StyleMap stylemap) {
		final Vector layer;
		if (stylemap != null) {
			layer = Vector.create(layerName, stylemap);
		} else {
			layer = Vector.create(layerName);
		}
		
		// Feature selected
		layer.addLayerEventListener(new LayerEventListener() {
			@Override
			public void onEvent(VectorFeature feature) {				
				if (!isExternalHilight) { // Ignore if select was triggered externally from the annotation tree panel
					ImageAnnotation annotation = findByVectorFeature(feature);
					if (annotation != null) {
						if (handlerManager != null) {
							handlerManager.fireEvent(new AnnotationSelectionEvent(annotation, true));
						}
					}
				} else {
					isExternalHilight = false; // Reset for next event
				}
			}
		}, Vector.FEATURE_SELECTED);

		// Feature unselected 
		layer.addLayerEventListener(new LayerEventListener() {
			@Override
			public void onEvent(VectorFeature feature) {
				if (!isExternalHilight) { // Ignore if select was triggered externally from the annotation tree panel
					ImageAnnotation annotation = findByVectorFeature(feature);
					if (annotation != null) {
						if (handlerManager != null)
							handlerManager.fireEvent(new AnnotationSelectionEvent(annotation, false));
					}
				} else {
					isExternalHilight = false; // Reset for next event
				}
			}
		}, Vector.FEATURE_UNSELECTED);
		
		return layer;		
	}
	
	private Vector createEditingLayer(String layerName, StyleMap stylemap) {
		final Vector editingLayer;
		if (stylemap != null) {
			editingLayer = Vector.create(layerName, stylemap);
		} else {
			editingLayer = Vector.create(layerName);
		}
		
		// Called after new shape was created -> disallow any other editing! 
		editingLayer.addLayerEventListener(new LayerEventListener() {
			@Override
			public void onEvent(VectorFeature feature) {
				editedFeature = feature;
				if (currentDrawingTool != null) currentDrawingTool.deactivate();
				selectControl.select(editedFeature);
				if (tagCloud != null) {
					tagCloud.show();
					modifyTagCloud(editedFeature, true);
				}
			}
		}, Vector.FEATURE_ADDED);
		
		// Disable any further selection! (Leaving edit mode only possible via annotation tree viewer)
		editingLayer.addLayerEventListener(new LayerEventListener() {
			@Override
			public void onEvent(VectorFeature feature) {
				selectControl.deactivate();
				modifyControl.activate();
				modifyControl.selectFeature(feature);
			}
		}, Vector.FEATURE_SELECTED);
		
		
		// Catch modification events for re-computing tag cloud
		editingLayer.addLayerEventListener(new LayerEventListener() {	
			@Override
			public void onEvent(VectorFeature feature) {
				modifyTagCloud(feature, false);	
			}
		}, Vector.FEATURE_MODIFIED);
				
		// Editing layer not shown unless in editing mode
		return editingLayer;
	}
	
	public void setVisibility(boolean visibility) {
		lAnnotations.setVisibility(visibility);
		if (tagCloud != null) {
			if (!visibility) {
				tagCloud.fadeoutAndClear();
			}
		}
	}
	
	public boolean isVisible() {
		return lAnnotations.isVisible();
	}
	
	public void showActiveFragmentPanel(ImageAnnotation annotation, boolean forceVisible) {	
		if (editedFeature != null) hideActiveFragmentPanel();
		
		if (annotation == null) {
			// New annotation -> enable toolbar
			if (currentDrawingTool != null) currentDrawingTool.activate();
		} else {
			// Edit existing feature -> add to editing layer
			editedFeature = fragments.get(annotation); 
			
			Timer t = new Timer() {
				@Override
				public void run() {
					lAnnotationEditing.addFeature(editedFeature);
					
				}
			};
			t.schedule(1);
			
			modifyControl.activate();
			modifyControl.selectFeature(editedFeature);
		}	
		
		lAnnotationEditing.setVisibility(true);
	}
	
	public void hideActiveFragmentPanel() {
		// Disable editing toolbar
		if (currentDrawingTool != null) currentDrawingTool.deactivate();
		
		// Remove features
		if (editedFeature != null) {
			lAnnotationEditing.removeFeature(editedFeature);
		}
		
		lAnnotationEditing.destroyFeatures();
		editedFeature = null;
		
		// Set editing layer to invisible
		lAnnotationEditing.setVisibility(false);
		
		if (tagCloud != null) {
			tagCloud.fadeoutAndClear();
		}	
	}

	
	private ImageAnnotation findByVectorFeature(VectorFeature feature) {
		Iterator<ImageAnnotation> keySet = fragments.keySet().iterator();
	
		ImageAnnotation i;
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
	
	public void showFragment(ImageAnnotation annotation) {
		if(!annotation.hasFragment()) return;
		
		if (!fragments.containsKey(annotation)) {
			VectorFeature f = MapUtils.shapeToVectorFeature(((ImageFragment)annotation.getFragment()).getShape());
			
			// Add feature to GUI
			lAnnotations.addFeature(f);
			
			// Add feature to map
			fragments.put(annotation, f);
		}
	}
	
	public void selectFragment(ImageAnnotation annotation, boolean selected) {
		isExternalHilight = true;
		VectorFeature feature = fragments.get(annotation);
		if (feature != null) {
			if(selected) {
				hilightControl.select(feature);
			} else {
				hilightControl.unselect(feature);
			}
		}
		
	}

	public void hideFragment(ImageAnnotation annotation) {
		if (annotation != null) {
			if (fragments.containsKey(annotation)) {
				lAnnotations.removeFeature(fragments.get(annotation));
				fragments.remove(annotation);
			}
		}
	}
	
	public Shape getActiveShape() {
		if (editedFeature != null) {
			return MapUtils.vectorFeatureToShape(editedFeature);
		} else {
			return null;
		}
	}
	
	public String serializeAnnotations() {
		StringBuffer sb = new StringBuffer();
		
		for (ImageAnnotation annotation : fragments.keySet()) {
			sb.append(fragments.get(annotation).getGeometry().toString());
			sb.append("@" + annotation.getTitle() + "@" + annotation.getText());
			sb.append(";");
		}
		
		return sb.toString();
	}
	
	public void modifyTagCloud(VectorFeature feature, boolean recenter) {
		Shape s = MapUtils.vectorFeatureToShape(feature);
		
		LonLat nw = LonLat.create(s.getLeft(), s.getTop() + s.getHeight());
		LonLat se = LonLat.create(s.getLeft() + s.getWidth(), s.getTop());
		
		Pixel ul = mapComponent.getMap().getViewPortPxFromLonLat(nw);
		Pixel lr = mapComponent.getMap().getViewPortPxFromLonLat(se);

		if (recenter) tagCloud.moveTo((int)(ul.getX() + lr.getX()) / 2, (int)(ul.getY() + lr.getY()) / 2);
		addGeonamesSuggestions(Bounds.create(nw.getLon(), se.getLat(), se.getLon(), nw.getLat()));
	}
	
	public void setAnnotationForm(ImageAnnotationEditForm annotationForm) {
		this.annotationForm = annotationForm;
	}
	
	private void addGeonamesSuggestions(Bounds bbox) {
		GeocoderServiceAsync service = (GeocoderServiceAsync) GWT.create(GeocoderService.class);
		service.getTags(YUMACoreProperties.getObjectURI(), new XYCoordinate((int)bbox.getLeft(), (int)bbox.getBottom()), new XYCoordinate((int)bbox.getRight(), (int)bbox.getTop()), new AsyncCallback<SemanticTag[]>(){
		    public void onSuccess(SemanticTag[] tags) {
	    		// 1. Remove all tags from the tag cloud which are NOT in this list
		    	for (int i=0; i<tags.length; i++) {
		    		geoTags.remove(tags[i]);
		    	}
	    		tagCloud.removeTags(geoTags.toArray(new SemanticTag[geoTags.size()]));
	    		
	    		// 2. Add the (new) tags
	    		geoTags.clear();
	    		int fontsize = FONT_XL;
	    		for (int i=0; i<tags.length; i++) {
	    			if (i > 1) fontsize = FONT_L;
	    			if (i > 3) fontsize = FONT_M;
	    			if (i > 5) fontsize = FONT_S;
	    			geoTags.add(tags[i]);
	    			tagCloud.addTag(tags[i], fontsize);
	    		}
	    		
	    		if (tags.length == 0) {
	    			InfoPanel.show("No Tag Suggestions Found", "There are no Geonames suggestions for this area. Try improving the geo-reference of your map by adding control points.");
	    		}
		    }
		    
			public void onFailure(Throwable t) {
				if (!(t instanceof TransformationException)) { 
					// Note: TransformationExceptions happen when a map is 
					// not geo-referenced yet - ignore this in the GUI
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					MessageBox.error(errorMessages.error(), errorMessages.geonamesError());
				}
			}
		});
	}

}
