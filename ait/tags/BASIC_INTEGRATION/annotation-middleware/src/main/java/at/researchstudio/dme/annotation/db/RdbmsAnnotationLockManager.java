package at.researchstudio.dme.annotation.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationLockedException;

/**
 * Implementation of the AnnotationDatabaseLockManager for RDBMS.
 * 
 * @author Christian Sadilek
 *
 */
public class RdbmsAnnotationLockManager implements AnnotationDatabaseLockManager {
	private static Logger logger = Logger.getLogger(RdbmsAnnotationLockManager.class);
	
	private static final long RETRY_INTERVAL = 250l;
	
	private static final String SQL_TEST_TABLE_EXISTS = 
    	"SELECT * FROM TB_ANNOTATION_LOCKS WHERE id = ''";
	private static final String SQL_CREATE_ANNOTATION_LOCK_TABLE = 
    	"CREATE TABLE TB_ANNOTATION_LOCKS ( id VARCHAR(128) UNIQUE)";
	private static final String SQL_ACQUIRE_LOCK = 
    	"INSERT INTO TB_ANNOTATION_LOCKS VALUES ( ? )";
	private static final String SQL_RELEASE_LOCK = 
    	"DELETE FROM TB_ANNOTATION_LOCKS WHERE id = ? ";
	
    private static volatile boolean initialized = false;
    
    private Connection connection = null;
    
	public RdbmsAnnotationLockManager() {}

	public RdbmsAnnotationLockManager(Connection connection) throws AnnotationDatabaseException {
		this.connection = connection;
		init();
	}
	
	@Override
	public void setConnection(Object connection) throws AnnotationDatabaseException {
		if(!(connection instanceof Connection))
			throw new AnnotationDatabaseException("connection not an instance of java.sql.Connect");
		this.connection = (Connection) connection;
		init();
	}
	
	/**
	 * Creates the lock table if it doesn't exist already. We use double checked
	 * locking to avoid multiple initializations without harming performance.
	 * 
	 * @throws AnnotationDatabaseException
	 */
	private void init() throws AnnotationDatabaseException {
		if(!initialized) {
			synchronized(this.getClass()) {			
				if(!initialized) {
					try {
						Statement st = connection.createStatement();						
						try {
							st.execute(SQL_TEST_TABLE_EXISTS);
						} catch(SQLException sqle) {						
							st.execute(SQL_CREATE_ANNOTATION_LOCK_TABLE);
						} finally {
							st.close();
						}												
					} catch(Throwable t) {
						logger.fatal("failed to initialize", t);
						throw new AnnotationDatabaseException(t);
					} 
					initialized=true;
				}					
			}			
		}
	}
	
	@Override
	public String acquireLock(String annotationId, long timeout) throws AnnotationDatabaseException, 
			AnnotationLockedException {
		try {
			boolean lockAcquired = false;
			while(!(lockAcquired=tryAcquireLock(annotationId))&&((timeout-RETRY_INTERVAL)>=0)) {
				Thread.sleep(RETRY_INTERVAL);
				timeout=timeout-RETRY_INTERVAL;
			}
			if(!lockAcquired) throw new AnnotationLockedException(annotationId); 			
			return annotationId;
		} catch (AnnotationLockedException ale) { 
			throw ale;
		} catch(Exception e) {
			logger.fatal("failed to acquire lock", e);
			throw new AnnotationDatabaseException(e);
		}				
	}

	@Override
	public boolean tryAcquireLock(String annotationId) {
		try {
			executeStatement(SQL_ACQUIRE_LOCK, annotationId);
		} catch(Exception ade) {			
			return false;
		}
		return true;
	}
	
	@Override
	public void releaseLock(String annotationId) throws AnnotationDatabaseException {
		try {
			executeStatement(SQL_RELEASE_LOCK, annotationId);
		} catch(Exception e) {	
			logger.fatal("failed to release lock", e);
			throw new AnnotationDatabaseException(e);
		}	
	}
	
	@Override
	public void closeConnection() throws AnnotationDatabaseException {
		try {
			if(connection!=null)
				connection.close();
		} catch(SQLException sqle) {
			logger.error("failed to close connection", sqle);
			throw new AnnotationDatabaseException(sqle);
		}
	}
	
	private void executeStatement(String statement, String annotationId) 
			throws AnnotationDatabaseException, AnnotationLockedException{

		if(connection==null) throw new AnnotationDatabaseException("no connection provided");
		
		PreparedStatement pstatement = null;
		try {			
			pstatement=connection.prepareStatement(statement);			
			pstatement.setString(1, annotationId);
			pstatement.execute();
		} catch (SQLException sqle) {			
			throw new AnnotationDatabaseException(sqle);
		} finally {
			if(pstatement!=null) {
				try {
					pstatement.close();
				} catch(SQLException sqle) {
					throw new AnnotationDatabaseException(sqle);
				}
			}
		}
	}
}
