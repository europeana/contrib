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

package at.ait.dme.yuma.server.annotation.builder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.sail.memory.MemoryStore;

import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.image.ImageFragment;
import at.ait.dme.yuma.client.image.ImageRect;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.shape.Color;
import at.ait.dme.yuma.client.image.shape.Cross;
import at.ait.dme.yuma.client.image.shape.Ellipse;
import at.ait.dme.yuma.client.image.shape.GeoPoint;
import at.ait.dme.yuma.client.image.shape.Line;
import at.ait.dme.yuma.client.image.shape.Point;
import at.ait.dme.yuma.client.image.shape.Polygon;
import at.ait.dme.yuma.client.image.shape.Polyline;
import at.ait.dme.yuma.client.image.shape.Rectangle;
import at.ait.dme.yuma.client.image.shape.Shape;
import at.ait.dme.yuma.server.util.W3CDateTimeParser;

/**
 * converts image annotations to rdf/xml and vice versa.
 * 
 * we use an Annotea (http://www.w3.org/2001/Annotea/) based model to represent annotations and 
 * mpeg21 media pointers as well as SVG (http://www.w3.org/2000/svg) to represent 
 * image fragments.
 * 
 * TODO should be refactored to have a common AnnotationBuilder sub classed by the 
 * media type specific annotation builders like the ImageAnnotationBuilder.
 * 
 * @author Christian Sadilek
 */
public class RdfXmlAnnotationBuilder {
	public static final String ANNOTATION_NS = "http://lemo.mminf.univie.ac.at/annotation-core#";
	public static final String ANNOTATION = ANNOTATION_NS+"Annotation";
	public static final String ANNOTATION_ANNOTATES = ANNOTATION_NS+"annotates";
	public static final String ANNOTATION_CREATED = ANNOTATION_NS+"created";
	public static final String ANNOTATION_MODIFIED = ANNOTATION_NS+"modified";			
	public static final String ANNOTATION_FRAGMENT = ANNOTATION_NS+"fragment";		
	public static final String ANNOTATION_LABEL = ANNOTATION_NS+"label";		
	public static final String ANNOTATION_AUTHOR = ANNOTATION_NS+"author";		
	
	public static final String ANNOTEA_ANNOTATION_NS = "http://www.w3.org/2000/10/annotation-ns#";
	public static final String ANNOTEA_ANNOTATION_BODY = ANNOTEA_ANNOTATION_NS+"body";

	public static final String ANNOTATION_REL_NS = "http://lemo.mminf.univie.ac.at/ann-relationship#";
	public static final String ANNOTATION_LINKED_TO = ANNOTATION_REL_NS+"isLinkedTo";
    public static final String ANNOTATION_LINKED_TO_RESOURCES = ANNOTATION_REL_NS+"isLinkedToResources";
    public static final Namespace ANNOTATION_REL_NAMESPACE = Namespace.getNamespace("rel", ANNOTATION_REL_NS);

	public static final String ANNOTATION_TYPE_NS = "http://www.w3.org/2000/10/annotationType#";	
	public static final String ANNOTATION_TYPE_COMMENT = ANNOTATION_TYPE_NS+"Comment";

	public static final String ANNOTATION_IMAGE_NS = "http://lemo.mminf.univie.ac.at/annotation-image#";
	public static final String ANNOTATION_IMAGE_FRAGMENT = ANNOTATION_IMAGE_NS+"svg-fragment";		

	public static final String ANNOTATION_SCOPE_NS = "http://lemo.mminf.univie.ac.at/ann-tel#";
	public static final String ANNOTATION_SCOPE = ANNOTATION_SCOPE_NS+"scope";		
	
	public static final String REPLY_TYPE = "http://www.w3.org/2001/12/replyType#";	
	public static final String REPLY_TYPE_AGREE = REPLY_TYPE+"Agree";		
	
	public static final String THREAD_NS = "http://www.w3.org/2001/03/thread#";
	public static final String THREAD_REPLY = THREAD_NS+"Reply";
	public static final String THREAD_IN_REPLY_TO = THREAD_NS+"inReplyTo";
	public static final String THREAD_ROOT = THREAD_NS+"root";

	public static final String HTTP_NS = "http://www.w3.org/1999/xx/http#";
	public static final String HTTP_BODY = HTTP_NS+"Body";
	public static final String HTTP_CONTENT_TYPE = HTTP_NS+"ContentType";
	public static final String HTTP_CONTENT_LENGTH = HTTP_NS+"ContentLength";
	
	public static final String DC = "dc";
	public static final String DUBLIN_CORE_NS = "http://purl.org/dc/elements/1.1/";
	public static final String DUBLIN_CORE_DATE = DUBLIN_CORE_NS+"date";	
	public static final String DUBLIN_CORE_CREATOR = DUBLIN_CORE_NS+"creator";
    public static final String DUBLIN_CORE_DESCRIPTION = DUBLIN_CORE_NS+"description";
	public static final String DUBLIN_CORE_TITLE = DUBLIN_CORE_NS+"title";
	public static final String DUBLIN_CORE_FORMAT = DUBLIN_CORE_NS+"format";
	public static final Namespace DUBLIN_CORE_NAMESPACE = Namespace.getNamespace(DC, DUBLIN_CORE_NS);
	
	public static final String RDFS = "rdfs";
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDFS_SEEALSO = RDFS_NS + "seeAlso";
	public static final Namespace RDFS_NAMESPACE = Namespace.getNamespace(RDFS, RDFS_NS);
	
	public static final Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

	public static final String PX = "px";
	public static final String SVG = "svg";
	public static final String SVG_X = "x";
	public static final String SVG_Y = "y";
	public static final String SVG_CX = "cx";	
	public static final String SVG_CY = "cy";	
	public static final String SVG_RX = "rx";	
	public static final String SVG_RY = "ry";	
	public static final String SVG_X1 = "x1";	
	public static final String SVG_Y1 = "y1";
	public static final String SVG_X2 = "x2";
	public static final String SVG_Y2 = "y2";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String NAME = "name";
	
	public static final String SVG_WIDTH = "width";
	public static final String SVG_HEIGHT = "height";
	public static final String SVG_VIEWBOX = "viewbox";
	public static final String SVG_SYMBOL = "symbol";
	public static final String SVG_SYMBOL_ID = "id";
	public static final String SVG_SYMBOL_ID_ELLIPSE = "Ellipse";
	public static final String SVG_SYMBOL_ID_RECTANGLE = "Rectangle";
	public static final String SVG_SYMBOL_ID_POLYGON = "Polygon";
	public static final String SVG_SYMBOL_ID_POLYLINE = "Polyline";	
	public static final String SVG_SYMBOL_ID_CROSS= "Cross";
	public static final String SVG_SYMBOL_ID_GEOPOINT= "GeoPoint";
	public static final String SVG_DEFINITIONS = "defs";
	public static final String SVG_USE = "use";
	public static final String SVG_IMAGE = "image";
	public static final String SVG_STROKE = "stroke";
	public static final String SVG_STROKE_WIDTH = "stroke-width";
	public static final String SVG_RECTANGLE = "rect";
	public static final String SVG_ELLIPSE = "ellipse";
	public static final String SVG_POLYGON = "polygon";
	public static final String SVG_POLYLINE = "polyline";
	public static final String SVG_LINE = "line";
	public static final String SVG_POINTS = "points";	
	public static final String SVG_FILL = "fill";
	public static final String SVG_PRESERVE_ASPECT_RATIO ="preserveAspectRatio";
	public static final String SVG_NONE = "none";
	public static final Namespace SVG_NS = Namespace.getNamespace(SVG, "http://www.w3.org/2000/svg");
	
	public static final String XLINK = "xlink";
	public static final String XLINK_HREF = "href";	
	public static final Namespace XLINK_NS = Namespace.getNamespace(XLINK,"http://www.w3.org/1999/xlink");
	
	private static final String MPEG21_MEDIA_POINTER ="mp";
	private static final String MPEG21_REGION_NODE ="region";
	private static final String MPEG21_REGION_RECT ="rect";
	private static final String MPEG21_REGION_ELLIPSE ="ellipse";
	private static final String MPEG21_REGION_POLYGON ="polygon";
	
	private static Logger logger = Logger.getLogger(RdfXmlAnnotationBuilder.class);
	
	/**
	 * create an rdf/xml representation of the given annotation
	 * see example: http://www.w3.org/2001/Annotea/User/Protocol.html .
	 * 
	 * @param annotation
	 * @return string representation
	 */
	public static String toRdfXml(ImageAnnotation annotation) throws RepositoryException, 
		RDFHandlerException, IOException {		
		annotation.setText(StringEscapeUtils.escapeXml(annotation.getText()));
		annotation.setTitle(StringEscapeUtils.escapeXml(annotation.getTitle()));
		
		Repository repository = new SailRepository(new MemoryStore());
		repository.initialize();		
		RepositoryConnection con = repository.getConnection();		
		ValueFactory valueFactory = repository.getValueFactory();
			
		toRdf(con,valueFactory,annotation);	
	    
	    StringWriter sw = new StringWriter();
	    RDFXMLWriter rdfXmlWriter=new RDFXMLPrettyWriter(sw);
	    rdfXmlWriter.handleNamespace("a", ANNOTATION_NS);
	    rdfXmlWriter.handleNamespace("ann", ANNOTEA_ANNOTATION_NS);
	    rdfXmlWriter.handleNamespace("scope", ANNOTATION_SCOPE_NS);
	    rdfXmlWriter.handleNamespace("image", ANNOTATION_IMAGE_NS);
	    rdfXmlWriter.handleNamespace("rel", ANNOTATION_REL_NS);
	    
	    rdfXmlWriter.handleNamespace("dc", DUBLIN_CORE_NS);
	    rdfXmlWriter.handleNamespace("h", HTTP_NS);
	    rdfXmlWriter.handleNamespace("tr", THREAD_NS);
	    rdfXmlWriter.handleNamespace("svg", SVG_NS.getURI());
	    
	    con.export(rdfXmlWriter);
	    return sw.toString();
	}
	
	private static void toRdf(RepositoryConnection con, ValueFactory vFactory, ImageAnnotation 
			annotation) throws RepositoryException, RDFHandlerException, IOException {		
		Resource annotationNode = (annotation.getId()==null)?
				vFactory.createBNode():vFactory.createURI(annotation.getId());
				
		URI annotationType = vFactory.createURI(ANNOTATION);
		URI annotationCommentType = vFactory.createURI(ANNOTATION_TYPE_COMMENT);		
		URI replyType = vFactory.createURI(THREAD_REPLY);
		URI agreeType = vFactory.createURI(REPLY_TYPE_AGREE);		
		URI annotatesPredicate = vFactory.createURI(ANNOTATION_ANNOTATES);
		URI linkedToPredicate = vFactory.createURI(ANNOTATION_LINKED_TO);
		URI annotationCreatedPredicate = vFactory.createURI(ANNOTATION_CREATED);
		URI annotationModifiedPredicate = vFactory.createURI(ANNOTATION_MODIFIED);		
		URI annotationBodyPredicate = vFactory.createURI(ANNOTEA_ANNOTATION_BODY);
		URI annotationLabelPredicate = vFactory.createURI(ANNOTATION_LABEL);		
		URI annotationFragmentPredicate = vFactory.createURI(ANNOTATION_FRAGMENT);
		URI annotationLinkedToResourcesPredicate = vFactory.createURI(ANNOTATION_LINKED_TO_RESOURCES);
		URI imageFragmentPredicate = vFactory.createURI(ANNOTATION_IMAGE_FRAGMENT);
		URI annotationAuthorPredicate = vFactory.createURI(ANNOTATION_AUTHOR);
		URI annotationScopePredicate = vFactory.createURI(ANNOTATION_SCOPE);
		
		URI rdfsSeeAlso = vFactory.createURI(RDFS_SEEALSO); 
		
		URI dcTitlePredicate = vFactory.createURI(DUBLIN_CORE_TITLE);
		URI dcCreatorPredicate = vFactory.createURI(DUBLIN_CORE_CREATOR);
		URI dcDateTypePredicate = vFactory.createURI(DUBLIN_CORE_DATE);
		URI dcFormatPredicate = vFactory.createURI(DUBLIN_CORE_FORMAT);		
		URI threadRootPredicate = vFactory.createURI(THREAD_ROOT);
		URI threadInReplyToPredicate = vFactory.createURI(THREAD_IN_REPLY_TO);
		
		Resource annotationBodyNode = vFactory.createBNode();
		URI htmlContentTypePredicate = vFactory.createURI(HTTP_CONTENT_TYPE);
		URI htmlContentLengthPredicate = vFactory.createURI(HTTP_CONTENT_LENGTH);
		URI htmlBodyPredicate = vFactory.createURI(HTTP_BODY);
	
		// annotation resource
		con.add(annotationNode,annotatesPredicate,vFactory.createURI(annotation.getImageUrl()));
		
		// seeAlso
		if(annotation.hasLinks()) {
			for(String link : annotation.getLinks()) 
				con.add(annotationNode, rdfsSeeAlso, vFactory.createURI(link));					
		}
		
		// linked to
		if(annotation.getExternalObjectId()!=null) {
			con.add(annotationNode,linkedToPredicate,
					vFactory.createLiteral(annotation.getExternalObjectId()));
		}
		
		// reply?
		if(annotation.getParentId()!=null) {
			con.add(annotationNode,RDF.TYPE,replyType);
			con.add(annotationNode,RDF.TYPE,agreeType);
			con.add(annotationNode,threadInReplyToPredicate,
					vFactory.createURI(annotation.getParentId()));			
		} else {
			con.add(annotationNode,RDF.TYPE,annotationType);
			con.add(annotationNode,RDF.TYPE,annotationCommentType);					
		}
		
		// root
		if(annotation.getRootId()!=null)
			con.add(annotationNode,threadRootPredicate,vFactory.createURI(annotation.getRootId()));		
		
		// title
		if(annotation.getTitle()!=null)			
			con.add(annotationNode,dcTitlePredicate, vFactory.createLiteral(annotation.getTitle()));
		
		// creator and author
		if(annotation.getCreatedBy()!=null) {
			con.add(annotationNode,annotationAuthorPredicate,
					vFactory.createLiteral(annotation.getCreatedBy()));
			con.add(annotationNode,dcCreatorPredicate,
					vFactory.createLiteral(annotation.getCreatedBy()));
		}
		
		// date
		String modified=W3CDateTimeParser.formatW3CDateTime(annotation.getModified());		
		con.add(annotationNode,annotationModifiedPredicate,vFactory.createLiteral(modified));		
		con.add(annotationNode,dcDateTypePredicate,vFactory.createLiteral(modified));
		String date=W3CDateTimeParser.formatW3CDateTime(annotation.getCreated());
		con.add(annotationNode,annotationCreatedPredicate,vFactory.createLiteral(date));	
		
		// format (mime type)
		if(annotation.getMimeType()!=null)
			con.add(annotationNode,dcFormatPredicate,
					vFactory.createLiteral(annotation.getMimeType()));		
		
		// annotated image fragment
		if(annotation.hasFragment()) {
			con.add(annotationNode, annotationFragmentPredicate,
					vFactory.createLiteral(createMpeg21FragmentURI(annotation)));
			con.add(annotationNode, imageFragmentPredicate,
					vFactory.createLiteral(createSvgFragment(annotation),RDF.XMLLITERAL));			
		}
		
		// linked to resources
		if(annotation.hasSemanticTags()) {
		    con.add(annotationNode, annotationLinkedToResourcesPredicate, 
		    		vFactory.createLiteral(createLinkedToResourcesFragment(annotation)));
		}
		
		// body and label
		if(annotation.getText()!=null)
			con.add(annotationNode, annotationLabelPredicate, 
					vFactory.createLiteral(annotation.getText()));	    
		String annotationBody = annotation.toHtml();
	    con.add(annotationNode, annotationBodyPredicate, annotationBodyNode);	    
	    con.add(annotationBodyNode,htmlContentTypePredicate,vFactory.createLiteral("text/html"));
	    con.add(annotationBodyNode,htmlContentLengthPredicate,
	    		vFactory.createLiteral(new Integer(annotationBody.length()).toString()));	    
	    con.add(annotationBodyNode,htmlBodyPredicate, 
	    		vFactory.createLiteral(annotationBody,RDF.XMLLITERAL));	
	    
	    // scope
	    con.add(annotationNode, annotationScopePredicate, 
	    		vFactory.createLiteral(annotation.getScopeAsString()));
	    
	    // replies
	    if(annotation.hasReplies()) {
	    	for(ImageAnnotation reply : annotation.getReplies()) 
	    		toRdf(con,vFactory, reply);
	    }
	}
	
	private static String createLinkedToResourcesFragment(ImageAnnotation annotation) throws IOException {

        StringWriter sw = new StringWriter();
        XMLOutputter xmlOutputter = new XMLOutputter();

        if(annotation.hasSemanticTags()) {
		    for(SemanticTag t : annotation.getSemanticTags()) {
		        Element tagEl = new Element("linked-resource");
		        Element title = new Element("title", DUBLIN_CORE_NAMESPACE);
		        Element type = new Element("type", DUBLIN_CORE_NAMESPACE);
		        Element seeAlso = new Element("seeAlso", RDFS_NAMESPACE);
		        Element language = new Element("language", DUBLIN_CORE_NAMESPACE);
		        Element description = new Element("description", DUBLIN_CORE_NAMESPACE);
		        
		        title.setText(t.getTitle());
		        type.setText(t.getType());
		        seeAlso.setAttribute(new Attribute("resource", t.getURI(), RDF_NAMESPACE));
		        language.setText(StringEscapeUtils.escapeXml(t.getLang()));
		        description.setText(StringEscapeUtils.escapeXml(t.getDescription()));
		        
		        tagEl.addContent(title);
		        tagEl.addContent(type);
	            tagEl.addContent(seeAlso);
	            tagEl.addContent(language);
	            tagEl.addContent(description);
	            
	            xmlOutputter.output(tagEl, sw);
		    }
        }
	    
        return sw.toString();
	    
    }

    /**
	 * create a mpeg21 fragment URI using a media pointer
	 * 
	 * @param annotation
	 * @return URI
	 */
	private static String createMpeg21FragmentURI(ImageAnnotation annotation) {
		// TODO zoom level has still to be considered here
		String mpeg21URI = annotation.getImageUrl()+"#";
		ImageFragment fragment = annotation.getFragment();
		Shape shape = fragment.getShape();
		
		mpeg21URI+=MPEG21_MEDIA_POINTER + "(~" + MPEG21_REGION_NODE + "(";
		// for polygons the first pair of coordinates use absolute values, the subsequent coords
		// are relative to the corresponding previous coordinate
		if(shape instanceof Polygon) {		
			mpeg21URI+=MPEG21_REGION_POLYGON + "(";
			Polygon polygon = (Polygon) shape;
			ArrayList<Point> points = new ArrayList<Point>(polygon.getPoints());
			Point firstPoint=points.remove(0);
			mpeg21URI+=String.valueOf(polygon.getLeft()+firstPoint.getX())+
				","+String.valueOf(polygon.getTop()+firstPoint.getY());
			Point previousPoint = firstPoint;
			for(Point point : points) {
				mpeg21URI += "," + String.valueOf(point.getX()-previousPoint.getX()) + 
					"," + String.valueOf(((Point)point).getY()-previousPoint.getY());
				previousPoint = point;
			}
			mpeg21URI+=")";
			
		} else if(shape instanceof Ellipse) {		
			mpeg21URI+=MPEG21_REGION_ELLIPSE + "(";
			Ellipse ellipse = (Ellipse) shape;			
			mpeg21URI+=String.valueOf(ellipse.getCx())+","+String.valueOf(ellipse.getCy())+",";
			mpeg21URI+=String.valueOf(ellipse.getCx()+ellipse.getRx())+","+
				String.valueOf(ellipse.getRy())+",";
			mpeg21URI+=String.valueOf(ellipse.getCx()+ellipse.getRx())+","+
				String.valueOf(ellipse.getCy()+ellipse.getRy());		
			mpeg21URI+=")";
			
		// in case of all other shapes we use rectangles	
		} else {		
			mpeg21URI+=MPEG21_REGION_RECT + "(";
			mpeg21URI+=String.valueOf(shape.getLeft())+","+String.valueOf(shape.getTop())+",";
			mpeg21URI+=String.valueOf(shape.getLeft()+shape.getWidth())+","
				+String.valueOf(shape.getTop()+shape.getHeight());
			mpeg21URI+=")";
		}
		mpeg21URI+="))";

		return mpeg21URI;		
	}
	
	/**
	 * create a svg representation of the annotated fragment
	 *  
	 * example of svg fragment (see also http://www.w3.org/2000/svg):
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
	 * @param annotation
	 * @return string svg representation
	 */
	private static String createSvgFragment(ImageAnnotation annotation) throws IOException {
		ImageFragment fragment = annotation.getFragment();
		Shape shape = fragment.getShape();
		ImageRect visibleRect = fragment.getVisibleRect();

		/* 
		 * <svg:svg xmlns:svg="http://www.w3.org/2000/svg" width="499px" height="223px" 
		 *		viewBox="0px 0px 499px 223px">
		 */
		Element svg = new Element(SVG, SVG_NS);
		svg.setAttribute(new Attribute(SVG_WIDTH, String.valueOf(visibleRect.getWidth())+PX));
		svg.setAttribute(new Attribute(SVG_HEIGHT, String.valueOf(visibleRect.getHeight())+PX));
		svg.setAttribute(new Attribute(SVG_VIEWBOX, 
				String.valueOf(visibleRect.getLeft()) + PX + " " + 
				String.valueOf(visibleRect.getTop()) + PX + " " +
				String.valueOf(visibleRect.getWidth()) + PX + " " + 
				String.valueOf(visibleRect.getHeight()) + PX));
		
		/*
		 * <svg:image xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="http://some.jpg" 
		 *		x="-20px" y="-30px" width="1000px" height="700px" preserveAspectRatio="none" />  
		 */
		ImageRect imageRect = fragment.getImageRect();		
		Element image = new Element(SVG_IMAGE,SVG_NS);
		image.setAttribute(new Attribute(XLINK_HREF,annotation.getImageUrl(),XLINK_NS));
		image.setAttribute(new Attribute(SVG_X, String.valueOf(imageRect.getLeft())+PX));
		image.setAttribute(new Attribute(SVG_Y, String.valueOf(imageRect.getTop())+PX));		
		image.setAttribute(new Attribute(SVG_WIDTH, String.valueOf(imageRect.getWidth())+PX));
		image.setAttribute(new Attribute(SVG_HEIGHT, String.valueOf(imageRect.getHeight())+PX));
		image.setAttribute(new Attribute(SVG_PRESERVE_ASPECT_RATIO,SVG_NONE));
		
		/*
		 * <svg:defs> 
		 * 		<svg:symbol id="Ellipse"> 
		 * 			<svg:ellipse cx="50px" cy="50px" rx="50px" ry="50px" stroke="rgb(0,120,255)" 
		 * 					stroke-width="1" fill="none" />
		 * 		</svg:symbol>
		 * 	</svg:defs>
		 * 
		 * 
		 * 	<svg:use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#Ellipse" x="0px" y="0px" 
		 * 		width="100px" height="100px" />
		 */		
		Element symbol = new Element(SVG_SYMBOL, SVG_NS);
		Element use = new Element(SVG_USE, SVG_NS);		
		createSvgSymbol(symbol, use, shape);
		
		Element defs = new Element(SVG_DEFINITIONS, SVG_NS);		
		defs.addContent(symbol);		
		
		svg.addContent(defs);		
		svg.addContent(image);
		svg.addContent(use);

		// write jdom tree to string
		StringWriter sw = new StringWriter();
		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.output(svg, sw);
		return sw.toString();
	}
	
	/**
	 * creates the svg shape see http://www.w3.org/TR/SVG/shapes.html .
	 * 
	 * @param symbol
	 * @param use
	 * @param shape
	 */
	private static void createSvgSymbol(Element symbol, Element use, Shape shape) {	
		// all coordinates are relative to the svg:use rect
		// see http://www.w3.org/TR/SVG/shapes.html#RectElement
		// NOTE: Why are we doing this ugly instanceof checks here? Instead, we could
		// implement shape.getSvg() and override the method for each type of shape. 
		// The problem is the shapes are also used on the client and therefore compiled 
		// to javascript which is not possible when using jdom. In the future we could
		// change that and use a client side parser.
		Element shapeEl = null;				
		if(shape instanceof Rectangle) {		
			symbol.setAttribute(SVG_SYMBOL_ID, SVG_SYMBOL_ID_RECTANGLE);
			use.setAttribute(new Attribute(XLINK_HREF,"#"+SVG_SYMBOL_ID_RECTANGLE, XLINK_NS));

			shapeEl = new Element(SVG_RECTANGLE,SVG_NS);
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
			shapeEl = new Element(SVG_ELLIPSE,SVG_NS);
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
			shapeEl.setAttribute(new Attribute(NAME, point.getName()));
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
	
	/**
	 * parse a svg fragment representation.
	 * 
	 * @param svgFragment
	 * @return image fragment
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static ImageFragment parseSvgFragment(String svgFragment) throws IOException {
			
		ImageFragment fragment = null;
		Document document = null;
		try {
			fragment = new ImageFragment();			
			document = new SAXBuilder().build(new StringReader(svgFragment));
			
			// visible rect
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
					
			// image rect
			ImageRect imageRect = null; 
			Element imageElement = document.getRootElement().getChild(SVG_IMAGE,SVG_NS);
			if(imageElement!=null) imageRect = parseSvgRect(imageElement);		
			fragment.setImageRect(imageRect);		
			
			// shape
			fragment.setShape(parseSvgSymbol(document));
		} catch (JDOMException e) {
			logger.error("invalid fragment", e);
		}
		
		return fragment;
	}
	
	/**
	 * parse a svg rect.
	 * 
	 * @param rectElement
	 * @return image rect
	 */
	private static ImageRect parseSvgRect(Element rectElement) {
		int left=0,top=0,width=0,height=0;
		
		String leftValue=rectElement.getAttributeValue(SVG_X);
		if(leftValue!=null) left=new Integer(leftValue.replace(PX, ""));
					
		String topValue=rectElement.getAttributeValue(SVG_Y);
		if(topValue!=null) top=new Integer(topValue.replace(PX, ""));
		
		String widthValue=rectElement.getAttributeValue(SVG_WIDTH);
		if(widthValue!=null) width=new Integer(widthValue.replace(PX, ""));
					
		String heightValue=rectElement.getAttributeValue(SVG_HEIGHT);
		if(heightValue!=null) height=new Integer(heightValue.replace(PX, ""));
		
		return new ImageRect(left,top,width,height);
	}
	
	/**
	 * parse a svg stroke color.
	 * 
	 * @param stroke attribute
	 * @return color
	 */
	private static Color parseSvgStrokeColor(Attribute stroke) {
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
	 * parse svg stroke width.
	 * 
	 * @param strokeWidth
	 * @return width
	 */
	private static int parseSvgStrokeWidth(Attribute strokeWidth) {
		String strokeWidthValue = strokeWidth.getValue();
		return new Integer(strokeWidthValue);
	}
	
	/**
	 * parse svg symbol.
	 * 
	 * @param document
	 * @return shape
	 * @throws JDOMException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static Shape parseSvgSymbol(Document document) throws JDOMException, IOException {		
		Shape shape = null;
		
		Element defs=document.getRootElement().getChild(SVG_DEFINITIONS, SVG_NS);
		if(defs==null) return null; 
		
		Element symbol=defs.getChild(SVG_SYMBOL, SVG_NS);		
		Element use=document.getRootElement().getChild(SVG_USE, SVG_NS);
		if(symbol==null||use==null) return null; 
		
		ImageRect useRect=parseSvgRect(use);
		
		String symbolId = symbol.getAttributeValue(SVG_SYMBOL_ID);	
		// parse rectangle
		if(symbolId.equals(SVG_SYMBOL_ID_RECTANGLE)) {
			Element rectElement=symbol.getChild(SVG_RECTANGLE, SVG_NS);
			Color color=parseSvgStrokeColor(rectElement.getAttribute(SVG_STROKE));			
			int strokeWidth=parseSvgStrokeWidth(rectElement.getAttribute(SVG_STROKE_WIDTH));
			
			shape = new Rectangle(useRect.getLeft(), useRect.getTop(), 
					useRect.getWidth(), useRect.getHeight(), color, strokeWidth);			
		// parse ellipse		
		} else if(symbolId.equals(SVG_SYMBOL_ID_ELLIPSE)) {
			Element ellipseElement=symbol.getChild(SVG_ELLIPSE, SVG_NS);
			Color color=parseSvgStrokeColor(ellipseElement.getAttribute(SVG_STROKE));			
			int strokeWidth=parseSvgStrokeWidth(ellipseElement.getAttribute(SVG_STROKE_WIDTH));

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
			
			Color color=parseSvgStrokeColor(polyElement.getAttribute(SVG_STROKE));			
			int strokeWidth=parseSvgStrokeWidth(polyElement.getAttribute(SVG_STROKE_WIDTH));
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
			List<Element> lineElements=symbol.getChildren(SVG_LINE, SVG_NS);
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
			Color color=parseSvgStrokeColor(oneLineElement.getAttribute(SVG_STROKE));			
			int strokeWidth=parseSvgStrokeWidth(oneLineElement.getAttribute(SVG_STROKE_WIDTH));

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
			String name=pointElement.getAttributeValue(NAME);
			
			shape = new GeoPoint(useRect.getLeft(), useRect.getTop(), useRect.getWidth(), 
					useRect.getHeight(), name, new Point(x, y), lat, lng);
		}
		return shape;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<SemanticTag> parseLinkedResources(String linkedToResources) throws IOException {
	    ArrayList<SemanticTag> semanticTags = null;
        Document document = null;
        try {
            semanticTags = new ArrayList<SemanticTag>();         
            document = new SAXBuilder().build(new StringReader(linkedToResources));
            
            List<Element> resources = document.getRootElement().getChildren();
            for(Element e : resources) {
                if(e.getName().equals("linked-resource")) {
                	String title = e.getChildText("title", DUBLIN_CORE_NAMESPACE);
                    String type = e.getChildText("type", DUBLIN_CORE_NAMESPACE);
                    String seeAlso = e.getChild("seeAlso", RDFS_NAMESPACE).getAttributeValue("resource", RDF_NAMESPACE);
                    String language = StringEscapeUtils.unescapeXml(e.getChildText("language", DUBLIN_CORE_NAMESPACE));
                    String description = StringEscapeUtils.unescapeXml(e.getChildText("description", DUBLIN_CORE_NAMESPACE));
                    semanticTags.add(new SemanticTag(title, type, language, description, seeAlso));
                }
            }
            
        } catch(JDOMException e) {
            logger.error("invalid linkedToResources", e);
        }
        
        return semanticTags;
	    
	    
	}
	
	/**
	 * convert a rdf/xml representation into an <code>ImageAnnotation</code>. The representation
	 * could contain more than one annotation. That is why we return a <code>Collection</code>.
	 * 
	 * @param rdfXml
	 * @return collection of image annotations
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	public static Collection<ImageAnnotation> fromRdfXml(String rdfXml) throws RDFParseException, 
		RDFHandlerException, IOException {		 
		
		if(rdfXml==null || rdfXml.isEmpty())
			return new ArrayList<ImageAnnotation>();
		
		RdfXmlAnnotationHandler annotationRdfHandler=new RdfXmlAnnotationHandler(); 
		RDFParser parser = new RDFXMLParser();
		parser.setRDFHandler(annotationRdfHandler);
		parser.setVerifyData(true);
		parser.parse(new StringReader(rdfXml), "http://foo/bar");

		return annotationRdfHandler.getAnnotations();
	}
}
