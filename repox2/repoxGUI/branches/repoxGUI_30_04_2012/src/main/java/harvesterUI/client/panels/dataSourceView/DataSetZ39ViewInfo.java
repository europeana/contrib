package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.shared.dataTypes.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 22-03-2011
 * Time: 15:46
 */
public class DataSetZ39ViewInfo {

    private FormData formData;
    private FieldSet infoSetFolder;

    private LabelField type,  metadataFormat, idPolicy, address, port, database, charset, recordSyntax, harvestMethod,
            recordSet, description, schema, metadataSchema, records, name, nameCode;

    public DataSetZ39ViewInfo(FormData form) {
        formData = form;
    }

    private void createFolderinfoSetFolder() {
        infoSetFolder = new FieldSet();
        infoSetFolder.setAutoHeight(true);
        infoSetFolder.setAutoWidth(true);
        infoSetFolder.setHeading(HarvesterUI.CONSTANTS.info());
        infoSetFolder.setLayout(new DefaultFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));

        type = new LabelField();
        type.setFieldLabel(HarvesterUI.CONSTANTS.type());
        infoSetFolder.add(type,formData);

        metadataFormat = new LabelField();
        metadataFormat.setFieldLabel(HarvesterUI.CONSTANTS.localMetadataFormat());
        infoSetFolder.add(metadataFormat,formData);

        address = new LabelField();
        address.setFieldLabel(HarvesterUI.CONSTANTS.address());
        infoSetFolder.add(address,formData);

        port = new LabelField();
        port.setFieldLabel(HarvesterUI.CONSTANTS.port());
        infoSetFolder.add(port,formData);

        database = new LabelField();
        database.setFieldLabel(HarvesterUI.CONSTANTS.database());
        infoSetFolder.add(database,formData);

        charset = new LabelField();
        charset.setFieldLabel(HarvesterUI.CONSTANTS.characterEncoding());
        infoSetFolder.add(charset,formData);

        recordSyntax = new LabelField();
        recordSyntax.setFieldLabel(HarvesterUI.CONSTANTS.recordSyntax());
        infoSetFolder.add(recordSyntax,formData);

        harvestMethod = new LabelField();
        harvestMethod.setFieldLabel(HarvesterUI.CONSTANTS.harvestMethod());
        infoSetFolder.add(harvestMethod,formData);

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

    public FieldSet showInfo(DataSourceUI dataSourceUI) {
        String ingest = dataSourceUI.getIngest();
        String delimType = "[ ]+";
        String[] tokensType = ingest.split(delimType);
        String typeOfDS = tokensType[0];

        createFolderinfoSetFolder();

        type.setValue(typeOfDS);
        address.setValue(dataSourceUI.getZ39Address());
        port.setValue(dataSourceUI.getZ39Port());
        database.setValue(dataSourceUI.getZ39Database());
        recordSyntax.setValue(dataSourceUI.getZ39RecordSyntax());
        harvestMethod.setValue(dataSourceUI.getZ39HarvestMethod());
        charset.setValue(dataSourceUI.getCharacterEncoding());
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
