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

package at.ait.dme.yuma.suite.apps.map.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageAnnotation;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageFragment;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Polygon;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Polyline;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Shape;
import at.ait.dme.yuma.suite.apps.map.client.MapUtils;
import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;
import at.ait.dme.yuma.suite.apps.map.shared.geo.XYCoordinate;
import at.ait.dme.yuma.suite.apps.map.shared.server.TransformationService;
import at.ait.dme.yuma.suite.apps.map.shared.server.TransformationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;

/**
 * this composite displays image annotations on google maps.
 * 
 * @author Christian Sadilek
 */
public class GoogleMapsComposite extends Composite {						
	
	// panel of this composite
	private AbsolutePanel compositePanel = new AbsolutePanel();
	
	// the map
	private MapWidget map = new MapWidget();
	
	// the annotations to display
	private Collection<ImageAnnotation> annotations;
	private Map<ImageAnnotation, Integer> coordinatesCount = 
		new LinkedHashMap<ImageAnnotation, Integer>();
	
	public GoogleMapsComposite(ImageAnnotation annotation) {
		annotations = new ArrayList<ImageAnnotation>();
		annotations.add(annotation);
		showMap();
	}
	
	public GoogleMapsComposite(Collection<ImageAnnotation> annotations) {
		this.annotations = annotations;
		showMap();
	}
		
	private void showMap() {
		initWidget(compositePanel);

		this.setStyleName("mapAnnotation-composite");
		
		// show GMap
		map.addControl(new LargeMapControl());
		map.setSize("500px", "300px");
		map.setZoomLevel(3);
		map.setScrollWheelZoomEnabled(true);
		map.setDraggable(true);	
		
		createMapOverlays();
		compositePanel.add(map);
		
		Anchor kmlLink = new Anchor(YUMACoreProperties.getConstants().kmlExport(), createKmlLink());
		kmlLink.setStyleName("mapAnnotation-kml");
		compositePanel.add(kmlLink, 10, 240);
		
	}
	
	/**
	 * transform a list of points from pixels to LatLng using the transformation service, in case
	 * no bounding box was provided.
	 * 
	 * @param xyCoord
	 */
	private void transformPoints(List<XYCoordinate> xyCoords) {
		TransformationServiceAsync transformationService = (TransformationServiceAsync) GWT
				.create(TransformationService.class);
		
		transformationService.transformCoordinates(YUMACoreProperties.getObjectURI(), xyCoords,
			new AsyncCallback<List<WGS84Coordinate>>() {
				public void onFailure(Throwable caught) {
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					Window.alert(errorMessages.transformationError());
				}

				public void onSuccess(List<WGS84Coordinate> coords) {
					List<LatLng> latlngCoords = new ArrayList<LatLng>();
					for(WGS84Coordinate coord : coords) {
						latlngCoords.add(LatLng.newInstance(coord.lat, coord.lon));
					}
					// we need to group the coordinates by annotation and display the overlays
					int fromIndex = 0;
					for(ImageAnnotation annotation : coordinatesCount.keySet()) {
						int coordsCount=coordinatesCount.get(annotation);
						GoogleMapsComposite.this.drawOverlay(
								new ArrayList<LatLng>(latlngCoords.
										subList(fromIndex, fromIndex+coordsCount-1)), 
								annotation);
						fromIndex+=coordsCount;
					}
				}
			});
	}
	
	
	/**
	 * create the map overlays for the provided annotations
	 */
	private void createMapOverlays() {
		List<XYCoordinate> allXyCoordinates = new ArrayList<XYCoordinate>();
		
		for(ImageAnnotation annotation : annotations) {
			if(!annotation.hasFragment()) continue;
			List<XYCoordinate> xyCoordinates = new ArrayList<XYCoordinate>();
			
			Shape shape = ((ImageFragment)annotation.getFragment()).getShape();
			if(shape instanceof Polygon) {
				Polygon polygon = (Polygon)shape;
					for (Point p : polygon.getPoints()) {
					xyCoordinates.add(new XYCoordinate(polygon.getLeft() + p.getX(), 
							polygon.getTop() + p.getY()));
				}
				if(!(shape instanceof Polyline)) {
					// add the first point to close the polygon
					XYCoordinate firstPoint=xyCoordinates.get(0);
					xyCoordinates.add(firstPoint);
				}
			} else {
				xyCoordinates.add(new XYCoordinate(shape.getLeft(), shape.getTop()));
				xyCoordinates.add(new XYCoordinate(shape.getLeft()+shape.getWidth(), shape.getTop()));
				xyCoordinates.add(new XYCoordinate(shape.getLeft()+shape.getWidth(), 
						shape.getTop()+shape.getHeight()));
				xyCoordinates.add(new XYCoordinate(shape.getLeft(), shape.getTop()+shape.getHeight()));
			}
			// add the center as last point to avoid an extra transformation call
			xyCoordinates.add(getCenter((ImageFragment) annotation.getFragment()));
		

				// in case we don't have a bounding box we need to contact the server to do a
				// transformation based on control points. to avoid unnecessary server calls,
				// we collect the coordinates of all annotations and use a single server call.
				// coordinatesCount is used to map the returned coordinates back to the 
				// corresponding annotation.
				allXyCoordinates.addAll(xyCoordinates);
				coordinatesCount.put(annotation, xyCoordinates.size());
			
		}
		if(!allXyCoordinates.isEmpty())
			transformPoints(allXyCoordinates);
	}
	
	/**
	 * draw the overlay
	 * 
	 * @param coords
	 * @param shape
	 * @return overlay
	 */
	private void drawOverlay(List<LatLng> coords, ImageAnnotation annotation) {
		Shape shape = ((ImageFragment)annotation.getFragment()).getShape();
		
		if(!coords.isEmpty()) {
			LatLng center = coords.remove(coords.size()-1);
			map.setCenter(center);
			map.getInfoWindow().open(center, new InfoWindowContent(annotation.getTitle()));
		}
		
		if(shape instanceof Polyline) {
			map.addOverlay(new com.google.gwt.maps.client.overlay.Polyline(
					coords.toArray(new LatLng[0]),
					shape.getColor().toRGBString(),
					shape.getStrokeWidth(), 0.5d));
		} else {		
			map.addOverlay(new com.google.gwt.maps.client.overlay.Polygon(
					coords.toArray(new LatLng[0]),
					shape.getColor().toRGBString(),
					shape.getStrokeWidth(), 0.5d,
					shape.getColor().toRGBString(), 0.5d));				
		}	
	}
	
	/**
	 * get the center for the given fragment for google maps
	 * 
	 * @param fragment
	 * @return latitude longitude
	 */
	private XYCoordinate getCenter(ImageFragment fragment) {
		Shape fragmentShape = fragment.getShape();

		return new XYCoordinate(fragmentShape.getLeft() + (fragmentShape.getWidth() / 2),
				fragmentShape.getTop() + (fragmentShape.getHeight() / 2));
	}
	
	/**
	 * create the link to export annotations as KML
	 * 
	 * @return href
	 */
	private String createKmlLink() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("tokml?url="); 
		sb.append(YUMACoreProperties.getObjectURI());
		sb.append("&a=");
		
		for (ImageAnnotation annotation : annotations) {
			if(!annotation.hasFragment()) continue;
			sb.append(MapUtils.shapeToVectorFeature(((ImageFragment)annotation.getFragment()).getShape()).
					getGeometry().toString());
			sb.append("@" + annotation.getTitle() + "@" + annotation.getText());
			sb.append(";");
		}
		
		return URL.encode(sb.toString());
	}
}
