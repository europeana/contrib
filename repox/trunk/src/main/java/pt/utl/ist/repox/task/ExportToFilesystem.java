package pt.utl.ist.repox.task;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import pt.utl.ist.repox.RunnableStoppable;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.ZipUtil;

import java.io.*;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Exports all the Records from a Data Source to a specified location of the Filesystem
 *
 * @author dreis
 *
 */
public class ExportToFilesystem implements RunnableStoppable {
	private static final Logger log = Logger.getLogger(ExportToFilesystem.class);
	private static final int RECORDS_PER_REQUEST = 250;

	private String taskId;
	private String dataSourceId;
	private File exportDir;
	private boolean stopExecution = false;
	private int recordsPerFile = 1;
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public File getExportDir() {
		return exportDir;
	}

	public void setExportDir(File exportDir) {
		this.exportDir = exportDir;
	}
	
	public int getRecordsPerFile() {
		return recordsPerFile;
	}

	public void setRecordsPerFile(int recordsPerFile) {
		this.recordsPerFile = recordsPerFile;
	}

	public ExportToFilesystem() {
		super();
	}

	public ExportToFilesystem(String taskId, String dataSourceId, String exportDirPath, String recordsPerFile) {
		this();
		this.taskId = taskId;
		this.dataSourceId = dataSourceId;
		this.exportDir = new File(exportDirPath);
		this.recordsPerFile = (recordsPerFile == null && Integer.valueOf(recordsPerFile) == 0 ? 1 : Integer.valueOf(recordsPerFile));
	}

	public void run() {
		stopExecution = false;
		
		try {
			if((exportDir.exists() && !exportDir.isDirectory())
					|| (!exportDir.exists() && !exportDir.mkdir())) {
				throw new IOException("Invalid directory or unable to create directory with path " + exportDir.getAbsolutePath());
			}

			AccessPointsManager accessPointsManager = RepoxContextUtil.getRepoxManager().getAccessPointsManager();
			DataSource dataSource = RepoxContextUtil.getRepoxManager().getDataManager().getDataSource(dataSourceId);
			//force updated count before exporting
			int totalRecords = RepoxContextUtil.getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true).getCount();

			int recordsCounter = 1;
			int batchNumber = 1;
			int requestOffset = 0;


            // remove old XML exported files
            FilenameFilter xmlFileFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            };

            File[] oldFiles = exportDir.listFiles(xmlFileFilter);

            for (File oldFile : oldFiles) {
                oldFile.delete();
            }


			
			Element rootElement = getRootElement(totalRecords);
			XMLWriter xmlWriter = null;
			
			while(requestOffset < totalRecords) {
				OaiListResponse oaiListResponse = accessPointsManager.getOaiRecordsFromDataSource(dataSource, null, null,
						requestOffset, RECORDS_PER_REQUEST, false);

	        	for (OaiItem currentItem : oaiListResponse.getOaiItems()) {
	        		if(stopExecution) {
	        			log.warn("Received stop signal: exiting export of Data Source " + dataSource.getId()
	        					+ " from Data Provider " + dataSource.getDataProvider().getName() + " to dir: " + exportDir.getAbsolutePath());
	        			return;
	        		}
	        		
	        		boolean isCreateFile = isFileToCreate(recordsCounter);
	        		boolean isCloseFile = isFileToClose(totalRecords, recordsCounter);
	        		
	        		Element recordToExport = getRecordToExport(currentItem);
	        		
	        		try {
	        			if(isCreateFile) {
	        				xmlWriter = getNewXmlWriter(rootElement, batchNumber);
	        				startXmlWriter(xmlWriter, rootElement);
	        			}
	        			
	        			xmlWriter.write(recordToExport);

	        			if(isCloseFile) {
	        				endXmlWriter(xmlWriter, rootElement);
	        			}
	        		}
	        		catch(Exception e) {
	        			log.error("Error saving records of batch " + batchNumber + " of Data Source " + dataSourceId, e);
	        		}
	        		finally {
	        			if(isCloseFile) {
	        				batchNumber++;
	        			}
	        			
	        			recordsCounter++;
	        		}
	        	}

				requestOffset += RECORDS_PER_REQUEST;
			}

            
            // create a zip file with all files
            File zipFile = new File(exportDir, dataSourceId + "_" + DateFormatUtils.format(new Date(), TimeUtil.LONG_DATE_FORMAT_COMPACT) + ".zip");

            xmlFileFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            };

            if(exportDir.listFiles(xmlFileFilter).length > 0)
                ZipUtil.zipFiles(exportDir.listFiles(xmlFileFilter), zipFile);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private boolean isFileToCreate(int recordsCounter) {
		if(recordsPerFile < 0) {
			return recordsCounter == 1;
		}
		else {
			return ((recordsCounter - 1) % recordsPerFile) == 0;
		}
	}
	
	private boolean isFileToClose(int totalRecords, int recordsCounter) {
		if(recordsPerFile < 0) {
			return recordsCounter == totalRecords;
		}
		else {
			return (recordsCounter % recordsPerFile == 0) || (recordsCounter == totalRecords);
		}
	}

	private XMLWriter getNewXmlWriter(Element rootElement, int batchNumber) throws FileNotFoundException, UnsupportedEncodingException {
		File currentBatchFile = new File(exportDir, dataSourceId + "-" + URLEncoder.encode(String.valueOf(batchNumber), "UTF-8") + ".xml");
		FileOutputStream outputStream = new FileOutputStream(currentBatchFile);	        			
		XMLWriter xmlWriter = new XMLWriter(outputStream, OutputFormat.createCompactFormat());
		
		return xmlWriter;
	}
	
	private void startXmlWriter(XMLWriter xmlWriter, Element rootElement) throws SAXException, IOException {
		xmlWriter.startDocument();
		xmlWriter.writeOpen(rootElement);
	}
	
	private void endXmlWriter(XMLWriter xmlWriter, Element rootElement) throws IOException {
		xmlWriter.writeClose(rootElement);
		xmlWriter.close();
	}

	//<exportedRecords set="" batchsize="" total="">
	private Element getRootElement(int recordCount) {
		Element rootElement = DocumentHelper.createElement("exportedRecords");
		rootElement.addAttribute("set", dataSourceId);
		rootElement.addAttribute("batchsize", String.valueOf(recordsPerFile));
		rootElement.addAttribute("total", String.valueOf(recordCount));
		
		return rootElement;
	}

	//<record id="" timestamp="" deleted=""><metadata>...
	private Element getRecordToExport(OaiItem currentItem) {
		try {
			Element recordElement = DocumentHelper.createElement("record");
			String identifier = new Urn(currentItem.getSetSpec(), currentItem.getIdentifier()).toString();
			recordElement.addAttribute("id", identifier);
			recordElement.addAttribute("timestamp", currentItem.getDatestamp());
			
			Element metadataElement = recordElement.addElement("metadata");
			
			if(currentItem.isDeleted()) {
				recordElement.addAttribute("deleted", "true");
			}
			else {
				String xmlRecordString = new String(currentItem.getMetadata(), "UTF-8");
				Document recordMetadata = DocumentHelper.parseText(xmlRecordString);
				metadataElement.add(recordMetadata.getRootElement().detach());
			}
			
			return recordElement;
		}
		catch (UnsupportedEncodingException e) {
			log.error("Could not get metadata in UTF-8", e);
			return null;
		}
		catch (DocumentException e) {
			log.error("Could not parse XML from record of item " + currentItem.getIdentifier() + " of Data Source " + dataSourceId, e);
			return null;
		}
	}

	public void stop() {
		stopExecution = true;
	}

	public static void main(String[] args) throws DocumentException, IOException {
		DataSource dataSource = RepoxContextUtil.getRepoxManager().getDataManager().getDataSource("bn_teses");
		ExportToFilesystem exportToFilesystem = new ExportToFilesystem("112911", dataSource.getId(), "f:/lixo" , "-1");
		System.out.println(exportToFilesystem.getRecordsPerFile());
//		if(true) System.exit(0);
		exportToFilesystem.run();
	}
}
