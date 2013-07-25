package at.ait.dme.yuma.server.config;

import org.apache.log4j.Logger;

import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;

/**
 * Configuration settings for the annotation server.
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
	private final String serverBaseUrl;
	private final String suiteBaseUrl;
	private final String adminUsername;
	private final String adminPassword;

	private Config(Builder builder) {
		this.dbHost = builder.dbHost;
		this.dbPort = builder.dbPort;
		this.dbName = builder.dbName;
		this.dbUser = builder.dbUser;
		this.dbPass = builder.dbPass;
		this.dbDir = builder.dbDir;
		this.dbFlags = builder.dbFlags;		
		this.serverBaseUrl = builder.serverBaseUrl;
		this.suiteBaseUrl = builder.suiteBaseUrl;
		this.adminUsername = builder.adminUsername;
		this.adminPassword = builder.adminPassword;
		this.dbImpl = builder.dbImpl;
		this.dbDriver = builder.dbDriver;
		this.dbDriverProtocol = builder.dbDriverProtocol;
	}
	
	public static class Builder {
		private String dbImpl = null;
		private String dbDriver = null;
		private String dbDriverProtocol = null;		
		private String serverBaseUrl = null;
		private String suiteBaseUrl = null;
		
		private String adminUsername = null;
		private String adminPassword = null;
		
		private String dbHost = null;
		private String dbPort = null;
		private String dbName = null;
		private String dbUser = null;
		private String dbPass = null;
		private String dbDir = null;
		private String dbFlags = null;	
		
		public Builder(String dbImpl, String serverBaseUrl, String suiteBaseUrl, String adminUsername, String adminPassword) {
			this.dbImpl = dbImpl;			
			this.serverBaseUrl = serverBaseUrl;
			this.suiteBaseUrl = suiteBaseUrl;
			this.adminUsername = adminUsername;
			this.adminPassword = adminPassword;
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
	
	public String getServerBaseUrl() {
		return serverBaseUrl;
	}
	
	public String getSuiteBaseUrl() {
		return suiteBaseUrl;
	}
	
	public String getAdminUsername() {
		return adminUsername;
	}
	
	public String getAdminPassword() {
		return adminPassword;
	}
	
	public AbstractAnnotationDB getAnnotationDatabase() throws AnnotationDatabaseException {
		AbstractAnnotationDB annotationDb = null;
		try {
			Class<?> annotationDbImplClass = Class.forName(dbImpl);
			Object obj = annotationDbImplClass.newInstance();
			if (obj instanceof AbstractAnnotationDB) {
				annotationDb = (AbstractAnnotationDB) obj;
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
