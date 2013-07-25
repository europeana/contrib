package pt.utl.ist.repox.data;

import eu.europeana.definitions.domain.Country;
import eu.europeana.definitions.domain.Language;
import eu.europeana.repox2sip.Repox2Sip;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.models.ProviderType;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.data.dataSource.*;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.marc.Iso2709FileExtract;
import pt.utl.ist.repox.marc.MarcXchangeFileExtract;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.util.PropertyUtil;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.XmlUtil;
import pt.utl.ist.repox.z3950.*;
import pt.utl.ist.util.DateUtil;
import pt.utl.ist.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

public class DataManager {
    private static final String ID_REGULAR_EXPRESSION = "[a-zA-Z][a-zA-Z_0-9]*";
    private static final int ID_MAX_SIZE = 64;
    private static final Logger log = Logger.getLogger(DataManager.class);

    private File configurationFile;
    private MetadataTransformationManager metadataTransformationManager;

    private Repox2Sip repox2sip;

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

    public Repox2Sip getRepox2sip() {
        return repox2sip;
    }

    public void setRepox2sip(Repox2Sip repox2sip) {
        this.repox2sip = repox2sip;
    }

    public DataManager(File configurationFile, MetadataTransformationManager metadataTransformationManager, RepoxConfiguration configuration) {
        super();
        this.configurationFile = configurationFile;
        if(!configurationFile.exists()) {
            try {
                saveData(new ArrayList<AggregatorRepox>());
            }
            catch(IOException e) {
                log.error("Can't create DataProviders configuration file", e);
            }
        }
        this.metadataTransformationManager = metadataTransformationManager;


        if(configuration.getUseSipDataBase().equals("true")){
            // fill repox2sip
            ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"/repox2sip-applicationRepox-context.xml"});
            this.repox2sip = (Repox2Sip) context.getBean("repox2sip");
        }
    }

    /**
     * Write all Data Providers from a specific Aggregator
     * @param rootNode
     * @param dataProviders
     */
    private void writeDataProviders(Element rootNode, List<DataProvider> dataProviders){
        for(DataProvider dataProvider : dataProviders) {
            Element dataProviderNode = rootNode.addElement("provider");
            dataProviderNode.addAttribute("id", dataProvider.getId());

            if(dataProvider.getIdDb() != null && dataProvider.getIdDb() != -1) {
                dataProviderNode.addAttribute("idDb",dataProvider.getIdDb().toString());
            }

            dataProviderNode.addElement("name").setText(dataProvider.getName());
            if(dataProvider.getCountry() != null) {
                dataProviderNode.addElement("country").setText(dataProvider.getCountry().name());
            }
            if(dataProvider.getDescription() != null) {
                dataProviderNode.addElement("description").setText(dataProvider.getDescription());
            }
            if(dataProvider.getDataSetType() != null) {
                dataProviderNode.addElement("type").setText(dataProvider.getDataSetType().toString());
            }

            if(dataProvider.getNameCode() != null) {
                dataProviderNode.addElement("nameCode").setText(dataProvider.getNameCode());
            }
            if(dataProvider.getHomePage() != null) {
                dataProviderNode.addElement("url").setText(dataProvider.getHomePage().toString());
            }

            if(dataProvider.getDataSources() != null && !dataProvider.getDataSources().isEmpty()) {
                for (DataSource dataSource : dataProvider.getDataSources()) {
                    Element sourceNode = dataProviderNode.addElement("source");

                    // Add generic Data Source Data
                    sourceNode.addAttribute("id", dataSource.getId());

                    if(dataSource.getIdDb() != -1){
                        sourceNode.addAttribute("idDb", String.valueOf(dataSource.getIdDb()));
                    }
                    sourceNode.addAttribute("metadataFormat", dataSource.getMetadataFormat());
                    sourceNode.addElement("description").setText(dataSource.getDescription());

                    if(dataSource.getLanguage() != null) {
                        sourceNode.addElement("language").setText(dataSource.getLanguage().toString());
                    }
                    if(dataSource.getName() != null) {
                        sourceNode.addElement("name").setText(dataSource.getName());
                    }
                    if(dataSource.getNameCode() != null) {
                        sourceNode.addElement("nameCode").setText(dataSource.getNameCode());
                    }

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

                                if(currentDataSource.getNamespaces() != null && currentDataSource.getNamespaces().size() > 0){
                                    Element namespacesElement = splitRecords.addElement("namespaces");

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
    }

    /**
     * Load all aggregators from XML file
     * @throws DocumentException
     * @throws IOException
     * @return List<AggregatorRepox>
     */
    public synchronized List<AggregatorRepox> loadAggregatorsRepox() throws DocumentException, IOException {
        return loadAggregatorsRepox(configurationFile);
    }


    /**
     * Load all aggregators from XML file
     * @param configurationFile
     * @throws DocumentException
     * @throws IOException
     * @return List<AggregatorRepox>
     */
    public synchronized List<AggregatorRepox> loadAggregatorsRepox(File configurationFile) throws DocumentException, IOException {
        List<AggregatorRepox> aggregatorsRepox = new ArrayList<AggregatorRepox>();

        if(!configurationFile.exists()) {
            return aggregatorsRepox;
        }

        SAXReader reader = new SAXReader();
        Document document = reader.read(configurationFile);

        Element root = document.getRootElement();

        for (Iterator aggIterator = root.elementIterator("aggregator"); aggIterator.hasNext();) {
            // read aggregatorRepox from XML file
            Element currentElementAgg = (Element) aggIterator.next();

            AggregatorRepox aggregatorRepox = new AggregatorRepox();
            aggregatorRepox.setId(currentElementAgg.attributeValue("id"));

            if(currentElementAgg.attributeValue("idDb") != null){
                aggregatorRepox.setIdDb(Long.valueOf(currentElementAgg.attributeValue("idDb")));
            }
            if(currentElementAgg.element("url") != null){
                aggregatorRepox.setHomePage(new URL(currentElementAgg.elementText("url")));
            }
            if(currentElementAgg.element("name") != null){
                aggregatorRepox.setName(currentElementAgg.elementText("name"));
            }
            if(currentElementAgg.element("nameCode") != null){
                aggregatorRepox.setNameCode(currentElementAgg.elementText("nameCode"));
            }

            for (Iterator provIterator = currentElementAgg.elementIterator("provider"); provIterator.hasNext();) {
                // read providers inside the aggregatorRepox
                Element currentElementProv = (Element) provIterator.next();

                String providerId = currentElementProv.attributeValue("id");

                long providerIdDb = -1;
                if(currentElementProv.attributeValue("idDb") != null){
                    providerIdDb = Long.valueOf(currentElementProv.attributeValue("idDb"));
                }

                String providerName = null;
                if(currentElementProv.elementText("name") != null){
                    providerName = currentElementProv.elementText("name");
                }
                String providerCountry = null;
                if(currentElementProv.elementText("country") != null){
                    providerCountry = currentElementProv.elementText("country");
                }
                String providerDescription = null;
                if(currentElementProv.elementText("description") != null){
                    providerDescription = currentElementProv.elementText("description");
                }
                String providerNameCode = null;
                if(currentElementProv.elementText("nameCode") != null){
                    providerNameCode = currentElementProv.elementText("nameCode");
                }
                String providerType = null;
                if(currentElementProv.elementText("type") != null){
                    providerType = currentElementProv.elementText("type");
                }
                URL providerHomePage = null;
                if(currentElementProv.elementText("url") != null){
                    providerHomePage = new URL(currentElementProv.elementText("url"));
                }


                Collection<DataSource> dataSources = new ArrayList<DataSource>();

                DataProvider dataProvider = new DataProvider(aggregatorRepox, providerId, providerName, providerDescription, dataSources, providerNameCode, ProviderType.get(providerType), Country.valueOf(providerCountry), providerHomePage, providerIdDb);

                for (Iterator dataSIterator = currentElementProv.elementIterator("source"); dataSIterator.hasNext();) {
                    // read data sources inside the aggregatorRepox
                    Element currentDataSourceElement = (Element) dataSIterator.next();

                    String dataSourceId = currentDataSourceElement.attributeValue("id");
                    long dataSourceIdDb = -1;
                    if(currentDataSourceElement.attributeValue("idDb") != null){
                        dataSourceIdDb = Long.valueOf(currentDataSourceElement.attributeValue("idDb"));
                    }
                    String dataSourceDescription = currentDataSourceElement.elementText("description");
                    String metadataFormat = currentDataSourceElement.attributeValue("metadataFormat");

                    String dataSourceLanguage = null;
                    if(currentDataSourceElement.elementText("language") != null){
                        dataSourceLanguage = currentDataSourceElement.elementText("language");
                    }

                    String dataSourceName = null;
                    if(currentDataSourceElement.elementText("name") != null){
                        dataSourceName = currentDataSourceElement.elementText("name");
                    }

                    String dataSourceNameCode = null;
                    if(currentDataSourceElement.elementText("nameCode") != null){
                        dataSourceNameCode = currentDataSourceElement.elementText("nameCode");
                    }

                    // Create RecordIdPolicy
                    Element recordIdPolicyNode = currentDataSourceElement.element("recordIdPolicy");
                    String recordIdPolicyClass = recordIdPolicyNode.attributeValue("type");
                    RecordIdPolicy recordIdPolicy;
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
                        DataSourceOai dataSource = new DataSourceOai(dataProvider, dataSourceId, dataSourceDescription, metadataFormat,
                                oaiSource, oaiSet, recordIdPolicy, metadataTransformations, Language.valueOf(dataSourceLanguage), dataSourceName, dataSourceNameCode, dataSourceIdDb);

                        dataProvider.addDataSource(dataSource);
                    }
                    else if(dataSourceType.equals("DataSourceDirectoryImporter")) {
                        String sourcesDirPath = currentDataSourceElement.elementText("sourcesDirPath");
                        String extractStrategyString = currentDataSourceElement.elementText("fileExtract");
                        DataSourceDirectoryImporter dataSource = null;

                        if(extractStrategyString.equals(Iso2709FileExtract.class.getSimpleName())) {
                            CharacterEncoding characterEncoding = CharacterEncoding.get(currentDataSourceElement.attributeValue("characterEncoding"));
                            String isoImplementationClass = currentDataSourceElement.attributeValue("isoImplementationClass");
                            FileExtractStrategy extractStrategy = new Iso2709FileExtract(isoImplementationClass);
                            dataSource = new DataSourceDirectoryImporter(dataProvider, dataSourceId, dataSourceDescription, metadataFormat, extractStrategy,
                                    characterEncoding, sourcesDirPath, recordIdPolicy, metadataTransformations, null, null, Language.valueOf(dataSourceLanguage), dataSourceName, dataSourceNameCode, dataSourceIdDb);
                        } else if(extractStrategyString.equals(MarcXchangeFileExtract.class.getSimpleName())) {
                            FileExtractStrategy extractStrategy = new MarcXchangeFileExtract();
                            dataSource = new DataSourceDirectoryImporter(dataProvider, dataSourceId, dataSourceDescription, metadataFormat, extractStrategy,
                                    sourcesDirPath, recordIdPolicy, metadataTransformations, null, null, Language.valueOf(dataSourceLanguage), dataSourceName, dataSourceNameCode, dataSourceIdDb);
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

                            dataSource = new DataSourceDirectoryImporter(dataProvider, dataSourceId, dataSourceDescription, metadataFormat, extractStrategy,
                                    sourcesDirPath, recordIdPolicy, metadataTransformations, recordXPath, namespaces, Language.valueOf(dataSourceLanguage), dataSourceName, dataSourceNameCode, dataSourceIdDb);
                        }
                        dataProvider.addDataSource(dataSource);
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

                        DataSourceZ3950 dataSourceZ3950 = new DataSourceZ3950(dataProvider, dataSourceId, dataSourceDescription,
                                harvestMethod, recordIdPolicy, metadataTransformations, Language.valueOf(dataSourceLanguage), dataSourceName, dataSourceNameCode, dataSourceIdDb);
                        dataProvider.addDataSource(dataSourceZ3950);
                    }
                    else {
                        throw new RuntimeException("Loading configuration from Data Source of type "
                                + dataSourceType + " not implemented");
                    }
                }
                aggregatorRepox.addDataProvider(dataProvider);
            }
            aggregatorsRepox.add(aggregatorRepox);
        }
        return aggregatorsRepox;
    }


    /**
     * Save all information in a XML file
     * @param aggregatorsRepox
     * @throws IOException
     */
    public synchronized void saveData(List<AggregatorRepox> aggregatorsRepox) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element rootNode = document.addElement("repox-data");

        for(AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            Element aggregatorNode = rootNode.addElement("aggregator");
            aggregatorNode.addAttribute("id", aggregatorRepox.getId());

            if(aggregatorRepox.getIdDb() != null){
                aggregatorNode.addAttribute("idDb", String.valueOf(aggregatorRepox.getIdDb()));
            }
            aggregatorNode.addElement("name").setText(aggregatorRepox.getName());

            if(aggregatorRepox.getNameCode() != null) {
                aggregatorNode.addElement("nameCode").setText(aggregatorRepox.getNameCode());
            }
            if(aggregatorRepox.getHomePage() != null) {
                aggregatorNode.addElement("url").setText(aggregatorRepox.getHomePage().toString());
            }

            if(aggregatorRepox.getDataProviders() != null){
                writeDataProviders(aggregatorNode, aggregatorRepox.getDataProviders());
            }
        }

        XmlUtil.writePrettyPrint(configurationFile, document);
    }


    /**
     * Add a new Aggregator
     * @param aggregatorRepox
     * @throws IOException
     * @throws DocumentException
     * @throws eu.europeana.repox2sip.Repox2SipException
     */
    public void saveAggregator(AggregatorRepox aggregatorRepox) throws IOException, DocumentException, Repox2SipException {
        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();
        aggregatorsRepox.add(aggregatorRepox);

        if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
            // REPOX2SIP add aggregator
            aggregatorRepox.addAggregator2Database(repox2sip);
        }

        saveData(aggregatorsRepox);
    }

    /**
     * Update Aggregator
     * @param aggregatorRepox
     * @param oldAggregatorId
     * @throws IOException
     * @throws DocumentException
     * @throws Repox2SipException
     */
    public void updateAggregator(AggregatorRepox aggregatorRepox, String oldAggregatorId) throws IOException, DocumentException, Repox2SipException {
        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox actualAggregatorRepox : aggregatorsRepox) {
            if(actualAggregatorRepox.getId().equals(oldAggregatorId)){
                aggregatorsRepox.remove(actualAggregatorRepox);
                break;
            }
        }
        aggregatorsRepox.add(aggregatorRepox);

        if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
            // REPOX2SIP update provider to DB
            aggregatorRepox.updateAggregator2Database(repox2sip);
        }

        saveData(aggregatorsRepox);
    }


    /**
     * Delete aggregator from REPOX
     * @param aggregatorId
     * @throws IOException
     * @throws DocumentException
     * @throws Repox2SipException
     */
    public void deleteAggregator(String aggregatorId) throws IOException, DocumentException, Repox2SipException {
        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        boolean deleted = false;

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            if(aggregatorRepox.getId().equals(aggregatorId)) {

                Iterator iteratorDP = aggregatorRepox.getDataProviders().iterator();
                while(iteratorDP.hasNext()) {
                    DataProvider dataProvider = (DataProvider)iteratorDP.next();

                    // remove all data sources from data provider
                    Iterator iteratorDS = dataProvider.getDataSources().iterator();
                    while(iteratorDS.hasNext()) {
                        DataSource currentDataSource = (DataSource)iteratorDS.next();

                        deleteDataSource(currentDataSource.getId());
                        //iteratorDS.remove();
                    }

                    if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                        // REPOX2SIP remove data provider from DB
                        dataProvider.deleteDataProviderFromDatabase(repox2sip);
                    }
                    // remove dataProvider from dataProvider's list
                    iteratorDP.remove();
                }

                if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                    // REPOX2SIP remove aggregator from DB
                    aggregatorRepox.deleteAggregatorFromDatabase(repox2sip);
                }
                aggregatorsRepox.remove(aggregatorRepox);

                deleted = true;
                break;
            }
        }
        if(deleted) {
            saveData(aggregatorsRepox);
        } else {
            throw new RuntimeException("Can't delete AggregatorRepox with id " + aggregatorId + " because it doesn't exist");
        }
    }


    /**
     * Add a new Data Provider
     * @param dataProvider
     * @throws IOException
     * @throws DocumentException
     * @throws eu.europeana.repox2sip.Repox2SipException
     */
    public void saveDataProvider(DataProvider dataProvider) throws IOException, DocumentException, Repox2SipException {
        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            if(aggregatorRepox.getId().equals(dataProvider.getAggregatorId())){

                if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                    // REPOX2SIP add data provider to DB
                    dataProvider.addDataProvider2Database(repox2sip);
                }

                aggregatorRepox.addDataProvider(dataProvider);
            }
        }
        saveData(aggregatorsRepox);
    }


    /**
     * Update Data Provider
     * @param dataProvider
     * @param oldDataProviderId
     * @throws IOException
     * @throws DocumentException
     * @throws Repox2SipException
     */
    public void updateDataProvider(DataProvider dataProvider, String oldDataProviderId) throws IOException, DocumentException, Repox2SipException {
        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            if(aggregatorRepox.getId().equals(dataProvider.getAggregatorId())){
                Iterator<DataProvider> iteratorDataProvider = aggregatorRepox.getDataProviders().iterator();
                while (iteratorDataProvider.hasNext()) {
                    DataProvider currentDataProvider = iteratorDataProvider.next();
                    if(currentDataProvider.getId().equals(oldDataProviderId)) {
                        iteratorDataProvider.remove();
                        break;
                    }
                }
                aggregatorRepox.getDataProviders().add(dataProvider);

                if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                    // REPOX2SIP update provider to DB
                    dataProvider.updateDataProvider2Database(repox2sip);
                }
                break;
            }
        }
        saveData(aggregatorsRepox);
    }



    /**
     * Delete data provider from REPOX
     * @param dataProviderId
     * @throws IOException
     * @throws DocumentException
     * @throws Repox2SipException
     */
    public void deleteDataProvider(String dataProviderId) throws IOException, DocumentException, Repox2SipException {
        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        boolean deleted = false;
        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            for (DataProvider dataProvider : aggregatorRepox.getDataProviders()) {
                if(dataProvider.getId().equals(dataProviderId)) {

                    for (DataSource currentDataSource : dataProvider.getDataSources()) {
                        if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                            // REPOX2SIP remove data source from DB
                            currentDataSource.deleteDataSourceFromDatabase(repox2sip);
                        }
                        deleteDataSource(currentDataSource.getId());
                    }

                    if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                        // REPOX2SIP remove data provider from DB
                        dataProvider.deleteDataProviderFromDatabase(repox2sip);
                    }
                    // remove dataProvider from dataProvider's list
                    aggregatorRepox.getDataProviders().remove(dataProvider);

                    deleted = true;
                    break;
                }
            }
        }

        if(deleted) {
            saveData(aggregatorsRepox);
        } else {
            throw new RuntimeException("Can't delete Data Provider with id " + dataProviderId + " because it doesn't exist");
        }
    }


    /**
     * Add Data Source to DataBase
     * @param dataSource
     * @throws Repox2SipException
     */
    public void saveDataSource(DataSource dataSource) throws Repox2SipException, IOException, DocumentException {
        DataProvider dataProviderDS = dataSource.getDataProvider();

        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            for (DataProvider dataProvider : aggregatorRepox.getDataProviders()) {
                if(dataProvider.getId().equals(dataProviderDS.getId())){
                    dataProvider.addDataSource(dataSource);

                    if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                        // REPOX2SIP add data source to DB
                        dataSource.addDataSource2Database(repox2sip);
                    }
                    break;
                }
            }
        }
        saveData(aggregatorsRepox);
    }


    /**
     * Update Data Source
     * @param dataProvider
     * @param newDataSource
     * @param oldDataSource
     * @throws IOException
     * @throws DocumentException
     * @throws Repox2SipException
     */
    public void updateDataSource(DataProvider dataProvider, DataSource newDataSource, DataSource oldDataSource) throws IOException, DocumentException, Repox2SipException {
        newDataSource.setIdDb(oldDataSource.getIdDb());
        dataProvider.getDataSources().remove(oldDataSource);
        dataProvider.getDataSources().add(newDataSource);

        if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
            // Repox2Sip update data source
            newDataSource.updateDataSet2Database(repox2sip);
        }
        updateDataProvider(dataProvider, dataProvider.getId());
    }



    /**
     * Update the Data Source with id newDataSourceId from oldDataSourceId.
     *
     * @param oldDataSourceId, newDataSourceId
     * @return true if deletion was completely successful, false otherwise.
     * @throws DocumentException
     */
    public void updateDataSourceId(String oldDataSourceId, String newDataSourceId)
            throws IOException, DocumentException, SQLException, Repox2SipException {

        DataSource dataSource = getDataSource(oldDataSourceId);

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

        if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
            // Repox2Sip update data source
            dataSource.updateDataSet2Database(repox2sip);
        }
    }


    public boolean deleteDataSource(String dataSourceId) throws DocumentException, IOException, Repox2SipException {
        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            for (DataProvider dataProvider : aggregatorRepox.getDataProviders()) {

                Iterator iteratorDS = dataProvider.getDataSources().iterator();
                while(iteratorDS.hasNext()){
                    DataSource currentDataSource = (DataSource)iteratorDS.next();
                    if (currentDataSource.getId().equals(dataSourceId)) {
                        log.info("Deleting Data Source with id " + currentDataSource.getId());

                        //Delete AccessPoints
                        for (AccessPoint accessPoint : currentDataSource.getAccessPoints().values()) {
                            try {
                                RepoxContextUtil.getRepoxManager().getAccessPointsManager().deleteIndex(accessPoint);
                                log.info("Deleted AccessPoint with id " + accessPoint.getId());
                            } catch (Exception e) {
                                log.error("Unable to delete Table from Database: " + accessPoint.getId(), e);
                                return false;
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
                                log.error("Unable to delete Data Source dir from Data Source with id " + dataSourceId);
                                return false;
                            } else {
                                log.info("Deleted Data Source dir with success from Data Source with id " + dataSourceId);
                            }
                        } catch (Exception e) {
                            log.error("Unable to delete Data Source dir from Data Source with id " + dataSourceId);
                            return false;
                        }

                        //Delete Record Counts cache
                        RepoxContextUtil.getRepoxManager().getRecordCountManager().removeDataSourceCounts(dataSourceId);

                        //Delete Scheduled Tasks
                        RepoxContextUtil.getRepoxManager().getTaskManager().deleteDataSourceTasks(dataSourceId);

                        if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                            // REPOX2SIP remove data source from DB
                            currentDataSource.deleteDataSourceFromDatabase(repox2sip);
                        }
                        iteratorDS.remove();
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Gets the DataProvider with dataProviderId from the configuration file if it exists or null otherwise.
     *
     * @param dataProviderId
     * @return DataProvider with id dataProviderId if exists, null otherwise
     * @throws DocumentException
     * @throws IOException
     */
    public DataProvider getDataProvider(String dataProviderId) throws DocumentException, IOException {
        Collection<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            for (DataProvider currentDataProvider : aggregatorRepox.getDataProviders()) {
                if (currentDataProvider.getId().equals(dataProviderId)) {
                    return currentDataProvider;
                }
            }
        }
        return null;
    }

    /**
     * Gets the AggregatorRepox with aggregatorId from the configuration file if it exists or null otherwise.
     *
     * @param aggregatorId
     * @return Aggregator with id aggregatorId if exists, null otherwise
     * @throws DocumentException
     * @throws IOException
     */
    public AggregatorRepox getAggregator(String aggregatorId) throws DocumentException, IOException {
        Collection<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();
        for (AggregatorRepox currentAggregatorRepox : aggregatorsRepox) {
            if (currentAggregatorRepox.getId().equals(aggregatorId)) {
                return currentAggregatorRepox;
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
        Collection<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            for (DataProvider currentDataProvider : aggregatorRepox.getDataProviders()) {
                DataSource dataSource = currentDataProvider.getDataSource(dataSourceId);
                if (dataSource != null) {
                    return dataSource;
                }
            }
        }
        return null;
    }



    public boolean isIdValid(String id) {
        return (id.length() <= ID_MAX_SIZE) && Pattern.compile(ID_REGULAR_EXPRESSION).matcher(id).matches();
    }

    public List<DataSource> loadDataSources() throws DocumentException, IOException {
        List<DataSource> dataSources = new ArrayList<DataSource>();

        List<AggregatorRepox> aggregatorsRepox = loadAggregatorsRepox();

        for (AggregatorRepox aggregatorRepox : aggregatorsRepox) {
            for (DataProvider dataProvider : aggregatorRepox.getDataProviders()) {
                dataSources.addAll(dataProvider.getDataSources());
            }
        }
        return dataSources;
    }




    /**
     * Get Data Provider Name
     * @param dataSourceId
     * @return
     */
    public static String getDataProviderName(String dataSourceId) {
        DataSource dataSource = null;
        try {
            dataSource = RepoxContextUtil.getRepoxManager().getDataManager().getDataSource(dataSourceId);
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


    public static void main(String[] args) {
        try{
            Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration("configuration.properties");
            RepoxConfiguration configuration = new RepoxConfiguration(configurationProperties);
            File metadataTransformationsFile = new File(configuration.getXmlConfigPath(), "metadataTransformations.xml");
            File xsltDir = new File(configuration.getXmlConfigPath(), RepoxConfiguration.METADATA_TRANSFORMATIONS_DIRNAME);
            MetadataTransformationManager metadataTransformationManager = new MetadataTransformationManager(metadataTransformationsFile, xsltDir);

            File dataProvidersFile = new File(configuration.getXmlConfigPath(), "dataProviders.xml");

            DataManager dm = new DataManager(dataProvidersFile, metadataTransformationManager, configuration);

            if(RepoxContextUtil.getRepoxManager().getConfiguration().getUseSipDataBase().equals("true")){
                ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"repox2sip-applicationRepox-context.xml"});
                dm.repox2sip = (Repox2Sip) context.getBean("repox2sip");

                AggregatorRepox aggregatorRepox = new AggregatorRepox();
                aggregatorRepox.setHomePage(new URL("http://google.pt"));
                aggregatorRepox.setName("aggregatorName");
                aggregatorRepox.setNameCode("nameCode");
                aggregatorRepox.setId("repoxId");

                aggregatorRepox.addAggregator2Database(dm.repox2sip);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


