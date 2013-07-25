package harvesterUI.server.harvest;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.harvest.HarvestOperationsService;
import harvesterUI.server.ProjectType;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.dataTypes.DataSourceUI;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.tasks.RunningTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.RepoxManagerDefault;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.task.*;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.util.DateUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.IOException;
import java.util.*;

public class HarvestOperationsServiceImpl extends RemoteServiceServlet implements HarvestOperationsService {

    public HarvestOperationsServiceImpl() {}

    public String dataSourceIngestNow(List<DataSourceUI> dataSourceUIList) throws ServerSideException{
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIList) {
                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                        .getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

                if(dataSource == null) {
                    return "NO_DS_FOUND";
                }

                int oldValue = dataSource.getMaxRecord4Sample();
                dataSource.setMaxRecord4Sample(-1);
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                        startIngestDataSource(dataSourceUI.getDataSourceSet(), oldValue != -1);
            }
        }catch (ObjectNotFoundException e) {
            return "NO_DS_FOUND";
        } catch (AlreadyExistsException e) {
            return "TASK_EXECUTING";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return "SUCCESS";
    }

    public String dataSourceIngestSample(List<DataSourceUI> dataSourceUIList, BaseModel configData) throws ServerSideException{
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIList) {
                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                        getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

                if(dataSource == null) {
                    return "NO_DS_FOUND";
                } else {
                    dataSource.setMaxRecord4Sample(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSampleRecords());

                    Task harvestSubsetTask = new DataSourceIngestTask(String.valueOf(dataSource.getNewTaskId()),dataSource.getId(),"true");

                    if(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().isTaskExecuting(harvestSubsetTask ))
                        return "TASK_EXECUTING";

                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().addOnetimeTask(harvestSubsetTask );
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return "SUCCESS";
    }

    public List<RunningTask> getAllRunningTasks() throws ServerSideException{
        try{
            List<RunningTask> results = new ArrayList<RunningTask>();
            Iterator<Task> runningTasksIterator = getRunningTasks().iterator();
            while(runningTasksIterator.hasNext()) {
                Task task = runningTasksIterator.next();
                if(task instanceof DataSourceIngestTask) {
                    DataSourceIngestTask dataSourceIngestTask = (DataSourceIngestTask) task;

                    if(dataSourceIngestTask.getStartTime() != null){
                        results.add(new RunningTask(dataSourceIngestTask.getDataSourceId(),"DATA_SOURCE_INGEST",
                                dataSourceIngestTask.getTaskClass().getName(), dataSourceIngestTask.getTaskId(),
                                dataSourceIngestTask.getFullIngest() + "", "OK",
                                dataSourceIngestTask.getRetries() + "", dataSourceIngestTask.getMaxRetries() + "",
                                dataSourceIngestTask.getRetryDelay() + "",
                                dataSourceIngestTask.getStartTime().toString()));
                    }
                } else if(task instanceof DataSourceExportTask) {
                    DataSourceExportTask dataSourceExportTask = (DataSourceExportTask) task;
                    RunningTask newExportTask = new RunningTask(dataSourceExportTask.getDataSourceId(),"DATA_SOURCE_EXPORT",
                            dataSourceExportTask.getTaskClass().getName(), dataSourceExportTask.getTaskId(),
                            "", "OK", dataSourceExportTask.getRetries()+"", dataSourceExportTask.getMaxRetries() + "",
                            dataSourceExportTask.getRetryDelay() + "",
                            dataSourceExportTask.getStartTime().toString());
                    newExportTask.setRecordsPerFile(dataSourceExportTask.getRecordsPerFile());
                    newExportTask.setExportDirectory(dataSourceExportTask.getExportDirectory());
                    results.add(newExportTask);
                } else if(task instanceof ScheduledTask) {
                    ScheduledTask scheduledTask = (ScheduledTask) task;
                    String[] params = scheduledTask.getParameters();
                    String dsID = params[1];
                    String fullIngestStr = params[2];
                    if(params.length <= 3) {
                        if(scheduledTask.getStartTime() != null){
                            results.add(new RunningTask(dsID,"DATA_SOURCE_INGEST",
                                    scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                    fullIngestStr + "", "OK",
                                    scheduledTask.getRetries() + "", scheduledTask.getMaxRetries() + "",
                                    scheduledTask.getRetryDelay() + "",
                                    scheduledTask.getStartTime().toString()));
                        }
                    } else {
                        RunningTask newExportTask = new RunningTask(dsID,"DATA_SOURCE_EXPORT",
                                scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                "", "OK", scheduledTask.getRetries()+"", scheduledTask.getMaxRetries() + "",
                                scheduledTask.getRetryDelay() + "",
                                scheduledTask.getStartTime().toString());
                        String recordPerFile = params[2];
                        String exportPath = params[3];
                        newExportTask.setRecordsPerFile(recordPerFile);
                        newExportTask.setExportDirectory(exportPath);
                        results.add(newExportTask);
                    }
                }
            }
            return results;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean dataSourceEmpty(List<DataSourceUI> dataSourceUIList) throws ServerSideException{
        boolean result = false;
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIList) {
                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                        getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

                if (dataSource == null) {
                    result = false;
                } else {
                    dataSource.cleanUp();
                    result = true;
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean deleteRunningTask(RunningTask runningTask) throws ServerSideException{
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    stopIngestDataSource(runningTask.getDataSet(), Task.Status.CANCELED);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    protected List<Task> getRunningTasks() throws ServerSideException{
        List<Task> returnList = new ArrayList<Task>();

        List<Task> runningTasks = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getRunningTasks();
        if(runningTasks != null) {
            returnList.addAll(runningTasks);
        }

        List<Task> onetimeTasks = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getOnetimeTasks();
        if(onetimeTasks != null) {
            returnList.addAll(onetimeTasks);
        }

        return returnList;
    }

    public RunningTask getRunningTask(String dataSourceId) throws ServerSideException{
        try{
            Iterator<Task> runningTasksIterator = getRunningTasks().iterator();
            while(runningTasksIterator.hasNext()) {
                Task task = runningTasksIterator.next();
                if(task instanceof DataSourceIngestTask) {
                    DataSourceIngestTask dataSourceIngestTask = (DataSourceIngestTask) task;
                    if(dataSourceIngestTask.getStartTime() != null && dataSourceIngestTask.getDataSourceId().equals(dataSourceId)){
                        return new RunningTask(dataSourceIngestTask.getDataSourceId(),"DATA_SOURCE_INGEST",
                                dataSourceIngestTask.getTaskClass().getName(), dataSourceIngestTask.getTaskId(),
                                dataSourceIngestTask.getFullIngest() + "", "OK",
                                dataSourceIngestTask.getRetries() + "", dataSourceIngestTask.getMaxRetries() + "",
                                dataSourceIngestTask.getRetryDelay() + "",
                                dataSourceIngestTask.getStartTime().toString());
                    }
                } else if(task instanceof DataSourceExportTask) {
                    DataSourceExportTask dataSourceExportTask = (DataSourceExportTask) task;
                    if(dataSourceExportTask.getDataSourceId().equals(dataSourceId)){
                        RunningTask newExportTask = new RunningTask(dataSourceExportTask.getDataSourceId(),"DATA_SOURCE_EXPORT",
                                dataSourceExportTask.getTaskClass().getName(), dataSourceExportTask.getTaskId(),
                                "", "OK", dataSourceExportTask.getRetries()+"", dataSourceExportTask.getMaxRetries() + "",
                                dataSourceExportTask.getRetryDelay() + "",
                                dataSourceExportTask.getStartTime().toString());
                        newExportTask.setRecordsPerFile(dataSourceExportTask.getRecordsPerFile());
                        newExportTask.setExportDirectory(dataSourceExportTask.getExportDirectory());
                        return newExportTask;
                    }
                } else if(task instanceof ScheduledTask) {
                    ScheduledTask scheduledTask = (ScheduledTask) task;
                    String[] params = scheduledTask.getParameters();
                    String dsID = params[1];
                    String fullIngestStr = params[2];
                    if(dsID.equals(dataSourceId)){
                        if(params.length <= 3) {
                            if(scheduledTask.getStartTime() != null){
                                return new RunningTask(dsID,"DATA_SOURCE_INGEST",
                                        scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                        fullIngestStr + "", "OK",
                                        scheduledTask.getRetries() + "", scheduledTask.getMaxRetries() + "",
                                        scheduledTask.getRetryDelay() + "",
                                        scheduledTask.getStartTime().toString());
                            }
                        } else {
                            RunningTask newExportTask = new RunningTask(dsID,"DATA_SOURCE_EXPORT",
                                    scheduledTask.getTaskClass().getName(), scheduledTask.getId(),
                                    "", "OK", scheduledTask.getRetries()+"", scheduledTask.getMaxRetries() + "",
                                    scheduledTask.getRetryDelay() + "",
                                    scheduledTask.getStartTime().toString());
                            String recordPerFile = params[2];
                            String exportPath = params[3];
                            newExportTask.setRecordsPerFile(recordPerFile);
                            newExportTask.setExportDirectory(exportPath);
                            return newExportTask;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return null;
    }

    public String addScheduledTask(ScheduledTaskUI taskUI) throws ServerSideException {
        String taskId = "";
        try {
            // Check if scheduled task already exists
            if(RepoxServiceImpl.getRepoxManager().getTaskManager().taskAlreadyExists(taskUI.getDataSetId(),
                    DateUtil.date2String(taskUI.getDate(), TimeUtil.LONG_DATE_FORMAT_NO_SECS),
                    ScheduledTask.Frequency.valueOf(taskUI.getType()),taskUI.getFullIngest()))
                return "alreadyExists";

            DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    getDataSourceContainer(taskUI.getDataSetId()).getDataSource();

            if(dataSource == null) {
                return "notFound";
            }
            ScheduledTask scheduledTask = null;
            if(taskUI.getScheduleType() == 0) {
                Calendar cal=Calendar.getInstance();
                cal.setTime(taskUI.getDate());
                String newTaskId = dataSource.getNewTaskId();
                taskId = newTaskId;
                scheduledTask = new ScheduledTask(newTaskId, cal, ScheduledTask.Frequency.valueOf(taskUI.getType()), taskUI.getMonthPeriod(),
                        new DataSourceIngestTask(newTaskId, taskUI.getDataSetId(), taskUI.getFullIngest()));
            }else{
                Calendar cal=Calendar.getInstance();
                cal.setTime(taskUI.getDate());
                String newTaskId = dataSource.getNewTaskId();
                taskId = newTaskId;

                String recordsPerFile;
                if(taskUI.getRecordsPerFile().equals("All"))
                    recordsPerFile = "-1";
                else
                    recordsPerFile = taskUI.getRecordsPerFile();

                scheduledTask = new ScheduledTask(newTaskId, cal, ScheduledTask.Frequency.valueOf(taskUI.getType()), taskUI.getMonthPeriod(),
                        new DataSourceExportTask(newTaskId, taskUI.getDataSetId(),
                                taskUI.getExportDirectory(),
                                recordsPerFile,
                                taskUI.getExportFormat() != null ? taskUI.getExportFormat() : ""));
                dataSource.setExportDir(taskUI.getExportDirectory());
                RepoxServiceImpl.getRepoxManager().getDataManager().saveData();
            }
            RepoxServiceImpl.getRepoxManager().getTaskManager().saveTask(scheduledTask);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return taskId;
    }

    public Boolean deleteScheduledTask(String scheduledTaskID) throws ServerSideException{
        TaskManager manager = RepoxServiceImpl.getRepoxManager().getTaskManager();
        try {
            return manager.deleteTask(scheduledTaskID);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean updateScheduledTask(ScheduledTaskUI scheduledTaskUI) throws ServerSideException{
        ScheduledTask scheduledTask = RepoxServiceImpl.getRepoxManager().getTaskManager().getTask(scheduledTaskUI.getId());
        Calendar testDateChange=Calendar.getInstance();
        testDateChange.setTime(scheduledTaskUI.getDate());
        if(!testDateChange.equals(scheduledTask.getFirstRun())) {
            try {
                String date = DateUtil.date2String(scheduledTaskUI.getDate(), TimeUtil.LONG_DATE_FORMAT_NO_SECS);
                if(RepoxServiceImpl.getRepoxManager().getTaskManager().taskAlreadyExists(scheduledTaskUI.getDataSetId(),
                        date, ScheduledTask.Frequency.valueOf(scheduledTaskUI.getType()), scheduledTaskUI.getFullIngest())){
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerSideException(Util.stackTraceToString(e));
            }
        }

        try {
            if(scheduledTask.getTaskClass().getSimpleName().equals("IngestDataSource")){
                Calendar cal=Calendar.getInstance();
                cal.setTime(scheduledTaskUI.getDate());
                scheduledTask.setFirstRun(cal);
                scheduledTask.setXmonths(scheduledTaskUI.getMonthPeriod());
                scheduledTask.getParameters()[2] = scheduledTaskUI.getFullIngest();
                scheduledTask.setFrequency(ScheduledTask.Frequency.valueOf(scheduledTaskUI.getType()));
            } else if(scheduledTask.getTaskClass().getSimpleName().equals("ExportToFilesystem")) {
                Calendar cal=Calendar.getInstance();
                cal.setTime(scheduledTaskUI.getDate());
                scheduledTask.setFirstRun(cal);

                String recordsPerFile;
                if(scheduledTaskUI.getRecordsPerFile().equals("All"))
                    recordsPerFile = "-1";
                else
                    recordsPerFile = scheduledTaskUI.getRecordsPerFile();

                scheduledTask.setXmonths(scheduledTaskUI.getMonthPeriod());
                scheduledTask.getParameters()[3] = recordsPerFile;
                scheduledTask.getParameters()[2] = scheduledTaskUI.getExportDirectory();
                scheduledTask.setFrequency(ScheduledTask.Frequency.valueOf(scheduledTaskUI.getType()));

                DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                        getDataSourceContainer(scheduledTaskUI.getDataSetId()).getDataSource();
                dataSource.setExportDir(scheduledTaskUI.getExportDirectory());
                RepoxServiceImpl.getRepoxManager().getDataManager().saveData();
            }
            RepoxServiceImpl.getRepoxManager().getTaskManager().deleteTask(scheduledTask.getId());
            RepoxServiceImpl.getRepoxManager().getTaskManager().saveTask(scheduledTask);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().dataSourceExport(dataSourceUI);
    }

}
