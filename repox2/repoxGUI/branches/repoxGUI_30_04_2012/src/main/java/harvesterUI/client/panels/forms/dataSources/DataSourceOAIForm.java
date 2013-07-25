package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.models.Attribute;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.DataSourceUI;

import java.util.List;
import java.util.Map;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-03-2011
 * Time: 14:14
 */
public class DataSourceOAIForm extends DataSourceForm {

    private FieldSet dataSet;
    private FormData formData;
    private String oldDataSetId = "";

    // Fields
    private TextField<String> oaiUrl, oaiSet, otherField,schema, metadataNamespace;
    private ComboBox<ModelData> metadataFormatCombo;
    private SimpleComboBox<String> setsCombo, mdPrefixesCombo;
    private LabelField currentField;

    // Europeana Fields
    private TextField<String> name, nameCode,exportPath;

    public DataSourceOAIForm(FormData data) {
        super(data);
        formData = data;
        setHeaderVisible(false);
        setLayout(new FitLayout());
        setBodyBorder(false);

        createDataSet();
        add(dataSet);
        add(createOutputSet());
        add(dataSourceServicesPanel);

        Button b = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                String metadataFormat;
                if(metadataFormatCombo.getValue().get("value").equals(HarvesterUI.CONSTANTS.other()))
                    metadataFormat = otherField.getValue();
                else if(mdPrefixesCombo.isVisible()) {
                    metadataFormat = mdPrefixesCombo.getValue().getValue();
                } else
                    metadataFormat = (String)metadataFormatCombo.getValue().get("value");

                String oai_url = oaiUrl.getValue().trim();
                String oai_set;
                if(oaiSet.isVisible())
                    oai_set = oaiSet.getValue();
                else
                    oai_set = setsCombo.getValue().getValue();

                String desc = description.getValue();
                if(dataSourceUI == null) {
                    dataSourceUI = new DataSourceUI(parent, desc.trim(), "", metadataFormat + " | ese", "OAI-PMH " + metadataFormat.trim(),
                            parent.getCountry(),desc.trim(), "", oai_url.trim(), oai_set != null ? oai_set.trim() : "",
                            "", "IdGenerated",metadataFormat);
                }

                dataSourceUI.setIngest("OAI-PMH " + metadataFormat.trim());
                dataSourceUI.setSourceMDFormat(metadataFormat != null ? metadataFormat.trim() : "");
                dataSourceUI.setOaiSource(oai_url != null ? oai_url.trim() : "");
                dataSourceUI.setOaiSet((oai_set != null && !oai_set.equals("") && !oai_set.equals("--none--")) ? oai_set.trim() : null);

                dataSourceUI.setExportDirectory(exportPath.getValue() != null ? exportPath.getValue().trim() : "");

                if(HarvesterUI.getProjectType().equals("EUROPEANA"))
                    saveDataSource(dataSourceUI,oldDataSetId,"oai",schema.getValue(),metadataNamespace.getValue(),
                            metadataFormat,name.getValue(),nameCode.getValue(),exportPath.getValue());
                else
                    saveDataSource(dataSourceUI,oldDataSetId,"oai",schema.getValue(),metadataNamespace.getValue(),
                            metadataFormat,"","","");
            }
        });
        addButton(b);
        addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                Dispatcher.forwardEvent(AppEvents.HideDataSourceForm);
            }
        }));

        setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(this);
        binding.addButton(b);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setScrollMode(Style.Scroll.AUTO);
    }

    private void createDataSet() {
        dataSet = new FieldSet();
        dataSet.setHeading(HarvesterUI.CONSTANTS.dataSet());
        dataSet.setAutoWidth(true);
        dataSet.setAutoHeight(true);
        dataSet.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));

        oaiUrl = new TextField<String>();
        oaiUrl.setId("oaiUrlField");
        oaiUrl.setAllowBlank(false);

        Button check = new Button(HarvesterUI.CONSTANTS.check(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<Map<String,List<String>>> callback = new AsyncCallback<Map<String,List<String>>>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(Map<String,List<String>> result) {
                        if(result == null) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.invalidUrl());
                            return;
                        }else if(result.get("ERROR") != null) {
                            if(result.get("ERROR").get(0).equals("URL_MALFORMED")) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.oaiUrlMalformed());
                                return;
                            } else if(result.get("ERROR").get(0).equals("URL_NOT_EXISTS")) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.oaiUrlNotExists());
                                return;
                            }
                        }

                        setsCombo.getStore().removeAll();
                        setsCombo.add("--none--");
                        setsCombo.add(result.get("sets"));
                        setsCombo.getStore().sort("value",Style.SortDir.ASC);
                        setsCombo.setValue(setsCombo.getStore().getAt(0));
                        setsCombo.show();
                        setsCombo.setAllowBlank(false);
                        oaiSet.hide();

                        if(result.get("mdPrefixes").size() > 0){
                            mdPrefixesCombo.getStore().removeAll();
                            mdPrefixesCombo.add(result.get("mdPrefixes"));
                            mdPrefixesCombo.getStore().sort("value",Style.SortDir.ASC);
                            mdPrefixesCombo.setValue(mdPrefixesCombo.getStore().getAt(0));
                            mdPrefixesCombo.show();
                            mdPrefixesCombo.setAllowBlank(false);
                            metadataFormatCombo.hide();
                        }

                        submit();
                        otherField.hide();
                    }
                };
                if(oaiUrl.getValue() == null || oaiUrl.getValue().trim().equals(""))
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.pleaseInsertUrl());
                else
                    dataSetOperationsService.checkOAIURL(oaiUrl.getValue().trim(), callback);
            }
        });
        Button addAll = new Button(HarvesterUI.CONSTANTS.addAll(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                // Check required data in Europeana
                if(HarvesterUI.getProjectType().equals("EUROPEANA") && (name.getValue() == null || nameCode.getValue() == null)) {
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.pleaseFillNameAndNamecode());
                    return;
                }

                AsyncCallback callback = new AsyncCallback() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        unmask();
                    }
                    public void onSuccess(Object result) {
                        if(result.equals("URL_MALFORMED")) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.oaiUrlMalformed());
                            unmask();
                            return;
                        } else if(result.equals("URL_NOT_EXISTS")) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.oaiUrlNotExists());
                            unmask();
                            return;
                        }
                        submit();
                        unmask();
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.allDataSetsAddedSuccess());
                        Dispatcher.get().dispatch(AppEvents.LoadMainData);
                        Dispatcher.forwardEvent(AppEvents.HideDataSourceForm);
                    }
                };
                mask(HarvesterUI.CONSTANTS.addAllDataSetsMask());
                String metadataFormat;
                if(metadataFormatCombo.getValue().get("value").equals(HarvesterUI.CONSTANTS.other()))
                    metadataFormat = otherField.getValue();
                else
                    metadataFormat = (String)metadataFormatCombo.getValue().get("value");
                String schem = "";
                String mtdNamespace = "";
                if(HarvesterUI.getProjectType().equals("EUROPEANA")){
                    dataSetOperationsService.addAllOAIURL(oaiUrl.getValue().trim(), parent.getId(), schem,mtdNamespace,
                            metadataFormat, name.getValue(), nameCode.getValue(), exportPath.getValue(), callback);
                }else {
                    dataSetOperationsService.addAllOAIURL(oaiUrl.getValue().trim(), parent.getId(), schem,mtdNamespace,
                            metadataFormat, "", "", "", callback);
                }
            }
        });

        LayoutContainer container = new LayoutContainer();
        HBoxLayout oaiUrlCotnainerLayout = new HBoxLayout();
        oaiUrlCotnainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        container.setLayout(oaiUrlCotnainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.oaiUrl()+ HarvesterUI.REQUIRED_STR);
        label.setWidth(SPECIAL_FIELDS_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        container.add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1);
        container.add(oaiUrl, flex);
        container.add(check, new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        container.add(addAll, new HBoxLayoutData(new Margins(0, 0, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        dataSet.add(container,formData);

        setsCombo = new SimpleComboBox<String>();
//        setsCombo.setAllowBlank(false);
//        setsCombo.setForceSelection(true);
        setsCombo.setFieldLabel(HarvesterUI.CONSTANTS.oaiSet());
//        setsCombo.add("<ALL>");
        setsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        dataSet.add(setsCombo,smallFixedFormData);

        mdPrefixesCombo = new SimpleComboBox<String>();
//        mdPrefixesCombo.setForceSelection(true);
//        mdPrefixesCombo.setAllowBlank(false);
        mdPrefixesCombo.setFieldLabel(HarvesterUI.CONSTANTS.metadataFormat());
        mdPrefixesCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        mdPrefixesCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem() != null) {
                    for(ModelData model : metadataFormatCombo.getStore().getModels()) {
                        if(model.get("value").equals(se.getSelectedItem().getValue())) {
                            schema.setValue((String)model.get("schema"));
                            metadataNamespace.setValue((String)model.get("namespace"));
                            break;
                        } else {
                            schema.clear();
                            metadataNamespace.clear();
                        }
                    }
                }
            }
        });
        dataSet.add(mdPrefixesCombo, smallFixedFormData);

        oaiSet = new TextField<String>();
        oaiSet.setFieldLabel(HarvesterUI.CONSTANTS.oaiSet());
        oaiSet.setId("oaiSetField");
        oaiSet.setAllowBlank(true);
        dataSet.add(oaiSet, formData);

        /*
         * Metadata Format ComboBox
        */

        final ListStore<ModelData> metadataFormatStore = new ListStore<ModelData>();

        Attribute current = new Attribute("country","*Current");
        current.set("schema", UtilManager.getSchema("ese"));
        current.set("namespace",UtilManager.getNamespace("ese"));
        Attribute ese = new Attribute("country","ese");
        ese.set("schema",UtilManager.getSchema("ese"));
        ese.set("namespace",UtilManager.getNamespace("ese"));
        Attribute marc = new Attribute("country","MarcXchange");
        marc.set("schema",UtilManager.getSchema("MarcXchange"));
        marc.set("namespace",UtilManager.getNamespace("MarcXchange"));
        Attribute tel = new Attribute("country","tel");
        tel.set("schema",UtilManager.getSchema("tel"));
        tel.set("namespace",UtilManager.getNamespace("tel"));
        Attribute oai = new Attribute("country","oai_dc");
        oai.set("schema",UtilManager.getSchema("oai_dc"));
        oai.set("namespace",UtilManager.getNamespace("oai_dc"));
        Attribute nlm_ai = new Attribute("country","NLM-AI");
        nlm_ai.set("schema",UtilManager.getSchema("NLM-AI"));
        nlm_ai.set("namespace",UtilManager.getNamespace("NLM-AI"));
        Attribute nlm_book = new Attribute("country","NLM-Book");
        nlm_book.set("schema",UtilManager.getSchema("NLM-Book"));
        nlm_book.set("namespace",UtilManager.getNamespace("NLM-Book"));
        Attribute lido = new Attribute("country","lido");
        lido.set("schema",UtilManager.getSchema("lido"));
        lido.set("namespace",UtilManager.getNamespace("lido"));
        Attribute edm = new Attribute("country","edm");
        edm.set("schema",UtilManager.getSchema("edm"));
        edm.set("namespace",UtilManager.getNamespace("edm"));
        Attribute other = new Attribute("country",HarvesterUI.CONSTANTS.other());
        other.set("schema","");
        other.set("namespace","");
        metadataFormatStore.add(current);
        metadataFormatStore.add(ese);
        metadataFormatStore.add(marc);
        metadataFormatStore.add(tel);
        metadataFormatStore.add(oai);
        metadataFormatStore.add(nlm_ai);
        metadataFormatStore.add(nlm_book);
        metadataFormatStore.add(lido);
        metadataFormatStore.add(edm);
        metadataFormatStore.add(other);

        metadataFormatCombo = new ComboBox<ModelData>();
        metadataFormatCombo.setFieldLabel(HarvesterUI.CONSTANTS.metadataFormat());
        metadataFormatCombo.setDisplayField("value");
        metadataFormatCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        metadataFormatCombo.setStore(metadataFormatStore);
        metadataFormatCombo.setId("metadatFormatComboId");
        metadataFormatCombo.setValue(metadataFormatStore.getModels().get(1));
        metadataFormatCombo.setEditable(false);
        metadataFormatCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals(HarvesterUI.CONSTANTS.other())) {
                    otherField.show();
                    dataSet.add(otherField,smallFixedFormData);
                    dataSet.insert(otherField, dataSet.getItems().indexOf(dataSet.getItemByItemId("metadatFormatComboId")) + 1);
                    otherField.setAllowBlank(false);
                    layout();
                } else {
                    otherField.hide();
                    otherField.setAllowBlank(true);
                }

                if(se.getSelectedItem().get("value").equals("*Current")){
                    currentField.setLayoutData(formData);
                    dataSet.insert(currentField, dataSet.getItems().indexOf(dataSet.getItemByItemId("metadatFormatComboId")) + 1);
                    layout();
                }else{
                    if(dataSet.getItems().contains(currentField))
                        dataSet.remove(currentField);
                }

                schema.setValue((String)se.getSelectedItem().get("schema"));
                metadataNamespace.setValue((String)se.getSelectedItem().get("namespace"));
            }
        });
        dataSet.add(metadataFormatCombo,smallFixedFormData);

        otherField = new TextField<String>();
        otherField.setFieldLabel(HarvesterUI.CONSTANTS.other()+ HarvesterUI.REQUIRED_STR);
        otherField.setId("otherField");
        otherField.hide();

        currentField = new LabelField();
        currentField.setFieldLabel("Current Value");
        //TODO : SET FIELD VALUE = CURRENT DO REPOX

        schema = new TextField<String>();
        schema.setFieldLabel(HarvesterUI.CONSTANTS.schema() + HarvesterUI.REQUIRED_STR);
        schema.setId("schemaField");
        schema.setValue((String)metadataFormatCombo.getSelection().get(0).get("schema"));
        schema.setAllowBlank(false);
        dataSet.add(schema, formData);

        metadataNamespace = new TextField<String>();
        metadataNamespace.setFieldLabel(HarvesterUI.CONSTANTS.metadataNamespace()+ HarvesterUI.REQUIRED_STR);
        metadataNamespace.setId("mtdNmspacField");
        metadataNamespace.setValue((String)metadataFormatCombo.getSelection().get(0).get("namespace"));
        metadataNamespace.setAllowBlank(false);
        dataSet.add(metadataNamespace, formData);

        exportPath = new TextField<String>();
        exportPath.setFieldLabel(HarvesterUI.CONSTANTS.exportPath());
        exportPath.setId("exportPathField");
        dataSet.add(exportPath, formData);
    }

    public void setEditMode(DataSourceUI ds){
        setScrollMode(Style.Scroll.AUTO);
        dataSourceUI = ds;
        edit = true;
        oldDataSetId = ds.getDataSourceSet();
        boolean foundMatch = false;

        // clear combo boxes filters
        metadataFormatCombo.getStore().clearFilters();

        for(ModelData comboSel: metadataFormatCombo.getStore().getModels()) {
            if(comboSel.get("value").equals(dataSourceUI.getSourceMDFormat())) {
                foundMatch = true;
                metadataFormatCombo.setValue(comboSel);
            }
        }

        if(!foundMatch) {
            metadataFormatCombo.setValue(metadataFormatCombo.getStore().findModel("value",HarvesterUI.CONSTANTS.other()));
        }

        otherField.setValue(dataSourceUI.getSourceMDFormat());

        setEditTransformationCombo(dataSourceUI);
        dataSourceServicesPanel.setEditServices(dataSourceUI);

        schema.setValue(dataSourceUI.getSchema());
        metadataNamespace.setValue(dataSourceUI.getMetadataNamespace());
        oaiUrl.setValue(dataSourceUI.getOaiSource());
        oaiSet.setValue(dataSourceUI.getOaiSet());
        oaiSet.show();
        metadataFormatCombo.show();
        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        exportPath.setValue(dataSourceUI.getExportDirectory());

        setsCombo.hide();
        setsCombo.setAllowBlank(true);
        mdPrefixesCombo.hide();
        mdPrefixesCombo.setAllowBlank(true);

        // Europeana Fields
        if(HarvesterUI.getProjectType().equals("EUROPEANA")) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }
    }

    public void resetValues(DataProviderUI parent) {
        edit = false;
        oldDataSetId = "";
        oaiUrl.clear();
        oaiSet.clear();
        currentField.clear();
        otherField.clear();
        setsCombo.hide();
        mdPrefixesCombo.hide();
        oaiSet.show();
        metadataFormatCombo.show();
        exportPath.clear();

        setResetNamespaces();
        setResetOutputSet(parent);
        dataSourceServicesPanel.resetValues();

        metadataFormatCombo.getStore().clearFilters();
        metadataFormatCombo.setValue(metadataFormatCombo.getStore().getModels().get(1));

        // Europeana Fields
        if(HarvesterUI.getProjectType().equals("EUROPEANA")) {
            name.clear();
            nameCode.clear();
        }
    }

    public void addEuropeanaFields() {
        name = new TextField<String>();
        name.setFieldLabel(HarvesterUI.CONSTANTS.name());
        name.setId("nameField");
        dataSet.add(name, formData);

        nameCode = new TextField<String>();
        nameCode.setFieldLabel(HarvesterUI.CONSTANTS.nameCode());
        nameCode.setId("nameCodeField");
        dataSet.add(nameCode, formData);
    }

    public String getMetadataFormat() {
        if(mdPrefixesCombo.isVisible())
            return mdPrefixesCombo.getSimpleValue();
        else
            return metadataFormatCombo.getValue().get("value");
    }

    public String getFolderPath() {
        return null;
    }

    public String getSchema() {
        return schema.getValue();
    }
}
