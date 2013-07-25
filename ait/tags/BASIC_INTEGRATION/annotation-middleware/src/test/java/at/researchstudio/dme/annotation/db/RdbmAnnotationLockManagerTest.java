package at.researchstudio.dme.annotation.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.researchstudio.dme.annotation.Setup;
import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.exception.AnnotationLockedException;

/**
 * Tests for the RdbmAnnotationLockManager
 *  
 * @author Christian Sadilek
 */
public class RdbmAnnotationLockManagerTest {
	private static final String LOCK_ANNOTATION_ID = "testid";
	private Collection<Connection> openConnections = new ArrayList<Connection>();
	
	@BeforeClass
	public static void setUp() throws Exception {
		   Setup.buildSesameRdbmsConfiguration();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		Config.getInstance().getAnnotationDatabase().shutdown();
	}
	
	@After
	public void cleanUp() throws Exception {
		AnnotationDatabaseLockManager lockMan = new RdbmsAnnotationLockManager(connect());
		lockMan.releaseLock(LOCK_ANNOTATION_ID);
		for (Connection c : openConnections) c.close();
	}
	
	@Test
	public void testInit() throws Exception {		
		AnnotationDatabaseLockManager lockMan = new RdbmsAnnotationLockManager(connect());
		lockMan = new RdbmsAnnotationLockManager(connect());		
	}
	
	
	@Test
	public void testAcquireReleaseLock() throws Exception {
		AnnotationDatabaseLockManager lockMan = new RdbmsAnnotationLockManager(connect());		
		try {
			lockMan.acquireLock(LOCK_ANNOTATION_ID,0);
			lockMan = new RdbmsAnnotationLockManager(connect());
			lockMan.acquireLock(LOCK_ANNOTATION_ID,0);
			fail("AnnotationLockedException expected");
		} catch(AnnotationLockedException ale) { /* expected */}
		
		lockMan = new RdbmsAnnotationLockManager(connect());
		lockMan.releaseLock(LOCK_ANNOTATION_ID);
		lockMan = new RdbmsAnnotationLockManager(connect());
		lockMan.acquireLock(LOCK_ANNOTATION_ID,0);
		lockMan = new RdbmsAnnotationLockManager(connect());
		lockMan.releaseLock(LOCK_ANNOTATION_ID);
		
		lockMan.acquireLock(LOCK_ANNOTATION_ID,0);
		lockMan = new RdbmsAnnotationLockManager(connect());
		final String lockAcquired = null;
		new Thread() {
			public void run() {
				try {
					AnnotationDatabaseLockManager lockMan = new RdbmsAnnotationLockManager(connect());
					lockMan.acquireLock(LOCK_ANNOTATION_ID,10000);
				} catch (Exception e) {/*ignored*/}
			}
		}.start();
		Thread.sleep(2000);
		lockMan.releaseLock(LOCK_ANNOTATION_ID);
		Thread.sleep(2000);
		// Thread should have taken the lock
		assertFalse(lockMan.tryAcquireLock(LOCK_ANNOTATION_ID));
		lockMan.releaseLock(LOCK_ANNOTATION_ID);		
	}
	
	@Test
	public void testTryAcquireReleaseLock() throws Exception {
		AnnotationDatabaseLockManager lockMan = new RdbmsAnnotationLockManager(connect());		
		assertTrue(lockMan.tryAcquireLock(LOCK_ANNOTATION_ID));		
		lockMan = new RdbmsAnnotationLockManager(connect());
		assertFalse(lockMan.tryAcquireLock(LOCK_ANNOTATION_ID));
		
		lockMan = new RdbmsAnnotationLockManager(connect());
		lockMan.releaseLock(LOCK_ANNOTATION_ID);
		lockMan = new RdbmsAnnotationLockManager(connect());
		assertTrue(lockMan.tryAcquireLock(LOCK_ANNOTATION_ID));
		lockMan = new RdbmsAnnotationLockManager(connect());
		lockMan.releaseLock(LOCK_ANNOTATION_ID);					
	}
	
	private Connection connect() throws Exception {
		Config config = Config.getInstance();
		Class.forName("org.postgresql.Driver");
	    Connection connection = DriverManager.getConnection(
				"jdbc:postgresql:" + config.getDbName(), config.getDbUser(), config.getDbPass());
	    openConnections.add(connection);
	    return connection;
	}
}
