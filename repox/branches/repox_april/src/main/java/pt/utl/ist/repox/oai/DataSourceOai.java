package pt.utl.ist.repox.oai;

import eu.europeana.repox2sip.models.DataSet;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.oclc.oai.harvester2.verb.ListIdentifiers;
import org.w3c.dom.NodeList;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
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

import javax.xml.transform.TransformerConfigurationException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a DataSource that makes requests to a OAI-PMH Provider and
 * harvests its Records into the DataSource
 */
public class DataSourceOai extends DataSource {
	private static final Logger log = Logger.getLogger(DataSourceOai.class);

	private String oaiSourceURL;
	private String oaiSet;

	public String getOaiSourceURL() {
		return oaiSourceURL;
	}

	public void setOaiSourceURL(String oaiSourceURL) {
		this.oaiSourceURL = oaiSourceURL;
	}

	public String getOaiSet() {
		return oaiSet;
	}

	public void setOaiSet(String oaiSet) {
		this.oaiSet = oaiSet;
	}

	public static List<String> getOaiMetadataFormats() {
		List<String> oaiMetadataFormats = new ArrayList<String>();
		oaiMetadataFormats.add(MetadataFormat.oai_dc.toString());
		oaiMetadataFormats.add(MetadataFormat.tel.toString());
		oaiMetadataFormats.add(MetadataFormat.MarcXchange.toString());

		return oaiMetadataFormats;
	}

	public DataSourceOai() {
		super();
		metadataFormat = MetadataFormat.oai_dc.toString();
	}

	public DataSourceOai(DataProvider dataProvider, String id, String description, String metadataFormat,
			RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations) {
		super(dataProvider, id, description, metadataFormat, recordIdPolicy, metadataTransformations);
	}

	public DataSourceOai(DataProvider dataProvider, String id, String description, String metadataFormat,
			String oaiSourceURL, String oaiSet, RecordIdPolicy recordIdPolicy,
			Map<String, MetadataTransformation> metadataTransformations) {
		this(dataProvider, id, description, metadataFormat, recordIdPolicy, metadataTransformations);
		this.id = id;
		this.oaiSourceURL = oaiSourceURL;
		this.oaiSet = oaiSet;
	}
	
	@Override
	public Task.Status ingestRecords(File logFile, boolean fullIngest) throws TransformerConfigurationException, IOException, DocumentException, SQLException {
		System.out.println("INGESTING NOW!");
		
		Task.Status ingestStatus = Task.Status.OK;
		
		Harvester harvester = new Harvester(oaiSourceURL, oaiSet, null, null, metadataFormat, logFile);
		String outputDirPath = Harvester.getOutputDirPath(oaiSourceURL, oaiSet);
		File ingestResumptionFile = new File(outputDirPath, "ingestResumption.txt");
		Date syncDate = getSynchronizationDate();
		
		if(fullIngest) {
			boolean successfulDeletion = emptyRecords();
			
			if(ingestResumptionFile.exists()) {
				ingestResumptionFile.delete();
			}
			
			syncDate = null;
			File syncDateFile = getSyncDateFile();
			if(syncDateFile.exists()) {
				syncDateFile.delete();
			}
			
			if(!successfulDeletion) {
				StringUtil.simpleLog("Importing aborted - unable to delete the current Records", this.getClass(), logFile);
				return Task.Status.FAILED;
			}
		}

		File requestOutputDir = new File(outputDirPath);
		if(!requestOutputDir.exists() && !requestOutputDir.mkdir()) {
			throw new RuntimeException("Unable to create directory: " + outputDirPath);
		}

		RecordCount recordCount = RepoxContextUtil.getRepoxManager().getRecordCountManager().getRecordCount(id, true);
		
		//if there is a previous successful harvest and has finished
		if(recordCount != null && recordCount.getCount() > 0 && syncDate != null && !ingestResumptionFile.exists()) {
			String syncDateString = DateUtil.date2String(syncDate, TimeUtil.SHORT_DATE_FORMAT);
			harvester.setFromDateString(syncDateString);
			StringUtil.simpleLog("OAI-PMH harvest from date: " + syncDateString, this.getClass(), logFile);
		}

		// Harvest the records
		Thread harvesterThread = new Thread(harvester);
		harvesterThread.start();
		int currentRequest = 1;

		try {
			if(ingestResumptionFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(ingestResumptionFile));
				currentRequest = Integer.parseInt(reader.readLine());
				reader.close();
				StringUtil.simpleLog("Continuing from previous stopped ingestion - request " + currentRequest, this.getClass(), logFile);
			}

			File currentRequestFile = harvester.getRequestFile(currentRequest);
			TimeUtil.startTimers();
			TimeUtil.getTimeSinceLastTimerArray(8);

			while(true) {
				if(stopExecution) {
					harvester.stop();
					StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
					return Task.Status.CANCELED;
				}
				while(!currentRequestFile.exists()) { // Wait for the Harvester to write the request file
					if(stopExecution) {
						harvester.stop();
						StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
						return Task.Status.CANCELED;
					}
					if(harvester.isHarvestFinished()) {
						StringUtil.simpleLog("Ingest Process ended. Exiting.", this.getClass(), logFile);
						log.info("* Total Time: " + TimeUtil.getTimeSinceLastTimerArray(8));

						harvester.cleanUp();
						return ingestStatus;
					}

					if(!harvesterThread.isAlive() && !harvester.isHarvestFinished()) {
						StringUtil.simpleLog("Harvester thread exited without finishing. Exiting ingesting Data Source Oai.", this.getClass(), logFile);
						return Task.Status.FAILED;
					}

					Thread.sleep(1000);
				}
				Thread.sleep(100); // avoid trying to read request being written

				// Split the OAI-PMH answer to Records and import them
				List<RecordRepox> batchRecords = new ArrayList<RecordRepox>();
				TimeUtil.getTimeSinceLastTimerArray(9);
				ResponseTransformer responseTransformer = new ResponseTransformer();
				List<RecordRepox> responseRecords = null;

				try {
					responseRecords = responseTransformer.splitResponseToRecords(currentRequestFile, this, logFile);
				}
				catch(DocumentException e) { // may be trying to read a request being written, wait for file to be written and retry
					log.info("Error reading XML (waiting 5s and retrying in case it's being written): " + e.getMessage(), e);
					Thread.sleep(5000);
					responseRecords = responseTransformer.splitResponseToRecords(currentRequestFile, this, logFile);
				}

				batchRecords.addAll(responseRecords);
				log.info("Time for splitting " + responseRecords.size() + " records from response file: " + TimeUtil.getTimeSinceLastTimerArray(9));

				while(batchRecords.size() > RECORDS_BATCH_SIZE) {
					List<RecordRepox> recordsToImport = batchRecords.subList(0, RECORDS_BATCH_SIZE);
					batchRecords = batchRecords.subList(RECORDS_BATCH_SIZE, batchRecords.size());


                    RepoxContextUtil.getRepoxManager().getAccessPointsManager().processRecords(this, recordsToImport);
                    //pooulate the ingestedRecords list to insert later the records into the DB
                    ingestedRecords.addAll(recordsToImport);
					log.info("Time for importing " + recordsToImport.size() + " records to DB: " + TimeUtil.getTimeSinceLastTimerArray(9));
				}

				if(!batchRecords.isEmpty()) {
					RepoxContextUtil.getRepoxManager().getAccessPointsManager().processRecords(this, batchRecords);
                    //pooulate the ingestedRecords list to insert later the records into the DB
                    ingestedRecords.addAll(batchRecords);
					log.info("Time for importing last " + batchRecords.size() + " records to DB: " + TimeUtil.getTimeSinceLastTimerArray(9));
				}


				currentRequestFile.delete();
				currentRequest++;
				OutputStream resumptionOutputStream = new FileOutputStream(ingestResumptionFile);
				resumptionOutputStream.write((currentRequest + "\n").getBytes("UTF-8"));
				resumptionOutputStream.close();

				currentRequestFile = harvester.getRequestFile(currentRequest);
			}
		}
		catch(Exception e) {
			log.error("Error ingesting : " + e.getMessage(), e);
			harvester.stop();
			StringUtil.simpleLog("Error ingesting. Exiting ingesting Data Source Oai.", this.getClass(), logFile);
			return Task.Status.ERRORS;
		}
	}

	@Override
	public boolean isWorking() {
		try {
			ListIdentifiers listIdentifiers = new ListIdentifiers(oaiSourceURL, null, null, oaiSet, "oai_dc");
			if(listIdentifiers.isResultEmpty()) {
				return false;
			}
			NodeList errors = listIdentifiers.getErrors();
			if(errors.getLength() > 0) {
				return false;
			}
		}
		catch(FileNotFoundException e) { //This is the error returned by a 404
			return false;
		}
		catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	public boolean isSameDataSource(DataSourceOai dSOai) {
		if(CompareUtil.compareObjectsAndNull(this.oaiSourceURL, dSOai.getOaiSourceURL())
				&&  CompareUtil.compareObjectsAndNull(this.oaiSet, dSOai.getOaiSet())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(!this.getClass().equals(obj.getClass())) {
			return false;
		}

		DataSourceOai dSOai = (DataSourceOai) obj;
        return equalsBaseProperties(dSOai) && isSameDataSource(dSOai);

    }
	
	@Override
	public void cleanUp() throws IOException {
		super.cleanUp();
		
		String outputDirPath = Harvester.getOutputDirPath(oaiSourceURL, oaiSet);
		File outputDir = new File(outputDirPath);
		if(outputDir.exists()) {
			boolean deleteSuccessful = FileUtil.deleteDir(outputDir);

			if(!deleteSuccessful) {
				log.error("Unable to delete OAI-PMH dir from Data Source with id " + id);
			}
			else {
				log.info("Deleted OAI-PMH dir with success from Data Source with id " + id);
			}
		}
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
