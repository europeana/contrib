/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.repox.marc;

import eu.europeana.definitions.domain.Language;
import eu.europeana.repox2sip.models.DataSet;
import eu.europeana.repox2sip.models.ImporterStrategy;
import eu.europeana.repox2sip.models.Request;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.FileExtractStrategy;
import pt.utl.ist.repox.data.dataSource.IdGenerated;
import pt.utl.ist.repox.data.dataSource.RecordIdPolicy;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.task.Task.Status;
import pt.utl.ist.repox.util.FileUtil;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataSourceDirectoryImporter extends DataSource {
    private static final Logger log = Logger.getLogger(DataSourceDirectoryImporter.class);

    private FileExtractStrategy extractStrategy;
    private CharacterEncoding characterEncoding;
    private String sourcesDirPath;
    private String URLSourcesPath;
    private String recordXPath;
    private Map<String, String> namespaces;


    public String getURLSourcesPath() {
        return URLSourcesPath;
    }

    public void setURLSourcesPath(String URLSourcesPath) {
        this.URLSourcesPath = URLSourcesPath;
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

    public DataSourceDirectoryImporter(DataProvider dataProvider, String id, String description, String metadataFormat,
                                       FileExtractStrategy extractStrategy, String sourcesDirPath, RecordIdPolicy recordIdPolicy,
                                       Map<String, MetadataTransformation> metadataTransformations, String recordXPath, Map<String, String> namespacesMap, String name, String nameCode, long idDb, String URLSourcesPath, String exportPath) {
        super(dataProvider, id, description, metadataFormat, recordIdPolicy, metadataTransformations, name, nameCode, idDb, exportPath);

        this.extractStrategy = extractStrategy;
        this.sourcesDirPath = sourcesDirPath;
        this.recordXPath = recordXPath;
        this.namespaces = namespacesMap;
        this.URLSourcesPath = URLSourcesPath;
    }

    public DataSourceDirectoryImporter(DataProvider dataProvider, String id, String description, String metadataFormat,
                                       FileExtractStrategy extractStrategy, CharacterEncoding characterEncoding, String sourcesDirPath,
                                       RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations, String recordXPath, Map<String, String> namespaces, String name, String nameCode, long idDb, String URLSourcesPath, String exportPath) {
        this(dataProvider, id, description, metadataFormat, extractStrategy, sourcesDirPath, recordIdPolicy, metadataTransformations, recordXPath, namespaces, name, nameCode, idDb,URLSourcesPath, exportPath);
        this.characterEncoding = characterEncoding;
    }

    @Override
    public Task.Status ingestRecords(Request request, File logFile, boolean fullIngest) throws IOException, DocumentException, SQLException {
        if(URLSourcesPath != null){
            updateURLFile();
        }
        System.out.println("INGESTING NOW - Directory Importer!");
        //boolean addedNewRecordsToSipDatabase = false;

        // Remove all records from IdGenerated because there is no version management or it is a full ingest
        if(this.getRecordIdPolicy() instanceof IdGenerated || fullIngest) {
            boolean successfulDeletion = emptyRecords();

            if(!successfulDeletion) {
                StringUtil.simpleLog("Importing aborted - unable to delete the current Records", this.getClass(), logFile);
                return Task.Status.FAILED;
            }
        }

        File sourcesDir = new File(sourcesDirPath);
        File[] changedFiles = FileUtil.getChangedFiles(null, sourcesDir.listFiles());

        StringUtil.simpleLog("Importing from directory: " + sourcesDirPath, this.getClass(), logFile);

        List<RecordRepox> batchRecords = new ArrayList<RecordRepox>();

        HashSet<Status> logStatus = new HashSet<Status>();

        for (File file : changedFiles) {
            if(stopExecution) {
                StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                return Task.Status.CANCELED;
            }

            if (file.exists() && file.isFile()) {
                StringUtil.simpleLog("Checking file: " + file.getName(), this.getClass(), logFile);

                if(file.getName().endsWith(".zip")) {
                    ZipInputStream in = new ZipInputStream(new FileInputStream(file));

                    ZipEntry entry;

                    TimeUtil.startTimers();

                    while((entry = in.getNextEntry()) != null) {
                        if(stopExecution) {
                            StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                            in.close();
                            return Task.Status.CANCELED;
                        }

                        TimeUtil.getTimeSinceLastTimerArray(0);

                        if(extractStrategy instanceof Iso2709FileExtract || entry.getName().toLowerCase().endsWith(".xml")) {
                            String outFilename = UUID.randomUUID().toString() + ".xml";
                            File tempDir =  RepoxContextUtil.getRepoxManager().getConfiguration().getTempDir();
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

                            RepoxRecordHandler repoxRecordHandler = new RepoxRecordHandler(batchRecords, logFile, request, unzippedFile);
                            try {
                                extractStrategy.iterateRecords(repoxRecordHandler, this, file, characterEncoding, logFile);
                                repoxRecordHandler.savePendingRecords();
                                if(stopExecution) {
                                    StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                    return Task.Status.CANCELED;
                                }
                            }
                            catch(Exception e) {
                                log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                                StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                            }
                            unzippedFile.delete();
                            logStatus.add(repoxRecordHandler.getIngestStatus());
                        }

                        log.debug("Total entry time: " + TimeUtil.getTimeSinceLastTimerArray(0));
                    }

                    log.debug("Total time: " + TimeUtil.getTotalTime());

                    in.close();
                }
                else {
                    RepoxRecordHandler repoxRecordHandler = new RepoxRecordHandler(batchRecords, logFile, request, file);
                    try {
                        extractStrategy.iterateRecords(repoxRecordHandler, this, file, characterEncoding, logFile);
                        repoxRecordHandler.savePendingRecords();
                        if(stopExecution) {
                            StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                            return Task.Status.CANCELED;
                        }
                    }
                    catch(Exception e) {
                        log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                        StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    }
                    logStatus.add(repoxRecordHandler.getIngestStatus());
                }
            }
        }

        /*
        // Import remaining records
        importBatchRecords(batchRecords, logFile);
        //add records to sip data base
        if(!batchRecords.isEmpty() && RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
            addMdRecord2Database(request,batchRecords);
            //addedNewRecordsToSipDatabase = true;
        }
        batchRecords.clear();

        //checkRequest(addedNewRecordsToSipDatabase, request);
        */

        if(logStatus.contains(Status.ERRORS))
            return Task.Status.ERRORS;

        return Task.Status.OK;
    }

    private void importBatchRecords(List<RecordRepox> batchRecords, File logFile) throws IOException, DocumentException, SQLException {
        long memBefore = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        TimeUtil.getTimeSinceLastTimerArray(9);

        RecordCountManager recordCountManager = RepoxContextUtil.getRepoxManager().getRecordCountManager();
        if(recordCountManager.getRecordCount(id) != null) {
            log.debug("[BEFORE] Count: " + recordCountManager.getRecordCount(id).getCount());
        }

        RepoxContextUtil.getRepoxManager().getAccessPointsManager().processRecords(this, batchRecords);

        if(recordCountManager.getRecordCount(id) != null) {
            log.debug("[AFTER]  count: " + recordCountManager.getRecordCount(id).getCount());
        }

        double importTime = TimeUtil.getTimeSinceLastTimerArray(9) / 1000.0;
        long memAfter = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        log.info(batchRecords.size() + " records imported in " + importTime + "s." +
                " Memory before/after (MB) : " + memBefore + "/"+ memAfter);
        StringUtil.simpleLog(batchRecords.size() + " records imported", this.getClass(), logFile);
    }

    @Override
    public boolean isWorking() {
        File sourcesDir = new File(sourcesDirPath);

        return (sourcesDir.isDirectory() && sourcesDir.exists());
    }



    public DataSet addSpecificInformation(DataSet dataset){
        String qName = this.getRecordXPath();
        if(qName!= null){
            dataset.setQName(qName);
        }
        else{
            dataset.setQName("/");
        }
        dataset.setStrategy(ImporterStrategy.get("DATA_SOURCE_DIRECTORY_IMPORTER"));
        return dataset;
    }

    public Request setRequestName(Request request){
        request.setName(sourcesDirPath);
        return request;
    }

    public DataSet createDataSetSip(){
        DataSet dataSet = new DataSet();

        dataSet.setName(this.getId());
        /*
        todo
        dataSet.setHomePage();
        dataSet.setLanguage();
        dataSet.setnameCode();
        dataSet.setProvider();
        dataSet.setQName();
        dataSet.setRequests();
        dataSet.setType();
        */

        return dataSet;
    }


    public void updateURLFile(){

        try {
            File tempFolder = new File(sourcesDirPath);
            tempFolder.mkdir();
            URL sourceURL = new URL(URLSourcesPath);
            InputStream inputStream = sourceURL.openStream();

            String OutputFileName =  URLSourcesPath.replaceAll("[:*<>|?]", "");
            OutputFileName = OutputFileName.substring(OutputFileName.lastIndexOf("/")+1);


            File outuputFile = new File(sourcesDirPath+"/"+OutputFileName);
            FileOutputStream outPutStream = new FileOutputStream(outuputFile);
            int c;
            while ((c = inputStream.read()) != -1) {
                outPutStream.write(c);
            }
            inputStream.close();
            outPutStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class RepoxRecordHandler implements FileExtractStrategy.RecordHandler{
        List<RecordRepox> batchRecords;
        File logFile;
        Request request;
        File file;

        Status ingestStatus;

        public RepoxRecordHandler(List<RecordRepox> batchRecords, File logFile, Request request, File file) {
            this.batchRecords = batchRecords;
            this.logFile = logFile;
            this.request = request;
            this.file = file;
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
                if(batchRecords.size() >= RECORDS_BATCH_SIZE) {
                    importBatchRecords(batchRecords, logFile);
                    //add records to sip database
                    if(!batchRecords.isEmpty() && RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                        addMdRecord2Database(request,batchRecords);
                        //addedNewRecordsToSipDatabase = true;
                    }
                    batchRecords.clear();
                }
                ingestStatus = Task.Status.OK;
            }
            catch(Exception e) {
                ingestStatus = Task.Status.ERRORS;
                log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                ingestStatus = Task.Status.ERRORS;
            }
        }

        public void savePendingRecords(){
            if(stopExecution) {
                return;
            }

            try {
                if(batchRecords.size() > 0) {
                    importBatchRecords(batchRecords, logFile);
                    //add records to sip database
                    if(!batchRecords.isEmpty() && RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                        addMdRecord2Database(request,batchRecords);
                        //addedNewRecordsToSipDatabase = true;
                    }
                    batchRecords.clear();
                }
                ingestStatus = Task.Status.OK;
            }
            catch(Exception e) {
                ingestStatus = Task.Status.ERRORS;
                log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                ingestStatus = Task.Status.ERRORS;
            }
        }

    }

}
