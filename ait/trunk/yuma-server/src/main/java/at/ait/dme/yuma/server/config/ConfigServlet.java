package at.ait.dme.yuma.server.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

/**
 * Configuration servlet.
 * 
 * @author Christian Sadilek
 */
public class ConfigServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(ConfigServlet.class);

	private static final long serialVersionUID = -4041232932917515128L;

	@Override
	public void init(ServletConfig config) throws ServletException {		
		ServletContext app = config.getServletContext();	    			    
		String dbImpl = app.getInitParameter("annotation.db.impl");
		String dbDriver = app.getInitParameter("annotation.db.driver");
		String dbDriverProtocol = app.getInitParameter("annotation.db.driver.protocol");		
		String dbHost = app.getInitParameter("annotation.db.host"); 
		String dbPort = app.getInitParameter("annotation.db.port"); 
		String dbName = app.getInitParameter("annotation.db.name");
	    String dbUser = app.getInitParameter("annotation.db.user"); 
	    String dbPass = app.getInitParameter("annotation.db.pass");
	    String dbDir = app.getInitParameter("annotation.db.dir"); 
	    String dbFlags = app.getInitParameter("annotation.db.flags");
	    String serverBaseUrl = app.getInitParameter("server.base.url");
	    String suiteBaseUrl = app.getInitParameter("suite.base.url");
	    String adminUsername = app.getInitParameter("admin.username");
	    String adminPassword = app.getInitParameter("admin.password");
	   
	    new Config.Builder(dbImpl, serverBaseUrl, suiteBaseUrl, adminUsername, adminPassword).
	    	dbDriver(dbDriver).dbDriverProtocol(dbDriverProtocol).dbHost(dbHost).
	    	dbPort(dbPort).dbName(dbName).dbUser(dbUser).dbUser(dbUser).
	    	dbPass(dbPass).dbDir(dbDir).dbFlags(dbFlags).
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

}
