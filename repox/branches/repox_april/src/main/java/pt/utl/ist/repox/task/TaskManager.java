package pt.utl.ist.repox.task;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.RunnableStoppable;
import pt.utl.ist.repox.dataProvider.DataProviderManager;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.DataSourcesMonitor;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.task.ScheduledTask.Frequency;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.XmlUtil;
import pt.utl.ist.util.DateUtil;
import pt.utl.ist.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.*;

public class TaskManager implements RunnableStoppable {
	private static final Logger log = Logger.getLogger(TaskManager.class);
	private static final int CYCLE_CHECK_TIME = 15 * 1000;

	private boolean stopExecution = false;
	private List<ScheduledTask> scheduledTasks;
	private List<Task> runningTasks;
	private List<Task> onetimeTasks;
	private File configurationFile;
	private File runningTasksFile;

	public List<ScheduledTask> getScheduledTasks() {
		return scheduledTasks;
	}

	public void setScheduledTasks(List<ScheduledTask> scheduledTasks) {
		this.scheduledTasks = scheduledTasks;
	}

	public List<Task> getRunningTasks() {
		return runningTasks;
	}

	public void setRunningTasks(List<Task> runningTasks) {
		this.runningTasks = runningTasks;
	}

	public List<Task> getOnetimeTasks() {
		return onetimeTasks;
	}

	public void setOnetimeTasks(List<Task> onetimeTasks) {
		this.onetimeTasks = onetimeTasks;
	}
	
	public File getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}

	public File getRunningTasksFile() {
		return runningTasksFile;
	}

	public void setRunningTasksFile(File runningTasksFile) {
		this.runningTasksFile = runningTasksFile;
	}

	public TaskManager(File scheduledTasksFile, File runningTasksFile) throws DocumentException, IllegalFileFormatException, ParseException, ClassNotFoundException, NoSuchMethodException {
		super();
		this.configurationFile = scheduledTasksFile;
		this.runningTasksFile = runningTasksFile;
		scheduledTasks = new ArrayList<ScheduledTask>();
		onetimeTasks = new ArrayList<Task>();
		runningTasks = new ArrayList<Task>();

		if(scheduledTasksFile.exists()) {
	        scheduledTasks = loadScheduledTasks(scheduledTasksFile);
		}

		if(runningTasksFile.exists()) {
			List<Task> loadedTasks = TaskFileHelper.loadTasks(runningTasksFile);
	        for (Task loadedTask : loadedTasks) {
				log.info("Adding task to be restarted : " + loadedTask);
				onetimeTasks.add(loadedTask);
			}
		}
	}

	private List<ScheduledTask> loadScheduledTasks(File scheduledTasksFile)
			throws DocumentException, IllegalFileFormatException, ParseException {
		List<ScheduledTask> loadedScheduledTasks = new ArrayList<ScheduledTask>();
		
		Document document = new SAXReader().read(scheduledTasksFile);

		List scheduledTaskElements = document.getRootElement().elements("scheduledTask");

		for (Element scheduledTaskElement : (List<Element>) scheduledTaskElements) {
			Integer id = Integer.parseInt(scheduledTaskElement.attributeValue("id"));
			
			Element timeElement = scheduledTaskElement.element("time");
			Calendar firstRun = Calendar.getInstance(); 
			firstRun.setTime(DateUtil.string2Date(timeElement.getText(), TimeUtil.LONG_DATE_FORMAT_NO_SECS));

			Element frequencyElement = scheduledTaskElement.element("frequency");
			Frequency frequency = Frequency.valueOf(frequencyElement.attributeValue("type"));
			Integer xmonths = null;
			if(frequency.equals(Frequency.XMONTHLY)) {
				xmonths = Integer.parseInt(frequencyElement.attributeValue("xmonthsPeriod"));
			}
			
			String runnable = scheduledTaskElement.element("runnable").attributeValue("class"); // class implementing RunnableStoppable
			
			Element parametersElement = scheduledTaskElement.element("parameters");
			List<Element> parametersList = parametersElement.elements("parameter");
			String[] parameters = null;
			if(parametersList.size() > 0) {
				parameters = new String[parametersList.size()];
				
				for (int i = 0; i < parametersList.size(); i++) {
					parameters[i] = parametersList.get(i).getText();
				}
			}

			try {
				Class[] paramTypes = new Class[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					paramTypes[i] = String.class;
				}
				Class<RunnableStoppable> taskClass = (Class<RunnableStoppable>) Class.forName(runnable);
				Constructor<RunnableStoppable> cons = taskClass.getConstructor(paramTypes);
				RunnableStoppable newInstance = cons.newInstance((Object[]) parameters);
				Task taskToRun = null;
				if(IngestDataSource.class.isAssignableFrom(taskClass)) {
					taskToRun = new DataSourceIngestTask(parameters[0], parameters[1], parameters[2]);
				}
				else if(ExportToFilesystem.class.isAssignableFrom(taskClass)) {
					taskToRun = new DataSourceExportTask(parameters[0], parameters[1], parameters[2], parameters[3]);
				}
				else {
					log.error("Unable to identify Task to handle RunnableStoppable of class: " + taskClass.getName());
					continue;
				}
				
				ScheduledTask scheduledTask = new ScheduledTask(id, firstRun, frequency, xmonths, taskToRun);
				loadedScheduledTasks.add(scheduledTask);
			} catch (Exception e) {
				throw new IllegalFileFormatException("Unable to create constructor for class " + runnable + " with " + parameters.length  + " arguments", e);
			}

		}
		
		return loadedScheduledTasks;
	}
		
	private void startTask(Task currentTask) {
		log.info("Starting task of class: " + currentTask.getTaskClass().getName()
				+ " with parameters " + StringUtil.getArrayAsString(currentTask.getParameters()));

		try {
			currentTask.startTask();
			runningTasks.add(currentTask);
			try {
				TaskFileHelper.saveTasks(runningTasksFile, runningTasks);
			}
			catch(IOException e) {
				log.error("Error saving runningTasks in file " + runningTasksFile.getAbsolutePath(), e);
			}
		}
		catch (Exception e) {
			log.error("Error running task of class: " + currentTask.getTaskClass().getName()
					+ " with parameters " + StringUtil.getArrayAsString(currentTask.getParameters()), e);
		}
	}

	private Integer getNewTaskId(){
		int largestId = 1;

		for (ScheduledTask currentScheduledTask : scheduledTasks) {
			if(currentScheduledTask.getId() >= largestId) {
				largestId = currentScheduledTask.getId() + 1;
			}
		}

		return largestId;
	}
	
	private void runOnetimeTasks()  {
		Calendar now = Calendar.getInstance();
		
		Iterator<Task> onetimeTasksIterator = onetimeTasks.iterator();
		while(onetimeTasksIterator.hasNext()) {
			Task currentTask = onetimeTasksIterator.next();
			
			if(currentTask.isTimeToRun(now)) {
				onetimeTasksIterator.remove();
				startTask(currentTask);
			}
		}
	}
	
	private void runDataSourcesMonitor(GregorianCalendar now) {
		try {
			DataSourcesMonitor dataSourceMonitor = DataSourcesMonitor.getInstance();
			if(dataSourceMonitor.isTimeForMonitoring(now)) {
				List<DataSource> dataSources = RepoxContextUtil.getRepoxManager().getDataProviderManager().loadDataSources();
				dataSourceMonitor.monitorDataSources(dataSources);
			}
		}
		catch (Exception e) {
			log.error("Error monitoring Data Sources", e);
		}
	}
	
	private void runDataSourcesRecordCount(GregorianCalendar now) {
		try {
			RecordCountManager recordCountManager = RepoxContextUtil.getRepoxManager().getRecordCountManager();
			if(recordCountManager.isTimeForFullCount(now)) {
				recordCountManager.generateCounts(true); // Force full count
			}
			else if(recordCountManager.isTimeToRun(now)) {
				recordCountManager.generateCounts(false);
			}
		}
		catch (Exception e) {
			log.error("Error counting records", e);
		}
	}

	private void runScheduledTasks(GregorianCalendar now) {
		for (ScheduledTask currentTask : scheduledTasks) {
			if(!isScheduledTaskExecuting(currentTask, runningTasks) && currentTask.isTimeToRun(now)) {
				startTask(currentTask);					
			}
		}
	}

	/**
	 * Returns true if an equivalent scheduled task (same parameters even if it's a different task) is executing and false otherwise.
	 * This means that there can't be two scheduled tasks of the same type executing at the same time.
	 */
	private boolean isScheduledTaskExecuting(ScheduledTask scheduledTask, Collection<Task> tasks) {
		for (Task currentTask : tasks) {
			if(scheduledTask.equalActionParameters(currentTask)) {
				return true;
			}
		}
		return false;
	}

	private void checkFinishedTasks() throws IOException, ClassNotFoundException, DocumentException, NoSuchMethodException, ParseException {
		if(runningTasks != null) {
			Iterator<Task> runningTasksIterator = runningTasks.iterator();
			while(runningTasksIterator.hasNext()) {
				Task task = runningTasksIterator.next();
				if(!task.isRunning()) { //means it has finished
					if(task.getStatus() != null && task.getStatus().equals(Task.Status.FAILED)
							&& task.getRetries() < task.getMaxRetries()) { // something went wrong, restart if retry is configured 
						log.info("Retrying task of class " + task.getTaskClass().getName());
						task.setStatus(null);
						task.setRetries(task.getRetries() + 1);
						task.setFailTime(Calendar.getInstance());
						addOnetimeTask(task);
					}
					else  { 
						task.setFinishTime(Calendar.getInstance());
						
						// if it's a DataSource Task, save log
						if(task instanceof DataSourceTask) {
							DataSourceTask dSTask = (DataSourceTask) task;

							try {
								DataProviderManager dataProviderManager = RepoxContextUtil.getRepoxManager().getDataProviderManager();
								DataSource dataSource = dataProviderManager.getDataSource(dSTask.getDataSourceId());
								File dSTaskFile = new File(dataSource.getTasksDir(), dSTask.getTaskId() + ".xml");
								TaskFileHelper.saveSingleTask(dSTaskFile, task);
							}
							catch(Exception e) {
								log.error("Error saving DataSourceTask with id " + dSTask.getTaskId()
										+ " of DataSource " + dSTask.getDataSourceId(), e);
							}
						}
					}
					
					if(task.isTimeToRemove()) {
						runningTasksIterator.remove();
					}
				}
			}
		}
		
		try {
			TaskFileHelper.saveTasks(runningTasksFile, runningTasks);
		}
		catch(IOException e) {
			log.error("Error saving runningTasks in file " + runningTasksFile.getAbsolutePath(), e);
		}
	}
	
	public void stop() {
		stopExecution = true;
	}

	public void run() {


        log.info("TaskManager started execution.");

		while(!stopExecution) {
			try {

				GregorianCalendar now = new GregorianCalendar();
				
				runOnetimeTasks();
				runDataSourcesMonitor(now);
				runDataSourcesRecordCount(now);
				runScheduledTasks(now);

				checkFinishedTasks();
			}catch (ClassNotFoundException e) {
                log.error("Error in TaskManager", e);
            } catch (NoSuchMethodException e) {
                log.error("Error in TaskManager", e);
            } catch (ParseException e) {
                log.error("Error in TaskManager", e);
            } catch (DocumentException e) {
                log.error("Error in TaskManager", e);
            } catch (IOException e) {
                log.error("Error in TaskManager", e);
            } finally {
				try {
					Thread.sleep(CYCLE_CHECK_TIME);
				}
				catch (InterruptedException e) {
					log.error("Error sleeping in TaskManager", e); 
				}
			}
		}

		log.info("TaskManager stopped execution.");
	}
	
	public boolean isTaskExecuting(Task newTask) {
		if(onetimeTasks != null) {
			for (Task task : onetimeTasks) {
				if(task.equalActionParameters(newTask)) {
					return true;
				}
			}
		}
		
		if(runningTasks != null) {
			for (Task task : runningTasks) {
				if(task.equalActionParameters(newTask)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void addOnetimeTask(Task task) throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, ParseException {
		onetimeTasks.add(task);
		TaskFileHelper.saveTask(runningTasksFile, task);
	}

	public void removeOnetimeTask(Task task) throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, ParseException {
		if(onetimeTasks.contains(task)) {
			onetimeTasks.remove(task);
		}
		else {
			log.warn("Fail to remove task from onetimeTasks. Class: " + task.getTaskClass().getName()
					+ " parameters: " + StringUtil.getArrayAsString(task.getParameters()));
		}
		
		TaskFileHelper.removeTask(runningTasksFile, task);
	}

	public synchronized ScheduledTask getTask(Integer id){
		for (ScheduledTask currentScheduledTask : scheduledTasks) {
			if(currentScheduledTask.getId().equals(id)) {
				return currentScheduledTask;
			}
		}

		return null;
	}

	public synchronized void saveTask(ScheduledTask task) throws IOException {
		scheduledTasks.add(task);
		saveTasks();
	}

	private void saveTasks() throws IOException {
		Document document = DocumentHelper.createDocument();
		Element rootNode = document.addElement("scheduledTasks");
		
		for (ScheduledTask scheduledTask : scheduledTasks) {
			Element scheduledTaskElement = rootNode.addElement("scheduledTask");
			scheduledTaskElement.addAttribute("id", scheduledTask.getId().toString());
			
			Element timeElement = scheduledTaskElement.addElement("time");
			timeElement.setText(DateUtil.date2String(scheduledTask.getFirstRun().getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS));
			
			Element frequencyElement = scheduledTaskElement.addElement("frequency");
			frequencyElement.addAttribute("type", scheduledTask.getFrequency().toString());
			if(scheduledTask.getFrequency().equals(Frequency.XMONTHLY)) {
				frequencyElement.addAttribute("xmonthsPeriod", scheduledTask.getXmonths().toString());
			}
			
			scheduledTaskElement.addElement("runnable").addAttribute("class", scheduledTask.getTaskClass().getName());
			
			Element parametersElement = scheduledTaskElement.addElement("parameters");
			for (String parameter : scheduledTask.getParameters()) {
				parametersElement.addElement("parameter").setText(parameter);
			}
		}
		
		XmlUtil.writePrettyPrint(configurationFile, document);
	}

	/**
	 * @param id
	 * @return true if ScheduledTask existed and was deleted, false otherwise
	 * @throws IOException
	 */
	public synchronized boolean deleteTask(Integer id) throws IOException {
		boolean deletionSuccessful = false;
		
		if(id == null) {
			return false;
		}

		if(configurationFile.exists()) {
			// Backup configuration file
			File backupFile = new File(configurationFile.getParent(), configurationFile.getName() + ".bkp");
			FileUtil.copyFile(configurationFile, backupFile);
			
			Iterator<ScheduledTask> iterator = scheduledTasks.iterator();
			while(iterator.hasNext()) {
				ScheduledTask scheduledTask = iterator.next();
				if(scheduledTask.getId().equals(id)) {
					iterator.remove();
					deletionSuccessful = true;
				}
			}
			
			if(deletionSuccessful) {
				saveTasks();
			}
		}

		return deletionSuccessful;
	}

	public void updateDataSourceTasks(String oldDataSourceId, String newDataSourceId) throws IOException {
		List<ScheduledTask> scheduledTasksClone = new ArrayList<ScheduledTask>(scheduledTasks);

		for (ScheduledTask currentTask : scheduledTasksClone) {
			if(DataSource.class.isAssignableFrom(currentTask.getTaskClass())
					|| ExportToFilesystem.class.isAssignableFrom(currentTask.getTaskClass())) {
				String dataSourceId = currentTask.getParameters()[0];
				if(dataSourceId.equals(oldDataSourceId)) {
					deleteTask(currentTask.getId());
					currentTask.getParameters()[0] = newDataSourceId;
					saveTask(currentTask);
				}
			}
		}
	}

	public void deleteDataSourceTasks(String dataSourceId) throws IOException {
		List<ScheduledTask> scheduledTasksClone = new ArrayList<ScheduledTask>(scheduledTasks);

		for (ScheduledTask currentTask : scheduledTasksClone) {
			if(DataSource.class.isAssignableFrom(currentTask.getTaskClass())
					|| ExportToFilesystem.class.isAssignableFrom(currentTask.getTaskClass())) {
				String taskDataSourceId = currentTask.getParameters()[0];
				if(taskDataSourceId.equals(dataSourceId)) {
					deleteTask(currentTask.getId());
				}
			}
		}
	}
	
	public List<ScheduledTask> getTasksForDay(Calendar day) {
		List<ScheduledTask> tasksForDay = new ArrayList<ScheduledTask>();
		
		for (ScheduledTask scheduledTask : scheduledTasks) {
			if(scheduledTask.isDayToRun(day)) {
				tasksForDay.add(scheduledTask);
			}
		}
		
		return tasksForDay;
	}

	public static void main(String[] args) throws IOException {
		TaskManager taskManager = RepoxContextUtil.getRepoxManager().getTaskManager();
		taskManager.deleteTask(1);
	}

}
