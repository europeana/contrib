/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.harvest;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.tasks.ScheduledTaskUI;

@RemoteServiceRelativePath("taskManagementService")
public interface TaskManagementService extends RemoteService {

    public PagingLoadResult<ScheduledTaskUI> getScheduledTasks(PagingLoadConfig config) throws ServerSideException;
    public ModelData getCalendarTasks() throws ServerSideException;

}
