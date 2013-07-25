package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-03-2011
 * Time: 12:46
 */
public class DataSourceTabPanel extends FormDialog {

    private DataSourceFolderForm dataSourceFolderForm;
    private DataSourceOAIForm dataSourceOAIForm;
    private DataSourceZ39Form dataSourceZ39Form;
    private TabPanel tabPanel;
    private TabItem oaiTab,folderTab,z39Tab;

    public DataSourceTabPanel() {
        super(0.9,0.6);
//        setHeading("Create Data Set");
        setIcon(HarvesterUI.ICONS.add());
//        setMaximizable(true);

        FormData formData = new FormData("98%");
        dataSourceFolderForm = new DataSourceFolderForm(formData);
        dataSourceOAIForm = new DataSourceOAIForm(formData);
        dataSourceZ39Form = new DataSourceZ39Form(formData);

        if(HarvesterUI.getProjectType().equals("EUROPEANA")) {
            dataSourceOAIForm.addEuropeanaFields();
            dataSourceFolderForm.addEuropeanaFields();
            dataSourceZ39Form.addEuropeanaFields();
        }

        tabPanel = new TabPanel();

        oaiTab = new TabItem("OAI-PMH");
        oaiTab.add(dataSourceOAIForm);
        oaiTab.setIcon(HarvesterUI.ICONS.album());
        oaiTab.setLayout(new FitLayout());
        tabPanel.add(oaiTab);

        folderTab = new TabItem("Folder");
        folderTab.add(dataSourceFolderForm);
        folderTab.setIcon(HarvesterUI.ICONS.album());
        folderTab.setLayout(new FitLayout());
        tabPanel.add(folderTab);

        z39Tab = new TabItem("Z39.50");
        z39Tab.add(dataSourceZ39Form);
        z39Tab.setIcon(HarvesterUI.ICONS.album());
        z39Tab.setLayout(new FitLayout());
        tabPanel.add(z39Tab);

        tabPanel.addListener(Events.Select, new Listener<TabPanelEvent>() {
            public void handleEvent(TabPanelEvent be) {
                TabItem tabItem = be.getItem();
                Component component = tabItem.getItem(0);
                if(component instanceof  DataSourceForm)
                    ((DataSourceForm) component).resetLayout();
            }
        });

        add(tabPanel);
    }

    public void setEditMode(DataSourceUI dataSourceUI){
        setHeading(HarvesterUI.CONSTANTS.editDataSet()+": " + dataSourceUI.getDataSourceSet());
        setIcon(HarvesterUI.ICONS.table());
        String type = dataSourceUI.getIngest();
        String delimType = "[ ]+";
        String[] tokensType = type.split(delimType);
        String parsedType = tokensType[0];

        if(parsedType.equals("OAI-PMH")){
            tabPanel.setSelection(oaiTab);
            oaiTab.enable();
            folderTab.disable();
            z39Tab.disable();
            dataSourceOAIForm.setEditMode(dataSourceUI);
        }
        else if(parsedType.equals("Folder")){
            tabPanel.setSelection(folderTab);
            folderTab.enable();
            oaiTab.disable();
            z39Tab.disable();
            dataSourceFolderForm.setEditMode(dataSourceUI);
        }
        else if(parsedType.equals("Z3950")){
            tabPanel.setSelection(z39Tab);
            z39Tab.enable();
            oaiTab.disable();
            folderTab.disable();
            dataSourceZ39Form.setEditMode(dataSourceUI);
        }
    }

    public void reloadTransformations(){
        dataSourceOAIForm.reloadTransformations();
        dataSourceFolderForm.reloadTransformations();
        dataSourceZ39Form.reloadTransformations();
    }

    public void resetValues(DataProviderUI parent){
        setHeading(HarvesterUI.CONSTANTS.newDSTitle()+": " + parent.getName());
        setIcon(HarvesterUI.ICONS.add());
        dataSourceOAIForm.resetValues(parent);
        dataSourceFolderForm.resetValues(parent);
        dataSourceZ39Form.resetValues(parent);
        folderTab.enable();
        oaiTab.enable();
        z39Tab.enable();
        tabPanel.setSelection(oaiTab);
    }
}
