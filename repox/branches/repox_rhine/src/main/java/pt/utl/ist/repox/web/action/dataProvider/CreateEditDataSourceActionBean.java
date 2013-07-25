package pt.utl.ist.repox.web.action.dataProvider;

import eu.europeana.repox2sip.Repox2SipException;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.DataManager;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.IdExtracted;
import pt.utl.ist.repox.data.dataSource.IdGenerated;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;

public abstract class CreateEditDataSourceActionBean extends RepoxActionBean {
    private static final Logger log = Logger.getLogger(CreateEditDataSourceActionBean.class);

    protected String dataProviderId;
    protected String dataSourceId;
    protected DataProvider dataProvider;
    protected Boolean editing;
    protected Map<String, List<MetadataTransformation>> metadataTransformations;
    protected String idType;
    protected String identifierXpath;
    protected ArrayList<String> namespacePrefixes4Id;
    protected ArrayList<String> namespaceUris4Id;
    protected ArrayList<String> namespacePrefixes4Record;
    protected ArrayList<String> namespaceUris4Record;
    protected ArrayList<String> selectedTransformationIds;

    public String getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(String dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public Boolean getEditing() {
        return editing;
    }

    public void setEditing(Boolean editing) {
        this.editing = editing;
    }

    public Map<String, List<MetadataTransformation>> getMetadataTransformations() {
        return metadataTransformations;
    }

    public void setMetadataTransformations(Map<String, List<MetadataTransformation>> metadataTransformations) {
        this.metadataTransformations = metadataTransformations;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdentifierXpath() {
        return identifierXpath;
    }

    public void setIdentifierXpath(String identifierXpath) {
        this.identifierXpath = identifierXpath;
    }

    public ArrayList<String> getNamespacePrefixes4Id() {
        return namespacePrefixes4Id;
    }

    public void setNamespacePrefixes4Id(ArrayList<String> namespacePrefixes4Id) {
        this.namespacePrefixes4Id = namespacePrefixes4Id;
    }

    public ArrayList<String> getNamespaceUris4Id() {
        return namespaceUris4Id;
    }

    public void setNamespaceUris4Id(ArrayList<String> namespaceUris4Id) {
        this.namespaceUris4Id = namespaceUris4Id;
    }

    public ArrayList<String> getNamespacePrefixes4Record() {
        return namespacePrefixes4Record;
    }

    public void setNamespacePrefixes4Record(ArrayList<String> namespacePrefixes4Record) {
        this.namespacePrefixes4Record = namespacePrefixes4Record;
    }

    public ArrayList<String> getNamespaceUris4Record() {
        return namespaceUris4Record;
    }

    public void setNamespaceUris4Record(ArrayList<String> namespaceUris4Record) {
        this.namespaceUris4Record = namespaceUris4Record;
    }

    public ArrayList<String> getSelectedTransformationIds() {
        return selectedTransformationIds;
    }

    public void setSelectedTransformationIds(ArrayList<String> selectedTransformationIds) {
        this.selectedTransformationIds = selectedTransformationIds;
    }

    public CreateEditDataSourceActionBean() throws IOException, DocumentException {
        super();
        metadataTransformations = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().loadMetadataTransformations();
        if(selectedTransformationIds == null || selectedTransformationIds.isEmpty()) {
            selectedTransformationIds = new ArrayList<String>();
            selectedTransformationIds.add("");
        }
    }

    @DefaultHandler
    public Resolution preEdit() throws Exception {
        if(dataProviderId != null) {
            dataProvider = RepoxContextUtil.getRepoxManager().getDataManager().getDataProvider(dataProviderId);
        }

        selectedTransformationIds = new ArrayList<String>();
        if(dataSourceId != null && !dataSourceId.isEmpty()) {
            editing = true;
            loadDataSource();
            if(getBeanDataSource() != null) {
                if(getBeanDataSource().getMetadataTransformations() != null
                        && !getBeanDataSource().getMetadataTransformations().isEmpty()) {
                    for (MetadataTransformation metadataTransformation : getBeanDataSource().getMetadataTransformations().values()) {
                        selectedTransformationIds.add(metadataTransformation.getId());
                    }
                }

                if(getBeanDataSource().getClass() == DataSourceDirectoryImporter.class && ((DataSourceDirectoryImporter)getBeanDataSource()).getRecordXPath() != null){
                    namespacePrefixes4Record = new ArrayList<String>(((DataSourceDirectoryImporter)getBeanDataSource()).getNamespaces().keySet());
                    namespaceUris4Record = new ArrayList<String>(((DataSourceDirectoryImporter)getBeanDataSource()).getNamespaces().values());
                }

                idType = getBeanDataSource().getRecordIdPolicy().getClass().getSimpleName();
                if(idType.equals(IdExtracted.class.getSimpleName())) {
                    IdExtracted idExtracted = (IdExtracted) getBeanDataSource().getRecordIdPolicy();
                    identifierXpath = idExtracted.getIdentifierXpath();
                    namespacePrefixes4Id = new ArrayList<String>(idExtracted.getNamespaces().keySet());
                    namespaceUris4Id = new ArrayList<String>(idExtracted.getNamespaces().values());
                }
            }
        }
        if(selectedTransformationIds.isEmpty()) {
            selectedTransformationIds.add("");
        }

        return getCreationResolution();
    }

    public abstract void loadDataSource() throws Exception;

    public abstract Resolution getCreationResolution();

    public abstract DataSource getBeanDataSource();

    public abstract Class<? extends DataSource> getDataSourceClass();

    public abstract void validateDataSource(ValidationErrors errors) throws DocumentException;

    public abstract void prepareSubmission();

    public Resolution cancelDataSource() throws UnsupportedEncodingException {
//		context.getMessages().add(new LocalizableMessage("common.cancel.message"));
        return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + URLEncoder.encode(dataProviderId, "UTF-8"));
    }

    @ValidationMethod(on = {"submitDataSource"})
    public void validateCommonDataSourceProperties(ValidationErrors errors) throws DocumentException, IOException {
        // Validate first specific errors that appear first in the jsp
        validateDataSource(errors);

        DataManager dataManager = context.getRepoxManager().getDataManager();

        if(getBeanDataSource() == null || getBeanDataSource().getId() == null || getBeanDataSource().getId().trim().isEmpty()) {
            errors.add("dataSource", new LocalizableError("error.dataSource.id"));
        }
        else if(editing == null || !editing) {
            if(dataManager.getDataSource(getBeanDataSource().getId()) != null) {
                errors.add("dataSource", new LocalizableError("error.dataSource.existingId", getBeanDataSource().getId()));
            }
            if(!dataManager.isIdValid(getBeanDataSource().getId())) {
                errors.add("dataSource", new LocalizableError("error.dataSource.invalidId", getBeanDataSource().getId()));
            }
        }
        else if(!dataSourceId.equals(getBeanDataSource().getId())) { //Id Changed
            if(dataManager.getDataSource(getBeanDataSource().getId()) != null) {
                errors.add("dataSource", new LocalizableError("error.dataSource.existingId", getBeanDataSource().getId()));
            }
        }

        if(getBeanDataSource() == null || getBeanDataSource().getDescription() == null || getBeanDataSource().getDescription().trim().isEmpty()) {
            errors.add("dataSource", new LocalizableError("error.dataSource.description"));
        }


        if(getBeanDataSource() != null && idType != null && idType.equals(IdExtracted.class.getSimpleName())) {
            if(identifierXpath == null || identifierXpath.trim().isEmpty()) {
                errors.add("dataSource", new LocalizableError("error.dataSource.identifierXpath"));
            }

            int prefixesSize = (namespacePrefixes4Id == null ? 0 : namespacePrefixes4Id.size());
            int urisSize = (namespaceUris4Id == null ? 0 : namespaceUris4Id.size());
            if((prefixesSize > 0 && urisSize == 0) || (prefixesSize == 0 && urisSize > 0)) {
                errors.add("dataSource", new LocalizableError("error.dataSource.namespace"));
            }
            else if(prefixesSize > 0) {
                for(int i = 0; i < namespacePrefixes4Id.size(); i++) {
                    String currentNamespacePrefix = namespacePrefixes4Id.get(i);
                    String currentNamespaceUri = namespaceUris4Id.get(i);
                    if(currentNamespacePrefix != null && currentNamespacePrefix.trim().isEmpty() ||
                            currentNamespaceUri != null && currentNamespaceUri.trim().isEmpty()) {
                        errors.add("dataSource", new LocalizableError("error.dataSource.namespace"));
                        break;
                    }
                }
            }
        }

        MetadataTransformationManager metadataTransformationManager = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager();

        String sourceFormat = null;

        if(getBeanDataSource() != null) {
            if(getBeanDataSource().getMetadataFormat().equals(MetadataFormat.ISO2709.toString())) {
                sourceFormat = MetadataFormat.MarcXchange.toString();
            }
            else {
                sourceFormat = getBeanDataSource().getMetadataFormat();
            }
        }

        if(selectedTransformationIds != null && !selectedTransformationIds.isEmpty()) {
            Set<String> destinationFormats = new HashSet<String>();

            for (String transformationId : selectedTransformationIds) { //validation of destination format
                if(!transformationId.isEmpty()) {
                    MetadataTransformation loadedTransformation = metadataTransformationManager.loadMetadataTransformation(transformationId);
                    if(destinationFormats.contains(loadedTransformation.getDestinationFormat())) {
                        errors.add("dataSource", new LocalizableError("error.dataSource.duplicatedTransformation"));
                        break;
                    }
                    destinationFormats.add(loadedTransformation.getDestinationFormat());
                }
            }

            for (String transformationId : selectedTransformationIds) { //validation of source format
                if(!transformationId.isEmpty()) {
                    MetadataTransformation loadedTransformation = metadataTransformationManager.loadMetadataTransformation(transformationId);
                    if(!sourceFormat.equals(loadedTransformation.getSourceFormat())) {
                        errors.add("dataSource", new LocalizableError("error.dataSource.invalidSourceFormat"));
                        break;
                    }
                    destinationFormats.add(loadedTransformation.getDestinationFormat());
                }
            }
        }
    }

    public Resolution addTransformation() throws DocumentException {
        if(selectedTransformationIds == null) {
            selectedTransformationIds = new ArrayList<String>();
        }
        selectedTransformationIds.add("");

        return getCreationResolution();
    }

    public Resolution addNamespace4Id() throws DocumentException {
        if(namespacePrefixes4Id == null || namespaceUris4Id == null) {
            namespacePrefixes4Id = new ArrayList<String>();
            namespaceUris4Id = new ArrayList<String>();
        }
        namespacePrefixes4Id.add("");
        namespaceUris4Id.add("");

        return getCreationResolution();
    }

    public Resolution addNamespace4Record() throws DocumentException {
        if(namespacePrefixes4Record == null || namespaceUris4Record == null) {
            namespacePrefixes4Record = new ArrayList<String>();
            namespaceUris4Record = new ArrayList<String>();
        }
        namespacePrefixes4Record.add("");
        namespaceUris4Record.add("");

        return getCreationResolution();
    }

    public Resolution submitDataSource() throws SQLException, IOException, DocumentException, Repox2SipException {
        prepareSubmission();

        DataManager dataManager = context.getRepoxManager().getDataManager();
        DataProvider dataProvider = dataManager.getDataProvider(dataProviderId);

        //System.out.println("dataProvider = " + dataProvider);
        //System.out.println("dataProvider.getId() = " + dataProvider.getId());
        //System.out.println("dataProvider.getName() = " + dataProvider.getName());

        getBeanDataSource().setDataProvider(dataProvider);

        MetadataTransformationManager transformationManager = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager();
        if(selectedTransformationIds != null && !selectedTransformationIds.isEmpty()) {
            if(getBeanDataSource().getMetadataTransformations() == null || getBeanDataSource().getMetadataTransformations().isEmpty()) {
                getBeanDataSource().setMetadataTransformations(new TreeMap<String, MetadataTransformation>());
            }
            for (String transformationId : selectedTransformationIds) {
                if(!transformationId.isEmpty()) {
                    MetadataTransformation transformation = transformationManager.loadMetadataTransformation(transformationId);
                    getBeanDataSource().getMetadataTransformations().put(transformation.getDestinationFormat(), transformation);
                }
            }
        }

        if(getBeanDataSource().getClass() == DataSourceDirectoryImporter.class && ((DataSourceDirectoryImporter)getBeanDataSource()).getRecordXPath() != null){
            Map<String, String> namespaces4Records = new HashMap<String, String>();
            if(namespacePrefixes4Record != null && !namespacePrefixes4Record.isEmpty()) {
                for(int i = 0; i < namespacePrefixes4Record.size(); i++) {
                    String currentPrefix = namespacePrefixes4Record.get(i);
                    String currentUri = namespaceUris4Record.get(i);
                    if(currentPrefix != null && !currentPrefix.trim().isEmpty()
                            && currentUri != null && !currentUri.trim().isEmpty()) {
                        namespaces4Records.put(currentPrefix, currentUri);
                    }
                }
            }
            ((DataSourceDirectoryImporter)getBeanDataSource()).setNamespaces(namespaces4Records);
        }

        if(idType != null && (idType.equals(IdGenerated.class.getSimpleName()))) {
            getBeanDataSource().setRecordIdPolicy(new IdGenerated());
        }
        else if(idType != null && idType.equals(IdExtracted.class.getSimpleName())) {
            Map<String, String> namespaces = new HashMap<String, String>();
            if(namespacePrefixes4Id != null && !namespacePrefixes4Id.isEmpty()) {
                for(int i = 0; i < namespacePrefixes4Id.size(); i++) {
                    String currentPrefix = namespacePrefixes4Id.get(i);
                    String currentUri = namespaceUris4Id.get(i);
                    if(currentPrefix != null && !currentPrefix.trim().isEmpty()
                            && currentUri != null && !currentUri.trim().isEmpty()) {
                        namespaces.put(currentPrefix, currentUri);
                    }
                }
            }
            getBeanDataSource().setRecordIdPolicy(new IdExtracted(identifierXpath, namespaces));
        }

        if(editing != null && editing && dataSourceId != null && !dataSourceId.isEmpty()) {
            DataSource oldDataSource = dataProvider.getDataSource(dataSourceId);

            if(!getBeanDataSource().getId().equals(oldDataSource.getId())) {
                dataManager.updateDataSourceId(oldDataSource.getId(), getBeanDataSource().getId());
                ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
                dataSources.add(getBeanDataSource());
                getBeanDataSource().initAccessPoints();
                context.getRepoxManager().getAccessPointsManager().initialize(dataSources);
            }

            //TODO Update SipBD
            dataManager.updateDataSource(dataProvider ,getBeanDataSource(),oldDataSource);

            log.debug("Updated new DataSource with id " + getBeanDataSource().getId() + " from DataProvider with id " + dataProvider.getId());
            context.getMessages().add(new LocalizableMessage("dataSource.edit.success", dataProvider.getId(), getBeanDataSource().getId()));
        }
        else {
            if(dataProvider.getDataSources() == null) {
                dataProvider.setDataSources(new ArrayList<DataSource>());
            }

            DataSource dataSource = getBeanDataSource();
            dataSource.setDataProvider(dataProvider);

            dataManager.saveDataSource(dataSource);

            //dataProvider.getDataSources().add(getBeanDataSource());

            // only add data source to database
            //dataManager.saveDataSource(getBeanDataSource());

            //dataManager.updateDataProvider(dataProvider, dataProvider.getId());

            ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
            dataSources.add(getBeanDataSource());
            getBeanDataSource().initAccessPoints();
            context.getRepoxManager().getAccessPointsManager().initialize(dataSources);

            log.debug("Saved new DataSource with id " + getBeanDataSource().getId() + " from DataProvider with id " + dataProvider.getId());
            context.getMessages().add(new LocalizableMessage("dataSource.create.success", dataProvider.getId(), getBeanDataSource().getId()));
        }

//		return new RedirectResolution("/Homepage.action");
        return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + dataProviderId);
    }

}