package at.researchstudio.dme.imageannotation.server.annotation.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.image.ImageFragment;
import at.researchstudio.dme.imageannotation.client.image.ImageRect;
import at.researchstudio.dme.imageannotation.client.image.shape.Color;
import at.researchstudio.dme.imageannotation.client.image.shape.Cross;
import at.researchstudio.dme.imageannotation.client.image.shape.Ellipse;
import at.researchstudio.dme.imageannotation.client.image.shape.Point;
import at.researchstudio.dme.imageannotation.client.image.shape.Polygon;
import at.researchstudio.dme.imageannotation.client.image.shape.Rectangle;
import at.researchstudio.dme.imageannotation.client.image.shape.Shape;

/**
 * converts image annotations to json and vice versa
 * 
 * @author Christian Sadilek
 */
public class JsonAnnotationBuilder {

	public static ArrayList<ImageAnnotation> fromJson(String json) {
		ArrayList<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();
		JSONArray jsonArray=(JSONArray)JSONValue.parse(json);
		
		if(jsonArray==null) return annotations;
		
		for(Object obj : jsonArray) {
			JSONObject jsonObj = (JSONObject)obj;
			ImageAnnotation annotation = new ImageAnnotation();
			
			String id = (String)jsonObj.get("id");
			if(id!=null) annotation.setId(id);
			
			String objectId = (String)jsonObj.get("objectId");
			if(objectId!=null) annotation.setObjectId(objectId);

			String externalObjectId = (String)jsonObj.get("linkedTo");
			if(externalObjectId!=null) annotation.setExternalObjectId(externalObjectId);
			
			String parentId = (String)jsonObj.get("parentId");
			if(parentId!=null) annotation.setParentId(parentId);
		
			String rootId = (String)jsonObj.get("rootId");
			if(rootId!=null) annotation.setRootId(rootId);
		
			String title = (String)jsonObj.get("title");
			if(title!=null) annotation.setTitle(title);
			
			String text = (String)jsonObj.get("text");
			if(text!=null) annotation.setText(text);
		
			String scope = (String)jsonObj.get("scope");
			if(scope!=null) annotation.setScopeFromString(scope);
		
			String format = (String)jsonObj.get("format");
			if(format!=null) annotation.setMimeType(format);
		
			String createdBy = (String)jsonObj.get("createdBy");
			if(createdBy!=null) annotation.setCreatedBy(createdBy);			

			Long created = (Long)jsonObj.get("created");
			if(created!=null) annotation.setCreated(new Date(created));
			
			Long modified = (Long)jsonObj.get("modified");
			if(modified!=null) annotation.setModified(new Date(modified));
			
			JSONObject jsonFragment = (JSONObject)jsonObj.get("fragment");			
			annotation.setAnnotatedFragment(fromJson(jsonFragment));									
			
			JSONArray jsonReplies=(JSONArray)jsonObj.get("replies");
			if(jsonReplies!=null) {
				ArrayList<ImageAnnotation> replies = fromJson(jsonReplies.toString());
				annotation.setReplies(replies);
			}
			
			JSONArray links = (JSONArray)jsonObj.get("links");
			if(links!=null) {
				for(Object link : links) {
					annotation.addLink(((String)link));					
				}
			}
			annotations.add(annotation);
		}
		return annotations;
	}
	
	private static ImageFragment fromJson(JSONObject jsonFragment) {
		ImageFragment fragment = null;
		if(jsonFragment!=null) {		
			JSONObject jsonImageRect = (JSONObject)jsonFragment.get("imageRect");
			int left = ((Long)jsonImageRect.get("left")).intValue();
			int top = ((Long)jsonImageRect.get("top")).intValue();
			int width = ((Long)jsonImageRect.get("width")).intValue();
			int height = ((Long)jsonImageRect.get("height")).intValue();
			ImageRect imageRect = new ImageRect(left,top,width,height);
			
			JSONObject jsonVisibleRect = (JSONObject)jsonFragment.get("visibleRect");
			left = ((Long)jsonVisibleRect.get("left")).intValue();
			top = ((Long)jsonVisibleRect.get("top")).intValue();
			width = ((Long)jsonVisibleRect.get("width")).intValue();
			height = ((Long)jsonVisibleRect.get("height")).intValue();
			ImageRect visibleRect = new ImageRect(left,top,width,height);
			
			JSONObject jsonShape = (JSONObject)jsonFragment.get("shape");
			left = ((Long)jsonShape.get("left")).intValue();
			top = ((Long)jsonShape.get("top")).intValue();
			width = ((Long)jsonShape.get("width")).intValue();
			height = ((Long)jsonShape.get("height")).intValue();
			int strokeWidth = ((Long)jsonShape.get("strokeWidth")).intValue();
			
			JSONObject jsonColor = (JSONObject)jsonShape.get("color");
			int r = ((Long)jsonColor.get("r")).intValue();
			int g = ((Long)jsonColor.get("g")).intValue();
			int b = ((Long)jsonColor.get("b")).intValue();				
			
			Shape shape = null;
			String type = (String)jsonShape.get("type");
			if(type.equals("ellipse")) 
				shape = new Ellipse(left,top,width,height,new Color(r,g,b),strokeWidth);
			else if(type.equals("rectangle")) 
				shape = new Rectangle(left,top,width,height,new Color(r,g,b),strokeWidth);
			else if(type.equals("cross")) 
				shape = new Cross(left,top,width,height,new Color(r,g,b),strokeWidth);
			else if(type.equals("polygon")) { 
				Collection<Point> points = new ArrayList<Point>();
				JSONArray jsonPoints=(JSONArray)JSONValue.parse((String)jsonShape.get("points"));
				for(Object jsonPoint : jsonPoints) {
					Point point = new Point(
							((Long)((JSONObject)jsonPoint).get("x")).intValue(),
							((Long)((JSONObject)jsonPoint).get("y")).intValue());
					points.add(point);
				}
				shape = new Polygon(left,top,width,height,new Color(r,g,b),strokeWidth,points);
			}	
			fragment = new ImageFragment(visibleRect, imageRect,shape);
		}
		return fragment;
	}
	
	public static String toJson(ImageAnnotation annotation) {
		Collection<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();
		annotations.add(annotation);
		return toJson(annotations);
	}
	
	public static String toJson(Collection<ImageAnnotation> annotations) {
		JSONArray jsonArray = new JSONArray();
		if(annotations!=null) {
			for(ImageAnnotation annotation : annotations) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", annotation.getId());
				jsonObj.put("objectId", annotation.getObjectId());
				jsonObj.put("linkedTo", annotation.getExternalObjectId());				
				jsonObj.put("parentId", annotation.getParentId());		
				jsonObj.put("rootId", annotation.getRootId());						
				jsonObj.put("title", annotation.getTitle());		
				jsonObj.put("text", annotation.getText());
				jsonObj.put("format", annotation.getMimeType());	
				jsonObj.put("scope", annotation.getScopeAsString());					
				jsonObj.put("modified", annotation.getModified().getTime());	
				jsonObj.put("created", annotation.getCreated().getTime());					
				jsonObj.put("createdBy", annotation.getCreatedBy());
				
				ImageFragment fragment = annotation.getAnnotatedFragment();
				if(fragment!=null) {														
					jsonObj.put("fragment", toJson(fragment));				
				}
				
				if(annotation.getReplies()!=null)
					jsonObj.put("replies", (JSONArray)JSONValue.parse(
							toJson(annotation.getReplies())));
				
				if(annotation.getLinks()!=null && !annotation.getLinks().isEmpty()) {
					JSONArray links = new JSONArray();
					for(String link : annotation.getLinks()) {
						links.add(link);
					}
					jsonObj.put("links",links);
				}
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray.toString();
	}
	
	private static JSONObject toJson(ImageFragment fragment) {
		JSONObject jsonFragment = new JSONObject();		
		
		JSONObject jsonImageRect= new JSONObject();
		jsonImageRect.put("left", fragment.getImageRect().getLeft());
		jsonImageRect.put("top", fragment.getImageRect().getTop());
		jsonImageRect.put("width", fragment.getImageRect().getWidth());
		jsonImageRect.put("height", fragment.getImageRect().getHeight());
		jsonFragment.put("imageRect", jsonImageRect);
		
		JSONObject jsonVisibleRect= new JSONObject();
		jsonVisibleRect.put("left", fragment.getVisibleRect().getLeft());
		jsonVisibleRect.put("top", fragment.getVisibleRect().getTop());
		jsonVisibleRect.put("width", fragment.getVisibleRect().getWidth());
		jsonVisibleRect.put("height", fragment.getVisibleRect().getHeight());
		jsonFragment.put("visibleRect", jsonVisibleRect);
		
		JSONObject jsonShape= new JSONObject();
		if(fragment.getShape() instanceof Ellipse)
			jsonShape.put("type", "ellipse");
		else if(fragment.getShape() instanceof Rectangle)
			jsonShape.put("type", "rectangle");
		else if(fragment.getShape() instanceof Cross)
			jsonShape.put("type", "cross");
		else if(fragment.getShape() instanceof Polygon) {
			jsonShape.put("type", "polygon");
			JSONArray jsonPoints = new JSONArray();
			for(Object point : ((Polygon)fragment.getShape()).getPoints()) {						
				JSONObject jsonPoint = new JSONObject();
				jsonPoint.put("x", ((Point)point).getX());
				jsonPoint.put("y", ((Point)point).getY());							
				jsonPoints.add(jsonPoint);
			}
			
			jsonShape.put("points", jsonPoints.toString());
		}	
		jsonShape.put("left", fragment.getShape().getLeft());
		jsonShape.put("top", fragment.getShape().getTop());
		jsonShape.put("width", fragment.getShape().getWidth());
		jsonShape.put("height", fragment.getShape().getHeight());
		jsonShape.put("strokeWidth", fragment.getShape().getStrokeWidth());
		
		JSONObject jsonColor= new JSONObject();
		jsonColor.put("r", fragment.getShape().getColor().getR());
		jsonColor.put("g", fragment.getShape().getColor().getG());
		jsonColor.put("b", fragment.getShape().getColor().getB());
		jsonShape.put("color", jsonColor);					
		jsonFragment.put("shape", jsonShape);			
	
		return jsonFragment;
	}
}
