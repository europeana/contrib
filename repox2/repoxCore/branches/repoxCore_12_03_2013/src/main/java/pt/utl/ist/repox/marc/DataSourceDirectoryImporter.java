/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.repox.marc;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.*;
import pt.utl.ist.repox.ftp.DataSourceFtp;
import pt.utl.ist.repox.http.DataSourceHttp;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.statistics.RecordCount;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.task.Task.Status;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.FileUtil;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.util.DateUtil;

import java.io.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataSourceDirectoryImporter extends DataSource {
    private static final Logger log = Logger.getLogger(DataSourceDirectoryImporter.class);

    private FileExtractStrategy extractStrategy;
    private FileRetrieveStrategy retrieveStrategy;
    private CharacterEncoding characterEncoding;
    private String sourcesDirPath;
    private String recordXPath;
    private Map<String, String> namespaces;
    private String idTypePolicy;

    public String getIdTypePolicy() {
        return idTypePolicy;
    }

    public void setIdTypePolicy(String idTypePolicy) {
        this.idTypePolicy = idTypePolicy;
    }

    public FileRetrieveStrategy getRetrieveStrategy() {
        return retrieveStrategy;
    }

    public void setRetrieveStrategy(FileRetrieveStrategy retrieveStrategy) {
        this.retrieveStrategy = retrieveStrategy;
    }

    public FileExtractStrategy getExtractStrategy() {
        return extractStrategy;
    }

    public void setExtractStrategy(FileExtractStrategy extractStrategy) {
        this.extractStrategy = extractStrategy;
    }

    public CharacterEncoding getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(CharacterEncoding characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public String getSourcesDirPath() {
        return sourcesDirPath;
    }

    public void setSourcesDirPath(String sourcesDirPath) {
        this.sourcesDirPath = sourcesDirPath;
    }

    public String getRecordXPath() {
        return recordXPath;
    }

    public void setRecordXPath(String recordXPath) {
        this.recordXPath = recordXPath;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public DataSourceDirectoryImporter() {
        super();
    }

    public DataSourceDirectoryImporter(DataProvider dataProvider, String id, String description, String schema, String namespace, String metadataFormat,
                                       FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy, CharacterEncoding characterEncoding, String sourcesDirPath,
                                       RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations, String recordXPath, Map<String, String> namespacesMap) {
        super(dataProvider, id, description, schema, namespace, metadataFormat, recordIdPolicy, metadataTransformations);
        this.characterEncoding = characterEncoding;
        this.extractStrategy = extractStrategy;
        this.sourcesDirPath = sourcesDirPath;
        this.recordXPath = recordXPath;
        this.namespaces = namespacesMap;
        this.retrieveStrategy = retrieveStrategy;
    }


    @Override
    public Status ingestRecords(File logFile, boolean fullIngest) throws IOException, DocumentException, SQLException {
        System.out.println("INGESTING NOW - Directory Importer!");
        Status ingestStatus = Status.OK;

        if(retrieveStrategy instanceof DataSourceHttp){
            System.out.println("INGESTING NOW - Retrieving Files from HTTP...");
            retrieveStrategy.retrieveFiles(getId());
        }
        else if(retrieveStrategy instanceof DataSourceFtp){
            System.out.println("INGESTING NOW - Retrieving Files from FTP...");
            retrieveStrategy.retrieveFiles(getId());
        }

        // Remove all records from IdGenerated because there is no version management or it is a full ingest
        if(this.getRecordIdPolicy() instanceof IdGenerated || fullIngest) {
            boolean successfulDeletion = emptyRecords();

            if(!successfulDeletion) {
                StringUtil.simpleLog("Importing aborted - unable to delete the current Records", this.getClass(), logFile);
                return Status.FAILED;
            }

            //Clear the last ingest date
            setLastUpdate(null);

            //Update the XML file
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        }
        else{
            //if there is a previous successful harvest and has finished
            RecordCount recordCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, true);
            if(recordCount != null && recordCount.getCount() > 0 && getLastUpdate() != null) {
                String syncDateString = DateUtil.date2String(getLastUpdate(), TimeUtil.SHORT_DATE_FORMAT);
                StringUtil.simpleLog("Directory Importer harvest from date: " + syncDateString, this.getClass(), logFile);
            }
        }

        File sourcesDir = new File(sourcesDirPath);
        File[] changedFiles = FileUtil.getChangedFiles(getLastUpdate(), sourcesDir.listFiles());

        StringUtil.simpleLog("Importing from directory: " + sourcesDirPath, this.getClass(), logFile);

        List<RecordRepox> batchRecords = new ArrayList<RecordRepox>();

        /*
        //todo
            long startTime = (new Date()).getTime();
            long totalTime = ((new Date()).getTime() - startTime ) / 1000;
            statisticsHarvester.add(totalTime);
        */

        int totalRecords = 0;
        for (File file : changedFiles) {
            if(stopExecution) {
                if(forceStopExecution){
                    return Task.Status.FORCE_EMPTY;
                }
                StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                return Status.CANCELED;
            }

            if(file.exists() && file.isFile()) {
                StringUtil.simpleLog("Checking file: " + file.getName(), this.getClass(), logFile);

                if(file.getName().endsWith(".zip")) {
                    // zip file
                    ZipInputStream in = null;
                    try {
                        in = new ZipInputStream(new FileInputStream(file));
                        ZipEntry entry;
                        TimeUtil.startTimers();

                        // Folders inside the zip file with special characters in the name are not allowed
                        while((entry = in.getNextEntry()) != null) {
                            if(stopExecution) {
                                if(forceStopExecution){
                                    return Task.Status.FORCE_EMPTY;
                                }
                                StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                in.close();
                                return Status.CANCELED;
                            }

                            TimeUtil.getTimeSinceLastTimerArray(0);

                            if(extractStrategy instanceof Iso2709FileExtract || entry.getName().toLowerCase().endsWith(".xml")) {
                                String outFilename = UUID.randomUUID().toString() + ".xml";
                                File tempDir =  ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getTempDir();
                                File unzippedFile = new File(tempDir, outFilename);
                                StringUtil.simpleLog("Importing zip entry: " + entry.getName(), this.getClass(), logFile);

                                // Open the output file
                                OutputStream out = new FileOutputStream(unzippedFile);

                                // Transfer bytes from the ZIP file to the output file
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }

                                // Close the streams
                                out.close();

                                TimeUtil.getTimeSinceLastTimerArray(1);

                                RepoxRecordHandler repoxRecordHandler = new RepoxRecordHandler(batchRecords, logFile, unzippedFile, totalRecords);
                                try {
                                    extractStrategy.iterateRecords(repoxRecordHandler, this, unzippedFile, characterEncoding, logFile);
                                    repoxRecordHandler.savePendingRecords();

                                    totalRecords = repoxRecordHandler.countTotalRecords;

                                    if(stopExecution && maxRecord4Sample != -1 && maxRecord4Sample <= totalRecords) {
                                        // stop execution created by ingest source code (activated when has the sample's number records)
                                        return Status.OK;
                                    }
                                    else if(stopExecution) {
                                        // stop forced by REPOX user (tasks)
                                        if(forceStopExecution){
                                            return Task.Status.FORCE_EMPTY;
                                        }
                                        StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                        return Status.CANCELED;
                                    }
                                }
                                catch(Exception e) {
                                    log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                                    StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                                }

                                unzippedFile.delete();
                            }

                            log.debug("Total entry time: " + TimeUtil.getTimeSinceLastTimerArray(0));
                        }

                        log.debug("Total time: " + TimeUtil.getTotalTime());

                    } catch (FileNotFoundException e) {
                        StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    }
                    finally {
                        in.close();
                    }
                }
                else {
                    RepoxRecordHandler repoxRecordHandler = new RepoxRecordHandler(batchRecords, logFile, file, totalRecords);
                    try {
                        extractStrategy.iterateRecords(repoxRecordHandler, this, file, characterEncoding, logFile);
                        repoxRecordHandler.savePendingRecords();

                        totalRecords = repoxRecordHandler.countTotalRecords;

                        if(stopExecution && maxRecord4Sample != -1 && maxRecord4Sample <= totalRecords) {
                            // stop execution created by ingest source code (activated when has the sample's number records)
                            return Status.OK;
                        }
                        else if(stopExecution) {
                            // stop forced by REPOX user (tasks)
                            if(forceStopExecution){
                                return Task.Status.FORCE_EMPTY;
                            }
                            StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                            return Status.CANCELED;
                        }
                    }
                    catch(Exception e) {
                        log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                        StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    }
                }
            }
        }

        // Import remaining records
        importBatchRecords(batchRecords, logFile);
        return ingestStatus;
    }

    protected void importBatchRecords(List<RecordRepox> batchRecords, File logFile) throws IOException, DocumentException, SQLException {
        //long memBefore = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        TimeUtil.getTimeSinceLastTimerArray(9);

        RecordCountManager recordCountManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager();
        if(recordCountManager.getRecordCount(id) != null) {
            log.debug("[BEFORE] Count: " + recordCountManager.getRecordCount(id).getCount());
        }


        // to avoid duplicates
        Map<String, RecordRepox> batchRecordsWithoutDuplicates = new HashMap<String, RecordRepox>();
        for (RecordRepox record : batchRecords) {
            batchRecordsWithoutDuplicates.put(record.getId().toString(), record);
        }
        batchRecords = new ArrayList<RecordRepox>(batchRecordsWithoutDuplicates.values());

        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().processRecords(this, batchRecords);

        if(recordCountManager.getRecordCount(id) != null) {
            log.debug("[AFTER]  count: " + recordCountManager.getRecordCount(id).getCount());
        }

        /*double importTime = TimeUtil.getTimeSinceLastTimerArray(9) / 1000.0;
        long memAfter = Runtime.getRuntime().totalMemory() / (1024 * 1024);
*/
        if(batchRecords.size() != 0){
            /*          log.info(batchRecords.size() + " records imported in " + importTime + "s." +
  " Memory before/after (MB) : " + memBefore + "/"+ memAfter);*/
            StringUtil.simpleLog(batchRecords.size() + " records imported", this.getClass(), logFile);
        }
    }

    @Override
    public boolean isWorking() {
        File sourcesDir = new File(sourcesDirPath);

        return (sourcesDir.isDirectory() && sourcesDir.exists());
    }

    @Override
    public Element addSpecificInfo(Element sourceNode) {
        sourceNode.addAttribute("type", "DataSourceDirectoryImporter");
        sourceNode.addElement("sourcesDirPath").setText(getSourcesDirPath());

        Element extractStrategyNode = sourceNode.addElement("retrieveStrategy");

        if(getRetrieveStrategy() instanceof DataSourceFtp){
            DataSourceFtp dataSourceFtp = (DataSourceFtp) getRetrieveStrategy();
            extractStrategyNode.addAttribute("type", DataSourceFtp.class.getName());
            extractStrategyNode.addElement("server").setText(dataSourceFtp.getServer());
            if(dataSourceFtp.getIdTypeAccess().equals(DataSourceFtp.NORMAL) && !dataSourceFtp.getIdTypeAccess().isEmpty()) {
                extractStrategyNode.addElement("user").setText((dataSourceFtp.getUser() != null ? dataSourceFtp.getUser() : ""));
                extractStrategyNode.addElement("password").setText(dataSourceFtp.getPassword() != null ? dataSourceFtp.getPassword() : "");
            }
            extractStrategyNode.addElement("folderPath").setText(dataSourceFtp.getFtpPath());
        }
        else if(getRetrieveStrategy() instanceof DataSourceHttp){
            DataSourceHttp dataSourceHttp = (DataSourceHttp) getRetrieveStrategy();
            extractStrategyNode.addAttribute("type", DataSourceHttp.class.getName());
            extractStrategyNode.addElement("url").setText(dataSourceHttp.getUrl());
        }
        else if(getRetrieveStrategy() instanceof DataSourceFolder){
            extractStrategyNode.addAttribute("type", DataSourceFolder.class.getName());
        }


        if(getExtractStrategy() instanceof Iso2709FileExtract) {
            Iso2709FileExtract extractStrategy = (Iso2709FileExtract) getExtractStrategy();
            sourceNode.addAttribute("isoImplementationClass", extractStrategy.getIsoImplementationClass().toString());
            sourceNode.addAttribute("characterEncoding", getCharacterEncoding().toString());
            sourceNode.addElement("fileExtract").setText(Iso2709FileExtract.class.getSimpleName());
        }
        else if(getExtractStrategy() instanceof MarcXchangeFileExtract) {
            sourceNode.addElement("fileExtract").setText(MarcXchangeFileExtract.class.getSimpleName());
        }
        else if(getExtractStrategy() instanceof SimpleFileExtract) {
            sourceNode.addElement("fileExtract").setText(SimpleFileExtract.class.getSimpleName());

            if(getRecordXPath() != null){
                Element splitRecords = sourceNode.addElement("splitRecords");
                splitRecords.addElement("recordXPath").setText(getRecordXPath());

                if(getNamespaces() != null && getNamespaces().size() > 0){
                    Element namespacesElement = splitRecords.addElement("namespaces");

                    //System.out.println("currentDataSource.getNamespaces() = " + currentDataSource.getNamespaces());

                    for(String currentKey : getNamespaces().keySet()) {
                        Element namespaceElement = namespacesElement.addElement("namespace");
                        namespaceElement.addElement("namespacePrefix").setText(currentKey);
                        namespaceElement.addElement("namespaceUri").setText(getNamespaces().get(currentKey));
                    }
                }
            }
        }
        return sourceNode;
    }

    class RepoxRecordHandler implements FileExtractStrategy.RecordHandler{
        List<RecordRepox> batchRecords;
        File logFile;
        File file;
        int countTotalRecords;
        Status ingestStatus;
        long startTime;

        public RepoxRecordHandler(List<RecordRepox> batchRecords, File logFile, File file, int actualNumber) {
            this.batchRecords = batchRecords;
            this.logFile = logFile;
            this.file = file;
            this.countTotalRecords = actualNumber;
            startTime = (new Date()).getTime();
        }

        public Status getIngestStatus() {
            return ingestStatus;
        }

        public void setIngestStatus(Status ingestStatus) {
            this.ingestStatus = ingestStatus;
        }

        public void handleRecord(RecordRepox record){
            if(stopExecution) {
                return;
            }

            try {
                batchRecords.add(record);
                if(maxRecord4Sample == -1 && batchRecords.size() >= RECORDS_BATCH_SIZE ) {
                    importBatchRecords(batchRecords, logFile);
                    countTotalRecords = countTotalRecords + batchRecords.size();
                    long totalTime = ((new Date()).getTime() - startTime ) / 1000;
                    statisticsHarvester.add(totalTime);
                    startTime = (new Date()).getTime();
                    batchRecords.clear();
                }
                else if(maxRecord4Sample != -1 && maxRecord4Sample <= (countTotalRecords + batchRecords.size())){
                    // test used when REPOX is getting a sample and the XML file has more records than the maxRecord4Sample
                    importBatchRecords(batchRecords, logFile);
                    StringUtil.simpleLog("Stop signal received. Sample set: max records number.", this.getClass(), logFile);
                    countTotalRecords = countTotalRecords + batchRecords.size();
                    stopExecution = true;
                }

                ingestStatus = Status.OK;
            }
            catch(Exception e) {
                if(stopExecution) {
                    if(forceStopExecution){
                        ingestStatus = Task.Status.FORCE_EMPTY;
                    }
                    StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                    ingestStatus = Task.Status.CANCELED;
                }
                else{
                    log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                    StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    ingestStatus = Status.ERRORS;
                }
            }
        }

        public void savePendingRecords(){
            if(stopExecution) {
                return;
            }

            try {
                if(batchRecords.size() > 0) {
                    importBatchRecords(batchRecords, logFile);
                    countTotalRecords = countTotalRecords + batchRecords.size();
                    batchRecords.clear();
                }
                ingestStatus = Status.OK;
            }
            catch(Exception e) {
                if(stopExecution) {
                    if(forceStopExecution){
                        ingestStatus = Task.Status.FORCE_EMPTY;
                    }
                    StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                    ingestStatus = Task.Status.CANCELED;
                }
                else{
                    log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                    StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    ingestStatus = Status.ERRORS;
                }
            }
        }

        public int getCountTotalRecords() {
            return countTotalRecords;
        }

        public void setCountTotalRecords(int countTotalRecords) {
            this.countTotalRecords = countTotalRecords;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
    }

    @Override
    public int getTotalRecords2Harvest(){
        return new File(sourcesDirPath).listFiles().length;
    }

    @Override
    public String getNumberOfRecords2HarvestStr() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
        return numberFormat.format(new File(sourcesDirPath).listFiles().length);
//        return String.valueOf(new File(sourcesDirPath).listFiles().length);
    }

    @Override
    public int getRecordsPerResponse() {
        return RECORDS_BATCH_SIZE;
    }

    @Override
    public ArrayList<Long> getStatisticsHarvester() {
        return statisticsHarvester;
    }
}
