package at.ait.dme.yuma.server.db.mongodb;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.exception.AnnotationHasReplyException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.model.Annotation;

public class MongoAnnotationDBTest_dontuse {
	
	@BeforeClass
	public static void setUp() throws Exception {		
		Setup.buildConfiguration();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Config.getInstance().getAnnotationDatabase().shutdown();
	}
	
	@Test
	public void testMongoDBCRUD() throws Exception {
		JSONFormatHandler format = new JSONFormatHandler();
		
		MongoAnnotationDB db = new MongoAnnotationDB();
		db.connect();
		
		// Create + Read
		Annotation before = format.parse(Data.ANNOTATION_JSON_ORIGINAL);
		String id = db.createAnnotation(before);
		Annotation after = db.findAnnotationById(id);
		assertEquals(before, after);
		assertEquals(id, after.getAnnotationID());
		
		// Update
		Annotation beforeUpdate = format.parse(Data.ANNOTATION_JSON_UPDATE);
		id = db.updateAnnotation(id, beforeUpdate);
		Annotation afterUpdate = db.findAnnotationById(id);
		assertFalse(afterUpdate.equals(after));
		
		// Delete
		db.deleteAnnotation(id);
		try {
			db.findAnnotationById(id);
			fail("Annotation was not deleted");
		} catch (AnnotationNotFoundException e) {
			// Expected exception
		}	
	}
	
	@Test
	public void testReplies() throws Exception {
		JSONFormatHandler format = new JSONFormatHandler();
		MongoAnnotationDB db = new MongoAnnotationDB();
		db.connect();
		
		// Store annotation
		Annotation root = format.parse(Data.ANNOTATION_JSON_ORIGINAL);
		String parentId = db.createAnnotation(root);
		
		// Store reply
		Annotation reply = format.parse(Data.reply(parentId, parentId));
		String replyId = db.createAnnotation(reply);
		
		// Some checks on stored annotations
		assertEquals(2, db.countAnnotationsForObject(root.getObjectUri()));
		assertEquals(1, db.countReplies(parentId));
		
		// Try delete root
		try {
			db.deleteAnnotation(parentId);
			fail("Annotation that has replies must not be deletable!");
		} catch (AnnotationHasReplyException e) {
			// Expected
		}
		
		// Delete reply, then root
		db.deleteAnnotation(replyId);
		db.deleteAnnotation(parentId);
	}
	
}
