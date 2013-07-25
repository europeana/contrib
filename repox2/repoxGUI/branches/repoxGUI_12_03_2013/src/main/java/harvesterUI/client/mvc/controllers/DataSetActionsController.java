package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.administration.AdminForm;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.DataSourceUI;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:48
 */
public class DataSetActionsController extends Controller {

    private DataSetOperationsServiceAsync dataSetOperationsService;
    private HarvestOperationsServiceAsync harvestOperationsService;

    public DataSetActionsController() {
        registerEventTypes(AppEvents.IngestDataSet);
        registerEventTypes(AppEvents.EmptyDataSet);
        registerEventTypes(AppEvents.RemoveDataSet);
//        registerEventTypes(AppEvents.SaveDataSet);
//        registerEventTypes(AppEvents.MoveDataSet);
        registerEventTypes(AppEvents.IngestDataSetSample);
//        registerEventTypes(AppEvents.ScheduleDataSetIngest);
//        registerEventTypes(AppEvents.ScheduleDataSetExport);
//        registerEventTypes(AppEvents.ExportDataSetNow);

        dataSetOperationsService = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
        harvestOperationsService = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.IngestDataSet)
            onStartHarvest(event);
        else if (type == AppEvents.EmptyDataSet)
            onEmptyDataSet(event);
        else if (type == AppEvents.RemoveDataSet)
            onRemoveDataSet(event);
//        else if (type == AppEvents.SaveDataSet)
//            onSaveDataSet(event);
//        else if (type == AppEvents.MoveDataSet)
//            onMoveDataSet(event);
        else if (type == AppEvents.IngestDataSetSample)
            onIngestDataSetSample(event);
//        else if (type == AppEvents.ScheduleDataSetIngest)
//            onScheduleIngestDataSet(event);
//        else if (type == AppEvents.ScheduleDataSetExport)
//            onScheduleExportDataSet(event);
//        else if (type == AppEvents.ExportDataSetNow)
//            onExportDataSetNow(event);
    }

    public void initialize() {
//        administrationView = new AdministrationView(this);
    }

    private void onStartHarvest(AppEvent event) {
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(String result) {
                if(result.equals("NO_DS_FOUND")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestNow(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                }
                else if(result.equals("TASK_EXECUTING")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestNow(), HarvesterUI.CONSTANTS.taskAlreadyExecuting());
                    return;
                }
                dataSourcesSelectedUI.get(0).setStatus("RUNNING");
                History.fireCurrentHistoryState();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.ingestNow(), HarvesterUI.CONSTANTS.harvestWillStart());
            }
        };
        harvestOperationsService.dataSourceIngestNow(dataSourcesSelectedUI, callback);
    }

    private void onEmptyDataSet(AppEvent event){
        UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.emptyingDataSetMask());
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Boolean result) {
                if(!result) {
                    UtilManager.unmaskCentralPanel();
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.emptyDataSet(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                }

                dataSourcesSelectedUI.get(0).setStatus(null);
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.emptyDataSet(), HarvesterUI.CONSTANTS.emptySuccessful());
                UtilManager.unmaskCentralPanel();
                History.fireCurrentHistoryState();
            }
        };
        harvestOperationsService.dataSourceEmpty(dataSourcesSelectedUI, callback);
    }

    private void onRemoveDataSet(AppEvent event){
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        final LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
        AsyncCallback callback = new AsyncCallback() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Object result) {
//                getMainDataManager().deleteDataSources(dataSourcesSelectedUI);
                wrapper.unmask();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteDataSets(), HarvesterUI.CONSTANTS.dataSetDeleted());
                Dispatcher.forwardEvent(AppEvents.LoadMainData);
            }
        };
        wrapper.mask(HarvesterUI.CONSTANTS.removeDataSetMask());
        dataSetOperationsService.deleteDataSources(dataSourcesSelectedUI, callback);
    }

    private void onSaveDataSet(AppEvent event){

    }

    private void onMoveDataSet(AppEvent event){

    }

    private void onIngestDataSetSample(AppEvent event){
        final List<DataSourceUI> dataSourcesSelectedUI = event.getData();
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(String result) {
                if(result.equals("NO_DS_FOUND")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestSample(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                }
                else if(result.equals("TASK_EXECUTING")) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.ingestSample(), HarvesterUI.CONSTANTS.taskAlreadyExecuting());
                    return;
                }

                dataSourcesSelectedUI.get(0).setStatus("RUNNING");
                History.fireCurrentHistoryState();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.ingestSample(), HarvesterUI.CONSTANTS.harvestWillStart());
            }
        };
        AdminForm adminForm = (AdminForm) Registry.get("adminConfig");
        BaseModel configData = new BaseModel();
        configData.set("data",adminForm.getRepositoryFolderPath());
        harvestOperationsService.dataSourceIngestSample(dataSourcesSelectedUI,configData, callback);
    }

    private void onScheduleIngestDataSet(AppEvent event){

    }

    private void onScheduleExportDataSet(AppEvent event){

    }

    private void onExportDataSetNow(AppEvent event){
    }
}
