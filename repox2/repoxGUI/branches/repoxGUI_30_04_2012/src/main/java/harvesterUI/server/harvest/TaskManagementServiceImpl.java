package harvesterUI.server.harvest;

import com.extjs.gxt.ui.client.data.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.harvest.TaskManagementService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.task.ScheduledTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskManagementServiceImpl extends RemoteServiceServlet implements TaskManagementService {

    public TaskManagementServiceImpl() {
    }

    public PagingLoadResult<ScheduledTaskUI> getScheduledTasks(PagingLoadConfig config) throws ServerSideException{
//        if (config.getSortInfo().getSortField() != null) {
//            final String sortField = config.getSortInfo().getSortField();
//            if (sortField != null) {
//                Collections.sort(posts, config.getSortInfo().getSortDir().comparator(new Comparator<Post>() {
//                    public int compare(Post p1, Post p2) {
//                        if (sortField.equals("forum")) {
//                            return p1.getForum().compareTo(p2.getForum());
//                        } else if (sortField.equals("username")) {
//                            return p1.getUsername().compareTo(p2.getUsername());
//                        } else if (sortField.equals("subject")) {
//                            return p1.getSubject().compareTo(p2.getSubject());
//                        } else if (sortField.equals("date")) {
//                            return p1.getDate().compareTo(p2.getDate());
//                        }
//                        return 0;
//                    }
//                }));
//            }
//        }
        int totalSize = RepoxServiceImpl.getRepoxManager().getTaskManager().getScheduledTasks().size();
        return new BasePagingLoadResult<ScheduledTaskUI>(getParsedScheduledTasks(config.getOffset(),config.getLimit()), config.getOffset(), totalSize);
    }

    private List<ScheduledTaskUI> getParsedScheduledTasks(int offSet, int limit) throws ServerSideException{
        List<ScheduledTaskUI> scheduledTaskUIs = new ArrayList<ScheduledTaskUI>();
        List<ScheduledTask> scheduledTasks = RepoxServiceImpl.getRepoxManager().getTaskManager().getScheduledTasks();
        for (int i = offSet; i < limit && i<scheduledTasks.size(); i++) {
            scheduledTaskUIs.add(parseScheduledTask(scheduledTasks.get(i)));
        }
        return scheduledTaskUIs;
    }

    private ScheduledTaskUI parseScheduledTask(ScheduledTask scheduledTask) throws ServerSideException{
        try{
            ScheduledTaskUI scheduledTaskUI = null;
            String[] setTokens = scheduledTask.getId().split("_");
            String dataSetId = setTokens[0];
            // Load only tasks after today that aren't frequency ONCE
            if(scheduledTask.getFirstRun().getTime().after(Calendar.getInstance().getTime()) || !scheduledTask.getFrequency().name().equals("ONCE")) {
                if(scheduledTask.getTaskClass().getSimpleName().equals("IngestDataSource")) {
                    String scheduledTaskId = scheduledTask.getId();
                    String firstRunStr = scheduledTask.getFirstRunString();
                    String freq = scheduledTask.getFrequency().name();
                    Integer xmonths = scheduledTask.getXmonths();
                    String fullIngest = scheduledTask.getParameters()[2];
                    scheduledTaskUI = new ScheduledTaskUI(dataSetId,scheduledTaskId,firstRunStr,freq,xmonths,fullIngest);
                    scheduledTaskUI.setScheduleType(0);
                } else if(scheduledTask.getTaskClass().getSimpleName().equals("ExportToFilesystem")) {
                    String scheduledTaskId = scheduledTask.getId();
                    String firstRunStr = scheduledTask.getFirstRunString();
                    String freq = scheduledTask.getFrequency().name();
                    Integer xmonths = scheduledTask.getXmonths();
                    String recordsPerFile = scheduledTask.getParameters()[3];
                    String exportDirectory = scheduledTask.getParameters()[2];
                    scheduledTaskUI = new ScheduledTaskUI(dataSetId,scheduledTaskId,firstRunStr,freq,xmonths,"");
                    scheduledTaskUI.setScheduleType(1);
                    scheduledTaskUI.createDateString(1);
                    scheduledTaskUI.setScheduleType("Data Set Export");
                    DataSourceContainer dataSourceContainer = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId);
                    String exportDir = dataSourceContainer.getDataSource().getExportDir().getAbsolutePath();
                    scheduledTaskUI.setParameters("Data Set: " + dataSetId + " -- Folder: " + exportDir);
                    scheduledTaskUI.setRecordsPerFile(recordsPerFile);
                    scheduledTaskUI.setExportDirectory(exportDirectory);
                }
            }
            return scheduledTaskUI;
        } catch (Exception e){
            throw  new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private List<OldTaskUI> getParsedOldTasks() throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getParsedOldTasks();
    }

    public ModelData getCalendarTasks() throws ServerSideException{
        try{
            ModelData data = new BaseModelData();
            List<ScheduledTask> scheduledTasks = RepoxServiceImpl.getRepoxManager().getTaskManager().getScheduledTasks();
            List<ScheduledTaskUI> scheduledTaskUIs = getParsedScheduledTasks(0,scheduledTasks.size());
            data.set("schedules",scheduledTaskUIs);
            data.set("oldTasks",getParsedOldTasks());
            return data;
        } catch (Exception e){
            throw  new ServerSideException(Util.stackTraceToString(e));
        }
    }

}
