package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.shared.dataTypes.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 22-03-2011
 * Time: 15:46
 */
public class DataSetOAIViewInfo {

    private FormData formData;
    private FieldSet infoSetFolder;

    private LabelField metadataFormat, oaiUrl, oaiSet, idPolicy,
            recordSet, description, schema, metadataSchema, records, name, nameCode;
    private LayoutContainer typeContainer;
    private LabelToolItem type;
    private Image oaiButton;
    private DataSourceUI dataSourceUI;

    public DataSetOAIViewInfo(FormData form) {
        formData = form;
    }

    private void createFolderinfoSetFolder() {
        infoSetFolder = new FieldSet();
        infoSetFolder.setAutoHeight(true);
        infoSetFolder.setAutoWidth(true);
        infoSetFolder.setHeading(HarvesterUI.CONSTANTS.info());
        infoSetFolder.setLayout(new DefaultFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));

        type = new LabelToolItem();
        typeContainer = new LayoutContainer();
        HBoxLayout typeContainerLayout = new HBoxLayout();
        typeContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        typeContainer.setLayout(typeContainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.type());
        label.setWidth(UtilManager.SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        typeContainer.add(label, new HBoxLayoutData(new Margins(0, 2, 4, 0)));
        typeContainer.add(type, new HBoxLayoutData(new Margins(0, 5, 4, 5)));
        oaiButton = UtilManager.createOAIImage();
        typeContainer.add(oaiButton,new HBoxLayoutData(new Margins(0, 5, 4, 0)));
        infoSetFolder.add(typeContainer,formData);

        metadataFormat = new LabelField();
        metadataFormat.setFieldLabel(HarvesterUI.CONSTANTS.localMetadataFormat());
        infoSetFolder.add(metadataFormat,formData);

        oaiUrl = new LabelField();
        oaiUrl.setFieldLabel(HarvesterUI.CONSTANTS.oaiUrl());
        infoSetFolder.add(oaiUrl,formData);

        oaiSet = new LabelField();
        oaiSet.setFieldLabel(HarvesterUI.CONSTANTS.oaiSet());
        infoSetFolder.add(oaiSet,formData);

        idPolicy = new LabelField();
        idPolicy.setFieldLabel(HarvesterUI.CONSTANTS.idPolicy());
        infoSetFolder.add(idPolicy,formData);

        recordSet = new LabelField();
        recordSet.setFieldLabel(HarvesterUI.CONSTANTS.recordSet());
        infoSetFolder.add(recordSet,formData);

        description = new LabelField();
        description.setFieldLabel(HarvesterUI.CONSTANTS.description());
        infoSetFolder.add(description,formData);

        schema = new LabelField();
        schema.setFieldLabel(HarvesterUI.CONSTANTS.schema());
        infoSetFolder.add(schema,formData);

        metadataSchema = new LabelField();
        metadataSchema.setId("metadataschema");
        metadataSchema.setFieldLabel(HarvesterUI.CONSTANTS.metadataNamespace());
        infoSetFolder.add(metadataSchema,formData);

        // Europeana Only
        if(HarvesterUI.getProjectType().equals("EUROPEANA")) {
            name = new LabelField();
            name.setFieldLabel(HarvesterUI.CONSTANTS.name());
            infoSetFolder.add(name,formData);

            nameCode = new LabelField();
            nameCode.setFieldLabel(HarvesterUI.CONSTANTS.nameCode());
            infoSetFolder.add(nameCode,formData);
        }

        records = new LabelField();
        records.setId("recordsViewDSInfo");
        records.setFieldLabel(HarvesterUI.CONSTANTS.numberOfRecords());
        infoSetFolder.add(records,formData);
    }

    public FieldSet showInfo(DataSourceUI ds) {
        dataSourceUI = ds;
        String ingest = dataSourceUI.getIngest();
        String delimType = "[ ]+";
        String[] tokensType = ingest.split(delimType);
        String typeOfDS = tokensType[0];

        createFolderinfoSetFolder();

        type.setLabel(typeOfDS);
        oaiButton.addMouseDownHandler(new MouseDownHandler(){
            public void onMouseDown(MouseDownEvent event) {
                Dispatcher.get().dispatch(AppEvents.ViewOAISpecificSet,dataSourceUI);
            }
        });
        oaiUrl.setValue(dataSourceUI.getOaiSource());
        oaiSet.setValue(dataSourceUI.getOaiSet());
        metadataFormat.setValue(dataSourceUI.getMetadataFormat());
        idPolicy.setValue(dataSourceUI.getRecordIdPolicy());

        if(idPolicy.getValue().toString().equals("IdExtracted")) {
            LabelField idXPath = new LabelField();
            idXPath.setId("idxpath");
            idXPath.setValue(dataSourceUI.getIdXPath());
            idXPath.setFieldLabel(HarvesterUI.CONSTANTS.identifierXpath());
            infoSetFolder.add(idXPath,formData);
            infoSetFolder.insert(idXPath, infoSetFolder.getItems().indexOf(infoSetFolder.getItemByItemId("idpolicy")) + 1);

            for(int i=0; i< dataSourceUI.getNamespaceList().size() ; i+=2) {
                LabelField namespace = new LabelField();
                namespace.setFieldLabel(HarvesterUI.CONSTANTS.namespacePrefixUri());
                namespace.setId("namespaceField" + i);
                namespace.setValue(dataSourceUI.getNamespaceList().get(i) + " - " + dataSourceUI.getNamespaceList().get(i + 1));
                infoSetFolder.add(namespace,formData);
                infoSetFolder.insert(namespace, infoSetFolder.getItems().indexOf(infoSetFolder.getItemByItemId("idxpath")) + ((i / 2)+1));
            }
        }

        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        schema.setValue(dataSourceUI.getSchema());
        metadataSchema.setValue(dataSourceUI.getMetadataNamespace());

        for(int i=0; i< dataSourceUI.getMetadataTransformations().size() ; i++) {
            LabelField transformation = new LabelField();
            transformation.setFieldLabel(HarvesterUI.CONSTANTS.transformation());
            transformation.setValue(dataSourceUI.getMetadataTransformations().get(i).getDSStringFormat());
            infoSetFolder.add(transformation,formData);
            infoSetFolder.insert(transformation, infoSetFolder.getItems().indexOf(infoSetFolder.getItemByItemId("recordsViewDSInfo")) + (i+1));
        }

        DataSetViewInfo.setRecordsLabel(dataSourceUI,records);

        // Europeana Only
        if(HarvesterUI.getProjectType().equals("EUROPEANA")) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }

        return infoSetFolder;
    }
}
