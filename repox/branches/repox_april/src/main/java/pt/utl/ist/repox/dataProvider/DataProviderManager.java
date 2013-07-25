package pt.utl.ist.repox.dataProvider;

import eu.europeana.repox2sip.models.Provider;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.dataProvider.dataSource.*;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.marc.Iso2709FileExtract;
import pt.utl.ist.repox.marc.MarcXchangeFileExtract;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.XmlUtil;
import pt.utl.ist.repox.z3950.*;
import pt.utl.ist.util.DateUtil;
import pt.utl.ist.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

public class DataProviderManager {
    private static final String ID_REGULAR_EXPRESSION = "[a-zA-Z][a-zA-Z_0-9]*";
    private static final int ID_MAX_SIZE = 64;
    private static final Logger log = Logger.getLogger(DataProviderManager.class);

    private File configurationFile;
    private MetadataTransformationManager metadataTransformationManager;

    public File getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    public MetadataTransformationManager getMetadataTransformationManager() {
        return metadataTransformationManager;
    }

    public void setMetadataTransformationManager(MetadataTransformationManager metadataTransformationManager) {
        this.metadataTransformationManager = metadataTransformationManager;
    }

    public DataProviderManager(File configurationFile, MetadataTransformationManager metadataTransformationManager) {
        super();
        this.configurationFile = configurationFile;
        if(!configurationFile.exists()) {
            try {
                saveDataProviders(new ArrayList<DataProvider>());
            }
            catch(IOException e) {
                log.error("Can't create DataProviders configuration file", e);
            }
        }
        this.metadataTransformationManager = metadataTransformationManager;
    }

    public static String getDataProviderName(String dataSourceId) {
        DataSource dataSource = null;

        try {
            dataSource = RepoxContextUtil.getRepoxManager().getDataProviderManager().getDataSource(dataSourceId);
        }
        catch (Exception e) {
            log.error("Error accessing Data Source", e);
        }

        if(dataSource == null) {
            return "";
        }

        DataProvider dataProvider = dataSource.getDataProvider();

        if(dataProvider == null) {
            return "";
        }

        return dataProvider.getName();
    }

    public void saveDataProvider(DataProvider dataProvider) throws IOException, DocumentException {
        List<DataProvider> dataProviders = loadDataProviders();
        dataProviders.add(dataProvider);
        saveDataProviders(dataProviders);

        //GFSP - new
        saveDataProvider2Database(dataProviders);
    }

    public synchronized void saveDataProviders(List<DataProvider> dataProviders) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element rootNode = document.addElement("data-providers");

        for(DataProvider dataProvider : dataProviders) {
            Element dataProviderNode = rootNode.addElement("provider");

            dataProviderNode.addAttribute("id", dataProvider.getId());
            dataProviderNode.addElement("name").setText(dataProvider.getName());
            if(dataProvider.getCountry() != null) {
                dataProviderNode.addElement("country").setText(dataProvider.getCountry());
            }
            if(dataProvider.getDescription() != null) {
                dataProviderNode.addElement("description").setText(dataProvider.getDescription());
            }
            Element logoElement = dataProviderNode.addElement("logo");
            if(dataProvider.getLogo() != null) {
                logoElement.setText(dataProvider.getLogo());
            }

            if(dataProvider.getDataSources() != null && !dataProvider.getDataSources().isEmpty()) {
                for (DataSource dataSource : dataProvider.getDataSources()) {
                    Element sourceNode = dataProviderNode.addElement("source");

                    // Add generic Data Source Data
                    sourceNode.addAttribute("id", dataSource.getId());
                    sourceNode.addAttribute("metadataFormat", dataSource.getMetadataFormat());
                    sourceNode.addElement("description").setText(dataSource.getDescription());
                    Element recordIdPolicyNode = sourceNode.addElement("recordIdPolicy");
                    recordIdPolicyNode.addAttribute("type", dataSource.getRecordIdPolicy().getClass().getSimpleName());
                    if(dataSource.getRecordIdPolicy() instanceof IdGenerated) {
                    }
                    else if(dataSource.getRecordIdPolicy() instanceof IdProvided) {
                    }
                    else if(dataSource.getRecordIdPolicy() instanceof IdExtracted) {
                        IdExtracted idExtracted = (IdExtracted) dataSource.getRecordIdPolicy();
                        recordIdPolicyNode.addElement("idXpath").setText(idExtracted.getIdentifierXpath());
                        if(idExtracted.getNamespaces() != null && !idExtracted.getNamespaces().isEmpty()) {
                            Element namespacesElement = recordIdPolicyNode.addElement("namespaces");
                            for(String currentKey : idExtracted.getNamespaces().keySet()) {
                                Element namespaceElement = namespacesElement.addElement("namespace");
                                namespaceElement.addElement("namespacePrefix").setText(currentKey);
                                namespaceElement.addElement("namespaceUri").setText(idExtracted.getNamespaces().get(currentKey));
                            }
                        }
                    }
                    else {
                        throw new RuntimeException("Invalid RecordIdPolicy of class " + dataSource.getRecordIdPolicy().getClass().getName());
                    }

                    // Add specific Data Source data
                    if(dataSource instanceof DataSourceOai) {
                        DataSourceOai currentDataSource = (DataSourceOai) dataSource;
                        sourceNode.addAttribute("type", "DataSourceOai");
                        sourceNode.addElement("oai-source").setText(currentDataSource.getOaiSourceURL());
                        if(currentDataSource.getOaiSet() != null && !currentDataSource.getOaiSet().isEmpty()) {
                            sourceNode.addElement("oai-set").setText(currentDataSource.getOaiSet());
                        }
                    }
                    else if(dataSource instanceof DataSourceDirectoryImporter) {
                        DataSourceDirectoryImporter currentDataSource = (DataSourceDirectoryImporter) dataSource;

                        sourceNode.addAttribute("type", "DataSourceDirectoryImporter");
                        sourceNode.addElement("sourcesDirPath").setText(currentDataSource.getSourcesDirPath());

                        if(currentDataSource.getExtractStrategy() instanceof Iso2709FileExtract) {
                            Iso2709FileExtract extractStrategy = (Iso2709FileExtract) currentDataSource.getExtractStrategy();
                            sourceNode.addAttribute("isoImplementationClass", extractStrategy.getIsoImplementationClass().toString());
                            sourceNode.addAttribute("characterEncoding", currentDataSource.getCharacterEncoding().toString());
                            sourceNode.addElement("fileExtract").setText(Iso2709FileExtract.class.getSimpleName());
                        }
                        else if(currentDataSource.getExtractStrategy() instanceof MarcXchangeFileExtract) {
                            sourceNode.addElement("fileExtract").setText(MarcXchangeFileExtract.class.getSimpleName());
                        }
                        else if(currentDataSource.getExtractStrategy() instanceof SimpleFileExtract) {
                            sourceNode.addElement("fileExtract").setText(SimpleFileExtract.class.getSimpleName());

                            if(currentDataSource.getRecordXPath() != null){
                                Element splitRecords = sourceNode.addElement("splitRecords");
                                splitRecords.addElement("recordXPath").setText(currentDataSource.getRecordXPath());

                                if(currentDataSource.getNamespaces() != null){
                                    Element namespacesElement = splitRecords.addElement("namespaces");

                                    System.out.println("currentDataSource.getNamespaces() = " + currentDataSource.getNamespaces());

                                    for(String currentKey : currentDataSource.getNamespaces().keySet()) {
                                        Element namespaceElement = namespacesElement.addElement("namespace");
                                        namespaceElement.addElement("namespacePrefix").setText(currentKey);
                                        namespaceElement.addElement("namespaceUri").setText(currentDataSource.getNamespaces().get(currentKey));
                                    }
                                }
                            }
                        }
                    }
                    else if(dataSource instanceof DataSourceZ3950) {
                        DataSourceZ3950 currentDataSource = (DataSourceZ3950) dataSource;
                        sourceNode.addAttribute("type", "DataSourceZ3950");

                        HarvestMethod harvestMethod = currentDataSource.getHarvestMethod();
                        Target target = harvestMethod.getTarget();
                        Element targetElement = sourceNode.addElement("target");
                        targetElement.addElement("address").setText(target.getAddress());
                        targetElement.addElement("port").setText(String.valueOf(target.getPort()));
                        targetElement.addElement("database").setText(target.getDatabase());
                        targetElement.addElement("user").setText((target.getUser() != null ? target.getUser() : ""));
                        targetElement.addElement("password").setText(target.getPassword() != null ? target.getPassword() : "");
                        targetElement.addElement("charset").setText(target.getCharset());
                        targetElement.addElement("recordSyntax").setText(target.getRecordSyntax());

                        if(harvestMethod instanceof TimestampHarvester) {
                            sourceNode.addElement("harvestMethod").setText(TimestampHarvester.class.getSimpleName());
                            TimestampHarvester timestampHarvester = (TimestampHarvester) harvestMethod;
                            String timestampString = DateUtil.date2String(timestampHarvester.getEarliestTimestamp(), "yyyyMMdd");
                            sourceNode.addElement("earliestTimestamp").setText(timestampString);

                        } else if(harvestMethod instanceof IdListHarvester) {
                            sourceNode.addElement("harvestMethod").setText(IdListHarvester.class.getSimpleName());
                            IdListHarvester idListHarvester = (IdListHarvester) harvestMethod;
                            sourceNode.addElement("idListFile").setText(idListHarvester.getIdListFile().getAbsolutePath());
                        } else if(harvestMethod instanceof IdSequenceHarvester) {
                            sourceNode.addElement("harvestMethod").setText(IdSequenceHarvester.class.getSimpleName());
                            IdSequenceHarvester idSequenceHarvester = (IdSequenceHarvester) harvestMethod;
                            if(idSequenceHarvester.getMaximumId() != null) {
                                sourceNode.addElement("maximumId").setText(String.valueOf(idSequenceHarvester.getMaximumId()));
                            }
                        }
                    }
                    else {
                        throw new RuntimeException("Saving configuration from Data Source of class "
                                + dataSource.getClass().getName() + " not implemented");
                    }

                    //Add MetadataTransformations
                    Element metadataTransformationsNode = sourceNode.addElement("metadataTransformations");
                    if(dataSource.getMetadataTransformations() != null && !dataSource.getMetadataTransformations().isEmpty()) {
                        for (MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values()) {
                            if(metadataTransformation != null) {
                                Element metadataTransformationNode = metadataTransformationsNode.addElement("metadataTransformation");
                                metadataTransformationNode.setText(metadataTransformation.getId());
                            }
                        }
                    }
                }
            }
        }

        XmlUtil.writePrettyPrint(configurationFile, document);
    }

    public synchronized List<DataProvider> loadDataProviders() throws DocumentException, IOException {
        List<DataProvider> dataProviders = new ArrayList<DataProvider>();

        if(!configurationFile.exists()) {
            return dataProviders;
        }

        SAXReader reader = new SAXReader();
        Document document = reader.read(configurationFile);

        if(!document.getRootElement().elements("provider").isEmpty()) {
            for (Element currentDataProviderElement : (List<Element>) document.getRootElement().elements("provider")) {
                String providerId = currentDataProviderElement.attributeValue("id");
                String providerName = currentDataProviderElement.elementText("name");
                String providerCountry = currentDataProviderElement.elementText("country");
                String providerDescription = currentDataProviderElement.elementText("description");
                String logo = currentDataProviderElement.elementText("logo");
                Collection<DataSource> dataSources = new ArrayList<DataSource>();

                DataProvider provider = new DataProvider(providerId, providerName, providerCountry, providerDescription, dataSources, logo);
                for (Element currentDataSourceElement : (List<Element>) currentDataProviderElement.elements("source")) {
                    String id = currentDataSourceElement.attributeValue("id");
                    String description = currentDataSourceElement.elementText("description");
                    String metadataFormat = currentDataSourceElement.attributeValue("metadataFormat");

                    // Create RecordIdPolicy
                    Element recordIdPolicyNode = currentDataSourceElement.element("recordIdPolicy");
                    String recordIdPolicyClass = recordIdPolicyNode.attributeValue("type");
                    RecordIdPolicy recordIdPolicy = null;
                    if(recordIdPolicyClass.equals(IdGenerated.class.getSimpleName())) {
                        recordIdPolicy = new IdGenerated();
                    }
                    else if(recordIdPolicyClass.equals(IdProvided.class.getSimpleName())) {
                        recordIdPolicy = new IdProvided();
                    }
                    else if(recordIdPolicyClass.equals(IdExtracted.class.getSimpleName())) {
                        String identifierXpath = recordIdPolicyNode.element("idXpath").getText();
                        Map<String, String> namespaces = new TreeMap<String, String>();
                        Element namespacesElement = recordIdPolicyNode.element("namespaces");
                        if(namespacesElement != null) {
                            List<Element> namespaceElement = namespacesElement.elements("namespace");
                            for (Element currentNamespace : namespaceElement) {
                                namespaces.put(currentNamespace.elementText("namespacePrefix"),
                                        currentNamespace.elementText("namespaceUri"));
                            }
                        }

                        recordIdPolicy = new IdExtracted(identifierXpath, namespaces);
                    }
                    else {
                        throw new RuntimeException("Invalid RecordIdPolicy of class " + recordIdPolicyClass);
                    }

                    //Create MetadataTransformations
                    Map<String, MetadataTransformation> metadataTransformations = new HashMap<String, MetadataTransformation>();
                    for(Element metadataTransformationElement: (List<Element>) currentDataSourceElement.element("metadataTransformations").elements("metadataTransformation")) {
                        String transformationId = metadataTransformationElement.getText();
                        MetadataTransformation metadataTransformation = metadataTransformationManager.loadMetadataTransformation(transformationId);
                        metadataTransformations.put(metadataTransformation.getDestinationFormat(), metadataTransformation);
                    }

                    //Create DataSource
                    String dataSourceType = currentDataSourceElement.attribute("type").getText();
                    if(dataSourceType.equals("DataSourceOai")) {
                        String oaiSource = currentDataSourceElement.elementText("oai-source");
                        String oaiSet = (currentDataSourceElement.element("oai-set") != null ? currentDataSourceElement.elementText("oai-set") : null);
                        DataSourceOai dataSource = new DataSourceOai(provider, id, description, metadataFormat,
                                oaiSource, oaiSet, recordIdPolicy, metadataTransformations);
                        provider.addDataSource(dataSource);
                        //dataSources.add(dataSource);
                    }
                    else if(dataSourceType.equals("DataSourceDirectoryImporter")) {
                        String sourcesDirPath = currentDataSourceElement.elementText("sourcesDirPath");
                        String extractStrategyString = currentDataSourceElement.elementText("fileExtract");
                        DataSourceDirectoryImporter dataSource = null;

                        if(extractStrategyString.equals(Iso2709FileExtract.class.getSimpleName())) {
                            CharacterEncoding characterEncoding = CharacterEncoding.get(currentDataSourceElement.attributeValue("characterEncoding"));
                            String isoImplementationClass = currentDataSourceElement.attributeValue("isoImplementationClass");
                            FileExtractStrategy extractStrategy = new Iso2709FileExtract(isoImplementationClass);
                            dataSource = new DataSourceDirectoryImporter(provider, id, description, metadataFormat, extractStrategy,
                                    characterEncoding, sourcesDirPath, recordIdPolicy, metadataTransformations, null, null);
                        } else if(extractStrategyString.equals(MarcXchangeFileExtract.class.getSimpleName())) {
                            FileExtractStrategy extractStrategy = new MarcXchangeFileExtract();
                            dataSource = new DataSourceDirectoryImporter(provider, id, description, metadataFormat, extractStrategy,
                                    sourcesDirPath, recordIdPolicy, metadataTransformations, null, null);
                        } else if(extractStrategyString.equals(SimpleFileExtract.class.getSimpleName())) {
                            FileExtractStrategy extractStrategy = new SimpleFileExtract();

                            String recordXPath = null;
                            Map<String, String> namespaces = null;

                            Element splitRecordsElement = currentDataSourceElement.element("splitRecords");
                            if(splitRecordsElement != null){
                                recordXPath = splitRecordsElement.elementText("recordXPath");

                                namespaces = new TreeMap<String, String>();
                                Element namespacesElement = splitRecordsElement.element("namespaces");
                                if(namespacesElement != null) {
                                    List<Element> namespaceElement = namespacesElement.elements("namespace");
                                    for (Element currentNamespace : namespaceElement) {
                                        namespaces.put(currentNamespace.elementText("namespacePrefix"),
                                                currentNamespace.elementText("namespaceUri"));
                                    }
                                }
                            }

                            dataSource = new DataSourceDirectoryImporter(provider, id, description, metadataFormat, extractStrategy,
                                    sourcesDirPath, recordIdPolicy, metadataTransformations, recordXPath, namespaces);
                        }
                        provider.addDataSource(dataSource);
                        //dataSources.add(dataSource);
                    }
                    else if(dataSourceType.equals("DataSourceZ3950")) {
                        Element targetElement = currentDataSourceElement.element("target");
                        String targetAddress = targetElement.elementText("address");
                        int targetPort = Integer.parseInt(targetElement.elementText("port"));
                        String targetDatabase = targetElement.elementText("database");
                        String targetUser = targetElement.elementText("user");
                        String targetPassword = targetElement.elementText("password");
                        String targetCharset = targetElement.elementText("charset");
                        String targetSyntax = targetElement.elementText("recordSyntax");
                        Target target = new Target(targetAddress, targetPort, targetDatabase, targetUser, targetPassword, targetCharset, targetSyntax);

                        HarvestMethod harvestMethod = null;
                        String harvestMethodString = currentDataSourceElement.elementText("harvestMethod");
                        if(harvestMethodString.equals(TimestampHarvester.class.getSimpleName())) {
                            String timestampString = currentDataSourceElement.elementText("earliestTimestamp");
                            try {
                                Date earliestTimestamp = DateUtil.string2Date(timestampString, "yyyyMMdd");
                                harvestMethod = new TimestampHarvester(target, earliestTimestamp);
                            }
                            catch (ParseException e) {
                                log.error("Error parsing date: " + timestampString, e);
                                break;
                            }


                        } else if(harvestMethodString.equals(IdListHarvester.class.getSimpleName())) {
                            String filePath = currentDataSourceElement.elementText("idListFile");
                            File idListFile = new File(filePath);

                            harvestMethod = new IdListHarvester(target, idListFile);

                        } else if(harvestMethodString.equals(IdSequenceHarvester.class.getSimpleName())) {
                            String maximumIdString =  currentDataSourceElement.elementText("maximumId");
                            Integer maximumId = (maximumIdString != null && !maximumIdString.isEmpty() ? Integer.valueOf(maximumIdString) : null);

                            harvestMethod = new IdSequenceHarvester(target, maximumId);
                        }

                        DataSourceZ3950 dataSourceZ3950 = new DataSourceZ3950(provider, id, description,
                                harvestMethod, recordIdPolicy, metadataTransformations);
                        provider.addDataSource(dataSourceZ3950);
                        //dataSources.add(dataSourceZ3950);
                    }
                    else {
                        throw new RuntimeException("Loading configuration from Data Source of type "
                                + dataSourceType + " not implemented");
                    }
                }

                dataProviders.add(provider);
            }
        }

        return dataProviders;
    }

    /**
     * Gets the DataProvider with id dataProviderId from the configuration file if it exists or null otherwise.
     *
     * @param dataProviderId
     * @return DataProvider with id dataProviderId if exists, null otherwise
     * @throws DocumentException
     * @throws IOException
     */
    public DataProvider getDataProvider(String dataProviderId) throws DocumentException, IOException {
        Collection<DataProvider> dataProviders = loadDataProviders();
        for (DataProvider currentDataProvider : dataProviders) {
            if (currentDataProvider.getId().equals(dataProviderId)) {
                return currentDataProvider;
            }
        }

        return null;
    }

    /**
     * Gets the DataSource with id dataSourceId from the configuration file if it exists or null otherwise.
     *
     * @param dataSourceId
     * @return DataSource with id dataSourceId if exists, null otherwise
     * @throws DocumentException
     * @throws IOException
     */
    public DataSource getDataSource(String dataSourceId) throws DocumentException, IOException {
        Collection<DataProvider> dataProviders = loadDataProviders();
        for (DataProvider currentDataProvider : dataProviders) {
            DataSource dataSource = currentDataProvider.getDataSource(dataSourceId);
            if (dataSource != null) {
                return dataSource;
            }
        }

        return null;
    }

    public void updateDataProvider(DataProvider dataProvider, String oldDataProviderId) throws IOException, DocumentException {
        List<DataProvider> dataProviders = loadDataProviders();
        Iterator<DataProvider> iteratorDataProvider = dataProviders.iterator();

        while (iteratorDataProvider.hasNext()) {
            DataProvider currentDataProvider = iteratorDataProvider.next();
            if(currentDataProvider.getId().equals(oldDataProviderId)) {
                iteratorDataProvider.remove();
                break;
            }
        }
        dataProviders.add(dataProvider);
        saveDataProviders(dataProviders);
    }

    public void deleteDataProvider(String dataProviderId) throws IOException, DocumentException {
        List<DataProvider> dataProviders = loadDataProviders();
        Iterator<DataProvider> iteratorDataProvider = dataProviders.iterator();
        boolean deleted = false;

        while (iteratorDataProvider.hasNext()) {
            DataProvider currentDataProvider = iteratorDataProvider.next();
            if(currentDataProvider.getId().equals(dataProviderId)) {
                iteratorDataProvider.remove();
                for (DataSource currentDataSource : currentDataProvider.getDataSources()) {
                    deleteDataSource(currentDataSource.getId());
                }
                deleted = true;

                break;
            }
        }

        if(deleted) {
            saveDataProviders(dataProviders);
        } else {
            throw new RuntimeException("Can't delete Data Provider with id " + dataProviderId + " because it doesn't exist");
        }
    }

    /**
     * Update the Data Source with id newDataSourceId from oldDataSourceId.
     *
     * @param oldDataSourceId, newDataSourceId
     * @return true if deletion was completely successful, false otherwise.
     * @throws DocumentException
     */
    public void updateDataSourceId(String oldDataSourceId, String newDataSourceId)
            throws IOException, DocumentException, SQLException {
        DataSource dataSource = getDataSource(oldDataSourceId);
//		dataSource.initAccessPoints();

        log.info("Updating Data Source with id " + oldDataSourceId + " to id " + newDataSourceId);
        AccessPointsManager accessPointsManager = RepoxContextUtil.getRepoxManager().getAccessPointsManager();

        //Update Access Points
        AccessPoint defaultTimestampAP = dataSource.getAccessPoints().get(oldDataSourceId + "_timestamp");
        accessPointsManager.updateDataSourceAccessPoint(dataSource, defaultTimestampAP.typeOfIndex(),
                oldDataSourceId + "_timestamp", newDataSourceId + "_timestamp");
        log.info("Updated AccessPoint with id " + oldDataSourceId + "_timestamp" + " to " + newDataSourceId + "_timestamp");

        AccessPoint defaultRecordAP = dataSource.getAccessPoints().get(oldDataSourceId + "_record");
        accessPointsManager.updateDataSourceAccessPoint(dataSource, defaultRecordAP.typeOfIndex(),
                oldDataSourceId + "_record", newDataSourceId + "_record");
        log.info("Updated AccessPoint with id " + oldDataSourceId + "_record" + " to " + newDataSourceId + "_record");

        //Update Record Counts cache
        RepoxContextUtil.getRepoxManager().getRecordCountManager().renameDataSourceCounts(oldDataSourceId, newDataSourceId);

        //Update Scheduled Tasks
        RepoxContextUtil.getRepoxManager().getTaskManager().updateDataSourceTasks(oldDataSourceId, newDataSourceId);

        //Rename Folder
        dataSource.renameDataSourceDir(oldDataSourceId, newDataSourceId);
        log.info("Renamed Data Source (with new Id " + newDataSourceId + ") Repository Dir");
    }

    public boolean deleteDataSource(String dataSourceId) throws DocumentException, IOException {
        List<DataProvider> dataProviders = loadDataProviders();

        for (DataProvider dataProvider : dataProviders) {
            DataProvider currentDataProvider = dataProvider;
            for (DataSource currentDataSource : currentDataProvider.getDataSources()) {
                if (currentDataSource.getId().equals(dataSourceId)) {
                    boolean successfulDeletion = true;
                    log.info("Deleting Data Source with id " + currentDataSource.getId());

                    //Delete AccessPoints
                    for (AccessPoint accessPoint : currentDataSource.getAccessPoints().values()) {
                        try {
                            RepoxContextUtil.getRepoxManager().getAccessPointsManager().deleteIndex(accessPoint);
                            log.info("Deleted AccessPoint with id " + accessPoint.getId());
                        } catch (Exception e) {
                            log.error("Unable to delete Table from Database: " + accessPoint.getId(), e);
                            successfulDeletion = false;
                        }
                    }

                    //Delete repository dir
                    try {
                        File dataSourceDir = currentDataSource.getDataSourceDir();
                        boolean deletedDir = true;
                        if (dataSourceDir.exists()) {
                            deletedDir = FileUtil.deleteDir(dataSourceDir);
                        }

                        if (!deletedDir) {
                            successfulDeletion = false;
                            log.error("Unable to delete Data Source dir from Data Source with id " + dataSourceId);
                        } else {
                            log.info("Deleted Data Source dir with success from Data Source with id " + dataSourceId);
                        }
                    } catch (Exception e) {
                        log.error("Unable to delete Data Source dir from Data Source with id " + dataSourceId);
                        successfulDeletion = false;
                    }

                    //Delete Record Counts cache
                    RepoxContextUtil.getRepoxManager().getRecordCountManager().removeDataSourceCounts(dataSourceId);

                    //Delete Scheduled Tasks
                    RepoxContextUtil.getRepoxManager().getTaskManager().deleteDataSourceTasks(dataSourceId);

                    return successfulDeletion;
                }
            }
        }

        return true;
    }

    public boolean isIdValid(String id) {
        return (id.length() <= ID_MAX_SIZE) && Pattern.compile(ID_REGULAR_EXPRESSION).matcher(id).matches();
    }

    public List<DataSource> loadDataSources() throws DocumentException, IOException {
        List<DataSource> dataSources = new ArrayList<DataSource>();

        List<DataProvider> dataProviders = loadDataProviders();
        if(dataProviders != null) {
            for (DataProvider dataProvider : dataProviders) {
                dataSources.addAll(dataProvider.getDataSources());
            }
        }

        return dataSources;
    }

    public synchronized void saveDataProvider2Database(List<DataProvider> dataProviders) throws IOException {
        DataProvider dataProvider = new DataProvider();

        

    }
}
