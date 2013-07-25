package at.researchstudio.dme.annotation.db.hibernate.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.researchstudio.dme.annotation.Data;
import at.researchstudio.dme.annotation.Setup;
import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.db.hibernate.HibernateAnnotationDatabase;
import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
 * tests the hibernate annotation database
 * 
 * @author Christian Sadilek
 */
public class HibernateAnnotationDatabaseTest {	
	
	@BeforeClass
	public static void setUp() throws Exception {				
		Setup.buildHibernateConfiguration();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Config.getInstance().getAnnotationDatabase().shutdown();
	}
	
	@Test
	public void testCreateUpdateDeleteAnnotation() throws Exception {			
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();
		
		try {
			had.connect();
			
			URI id = had.createAnnotation(Data.LEMO_ANNOTATION);						
			String annotation=had.findAnnotationById(id.toString());		
			assertNotNull(annotation);
			assertFalse(annotation.isEmpty());
			assertTrue(annotation.contains(id.toString()));
			
			URI updId=had.updateAnnotation(id.toString(), annotation);
			assertFalse(id.equals(updId));
			try {
				annotation=had.findAnnotationById(id.toString());
				fail("AnnotationNotFoundException expected");
			} catch(AnnotationNotFoundException e) {/*expected*/}			
			annotation=had.findAnnotationById(updId.toString());		
			assertNotNull(annotation);
			assertFalse(annotation.isEmpty());
			
			had.deleteAnnotation(updId.toString());
			try {
				annotation=had.findAnnotationById(updId.toString());
				fail("AnnotationNotFoundException expected");
			} catch(AnnotationNotFoundException e) {/*expected*/}
		} finally {
			had.disconnect();	
		}			
	}
	
	@Test
	public void testListAnnotationReplies() throws Exception {
		URI rootId = null, replyId = null, replyreplyId = null;
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();
	
		try {
			had.connect();
			
			rootId = had.createAnnotation(Data.LEMO_ANNOTATION);
			replyId= had.createAnnotation(
					Data.LEMO_ANNOTATION_REPLY.replace("$ID", rootId.toString()));
			replyreplyId=had.createAnnotation(
					Data.LEMO_ANNOTATION_REPLY_REPLY.replace("$ROOTID", rootId.toString()).
						replace("$ID", replyId.toString()));
			String annotation=had.findAnnotationById(rootId.toString());		
			assertNotNull(annotation);
			assertFalse(annotation.isEmpty());
			assertTrue(annotation.contains(rootId.toString()));
			
			String allReplies=had.listAnnotationReplies(rootId.toString());
			assertTrue(allReplies.contains(replyId.toString()));
			assertTrue(allReplies.contains(replyreplyId.toString()));		
		} finally {		
			if(replyreplyId!=null) had.deleteAnnotation(replyreplyId.toString());
			if(replyId!=null) had.deleteAnnotation(replyId.toString());
			if(rootId!=null) had.deleteAnnotation(rootId.toString());
			had.disconnect();
		}				
	}
	
	@Test
	public void testCreateReplyForModifiedAnnotation() throws Exception {
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();	
		URI uriUpdate = null;
		try {
			had.connect();
			
			URI uri=had.createAnnotation(Data.LEMO_ANNOTATION);
			uriUpdate=had.updateAnnotation(uri.toString(), Data.LEMO_ANNOTATION);
			String reply=Data.LEMO_ANNOTATION_REPLY.replace("$ID", uri.toString());					
			try {
				had.setAutoCommit(false);
				had.createAnnotation(reply);
				fail("AnnotationModifiedException expected");					
			} catch(AnnotationModifiedException ame) {
				//expected
			}	
		} finally {
			had.setAutoCommit(true);
			if(uriUpdate!=null)
				had.deleteAnnotation(uriUpdate.toString());
			had.disconnect();
		}
	}

	@Test
	public void testUpdateAndDeleteOfReferencedAnnotation() throws Exception {
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();			
		URI uri = null, uriReply = null;
		try {
			had.connect();
			
			uri=had.createAnnotation(Data.LEMO_ANNOTATION);
			String reply=Data.LEMO_ANNOTATION_REPLY.replace("$ID", uri.toString());					
			uriReply=had.createAnnotation(reply);
			
			try {
				had.updateAnnotation(uri.toString(), Data.LEMO_ANNOTATION);				
				fail("AnnotationAlreadyReferencedException expected");					
			} catch(AnnotationAlreadyReferencedException ame) {
				//expected
			}
			try {
				had.deleteAnnotation(uri.toString());				
				fail("AnnotationAlreadyReferencedException expected");					
			} catch(AnnotationAlreadyReferencedException ame) {
				//expected
			}	
		} finally {
			if(uriReply!=null)
				had.deleteAnnotation(uriReply.toString());					
			if(uri!=null)
				had.deleteAnnotation(uri.toString());
			had.disconnect();
		}
	}
	
	@Test
	public void testListAnnotations() throws Exception {
		URI id = null;
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();		
		
		try {
			had.connect();
			
			id=had.createAnnotation(Data.LEMO_ANNOTATION);		
			String annotation = had.listAnnotations("http://upload.wikimedia.org/" +
					"wikipedia/commons/7/77/Lissabon.jpg");		
			assertNotNull(annotation);
			assertFalse(annotation.isEmpty());
			assertTrue(annotation.contains(id.toString()));
		} finally {
			had.deleteAnnotation(id.toString());
			had.disconnect();
		}					
	}
	
	@Test
	public void testCountAnnotations() throws Exception {
		URI id = null;
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();		

		try {
			had.connect();
			id=had.createAnnotation(Data.LEMO_ANNOTATION);		
			int n = had.countAnnotations("http://upload.wikimedia.org/" +
					"wikipedia/commons/7/77/Lissabon.jpg");
			assertTrue(n >= 1);
		} finally {
			had.deleteAnnotation(id.toString());
			had.disconnect();	
		}		
	}
	
	@Test
	public void testListLinkedAnnotations() throws Exception {
		URI id = null;
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();		
		
		try {
			had.connect();
			
			id=had.createAnnotation(Data.LEMO_ANNOTATION);		
			String annotation = had.listLinkedAnnotations("someexternalobject");		
			assertNotNull(annotation);
			assertFalse(annotation.isEmpty());
			assertTrue(annotation.contains(id.toString()));
		} finally {			
			had.deleteAnnotation(id.toString());
			had.disconnect();
		}					
	}
	
	@Test
	public void testCountLinkedAnnotations() throws Exception {
		URI id = null;
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();
		
		try {
			had.connect();

			id=had.createAnnotation(Data.LEMO_ANNOTATION);		
			int n = had.countLinkedAnnotations("someexternalobject");
			assertTrue(n >= 1);
		} finally {			
			had.deleteAnnotation(id.toString());
			had.disconnect();
		}			
	}
	
	@Test
	public void testFindAnnotationBodyById() throws Exception {
		URI id = null;
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();
	
		try {
			had.connect();

			id=had.createAnnotation(Data.LEMO_ANNOTATION);		
			String body = had.findAnnotationBodyById(id.toString());
			assertNotNull(body);
			assertFalse(body.isEmpty());			
		} finally {
			had.deleteAnnotation(id.toString());
			had.disconnect();
		}
	}
	
	@Test
	public void testFindAnnotations() throws Exception {
		URI id = null;
		HibernateAnnotationDatabase had = new HibernateAnnotationDatabase();
		
		try {
			had.connect();

			id=had.createAnnotation(Data.LEMO_ANNOTATION);		
			// in title
			String annotations = had.findAnnotations("ponte");
			assertNotNull(annotations);
			assertFalse(annotations.isEmpty());		
			assertTrue(annotations.contains(id.toString()));
			// in text
			annotations = had.findAnnotations("lisbon");
			assertNotNull(annotations);
			assertFalse(annotations.isEmpty());		
			assertTrue(annotations.contains(id.toString()));		
		} finally {
			had.deleteAnnotation(id.toString());
			had.disconnect();	
		}			
	}
}
