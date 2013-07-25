package at.ait.dme.yuma.server.bootstrap;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;

/**
 * The code in all of the test setup methods is the same
 *
 * @author Gerald de Jong <geralddejong@gmail.com>
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 * @author Rainer Simon
 */
public class Setup {
	
	/**
	 * Properties file
	 */
	private static Properties props = null;
	
	/**
	 * Property keys
	 */
	private static String SERVER_URL = "test.server.url";
	private static String SUITE_URL = "test.suite.url";
	private static String ADMIN_USER = "test.admin.user";
	private static String ADMIN_PASSWORD = "test.admin.pass";
	
	private static String DB_CLASS = "test.db.class";
	private static String DB_DRIVER = "test.db.driver";
	private static String DB_DRIVER_PROTOCOL = "test.db.driver.protocol";
	private static String DB_HOST = "test.db.host";
	private static String DB_PORT = "test.db.port";
	private static String DB_NAME = "test.db.name";
	private static String DB_USER = "test.db.user";
	private static String DB_PASSWORD = "test.db.pass";
	
	static {
		try {
			props = new Properties();
			props.load(Setup.class.getResourceAsStream("/test.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
       
    public static void buildConfiguration() throws AnnotationDatabaseException {
    	new Config.Builder(
    			getProperty(DB_CLASS),
    			getProperty(SERVER_URL),
    			getProperty(SUITE_URL),
    			getProperty(ADMIN_USER),
				getProperty(ADMIN_PASSWORD)
		)    	
    			.dbDriver(getProperty(DB_DRIVER))
    			.dbDriverProtocol(getProperty(DB_DRIVER_PROTOCOL))
    			.dbHost(getProperty(DB_HOST))
				.dbPort(getProperty(DB_PORT))
				.dbName(getProperty(DB_NAME))
				.dbUser(getProperty(DB_USER))
				.dbPass(getProperty(DB_PASSWORD)).
				createInstance();
		
		Config.getInstance().getAnnotationDatabase().init();
    }
    
    public static void startEmbeddedJaxrsServer(Class <?> clazz) {
    	TJWSEmbeddedJaxrsServer tjws = new TJWSEmbeddedJaxrsServer();
		tjws.setBindAddress("localhost");
		tjws.setRootResourcePath("/yuma-server");
		tjws.setPort(8081);
		tjws.getDeployment().setResourceClasses(Collections.singletonList(clazz.getName()));
		tjws.start();
    }
    
    private static String getProperty(String key) {
    	return props.getProperty(key).trim();
    }
    
}
