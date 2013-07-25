package pt.utl.ist.repox.util;

import java.io.*;
import java.net.URL;
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
	 * @param loader classloader through which to load the resource [null is
	 *            equivalent to the application loader]
	 * @return resource converted to java.util.Properties [may be null if the
	 *         resource was not found and THROW_ON_LOAD_FAILURE is false]
	 * @throws IllegalArgumentException if the resource was not found and
	 *             THROW_ON_LOAD_FAILURE is true
	 */
	public static Properties loadProperties(String name) {
		final String SUFFIX = ".properties";

		if(name == null) {
			throw new IllegalArgumentException("null input: name");
		}

		if(name.startsWith("/")) {
			name = name.substring(1);
		}

		if(name.endsWith(SUFFIX)) {
			name = name.substring(0, name.length() - SUFFIX.length());
		}

		Properties result = null;

		InputStream in = null;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();

			name = name.replace('/', '.');
			// Throws MissingResourceException on lookup failures:
			final ResourceBundle rb = ResourceBundle.getBundle(name, Locale.getDefault(), loader);

			result = new Properties();
			for(Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
				final String key = (String) keys.nextElement();
				final String value = rb.getString(key);
								
				result.put(key, value);
			}
		} catch(Exception e) {
			e.printStackTrace();
			result = null;
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(Throwable ignore) {
				}
			}
		}

		if(result == null) {
			throw new IllegalArgumentException("could not load [" + name + "]");
		}

		return result;
	}
	
	public static void main(String[] args) throws Exception {
		URL lixoURL = Thread.currentThread().getContextClassLoader().getResource("lixo.properties");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(lixoURL.getFile()));
		writer.write("asd=asd/asd/asd\nasd2=asd\\asd\\asd\n");
		writer.close();
		
		String fullString = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lixoURL.getFile())));
		String currentLine = "";
		while((currentLine = reader.readLine()) != null) {
			fullString += currentLine + "\n";
		}
		reader.close();

		System.out.println("fullString: " + fullString);
		System.out.println("corrected value: " + fullString.replace('\\', '/'));
		
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(lixoURL.getFile()));
		writer2.write(fullString.replace('\\', '/'));
		writer2.close();
		
		PropertyUtil.loadProperties("lixo.properties");
	}
}
