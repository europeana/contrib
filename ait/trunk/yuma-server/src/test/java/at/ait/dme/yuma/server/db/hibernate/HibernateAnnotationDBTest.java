package at.ait.dme.yuma.server.db.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.exception.AnnotationHasReplyException;
import at.ait.dme.yuma.server.model.Annotation;

public class HibernateAnnotationDBTest {

	private static final String OBJ_URI = "http://dme.ait.ac.at/object/lissabon.jpg";
	
	@BeforeClass
	public static void setUp() throws Exception {		
		Setup.buildConfiguration();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Config.getInstance().getAnnotationDatabase().shutdown();
	}
	
	@Test
	public void testHibernateCRUD() throws Exception {
		JSONFormatHandler format = new JSONFormatHandler();
		
		HibernateAnnotationDB db = new HibernateAnnotationDB();
		db.connect();
		
		// Create
		Annotation before = format.parse(Data.ANNOTATION_JSON_ORIGINAL);
		
		String id = db.createAnnotation(before);
		System.out.println("Created: " + id);
		
		// Read
		Annotation annotation = db.findAnnotationById(id);
		System.out.println(format.serialize(annotation));
		
		// Update
		Annotation after = format.parse(Data.ANNOTATION_JSON_UPDATE);		
		id = db.updateAnnotation(id, after);
		System.out.println("Updated to: " + id);
		
		// Create reply
		Annotation reply = format.parse(Data.reply(id, id));
		String replyId = db.createAnnotation(reply);
		
		// Try delete root annotation
		try {
			db.deleteAnnotation(id);
			
			fail("Annotation has reply - delete should fail!");
		} catch (AnnotationHasReplyException e) {
			// Expected
		}
		
		long count = db.countAnnotationsForObject(OBJ_URI);
		
		// Delete
		db.deleteAnnotation(replyId);
		db.deleteAnnotation(id);
		
		assertEquals(count - 2, db.countAnnotationsForObject(OBJ_URI));
		
		// Search
		List<Annotation> annotations = db.findAnnotations("ponte");
		for (Annotation a : annotations) {
			System.out.println(format.serialize(a));
		}
	}
	
}
