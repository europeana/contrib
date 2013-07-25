package pt.utl.ist.repox;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.accessPoint.database.AccessPointManagerFactory;
import pt.utl.ist.repox.data.DataManager;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.statistics.StatisticsManager;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * The main class of REPOX. It manages all other components.
 *
 * @author Nuno Freire
 */
public class RepoxManager {
    private static final Logger log = Logger.getLogger(RepoxManager.class);
    private static String baseUrn;

    private RepoxConfiguration configuration;
    private AccessPointsManager accessPointsManager;
    private DataManager dataManager;
    private StatisticsManager statisticsManager;
    private RecordCountManager recordCountManager;
    private TaskManager taskManager;
    private MetadataTransformationManager metadataTransformationManager;
    private Thread taskManagerThread;

    public RepoxConfiguration getConfiguration() {
        return configuration;
    }

    public AccessPointsManager getAccessPointsManager() {
        return accessPointsManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public RecordCountManager getRecordCountManager() {
        return recordCountManager;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public MetadataTransformationManager getMetadataTransformationManager() {
        return metadataTransformationManager;
    }

    public Thread getTaskManagerThread() {
        return taskManagerThread;
    }

    public RepoxManager(RepoxConfiguration configuration, String dataProvidersFilename, String statisticsFilename,
                        String recordCountsFilename, String schedulerFilename, String ongoingTasksFilename,
                        String metadataTransformationsFilename) throws DocumentException, ParseException, SQLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException {
        this.configuration = configuration;

        if(configuration.isConfigurationFileOk()){
            File statisticsFile = new File(configuration.getXmlConfigPath(), statisticsFilename);
            this.statisticsManager = new StatisticsManager(statisticsFile);

            File countsFile = new File(configuration.getXmlConfigPath(), recordCountsFilename);
            this.recordCountManager = new RecordCountManager(countsFile);

            File metadataTransformationsFile = new File(configuration.getXmlConfigPath(), metadataTransformationsFilename);
            File xsltDir = new File(configuration.getXmlConfigPath(), RepoxConfiguration.METADATA_TRANSFORMATIONS_DIRNAME);
            this.metadataTransformationManager = new MetadataTransformationManager(metadataTransformationsFile, xsltDir);

            File dataProvidersFile = new File(configuration.getXmlConfigPath(), dataProvidersFilename);
            this.dataManager = new DataManager(dataProvidersFile, this.metadataTransformationManager, configuration);

            RepoxManager.baseUrn = configuration.getBaseUrn();

            this.accessPointsManager = AccessPointManagerFactory.getInstance(configuration);
            accessPointsManager.initialize(dataManager.loadDataSources());

            File scheduledTasksFile = new File(configuration.getXmlConfigPath(), schedulerFilename);
            File ongoingTasksFile = new File(configuration.getXmlConfigPath(), ongoingTasksFilename);
            taskManager = new TaskManager(scheduledTasksFile, ongoingTasksFile);
            taskManagerThread = new Thread(taskManager);
            taskManagerThread.start();
        }


    }

    /**
     * Gets the base URN of this Repox instance. Ex: urn:bn:repox:
     * @return the base URN of this Repox instance. Ex: urn:bn:repox:
     */
    public String getBaseUrn() {
        return baseUrn;
    }
}
