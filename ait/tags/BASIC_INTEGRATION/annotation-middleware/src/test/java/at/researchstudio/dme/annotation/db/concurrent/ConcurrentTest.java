package at.researchstudio.dme.annotation.db.concurrent;

import static org.junit.Assert.fail;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import at.researchstudio.dme.annotation.Setup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.researchstudio.dme.annotation.Data;
import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.db.sesame.SesameAnnotationDatabase;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
* Test for concurrent access to the annotation database
*  
* @author Christian Sadilek
*/
public class ConcurrentTest {
	private static final int THREADS = 25;

	@BeforeClass
	public static void setUp() throws Exception {
        Setup.buildSesameNativeConfiguration();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Config.getInstance().getAnnotationDatabase().shutdown();
	}
	
	@Test
	public void testConcurrentCrud() throws Exception {
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(THREADS);			
		
		for (int i = 0; i < THREADS; i++) {
			final int index = i;
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						startGate.await();						
						SesameAnnotationDatabase sad = new SesameAnnotationDatabase();						
						try {
							long start = System.currentTimeMillis();
							sad.connect();						
							URI uri=sad.createAnnotation(Data.LEMO_ANNOTATION);
							sad.disconnect();
							System.out.println("CREATE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms" + " ID:"+uri.toString());														

							start = System.currentTimeMillis();
							sad.connect();																											
							uri=sad.updateAnnotation(uri.toString(),Data.LEMO_ANNOTATION);
							sad.disconnect();
							System.out.println("UPDATE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms");
					
							start = System.currentTimeMillis();																																			
							sad.connect();
							sad.deleteAnnotation(uri.toString());							
							sad.disconnect();
							System.out.println("DELETE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms");
						} finally {
							endGate.countDown();
							sad.disconnect();
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			};
			thread.start();
		}
		long start = System.currentTimeMillis();
		startGate.countDown();
		endGate.await();
		System.out.println("TOTAL TIME:"+(System.currentTimeMillis()- start)+" ms");
	}
	
	@Test
	public void testReadCommited() throws Exception {
		SesameAnnotationDatabase sad1 = new SesameAnnotationDatabase();
		SesameAnnotationDatabase sad2 = new SesameAnnotationDatabase();
		
		sad1.setAutoCommit(false);
		URI id = null;
		try {			
			sad1.connect();
			id = sad1.createAnnotation(Data.LEMO_ANNOTATION);
			sad2.connect();
			try {
				sad2.findAnnotationById(id.toString());
				fail("AnnotationNotFoundException expected");
			} catch(AnnotationNotFoundException anfe) {
				/*expected */
			}
			sad1.commit();
			sad2.findAnnotationById(id.toString());			
		} finally {
			if(id!=null) {
				sad1.deleteAnnotation(id.toString());
				sad1.commit();
			}
			sad1.disconnect();
			sad2.disconnect();			
		}
		
	}
}
