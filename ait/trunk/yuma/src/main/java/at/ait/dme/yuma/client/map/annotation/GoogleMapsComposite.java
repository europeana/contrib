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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.image.ImageFragment;
import at.ait.dme.yuma.client.image.ImageRect;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.shape.Point;
import at.ait.dme.yuma.client.image.shape.Polygon;
import at.ait.dme.yuma.client.image.shape.Polyline;
import at.ait.dme.yuma.client.image.shape.Shape;
import at.ait.dme.yuma.client.server.TransformationService;
import at.ait.dme.yuma.client.server.TransformationServiceAsync;
import at.ait.dme.yuma.client.util.AnnotationUtils;

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
	
	private LatLng mapNorthEast = null;
	private LatLng mapSouthWest = null;
	
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
		parseBoundingBox();		
		this.setStyleName("mapAnnotation-composite");
		
		// show GMap
		map.addControl(new LargeMapControl());
		map.setSize("500px", "300px");
		map.setZoomLevel(3);
		map.setScrollWheelZoomEnabled(true);
		map.setDraggable(true);	
		
		createMapOverlays();
		compositePanel.add(map);
		
		if(Application.isInTileMode()) {
			Anchor kmlLink = new Anchor(Application.getConstants().kmlExport(), createKmlLink());
			kmlLink.setStyleName("mapAnnotation-kml");
			compositePanel.add(kmlLink, 10, 240);
		}
		
	}
	
	/**
	 * parse the bounding box into 2 instances of LatLng. 
	 * one for the north east and one for the south west.
	 */
	private void parseBoundingBox() {
		String bBox = Application.getBbox();
		if(bBox==null) return;
		
		bBox=bBox.replaceAll("[\\[\\]]", "");
		String[] coords=bBox.split(",");
		
		if(coords.length!=4) return;
		mapNorthEast = LatLng.newInstance(Double.parseDouble(coords[0]), 
				Double.parseDouble(coords[1]));
		mapSouthWest = LatLng.newInstance(Double.parseDouble(coords[2]), 
				Double.parseDouble(coords[3]));
	}
	
	/**
	 * transform a list of points from pixels to LatLng
	 * 
	 * @param imageRect
	 * @param XYCoordinate
	 * @return latitude and longitude
	 */
	private List<LatLng> transformPoints(final ImageAnnotation annotation, List<XYCoordinate> xyCoords) {
		List<LatLng> coords = new ArrayList<LatLng>();
		for(XYCoordinate xyCoord : xyCoords) {
			coords.add(transformPoint(annotation, xyCoord));
		}
		return coords;
	}
	
	/**
	 * transform a point from pixels to LatLng using the provided bounding box
	 * 
	 * @param imageRect
	 * @param xyCoord
	 * @return latlng
	 */
	private LatLng transformPoint(ImageAnnotation annotation, XYCoordinate xyCoord) {
		ImageRect imageRect = annotation.getFragment().getImageRect();
		float xRatio =  ((float)imageRect.getWidth() / (xyCoord.x - imageRect.getLeft()));
		float yRatio =  ((float)imageRect.getHeight() / (xyCoord.y - imageRect.getTop()));
	
		double lat = mapNorthEast.getLatitude() - 
			((mapNorthEast.getLatitude() - mapSouthWest.getLatitude()) / yRatio);
		double lng = mapSouthWest.getLongitude() + 
			((mapNorthEast.getLongitude() - mapSouthWest.getLongitude()) / xRatio);
		
		return LatLng.newInstance(lat, lng);
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
		
		transformationService.transformCoordinates(Application.getExternalObjectId(), 
				Application.getImageUrl(), xyCoords,
			new AsyncCallback<List<WGS84Coordinate>>() {
				public void onFailure(Throwable caught) {
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
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
		List<LatLng> coords = null;
		List<XYCoordinate> allXyCoordinates = new ArrayList<XYCoordinate>();
		
		for(ImageAnnotation annotation : annotations) {
			if(!annotation.hasFragment()) continue;
			List<XYCoordinate> xyCoordinates = new ArrayList<XYCoordinate>();
			
			Shape shape = annotation.getFragment().getShape();
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
			xyCoordinates.add(getCenter(annotation.getFragment()));
		
			if(Application.getBbox()!=null) {
				coords = transformPoints(annotation, xyCoordinates);
				if(coords!=null) drawOverlay(coords, annotation);
			} else {
				// in case we don't have a bounding box we need to contact the server to do a
				// transformation based on control points. to avoid unnecessary server calls,
				// we collect the coordinates of all annotations and use a single server call.
				// coordinatesCount is used to map the returned coordinates back to the 
				// corresponding annotation.
				allXyCoordinates.addAll(xyCoordinates);
				coordinatesCount.put(annotation, xyCoordinates.size());
			}
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
		Shape shape = annotation.getFragment().getShape();
		
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
		
		sb.append("yuma/tokml?url="); 
		sb.append(Application.getImageUrl());
		sb.append("&europeanaUri=");
		sb.append(Application.getExternalObjectId());
		sb.append("&a=");
		
		for (ImageAnnotation annotation : annotations) {
			if(!annotation.hasFragment()) continue;
			sb.append(AnnotationUtils.shapeToVectorFeature(annotation.getFragment().getShape()).
					getGeometry().toString());
			sb.append("@" + annotation.getTitle() + "@" + annotation.getText());
			sb.append(";");
		}
		
		return URL.encode(sb.toString());
	}
}
