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

package at.ait.dme.yuma.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import at.ait.dme.yuma.client.image.ImageFragment;
import at.ait.dme.yuma.client.image.ImageRect;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.client.image.shape.Color;
import at.ait.dme.yuma.client.image.shape.Cross;
import at.ait.dme.yuma.client.image.shape.Ellipse;
import at.ait.dme.yuma.client.image.shape.GeoPoint;
import at.ait.dme.yuma.client.image.shape.Point;
import at.ait.dme.yuma.client.image.shape.Polygon;
import at.ait.dme.yuma.client.image.shape.Rectangle;
import at.ait.dme.yuma.client.image.shape.Shape;
import at.ait.dme.yuma.client.image.shape.VoidShape;
import at.ait.dme.yuma.server.annotation.builder.JsonAnnotationBuilder;
import at.ait.dme.yuma.server.annotation.builder.RdfXmlAnnotationBuilder;

public class ImageAnnotationBuilderTest {

	private ImageAnnotation createAnnotation() {
		// annotation w/ polygon fragment
		ImageAnnotation annotation = new ImageAnnotation("http://id","http://objectid",
				"externalObjectId",null,null,"user","title","text", ImageAnnotation.Scope.PUBLIC);
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
		annotation.setFragment(fragment);
		annotation.setMimeType("image/gif");
		annotation.addLink("http://something.relevant.to.point.at");
		annotation.addLink("http://anotherthing.relevant.to.point.at");
		
		// add reply w/ rectangle fragment
		ImageAnnotation reply = new ImageAnnotation("http://id reply","http://objectid",
				"externalObjectId", "http://id", "http://id", "user reply","title reply",
				"text reply", ImageAnnotation.Scope.PUBLIC);
		
		Shape shapeReply = new Rectangle(0,0,100,100,new Color(0,120,255),1);
		ImageFragment fragmentReply = new ImageFragment(
				new ImageRect(0,0,499,223),
				new ImageRect(-20,-30,700,400),			
				shapeReply);
		reply.setFragment(fragmentReply);	
		
		annotation.addReply(reply);	
		
		// add reply w/ ellipse fragment
		ImageAnnotation reply2 = new ImageAnnotation("http://id reply2","http://objectid",
				"externalObjectId","http://id","http://id", "user reply2","title reply2",
				"text reply2", ImageAnnotation.Scope.PUBLIC);
		
		Shape shapeReply2 = new Ellipse(0,0,100,100,new Color(0,120,255),1);
		ImageFragment fragmentReply2 = new ImageFragment(
				new ImageRect(0,0,499,223),
				new ImageRect(-20,-30,700,400),			
				shapeReply2);
		reply2.setFragment(fragmentReply2);	
		
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
		replyReply.setFragment(fragmentReplyReply);	
		
		reply.addReply(replyReply);
		
		// add reply to the reply w/o fragment
		ImageAnnotation replyReply2 = new ImageAnnotation("http://id reply reply 2","http://objectid",
				"externalObjectId","http://id reply", "http://id", "user reply reply",
				"title reply reply","text reply reply",ImageAnnotation.Scope.PRIVATE);
		
		reply.addReply(replyReply2);
		
		return annotation;
	}
	
	private ImageAnnotation createGeoPointAnnotation() {
		ImageFragment fragment = new ImageFragment(
				new ImageRect(0, 0, 10492, 11908),
				new ImageRect(-20, -30, 10462, 11888),
				new GeoPoint("Aruba", 4460, 6553, 12.478056909259756, -69.93896484375));
		
		// id is just for testing, will be generated by db
		ImageAnnotation annotation = new ImageAnnotation("http://id", "http://urlToMap", "user");
		annotation.setFragment(fragment);
		
		// optionally we could set title and text
		annotation.setTitle("controlpoint");
		annotation.setText("This is a controlpoint for Aruba on the Map of the Americas by Diego Guiterrez");
		
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
		assertEquals(annotations.iterator().next(), annotation);
		
		annotation = createGeoPointAnnotation();
		rdfXmlAnnotation = RdfXmlAnnotationBuilder.toRdfXml(annotation);
		annotations = RdfXmlAnnotationBuilder.fromRdfXml(rdfXmlAnnotation);				
		assertEquals(annotations.iterator().next(), annotation);
		
		annotations=RdfXmlAnnotationBuilder.fromRdfXml(null);		
		assertTrue(annotations.isEmpty());
		
		annotations=RdfXmlAnnotationBuilder.fromRdfXml("");		
		assertTrue(annotations.isEmpty());
	}
	
	@Test
	public void testAnnotationWithoutFragment() throws Exception {
		ImageAnnotation annotation = new ImageAnnotation("http://id", "http://objectid",
				"externalObjectId", null, null, "user", "title", "text",
				ImageAnnotation.Scope.PUBLIC);
		
		String jsonAnnotation = JsonAnnotationBuilder.toJson(annotation);
		Collection<ImageAnnotation> annotations=
			JsonAnnotationBuilder.fromJson(jsonAnnotation);				
		assertEquals(annotations.iterator().next(), annotation);
		
		String rdfXmlAnnotation = RdfXmlAnnotationBuilder.toRdfXml(annotation);
		annotations= RdfXmlAnnotationBuilder.fromRdfXml(rdfXmlAnnotation);				
		assertEquals(annotations.iterator().next(), annotation);
		
		ImageAnnotation a = annotations.iterator().next();
		assertEquals(a.getFragment(), new ImageFragment());
		assertTrue(a.getFragment().getShape() instanceof VoidShape);
		assertEquals(a.getFragment().getShape(), new VoidShape());	
	}
}
