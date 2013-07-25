package pt.utl.ist.repox.web.action.scheduler;

import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.validation.LocalizableError;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.task.ExportToFilesystem;
import pt.utl.ist.repox.task.IngestDataSource;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.web.action.RepoxActionBean;
import pt.utl.ist.repox.web.action.RepoxActionBeanContext;

import java.io.File;
import java.io.IOException;

public class SchedulingActionHelper extends RepoxActionBean {
	protected ScheduledTask scheduledTask;
	protected String exportDirectory;
	protected int recordsPerFile;
	protected Boolean editing;
	protected Integer taskId;
	protected boolean fullIngest;

	public ScheduledTask getScheduledTask() {
		return scheduledTask;
	}

	public void setScheduledTask(ScheduledTask scheduledTask) {
		this.scheduledTask = scheduledTask;
	}

	public String getExportDirectory() {
		return exportDirectory;
	}

	public void setExportDirectory(String exportDirectory) {
		this.exportDirectory = exportDirectory;
	}

	public int getRecordsPerFile() {
		return recordsPerFile;
	}

	public void setRecordsPerFile(int recordsPerFile) {
		this.recordsPerFile = recordsPerFile;
	}

	public Boolean getEditing() {
		return editing;
	}

	public void setEditing(Boolean editing) {
		this.editing = editing;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	
	public boolean isFullIngest() {
		return fullIngest;
	}

	public void setFullIngest(boolean fullIngest) {
		this.fullIngest = fullIngest;
	}

	public SchedulingActionHelper() {
		super();

		scheduledTask = new ScheduledTask();
	}

	protected boolean scheduleExportFilesystemMethod(RepoxActionBeanContext context, DataSource dataSource, String exportDirectory,
			ScheduledTask scheduledTask, Boolean editing, Integer taskId)
				throws DocumentException, IOException {
		if(exportDirectory == null) {
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSource.invalidDirectory", exportDirectory));
			return false;
		}

		File exportDir = new File(exportDirectory);
		if((exportDir.exists() && !exportDir.isDirectory()) || (!exportDir.exists() && !exportDir.mkdir())) {
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSource.invalidDirectory", exportDir.getAbsolutePath()));
			return false;
		}

		
		if(scheduledTask == null || scheduledTask.getFrequency() == null) {
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSource.nofrequency"));
			return false;
		}
		
		int newTaskId = dataSource.getNewTaskId();
		scheduledTask.setId(newTaskId);

		scheduledTask.setTaskClass(ExportToFilesystem.class);
		String[] parameters = new String[]{new Integer(newTaskId).toString(), dataSource.getId(), exportDir.getAbsolutePath(), String.valueOf(recordsPerFile)};
		scheduledTask.setParameters(parameters);

		
		if(editing != null && taskId != null) {
			context.getRepoxManager().getTaskManager().deleteTask(taskId);
		}
		
		context.getRepoxManager().getTaskManager().saveTask(scheduledTask);
	  	context.getMessages().add(new LocalizableMessage("dataSource.scheduledExportSuccess", dataSource.getId()));
	  	
	  	return true;
	}

	protected boolean scheduleIngestMethod(RepoxActionBeanContext context, DataSource dataSource,
			ScheduledTask scheduledTask, Boolean editing, Integer taskId, boolean fullIngest)
				throws DocumentException, IOException {
		if(scheduledTask == null || scheduledTask.getFrequency() == null) {
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSource.nofrequency"));
			return false;
		}

		int newTaskId = dataSource.getNewTaskId();
		scheduledTask.setId(newTaskId);
		
		scheduledTask.setTaskClass(IngestDataSource.class);
		String[] parameters = new String[]{new Integer(newTaskId).toString(), dataSource.getId(), (new Boolean(fullIngest)).toString()};
		scheduledTask.setParameters(parameters);
		
		if(editing != null && taskId != null) {
			context.getRepoxManager().getTaskManager().deleteTask(taskId);
		}

		context.getRepoxManager().getTaskManager().saveTask(scheduledTask);
	  	context.getMessages().add(new LocalizableMessage("dataSource.scheduledHarvestSuccess", dataSource.getId()));
	  	
	  	return true;
	}
	
	protected void deleteScheduledTaskMethod(RepoxActionBeanContext context, Integer taskId) throws IOException {
		TaskManager manager = getContext().getRepoxManager().getTaskManager();
		if(manager.deleteTask(taskId)) {
			context.getMessages().add(new LocalizableMessage("scheduledTask.delete.success", taskId));
		}
		else {
			context.getMessages().add(new LocalizableMessage("scheduledTask.delete.error", taskId));
		}
	}

}