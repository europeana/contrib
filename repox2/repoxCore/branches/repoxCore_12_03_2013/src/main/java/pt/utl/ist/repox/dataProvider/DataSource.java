/*
 * Created on 23/Mar/2006
 *
 */
package pt.utl.ist.repox.dataProvider;

import com.ibm.icu.util.Calendar;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.accessPoint.AccessPointRecordRepoxFull;
import pt.utl.ist.repox.accessPoint.AccessPointTimestamp;
import pt.utl.ist.repox.dataProvider.dataSource.IdExtracted;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.repox.externalServices.*;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.statistics.RecordCount;
import pt.utl.ist.repox.task.DataSourceIngestTask;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.util.CompareUtil;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.util.DateUtil;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a Data Source in REPOX. It will be used to harvest records and ingest them. Known
 * implementations are: DataSourceOai, DataSourceDirectoryImporter
 */
public abstract class DataSource {
    public enum StatusDS { RUNNING, ERROR, OK, WARNING, CANCELED, PRE_PROCESSING, POST_PROCESSING, PRE_PROCESS_ERROR, POST_PROCESS_ERROR }

    private static final Logger log = Logger.getLogger(DataSource.class);

    public static final int MAX_ID_SIZE = 32;

    protected static final int RECORDS_BATCH_SIZE = 1000;
    protected static final String LAST_TASK_FILENAME = "lastTask.txt";
    protected HashMap<String, AccessPoint> accessPoints = new HashMap<String, AccessPoint>(); // AccessPoints for this Data Source
    protected Map<String, MetadataTransformation> metadataTransformations; // Map of source -> destination MetadataFormat
    protected List<ExternalRestService> externalRestServices;
    protected List<OldTask> oldTasksList = new ArrayList<OldTask>();
    protected String id;
    protected String schema;
    protected String namespace;
    protected RecordIdPolicy recordIdPolicy;
    protected String description;
    protected StatusDS status;
    protected String lastRunResult;
    protected Date lastUpdate;
    protected String metadataFormat;
    protected boolean stopExecution = false;
    protected boolean forceStopExecution = false;
    protected int maxRecord4Sample = -1;
    protected int numberOfRecords2Harvest = -1;
    protected int numberOfRecordsPerResponse = -1;
    protected ArrayList<Long> statisticsHarvester = new ArrayList<Long>();
    protected ExternalServiceStates.ContainerType externalServicesRunType;
    protected File exportDir;

    public int getMaxRecord4Sample() {
        return maxRecord4Sample;
    }

    public void setMaxRecord4Sample(int maxRecord4Sample) {
        this.maxRecord4Sample = maxRecord4Sample;
    }
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public HashMap<String, AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(HashMap<String, AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }

    public Map<String, MetadataTransformation> getMetadataTransformations() {
        return metadataTransformations;
    }

    public void setMetadataTransformations(Map<String, MetadataTransformation> metadataTransformations) {
        this.metadataTransformations = metadataTransformations;
    }

    public List<ExternalRestService> getExternalRestServices() {
        if(externalRestServices == null)
            externalRestServices = new ArrayList<ExternalRestService>();
        return externalRestServices;
    }

    public void setExternalRestServices(List<ExternalRestService> externalRestServices) {
        this.externalRestServices = externalRestServices;
    }

    public ExternalServiceStates.ContainerType getExternalServicesRunType() {
        return externalServicesRunType;
    }

    public void setExternalServicesRunType(ExternalServiceStates.ContainerType externalServicesRunType) {
        this.externalServicesRunType = externalServicesRunType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecordIdPolicy getRecordIdPolicy() {
        return recordIdPolicy;
    }

    public void setRecordIdPolicy(RecordIdPolicy recordIdPolicy) {
        this.recordIdPolicy = recordIdPolicy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusDS getStatus() {
        return status;
        //return StatusDS.OK;
    }

    public List<OldTask> getOldTasksList() {
        return oldTasksList;
    }

    /**
     * Obtains the current status of this DataSource
     */
    public String getStatusString() throws IOException {
        if(status != null){
            return status.toString();
        }
        return "";
    }

    public void setStatus(StatusDS status) {
        this.status = status;
    }

    public String getLastRunResult() {
        return lastRunResult;
    }

    public void setLastRunResult(String lastRunResult) {
        this.lastRunResult = lastRunResult;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMetadataFormat() {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat) {
        this.metadataFormat = metadataFormat;
    }


    public DataSource() {
        super();
    }

    public DataSource(DataProvider dataProvider, String id, String description, String schema, String namespace, String metadataFormat,
                      RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations) {
        this();
        this.id = id;
        this.description = description;
        this.schema = schema;
        this.namespace = namespace;
        this.metadataFormat = metadataFormat;
        this.recordIdPolicy = recordIdPolicy;

        if(metadataTransformations == null){
            this.metadataTransformations = new TreeMap<String, MetadataTransformation>();
        }
        else{
            this.metadataTransformations = metadataTransformations;
        }

        initAccessPoints();
    }

    private void sendEmail(Task.Status exitStatus, File logFile) throws FileNotFoundException, MessagingException {
        String smtpServer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer();
        if(smtpServer == null || smtpServer.isEmpty()) {
            return;
        }

        String fromEmail = "repox@noreply.eu";
        String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();
        String[] recipientsEmail = new String[]{ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getAdministratorEmail()};
        File[] attachments = new File[] { logFile };

        String message = "Data Source " + id + " finished ingesting." + " Exit status: " + exitStatus.toString()
                + "\nLog file is attached to this email."
                + "\n\n--------------------------------------------------------------------------------\n"
                + "This email is sent automatically by REPOX. Do not reply to this message.";



    }

    public Task.Status startIngest(String taskId, boolean fullIngest) {
        Task.Status exitStatus = Task.Status.OK;
        stopExecution = false;
        forceStopExecution = false;

        File logFile = getLogFile(taskId);

        try {
            // Run Pre-Process Services
            ExternalServiceStates.ServiceExitState esState = runExternalServices("PRE_PROCESS",logFile);

            if(checkProcessingState(esState,"PRE_PROCESS"))
                return exitStatus;

            //Status - running ingest
            status = StatusDS.RUNNING;
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();

            StringUtil.simpleLog("Starting to import from Data Source with id " + id, this.getClass(), logFile);
            Date now = new Date();

            exitStatus = ingestRecords(logFile, fullIngest);

            if(exitStatus.isSuccessful()){
                status = StatusDS.OK;
            }
            else if(exitStatus.isCanceled()){
                status = StatusDS.CANCELED;
            }
            else if(exitStatus.isForceEmpty()){
                status = null;
                // update record's count
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, true);
                // update dataProviders.xml
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
                return exitStatus;
            }
            else{
                status = StatusDS.ERROR;
            }

            // update record's count
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, true);

            lastUpdate = now;

            // update dataProviders.xml
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();

            // Create last ingest old Task
            Task task = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getRunningTask(taskId);
            if(task instanceof DataSourceIngestTask) {
                DataSourceIngestTask dSTask = (DataSourceIngestTask) task;
                DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dSTask.getDataSourceId());

                if(dataSourceContainer != null){
                    DataSource dataSource = dataSourceContainer.getDataSource();
                    if(dataSource.getLogFilenames().size() > 0) {
                        createOldTaks(dataSource, dSTask.getFullIngest(), dSTask.getTaskId(), dSTask.getRetries(),
                                dSTask.getRetryDelay(), dSTask.getMaxRetries());

                    }
                }
            } else if(task instanceof ScheduledTask) {
                ScheduledTask scheduledTask = (ScheduledTask) task;
                String[] params = scheduledTask.getParameters();
                String dsID = params[1];
                String fullIngestStr = params[2];
                DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dsID);

                if(dataSourceContainer != null){
                    DataSource dataSource = dataSourceContainer.getDataSource();
                    if(dataSource.getLogFilenames().size() > 0) {
                        createOldTaks(dataSource, Boolean.getBoolean(fullIngestStr), scheduledTask.getId(), scheduledTask.getRetries(),
                                scheduledTask.getRetryDelay(), scheduledTask.getMaxRetries());

                    }
                }
            }

            StringUtil.simpleLog("Finished importing from Data Source with id " + id + ". Exit status: " + exitStatus.toString(),
                    this.getClass(), logFile);

            String host = InetAddress.getLocalHost().getHostAddress();
            String fromEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String[] recipientsEmail = new String[]{ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getAdministratorEmail()};
            String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();
            String messageText = "Data Source " + id + " finished ingesting."
                    + "\nExit status: " + exitStatus.toString()
                    + "\nRepox Address: " + host
                    + "\nLog file is attached to this email."
                    + "\n\n--------------------------------------------------------------------------------\n"
                    + "This email is sent automatically by REPOX. Do not reply to this message.";

            File zipFile = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient().createZipFile(logFile);
            File[] attachments = new File[] { zipFile };

            HashMap map = new HashMap<String, String>();
            map.put("exitStatus", exitStatus.toString());
            map.put("id", id);
            map.put("mailType","ingest");
            map.put("repoxAddress",host);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient().sendEmail(fromEmail, recipientsEmail, subject, messageText, attachments, map);

            // Run Post-Process Services
            ExternalServiceStates.ServiceExitState esStatus = runExternalServices("POST_PROCESS",logFile);

            if(checkProcessingState(esStatus,"POST_PROCESS"))
                return exitStatus;

            if(exitStatus.isSuccessful()){
                status = StatusDS.OK;
            }
            else if(exitStatus.isCanceled()){
                status = StatusDS.CANCELED;
            }
            else if(exitStatus.isForceEmpty()){
                status = null;
            }
            else{
                status = StatusDS.ERROR;
            }

        } catch (MessagingException e) {
            log.warn(e.getMessage(), e);
            if(status != null && status == StatusDS.OK)
                status = StatusDS.WARNING;
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
            StringUtil.simpleLog("WARN - Could not send email notification: " + e.getMessage(), e, this.getClass(), logFile);
        } catch (Exception e) {
            status = StatusDS.ERROR;
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
            log.error(e.getMessage(), e);
            StringUtil.simpleLog("ERROR importing from Data Source with id " + id + ": " + e.getMessage(), e, this.getClass(), logFile);
        }

        return exitStatus;
    }

    private void createOldTaks(DataSource dataSource, boolean fullIngest, String taskID, int retries, long retryDelay,
                               int maxRetries) {
        try {
            Calendar cal=Calendar.getInstance();
            cal.setTime(new Date());

            int m = cal.get(GregorianCalendar.MONTH) + 1;
            int d = cal.get(GregorianCalendar.DATE);
            String mm = Integer.toString(m);
            String dd = Integer.toString(d);
            String dateString =  "" + cal.get(GregorianCalendar.YEAR) + "-" +
                    (m < 10 ? "0" + mm : mm) + "-" +
                    (d < 10 ? "0" + dd : dd) + " " + cal.get(GregorianCalendar.HOUR_OF_DAY) + ":" +
                    cal.get(GregorianCalendar.MINUTE);

            String logName = dataSource.getLogFilenames().get(0);
            String ingestType;
            if(fullIngest)
                ingestType = "fullIngest";
            else
                ingestType = "incrementalIngest";

            OldTask newOldTask = new OldTask(dataSource, taskID, logName,
                    ingestType,dataSource.getStatusString(), String.valueOf(retries),
                    String.valueOf(maxRetries), String.valueOf(retryDelay), dateString,
                    dataSource.getNumberRecords());

            dataSource.getOldTasksList().add(newOldTask);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveOldTask(newOldTask);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private File getLogFile(String taskId) {
        String yearMonthString = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1);
        File logsMonthDir = new File(getLogsDir(), yearMonthString);
        logsMonthDir.mkdir();
        File logFile = new File(logsMonthDir, taskId + "_" + DateFormatUtils.format(new Date(), TimeUtil.LONG_DATE_FORMAT_COMPACT) + ".log");

        return logFile;
    }

    public void stopIngest(boolean forceStop) {
        if(forceStop){
            forceStopExecution = true;
        }
        log.warn("Received stop signal for execution of Data Source " + id + " of Data Provider " + ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(id));
        stopExecution = true;
    }

    /**
     * Function to show the number od records according #NumberFormat
     * @return
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
    public String getNumberRecords() throws IOException, DocumentException, SQLException {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
        RecordCount dataSourceCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id);
        return (dataSourceCount == null ? "0" : numberFormat.format(dataSourceCount.getCount()));
    }

    public int getIntNumberRecords() throws IOException, DocumentException, SQLException {
        RecordCount dataSourceCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id);
        return (dataSourceCount == null ? 0 : dataSourceCount.getCount());
    }

    public List<String> getLogFilenames() {
        File logDir = getLogsDir();
        List<File> logDirnames = Arrays.asList(logDir.listFiles());
        List<String> logFilenames = new ArrayList<String>();

        for (File logMonthDir : logDirnames) {
            if(logMonthDir.isDirectory() && logMonthDir.listFiles().length > 0) {
                for (File logFile : logMonthDir.listFiles()) {
                    if(logFile.getName().endsWith(".log")) {
                        logFilenames.add(logMonthDir.getName() + File.separator + logFile.getName());
                    }
                }
            }
        }

        Collections.sort(logFilenames, new LogFilenameComparator());
        Collections.reverse(logFilenames);

        return logFilenames;
    }

    public File getOutputDir() {
        File outputDir = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath(), id);
        outputDir.mkdir();
        return outputDir;
    }

    public File getLogsDir() {
        File logDir = new File(getOutputDir(), "logs");
        logDir.mkdir();
        return logDir;
    }

    public File getTasksDir() {
        File tasksDir = new File(getOutputDir(), "tasks");
        tasksDir.mkdir();
        return tasksDir;
    }

    public File getExportDir() {
         return exportDir;
    }

    public void setExportDir(String exportDirPath){
        exportDir = new File(exportDirPath);
        exportDir.mkdir();
    }

    public String getNewTaskId() throws IOException {
        int nextId = getLastTaskId() + 1;
        setLastTaskId(nextId);
        return getId() + "_" + nextId;
    }

    protected int getLastTaskId() throws IOException {
        int lastId = 0;

        File lastTaskFile = new File(getTasksDir(), LAST_TASK_FILENAME);
        if(lastTaskFile.exists()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lastTaskFile)));
            String currentLine;
            if((currentLine = reader.readLine()) != null) {
                try {
                    lastId = Integer.parseInt(currentLine);
                }
                catch (NumberFormatException e) {
                    log.error("Trying to parse as int: " + currentLine, e);
                }
            }

        }

        return lastId;
    }

    protected void setLastTaskId(int taskId) throws IOException {
        File lastTaskFile = new File(getTasksDir(), LAST_TASK_FILENAME);
        if(lastTaskFile.exists()) {
            File backupFile = new File(lastTaskFile.getParent(), lastTaskFile.getName() + ".bkp");
            FileUtil.copyFile(lastTaskFile, backupFile);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lastTaskFile)));
            writer.write(Integer.toString(taskId));
            writer.newLine();
        }
        catch(IOException e) {
            log.error("Error writing last task file", e);
        }
        finally {
            if(writer != null) {
                writer.close();
            }
        }
    }

    public File getNewLogFile(Date startDate) {
        String currentTime = new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT_COMPACT).format(startDate);
        return new File(getLogsDir(), currentTime + ".log");
    }

    public void initAccessPoints() {
        // initialization of the default AccessPoints used internally by REPOX
        AccessPoint defaultTimestampAP = new AccessPointTimestamp(AccessPoint.PREFIX_INTERNAL_BD + id +  AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD);
        defaultTimestampAP.setIndexDeletedRecords(false);
        defaultTimestampAP.setRepoxInternal(true);
        this.accessPoints.put(defaultTimestampAP.getId(), defaultTimestampAP);

        AccessPoint defaultRecordAP = new AccessPointRecordRepoxFull(AccessPoint.PREFIX_INTERNAL_BD + id + AccessPoint.SUFIX_RECORD_INTERNAL_BD);
        defaultRecordAP.setRepoxInternal(true);
        this.accessPoints.put(defaultRecordAP.getId(), defaultRecordAP);
    }


    private boolean equalsAccessPoints(HashMap<String, AccessPoint> accessPoints) {
        if(this.accessPoints == null && accessPoints == null) {
            return true;
        }
        else if(this.accessPoints == null || accessPoints == null
                || this.accessPoints.size() != accessPoints.size()) {
            return false;
        }

        Set<String> localAccessPointsIds = this.accessPoints.keySet();
        Set<String> otherAccessPointsIds = accessPoints.keySet();

        return localAccessPointsIds.containsAll(otherAccessPointsIds);
    }

    protected boolean equalsBaseProperties(DataSource dataSource) {
        return CompareUtil.compareObjectsAndNull(this.id, dataSource.getId())
                && CompareUtil.compareObjectsAndNull(this.status, dataSource.getStatus())
                && CompareUtil.compareObjectsAndNull(this.lastRunResult, dataSource.getLastRunResult())
                && CompareUtil.compareObjectsAndNull(this.lastUpdate, dataSource.getLastUpdate())
                && this.equalsAccessPoints(dataSource.getAccessPoints());
    }

    protected boolean emptyRecords() throws IOException {
        boolean successfulDeletion = true;

        //Delete records indexes
        for(AccessPoint accessPoint : this.getAccessPoints().values()) {
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().emptyIndex(this, accessPoint);
                log.info("Emptied AccessPoint with id " + accessPoint.getId());
            } catch(Exception e) {
                log.error("Unable to empty Table from Database: " + accessPoint.getId(), e);
                successfulDeletion = false;
            }
        }

        //Remove from Record Counts cache
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().removeDataSourceCounts(id);

        return successfulDeletion;
    }

    public abstract Task.Status ingestRecords(File logFile, boolean fullIngest) throws Exception;

    public abstract boolean isWorking();

    /**
     * @return The class of the local IDs of this Data Source.
     */
    public Class getClassOfLocalId() {
        return String.class;
    }

    /**
     * Obtains the date of the last synchronization of this DataSource
     */
    public String getSynchronizationDate(File syncDateFile) {
        if(!syncDateFile.exists()) {
            return "";
        }
        String dateString = FileUtil.readFile(syncDateFile, 0);
        try{
            return dateString;
        }
        catch (Exception e){
            return "";
        }
    }

    public String getSynchronizationDateString() throws IOException {
        return DateUtil.date2String(lastUpdate, TimeUtil.LONG_DATE_FORMAT);
    }


    public int getSampleNumber() {
        File syncDateFile = getSyncDateFile();
        if(!syncDateFile.exists()) {
            return -1;
        }
        String sample = FileUtil.readFile(syncDateFile, 1);
        return Integer.valueOf(sample);
    }

    public int getSampleNumber(File syncDateFile) {
        if(!syncDateFile.exists()) {
            return -1;
        }
        String sample = FileUtil.readFile(syncDateFile, 1);
        try{
            return Integer.valueOf(sample);
        }
        catch (Exception e){
            return -1;
        }
    }


    public File getSyncDateFile() {
        return new File(getOutputDir(), "synchronization-date.txt");
    }

    public void renameDataSourceDir(String oldDataSourceId, String newDataSourceId) throws IOException {
        File oldDataSourceDir = getDataSourceDir(oldDataSourceId);

        RepoxConfiguration configuration = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration();
        File newSourceDir = new File(configuration.getRepositoryPath(), newDataSourceId);

        if(oldDataSourceDir.exists()) {
            oldDataSourceDir.renameTo(newSourceDir);
        }
    }

    public File getDataSourceDir() {
        return getDataSourceDir(id);
    }

    public File getDataSourceDir(String dataSourceId) {
        RepoxConfiguration configuration = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration();
        File dataSourceDir = new File(configuration.getRepositoryPath(), dataSourceId);
        dataSourceDir.mkdir();
        return dataSourceDir;
    }

    public File getDataSourceLogsDir(String dataSourceId) {
        File logDir = new File(getDataSourceDir(dataSourceId), "logs");
        logDir.mkdir();
        return logDir;
    }

    public File getNewDataSourceLogFile(String dataSourceId, Date startDate) {
        String currentTime = new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT_COMPACT).format(startDate);
        return new File(getDataSourceLogsDir(dataSourceId), currentTime + ".log");
    }

    /**
     * Empty repository directory, record count and specific Data Source temporary files and directories.
     *
     * @throws Exception
     */
    public void cleanUp() throws IOException, DocumentException {
        try {
            // stop the ingestion task
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().stopIngestDataSource(id, Task.Status.FORCE_EMPTY);
        } catch (NoSuchMethodException e) {
        } catch (ObjectNotFoundException e) {
        } catch (ClassNotFoundException e) {
        } catch (ParseException e) {
        }

        //Delete records indexes
        for(AccessPoint accessPoint : getAccessPoints().values()) {
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().emptyIndex(this, accessPoint);
                log.info("Emptied AccessPoint with id " + accessPoint.getId());
            } catch(Exception e) {
                log.error("Unable to empty Table from Database: " + accessPoint.getId(), e);
            }
        }
        if(getMaxRecord4Sample()!= -1){
            setMaxRecord4Sample(-1);
        }

        File dataSourceDir = getDataSourceDir();
        if(dataSourceDir.exists()) {

            try{
                FileUtils.deleteDirectory(dataSourceDir);
                log.info("Deleted Data Source dir with success from Data Source with id " + id);
                dataSourceDir.mkdir();

            }
            catch (IOException e){
                log.error("Unable to delete Data Source dir from Data Source with id " + id);
            }
        }

        //clear the status
        setStatus(null);

        //Clear the last ingest date
        setLastUpdate(null);

        //Clear Old tasks
        oldTasksList.clear();

        //Delete Record Counts cache
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().removeDataSourceCounts(id);

        //Update XML file
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeOldTasks(id);
    }

    public abstract Element addSpecificInfo(Element sourceElement);

    public Element createElement() {
        try {
            Element sourceElement = DocumentHelper.createElement("source");
            sourceElement.addAttribute("id", getId());
            sourceElement.addAttribute("metadataFormat", getMetadataFormat());
            sourceElement.addAttribute("schema", getSchema());
            sourceElement.addAttribute("namespace", getNamespace());
            sourceElement.addAttribute("lastIngest", getLastUpdate() != null ? getSynchronizationDateString() : "");
            sourceElement.addAttribute("sample", String.valueOf(getMaxRecord4Sample()));
            sourceElement.addAttribute("status", getStatus() != null ? getStatus().toString() : "");
            sourceElement.addElement("description").setText(getDescription());
            sourceElement.addElement("exportDirPath").
                    setText(getExportDir() != null ? getExportDir().getAbsolutePath() :
                            ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                                    getConfiguration().getRepositoryPath() + "/" + getId() + "/" + "export");

            Element recordIdPolicyNode = sourceElement.addElement("recordIdPolicy");
            recordIdPolicyNode.addAttribute("type", getRecordIdPolicy().getClass().getSimpleName());
            if(getRecordIdPolicy() instanceof IdGenerated) {
            }
            else if(getRecordIdPolicy() instanceof IdProvided) {
            }
            else if(getRecordIdPolicy() instanceof IdExtracted) {
                IdExtracted idExtracted = (IdExtracted) getRecordIdPolicy();
                recordIdPolicyNode.addElement("idXpath").setText(idExtracted.getIdentifierXpath());
                if(idExtracted.getNamespaces() != null && !idExtracted.getNamespaces().isEmpty()) {
                    Element namespacesElement = recordIdPolicyNode.addElement("namespaces");
                    for(String currentKey : idExtracted.getNamespaces().keySet()) {
                        Element namespaceElement = namespacesElement.addElement("namespace");
                        namespaceElement.addElement("namespacePrefix").setText(currentKey);
                        namespaceElement.addElement("namespaceUri").setText(idExtracted.getNamespaces().get(currentKey));
                    }
                }
            }
            else {
                throw new RuntimeException("Invalid RecordIdPolicy of class " + getRecordIdPolicy().getClass().getName());
            }

            // fill with specific information according to the data source type (OAI, Folder, Z39.50)
            sourceElement = addSpecificInfo(sourceElement);


            //Add MetadataTransformations
            Element metadataTransformationsNode = sourceElement.addElement("metadataTransformations");
            if(getMetadataTransformations() != null && !getMetadataTransformations().isEmpty()) {
                for (MetadataTransformation metadataTransformation : getMetadataTransformations().values()) {
                    if(metadataTransformation != null) {
                        Element metadataTransformationNode = metadataTransformationsNode.addElement("metadataTransformation");
                        metadataTransformationNode.setText(metadataTransformation.getId());
                    }
                }
            }

            //Add ExternalServices
            Element externalServicesNode = sourceElement.addElement("restServices");
            if(externalServicesRunType != null && getExternalRestServices().size() > 0)
                externalServicesNode.addAttribute("executeType",externalServicesRunType.name());
            if(getExternalRestServices().size() > 0) {
                for (ExternalRestService externalRestService : getExternalRestServices()) {
                    if(externalRestService != null) {
                        Element externalServiceNode = externalServicesNode.addElement("restService");
                        externalServiceNode.addAttribute("id",externalRestService.getId());
                        externalServiceNode.addAttribute("uri",externalRestService.getUri());
                        externalServiceNode.addAttribute("statusUri",externalRestService.getStatusUri());
                        externalServiceNode.addAttribute("name",externalRestService.getName());
                        externalServiceNode.addAttribute("type",externalRestService.getType());
                        Element serviceParameters = externalServiceNode.addElement("parameters");
                        for(ServiceParameter serviceParameter : externalRestService.getServiceParameters()){
                            Element serviceParameterNode = serviceParameters.addElement("parameter");
                            serviceParameterNode.addAttribute("name",serviceParameter.getName());
                            serviceParameterNode.addAttribute("value",serviceParameter.getValue());
                            serviceParameterNode.addAttribute("type",serviceParameter.getType());
                            serviceParameterNode.addAttribute("semantics",serviceParameter.getSemantics());
                            serviceParameterNode.addAttribute("required",String.valueOf(serviceParameter.getRequired()));
                        }
                    }
                }
            }

            return sourceElement;
        } catch (IOException e) {
            return null;
        }
    }

    public abstract int getTotalRecords2Harvest();
    public abstract String getNumberOfRecords2HarvestStr();
    public abstract int getRecordsPerResponse();
    public abstract List<Long> getStatisticsHarvester();

    public float getPercentage(){
        try {
            if(getTotalRecords2Harvest() == 0)
                return 0;
            else
                return (getIntNumberRecords()*100)/getTotalRecords2Harvest();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
    }

    public long getTimeLeft(){
        try{
            int recordsLeft = getTotalRecords2Harvest() - getIntNumberRecords();
            int segmentsLeft = recordsLeft/getRecordsPerResponse();
            long value = segmentsLeft * getAverageOfSecondsPerIngest();
            return value > 0 ? value : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private long getAverageOfSecondsPerIngest(){
        if(getStatisticsHarvester().size() > 0) {
            long sum = 0;
            for(long time : getStatisticsHarvester()) {
                sum += time;
            }
            return (long)sum/getStatisticsHarvester().size();
        }
        return -1;
    }

    protected ExternalServiceStates.ServiceExitState runExternalServices(String type, File logFile){
        try {
            if(type.equals("PRE_PROCESS"))
                status = StatusDS.PRE_PROCESSING;
            else
                status = StatusDS.POST_PROCESSING;

            if(containsExternalServicesOfType(type)){
                ExternalRestServiceContainer externalRestServiceContainer = new ExternalRestServiceContainer(externalServicesRunType);

                for(ExternalRestService externalRestService: externalRestServices){
                    if(externalRestService.getType().equals(type)){
                        externalRestServiceContainer.addExternalService(new ExternalRestServiceThread(externalRestService,
                                externalRestServiceContainer,logFile));
                    }
                }
                while(!externalRestServiceContainer.getContainerRunningState().equals(ExternalServiceStates.ServiceRunningState.FINISHED)){
                    Thread.sleep(3000);
                }
                return externalRestServiceContainer.getContainerExitState();
            }else
                return ExternalServiceStates.ServiceExitState.NONE;
        } catch (InterruptedException e) {
            return ExternalServiceStates.ServiceExitState.ERROR;
        }
    }

    protected boolean containsExternalServicesOfType(String type){
        boolean contains = false;
        for(ExternalRestService externalRestService: externalRestServices){
            if(externalRestService.getType().equals(type)){
                contains = true;
                break;
            }
        }
        return contains;
    }

    protected boolean checkProcessingState(ExternalServiceStates.ServiceExitState exitState, String type){
        boolean isError = false;
        if(exitState.equals(ExternalServiceStates.ServiceExitState.ERROR) && type.equals("PRE_PROCESS")){
            status = StatusDS.PRE_PROCESS_ERROR;
            isError = true;
        }else if(exitState.equals(ExternalServiceStates.ServiceExitState.ERROR) && type.equals("POST_PROCESS")){
            status = StatusDS.POST_PROCESS_ERROR;
            isError = true;
        }
        return isError;
    }
    
    public boolean hasTransformation(String format){
        for(MetadataTransformation metadataTransformation : getMetadataTransformations().values()){
            if(metadataTransformation.getDestinationFormat().equals(format)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args)throws Exception{
        /*VelocityEngine ve = new VelocityEngine();
        ve.init();
        Template t = ve.getTemplate( "src/main/resources/emailExitStatus.vm" );
        VelocityContext context = new VelocityContext();
        context.put("id", "44");
        context.put("exitStatus","exitStatus.toString()");
        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        String message = writer.toString();
        System.out.println(message);*/
        try{

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource("http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd")}));

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource("C:\\Users\\GPedrosa\\Desktop\\testeValidate\\teste.xml"));

        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
