package pt.utl.ist.repox.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

public class PropertyUtil {
	public static Properties loadCorrectedConfiguration(String configurationFilename) {
        try {
            URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(configurationFilename);
            String configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");

            Properties properties = new Properties();
            properties.load(new FileInputStream(configurationFile));

            return properties;
        }
        catch (Exception e){
            throw new IllegalArgumentException("could not load [" + configurationFilename + "]");
        }
    }

    public static void saveProperties(Properties properties, String name){
        try {
            URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(name);
            String propertiesPath = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");
            properties.store(new FileOutputStream(propertiesPath), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
