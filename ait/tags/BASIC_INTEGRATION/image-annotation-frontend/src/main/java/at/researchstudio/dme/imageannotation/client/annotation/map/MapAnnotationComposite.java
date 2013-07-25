package at.researchstudio.dme.imageannotation.client.annotation.map;

import java.util.ArrayList;
import java.util.Collection;

import at.researchstudio.dme.imageannotation.client.Application;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.dnd.DraggableWindowComposite;
import at.researchstudio.dme.imageannotation.client.image.ImageFragment;
import at.researchstudio.dme.imageannotation.client.image.ImageRect;
import at.researchstudio.dme.imageannotation.client.image.shape.Point;
import at.researchstudio.dme.imageannotation.client.image.shape.Polygon;
import at.researchstudio.dme.imageannotation.client.image.shape.Polyline;
import at.researchstudio.dme.imageannotation.client.image.shape.Shape;

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;

/**
 * this composite displays an image annotation on google maps.
 * 
 * @author Christian Sadilek
 */
public class MapAnnotationComposite extends DraggableWindowComposite {						
	private LatLng mapNorthEast = null;
	private LatLng mapSouthWest = null;
	
	public MapAnnotationComposite(ImageAnnotation annotation) {
		Collection<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();
		annotations.add(annotation);
		showMap(annotations);
	}
	
	public MapAnnotationComposite(Collection<ImageAnnotation> annotations) {
		showMap(annotations);
	}
		
	private void showMap(Collection<ImageAnnotation> annotations) {
		parseBoundingBox();		
		this.setStyleName("mapAnnotation-composite");
		closeWindowImage.setStyleName("mapAnnotation-window-bar-close");
		minWindowImage.setStyleName("mapAnnotation-window-bar-minmax");
		maxWindowImage.setStyleName("mapAnnotation-window-bar-minmax");
				
		// show GMap
		MapWidget map = new MapWidget();
		map.setStyleName("mapAnnotation-map");
		map.setSize("500px", "300px");
		map.addControl(new LargeMapControl());
		map.setZoomLevel(3);
		map.setScrollWheelZoomEnabled(true);
		map.setDraggable(true);
		
		for(ImageAnnotation annotation : annotations) {
			Overlay overlay = createFragmentOverlay(annotation.getAnnotatedFragment());
			if(annotation.getAnnotatedFragment()!=null) 
				map.setCenter(getCenter(annotation.getAnnotatedFragment()));
			map.getInfoWindow().open(map.getCenter(), new InfoWindowContent(annotation.getTitle()));			
			if(overlay!=null) map.addOverlay(overlay);
		}			
		compositePanel.add(map);
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
	 * transform a point from pixels to LatLng
	 * 
	 * @param imageRect
	 * @param x
	 * @param y
	 * @return latitude and longitude
	 */
	private LatLng transformPoint(ImageRect imageRect, int x, int y) {
		float xRatio =  ((float)imageRect.getWidth() / (x - imageRect.getLeft()));
		float yRatio =  ((float)imageRect.getHeight() / (y - imageRect.getTop()));
	
		double lat = mapNorthEast.getLatitude() - 
			((mapNorthEast.getLatitude() - mapSouthWest.getLatitude()) / yRatio);
		double lng = mapSouthWest.getLongitude() + 
			((mapNorthEast.getLongitude() - mapSouthWest.getLongitude()) / xRatio);

		return LatLng.newInstance(lat, lng);
	}
	
	/**
	 * create the google maps overlay for the given image fragment
	 * 
	 * @param fragment
	 * @return overlay	 
	 */
	public Overlay createFragmentOverlay(ImageFragment fragment) {		
		if(mapSouthWest==null || mapNorthEast==null) return null;
		
		ImageRect imageRect  = fragment.getImageRect();
		Shape shape = fragment.getShape();
		
		ArrayList<LatLng> coords = new ArrayList<LatLng>();		
		if(shape instanceof Polygon) {
			Polygon polygon = (Polygon)shape;
			// transform all points to latlng
			for (Point p : polygon.getPoints()) {
				coords.add(transformPoint(imageRect, 
						polygon.getLeft() + p.getX(), 
						polygon.getTop()+ p.getY()));
			}
			
			if(!(shape instanceof Polyline)) {
				// add the first point to close the polygon
				LatLng firstPoint=coords.get(0);
				coords.add(firstPoint);
			}
		} else {
			coords.add(transformPoint(imageRect,shape.getLeft(),shape.getTop()));
			coords.add(transformPoint(imageRect,shape.getLeft()+shape.getWidth(),shape.getTop()));
			coords.add(transformPoint(imageRect,shape.getLeft()+shape.getWidth(), 
					shape.getTop()+shape.getHeight()));
			coords.add(transformPoint(imageRect, shape.getLeft(), 
					shape.getTop()+shape.getHeight()));
		}
		
		if(shape instanceof Polyline) {
			return new com.google.gwt.maps.client.overlay.Polyline(coords.toArray(new LatLng[0]),
					shape.getColor().toRGBString(),
					shape.getStrokeWidth(), 0.5d);
		} else {		
			return new com.google.gwt.maps.client.overlay.Polygon(coords.toArray(new LatLng[0]),
					shape.getColor().toRGBString(),
					shape.getStrokeWidth(), 0.5d,
					shape.getColor().toRGBString(), 0.5d);				
		}		
	}
	
	/**
	 * get the center for the given fragment for google maps
	 * 
	 * @param fragment
	 * @return latitude longitude
	 */
	public LatLng getCenter(ImageFragment fragment) {
		ImageRect imageRect = fragment.getImageRect();
		Shape fragmentShape = fragment.getShape();

		return transformPoint(imageRect, 
				fragmentShape.getLeft() + (fragmentShape.getWidth() / 2),
				fragmentShape.getTop() + (fragmentShape.getHeight() / 2));
	}
}
