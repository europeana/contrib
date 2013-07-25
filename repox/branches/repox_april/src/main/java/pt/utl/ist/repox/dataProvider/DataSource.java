/*
 * Created on 23/Mar/2006
 *
 */
package pt.utl.ist.repox.dataProvider;

import com.ibm.icu.util.Calendar;
import eu.europeana.core.util.web.EmailSender;
import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.dao.Repox2SipImpl;
import eu.europeana.repox2sip.models.Request;
import freemarker.template.TemplateException;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.accessPoint.AccessPointRecordRepoxFull;
import pt.utl.ist.repox.accessPoint.AccessPointTimestamp;
import pt.utl.ist.repox.dataProvider.dataSource.RecordIdPolicy;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a Data Source in REPOX. It will be used to harvest records and ingest them. Known
 * implementations are: DataSourceOai, DataSourceDirectoryImporter
 */
public abstract class DataSource {
//	public enum Status { RUNNING, SLEEPING, OK }

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
	protected String status;
	protected String lastRunResult;
	protected Date lastUpdate;
	protected String metadataFormat;
	protected boolean stopExecution = false;

    //REPOSX2SIP: ingestedRecords list will be used to contain the records obtained from a single ingest,it must be empty before any ingest
    protected List<RecordRepox> ingestedRecords = new ArrayList<RecordRepox>();

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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

	public DataSource(DataProvider dataProvider, String id, String description, String metadataFormat,
			RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations) {
		this();
		this.dataProvider = dataProvider;
		this.id = id;
		this.description = description;
		this.metadataFormat = metadataFormat;
		this.recordIdPolicy = recordIdPolicy;
		this.metadataTransformations = metadataTransformations;

		initAccessPoints();
	}

    private void sendEmail(Task.Status exitStatus, File logFile) throws IOException, MessagingException, TemplateException {
		String smtpServer = RepoxContextUtil.getRepoxManager().getConfiguration().getSmtpServer();
		if(smtpServer == null || smtpServer.isEmpty()) {
			return;
		}

        String fromEmail = RepoxContextUtil.getRepoxManager().getConfiguration().getDefaultEmail();
        String[] recipientsEmail = new String[]{RepoxContextUtil.getRepoxManager().getConfiguration().getAdministratorEmail()};
	    String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();

        EmailSender emailSender = new EmailSender();
        emailSender.setTemplate("src/main/resources/ingest");
        HashMap map = new HashMap<String, String>();
        map.put("exitStatus", exitStatus.toString());
        map.put("id",id);

        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setUsername(fromEmail);

        mail.setPort(25);

        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpServer);
        mail.setJavaMailProperties(props);

        emailSender.setMailSender(mail);
        emailSender.sendEmail(recipientsEmail.toString(), fromEmail, subject, map, logFile.getAbsolutePath() );

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
		Task.Status exitStatus = Task.Status.OK;
		stopExecution = false;

		File logFile = getLogFile(taskId);
		
		try {
			StringUtil.simpleLog("Starting to import from Data Source with id " + id, this.getClass(), logFile);
			lastUpdate = getSynchronizationDate();
			Date now = new Date();

            //TODO REPOX2SIP: clear ingestedRecords list
            //ingestedRecords = new ArrayList<RecordRepox>();
            //TODO REPOX2SIP: this method must populate the ingestedRecords list
            exitStatus = ingestRecords(logFile, fullIngest);
            //TODO REPOX2SIP: add Request to request table
            //Long requestId = insinsertRequest2DB();

            //TODO REPOX2SIP: call method to add the ingestedRecords to Request_MdRecord and MdRecord tables
            //insertRecords2DB(ingestedRecords,requestId);

			lastUpdate = now;
			if(exitStatus.isSuccessful()) {
				signalSynchronizationFinished(lastUpdate);
			}

			StringUtil.simpleLog("Finished importing from Data Source with id " + id + ". Exit status: " + exitStatus.toString(),
					this.getClass(), logFile);
			sendEmail(exitStatus, logFile);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			StringUtil.simpleLog("ERROR importing from Data Source with id " + id + ": " + e.getMessage(), this.getClass(), logFile);
		}
		
		return exitStatus;
	}

    //TODO REPOX2SIP:to implement
    /*private Long insertRequest2DB(){
        Repox2Sip repox2sip = new Repox2SipImpl();
        Request request = new Request();
        //TODO REPOX2SIP: preencher o request
        repox2sip.addRequest(request);
        return request.getId();
    }*/
    
    //TODO REPOX2SIP:to implement
    /*private void insertRecords2DB(List<RecordRepox> ingestedRecords,int requestId ){
        //for each RecordRepox
            if not exists in MdRecord table
                create new one and insert on table
            else
                get its id
            insert the requestId and the recordId to Request_MdRecord  

    }*/


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
		AccessPoint defaultTimestampAP = new AccessPointTimestamp(id +  "_timestamp");
		defaultTimestampAP.setIndexDeletedRecords(false);
		defaultTimestampAP.setRepoxInternal(true);
		this.accessPoints.put(defaultTimestampAP.getId(), defaultTimestampAP);

		/*
		AccessPoint deletedTimestampAP = new AccessPointTimestamp(id + "_timestamp_deleted");
		deletedTimestampAP.setIndexDeletedRecords(true);
		deletedTimestampAP.setRepoxInternal(true);
		this.accessPoints.put(deletedTimestampAP.getId(), deletedTimestampAP);
		 */

		AccessPoint defaultRecordAP = new AccessPointRecordRepoxFull(id + "_record");
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

	public String getSynchronizationDateString() throws FileNotFoundException, IOException {
		File syncDateFile = getSyncDateFile();
		if(!syncDateFile.exists()) {
			return null;
		}
		String syncDate = FileUtil.readFileToString(syncDateFile);
		return syncDate;
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

	public void renameDataSourceDir(String oldDataSourceId, String newDataSourceId) throws FileNotFoundException, IOException {
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

        EmailSender emailSender = new EmailSender();
        emailSender.setTemplate("src/main/resources/ingest");
        HashMap map = new HashMap<String, String>();
        
        map.put("exitStatus", "Coisas");
        map.put("id","55");
       
        JavaMailSenderImpl mail = new JavaMailSenderImpl();

        Properties props = System.getProperties();

        props.put("mail.smtp.host", "mail.inesc-id.pt");
        //props.put("mail.smtp.starttls.enable", "true");
        //props.put("mail.smtp.auth", "true");
        mail.setJavaMailProperties(props);
        mail.setPort(25);
        
        emailSender.setMailSender(mail);
        emailSender.sendEmail("repoxist@gmail.com", "repox@noreply.eu", "assunto", map ,"src/main/resources/configuration.properties" );
    }
}
