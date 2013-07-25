package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.administration.AdminForm;
import harvesterUI.client.panels.services.DataSetListParameter;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.util.*;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.DataSourceUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.TransformationUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.externalServices.ServiceParameterUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-03-2011
 * Time: 17:06
 */
public abstract class DataSourceForm extends DefaultFormPanel {

    private FormData formData;
    protected boolean edit = false;
    protected DataSourceUI dataSourceUI;
    protected DataProviderUI parent;

    protected FieldSet outputSet;
    protected FieldSetWithClickOption namespaces;
    protected TextField<String> recordSet, description;
    protected List<ComboBox<TransformationUI>> transformationsComboBoxes;
    private ListStore<TransformationUI> transformationsStore;
    protected NamespacePanelExtension namespacePanelExtension;
    private Button addTransformation, newTransformationButton;
    private ComboBox<TransformationUI> firstTransformationsCombo;

    protected DataSetOperationsServiceAsync dataSetOperationsService;
    protected RepoxServiceAsync repoxService;
    protected DataSourceServicesPanel dataSourceServicesPanel;

    protected String MEDIUM_FORM_DATA = "80%";
    protected FormData smallFixedFormData;
    protected int DEFAULT_LABEL_WIDTH = 190;
    protected int SPECIAL_FIELDS_LABEL_WIDTH = 194;

    public DataSourceForm(FormData data) {
        formData = data;

        smallFixedFormData = new FormData(250,22);

        dataSourceServicesPanel = new DataSourceServicesPanel(formData);

        dataSetOperationsService = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
        repoxService = (RepoxServiceAsync) Registry.get(HarvesterUI.REPOX_SERVICE);

        transformationsComboBoxes = new ArrayList<ComboBox<TransformationUI>>();

        namespaces = new FieldSetWithClickOption();
        namespaces.setHeading(HarvesterUI.CONSTANTS.namespaces());
        namespaces.setCheckboxToggle(true);

        namespacePanelExtension = new NamespacePanelExtension(namespaces,formData);

        outputSet = new FieldSet();
        outputSet.setHeading(HarvesterUI.CONSTANTS.output());
        outputSet.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));

        addTransformation = new Button(HarvesterUI.CONSTANTS.add(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if(!transformationsComboBoxes.get(transformationsComboBoxes.size()-1).getSelection().get(0).get("identifier").equals("-"))
                    createNewTransformationCombo();
            }
        });
        newTransformationButton = new Button(HarvesterUI.CONSTANTS.newStr(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.get().dispatch(AppEvents.ViewAddSchemaDialog);
            }
        });
    }

    /**
     * Create the Namespace Field set for Folder and Z39 Id extraction form
     */
    public FieldSetWithClickOption createNamespaceSet(){
        namespaces.setLayout(new EditableFormLayout(150));
        namespacePanelExtension.createNewNamespace();
        return namespaces;
    }

    public void setEditNamespaces(DataSourceUI treeDataSourceUI){
        List<String> namespaceList = treeDataSourceUI.getNamespaceList();
        int namespaceListSize = namespaceList.size();

        setResetNamespaces();

        if(namespaceListSize > 0){
            namespaces.expand();
            namespacePanelExtension.editNamespaces(namespaceList);
        }
    }

    public void setResetNamespaces(){
        namespacePanelExtension.clearNamespacesList(1);
    }

    /**
     * Create the Output Field set for each data source Form
     */
    public FieldSet createOutputSet() {
        recordSet = new TextField<String>();
        recordSet.setFieldLabel(HarvesterUI.CONSTANTS.recordSet()+ HarvesterUI.REQUIRED_STR);
        recordSet.setId("rcrdSet_europ");
        recordSet.setAllowBlank(false);
        outputSet.add(recordSet,formData);

        description = new TextField<String>();
        description.setFieldLabel(HarvesterUI.CONSTANTS.description() + HarvesterUI.REQUIRED_STR);
        description.setId("desc_europ");
        description.setAllowBlank(false);
        outputSet.add(description, formData);

        transformationsStore = new ListStore<TransformationUI>();

        firstTransformationsCombo = new ComboBox<TransformationUI>();
        firstTransformationsCombo.setDisplayField("dsStringFormat");
        firstTransformationsCombo.setId("firstTransCombo");
        firstTransformationsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        firstTransformationsCombo.setStore(transformationsStore);
        firstTransformationsCombo.setEditable(false);
        transformationsComboBoxes.add(firstTransformationsCombo);

        LayoutContainer transformContainer = new LayoutContainer();
        HBoxLayout transformContainerLayout = new HBoxLayout();
        transformContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        transformContainer.setLayout(transformContainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.transformations());
        label.setWidth(SPECIAL_FIELDS_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        transformContainer.add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        transformContainer.add(addTransformation, new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1);
        transformContainer.add(firstTransformationsCombo, flex);
        transformContainer.add(newTransformationButton, new HBoxLayoutData(new Margins(0, 0, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        outputSet.add(transformContainer, formData);

        return outputSet;
    }

    private void createNewTransformationCombo(){
        ComboBox<TransformationUI> newTransformationsCombo = new ComboBox<TransformationUI>();
        newTransformationsCombo.setFieldLabel(HarvesterUI.CONSTANTS.transformation());
        newTransformationsCombo.setDisplayField("dsStringFormat");
        newTransformationsCombo.setValue(transformationsStore.getModels().get(0));
        newTransformationsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        newTransformationsCombo.setStore(transformationsStore);
        newTransformationsCombo.setEditable(false);

        outputSet.add(newTransformationsCombo);

        transformationsComboBoxes.add(newTransformationsCombo);

        layout();
    }

    public void setEditTransformationCombo(DataSourceUI treeDataSourceUI) {
        // Save the date this data source was edited
        treeDataSourceUI.setUsed(new Date());

        setEditTransformationComboWithLoad(treeDataSourceUI);
    }

    public void setResetOutputSet(DataProviderUI par){
        parent = par;
        dataSourceUI = null;
        recordSet.clear();
        description.clear();

        resetTransCombo();
        repaint();
    }

    public void reloadTransformations(){
        AsyncCallback<List<TransformationUI>> callback = new AsyncCallback<List<TransformationUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<TransformationUI> mdTrans) {
                transformationsStore.removeAll();
                transformationsStore.add(new TransformationUI("-", "", "", "", "","","",false));
                transformationsStore.add(mdTrans);
                firstTransformationsCombo.setValue(firstTransformationsCombo.getStore().getAt(0));
            }
        };
        repoxService.getFullTransformationsList(callback);
    }

    private void resetTransCombo(){
        if(transformationsComboBoxes.size() > 1){
            for(int i = transformationsComboBoxes.size()-1; i>0; i--){
                if(!transformationsComboBoxes.get(i).equals(firstTransformationsCombo)){
                    outputSet.remove(transformationsComboBoxes.get(i));
                    transformationsComboBoxes.remove(i);
                }
            }
        }

        reloadTransformations();
    }

    private void setEditTransformationComboWithLoad(final DataSourceUI dataSourceUI) {
        firstTransformationsCombo.getStore().clearFilters();

        AsyncCallback<List<TransformationUI>> callback = new AsyncCallback<List<TransformationUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<TransformationUI> mdTrans) {
                List<TransformationUI> dsMetadataTransformations = dataSourceUI.getMetadataTransformations();
                int metadataTransformationsSize = dsMetadataTransformations.size();

                transformationsStore.removeAll();
                transformationsStore.add(new TransformationUI("-", "", "", "", "","","",false));
                transformationsStore.add(mdTrans);

                if(metadataTransformationsSize > 0){
                    for(TransformationUI comboSel: firstTransformationsCombo.getStore().getModels()){
                        if(comboSel.get("identifier").equals(dsMetadataTransformations.get(0).getIdentifier()))
                            firstTransformationsCombo.setValue(comboSel);
                    }

                    for(int i=1; i<metadataTransformationsSize; i++){
                        ComboBox<TransformationUI> newTransformationsCombo = new ComboBox<TransformationUI>();
                        newTransformationsCombo.setHideLabel(true);
                        newTransformationsCombo.setPosition(155,0);
                        newTransformationsCombo.setDisplayField("dsStringFormat");
                        newTransformationsCombo.setValue(transformationsStore.getModels().get(0));
                        newTransformationsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
                        newTransformationsCombo.setStore(transformationsStore);
                        newTransformationsCombo.setEditable(false);

                        for(TransformationUI comboSel: newTransformationsCombo.getStore().getModels()){
                            if(comboSel.get("identifier").equals(dsMetadataTransformations.get(i).getIdentifier()))
                                newTransformationsCombo.setValue(comboSel);
                        }

                        outputSet.add(newTransformationsCombo);

                        transformationsComboBoxes.add(newTransformationsCombo);
                    }
                } else
                    firstTransformationsCombo.setValue(firstTransformationsCombo.getStore().getAt(0));
            }
        };
        repoxService.getFullTransformationsList(callback);
    }

    public void saveDataSource(final DataSourceUI dataSourceUI, final String oldDataSetId, String type, String schema,
                               String metadataNamespace,String metadataFormat,String name,String nameCode, String exportPath) {
        mask(HarvesterUI.CONSTANTS.saveDataSetMask());
        AsyncCallback<SaveDataResponse> callback = new AsyncCallback<SaveDataResponse>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(SaveDataResponse response) {
                unmask();
                ResponseState responseState = response.getResponseState();
                if(responseState == ResponseState.INVALID_ARGUMENTS) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.invalidArguments());
                    dataSourceUI.setDataSourceSet(oldDataSetId);
                    return;
                } else if(responseState == ResponseState.URL_MALFORMED) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.oaiUrlMalformed());
                    return;
                } else if(responseState == ResponseState.URL_NOT_EXISTS) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.oaiUrlNotExists());
                    return;
                } else if(responseState == ResponseState.HTTP_URL_MALFORMED) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.httpUrlMalformed());
                    return;
                } else if(responseState == ResponseState.HTTP_URL_NOT_EXISTS) {
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.httpUrlNotExists());
                    return;
                }else if(responseState == ResponseState.NOT_FOUND){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.dataSetNotFound());
                    return;
                } else if(responseState == ResponseState.INCOMPATIBLE_TYPE){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.incompatibleType());
                    return;
                } else if(responseState == ResponseState.ERROR_DATABASE){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.errorAccessDatabase());
                    return;
                } else if(responseState == ResponseState.ALREADY_EXISTS){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.dataSetAlreadyExists());
                    dataSourceUI.setDataSourceSet(oldDataSetId);
                    return;
                } else if(responseState == ResponseState.FTP_CONNECTION_FAILED){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.ftpConnectionFailed());
                    return;
                } else if(responseState == ResponseState.OTHER){
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.errorSaveDataSet());
                    return;
                }

                submit();
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveDataSet(), HarvesterUI.CONSTANTS.saveDataSetSuccess());

                // Special case for data source set change
                if(History.getToken().contains("VIEW_DS"))
                    Dispatcher.forwardEvent(AppEvents.ViewDataSetInfo,dataSourceUI);
                else
                    PageUtil.setActivePage(response.getPage());

                Dispatcher.forwardEvent(AppEvents.HideDataSourceForm);
            }
        };

        // Add General attributes
        String record_set = recordSet.getValue();
        String desc = description.getValue();

        dataSourceUI.setName(desc.trim());
        dataSourceUI.setDescription(desc != null ? desc.trim() : "");
        dataSourceUI.setDataSourceSet(record_set != null ? record_set.trim() : "");
        dataSourceUI.setSchema(schema.trim());
        dataSourceUI.setMetadataNamespace(metadataNamespace.trim());

        String oaiSchemas = metadataFormat.trim();
        if(transformationsComboBoxes.size() >0 && !transformationsComboBoxes.get(0).getValue().getDestFormat().equals("")) {
            oaiSchemas = metadataFormat + " | " + transformationsComboBoxes.get(0).getValue().getDestFormat();
        }
        dataSourceUI.setMetadataFormat(oaiSchemas != null ? oaiSchemas.trim() : "");

        List<TransformationUI> transformations = new ArrayList<TransformationUI>();
        for(ComboBox<TransformationUI> transBox : transformationsComboBoxes){
            if(!transBox.getValue().getIdentifier().equals("-")){
                transformations.add(transBox.getValue());
            }
        }
        dataSourceUI.setMetadataTransformations(transformations);

        // Europeana Fields
        if(HarvesterUI.getProjectType().equals("EUROPEANA")) {
            String exprtP;
            if(exportPath == null) {
                AdminForm adminForm = (AdminForm) Registry.get("adminConfig");
                exprtP = adminForm.getDefaultExportFolder() + "/" + record_set;
            } else {
                exprtP= exportPath;
            }
            dataSourceUI.setName(name != null ? name.trim() : "");
            dataSourceUI.setNameCode(nameCode != null ? nameCode.trim() : "");
//            dataSourceUI.setExportDirectory(exprtP != null ? exprtP.trim() : "");
        }

        if(dataSourceUI.getExportDirectory().isEmpty()) {
            AdminForm adminForm = (AdminForm) Registry.get("adminConfig");
            String dsExportPath;
            if(HarvesterUI.getProjectType().equals("EUROPEANA"))
                dsExportPath = adminForm.getDefaultExportFolder() + "/" + record_set;
            else
                dsExportPath = adminForm.getDefaultExportFolder() + "/" + record_set + "/export";
            dataSourceUI.setExportDirectory(dsExportPath);
        } else {
            dataSourceUI.setExportDirectory(dataSourceUI.getExportDirectory());
        }

        //Post-Ingest Services
        dataSourceUI.getRestServiceUIList().clear();
        if(dataSourceServicesPanel != null && dataSourceServicesPanel.isExpanded()){
            dataSourceUI.setExternalServicesRunType(dataSourceServicesPanel.getExecutionTypeCombo().getSimpleValue());

            for(Component fieldSetC : dataSourceServicesPanel.getItems()){
                FieldSetWithExternalService fieldSet = (FieldSetWithExternalService)fieldSetC;
                for(Component component : fieldSet.getItems()){
                    for(ServiceParameterUI serviceParameterUI: fieldSet.getExternalServiceUI().getServiceParameters()){
                        if(component.getId().equals(serviceParameterUI.getId())){
                            // TODO: different field types
                            if(component instanceof TextField && !(component instanceof DateField) && !(component instanceof SimpleComboBox)){
                                serviceParameterUI.setValue(((TextField<String>)component).getValue());
                            }else if(component instanceof DateField){
                                serviceParameterUI.setValue(((DateField)component).getValue().toString());
                            }else if(component instanceof CheckBoxGroup){
                                CheckBoxGroup checkBoxGroup = (CheckBoxGroup)component;
                                CheckBox checkBox = (CheckBox)checkBoxGroup.get(0);
                                serviceParameterUI.setValue(checkBox.getValue().toString());
                            }else if(component instanceof SimpleComboBox){
                                serviceParameterUI.setValue(((SimpleComboBox<String>)component).getSimpleValue());
                            }else if(component instanceof DataSetListParameter){
                                serviceParameterUI.setValue(((DataSetListParameter)component).getTextField().getValue());
                            }
                        }
                    }
                }
                ExternalServiceUI currentExternalService = fieldSet.getExternalServiceUI();
                ExternalServiceUI newExternalService = new ExternalServiceUI(currentExternalService.getId(),
                        currentExternalService.getName(),currentExternalService.getUri(),
                        currentExternalService.getStatusUri(),
                        currentExternalService.getType(),
                        new ArrayList<ServiceParameterUI>());
                copyServiceParameters(currentExternalService,newExternalService);
                dataSourceUI.getRestServiceUIList().add(newExternalService);
            }
        }
        dataSetOperationsService.saveDataSource(edit, type, oldDataSetId, dataSourceUI,PageUtil.getCurrentPageSize(), callback);
    }

    protected void copyServiceParameters(ExternalServiceUI toCopy, ExternalServiceUI newService){
        for(ServiceParameterUI serviceParameterUI: toCopy.getServiceParameters()){
            ServiceParameterUI newSPUI = new ServiceParameterUI(serviceParameterUI.getName(),
                    serviceParameterUI.getType(),serviceParameterUI.getRequired(),serviceParameterUI.getExample(),
                    serviceParameterUI.getSemantics());
            newSPUI.setValue(serviceParameterUI.getValue());
            newService.getServiceParameters().add(newSPUI);
        }
    }

    public String getDataSetId() {
        return recordSet.getValue();
    }

    public abstract String getMetadataFormat();
    public abstract String getFolderPath();
    public abstract String getSchema();

    public void resetLayout(){
        super.layout(true);
        outputSet.layout(true);
        dataSourceServicesPanel.layout(true);
        layout(true);
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
        resetLayout();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        resetLayout();
    }
}
