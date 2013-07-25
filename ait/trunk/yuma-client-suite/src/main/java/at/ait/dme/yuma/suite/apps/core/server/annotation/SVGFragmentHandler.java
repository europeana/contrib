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

package at.ait.dme.yuma.suite.apps.core.server.annotation;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageFragment;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageRect;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Color;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Cross;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Ellipse;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.GeoPoint;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Line;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Point;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Polygon;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Polyline;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Rectangle;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Shape;

public class SVGFragmentHandler {
	
	private static final String ANNOTATION_IMAGE_NS = "http://lemo.mminf.univie.ac.at/annotation-image#";
	
	private static final String PX = "px";
	private static final String SVG = "svg";
	private static final String SVG_X = "x";
	private static final String SVG_Y = "y";
	private static final String SVG_CX = "cx";	
	private static final String SVG_CY = "cy";	
	private static final String SVG_RX = "rx";	
	private static final String SVG_RY = "ry";	
	private static final String SVG_X1 = "x1";	
	private static final String SVG_Y1 = "y1";
	private static final String SVG_X2 = "x2";
	private static final String SVG_Y2 = "y2";
	private static final String LAT = "lat";
	private static final String LNG = "lng";
	
	private static final String SVG_WIDTH = "width";
	private static final String SVG_HEIGHT = "height";
	private static final String SVG_VIEWBOX = "viewbox";
	private static final String SVG_SYMBOL = "symbol";
	private static final String SVG_SYMBOL_ID = "id";
	private static final String SVG_SYMBOL_ID_ELLIPSE = "Ellipse";
	private static final String SVG_SYMBOL_ID_RECTANGLE = "Rectangle";
	private static final String SVG_SYMBOL_ID_POLYGON = "Polygon";
	private static final String SVG_SYMBOL_ID_POLYLINE = "Polyline";	
	private static final String SVG_SYMBOL_ID_CROSS= "Cross";
	private static final String SVG_SYMBOL_ID_GEOPOINT= "GeoPoint";
	private static final String SVG_DEFINITIONS = "defs";
	private static final String SVG_USE = "use";
	private static final String SVG_IMAGE = "image";
	private static final String SVG_STROKE = "stroke";
	private static final String SVG_STROKE_WIDTH = "stroke-width";
	private static final String SVG_RECTANGLE = "rect";
	private static final String SVG_ELLIPSE = "ellipse";
	private static final String SVG_POLYGON = "polygon";
	private static final String SVG_POLYLINE = "polyline";
	private static final String SVG_LINE = "line";
	private static final String SVG_POINTS = "points";	
	private static final String SVG_FILL = "fill";
	private static final String SVG_PRESERVE_ASPECT_RATIO ="preserveAspectRatio";
	private static final String SVG_NONE = "none";
	private static final Namespace SVG_NS = Namespace.getNamespace(SVG, "http://www.w3.org/2000/svg");
	
	private static final String XLINK = "xlink";
	private static final String XLINK_HREF = "href";	
	private static final Namespace XLINK_NS = Namespace.getNamespace(XLINK,"http://www.w3.org/1999/xlink");
	
    private static Logger logger = Logger.getLogger(SVGFragmentHandler.class);
	
	public ImageFragment toImageFragment(String svg) throws IOException {
		ImageFragment fragment = null;
		Document document = null;
		
		try {
			fragment = new ImageFragment();			
			document = new SAXBuilder().build(new StringReader(svg));
			
			// VisibleRect
			ImageRect visibleRect = null; 
			Attribute viewBox = document.getRootElement().getAttribute(SVG_VIEWBOX);
			if(viewBox!=null) {
				String viewBoxValue=viewBox.getValue();
				String[] values=viewBoxValue.split(" ");
				if(values.length==4) {
					int left=new Integer(values[0].replace(PX, ""));
					int top=new Integer(values[1].replace(PX, ""));
					int width=new Integer(values[2].replace(PX, ""));
					int height=new Integer(values[3].replace(PX, ""));			
					visibleRect = new ImageRect(left,top,width,height);
				}
			}
			fragment.setVisibleRect(visibleRect);
					
			// ImageRect
			ImageRect imageRect = null; 
			Element imageElement = document.getRootElement().getChild(SVG_IMAGE, SVG_NS);
			if(imageElement!=null) imageRect = toImageRect(imageElement);		
			fragment.setImageRect(imageRect);		
			
			// Shape
			fragment.setShape(toShape(document));
		} catch (JDOMException e) {
			logger.error("invalid fragment", e);
		}
		
		return fragment;
	}
	
	/**
	 * Parse the SVG image rect.
	 * 
	 * @param element the DOM element
	 * @return the ImageRect
	 */
	private ImageRect toImageRect(Element element) {
		int left=0,top=0,width=0,height=0;
		
		String leftValue = element.getAttributeValue(SVG_X);
		if (leftValue != null)
			left=new Integer(leftValue.replace(PX, ""));
					
		String topValue = element.getAttributeValue(SVG_Y);
		if (topValue!=null)
			top=new Integer(topValue.replace(PX, ""));
		
		String widthValue = element.getAttributeValue(SVG_WIDTH);
		if (widthValue!=null)
			width=new Integer(widthValue.replace(PX, ""));
					
		String heightValue = element.getAttributeValue(SVG_HEIGHT);
		if (heightValue!=null)
			height=new Integer(heightValue.replace(PX, ""));
		
		return new ImageRect(left, top, width, height);
	}
	
	/**
	 * Parse the SVG symbol.
	 *
	 * @param document the Document
	 * @return shape the Shaoe
	 * @throws JDOMException
	 * @throws IOException
	 */
	private Shape toShape(Document document) throws JDOMException, IOException {		
		Shape shape = null;
		
		Element defs=document.getRootElement().getChild(SVG_DEFINITIONS, SVG_NS);
		if(defs==null) return null; 
		
		Element symbol=defs.getChild(SVG_SYMBOL, SVG_NS);		
		Element use=document.getRootElement().getChild(SVG_USE, SVG_NS);
		if(symbol==null||use==null) return null; 
		
		ImageRect useRect = toImageRect(use);
		
		String symbolId = symbol.getAttributeValue(SVG_SYMBOL_ID);	
		// parse rectangle
		if(symbolId.equals(SVG_SYMBOL_ID_RECTANGLE)) {
			Element rectElement=symbol.getChild(SVG_RECTANGLE, SVG_NS);
			Color color=parseStrokeColor(rectElement.getAttribute(SVG_STROKE));			
			int strokeWidth = new Integer(rectElement.getAttribute(SVG_STROKE_WIDTH).getValue());
			
			shape = new Rectangle(useRect.getLeft(), useRect.getTop(), 
					useRect.getWidth(), useRect.getHeight(), color, strokeWidth);			
		// parse ellipse		
		} else if(symbolId.equals(SVG_SYMBOL_ID_ELLIPSE)) {
			Element ellipseElement=symbol.getChild(SVG_ELLIPSE, SVG_NS);
			Color color=parseStrokeColor(ellipseElement.getAttribute(SVG_STROKE));			
			int strokeWidth = new Integer(ellipseElement.getAttribute(SVG_STROKE_WIDTH).getValue());

			shape = new Ellipse(useRect.getLeft(), useRect.getTop(), 
					useRect.getWidth(), useRect.getHeight(), color, strokeWidth);					
		// parse polygon or polyline
		} else if(symbolId.equals(SVG_SYMBOL_ID_POLYGON) || 
				symbolId.equals(SVG_SYMBOL_ID_POLYLINE)) {
			Element polyElement = null;
			if(symbolId.equals(SVG_SYMBOL_ID_POLYGON)) {
				polyElement=symbol.getChild(SVG_POLYGON, SVG_NS);
			} else {
				polyElement=symbol.getChild(SVG_POLYLINE, SVG_NS);
			}
			
			Color color=parseStrokeColor(polyElement.getAttribute(SVG_STROKE));			
			int strokeWidth = new Integer(polyElement.getAttribute(SVG_STROKE_WIDTH).getValue());
			if(symbolId.equals(SVG_SYMBOL_ID_POLYGON)) {				
				shape = new Polygon(useRect.getLeft(), useRect.getTop(), 
						useRect.getWidth(), useRect.getHeight(), color, strokeWidth);
			} else {
				shape = new Polyline(useRect.getLeft(), useRect.getTop(), 
						useRect.getWidth(), useRect.getHeight(), color, strokeWidth);				
			}

			String pointsValue=polyElement.getAttributeValue(SVG_POINTS);
			String[] points = pointsValue.split(" ");
			for (String point : points) {
				String[] coords=point.split(",");
				if(coords.length==2) {
					Point p = new Point(new Integer(coords[0]), new Integer(coords[1]));
					((Polygon)shape).addPoint(p);
				}
			}
		// parse cross	
		} else if(symbolId.equals(SVG_SYMBOL_ID_CROSS)) {
			@SuppressWarnings("unchecked")
			List<Element> lineElements = symbol.getChildren(SVG_LINE, SVG_NS);
			if(lineElements.size()!=4) return null;
			
			Element oneLineElement = lineElements.get(0);
			List<Line> lines = new ArrayList<Line>();
			for(Element lineElement : lineElements) {
				int x1=0,y1=0,x2=0,y2=0;
				String x1Value=lineElement.getAttributeValue(SVG_X1);
				if(x1Value!=null) x1 = new Integer(x1Value);				
				String y1Value=lineElement.getAttributeValue(SVG_Y1);
				if(y1Value!=null) y1 = new Integer(y1Value);								
				String x2Value=lineElement.getAttributeValue(SVG_X2);
				if(x2Value!=null) x2 = new Integer(x2Value);								
				String y2Value=lineElement.getAttributeValue(SVG_Y2);
				if(y2Value!=null) y2 = new Integer(y2Value);
								
				lines.add(new Line(x1,y1,x2,y2));
			}			
			Color color=parseStrokeColor(oneLineElement.getAttribute(SVG_STROKE));			
			int strokeWidth = new Integer(oneLineElement.getAttribute(SVG_STROKE_WIDTH).getValue());

			shape = new Cross(useRect.getLeft(), useRect.getTop(), 
					useRect.getWidth(), useRect.getHeight(),color, strokeWidth);									
		// parse geopoint
		} else if (symbolId.equals(SVG_SYMBOL_ID_GEOPOINT)) {
			Element pointElement=symbol.getChild(SVG_SYMBOL_ID_GEOPOINT, 
					Namespace.getNamespace(ANNOTATION_IMAGE_NS));
			int x=new Integer(pointElement.getAttributeValue(SVG_X));
			int y=new Integer(pointElement.getAttributeValue(SVG_Y));
			double lat=new Double(pointElement.getAttributeValue(LAT));
			double lng=new Double(pointElement.getAttributeValue(LNG));
			
			shape = new GeoPoint(useRect.getLeft(), useRect.getTop(), useRect.getWidth(), 
					useRect.getHeight(), new Point(x, y), lat, lng);
		}
		return shape;
	}

	/**
	 * Parse SVG stroke color.
	 * 
	 * @param stroke attribute
	 * @return color the color
	 */
	private Color parseStrokeColor(Attribute stroke) {
		Color color = new Color();
		if(stroke!=null) {
			String strokeValue=stroke.getValue();
			strokeValue=strokeValue.replace("rgb(","").replace(")", "");
			String[] values=strokeValue.split(",");
			if(values.length==3) {				
				color.setR(new Integer(values[0]));
				color.setG(new Integer(values[1]));
				color.setB(new Integer(values[2]));
			}
		}
		return color;
	}
	
	/**
	 * Creates an SVG representation for an ImageFragment.
	 * Example (see also http://www.w3.org/2000/svg):
	 * 
	 * <svg:svg xmlns:svg="http://www.w3.org/2000/svg" width="499px" height="223px" 
	 * 		viewBox="0px 0px 499px 223px">
	 *  
	 * 	<svg:defs> 
	 * 		<svg:symbol id="Ellipse"> 
	 * 			<svg:ellipse cx="50px" cy="50px" rx="50px" ry="50px" stroke="rgb(0,120,255)" 
	 * 					stroke-width="1" fill="none" />
	 * 		</svg:symbol>
	 * 	</svg:defs>
	 * 
	 * 	<svg:image xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="http://some.jpg" 
	 * 		x="-20px" y="-30px" width="1000px" height="700px" preserveAspectRatio="none" /> 
	 * 
	 * 	<svg:use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#Ellipse" x="0px" y="0px" 
	 * 		width="100px" height="100px" />
	 * 
	 * </svg:svg>
	 * 
	 * @param fragment the ImageFragment
	 * @return the SVG representation
	 * @throws IOException 
	 */
	public String toSVG(ImageFragment fragment) throws IOException {
		Shape shape = fragment.getShape();
		ImageRect visibleRect = fragment.getVisibleRect();

		// <svg:svg xmlns:svg="http://www.w3.org/2000/svg" width="499px" height="223px" 
		//      viewBox="0px 0px 499px 223px">
		Element svg = new Element(SVG, SVG_NS);
		svg.setAttribute(new Attribute(SVG_WIDTH, String.valueOf(visibleRect.getWidth())+PX));
		svg.setAttribute(new Attribute(SVG_HEIGHT, String.valueOf(visibleRect.getHeight())+PX));
		svg.setAttribute(new Attribute(SVG_VIEWBOX, 
				String.valueOf(visibleRect.getLeft()) + PX + " " + 
				String.valueOf(visibleRect.getTop()) + PX + " " +
				String.valueOf(visibleRect.getWidth()) + PX + " " + 
				String.valueOf(visibleRect.getHeight()) + PX));
		
		// <svg:image xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="http://some.jpg" 
		//      x="-20px" y="-30px" width="1000px" height="700px" preserveAspectRatio="none" />  
		ImageRect imageRect = fragment.getImageRect();		
		Element image = new Element(SVG_IMAGE, SVG_NS);
		image.setAttribute(new Attribute(SVG_X, String.valueOf(imageRect.getLeft()) + PX));
		image.setAttribute(new Attribute(SVG_Y, String.valueOf(imageRect.getTop()) + PX));		
		image.setAttribute(new Attribute(SVG_WIDTH, String.valueOf(imageRect.getWidth()) + PX));
		image.setAttribute(new Attribute(SVG_HEIGHT, String.valueOf(imageRect.getHeight()) + PX));
		image.setAttribute(new Attribute(SVG_PRESERVE_ASPECT_RATIO, SVG_NONE));
		
		// <svg:defs> 
		//      <svg:symbol id="Ellipse"> 
		//           <svg:ellipse cx="50px" cy="50px" rx="50px" ry="50px" stroke="rgb(0,120,255)" 
		//                stroke-width="1" fill="none" />
		//           </svg:symbol>
		//      </svg:defs>
		// 
		//      <svg:use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#Ellipse" x="0px" y="0px" 
		//           width="100px" height="100px" />
		Element symbol = new Element(SVG_SYMBOL, SVG_NS);
		Element use = new Element(SVG_USE, SVG_NS);		
		toSymbol(symbol, use, shape);
		
		Element defs = new Element(SVG_DEFINITIONS, SVG_NS);		
		defs.addContent(symbol);		
		
		svg.addContent(defs);		
		svg.addContent(image);
		svg.addContent(use);

		StringWriter sw = new StringWriter();
		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.output(svg, sw);
		return sw.toString();
	}

	private void toSymbol(Element symbol, Element use, Shape shape) {	
		// All coordinates are relative to the svg:use rect
		// see http://www.w3.org/TR/SVG/shapes.html#RectElement
		// NOTE: Why are we doing this ugly instanceof checks here? Instead, we could
		// implement shape.getSvg() and override the method for each type of shape. 
		// The problem is the shapes are also used on the client and therefore compiled 
		// to javascript which is not possible when using jdom. In the future we could
		// change that and use a client side parser.
		Element shapeEl = null;				
		if(shape instanceof Rectangle) {		
			symbol.setAttribute(SVG_SYMBOL_ID, SVG_SYMBOL_ID_RECTANGLE);
			use.setAttribute(new Attribute(XLINK_HREF, "#"+SVG_SYMBOL_ID_RECTANGLE, XLINK_NS));

			shapeEl = new Element(SVG_RECTANGLE, SVG_NS);
			shapeEl.setAttribute(new Attribute(SVG_X, "0"+PX));
			shapeEl.setAttribute(new Attribute(SVG_Y, "0"+PX));		
			shapeEl.setAttribute(new Attribute(SVG_WIDTH, String.valueOf(shape.getWidth())+PX));
			shapeEl.setAttribute(new Attribute(SVG_HEIGHT, String.valueOf(shape.getHeight())+PX));
			symbol.addContent(shapeEl);	
			
		// see http://www.w3.org/TR/SVG/shapes.html#EllipseElement
		} else if(shape instanceof Ellipse) {
			symbol.setAttribute(SVG_SYMBOL_ID, SVG_SYMBOL_ID_ELLIPSE);
			use.setAttribute(new Attribute(XLINK_HREF,"#"+SVG_SYMBOL_ID_ELLIPSE, XLINK_NS));

			Ellipse ellipse = (Ellipse) shape;
			shapeEl = new Element(SVG_ELLIPSE, SVG_NS);
			shapeEl.setAttribute(new Attribute(SVG_CX, 
					String.valueOf(ellipse.getCx()-shape.getLeft())+PX));
			shapeEl.setAttribute(new Attribute(SVG_CY, 
					String.valueOf(ellipse.getCy()-shape.getTop())+PX));
			shapeEl.setAttribute(new Attribute(SVG_RX, String.valueOf(ellipse.getRx())+PX));
			shapeEl.setAttribute(new Attribute(SVG_RY, String.valueOf(ellipse.getRy())+PX));
			symbol.addContent(shapeEl);		
			
		// http://www.w3.org/TR/SVG/shapes.html#PolygonElement
		} else if (shape instanceof Polygon) {
			if(shape instanceof Polyline) {
				symbol.setAttribute(SVG_SYMBOL_ID, SVG_SYMBOL_ID_POLYLINE);
				use.setAttribute(new Attribute(XLINK_HREF,"#"+SVG_SYMBOL_ID_POLYLINE, XLINK_NS));
				shapeEl = new Element(SVG_POLYLINE, SVG_NS);
			} else {
				symbol.setAttribute(SVG_SYMBOL_ID, SVG_SYMBOL_ID_POLYGON);
				use.setAttribute(new Attribute(XLINK_HREF,"#"+SVG_SYMBOL_ID_POLYGON, XLINK_NS));
				shapeEl = new Element(SVG_POLYGON, SVG_NS);
			}

			Polygon polygon = (Polygon) shape;
			String points="";
			for(Point point : polygon.getPoints()) {
				points += String.valueOf(point.getX()-polygon.getRelativeLeft()) + "," + 
					String.valueOf(((Point)point).getY()-polygon.getRelativeTop()) + " ";
			}			
			shapeEl.setAttribute(new Attribute(SVG_POINTS,points));	
			symbol.addContent(shapeEl);		
		
		// see http://www.w3.org/TR/SVG/shapes.html#LineElement
		// a cross is presented using 4 lines 
		} else if (shape instanceof Cross) {
			symbol.setAttribute(SVG_SYMBOL_ID, SVG_SYMBOL_ID_CROSS);
			use.setAttribute(new Attribute(XLINK_HREF,"#"+SVG_SYMBOL_ID_CROSS, XLINK_NS));
			
			Cross cross = (Cross) shape;
			for(Line line : cross.getLines()) {
				Element lineEl = new Element(SVG_LINE, SVG_NS);
				lineEl.setAttribute(new Attribute(SVG_X1, String.valueOf(line.getStart().getX())));
				lineEl.setAttribute(new Attribute(SVG_Y1, String.valueOf(line.getStart().getY())));	
				lineEl.setAttribute(new Attribute(SVG_X2, String.valueOf(line.getEnd().getX())));	
				lineEl.setAttribute(new Attribute(SVG_Y2, String.valueOf(line.getEnd().getY())));
				lineEl.setAttribute(new Attribute(SVG_STROKE,"rgb("+shape.getColor().getR()+","+
						shape.getColor().getG()+","+shape.getColor().getB()+")"));			
				lineEl.setAttribute(new Attribute(SVG_STROKE_WIDTH, 
						String.valueOf(shape.getStrokeWidth())));		
			
				symbol.addContent(lineEl);
			}
			
		// a geopoint is our own invention representing a location on a map and a pixel on an image 		
		} else if (shape instanceof GeoPoint) {
			symbol.setAttribute(SVG_SYMBOL_ID, SVG_SYMBOL_ID_GEOPOINT);
			use.setAttribute(new Attribute(XLINK_HREF,"#"+SVG_SYMBOL_ID_GEOPOINT, XLINK_NS));
			
			GeoPoint point = (GeoPoint) shape;
			shapeEl = new Element(SVG_SYMBOL_ID_GEOPOINT, ANNOTATION_IMAGE_NS);
			shapeEl.setAttribute(new Attribute(SVG_X, new Integer(point.getX()).toString()));
			shapeEl.setAttribute(new Attribute(SVG_Y, new Integer(point.getY()).toString()));
			shapeEl.setAttribute(new Attribute(LAT, new Double(point.getLat()).toString()));		
			shapeEl.setAttribute(new Attribute(LNG, new Double(point.getLng()).toString()));
			symbol.addContent(shapeEl);		
		}
		
		// use the defined symbol
		use.setAttribute(new Attribute(SVG_X, String.valueOf(shape.getLeft())+PX));
		use.setAttribute(new Attribute(SVG_Y, String.valueOf(shape.getTop())+PX));		
		use.setAttribute(new Attribute(SVG_WIDTH, String.valueOf(shape.getWidth())+PX));
		use.setAttribute(new Attribute(SVG_HEIGHT, String.valueOf(shape.getHeight())+PX));
		
		if(shapeEl!=null) {
			shapeEl.setAttribute(new Attribute(SVG_STROKE,"rgb("+shape.getColor().getR()+","+
					shape.getColor().getG()+","+shape.getColor().getB()+")"));			
			shapeEl.setAttribute(new Attribute(SVG_STROKE_WIDTH, 
					String.valueOf(shape.getStrokeWidth())));		
			shapeEl.setAttribute(new Attribute(SVG_FILL, SVG_NONE));
		}
	}
	
}
