package at.researchstudio.dme.annotation;

import java.util.Collections;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;

/**
 * The code in all of the test setup methods is the same
 *
 * @author Gerald de Jong <geralddejong@gmail.com>
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 */

public class Setup {

    private static String databaseDir() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac")) {
            return "/tmp/sesametest";
        }
        else if (osName.startsWith("Win")) {
            return "c:\\sesame\\test";
        }
        else {
            throw new RuntimeException("Unrecognized OS Name");
        }
    }

    public static void buildSesameNativeConfiguration() throws AnnotationDatabaseException {
        new Config.Builder(
                "at.researchstudio.dme.annotation.db.sesame.SesameAnnotationDatabase",
                "http://localhost:8081/annotation-middleware/Annotation/",
                "http://localhost:8081/annotation-middleware/Annotation/body"
        )
                .dbDriver("org.postgresql.Driver")
                .dbDriverProtocol("jdbc:postgresql")
                .dbHost("localhost")
                .dbPort("5432")
                .dbName("postgres")
                .dbUser("postgres")
                .dbPass("postgres")
                .dbFlags("native")
                .dbDir(databaseDir())
                .lockManager("at.researchstudio.dme.annotation.db.RdbmsAnnotationLockManager")
                .createInstance();
        Config.getInstance().getAnnotationDatabase().init();
    }

    public static void buildSesameRdbmsConfiguration() throws AnnotationDatabaseException {
        new Config.Builder(
                "at.researchstudio.dme.annotation.db.sesame.SesameAnnotationDatabase",
                "http://localhost:8081/annotation-middleware/Annotation/",
                "http://localhost:8081/annotation-middleware/Annotation/body"
        )
                .dbDriver("org.postgresql.Driver")
                .dbDriverProtocol("jdbc:postgresql")
                .dbHost("localhost")
                .dbPort("5432")
                .dbName("postgres")
                .dbUser("postgres")
                .dbPass("postgres")                
                .dbDir(databaseDir())
                .lockManager("at.researchstudio.dme.annotation.db.RdbmsAnnotationLockManager")
                .createInstance();
        Config.getInstance().getAnnotationDatabase().init();
    }
    
    public static void buildHibernateConfiguration() throws AnnotationDatabaseException {
    	new Config.Builder(
    			"at.researchstudio.dme.annotation.db.hibernate.HibernateAnnotationDatabase",				
				"http://localhost:8888/annotation-middleware/annotations/annotation/",
				"http://localhost:8888/annotation-middleware/annotations/annotation/body"
		)    	
    			.dbDriver("org.postgresql.Driver")
    			.dbDriverProtocol("jdbc:postgresql").
				dbHost("localhost")
				.dbPort("5432")
				.dbName("postgres")
				.dbUser("postgres")
				.dbPass("postgres").
				createInstance();
		
		Config.getInstance().getAnnotationDatabase().init();
    }
    
    public static void startEmbeddedJaxrsServer(Class <?> clazz) {
        TJWSEmbeddedJaxrsServer tjws = new TJWSEmbeddedJaxrsServer();
		tjws.setBindAddress("localhost");
		tjws.setRootResourcePath("/annotation-middleware");
		tjws.setPort(8081);
		tjws.getDeployment().setResourceClasses(Collections.singletonList(clazz.getName()));
		tjws.start();
    }
}
