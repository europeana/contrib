package harvesterUI.client.panels.harvesting;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.servlets.harvest.TaskManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.tasks.HarvestTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 25-03-2011
 * Time: 22:25
 */
public class ScheduledTasksList extends ContentPanel{

    private Grid<ScheduledTaskUI> grid;
    private HarvestOperationsServiceAsync harvestOperationsService;
    private TaskManagementServiceAsync taskManagementService;

    public ScheduledTasksList(){
        harvestOperationsService = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
        taskManagementService = (TaskManagementServiceAsync) Registry.get(HarvesterUI.TASK_MANAGEMENT_SERVICE);

        setHeading(HarvesterUI.CONSTANTS.scheduledTasksList());
        setIcon(HarvesterUI.ICONS.table());
        setScrollMode(Style.Scroll.AUTO);
        setLayout(new FitLayout());

        createGridList();
    }

    private void createGridList(){
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig id = new ColumnConfig("id", HarvesterUI.CONSTANTS.id(), 50);
        columns.add(id);

        ColumnConfig type = new ColumnConfig("listType", HarvesterUI.CONSTANTS.type(), 100);
        columns.add(type);

        ColumnConfig parameters = new ColumnConfig("parameters", HarvesterUI.CONSTANTS.parameters(), 250);
        columns.add(parameters);

        ColumnConfig firstRun = new ColumnConfig("dateString", HarvesterUI.CONSTANTS.firstRun(), 220);
        columns.add(firstRun);

        GridCellRenderer<ModelData> deleteTask = new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<ModelData> store, Grid<ModelData> grid) {
                final HarvestTask task = (HarvestTask) model;

                Button deleteButton = new Button();
                deleteButton.setIcon(HarvesterUI.ICONS.delete());
                deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        createConfirmMessageBox(task,HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteScheduledTaskMessage());
                    }
                });
                return deleteButton;
            }
        };
        ColumnConfig delete = new ColumnConfig("monthPeriod", HarvesterUI.CONSTANTS.delete(), 30);
        delete.setRenderer(deleteTask);
        columns.add(delete);

        ColumnModel cm = new ColumnModel(columns);

        RpcProxy<PagingLoadResult<ScheduledTaskUI>> proxy = new RpcProxy<PagingLoadResult<ScheduledTaskUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<ScheduledTaskUI>> callback) {
                taskManagementService.getScheduledTasks((PagingLoadConfig) loadConfig, callback);
            }
        };

        // loader
        final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);

        ListStore<ScheduledTaskUI> store = new ListStore<ScheduledTaskUI>(loader);

        PagingToolBar toolBar = new PagingToolBar(50);
        toolBar.bind(loader);

        grid = new Grid<ScheduledTaskUI>(store, cm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noScheduledTasks());
        grid.setBorders(false);
        grid.setTrackMouseOver(false);
        grid.setLayoutData(new FitLayout());
        grid.setLoadMask(true);
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.getView().setForceFit(true);
        grid.addListener(Events.Attach, new Listener<GridEvent<ScheduledTaskUI>>() {
            public void handleEvent(GridEvent<ScheduledTaskUI> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                config.setLimit(50);

                Map<String, Object> state = grid.getState();
                if (state.containsKey("offset")) {
                    int offset = (Integer)state.get("offset");
                    int limit = (Integer)state.get("limit");
                    config.setOffset(offset);
                    config.setLimit(limit);
                }
                if (state.containsKey("sortField")) {
                    config.setSortField((String)state.get("sortField"));
                    config.setSortDir(Style.SortDir.valueOf((String) state.get("sortDir")));
                }
                loader.load(config);
            }
        });
        add(grid);

        setBottomComponent(toolBar);
    }

    private void createConfirmMessageBox(final HarvestTask task,
                                         String title, String msg){
        SelectionListener<ButtonEvent> scheduledTaskRemoveListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                if(task instanceof ScheduledTaskUI) {
                    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        }
                        public void onSuccess(Boolean result) {
                            if(!result) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.deleteScheduledTask(), HarvesterUI.CONSTANTS.failedDeleteScheduledTask());
                                return;
                            }
//                                task.getDataSource().removeScheduledTask(task.getId());
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteScheduledTask(),
                                    "Scheduled Task with id " + task.getId() + " was deleted successfully");
                            Dispatcher.get().dispatch(AppEvents.ViewScheduledTasksList);
                        }
                    };
                    harvestOperationsService.deleteScheduledTask(task.getId(), callback);
                }
            }
        };

        HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(title,msg,scheduledTaskRemoveListener);
    }
}
