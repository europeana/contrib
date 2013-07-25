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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

import at.ait.dme.gwt.openlayers.client.Style;
import at.ait.dme.gwt.openlayers.client.StyleMap;
import at.ait.dme.gwt.openlayers.client.controls.Control;
import at.ait.dme.gwt.openlayers.client.controls.DragFeature;
import at.ait.dme.gwt.openlayers.client.event.DragCompleteListener;
import at.ait.dme.gwt.openlayers.client.geometry.Point;
import at.ait.dme.gwt.openlayers.client.geometry.VectorFeature;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.shape.GeoPoint;
import at.ait.dme.yuma.client.image.shape.Shape;
import at.ait.dme.yuma.client.map.MapComponent;

/**
 * A sub-class of the AnnotationLayer specifically for control
 * points. Replaces the default layer style, removes the
 * drawing tools and provides custom show() and edit() methods.
 *  
 * @author Rainer Simon
 */
public class ControlPointLayer extends AnnotationLayer {
	
	/**
	 * Reference to Control Point Form
	 */
	private ControlPointForm controlPointForm;
	
	public ControlPointLayer(String layerName, MapComponent mapComponent, HandlerManager handlerManager) {
		super(layerName, mapComponent, handlerManager, false);
		
		// Add controls
	  	DragFeature dragControl = DragFeature.create(lAnnotationEditing, new DragCompleteListener() {
			@Override
			public void onHandle(JavaScriptObject feature, JavaScriptObject pixel) {
				Point p = (Point) ((VectorFeature) feature).getGeometry();
				if (controlPointForm != null) controlPointForm.setXY((int)p.getX(), (int)p.getY());
			}
		});
	  	mapComponent.getMap().addControl(dragControl);
	  	dragControl.activate();
	}
	
	@Override
	protected StyleMap[] initStyles() {
		Style def = new Style("default");
		def.pointRadius = 6;
		def.fillColor = "#ffffff"; 
		def.strokeColor = "#000000";
		def.fillOpacity = 1;
		def.strokeWidth = 2;	
		
		Style sel = new Style("select");
		sel.pointRadius = 6;
		sel.fillColor = "#ff0000";
		sel.strokeColor = "#000000";
		sel.fillOpacity = 1;
		sel.strokeWidth = 2;	
		StyleMap cpLayer = StyleMap.create(new Style[] { def, sel });
		
		Style edit = new Style("select");
		edit.pointRadius = 6;
		edit.fillColor = "#ff0000";
		edit.strokeColor = "#480000";
		edit.fillOpacity = 0.8;
		edit.strokeWidth = 2;	
		StyleMap editLayer = StyleMap.create(new Style[] { edit });
		return new StyleMap[] { cpLayer, editLayer };
	}
	
	protected Control[] initDrawingTools() {
		return new Control[0];
	}
	
	@Override
	public void showFragment(ImageAnnotation annotation) {
		if (!fragments.containsKey(annotation)) {
			if (annotation.getFragment().getShape() instanceof GeoPoint) {
				GeoPoint g = (GeoPoint) annotation.getFragment().getShape();
				VectorFeature f = VectorFeature.create(Point.create(g.getX(), g.getY()));
				lAnnotations.addFeature(f);
				fragments.put(annotation, f);		
			}
		}
	}
	
	@Override
	public void showActiveFragmentPanel(ImageAnnotation annotation, boolean forceVisible) {
		// Stop editing any other annotation first
		if (editedFeature != null) hideActiveFragmentPanel();
		if (annotation == null) {
			editedFeature = VectorFeature.create(Point.create(mapComponent.getMap().getCenterLon(), mapComponent.getMap().getCenterLat()));
		} else {
			editedFeature = fragments.get(annotation);
		} 
		
		Timer t = new Timer() {
			@Override
			public void run() {
				lAnnotationEditing.addFeature(editedFeature);
			}
		};
		t.schedule(1);

		modifyControl.activate();
		modifyControl.selectFeature(editedFeature);
		
		lAnnotationEditing.setVisibility(true);
	}	
	
	@Override
	public Shape getActiveShape() {
		if (editedFeature != null) {
			Point p = (Point) editedFeature.getGeometry();
			return new GeoPoint(
					controlPointForm.getAnnotationTitle(),
					Integer.valueOf((int)p.getX()), 
					Integer.valueOf((int)p.getY()), 
					controlPointForm.getLat(),
					controlPointForm.getLon()
			);
		}
		return null;
	}
	
	public void setControlPointForm(ControlPointForm controlPointForm) {
		this.controlPointForm = controlPointForm;
	}

}
