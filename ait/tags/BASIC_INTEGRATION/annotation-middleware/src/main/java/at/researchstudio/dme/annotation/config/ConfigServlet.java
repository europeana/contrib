package at.researchstudio.dme.annotation.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

public class ConfigServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(ConfigServlet.class);

	private static final long serialVersionUID = -4041232932917515128L;

	@Override
	public void init(ServletConfig config) throws ServletException {		
		ServletContext application = config.getServletContext();	    			    
		String dbImpl=readProperty("annotation.db.impl", application);
		String dbDriver=readProperty("annotation.db.driver", application);
		String dbDriverProtocol=readProperty("annotation.db.driver.protocol", application);		
		String dbHost = readProperty("annotation.db.host", application); 
		String dbPort=readProperty("annotation.db.port", application); 
		String dbName=readProperty("annotation.db.name", application);
	    String dbUser=readProperty("annotation.db.user", application); 
	    String dbPass=readProperty("annotation.db.pass", application);
	    String dbDir=readProperty("annotation.db.dir", application); 
	    String dbFlags=readProperty("annotation.db.flags", application);
	    String annotationBaseUrl=readProperty("annotation.base.url", application);
	    String annotationBodyBaseUrl=readProperty("annotation.body.base.url", application);	   
	    String lockManagerImpl=readProperty("annotation.db.lock.manager", application);
	   
	    new Config.Builder(dbImpl, annotationBaseUrl,annotationBodyBaseUrl).
	    	dbDriver(dbDriver).dbDriverProtocol(dbDriverProtocol).dbHost(dbHost).
	    	dbPort(dbPort).dbName(dbName).dbUser(dbUser).dbUser(dbUser).
	    	dbPass(dbPass).dbDir(dbDir).dbFlags(dbFlags).lockManager(lockManagerImpl).
	    	createInstance();	    
	    try {
	    	Config.getInstance().getAnnotationDatabase().init();
	    } catch(Throwable t) {
	    	logger.fatal(t.getMessage(), t);
	    	throw new ServletException("failed to initialize annotation database");
	    }
	}
	
	
	@Override
	public void destroy() {
	    try {
	    	Config.getInstance().getAnnotationDatabase().shutdown();
	    } catch(Throwable t) {
	    	logger.fatal("failed to shutdown annotation database", t);
	    }
	}


	private String readProperty(String propertyName, ServletContext application) throws ServletException {			    		    
		String propertyValue = application.getInitParameter(propertyName);
		if (propertyValue == null) {
			logger.error("context parameter " + propertyName + " not set!");
			throw new ServletException("missing context parameter");
		}
		return propertyValue;
	}
}
