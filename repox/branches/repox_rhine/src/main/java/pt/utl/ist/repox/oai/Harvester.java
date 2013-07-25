package pt.utl.ist.repox.oai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;


import org.apache.log4j.Logger;
import org.oclc.oai.harvester2.verb.ListRecords;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.xml.sax.SAXParseException;
import pt.utl.ist.repox.RunnableStoppable;
import pt.utl.ist.repox.util.FileUtil;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.StringUtil;

public class Harvester implements RunnableStoppable {
    private static final Logger log = Logger.getLogger(Harvester.class);
    private static final int SIZE_HTTP_PROTOCOL = 7; // http://
    private static final int MAX_OAI_VERB_RETRIES= 3;
    private static final String SERVERS_FILENAME = "servers.txt"; //tab separated file with the servers
    private static final String OAI_DATE_FORMAT = "yyyy-MM-dd";

    public static final String REQUEST_FILENAME_START = "recordsRequest";

    private boolean stopExecution = false;
    private String sourceUrl;
    private String sourceSet;
//	  YYYY = four-digit year ; MM = two-digit month (01=January, etc.) ; DD = two-digit day of month (01 through 31)
    private String fromDateString; 			//Date format: YYYY-MM-DD
    private String untilDateString;			//Date format: YYYY-MM-DD
    private ResponseTransformer responseTransformer;
    private String metadataFormat;
    private File logFile;

    private String outputDirname;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceSet() {
        return sourceSet;
    }

    public void setSourceSet(String sourceSet) {
        this.sourceSet = sourceSet;
    }

    public String getFromDateString() {
        return fromDateString;
    }

    public void setFromDateString(String fromDateString) {
        this.fromDateString = fromDateString;
    }

    public String getUntilDateString() {
        return untilDateString;
    }

    public void setUntilDateString(String untilDateString) {
        this.untilDateString = untilDateString;
    }

    public ResponseTransformer getResponseTransformer() {
        return responseTransformer;
    }

    public void setResponseTransformer(ResponseTransformer responseTransformer) {
        this.responseTransformer = responseTransformer;
    }

    public String getMetadataFormat() {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat) {
        this.metadataFormat = metadataFormat;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    /**
     * Creates a OAI-PMH Harvester that will harvest a pair of URL/set
     * for records that where changed between fromDate and untilDate, if defined. The resulting requests
     * are stored in outputBaseDir.
     *
     * @param sourceUrl OAI-PMH source URL
     * @param sourceSet OAI-PMH set name
     * @param fromDate only request Records changed after this date, if null is ignored
     * @param untilDate only request Records changed before this date, if null is ignored
     * @throws TransformerConfigurationException
     */
    public Harvester(String sourceUrl, String sourceSet, Date fromDate, Date untilDate,
                     String metadataFormat, File logFile) throws TransformerConfigurationException {
        super();

        SimpleDateFormat format = new SimpleDateFormat(OAI_DATE_FORMAT);
        if(fromDate != null) {
            fromDateString = format.format(fromDate);
        }
        if(untilDate != null) {
            untilDateString = format.format(untilDate);
        }

        this.sourceUrl = sourceUrl;
        this.sourceSet= sourceSet;
        this.responseTransformer = new ResponseTransformer();
        this.metadataFormat = metadataFormat;
        this.logFile = logFile;

        this.outputDirname = getOutputDirPath(sourceUrl, sourceSet);
    }

    public static String getOutputDirPath(String url, String set) {
        String oaiRequestPath = RepoxContextUtil.getRepoxManager().getConfiguration().getOaiRequestPath();

        int endIndex = (url.indexOf("/", SIZE_HTTP_PROTOCOL) > 0 ? url.indexOf("/", SIZE_HTTP_PROTOCOL) : url.length());
        String setName = (set != null ? set : "ALL");
        String outputDirString = oaiRequestPath
                + File.separator + FileUtil.sanitizeToValidFilename(url.substring(SIZE_HTTP_PROTOCOL, endIndex))
                + "-" + FileUtil.sanitizeToValidFilename(setName);

        return outputDirString;
    }

    public void stop() {
        stopExecution = true;
    }

    public void run() {
        stopExecution = false;

        try {
            if(logFile == null || (!logFile.exists() && !logFile.createNewFile())) {
                throw new IOException("Unable to create log file: " + logFile.getAbsolutePath());
            }

            StringUtil.simpleLog("Starting OAI Harvest URL: " + sourceUrl + " - Set:" + sourceSet, this.getClass(), logFile);

            String resumptionTokenPath = outputDirname + "/resumptionToken.txt";

            if(isHarvestFinished()) {
                StringUtil.simpleLog("Harvest finished, skipping source", this.getClass(), logFile);
                return;
            }

            File outputDir = new File(outputDirname);
            outputDir.mkdir();

            int currentRequest;
            ListRecords listRecords;
            File resumptionTokenFile = new File(resumptionTokenPath);
            if(resumptionTokenFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(resumptionTokenFile));
                currentRequest = Integer.parseInt(reader.readLine());
                String resumptionToken = reader.readLine();
                reader.close();

                StringUtil.simpleLog("Using previous resumption token: " + resumptionToken, this.getClass(), logFile);

                listRecords = getListRecordsWithRetries(resumptionToken, MAX_OAI_VERB_RETRIES);
            }
            else {
                currentRequest = 1;
                StringUtil.simpleLog(this.toString(), this.getClass(), logFile);
                listRecords = getListRecordsWithRetries(MAX_OAI_VERB_RETRIES);
            }

            while (listRecords != null) {
                if(stopExecution) {
                    StringUtil.simpleLog("Stop signal received. Exiting harvest.", this.getClass(), logFile);
                    return;
                }

                long startTime = (new Date()).getTime();

                if(listRecords.isResultEmpty()) {
                    StringUtil.simpleLog("Response was an empty list in operation ListRecords (may be invalid set)", this.getClass(), logFile);
                }

                NodeList errors = listRecords.getErrors();
                if(errors != null && errors.getLength() > 0) {
                    processErrors(errors, logFile, listRecords);
                    if(isBadResumptionToken(errors)) { // bad resumptionToken, restarting
                        listRecords = getListRecordsWithRetries(MAX_OAI_VERB_RETRIES);
                    }
                    else {
                        break;
                    }
                }

                writeRequest(currentRequest, listRecords);

                String resumptionToken = listRecords.getResumptionToken();

                if (resumptionToken == null || resumptionToken.length() == 0) {
                    StringUtil.simpleLog("Harvest finished - number of requests: " + currentRequest, this.getClass(), logFile);
                    OutputStream harvestFinishedOutputStream = new FileOutputStream(getHarvestFinishedFile());
                    harvestFinishedOutputStream.write(("Harvest Finished.").getBytes("UTF-8"));
                    harvestFinishedOutputStream.close();

                    listRecords = null;
                } else {
                    StringUtil.simpleLog("ResumptionToken: " + resumptionToken, this.getClass(), logFile);
                    OutputStream resumptionOutputStream = new FileOutputStream(resumptionTokenPath);
                    resumptionOutputStream.write((currentRequest + "\n").getBytes("UTF-8"));
                    resumptionOutputStream.write(resumptionToken.getBytes("UTF-8"));
                    resumptionOutputStream.close();

                    listRecords = getListRecordsWithRetries(resumptionToken, MAX_OAI_VERB_RETRIES);
                }

                long totalTime = ((new Date()).getTime() - startTime ) / 1000;
                StringUtil.simpleLog("End of request - time: " + totalTime + "s", this.getClass(), logFile);

                currentRequest++;
            }

            StringUtil.simpleLog("Finished OAI Harvest URL: " + sourceUrl + " - Set:" + sourceSet, this.getClass(), logFile);

        }
        catch (SAXParseException e){
            StringUtil.simpleLog("Harvest ABORTED: SAXParseException.", this.getClass(), logFile);
            log.error("Error harvesting: SAXParseException.");
        }
        catch(Exception e) {
            log.error("Error harvesting: " + e.getMessage(), e);
        }
    }

    public boolean isHarvestFinished() {
        return getHarvestFinishedFile().exists();
    }

    public File getHarvestFinishedFile() {
        File harvestFinishedFile = new File(outputDirname + "/harvestFinished.txt");

        return harvestFinishedFile;
    }

    private ListRecords getListRecordsWithRetries(String resumptionToken, int retries) throws IOException, ParserConfigurationException,
            SAXException, TransformerException, InterruptedException {
        try {
            return new ListRecords(sourceUrl, resumptionToken);
        }
        catch(FileNotFoundException e) { //This is the error returned by a 404
            StringUtil.simpleLog("Error 404 harvesting " + sourceUrl + " - "  + e.getMessage(), this.getClass(), logFile);
            if(retries > 0) {
                int currentRetry = MAX_OAI_VERB_RETRIES - retries + 1;
                long sleepTime = currentRetry * 10 * 1000;
                StringUtil.simpleLog("Retrying ListRecords - RETRY " + currentRetry + "/" + MAX_OAI_VERB_RETRIES
                        + " sleeping for " + sleepTime + " ms", this.getClass(), logFile);
                Thread.sleep(sleepTime);
                return getListRecordsWithRetries(resumptionToken, retries -1);
            }
            else {
                StringUtil.simpleLog("Harvest ABORTED: exceeded " + MAX_OAI_VERB_RETRIES + " retries", this.getClass(), logFile);
                throw e;
            }
        }
    }

    private ListRecords getListRecordsWithRetries(int retries)
            throws IOException, ParserConfigurationException, SAXException, TransformerException, InterruptedException {
        try {
            ListRecords listRecords = new ListRecords(sourceUrl, fromDateString, untilDateString, sourceSet, metadataFormat);
            return listRecords;
        }
        catch(FileNotFoundException e) { //This is the error returned by a 404
            StringUtil.simpleLog("Error 404 harvesting " + sourceUrl + " - "  + e.getMessage(), this.getClass(), logFile);
            if(retries > 0) {
                int currentRetry = MAX_OAI_VERB_RETRIES - retries + 1;
                long sleepTime = currentRetry * 10 * 1000;
                StringUtil.simpleLog("Retrying ListRecords - RETRY " + currentRetry + "/" + MAX_OAI_VERB_RETRIES
                        + " sleeping for " + sleepTime + " ms", this.getClass(), logFile);
                Thread.sleep(sleepTime);
                return getListRecordsWithRetries(retries - 1);
            }
            else {
                StringUtil.simpleLog("Harvest ABORTED: exceeded " + MAX_OAI_VERB_RETRIES + " retries", this.getClass(), logFile);
                throw e;
            }
        }
    }

    private void writeRequest(int currentRequest, ListRecords listRecords) throws IOException {
        File outputOAIResponseFile = getRequestFile(currentRequest);
        OutputStream outputStream = new FileOutputStream(outputOAIResponseFile);
        outputStream.write(listRecords.toString().getBytes("UTF-8"));
        outputStream.write("\n".getBytes("UTF-8"));
        outputStream.close();
    }

    public File getRequestFile(int request) {
        return new File(outputDirname + "/" + REQUEST_FILENAME_START + "-" + request + ".xml");
    }

    private void processErrors(NodeList errors, File logFile, ListRecords listRecords) throws IOException {
        writeRequest(-1, listRecords); // Write the error as identifier -1 to be read later
        StringUtil.simpleLog("Found errors in operation ListRecords, recorded to file " + getRequestFile(-1).getAbsolutePath(), this.getClass(), logFile);
        int length = errors.getLength();
        for (int i=0; i < length; ++i) {
            Node item = errors.item(i);
            StringUtil.simpleLog("ERROR: " + item.getNodeValue(), this.getClass(), logFile);
        }
    }

    private boolean isBadResumptionToken(NodeList errors) {
        int length = errors.getLength();
        for (int i=0; i < length; ++i) {
            Node item = errors.item(i);
            if(item.getAttributes() != null
                    && item.getAttributes().getNamedItem("code") != null
                    && item.getAttributes().getNamedItem("code").getNodeValue() != null
                    && item.getAttributes().getNamedItem("code").getNodeValue().equals("badResumptionToken")) {
                StringUtil.simpleLog("badResumptionToken in operation ListRecords, restarting harvest", this.getClass(), logFile);
                return true;
            }
        }

        return false;
    }

    /**
     * Loads tab separated file SERVERS_FILENAME and uses it to extract 2 to 4 parameters.
     * File parameters: 1 - Server; 2 - OAI Set; 3 - From Date; 4 - Until Date.
     * <p>Only the Server is required in the file. Both dates must use the format YYYY-MM-DD
     *
     * @throws Exception
     */
    public static void runFromFile(String recordsDir, String metadataFormat, File logFile) throws Exception {
        //Loads the resource from [REPOX_CLASSES]/[SERVERS_FILENAME]
        InputStream inputServersStream = ResponseTransformer.class.getClassLoader().getResourceAsStream(SERVERS_FILENAME);
        InputStreamReader inputStreamReader = new InputStreamReader(inputServersStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        SimpleDateFormat dateFormat = new SimpleDateFormat(OAI_DATE_FORMAT);

        String outputBaseDirname = bufferedReader.readLine();
        File outputBaseDir = new File(outputBaseDirname);
        if(!outputBaseDir.mkdir()) {
            throw new RuntimeException("Could not create dir: " + outputBaseDirname + " read from + " + SERVERS_FILENAME);
        }

        String currentLine;
        while((currentLine = bufferedReader.readLine()) != null) {
            StringUtil.simpleLog("currentline = " + currentLine, Harvester.class, logFile);
            String sourceUrl = null;
            String sourceSet = null;
            Date fromDate = null;
            Date untilDate = null;

            String[] serverParameters = currentLine.split("\t");
            if(serverParameters.length < 2 || serverParameters.length > 4) {
                throw new RuntimeException("Number of parameters must be between 2 and 4, line '"
                        + currentLine + "'" + " has " + serverParameters.length + "parameters");
            }
            else if (serverParameters.length >= 2) {
                sourceUrl = serverParameters[0];
                if(!serverParameters[1].isEmpty()) {
                    sourceSet = serverParameters[1];
                }
            }
            else if (serverParameters.length >= 3) {
                if(!serverParameters[2].isEmpty()) {
                    fromDate = dateFormat.parse(serverParameters[2]);
                }
            }
            else if (serverParameters.length == 4) {
                if(!serverParameters[3].isEmpty()) {
                    untilDate = dateFormat.parse(serverParameters[3]);
                }
            }

            Harvester harvester = new Harvester(sourceUrl, sourceSet, fromDate, untilDate, metadataFormat, logFile);
            harvester.run();
        }
    }

    public File[] getRequestFiles() {
        File requestDir = new File(outputDirname);
        if(requestDir.isDirectory()) {
            File[] targetFileList = requestDir.listFiles();
            List<File> recordRequestFileList = new ArrayList<File>();
            for (File currentFile : targetFileList) {
                if(currentFile.getName().startsWith(REQUEST_FILENAME_START)) {
                    recordRequestFileList.add(currentFile);
                }
            }

            File[] recordRequestFiles = new File[recordRequestFileList.size()];
            recordRequestFileList.toArray(recordRequestFiles);

            return recordRequestFiles;
        }
        else {
            return null;
        }
    }

    public void cleanUp() throws FileNotFoundException, IOException {
        File requestDir = new File(outputDirname);
        if(requestDir.isDirectory()) {
            if(!pt.utl.ist.util.FileUtil.deleteDir(requestDir)){
                throw new RuntimeException("Unable to delete temporary directory: " + requestDir.getAbsolutePath());
            }
        }
    }

    @Override
    public String toString() {
        String returnString = "sourceUrl: " + sourceUrl + ", sourceSet: " + sourceSet + ", outputBaseDir: " + outputDirname
                + ", fromDateString: " + fromDateString + ", untilDateString: " + untilDateString + ", metadataFormat: " + metadataFormat;
        return returnString;
    }

    public static void main(String[] args) throws Exception {
//		if(true) {
//			runFromFile("records");
//			return;
//		}

        Date fromDate = null;
        Date untilDate = null;
        String sourceUrl = "http://localhost:8080/oaiserver";
        String sourceSet = "Map";
//		sources.put("http://louisdl.louislibraries.org/cgi-bin/oai.exe", "LHC");
//		sources.put("http://louisdl.louislibraries.org/cgi-bin/oai.exe", "LMP");
//		sources.put("http://www.nla.gov.au/apps/oaicat/servlet/OAIHandler", "Map");
//		sources.put("http://zbc.uz.zgora.pl/dlibra/oai-pmh-repository.xml", "DigitalLibraryZielonaGora:RegionalMaterialss:Dokkartee");
//		sources.put("http://u2.gmu.edu:8080/dspace-oai/request", "hdl_1920_1935");
//		sources.put("http://broker10.fcla.edu/cgi/b/broker20/broker20", "palmmfof:mapflbib");
//		sources.put("http://vacani.icc.cat/cgi-bin/oai.exe", "catalunya");

        String outputBaseDir = "/home/dreis/test/";
        File logFile = new File("/home/dreis/test/log.txt");

        Harvester harvester = new Harvester(sourceUrl, sourceSet, fromDate, untilDate, "oai_dc", logFile);
        harvester.run();

    }
}
