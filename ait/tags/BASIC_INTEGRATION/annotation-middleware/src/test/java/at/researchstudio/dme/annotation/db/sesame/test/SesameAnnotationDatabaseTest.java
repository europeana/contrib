package at.researchstudio.dme.annotation.db.sesame.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import at.researchstudio.dme.annotation.Setup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.researchstudio.dme.annotation.Data;
import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.db.sesame.SesameAnnotationDatabase;
import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
* Tests for the Sesame annotation database
*  
* @author Christian Sadilek
*/
public class SesameAnnotationDatabaseTest {

    @BeforeClass
	public static void setUp() throws Exception {
        Setup.buildSesameNativeConfiguration();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		Config.getInstance().getAnnotationDatabase().shutdown();
	}
	
	@Test
	public void testCreateReplyForModifiedAnnotation() throws Exception {
		SesameAnnotationDatabase sad = new SesameAnnotationDatabase();		
		URI uriUpdate = null;
		try {
			sad.connect();
			
			URI uri=sad.createAnnotation(Data.LEMO_ANNOTATION);
			uriUpdate=sad.updateAnnotation(uri.toString(), Data.LEMO_ANNOTATION);
			String reply=Data.LEMO_ANNOTATION_REPLY.replace("$ID", uri.toString());					
			try {
				sad.setAutoCommit(false);
				sad.createAnnotation(reply);
				fail("AnnotationModifiedException expected");					
			} catch(AnnotationModifiedException ame) {
				//expected
			}	
		} finally {
			sad.setAutoCommit(true);
			if(uriUpdate!=null)
				sad.deleteAnnotation(uriUpdate.toString());
			sad.disconnect();
		}
	}

	@Test
	public void testUpdateAndDeleteOfReferencedAnnotation() throws Exception {
		SesameAnnotationDatabase sad = new SesameAnnotationDatabase();		
		URI uri = null, uriReply = null;
		try {
			sad.connect();
			
			uri=sad.createAnnotation(Data.LEMO_ANNOTATION);
			String reply=Data.LEMO_ANNOTATION_REPLY.replace("$ID", uri.toString());					
			uriReply=sad.createAnnotation(reply);
			
			try {
				sad.updateAnnotation(uri.toString(), Data.LEMO_ANNOTATION);				
				fail("AnnotationAlreadyReferencedException expected");					
			} catch(AnnotationAlreadyReferencedException ame) {
				//expected
			}
			try {
				sad.deleteAnnotation(uri.toString());				
				fail("AnnotationAlreadyReferencedException expected");					
			} catch(AnnotationAlreadyReferencedException ame) {
				//expected
			}	
		} finally {
			if(uriReply!=null)
				sad.deleteAnnotation(uriReply.toString());					
			if(uri!=null)
				sad.deleteAnnotation(uri.toString());
			sad.disconnect();
		}
	}

	@Test
	public void testExecuteQuery() throws Exception {
		URI uri = null;
		SesameAnnotationDatabase sad = new SesameAnnotationDatabase();		
		try {
			sad.connect();
			uri=sad.createAnnotation(Data.LEMO_ANNOTATION);
			String results = sad.findAnnotations("anything");
			assertFalse(results.contains(uri.toString()));
			results = sad.findAnnotations("lisbon");
			assertTrue(results.contains(uri.toString()));			
		} finally {
			if(uri!=null)
				sad.deleteAnnotation(uri.toString());
			sad.disconnect();
		}
	}
	
	@Test
	public void testFindAnnotationById() throws Exception {
		SesameAnnotationDatabase sad = new SesameAnnotationDatabase();		
		try {
			sad.connect();
			sad.findAnnotationById("anything");
			fail("AnnotationNotFoundException expected");
		} catch(AnnotationNotFoundException anfe) {
			//expected
		} finally {
			sad.disconnect();
		}
	}
}
