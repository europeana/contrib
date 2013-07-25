package at.ait.dme.yuma.server.db.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.model.Annotation;

/**
* Test for concurrent access to the annotation database
*  
* @author Christian Sadilek
*/
public class ConcurrentTest {
	private static final int THREADS = 25;

	@BeforeClass
	public static void setUp() throws Exception {
		Setup.buildConfiguration();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Config.getInstance().getAnnotationDatabase().shutdown();
	}
	
	@Test
	public void testConcurrentCrud() throws Exception {
		final JSONFormatHandler format = new JSONFormatHandler();
		
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(THREADS);			
		
		for (int i = 0; i < THREADS; i++) {
			final int index = i;
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						startGate.await();						
						AbstractAnnotationDB db = new HibernateAnnotationDB();						
						try {
							long start = System.currentTimeMillis();
							db.connect();						
							String id = db.createAnnotation(format.parse(Data.ANNOTATION_JSON_ORIGINAL));
							db.disconnect();
							System.out.println("CREATE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms" + " ID:"+id);														

							start = System.currentTimeMillis();
							db.connect();																											
							id = db.updateAnnotation(id, format.parse(Data.ANNOTATION_JSON_UPDATE));
							db.disconnect();
							System.out.println("UPDATE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms");
					
							start = System.currentTimeMillis();																																			
							db.connect();
							db.deleteAnnotation(id);							
							db.disconnect();
							System.out.println("DELETE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms");
						} finally {
							endGate.countDown();
							db.disconnect();
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
		final JSONFormatHandler format = new JSONFormatHandler();
		
		AbstractAnnotationDB db1 = new HibernateAnnotationDB();
		AbstractAnnotationDB db2 = new HibernateAnnotationDB();
		
		db1.setAutoCommit(false);
		String id = null;
		try {
			db1.connect();
			id = db1.createAnnotation(format.parse(Data.ANNOTATION_JSON_ORIGINAL));
			
			db2.connect();
			
			try {
				Annotation foundAnnotation = db2.findAnnotationById(id.toString());

				assertEquals(id, foundAnnotation.getAnnotationID());
			} catch(AnnotationNotFoundException e) {
				fail("created annotation not found");
			}
			db1.commit();
			db2.findAnnotationById(id.toString());			
		} finally {
			if(id!=null) {
				db1.deleteAnnotation(id.toString());
				db1.commit();
			}
			db1.disconnect();
			db2.disconnect();			
		}
		
	}
}
