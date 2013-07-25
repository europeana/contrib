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
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataSourceDirectoryImporter extends DataSource {
    private static final Logger log = Logger.getLogger(DataSourceDirectoryImporter.class);

    private FileExtractStrategy extractStrategy;
    private CharacterEncoding characterEncoding;
    private String sourcesDirPath;
    private String recordXPath;
    private Map<String, String> namespaces;

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
                                       Map<String, MetadataTransformation> metadataTransformations, String recordXPath, Map<String, String> namespacesMap, Language language, String name, String nameCode, long idDb) {
        super(dataProvider, id, description, metadataFormat, recordIdPolicy, metadataTransformations, language, name, nameCode, idDb);

        this.extractStrategy = extractStrategy;
        this.sourcesDirPath = sourcesDirPath;
        this.recordXPath = recordXPath;
        this.namespaces = namespacesMap;
    }

    public DataSourceDirectoryImporter(DataProvider dataProvider, String id, String description, String metadataFormat,
                                       FileExtractStrategy extractStrategy, CharacterEncoding characterEncoding, String sourcesDirPath,
                                       RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations, String recordXPath, Map<String, String> namespaces, Language language, String name, String nameCode, long idDb) {
        this(dataProvider, id, description, metadataFormat, extractStrategy, sourcesDirPath, recordIdPolicy, metadataTransformations, recordXPath, namespaces, language, name, nameCode, idDb);
        this.characterEncoding = characterEncoding;
    }

    @Override
    public Task.Status ingestRecords(Request request ,File logFile, boolean fullIngest) throws IOException, DocumentException, SQLException {
        System.out.println("INGESTING NOW - Directoty Importer!");
        boolean addedNewRecordsToSipDatabase = false;
        Status ingestStatus = Task.Status.OK;

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

                            try {
                                TimeUtil.getTimeSinceLastTimerArray(1);
                                Iterator<RecordRepox> recordIterator = extractStrategy.getIterator(this, unzippedFile,
                                        characterEncoding, logFile);
                                while(recordIterator.hasNext()) {
                                    if(stopExecution) {
                                        StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                        unzippedFile.delete();
                                        in.close();
                                        return Task.Status.CANCELED;
                                    }

                                    batchRecords.add(recordIterator.next());
                                    if(batchRecords.size() >= RECORDS_BATCH_SIZE) {
                                        importBatchRecords(batchRecords, logFile);
                                        //add records to sip data base
                                        if(!batchRecords.isEmpty() && RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                                            addMdRecord2Database(request, batchRecords);
                                            addedNewRecordsToSipDatabase = true;
                                        }
                                        batchRecords = new ArrayList<RecordRepox>();
                                    }
                                }

                            }
                            catch(Exception e) {
                                ingestStatus = Task.Status.ERRORS;
                                log.error("Error importing zip file " + file.getAbsolutePath() + " in entry " + entry.getName() + ": " + e.getMessage(), e);
                                StringUtil.simpleLog("Error importing zip file " + file.getAbsolutePath()
                                        + " in entry " + entry.getName() + ": " + e.getMessage(), this.getClass(), logFile);
                            }

                            unzippedFile.delete();
                        }

                        log.debug("Total entry time: " + TimeUtil.getTimeSinceLastTimerArray(0));
                    }

                    log.debug("Total time: " + TimeUtil.getTotalTime());

                    in.close();
                }
                else {
                    try {
                        Iterator<RecordRepox> recordIterator = extractStrategy.getIterator(this, file, characterEncoding, logFile);
                        while (recordIterator.hasNext()) {
                            if(stopExecution) {
                                StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                return Task.Status.CANCELED;
                            }

                            batchRecords.add(recordIterator.next());
                            if(batchRecords.size() >= RECORDS_BATCH_SIZE) {
                                importBatchRecords(batchRecords, logFile);
                                //add records to sip data base
                                if(!batchRecords.isEmpty() && RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                                    addMdRecord2Database(request,batchRecords);
                                    addedNewRecordsToSipDatabase = true;
                                }
                                batchRecords = new ArrayList<RecordRepox>();
                            }
                        }

                    }
                    catch(Exception e) {
                        ingestStatus = Task.Status.ERRORS;
                        log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                        StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    }
                }
            }
        }

        // Import remaining records
        importBatchRecords(batchRecords, logFile);
        //add records to sip data base
        if(!batchRecords.isEmpty() && RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
            addMdRecord2Database(request,batchRecords);
            addedNewRecordsToSipDatabase = true;
        }
        batchRecords = new ArrayList<RecordRepox>();

        //checkRequest(addedNewRecordsToSipDatabase , request);
        return ingestStatus ;
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
}
