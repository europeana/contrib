package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
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

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-03-2011
 * Time: 14:56
 */
public class DataSourceFolderForm extends DataSourceForm {

    private FormData formData;
    private LabelField currentField;
    ComboBox<ModelData> isoVariant;
    ComboBox<ModelData> characterEncoding;
    private FieldSet dataSet;
    private String oldDataSetId = "";

    // Fields
    ComboBox<ModelData> metadataFormatCombo, idPolicyCombo, retrieveVariantCombo, authenticationCombo;
    TextField<String> rootName, otherField, idXPathField,
            server, folderFtp, httpUrl, user, password, folderPath, schema, metadataNamespace;

    // Europeana Fields
    TextField<String> name, nameCode, exportPath;

    public DataSourceFolderForm(FormData data) {
        super(data);
        formData = data;
        setHeaderVisible(false);
        setBodyBorder(false);
        setLayout(new FitLayout());
        setLayoutOnChange(true);

        initOptionalFields();
        createIDPolicyBoxes();
        createRetrieveVariant();

        createFolderForm();
        createISOBoxes();

        add(createOutputSet());
        add(dataSourceServicesPanel);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setScrollMode(Style.Scroll.AUTO);
    }

    private void createFolderForm() {
        dataSet = new FieldSet();
        dataSet.setAutoHeight(true);
        dataSet.setAutoWidth(true);
        dataSet.setHeading(HarvesterUI.CONSTANTS.dataSet());

        dataSet.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));

        // test
        dataSet.add(idXPathField,formData);
        idXPathField.hide();
        idXPathField.setAllowBlank(true);
        dataSet.add(folderPath, formData);
        folderPath.hide();
        dataSet.add(httpUrl, formData);
        httpUrl.hide();
        dataSet.add(server, formData);
        server.hide();

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
        Attribute iso = new Attribute("country","ISO2709");
        iso.set("schema",UtilManager.getSchema("ISO2709"));
        iso.set("namespace",UtilManager.getNamespace("ISO2709"));
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
        metadataFormatStore.add(iso);
        metadataFormatStore.add(marc);
        metadataFormatStore.add(tel);
        metadataFormatStore.add(oai);
        metadataFormatStore.add(nlm_ai);
        metadataFormatStore.add(nlm_book);
        metadataFormatStore.add(lido);
        metadataFormatStore.add(edm);
        metadataFormatStore.add(other);

        metadataFormatCombo = new ComboBox<ModelData>();
        metadataFormatCombo.setFieldLabel(HarvesterUI.CONSTANTS.metadataFormat() + HarvesterUI.REQUIRED_STR);
        metadataFormatCombo.setDisplayField("value");
        metadataFormatCombo.setId("europeana_mtdformatcombo");
        metadataFormatCombo.setValue(metadataFormatStore.getModels().get(1));
        metadataFormatCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        metadataFormatCombo.setStore(metadataFormatStore);
        metadataFormatCombo.setEditable(false);
        metadataFormatCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals(HarvesterUI.CONSTANTS.other())) {
                    otherField.show();
                    dataSet.add(otherField,smallFixedFormData);
                    dataSet.insert(otherField, dataSet.getItems().indexOf(dataSet.getItemByItemId("europeana_mtdformatcombo")) + 1);
                    otherField.setAllowBlank(false);
                    layout();
                } else {
                    otherField.hide();
                    otherField.setAllowBlank(true);
                }
                if(se.getSelectedItem().get("value").equals("MarcXchange")) {
                    rootName.setEnabled(false);
                } else {
                    rootName.setEnabled(true);
                }
                if(se.getSelectedItem().get("value").equals("*Current")) {
                    currentField.setLayoutData(formData);
                    dataSet.insert(currentField, dataSet.getItems().indexOf(dataSet.getItemByItemId("europeana_mtdformatcombo")) + 1);
                    layout();
                } else {
                    if(dataSet.getItems().contains(currentField))
                        dataSet.remove(currentField);
                }
                if(se.getSelectedItem().get("value").equals("ISO2709")) {
                    dataSet.add(isoVariant,smallFixedFormData);
                    dataSet.insert(isoVariant,dataSet.getItems().indexOf(dataSet.getItemByItemId("europeana_mtdformatcombo")) + 1);
                    dataSet.add(characterEncoding,smallFixedFormData);
                    dataSet.insert(characterEncoding,dataSet.getItems().indexOf(dataSet.getItemByItemId("europeana_mtdformatcombo")) + 2);
                    layout();
                } else {
                    if(dataSet.getItems().contains(isoVariant)) {
                        dataSet.remove(isoVariant);
                        dataSet.remove(characterEncoding);
                    }
                }

                schema.setValue((String)se.getSelectedItem().get("schema"));
                metadataNamespace.setValue((String)se.getSelectedItem().get("namespace"));
            }
        });
        dataSet.add(metadataFormatCombo, smallFixedFormData);

        otherField = new TextField<String>();
        otherField.setFieldLabel(HarvesterUI.CONSTANTS.other()+ HarvesterUI.REQUIRED_STR);
        otherField.setId("otherField");
        otherField.setLayoutData(formData);
        dataSet.insert(otherField, 3);
        otherField.hide();

        currentField = new LabelField();
        currentField.setFieldLabel("Current Value: ");
        //TODO : SET FIELD VALUE = CURRENT DO REPOX

        schema = new TextField<String>();
        schema.setFieldLabel(HarvesterUI.CONSTANTS.schema()+ HarvesterUI.REQUIRED_STR);
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

        rootName = new TextField<String>();
        rootName.setFieldLabel(HarvesterUI.CONSTANTS.recordRootName());
        rootName.setId("rootNameField");
        dataSet.add(rootName, formData);

        /*
         *ID Policy ComboBox
        */
        dataSet.add(idPolicyCombo,smallFixedFormData);

        dataSet.add(retrieveVariantCombo,smallFixedFormData);
        // Add default folder value fields
        addFolderFields();

        Button b = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                String metadataFormat;
                if(metadataFormatCombo.getValue().get("value").equals(HarvesterUI.CONSTANTS.other()))
                    metadataFormat = otherField.getValue();
                else
                    metadataFormat = (String)metadataFormatCombo.getValue().get("value");
                String charEnc = characterEncoding.getValue().get("value");
                String idPolicy;
                if(idPolicyCombo.getValue().get("value").equals("ID Generated"))
                    idPolicy = "IdGenerated";
                else
                    idPolicy = "IdExtracted";
                String idXPath = idXPathField.getValue();
                String variant = retrieveVariantCombo.getValue().get("value");
                String dsRetrieveStrat = "";
                if(variant.equals("Folder"))
                    dsRetrieveStrat = "pt.utl.ist.repox.marc.DataSourceFolder";
                else if(variant.equals("FTP")) {
                    dsRetrieveStrat = "pt.utl.ist.repox.ftp.DataSourceFtp";
                } else if(variant.equals("HTTP")) {
                    dsRetrieveStrat = "pt.utl.ist.repox.ftp.DataSourceHTTP";
                }
                String serverUrl = server.getValue();
                String userId = user.getValue();
                String pwd = password.getValue();
                String folderFtpStr = folderFtp.getValue();
                String httpURL = httpUrl.getValue();
                if (httpURL != null && !httpURL.startsWith("http://") && !httpURL.startsWith("https://")) {
                    httpURL = "http://" + httpURL;
                }
                String folderPathStr = folderPath.getValue();
                String recordRootStr = rootName.getValue();
                String record_set = recordSet.getValue();
                String desc = description.getValue();

                String isoVariantStr = "";
                if(isoVariant.getValue().get("value").equals("Standard"))
                    isoVariantStr = "pt.utl.ist.marc.iso2709.IteratorIso2709";
                else if(isoVariant.getValue().get("value").equals("Variant From Albania")) {
                    isoVariantStr = "pt.utl.ist.marc.iso2709.IteratorIso2709Albania";
                } else if(isoVariant.getValue().get("value").equals("Variant From Ukraine")) {
                    isoVariantStr = "pt.utl.ist.marc.iso2709.IteratorIso2709Ukraine";
                }

                if(dataSourceUI == null) {
                    dataSourceUI = new DataSourceUI(parent, desc.trim(), "", metadataFormat.trim() + " | ese", "Folder " + metadataFormat.trim(),
                            parent.getCountry().trim(),desc.trim(), "", "", "",
                            "", idPolicy.trim(), metadataFormat.trim());
                }

                dataSourceUI.setIngest("Folder " + metadataFormat.trim());
                dataSourceUI.setSourceMDFormat(metadataFormat.trim());
                if(httpURL != null){
                    dataSourceUI.setHttpURL(httpURL.trim());
                }
                dataSourceUI.setRecordIdPolicy(idPolicy.trim());
                dataSourceUI.setCharacterEncoding(charEnc.trim());
                dataSourceUI.setIdXPath(idXPath != null ? idXPath.trim() : "");
                dataSourceUI.setRetrieveStartegy(dsRetrieveStrat.trim());
                dataSourceUI.setServer(serverUrl != null ? serverUrl.trim() : "");
                dataSourceUI.setUser(userId != null ? userId.trim() : "");
                dataSourceUI.setPassword(pwd != null ? pwd.trim(): "");
                dataSourceUI.setFolderPath(folderFtpStr != null ? folderFtpStr.trim() : "");
                if(dataSourceUI.getFolderPath() != null && (dataSourceUI.getFolderPath().startsWith("\\")
                        || dataSourceUI.getFolderPath().startsWith("/"))){
                    dataSourceUI.setFolderPath(dataSourceUI.getFolderPath().substring(1));
                }
                dataSourceUI.setDirPath(folderPathStr != null ? folderPathStr.trim() : null);
                dataSourceUI.setRecordRootName(recordRootStr != null ? recordRootStr.trim() : "");
                dataSourceUI.setDataSourceSet(record_set.trim());
                dataSourceUI.setIsoVariant(isoVariantStr.trim());

                List<String> namespaces = namespacePanelExtension.getFinalNamespacesList();

                dataSourceUI.setNamespaceList(namespaces);

                dataSourceUI.setExportDirectory(exportPath.getValue() != null ? exportPath.getValue().trim() : "");

                if(HarvesterUI.getProjectType().equals("EUROPEANA"))
                    saveDataSource(dataSourceUI,oldDataSetId,"folder",schema.getValue(),metadataNamespace.getValue(),
                            metadataFormat,name.getValue(),nameCode.getValue(),exportPath.getValue());
                else
                    saveDataSource(dataSourceUI,oldDataSetId,"folder",schema.getValue(),metadataNamespace.getValue(),
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

        add(dataSet);
    }

    private void createISOBoxes(){
        final ListStore<ModelData> isoVariantStore = new ListStore<ModelData>();
        isoVariantStore.add(new Attribute("country","Standard"));
        isoVariantStore.add(new Attribute("country","Variant From Albania"));
        isoVariantStore.add(new Attribute("country","Variant From Ukraine"));

        isoVariant = new ComboBox<ModelData>();
        isoVariant.setFieldLabel(HarvesterUI.CONSTANTS.iso2709Variant()+ HarvesterUI.REQUIRED_STR);
        isoVariant.setDisplayField("value");
        isoVariant.setValue(isoVariantStore.getModels().get(0));
        isoVariant.setTriggerAction(ComboBox.TriggerAction.ALL);
        isoVariant.setStore(isoVariantStore);
        isoVariant.setEditable(false);

        final ListStore<ModelData> characterEncStore = new ListStore<ModelData>();
        AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<String> charSets) {
                for(String charSet : charSets)
                    characterEncStore.add(new Attribute("country",charSet));
                characterEncoding.setValue(characterEncStore.getModels().get(0));
            }
        };
        repoxService.getFullCharacterEncodingList(callback);

        characterEncoding = new ComboBox<ModelData>();
        characterEncoding.setFieldLabel(HarvesterUI.CONSTANTS.characterEncoding()+ HarvesterUI.REQUIRED_STR);
        characterEncoding.setDisplayField("value");
        characterEncoding.setTriggerAction(ComboBox.TriggerAction.ALL);
        characterEncoding.setStore(characterEncStore);
        characterEncoding.setEditable(false);

        exportPath = new TextField<String>();
        exportPath.setFieldLabel(HarvesterUI.CONSTANTS.exportPath());
        exportPath.setId("exportPathField");
        dataSet.add(exportPath, formData);

    }

    private void createIDPolicyBoxes(){
        final ListStore<ModelData> idPolicyStore = new ListStore<ModelData>();
        idPolicyStore.add(new Attribute("country","ID Generated"));
        idPolicyStore.add(new Attribute("country","ID Extracted"));

        idPolicyCombo = new ComboBox<ModelData>();
        idPolicyCombo.setFieldLabel(HarvesterUI.CONSTANTS.idPolicy()+ HarvesterUI.REQUIRED_STR);
        idPolicyCombo.setId("idPolicy");
        idPolicyCombo.setDisplayField("value");
        idPolicyCombo.setValue(idPolicyStore.getModels().get(0));
        idPolicyCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        idPolicyCombo.setStore(idPolicyStore);
//        idPolicyCombo.setForceSelection(true);
        idPolicyCombo.setEditable(false);

        idXPathField = new TextField<String>();
        idXPathField.setAllowBlank(false);
        idXPathField.setId("idXpathField");
        idXPathField.setFieldLabel(HarvesterUI.CONSTANTS.identifierXpath()+ HarvesterUI.REQUIRED_STR);

//        namespaces = new FieldSet();
//        namespaces.setHeading("Namespaces");
//        namespaces.setCheckboxToggle(true);
//        FormLayout layout = new FormLayout();
//        layout.setLabelWidth(150);
//        namespaces.setLayout(layout);
//        namespaces.collapse();

        createNamespaceSet();

        idPolicyCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals("ID Extracted")){
                    dataSet.insert(idXPathField, dataSet.getItems().indexOf(dataSet.getItemByItemId("idPolicy")) + 1);
                    idXPathField.show();
                    idXPathField.setAllowBlank(false);
                    dataSet.add(namespaces,new FormData(MEDIUM_FORM_DATA));
                    dataSet.insert(namespaces, dataSet.getItems().indexOf(dataSet.getItemByItemId("idPolicy"))+2);
                    layout();
                }else{
                    if(dataSet.getItems().contains(idXPathField)){
                        dataSet.remove(idXPathField);
                        dataSet.remove(namespaces);
                    }
                }
            }
        });
    }

    private void createRetrieveVariant() {
        final ListStore<ModelData> retrieveVariantStore = new ListStore<ModelData>();
        retrieveVariantStore.add(new Attribute("country","Folder"));
        retrieveVariantStore.add(new Attribute("country","FTP"));
        retrieveVariantStore.add(new Attribute("country","HTTP"));

        retrieveVariantCombo = new ComboBox<ModelData>();
        retrieveVariantCombo.setFieldLabel(HarvesterUI.CONSTANTS.retrieveVariant()+ HarvesterUI.REQUIRED_STR);
        retrieveVariantCombo.setId("retrieveVariant");
        retrieveVariantCombo.setDisplayField("value");
        retrieveVariantCombo.setValue(retrieveVariantStore.getModels().get(0));
        retrieveVariantCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        retrieveVariantCombo.setStore(retrieveVariantStore);
        retrieveVariantCombo.setEditable(false);
        retrieveVariantCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals("Folder"))
                    addFolderFields();
                else {
                    folderPath.hide();
                    if(dataSet.getItems().contains(user)){
                        dataSet.remove(user);
                        dataSet.remove(password);
                    }
                }
                if(se.getSelectedItem().get("value").equals("FTP")){
                    addFTPFields();
                }
                else {
                    if(dataSet.getItems().contains(authenticationCombo)){
                        server.hide();
                        dataSet.remove(authenticationCombo);
                        dataSet.remove(folderFtp);
                    }
                }
                if(se.getSelectedItem().get("value").equals("HTTP"))
                    addHTTPFields();
                else {
                    httpUrl.hide();
                    if(dataSet.getItems().contains(user)){
                        dataSet.remove(user);
                        dataSet.remove(password);
                    }
                }
            }
        });
    }

    private void addFolderFields() {
        dataSet.add(folderPath,new FormData(MEDIUM_FORM_DATA));
        dataSet.insert(folderPath, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 1);
        folderPath.show();
        folderPath.setAllowBlank(false);
        server.setAllowBlank(true);
        httpUrl.setAllowBlank(true);
        layout();
    }

    private void addHTTPFields() {
        dataSet.add(httpUrl,new FormData(MEDIUM_FORM_DATA));
        dataSet.insert(httpUrl, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 1);
        httpUrl.show();
        folderPath.setAllowBlank(true);
        server.setAllowBlank(true);
        httpUrl.setAllowBlank(false);
        layout();
    }

    private void addFTPFields() {
        dataSet.add(server,new FormData(MEDIUM_FORM_DATA));
        dataSet.add(authenticationCombo,smallFixedFormData);
        dataSet.add(folderFtp,new FormData(MEDIUM_FORM_DATA));
        dataSet.insert(server, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 1);
        dataSet.insert(authenticationCombo, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 2);
        dataSet.insert(folderFtp, dataSet.getItems().indexOf(dataSet.getItemByItemId("retrieveVariant")) + 3);
        server.show();
        folderPath.setAllowBlank(true);
        server.setAllowBlank(false);
        httpUrl.setAllowBlank(true);
        layout();
        authenticationCombo.setValue(authenticationCombo.getStore().getModels().get(0));
    }

    private void initOptionalFields() {
        server = new TextField<String>();
        server.setAllowBlank(false);
        server.setId("serverFieldID");
        server.setValidationDelay(40000);
        server.setFieldLabel(HarvesterUI.CONSTANTS.server()+ HarvesterUI.REQUIRED_STR);

        user = new TextField<String>();
        user.setAllowBlank(false);
        user.setId("telFolderUserField");
        user.setFieldLabel(HarvesterUI.CONSTANTS.user()+ HarvesterUI.REQUIRED_STR);

        password = new TextField<String>();
        password.setAllowBlank(false);
        password.setId("telFolderPass");
        password.setValidationDelay(40000);
        password.setFieldLabel(HarvesterUI.CONSTANTS.password()+ HarvesterUI.REQUIRED_STR);

        httpUrl = new TextField<String>();
        httpUrl.setAllowBlank(false);
        httpUrl.setId("httpUrltelfolder");
        httpUrl.setValidationDelay(40000);
        httpUrl.setFieldLabel(HarvesterUI.CONSTANTS.httpUrl()+ HarvesterUI.REQUIRED_STR);

        folderFtp = new TextField<String>();
        folderFtp.setAllowBlank(false);
        folderFtp.setId("folderFtptel");
        folderFtp.setValidationDelay(40000);
        folderFtp.setFieldLabel(HarvesterUI.CONSTANTS.folderFtp()+ HarvesterUI.REQUIRED_STR);

        folderPath = new TextField<String>();
        folderPath.setAllowBlank(false);
        folderPath.setId("folderPathFieldTel");
        folderPath.setValidationDelay(40000);
        folderPath.setFieldLabel(HarvesterUI.CONSTANTS.folderPath()+ HarvesterUI.REQUIRED_STR);

        final ListStore<ModelData> authenticationStore = new ListStore<ModelData>();
        authenticationStore.add(new Attribute("country","Anonymous"));
        authenticationStore.add(new Attribute("country","Normal"));

        authenticationCombo = new ComboBox<ModelData>();
        authenticationCombo.setFieldLabel(HarvesterUI.CONSTANTS.authentication()+ HarvesterUI.REQUIRED_STR);
        authenticationCombo.setId("authentication");
        authenticationCombo.setDisplayField("value");
        authenticationCombo.setValue(authenticationStore.getModels().get(0));
        authenticationCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        authenticationCombo.setStore(authenticationStore);
        authenticationCombo.setEditable(false);
        authenticationCombo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                if(se.getSelectedItem().get("value").equals("Normal")){
                    dataSet.add(user,smallFixedFormData);
                    dataSet.add(password,smallFixedFormData);
                    dataSet.insert(user, dataSet.getItems().indexOf(dataSet.getItemByItemId("authentication")) + 1);
                    dataSet.insert(password, dataSet.getItems().indexOf(dataSet.getItemByItemId("authentication"))+2);
                    layout();
                    user.clear();
                    password.clear();
                }
                else{
                    if(dataSet.getItems().contains(user)){
                        dataSet.remove(user);
                        dataSet.remove(password);
                    }
                }
            }
        });
    }

    public void setEditMode(DataSourceUI ds) {
//        setLayout(new FlowLayout());
//        setScrollMode(Style.Scroll.AUTO);
        dataSourceUI = ds;
        edit = true;
        oldDataSetId = ds.getDataSourceSet();
        boolean foundMatch = false;

        metadataFormatCombo.getStore().clearFilters();
        idPolicyCombo.getStore().clearFilters();
        retrieveVariantCombo.getStore().clearFilters();
        authenticationCombo.getStore().clearFilters();
        characterEncoding.getStore().clearFilters();

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

        for(ModelData comboSel: isoVariant.getStore().getModels()) {
            if(comboSel.get("value").equals(dataSourceUI.getIsoVariant()))
                isoVariant.setValue(comboSel);
        }

        String idPolicy = dataSourceUI.getRecordIdPolicy();
        if(idPolicy.equals("IdGenerated")){
            idPolicyCombo.setValue(idPolicyCombo.getStore().getAt(0));
        }
        else if(idPolicy.equals("IdExtracted")){
            idPolicyCombo.setValue(idPolicyCombo.getStore().getAt(1));
            idXPathField.setValue(dataSourceUI.getIdXPath());
            setEditNamespaces(dataSourceUI);
        }

        setEditTransformationCombo(dataSourceUI);
        dataSourceServicesPanel.setEditServices(dataSourceUI);

        for(ModelData comboSel: characterEncoding.getStore().getModels()) {
            if(comboSel.get("value").equals(dataSourceUI.getCharacterEncoding()))
                characterEncoding.setValue(comboSel);
        }

        String dsRetriveStrat = dataSourceUI.getRetrieveStartegy();
        if(dsRetriveStrat.equals("pt.utl.ist.repox.ftp.DataSourceHttp")) {
            retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getAt(2));
            httpUrl.setValue(dataSourceUI.getHttpURL().trim());
        }
        if(dsRetriveStrat.equals("pt.utl.ist.repox.marc.DataSourceFolder")) {
            retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getAt(0));
            folderPath.setValue(dataSourceUI.getDirPath());
        }
        if(dsRetriveStrat.equals("pt.utl.ist.repox.ftp.DataSourceFtp")) {
            server.setValue(dataSourceUI.getServer());
            retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getAt(1));
            if(dataSourceUI.getUser() != null && !dataSourceUI.getUser().isEmpty() &&
                    dataSourceUI.getPassword() != null && !dataSourceUI.getPassword().isEmpty()){
                authenticationCombo.setValue(authenticationCombo.getStore().getAt(1));
                user.setValue(dataSourceUI.getUser());
                password.setValue(dataSourceUI.getPassword());
            }
            else{
                authenticationCombo.setValue(authenticationCombo.getStore().getAt(0));
            }
            folderFtp.setValue(dataSourceUI.getFolderPath());
        }

        retrieveVariantCombo.disable();
        schema.setValue(dataSourceUI.getSchema());
        metadataNamespace.setValue(dataSourceUI.getMetadataNamespace());
        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        rootName.setValue(dataSourceUI.getRecordRootName());
        exportPath.setValue(dataSourceUI.getExportDirectory());

        // Europeana Fields
        if(HarvesterUI.getProjectType().equals("EUROPEANA")) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }
    }

    public void resetValues(DataProviderUI parent){
        edit = false;
        oldDataSetId = "";
        idXPathField.clear();
        rootName.clear();
        otherField.clear();
        httpUrl.clear();
        server.clear();
        folderFtp.clear();
        folderPath.clear();
        setResetNamespaces();
        setResetOutputSet(parent);
        dataSourceServicesPanel.resetValues();
        exportPath.clear();

        metadataFormatCombo.getStore().clearFilters();
        idPolicyCombo.getStore().clearFilters();
        retrieveVariantCombo.getStore().clearFilters();
        authenticationCombo.getStore().clearFilters();
        characterEncoding.getStore().clearFilters();

        metadataFormatCombo.setValue(metadataFormatCombo.getStore().getModels().get(1));
        idPolicyCombo.setValue(idPolicyCombo.getStore().getModels().get(0));
        if(characterEncoding.getStore().getModels().size() > 0)
            characterEncoding.setValue(characterEncoding.getStore().getModels().get(0));
        retrieveVariantCombo.setValue(retrieveVariantCombo.getStore().getModels().get(0));
        retrieveVariantCombo.enable();
        authenticationCombo.setValue(authenticationCombo.getStore().getModels().get(0));

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
        return metadataFormatCombo.getValue().get("value");
    }

    public String getFolderPath() {
        if(folderPath.getValue() != null)
            return folderPath.getValue();
        else
            return null;
    }

    public String getSchema() {
        return schema.getValue();
    }
}

