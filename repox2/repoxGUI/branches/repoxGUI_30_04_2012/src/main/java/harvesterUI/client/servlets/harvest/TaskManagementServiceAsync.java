package harvesterUI.client.servlets.harvest;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.tasks.ScheduledTaskUI;

public interface TaskManagementServiceAsync {

    public void getScheduledTasks(PagingLoadConfig config, AsyncCallback<PagingLoadResult<ScheduledTaskUI>> callback);
    public void getCalendarTasks(AsyncCallback<ModelData> callback);

}
