package pt.utl.ist.repox.web.action.scheduler;

import com.ibm.icu.util.Calendar;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.task.*;
import pt.utl.ist.repox.util.Month;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SchedulerActionBean extends SchedulingActionHelper {
    private static final Logger log = Logger.getLogger(SchedulerActionBean.class);

    private TaskCalendarMonth taskCalendarMonth;
    private Integer calendarMonth;
    private Integer calendarYear;

    public List<ScheduledTask> getScheduledTasks() {
        return getContext().getRepoxManager().getTaskManager().getScheduledTasks();
    }

    public TaskCalendarMonth getTaskCalendarMonth() {
        return taskCalendarMonth;
    }

    public void setTaskCalendarMonth(TaskCalendarMonth taskCalendarMonth) {
        this.taskCalendarMonth = taskCalendarMonth;
    }

    public Integer getCalendarMonth() {
        return calendarMonth;
    }

    public void setCalendarMonth(Integer calendarMonth) {
        this.calendarMonth = calendarMonth;
    }

    public Integer getCalendarYear() {
        return calendarYear;
    }

    public void setCalendarYear(Integer calendarYear) {
        this.calendarYear = calendarYear;
    }

    public SchedulerActionBean() {
        super();
    }

    @ValidationMethod(on = {"delete"})
    public void validateScheduledTask(ValidationErrors errors) throws DocumentException {
        ScheduledTask task = getContext().getRepoxManager().getTaskManager().getTask(taskId);
        if(task == null) {
            errors.add("scheduledTaskId", new LocalizableError("error.scheduledTask.delete.scheduledTaskId", taskId));
        }
    }

    @DefaultHandler
    public Resolution view(){
        if(RepoxContextUtil.getRepoxManager().getConfiguration().isConfigurationFileOk()){
            Month month;
            calendarYear = (calendarYear != null ? calendarYear : Calendar.getInstance().get(Calendar.YEAR));

            if(calendarMonth != null) {
                if(calendarMonth < Month.JANUARY.getMonthNumber()) { // Previous Year
                    month = Month.DECEMBER;
                    calendarMonth = Month.DECEMBER.getMonthNumber();
                    calendarYear--;
                }
                else if(calendarMonth > Month.DECEMBER.getMonthNumber()) { // Next Year
                    month = Month.JANUARY;
                    calendarMonth = Month.JANUARY.getMonthNumber();
                    calendarYear++;
                }
                else {
                    month = Month.getMonth(calendarMonth);
                }
            }
            else {
                month = Month.getCurrentMonth();
            }

            taskCalendarMonth = new TaskCalendarMonth(month, calendarYear);
            return new ForwardResolution("/jsp/scheduler/list.jsp");
        }
        else{
            return new ForwardResolution("/propertiesForm.html");
        }
    }

    public Resolution deleteScheduledTask() throws IOException {
        deleteScheduledTaskMethod(context, taskId);

        return view();
    }

    private DataSource getDataSourceFromParameter() throws DocumentException, IOException {
        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        if(dataSourceId == null) {
            dataSourceId = (String) context.getRequest().getAttribute("dataSourceId");
        }

        DataSource dataSource = context.getRepoxManager().getDataManager().getDataSource(dataSourceId);
        return dataSource;
    }

    public Resolution scheduleHarvest() throws IOException, DocumentException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        scheduleIngestMethod(context, dataSource, scheduledTask, editing, taskId, fullIngest);

        return view();
    }

    public Resolution scheduleExportFilesystem() throws IOException, DocumentException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        scheduleExportFilesystemMethod(context, dataSource, exportDirectory, scheduledTask, editing, taskId);

        return view();
    }

    public Resolution cancelTask() throws IOException, DocumentException, NoSuchMethodException, ClassNotFoundException, ParseException {
        String taskClass = context.getRequest().getParameter("taskClass");
        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        String exportFolder = context.getRequest().getParameter("exportFolder");
        boolean fullIngest = Boolean.parseBoolean(context.getRequest().getParameter("fullIngest"));

        if(stopTask(taskClass, dataSourceId, exportFolder, fullIngest)){
            context.getMessages().add(new LocalizableMessage("task.cancel.success", dataSourceId));
        }
        else {
            context.getValidationErrors().add("task", new LocalizableError("error.task.stop", dataSourceId));
        }

        return view();
    }

    private boolean stopTask(String taskClass, String dataSourceId, String exportFolder, boolean fullIngest)
            throws SecurityException, NoSuchMethodException, DocumentException, IOException, ClassNotFoundException, ParseException {
        DataSourceTask dummyTask = null; // necessary to compare the action with the running tasks

        if(taskClass.equals(IngestDataSource.class.getName())) {
            dummyTask = new DataSourceIngestTask(null, dataSourceId, Boolean.toString(fullIngest));
        }
        else if(taskClass.equals(ExportToFilesystem.class.getName())) {
            dummyTask = new DataSourceExportTask(null, dataSourceId, exportDirectory, String.valueOf(recordsPerFile));
        }
        else {
            log.error("Cannot stop task. Unknown task class:" + taskClass);
            return false;
        }

        List<Task> runningTasks = getRunningTasks();
        for (Task task : runningTasks) {
            if(task instanceof ScheduledTask && task.getParameters() != null && task.getParameters().length > 0) {
                dummyTask.setTaskId(task.getParameters()[0]);
            }

            if(task.equalsAction((Task) dummyTask)) {
                task.stop();
                context.getRepoxManager().getTaskManager().removeOnetimeTask(task);
                return true;
            }
        }

        return false;
    }

    public List<Task> getRunningTasks() {
        List<Task> returnList = new ArrayList<Task>();

        List<Task> runningTasks = context.getRepoxManager().getTaskManager().getRunningTasks();
        if(runningTasks != null) {
            returnList.addAll(runningTasks);
        }

        List<Task> onetimeTasks = context.getRepoxManager().getTaskManager().getOnetimeTasks();
        if(onetimeTasks != null) {
            returnList.addAll(onetimeTasks);
        }

        return returnList;
    }


}