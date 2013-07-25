/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.harvest;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.dataTypes.DataSourceUI;
import harvesterUI.shared.tasks.RunningTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.List;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface HarvestOperationsServiceAsync {

    public void dataSourceIngestNow(List<DataSourceUI> dataSourceUI,AsyncCallback<String> callback);
    public void dataSourceIngestSample(List<DataSourceUI> dataSourceUI, BaseModel configData,AsyncCallback<String> callback);

    public void getAllRunningTasks(AsyncCallback<List<RunningTask>> callback);
    public void dataSourceEmpty(List<DataSourceUI> dataSourceUI,AsyncCallback<Boolean> callback);
    public void dataSourceExport(DataSourceUI dataSourceUI,AsyncCallback<Boolean> callback);
    public void getRunningTask(String dataSourceId,AsyncCallback<RunningTask> callback);

    public void deleteRunningTask(RunningTask runningTask,AsyncCallback<Boolean> callback);

    public void addScheduledTask(ScheduledTaskUI taskUI, AsyncCallback<String> callback);
    public void deleteScheduledTask(String scheduledTaskID,AsyncCallback<Boolean> callback);
    public void updateScheduledTask(ScheduledTaskUI scheduledTaskUI,AsyncCallback<Boolean> callback);
}
