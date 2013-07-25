package at.ait.dme.dbpedix.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central access to DBpedix' configuration properties.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class Config {
	
	public static final String DBPEDIA_DOWNLOAD_BASEURL = "dbpedia.download.baseURL";
	
	public static final String DBPEDIA_DUMP_DIR = "dbpedia.dump.dir";
	
	public static final String DBPEDIA_INDEX_DIR = "dbpedia.index.dir";
	
	private static Config instance = null;
	
	private Properties props;
	
	private Config() {
        props = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties");
        
        if (is == null)
            throw new RuntimeException("Fatal error: config.properties not found");
        
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Fatal error: could not load config.properties: " + e.getMessage());
        }
	}
	
	private Config(Properties properties) {
		this.props = properties;
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the singleton instance
	 */
	public static Config getInstance() {
		if (instance == null)
			instance = new Config();
		
		return instance;
	}
	
	/**
	 * Use this to override the settings in the 'config.properties'
	 * file with your own. (We're using this for unit testing. I'd
	 * strongly advise against using it for anything else!)
	 * @param properties your own properties
	 */
	public static void override(Properties properties) {
		instance = new Config(properties);
	}
	
	/**
	 * The base URL of the dataset on the DBpedia download server 
	 * @return the download base URL
	 */
	public String getDBpediaDownloadBaseURL() {
		return props.getProperty(DBPEDIA_DOWNLOAD_BASEURL);
	}
	
	/**
	 * The directory where the downloaded DBpedia dumps are stored
	 * @return the dump directory
	 */
	public String getDumpDirectory() {
		return props.getProperty(DBPEDIA_DUMP_DIR);
	}
	
	/**
	 * The directory where the Lucene index is stored
	 * @return the index directory
	 */
	public String getIndexDirectory() {
		return props.getProperty(DBPEDIA_INDEX_DIR);
	}

}
