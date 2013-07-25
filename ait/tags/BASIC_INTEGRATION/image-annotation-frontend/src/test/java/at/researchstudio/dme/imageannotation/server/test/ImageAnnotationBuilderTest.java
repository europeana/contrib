package at.researchstudio.dme.imageannotation.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

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
import at.researchstudio.dme.imageannotation.server.annotation.builder.JsonAnnotationBuilder;
import at.researchstudio.dme.imageannotation.server.annotation.builder.RdfXmlAnnotationBuilder;

public class ImageAnnotationBuilderTest {

	private ImageAnnotation createAnnotation() {
		// annotation w/ polygon fragment
		ImageAnnotation annotation = new ImageAnnotation("http://id","http://objectid",
				"externalObjectId",null,null,"user","title","text",ImageAnnotation.Scope.PUBLIC);
		Collection<Point> points = new ArrayList<Point>();
		points.add(new Point(350,75));
		points.add(new Point(379,161));
		points.add(new Point(469,161));
		points.add(new Point(397,215));
		
		Shape shape = new Polygon(0,0,100,120,new Color(0,120,255),1, points);
		ImageFragment fragment = new ImageFragment(
				new ImageRect(0,0,499,223),
				new ImageRect(-20,-30,700,400),
				shape);
		annotation.setAnnotatedFragment(fragment);
		annotation.setMimeType("image/gif");
		annotation.addLink("http://something.relevant.to.point.at");
		annotation.addLink("http://anotherthing.relevant.to.point.at");
		
		// add reply w/ rectangle fragment
		ImageAnnotation reply = new ImageAnnotation("http://id reply","http://objectid",
				"externalObjectId", "http://id", "http://id", "user reply","title reply",
				"text reply",ImageAnnotation.Scope.PUBLIC);
		
		Shape shapeReply = new Rectangle(0,0,100,100,new Color(0,120,255),1);
		ImageFragment fragmentReply = new ImageFragment(
				new ImageRect(0,0,499,223),
				new ImageRect(-20,-30,700,400),			
				shapeReply);
		reply.setAnnotatedFragment(fragmentReply);	
		
		annotation.addReply(reply);	
		
		// add reply w/ ellipse fragment
		ImageAnnotation reply2 = new ImageAnnotation("http://id reply2","http://objectid",
				"externalObjectId","http://id","http://id", "user reply2","title reply2",
				"text reply2",ImageAnnotation.Scope.PUBLIC);
		
		Shape shapeReply2 = new Ellipse(0,0,100,100,new Color(0,120,255),1);
		ImageFragment fragmentReply2 = new ImageFragment(
				new ImageRect(0,0,499,223),
				new ImageRect(-20,-30,700,400),			
				shapeReply2);
		reply2.setAnnotatedFragment(fragmentReply2);	
		
		annotation.addReply(reply2);	
		
		// add reply to the reply w/ cross fragment
		ImageAnnotation replyReply = new ImageAnnotation("http://id reply reply","http://objectid",
				"externalObjectId","http://id reply", "http://id", "user reply reply",
				"title reply reply","text reply reply",ImageAnnotation.Scope.PRIVATE);
		
		Cross shapeReplyReply = new Cross(0,0,100,100,new Color(0,120,255),1);
		ImageFragment fragmentReplyReply = new ImageFragment(
				new ImageRect(0,0,499,223),
				new ImageRect(-20,-30,700,400),			
				shapeReplyReply);
		replyReply.setAnnotatedFragment(fragmentReplyReply);	
		
		reply.addReply(replyReply);
		
		return annotation;
	}
	
	@Test
	public void testJson() {		
		ImageAnnotation annotation = createAnnotation();
		
		String jsonAnnotation=JsonAnnotationBuilder.toJson(annotation);		
		Collection<ImageAnnotation> annotations=JsonAnnotationBuilder.fromJson(jsonAnnotation);
		
		assertEquals(annotations.iterator().next(),annotation);						
	}
	
	@Test
	public void testRdfXml() throws RepositoryException, RDFHandlerException, IOException, 
		RDFParseException {
		
		ImageAnnotation annotation = createAnnotation();

		String rdfXmlAnnotation = RdfXmlAnnotationBuilder.toRdfXml(annotation);
		Collection<ImageAnnotation> annotations=
			RdfXmlAnnotationBuilder.fromRdfXml(rdfXmlAnnotation);				
		assertEquals(annotations.iterator().next(),annotation);
		
		annotations=RdfXmlAnnotationBuilder.fromRdfXml(null);		
		assertTrue(annotations.isEmpty());
		
		annotations=RdfXmlAnnotationBuilder.fromRdfXml("");		
		assertTrue(annotations.isEmpty());
	}
}
