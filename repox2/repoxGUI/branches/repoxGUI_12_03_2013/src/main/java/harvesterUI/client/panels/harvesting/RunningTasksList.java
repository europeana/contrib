package harvesterUI.client.panels.harvesting;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.tasks.RunningTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 25-03-2011
 * Time: 22:26
 */
public class RunningTasksList extends ContentPanel {

    private List<RunningTask> runningTasksList;
    private ListStore<ModelData> store;
    private Grid<ModelData> grid;
    private HarvestOperationsServiceAsync service;

    public RunningTasksList(){
        setHeading(HarvesterUI.CONSTANTS.runningTasks());
        setIcon(HarvesterUI.ICONS.running_tasks_icon());
        setScrollMode(Style.Scroll.AUTO);
        setLayout(new FitLayout());

        service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
        store = new ListStore<ModelData>();
        runningTasksList = new ArrayList<RunningTask>();

        createGridList();
    }

    public void updateRunningTasks(){
        addAllTasks();
    }

    private void addAllTasks() {
        AsyncCallback<List<RunningTask>> callbackRT = new AsyncCallback<List<RunningTask>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<RunningTask> result) {
                runningTasksList.clear();
                store.removeAll();
                runningTasksList.addAll(result);
                store.add(runningTasksList);
            }
        };
        service.getAllRunningTasks(callbackRT);
    }

    private void createGridList() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        GridCellRenderer<ModelData> cancelTask = new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<ModelData> store, Grid<ModelData> grid) {
                final RunningTask task = (RunningTask) model;

                Button b = new Button();
                b.setIcon(HarvesterUI.ICONS.delete());
                b.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                            public void onFailure(Throwable caught) {
                                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                            }
                            public void onSuccess(Boolean result) {
                                if(!result) {
                                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.cancelHarvest(),HarvesterUI.CONSTANTS.taskOfDataSet() + " "
                                            +task.getDataSet()+" " + HarvesterUI.CONSTANTS.notFound());
                                    return;
                                }

                                runningTasksList.remove(task);
                                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.cancelHarvest(), HarvesterUI.CONSTANTS.taskOfDataSet() + " "
                                        +task.getDataSet()+" "+HarvesterUI.CONSTANTS.cancelTaskWarning());
                                Dispatcher.get().dispatch(AppEvents.ViewRunningTasksList);
                            }
                        };
                        service.deleteRunningTask(task, callback);
                    }
                });
                return b;
            }
        };



        ColumnConfig type = new ColumnConfig("listType", HarvesterUI.CONSTANTS.type(), 100);
        columns.add(type);

        ColumnConfig parameters = new ColumnConfig("parameters", HarvesterUI.CONSTANTS.parameters(), 170);
        columns.add(parameters);

        ColumnConfig status = new ColumnConfig("statusListString", HarvesterUI.CONSTANTS.status(), 100);
        columns.add(status);

        ColumnConfig delete = new ColumnConfig("", HarvesterUI.CONSTANTS.delete(), 25);
        delete.setRenderer(cancelTask);
        columns.add(delete);

        ColumnModel cm = new ColumnModel(columns);

        grid = new Grid<ModelData>(store, cm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noTasksRunning());
        grid.setBorders(false);
        grid.setTrackMouseOver(false);
        grid.setLayoutData(new FitLayout());
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.getView().setForceFit(true);
        add(grid);

        // Refresh export button
        ToolButton refreshExport = new ToolButton("x-tool-refresh");
        refreshExport.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            @Override
            public void componentSelected(IconButtonEvent ce) {
                Dispatcher.get().dispatch(AppEvents.ViewRunningTasksList);
            }
        });
        getHeader().addTool(refreshExport);
    }
}
