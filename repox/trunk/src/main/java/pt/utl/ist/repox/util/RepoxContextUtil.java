package pt.utl.ist.repox.util;

import eu.europeana.repox2sip.Repox2SipException;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;

public class RepoxContextUtil {
    private static final Logger log = Logger.getLogger(RepoxContextUtil.class);
    public static final String CONFIG_FILE = "configuration.properties";
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
            log.debug("repoxManager: " + repoxManager);
            if(repoxManager == null) {
                Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
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

    public static void reloadProperties(){
        try {
            Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
            RepoxConfiguration configuration = new RepoxConfiguration(configurationProperties);
            log.warn("Using DEFAULT configuration properties file: " + CONFIG_FILE);
            repoxManager = new RepoxManager(configuration, DATA_PROVIDERS_FILENAME, STATISTICS_FILENAME, RECORD_COUNTS_FILENAME,
                    SCHEDULED_TASKS_FILENAME, RECOVERABLE_TASKS_FILENAME, METADATA_TRANSFORMATIONS_FILENAME);
        } catch(Exception e) {
            log.fatal("Unable to load RepoxManager", e);
        }
    }

    public static void useRepoxManagerTest() throws IOException, ClassNotFoundException, DocumentException, IllegalFileFormatException, NoSuchMethodException, SQLException, ParseException, Repox2SipException {
        Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(TEST_CONFIG_FILE);
        RepoxConfiguration configuration = new RepoxConfiguration(configurationProperties);
        log.warn("Using TEST configuration properties file: " + TEST_CONFIG_FILE);
        repoxManager = new RepoxManager(configuration, DATA_PROVIDERS_FILENAME, STATISTICS_FILENAME, RECORD_COUNTS_FILENAME,
                SCHEDULED_TASKS_FILENAME, RECOVERABLE_TASKS_FILENAME, METADATA_TRANSFORMATIONS_FILENAME);
    }





}
