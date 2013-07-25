package pt.utl.ist.repox.util;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertyUtil {

    /**
     * Looks up a resource named 'name' in the classpath. The resource must map
     * to a file with .properties extension. The name is assumed to be absolute
     * and uses "/" (ex: resources/configuration.properties)
     *
     * @param name classpath resource name [may not be null]
     * @return resource converted to java.util.Properties [may be null if the
     *         resource was not found and THROW_ON_LOAD_FAILURE is false]
     * @throws IllegalArgumentException if the resource was not found and
     *             THROW_ON_LOAD_FAILURE is true
     */
    public static Properties loadProperties(String name) {
        final String SUFFIX = ".properties";

        if (name == null) {
            throw new IllegalArgumentException("null input: name");
        }

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        if (name.endsWith(SUFFIX)) {
            name = name.substring(0, name.length() - SUFFIX.length());
        }

        Properties result;

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            name = name.replace('/', '.');
            // Throws MissingResourceException on lookup failures:
            ResourceBundle.clearCache();
            ResourceBundle.clearCache(loader);
            ResourceBundle rb = ResourceBundle.getBundle(name, Locale.getDefault(), loader);

            result = new Properties();
            for (Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
                final String key = (String) keys.nextElement();
                String value = rb.getString(key);

                if(value.contains("/:")){
                    value = value.replace("/:", ":");
                }

                result.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }

        if (result == null) {
            throw new IllegalArgumentException("could not load [" + name + "]");
        }

        return result;
    }


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
