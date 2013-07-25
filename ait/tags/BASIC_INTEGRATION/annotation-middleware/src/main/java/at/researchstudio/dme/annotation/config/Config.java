package at.researchstudio.dme.annotation.config;

import org.apache.log4j.Logger;

import at.researchstudio.dme.annotation.db.AnnotationDatabase;
import at.researchstudio.dme.annotation.db.AnnotationDatabaseLockManager;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;

/**
 * Configuration settings for the annotation middleware
 * 
 * @author Christian Sadilek
 */
public class Config {
	private static Logger logger = Logger.getLogger(Config.class);

	private static Config singletonInstance = null;

	private final String dbImpl;
	private final String dbDriver;
	private final String dbDriverProtocol;	
	private final String dbHost;
	private final String dbPort;
	private final String dbName;
	private final String dbUser;
	private final String dbPass;
	private final String dbDir;
	private final String dbFlags;	
	private final String annotationBaseUrl;
	private final String annotationBodyBaseUrl;
	private final String lockManagerImpl;

	private Config(Builder builder) {
		this.dbHost = builder.dbHost;
		this.dbPort = builder.dbPort;
		this.dbName = builder.dbName;
		this.dbUser = builder.dbUser;
		this.dbPass = builder.dbPass;
		this.dbDir = builder.dbDir;
		this.dbFlags = builder.dbFlags;		
		this.annotationBaseUrl = builder.annotationBaseUrl;
		this.annotationBodyBaseUrl = builder.annotationBodyBaseUrl;
		this.dbImpl = builder.dbImpl;
		this.dbDriver = builder.dbDriver;
		this.dbDriverProtocol = builder.dbDriverProtocol;
		this.lockManagerImpl = builder.lockManagerImpl;
	}
	
	public static class Builder {
		private String lockManagerImpl = null;		
		private String dbImpl = null;
		private String dbDriver = null;
		private String dbDriverProtocol = null;		
		private String annotationBaseUrl = null;
		private String annotationBodyBaseUrl = null;
		
		private String dbHost = null;
		private String dbPort = null;
		private String dbName = null;
		private String dbUser = null;
		private String dbPass = null;
		private String dbDir = null;
		private String dbFlags = null;	
		
		public Builder(String dbImpl, String annotationBaseUrl, 
				String annotationBodyBaseUrl) {
			this.dbImpl = dbImpl;			
			this.annotationBaseUrl = annotationBaseUrl;
			this.annotationBodyBaseUrl = annotationBodyBaseUrl;
		}
		
		public Builder dbDriver(String val) {
			dbDriver = val;
			return this;
		}
		
		public Builder dbDriverProtocol(String val) {
			dbDriverProtocol = val;
			return this;
		}
		
		public Builder dbHost(String val) {
			dbHost = val;
			return this;
		}
		
		public Builder dbPort(String val) {
			dbPort = val;
			return this;
		}
		
		public Builder dbName(String val) {
			dbName = val;
			return this;
		}
		
		public Builder dbUser(String val) {
			dbUser = val;
			return this;
		}
		
		public Builder dbPass(String val) {
			dbPass = val;
			return this;
		}
		
		public Builder dbDir(String val) {
			dbDir = val;
			return this;
		}
		
		public Builder dbFlags(String val) {
			dbFlags = val;
			return this;
		}
		
		public Builder lockManager(String val) {
			lockManagerImpl = val;
			return this;
		}
		
		public Config createInstance() {
			synchronized(this.getClass()) {
				if (singletonInstance == null) {
					singletonInstance = new Config(this);
				} else {
					throw new IllegalStateException("configuration already initialized");
				}
				return singletonInstance;
			}
		}
	}

	public static Config getInstance() {
		if (singletonInstance == null)
			throw new IllegalStateException("configuration has not been initialized");

		return singletonInstance;
	}

	public String getDbHost() {
		return dbHost;
	}

	public String getDbPort() {
		return dbPort;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public String getDbDir() {
		return dbDir;
	}

	public String getDbFlags() {
		return dbFlags;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public String getDbDriverProtocol() {
		return dbDriverProtocol;
	}
	
	public String getAnnotationBaseUrl() {
		return annotationBaseUrl;
	}

	public String getAnnotationBodyBaseUrl() {
		return annotationBodyBaseUrl;
	}	

	public AnnotationDatabaseLockManager getLockManager() throws AnnotationDatabaseException {
		AnnotationDatabaseLockManager lockManager = null;
		try {
			if(lockManagerImpl.isEmpty()) return lockManager;
			Class annotationDbLockImplClass = Class.forName(lockManagerImpl);
			Object obj = annotationDbLockImplClass.newInstance();
			if (obj instanceof AnnotationDatabaseLockManager) {
				lockManager = (AnnotationDatabaseLockManager) obj;
			} else {
				logger.fatal("annotation lockmanager implementation class is invalid. "
						+ "check inheritance");
				throw new InstantiationException();
			}
		} catch (Exception e) {
			logger.fatal("failed to load lock manager implementation", e);
			throw new AnnotationDatabaseException(e);
		}
		return lockManager;
	}
	
	public AnnotationDatabase getAnnotationDatabase() throws AnnotationDatabaseException {
		AnnotationDatabase annotationDb = null;
		try {
			Class annotationDbImplClass = Class.forName(dbImpl);
			Object obj = annotationDbImplClass.newInstance();
			if (obj instanceof AnnotationDatabase) {
				annotationDb = (AnnotationDatabase) obj;
			} else {
				logger.fatal("annotation database implementation class is invalid. "
						+ "check inheritance");
				throw new InstantiationException();
			}
		} catch (Exception e) {
			logger.fatal("failed to load annotation database implementation", e);
			throw new AnnotationDatabaseException(e);
		}
		return annotationDb;
	}
}
