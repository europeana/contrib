/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.harvest;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.dataTypes.DataSourceUI;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.tasks.RunningTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

@RemoteServiceRelativePath("harvestOperationsService")
public interface HarvestOperationsService extends RemoteService {

    public String dataSourceIngestNow(List<DataSourceUI> dataSourceUI) throws ServerSideException;
    public String dataSourceIngestSample(List<DataSourceUI> dataSourceUI, BaseModel configData) throws ServerSideException;
    public List<RunningTask> getAllRunningTasks() throws ServerSideException;
    public Boolean dataSourceEmpty(List<DataSourceUI> dataSourceUI) throws ServerSideException;
    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException;
    public Boolean deleteRunningTask(RunningTask runningTask) throws ServerSideException;
    public RunningTask getRunningTask(String dataSourceId) throws ServerSideException;

    public String addScheduledTask(ScheduledTaskUI taskUI) throws ServerSideException;
    public Boolean deleteScheduledTask(String scheduledTaskID) throws ServerSideException;
    public Boolean updateScheduledTask(ScheduledTaskUI scheduledTaskUI) throws ServerSideException;

}
