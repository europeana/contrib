package pt.utl.ist.repox.util;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;

public class RepoxContextUtil {
	private static final Logger log = Logger.getLogger(RepoxContextUtil.class);
	private static final String CONFIG_FILE = "configuration.properties";
	private static final String TEST_CONFIG_FILE = "Test-configuration.properties";

	public static final String COUNTRIES_FILENAME = "countries.txt";

	public static final String DATA_PROVIDERS_FILENAME = "dataProviders.xml";
	public static final String STATISTICS_FILENAME = "repoxStatistics.xml";
	public static final String RECORD_COUNTS_FILENAME = "recordCounts.xml";
	public static final String DATA_SOURCES_STATE_FILENAME = "dataSourcesStates.xml";
	private static final String SCHEDULED_TASKS_FILENAME = "scheduledTasks.xml";
	private static final String RECOVERABLE_TASKS_FILENAME = "recoverableTasks.xml";
	private static final String METADATA_TRANSFORMATIONS_FILENAME = "metadataTransformations.xml";

	private static RepoxManager repoxManager;

	public static RepoxManager getRepoxManager(){
		try {
			if(repoxManager == null) {

				Properties configurationProperties = loadCorrectedConfiguration(CONFIG_FILE);
				RepoxConfiguration configuration = new RepoxConfiguration(configurationProperties);
				log.warn("Using DEFAULT configuration properties file: " + CONFIG_FILE);
				repoxManager = new RepoxManager(configuration, DATA_PROVIDERS_FILENAME, STATISTICS_FILENAME, RECORD_COUNTS_FILENAME,
						SCHEDULED_TASKS_FILENAME, RECOVERABLE_TASKS_FILENAME, METADATA_TRANSFORMATIONS_FILENAME);
			}

			return repoxManager;
		} catch(Exception e) {
			log.fatal("Unable to load RepoxManager", e);
			return null;
		}
	}

	public static void useRepoxManagerTest() throws IOException, ClassNotFoundException, DocumentException, IllegalFileFormatException, NoSuchMethodException, SQLException, ParseException {
		Properties configurationProperties = loadCorrectedConfiguration(TEST_CONFIG_FILE);
		RepoxConfiguration configuration = new RepoxConfiguration(configurationProperties);
		log.warn("Using TEST configuration properties file: " + TEST_CONFIG_FILE);
		repoxManager = new RepoxManager(configuration, DATA_PROVIDERS_FILENAME, STATISTICS_FILENAME, RECORD_COUNTS_FILENAME,
				SCHEDULED_TASKS_FILENAME, RECOVERABLE_TASKS_FILENAME, METADATA_TRANSFORMATIONS_FILENAME);
	}

	public static Properties loadCorrectedConfiguration(String configurationFilename) throws IOException {
		URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(configurationFilename);
		String configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");

		String fullString = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configurationFile)));
		String currentLine = "";
		while((currentLine = reader.readLine()) != null) {
			fullString += currentLine + "\n";
		}
		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(configurationFile));
		writer.write(fullString.replace('\\', '/'));
		writer.close();

		Properties configurationProperties = PropertyUtil.loadProperties(configurationFilename);

		return configurationProperties;
	}
    


}
