/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.*;
import harvesterUI.client.panels.forms.dataSources.DataSourceTabPanel;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.DataSourceUI;
import harvesterUI.shared.TransformationUI;


public class FormView extends View {

    private AggregatorForm aggregatorForm;
    private DataProviderForm dataProviderForm;
    private DataSourceTabPanel dataSourceTabPanel;
    private DataProviderImportForm dataProviderImportForm;
    private NewTransformationDialog newTransformationDialog;

    public FormView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.ChangeToLightVersion){
            makeLightChanges();
        }
        else if (event.getType() == AppEvents.ChangeToEuropeana){
            makeEuropeanaChanges();
        }
        else if (event.getType() == AppEvents.ChangeToEudml){
            makeLightChanges();
        }
        else if (event.getType() == AppEvents.ViewAggregatorForm){
            // Edit mode
            AggregatorUI aggregatorUI = event.getData();
            if(aggregatorUI != null) {
                aggregatorForm.setEditMode(aggregatorUI);
            } else
                aggregatorForm.resetValues();

            aggregatorForm.showAndCenter();
        }else if (event.getType() == AppEvents.ViewDataProviderForm) {
            // Edit mode
            if(event.getData() instanceof DataProviderUI) {
                DataProviderUI folder = event.getData();
                dataProviderForm.setEditMode(folder);
            }
            else
                dataProviderForm.resetValues(event.getData());

            dataProviderForm.showAndCenter();
        }else if (event.getType() == AppEvents.ViewDataSourceForm){

            // Edit mode
            if(event.getData() instanceof DataSourceUI) {
                DataSourceUI dataSourceUI = event.getData();
                if(dataSourceUI.getStatus().startsWith("RUNNING") ||
                        dataSourceUI.getStatus().startsWith("PRE_PROCESSING") ||
                        dataSourceUI.getStatus().startsWith("POST_PROCESSING")){
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.editDataSet(),HarvesterUI.CONSTANTS.dataSetIngesting());
                    return;
                }else
                    dataSourceTabPanel.setEditMode(dataSourceUI);
            }
            else if(event.getData() instanceof DataProviderUI)
                dataSourceTabPanel.resetValues((DataProviderUI)event.getData());

            dataSourceTabPanel.showAndCenter();
        }else if (event.getType() == AppEvents.ViewDPImportForm){
            dataProviderImportForm.showAndCenter();
            dataProviderImportForm.resetFileUploadField();
        }else if (event.getType() == AppEvents.HideDataSourceForm){
            dataSourceTabPanel.hide();
        }else if (event.getType() == AppEvents.ViewAddSchemaDialog){
            newTransformationDialog.showAndCenter();
            if(event.getData() instanceof TransformationUI)
                newTransformationDialog.edit((TransformationUI)event.getData());
            else
                newTransformationDialog.resetValues();
        }else if (event.getType() == AppEvents.ReloadTransformations){
            dataSourceTabPanel.reloadTransformations();
        }
    }

    @Override
    protected void initialize(){
        newTransformationDialog = new NewTransformationDialog();
        dataSourceTabPanel = new DataSourceTabPanel();
    }

    protected void makeEuropeanaChanges(){
        aggregatorForm = new AggregatorForm();
        dataProviderForm = new DataProviderEuropeanaForm();
        dataProviderImportForm = new DataProviderImportForm();
    }

    protected void makeLightChanges(){
        aggregatorForm = null;
        dataProviderForm = new DataProviderLightForm();
        dataProviderImportForm = new DataProviderImportForm();
    }
}
