/*
 * Created on 23/Mar/2006
 *
 */
package pt.utl.ist.repox.data;

import com.ibm.icu.util.Calendar;
import eu.europeana.core.util.web.EmailSender;
import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.models.*;
import freemarker.template.TemplateException;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.accessPoint.AccessPointRecordRepoxFull;
import pt.utl.ist.repox.accessPoint.AccessPointTimestamp;
import pt.utl.ist.repox.data.dataSource.IdExtracted;
import pt.utl.ist.repox.data.dataSource.RecordIdPolicy;
import pt.utl.ist.repox.data.sorter.DateSorter;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.statistics.RecordCount;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.util.CompareUtil;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.util.DateUtil;
import pt.utl.ist.util.FileUtil;

import javax.mail.MessagingException;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Represents a Data Source in REPOX. It will be used to harvest records and ingest them. Known
 * implementations are: DataSourceOai, DataSourceDirectoryImporter
 */
public abstract class DataSource {
    public enum StatusDS { RUNNING, ERROR, OK, WARNING }

    private static final Logger log = Logger.getLogger(DataSource.class);

    public static final int MAX_ID_SIZE = 32;

    protected static final int RECORDS_BATCH_SIZE = 1000;
    protected static final String LAST_TASK_FILENAME = "lastTask.txt";

    protected HashMap<String, AccessPoint> accessPoints = new HashMap<String, AccessPoint>(); // AccessPoints for this Data Source
    protected Map<String, MetadataTransformation> metadataTransformations; // Map of source -> destination MetadataFormat
    protected DataProvider dataProvider;
    protected String id;

    protected RecordIdPolicy recordIdPolicy;
    protected String description;
    protected StatusDS status;
    protected String lastRunResult;
    protected Date lastUpdate;
    protected String metadataFormat;
    protected boolean stopExecution = false;

    protected String nameCode;
    protected String name;
    protected long idDb = -1;
    protected String exportPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
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

    public DataProvider getDataProvider() {
        return dataProvider;
    }
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getIdDb() {
        return idDb;
    }

    public void setIdDb(long idDb) {
        this.idDb = idDb;
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

    public String getExportPath() {
        if(getId() != null && exportPath == null){
            exportPath = getDefaultExportDir();
        }
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public DataSource() {
        super();
    }

    public DataSource(DataProvider dataProvider, String id, String description, String metadataFormat,
                      RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations, String name, String nameCode, long idDb, String exportPath) {
        this();
        this.dataProvider = dataProvider;
        this.id = id;
        this.idDb = idDb;
        this.description = description;
        this.metadataFormat = metadataFormat;
        this.recordIdPolicy = recordIdPolicy;
        this.metadataTransformations = metadataTransformations;
        this.name = name;
        this.nameCode = nameCode;

        if(exportPath == null || !exportPath.isEmpty()){
            this.exportPath = exportPath;
        }
        else{
            this.exportPath = getDefaultExportDir();
        }

        initAccessPoints();
    }

    private void sendEmail(Task.Status exitStatus, File logFile) throws MessagingException, TemplateException, IOException {
        String smtpServer = RepoxContextUtil.getRepoxManager().getConfiguration().getSmtpServer();
        String smtpPort = RepoxContextUtil.getRepoxManager().getConfiguration().getSmtpPort();
        if(smtpServer == null || smtpServer.isEmpty()) {
            return;
        }

        String fromEmail = RepoxContextUtil.getRepoxManager().getConfiguration().getDefaultEmail();
        String recipientsEmail = RepoxContextUtil.getRepoxManager().getConfiguration().getAdministratorEmail();
        String adminMailPass =  RepoxContextUtil.getRepoxManager().getConfiguration().getMailPassword();
        String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();

        EmailSender emailSender = new EmailSender();
        String pathIngestFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("ingest.html.ftl").getFile(), "ISO-8859-1");
        emailSender.setTemplate(pathIngestFile.substring(0, pathIngestFile.lastIndexOf("/")) + "/ingest");


        HashMap map = new HashMap<String, String>();
        map.put("exitStatus", exitStatus.toString());
        map.put("id",id);

        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setUsername(fromEmail);
        mail.setPassword(adminMailPass);
        mail.setPort(Integer.valueOf(smtpPort));
        mail.setHost(smtpServer);

        Properties mailConnectionProperties = (Properties)System.getProperties().clone();
        mailConnectionProperties.put("mail.smtp.auth", "true");
        mailConnectionProperties.put("mail.smtp.starttls.enable","true");
        mailConnectionProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailConnectionProperties.put("mail.smtp.socketFactory.fallback", "false");
        mail.setJavaMailProperties(mailConnectionProperties);

        emailSender.setMailSender(mail);
        emailSender.sendEmail(recipientsEmail, fromEmail, subject, map, logFile.getAbsolutePath());

    }

    /*private void sendEmail(Task.Status exitStatus, File logFile) throws FileNotFoundException, MessagingException {
        String smtpServer = RepoxContextUtil.getRepoxManager().getConfiguration().getSmtpServer();
        if(smtpServer == null || smtpServer.isEmpty()) {
            return;
        }

        String fromEmail = "repox@noreply.eu";
        String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();
        String[] recipientsEmail = new String[]{RepoxContextUtil.getRepoxManager().getConfiguration().getAdministratorEmail()};
        File[] attachments = new File[] { logFile };

        String message = "Data Source " + id + " finished ingesting." + " Exit status: " + exitStatus.toString()
                        + "\nLog file is attached to this email."
                        + "\n\n--------------------------------------------------------------------------------\n"
                        + "This email is sent automatically by REPOX. Do not reply to this message.";

        EmailUtil.sendEmail(smtpServer, fromEmail, recipientsEmail, subject, message, attachments);

    }*/

    public Task.Status startIngest(String taskId, boolean fullIngest) {
        Repox2Sip repox2sip = RepoxContextUtil.getRepoxManager().getDataManager().getRepox2sip();
        Request request = new Request();
        File logFile = getLogFile(taskId);
        Task.Status exitStatus = Task.Status.OK;

        try {
            status = StatusDS.RUNNING;
            writeDataSourceStatus();

            if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                // REPOX2SIP Create request
                DataSet dataSet = repox2sip.getDataSet(this.getIdDb());
                request.setDataSet(dataSet);
                request.setStatus(RequestStatus.UNDER_CONSTRUCTION);
                request.setCreationDate(new Date());
                request = setRequestName(request);
                repox2sip.addRequest(request);
            }

            stopExecution = false;

            StringUtil.simpleLog("Starting to import from Data Source with id " + id, this.getClass(), logFile);
            lastUpdate = getSynchronizationDate();
            Date now = new Date();

            exitStatus = ingestRecords(request,logFile, fullIngest);

            if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                // REPOX2SIP Change request status
                request.setStatus(getCorrespondentRequestStatus(exitStatus));
                repox2sip.updateRequest(request);
            }

            lastUpdate = now;
            if(exitStatus.isSuccessful()) {
                signalSynchronizationFinished(lastUpdate);
                status = StatusDS.OK;
                writeDataSourceStatus();
            }
            else{
                status = StatusDS.ERROR;
                writeDataSourceStatus();
            }

            StringUtil.simpleLog("Finished importing from Data Source with id " + id + ". Exit status: " + exitStatus.toString(),
                    this.getClass(), logFile);
            sendEmail(exitStatus, logFile);
            request.setStatus(RequestStatus.IMPORT_COMPLETED);
        } catch (TemplateException e) {
            log.error(e.getMessage(), e);
            status = StatusDS.WARNING;
            writeDataSourceStatus();
            StringUtil.simpleLog("TemplateException: ERROR importing from Data Source with id " + id + ": " + e.getMessage(), this.getClass(), logFile);
            request.setStatus(RequestStatus.ABORTED);
        } catch (Repox2SipException e) {
            log.error(e.getMessage(), e);
            status = StatusDS.ERROR;
            writeDataSourceStatus();
            StringUtil.simpleLog("Repox2SipException: ERROR importing from Data Source with id " + id + ": " + e.getMessage(), this.getClass(), logFile);
            request.setStatus(RequestStatus.ABORTED);
        } catch (MessagingException e) {
            log.warn(e.getMessage(), e);
            status = StatusDS.WARNING;
            writeDataSourceStatus();
            StringUtil.simpleLog("WARN - Could not send email notification: "+e.getMessage(), this.getClass(), logFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            if(status != StatusDS.ERROR)
                status = StatusDS.WARNING;
            writeDataSourceStatus();
            StringUtil.simpleLog("IOException: ERROR importing from Data Source with id " + id + ": " + e.getMessage(), this.getClass(), logFile);
            request.setStatus(RequestStatus.ABORTED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            status = StatusDS.ERROR;
            writeDataSourceStatus();
            StringUtil.simpleLog("ERROR importing from Data Source with id " + id + ": " + e.getMessage(), this.getClass(), logFile);
            request.setStatus(RequestStatus.ABORTED);
        }

        return exitStatus;
    }

    private RequestStatus getCorrespondentRequestStatus(Task.Status status){
        if(status.equals(Task.Status.OK)){
            return RequestStatus.IMPORT_COMPLETED;
        }
        if(status.equals(Task.Status.CANCELED) ||status.equals(Task.Status.FAILED)){
            return RequestStatus.ABORTED;
        }
        return null;
    }


    public abstract Request setRequestName(Request request);


    private File getLogFile(String taskId) {
        String yearMonthString = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1);
        File logsMonthDir = new File(getLogsDir(), yearMonthString);
        logsMonthDir.mkdir();
        File logFile = new File(logsMonthDir, taskId + "_" + DateFormatUtils.format(new Date(), TimeUtil.LONG_DATE_FORMAT_COMPACT) + ".log");
        return logFile;
    }

    public void stopIngest() {
        log.warn("Received stop signal for execution of Data Source " + id + " of Data Provider " + dataProvider.getName());
        stopExecution = true;
    }

    public int getNumberRecords() throws IOException, DocumentException, SQLException {
        RecordCount dataSourceCount = RepoxContextUtil.getRepoxManager().getRecordCountManager().getRecordCount(id);
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


    public List<String> getExportFileNames() {
        File exportDir = new File(getExportPath());

        if(!exportDir.exists()){
            exportDir.mkdir();
        }

        List<File> exportDirNames = Arrays.asList(exportDir.listFiles());
        List<String> exportFileNames = new ArrayList<String>();

        for (File exportFile : exportDirNames) {
            if(exportFile.isFile() && exportFile.getName().endsWith(".zip")) {
                exportFileNames.add(exportFile.getName());
            }
        }

        Collections.sort(exportFileNames, new LogFilenameComparator());
        Collections.reverse(exportFileNames);

        return exportFileNames;
    }

    public File getOutputDir() {
        File outputDir = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getRepositoryPath(), id);
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

    public String getDefaultExportDir() {
        File exportDir;
        if(RepoxContextUtil.getRepoxManager().getConfiguration().getExportDefaultFolder() != null){
            exportDir = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getExportDefaultFolder());
            if(!exportDir.exists()){
                // create relative path
                exportDir.mkdir();
            }
            exportDir = new File(exportDir.getAbsolutePath() + File.separator + getId());
        }
        else{
            // repox default folder
            exportDir = new File(getOutputDir(), "export");
        }

        exportDir.mkdir();
        return exportDir.getAbsolutePath();
    }

    public int getNewTaskId() throws IOException {
        int nextId = getLastTaskId() + 1;
        setLastTaskId(nextId);
        return nextId;
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
            writer.write(new Integer(taskId).toString());
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
        AccessPoint defaultTimestampAP = new AccessPointTimestamp(DataManager.PREFIX_INTERNAL_BD + id +  DataManager.SUFIX_TIMESTAMP_INTERNAL_BD);
        defaultTimestampAP.setIndexDeletedRecords(false);
        defaultTimestampAP.setRepoxInternal(true);
        this.accessPoints.put(defaultTimestampAP.getId(), defaultTimestampAP);

        AccessPoint defaultRecordAP = new AccessPointRecordRepoxFull(DataManager.PREFIX_INTERNAL_BD + id + DataManager.SUFIX_RECORD_INTERNAL_BD);
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
                RepoxContextUtil.getRepoxManager().getAccessPointsManager().emptyIndex(this, accessPoint);
                log.info("Emptied AccessPoint with id " + accessPoint.getId());
            } catch(Exception e) {
                log.error("Unable to empty Table from Database: " + accessPoint.getId(), e);
                successfulDeletion = false;
            }
        }

        //Remove from Record Counts cache
        RepoxContextUtil.getRepoxManager().getRecordCountManager().removeDataSourceCounts(id);

        return successfulDeletion;
    }

    public abstract Task.Status ingestRecords(Request request ,File logFile, boolean fullIngest) throws Exception;

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
    public Date getSynchronizationDate() {
        try {
            String syncDate = getSynchronizationDateString();
            if(syncDate == null) {
                return null;
            }

            return DateUtil.string2Date(syncDate, TimeUtil.SHORT_DATE_FORMAT);
        } catch (Exception e) {
            log.warn("Unable to load file with Synchronization Date", e);
            return null;
        }
    }

    public String getSynchronizationDateString() throws IOException {
        File syncDateFile = getSyncDateFile();
        if(!syncDateFile.exists()) {
            return null;
        }
        return FileUtil.readFileToString(syncDateFile);
    }

    /**
     * Signals that a harvest of a Data Source finished.
     * RepoxManager will store synchronization dates
     * @param date
     * @throws Exception
     */
    public void signalSynchronizationFinished(Date date) throws IOException {
        File dataSourceSyncDateFile = getSyncDateFile();
        RepoxConfiguration configuration = RepoxContextUtil.getRepoxManager().getConfiguration();
        File syncDateFile = new File(configuration.getRepositoryPath(), "synchronization-date-global.txt");
        String dateAsString = DateUtil.date2String(date, TimeUtil.SHORT_DATE_FORMAT);
        FileUtil.writeToFile(dataSourceSyncDateFile, dateAsString);
        FileUtil.writeToFile(syncDateFile, dateAsString);
    }

    protected File getSyncDateFile() {
        return new File(getOutputDir(), "synchronization-date.txt");
    }




    /**
     * Obtains the current status of this DataSource
     */
    public String getStatusString() throws IOException {
        File statusFile = getDataSourceStatusFile();
        if(!statusFile.exists()) {
            return null;
        }
        return FileUtil.readFileToString(statusFile);
    }


    /**
     * Write the Data Source status information
     * RepoxManager will store data source status
     * @throws Exception
     */
    public void writeDataSourceStatus() {
        try {
            File dataSourceStatusFile = getDataSourceStatusFile();
            FileUtil.writeToFile(dataSourceStatusFile, getStatus().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected File getDataSourceStatusFile() {
        return new File(getOutputDir(), "dataSource-status.txt");
    }




    public void renameDataSourceDir(String oldDataSourceId, String newDataSourceId) throws IOException {
        File dataSourceDir = getDataSourceDir(oldDataSourceId);
        if(dataSourceDir.exists()) {
            dataSourceDir.renameTo(getDataSourceDir(newDataSourceId));
        }
    }

    public File getDataSourceDir() {
        return getDataSourceDir(id);
    }

    public File getDataSourceDir(String dataSourceId) {
        RepoxConfiguration configuration = RepoxContextUtil.getRepoxManager().getConfiguration();
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
    public void cleanUp() throws IOException {
        //Delete records indexes
        for(AccessPoint accessPoint : getAccessPoints().values()) {
            try {
                RepoxContextUtil.getRepoxManager().getAccessPointsManager().emptyIndex(this, accessPoint);
                log.info("Emptied AccessPoint with id " + accessPoint.getId());
            } catch(Exception e) {
                log.error("Unable to empty Table from Database: " + accessPoint.getId(), e);
            }
        }

        File dataSourceDir = getDataSourceDir();

        boolean deletedDir = true;
        if(dataSourceDir.exists()) {
            deletedDir = FileUtil.deleteDir(dataSourceDir);
            dataSourceDir.mkdir();
        }

        if(!deletedDir) {
            log.error("Unable to delete Data Source dir from Data Source with id " + id);
        }
        else {
            log.info("Deleted Data Source dir with success from Data Source with id " + id);
        }

        RepoxContextUtil.getRepoxManager().getRecordCountManager().removeDataSourceCounts(id);
    }


    public abstract DataSet addSpecificInformation(DataSet dataset);


    /**
     * Create an DataSet object, and add it to the database
     * @param repox2sip
     * @return
     * @throws eu.europeana.repox2sip.Repox2SipException
     */
    public DataSet addDataSource2Database(Repox2Sip repox2sip) throws Repox2SipException {
        DataSet dataSetSip = new DataSet();

        dataSetSip.setProvider(repox2sip.getProvider(getDataProvider().getIdDb()));
        dataSetSip.setNameCode(this.getNameCode());
        dataSetSip.setType(DataSetType.ESE);
        dataSetSip.setName(this.getName());
        dataSetSip.setDescription(this.getDescription());
        dataSetSip.setOaiSetOutput(this.getId());


        if(this.getRecordIdPolicy() instanceof IdExtracted){
            dataSetSip.setIdQName(((IdExtracted) this.getRecordIdPolicy()).getIdentifierXpath());
        }
        else{
            dataSetSip.setIdQName("Calculated by Repox");
        }

        addSpecificInformation(dataSetSip);

        dataSetSip = repox2sip.addDataSet(dataSetSip);
        this.setIdDb(dataSetSip.getId());

        return dataSetSip;
    }

    /**
     * Update DataSet in the database
     * @param repox2sip
     * @throws IOException
     * @throws Repox2SipException
     */
    public synchronized void updateDataSet2Database(Repox2Sip repox2sip) throws IOException, Repox2SipException {
        DataSet dataSet = repox2sip.getDataSet(this.getIdDb());
        dataSet.setNameCode(this.getNameCode());
        dataSet.setType(DataSetType.ESE);
        dataSet.setIdQName(this.getId());
        dataSet.setName(this.getName());
        dataSet.setDescription(this.getDescription());

        dataSet.setProvider(repox2sip.getProvider(getDataProvider().getIdDb()));

        if(this.getRecordIdPolicy() instanceof IdExtracted){
            dataSet.setIdQName(((IdExtracted) this.getRecordIdPolicy()).getIdentifierXpath());
        }
        else{
            dataSet.setIdQName("Calculated by Repox");
        }

        addSpecificInformation(dataSet);

        repox2sip.updateDataSet(dataSet);
    }


    /**
     * Delete DataSet from the database
     * @param repox2sip
     * @throws IOException
     * @throws eu.europeana.repox2sip.Repox2SipException
     */
    public synchronized void deleteDataSourceFromDatabase(Repox2Sip repox2sip) throws IOException, Repox2SipException {
        repox2sip.removeDataSet(repox2sip.getDataSet(this.getIdDb()));
    }


    /**
     * Add MDRecord to database
     * @param request
     * @param ingestedRecords
     */
    protected void addMdRecord2Database(Request request, List<RecordRepox> ingestedRecords){
        try{
            Repox2Sip repox2Sip = RepoxContextUtil.getRepoxManager().getDataManager().getRepox2sip();
            List<MetadataRecord> allMetadataRecords = new ArrayList<MetadataRecord>();
            for(RecordRepox rp : ingestedRecords){
                allMetadataRecords.add(rp.createRecordSip());
                //rp.addRecord2DataBase(request.getId(), repox2Sip);
            }
            repox2Sip.addMetadataRecords(request.getId(), allMetadataRecords);

        } catch (Repox2SipException e) {
            log.error("Could not add MetadataRecord to Data Base",e);
            e.printStackTrace();
        }
    }

    /**
     * TODO: We are unable to associate existing records to the new request -> ask Cesare and Nicola to solve the problem
     * Method used for the OAI-PHM requests, when REPOX detects that does not exist any changes or new records - it associate the last version of records to the new request
     * @param addedNewRecords
     * @param newRequest
     */
    protected void checkRequest(boolean addedNewRecords, Request newRequest){
        try{
            if(!addedNewRecords){
                Repox2Sip repox2sip = RepoxContextUtil.getRepoxManager().getDataManager().getRepox2sip();
                List<Request> requestList = repox2sip.getDataSetRequests(this.getIdDb());

                Collections.sort(requestList, new DateSorter());
                for (Request request : requestList) {
                    if(request.getStatus().equals(RequestStatus.IMPORT_COMPLETED)){
                        Long myId = request.getId();
                        repox2sip.addMetadataRecords(newRequest.getId(), repox2sip.getRequestMetadataRecords(myId));
                        break;
                    }
                }
            }
        }catch (Repox2SipException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static void main(String[] args)throws Exception{
        String smtpServer = "smtp.gmail.com";
        if(smtpServer == null || smtpServer.isEmpty()) {
            return;
        }

        String fromEmail = "gilberto.pedrosa@gmail.com";
        String recipientsEmail = "gilberto.pedrosa@ist.utl.pt";//"cesare.concordia@isti.cnr.it";
        String adminMailPass =  "xxxx";
        String smtpPort = "465";
        String subject = "REPOX Data Source ingesting finished. Exit status: ";

        EmailSender emailSender = new EmailSender();
        String pathIngestFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("ingest.html.ftl").getFile(), "ISO-8859-1");
        emailSender.setTemplate(pathIngestFile.substring(0, pathIngestFile.lastIndexOf("/")) + "/ingest");


        HashMap map = new HashMap<String, String>();
        map.put("exitStatus", "OK");
        map.put("id", "1111");

        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setUsername(fromEmail);
        mail.setPassword(adminMailPass);
        mail.setPort(Integer.valueOf(smtpPort));
        mail.setHost(smtpServer);

        Properties mailConnectionProperties = (Properties)System.getProperties().clone();


        mailConnectionProperties.put("mail.debug", "true");
        mailConnectionProperties.put("mail.smtp.auth", "true");
        //mailConnectionProperties.put("mail.smtp.socketFactory.port", smtpPort);
        mailConnectionProperties.put("mail.smtp.starttls.enable","true");
        mailConnectionProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailConnectionProperties.put("mail.smtp.socketFactory.fallback", "false");
        mail.setJavaMailProperties(mailConnectionProperties);

        emailSender.setMailSender(mail);
        emailSender.sendEmail(recipientsEmail, fromEmail, subject, map, "C:\\Users\\GPedrosa\\Desktop\\test.txt");
    }


}
