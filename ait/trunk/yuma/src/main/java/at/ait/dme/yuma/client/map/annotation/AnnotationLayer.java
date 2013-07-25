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

package at.ait.dme.yuma.client.map.annotation;

import java.util.HashMap;
import java.util.Iterator;

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
import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionEvent;
import at.ait.dme.yuma.client.image.shape.Shape;
import at.ait.dme.yuma.client.map.MapComponent;
import at.ait.dme.yuma.client.map.tagcloud.TagCloudLayer;
import at.ait.dme.yuma.client.map.tagcloud.TagSelectionListener;
import at.ait.dme.yuma.client.util.AnnotationUtils;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Implements functionality to display, highlight and edit annotation fragments on
 * the OpenLayers map (including tag-cloud based smart tagging). 
 *  
 * @author Rainer Simon
 */
public class AnnotationLayer {
	
	/**
	 * Reference to the map component
	 */
	protected MapComponent mapComponent;
	
	/**
	 * Reference to the Annotation Form
	 */
	private TagEnabledMapAnnotationForm annotationForm;
	
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
	private TagCloudLayer lTagCloud;
	
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
			lTagCloud = new TagCloudLayer(mapComponent, new TagSelectionListener() {
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
		}
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
						if (handlerManager != null) handlerManager.fireEvent(new ImageAnnotationSelectionEvent(annotation, true));
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
						if (handlerManager != null) handlerManager.fireEvent(new ImageAnnotationSelectionEvent(annotation, false));
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
				if (lTagCloud != null) {
					lTagCloud.show();
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
		if (lTagCloud != null) {
			if (!visibility) {
				lTagCloud.hide();
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
		
		if (lTagCloud != null) {
			lTagCloud.hide();
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
			VectorFeature f = AnnotationUtils.shapeToVectorFeature(annotation.getFragment().getShape());
			
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
			return AnnotationUtils.vectorFeatureToShape(editedFeature);
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
		Shape s = AnnotationUtils.vectorFeatureToShape(feature);
		
		LonLat nw = LonLat.create(s.getLeft(), s.getTop() + s.getHeight());
		LonLat se = LonLat.create(s.getLeft() + s.getWidth(), s.getTop());
		
		Pixel ul = mapComponent.getMap().getViewPortPxFromLonLat(nw);
		Pixel lr = mapComponent.getMap().getViewPortPxFromLonLat(se);

		if (recenter) lTagCloud.centerAt((int)(ul.getX() + lr.getX()) / 2, (int)(ul.getY() + lr.getY()) / 2);
		lTagCloud.addGeonamesSuggestions(Bounds.create(nw.getLon(), se.getLat(), se.getLon(), nw.getLat()));
	}
	
	public void modifyTagCloud(SemanticTag[] tags) {
		lTagCloud.addNERSuggestions(tags);
	}
	
	public void setAnnotationForm(TagEnabledMapAnnotationForm annotationForm) {
		this.annotationForm = annotationForm;
	}

}
