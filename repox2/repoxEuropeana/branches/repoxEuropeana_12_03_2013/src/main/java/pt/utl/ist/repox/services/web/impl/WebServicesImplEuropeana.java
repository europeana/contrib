package pt.utl.ist.repox.services.web.impl;

import eu.europeana.repox2sip.models.ProviderType;
import org.apache.log4j.Logger;
import org.dom4j.*;
import pt.utl.ist.repox.RepoxConfigurationEuropeana;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.dataProvider.*;
import pt.utl.ist.repox.externalServices.ExternalRestService;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.services.web.WebServices;
import pt.utl.ist.repox.services.web.rest.RestUtils;
import pt.utl.ist.repox.task.*;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.util.DateUtil;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.IncompatibleInstanceException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 01-07-2011
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class WebServicesImplEuropeana implements WebServices {
    private static final Logger log = Logger.getLogger(WebServicesImpl.class);

    private String requestURI;

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public WebServicesImplEuropeana() {
        super();
    }


    public void writeAggregators(OutputStream out) throws DocumentException, IOException {
        List<AggregatorEuropeana> aggregatorsEuropeana = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregatorsEuropeana();

        Element aggregatorsElement = DocumentHelper.createElement("aggregators");
        for (AggregatorEuropeana currentAggregator : aggregatorsEuropeana) {
            Element currentAggregatorElement = currentAggregator.createElement(false);
            aggregatorsElement.add(currentAggregatorElement);
        }
        RestUtils.writeRestResponse(out, aggregatorsElement);
    }


    public void createAggregator(OutputStream out, String name, String nameCode, String homepageUrl) throws DocumentException, IOException {
        try {
            AggregatorEuropeana aggregatorEuropeana = ((DataManagerEuropeana) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createAggregator(name, nameCode, homepageUrl);
            RestUtils.writeRestResponse(out, aggregatorEuropeana.createElement(false));
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Aggregator: homepage \"" + homepageUrl + "\" was not valid.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Aggregator. Aggregator with name \"" + name + "\" and name code \"" + nameCode + "\" already exists.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Aggregator.");
        }
    }

    public void updateAggregator(OutputStream out, String id, String name, String nameCode, String homepageUrl) throws DocumentException, IOException {
        try {
            AggregatorEuropeana aggregatorEuropeana = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateAggregator(id, name, nameCode, homepageUrl);
            RestUtils.writeRestResponse(out, aggregatorEuropeana.createElement(false));
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating Aggregator: id \"" + id + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating Aggregator: homepage \"" + homepageUrl + "\" was not valid.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating Aggregator with id \"" + id + "\".");
        }
    }


    public void deleteAggregator(OutputStream out, String aggregatorId) throws DocumentException, IOException {
        try {
            ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).deleteAggregator(aggregatorId);
            Element currentAggregatorElement = DocumentHelper.createElement("success");
            currentAggregatorElement.setText("Aggregator with id \"" + aggregatorId + "\" was successfully deleted.");
            RestUtils.writeRestResponse(out, currentAggregatorElement);
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error deleting Aggregator. Data provider with id \"" + aggregatorId + "\" was not found.");
        }
    }

    public void getAggregator(OutputStream out, String aggregatorId) throws DocumentException, IOException {
        try {
            AggregatorEuropeana aggregatorEuropeana = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregator(aggregatorId);
            if(aggregatorEuropeana != null){
                Element aggregatorsElement = aggregatorEuropeana.createElement(false);
                RestUtils.writeRestResponse(out, aggregatorsElement);
            }
            else{
                createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving Aggregator. Aggregator with id \"" + aggregatorId + "\" was not found.");
            }
        }
        catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error retrieving Data Provider with id \"" + aggregatorId + "\".");
        }
    }


    @Override
    public void writeDataProviders(OutputStream out) throws DocumentException, IOException {
        List<DataProvider> dataProviders = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviders();

        Element dataProvidersElement = DocumentHelper.createElement("dataProviders");

        for (DataProvider dataProvider : dataProviders) {
            Element currentDataProviderElement = dataProvider.createElement(false);
            dataProvidersElement.add(currentDataProviderElement);
        }

        RestUtils.writeRestResponse(out, dataProvidersElement);
    }


    public void writeDataProviders(OutputStream out, String aggregatorId) throws DocumentException, IOException {
        AggregatorEuropeana aggregatorEuropeana = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregator(aggregatorId);

        Element dataProvidersElement = DocumentHelper.createElement("dataProviders");

        for (DataProvider dataProvider : aggregatorEuropeana.getDataProviders()) {
            Element currentDataProviderElement = dataProvider.createElement(false);
            dataProvidersElement.add(currentDataProviderElement);
        }

        RestUtils.writeRestResponse(out, dataProvidersElement);
    }


    @Deprecated
    public void createDataProvider(OutputStream out, String name, String country, String description) throws DocumentException {
    }

    public void createDataProvider(OutputStream out, String aggregatorId, String name, String country, String description,
                                   String nameCode, String url, String dataSetType) throws DocumentException, IOException {
        try {
            DataProvider dataProvider = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataProvider(aggregatorId,
                    name, country, description, nameCode, url, dataSetType);
            RestUtils.writeRestResponse(out, dataProvider.createElement(false));
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating Data Provider. Data provider with id \"" + aggregatorId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Data Provider. Data provider with id \"" +url+ "\" Already exists.");
        } catch (InvalidArgumentsException e) {
            String list = "";
            for (ProviderType providerType : ProviderType.values()) {
                if(!list.equals(""))
                    list = list + ", " + providerType.name();
                else
                    list = providerType.name();
            }
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Provider: invalid parameters - check homepage url \"" + url + "\" was not valid and dataSetType (" + list + ").");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Provider.");
        }
    }

    public void createDataProvider(OutputStream out, String aggregatorId, String dataProviderId, String name, String country, String description,
                                   String nameCode, String url, String dataSetType) throws DocumentException, IOException {

        DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(dataProviderId);

        if(dataProvider != null){
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Data Provider. Data provider with id \"" +url+ "\" Already exists.");
        }
        else{
            try {
                dataProvider = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataProvider(aggregatorId,
                        dataProviderId, name, country, description, nameCode, url, dataSetType);
                RestUtils.writeRestResponse(out, dataProvider.createElement(false));
            } catch (ObjectNotFoundException e) {
                createErrorMessage(out, MessageType.NOT_FOUND, "Error creating Data Provider. Data provider with id \"" + aggregatorId + "\" was not found.");
            } catch (AlreadyExistsException e) {
                createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Data Provider. Data provider with id \"" +url+ "\" Already exists.");
            } catch (InvalidArgumentsException e) {
                String list = "";
                for (ProviderType providerType : ProviderType.values()) {
                    if(!list.equals(""))
                        list = list + ", " + providerType.name();
                    else
                        list = providerType.name();
                }
                createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Provider: invalid parameters - check homepage url \"" + url + "\" was not valid and dataSetType (" + list + ").");
            } catch (IOException e) {
                createErrorMessage(out, MessageType.OTHER, "Error creating Data Provider.");
            }
        }
    }

    @Deprecated
    public void updateDataProvider(OutputStream out, String id, String name, String country, String description) throws DocumentException {
    }

    public void updateDataProvider(OutputStream out, String id, String name, String country, String description, String nameCode, String url, String dataSetType) throws DocumentException, IOException {
        try {
            DataProvider dataProvider = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataProvider(id,
                    name, country, description, nameCode, url, dataSetType);
            RestUtils.writeRestResponse(out, dataProvider.createElement(false));
        } catch (ObjectNotFoundException e) {
            String list = "";
            for (ProviderType providerType : ProviderType.values()) {
                if(!list.equals(""))
                    list = list + ", " + providerType.name();
                else
                    list = providerType.name();
            }
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating Data Provider: invalid parameters - check homepage url \"" + url + "\" was not valid and dataSetType (" + list + ").");
        } catch (InvalidArgumentsException e) {
            String list = "";
            for (ProviderType providerType : ProviderType.values()) {
                if(!list.equals(""))
                    list = list + ", " + providerType.name();
                else
                    list = providerType.name();
            }
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating Data Provider: invalid parameters - check homepage url \"" + url + "\" was not valid and dataSetType (" + list + ").");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating Data Provider with id \"" + id + "\".");
        }
    }


    @Override
    public void deleteDataProvider(OutputStream out, String dataProviderId) throws DocumentException, IOException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().deleteDataProvider(dataProviderId);
            Element currentDataProviderElement = DocumentHelper.createElement("success");
            currentDataProviderElement.setText("Data Provider with id \"" + dataProviderId + "\" was successfully deleted.");
            RestUtils.writeRestResponse(out, currentDataProviderElement);
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error deleting Data Provider. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error deleting Data Provider with id \"" + dataProviderId + "\".");
        }
    }

    public void moveDataProvider(OutputStream out, String dataProviderId, String newAggregatorId) throws DocumentException, IOException {
        try {
            ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).moveDataProvider(newAggregatorId, dataProviderId);
            Element currentDataProviderElement = DocumentHelper.createElement("success");
            currentDataProviderElement.setText("Data Provider with id \"" + dataProviderId + "\" was successfully moved to Aggregator " + newAggregatorId + ".");
            RestUtils.writeRestResponse(out, currentDataProviderElement);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error moving Data Provider with id \"" + dataProviderId + "\".");
        }
    }

    @Override
    public void getDataProvider(OutputStream out, String dataProviderId) throws DocumentException, IOException {
        try {
            DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(dataProviderId);

            if(dataProvider != null){
                Element dataProviderElement = dataProvider.createElement(false);
                RestUtils.writeRestResponse(out, dataProviderElement);
            }
            else{
                createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving Data Provider. Data Provider with id \"" + dataProviderId + "\" was not found.");
            }
        }
        catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error retrieving Data Provider with id \"" + dataProviderId + "\".");
        }
    }

    @Override
    public void writeDataSources(OutputStream out) throws DocumentException, IOException {
        HashMap<String, DataSourceContainer> dataSourceContainers = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers();

        Element dataSourcesElement = DocumentHelper.createElement("dataSources");

        for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
            Element currentDatasourceElement = dataSourceContainer.createElement();
            dataSourcesElement.add(currentDatasourceElement);
        }

        RestUtils.writeRestResponse(out, dataSourcesElement);
    }

    @Override
    public void writeDataSources(OutputStream out, String dataProviderId) throws DocumentException, IOException {
        DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(dataProviderId);

        if(dataProvider != null){
            Element dataSourcesElement = DocumentHelper.createElement("dataSources");

            for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
                Element currentDatasourceElement = dataSourceContainer.createElement();
                dataSourcesElement.add(currentDatasourceElement);
            }

            RestUtils.writeRestResponse(out, dataSourcesElement);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving Data Sources. Data provider with id \"" + dataProviderId + "\" was not found.");
        }
    }


    @Deprecated
    public void createDataSourceOai(OutputStream out, String dataProviderId, String id, String description,
                                    String schema, String namespace, String metadataFormat, String oaiSourceURL, String oaiSet) throws DocumentException {

    }


    public void createDataSourceOai(OutputStream out, String dataProviderId, String id, String description,
                                    String nameCode, String name, String exportPath, String schema, String namespace,
                                    String metadataFormat, String oaiSourceURL, String oaiSet) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                 exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceOai(dataProviderId,
                    id, description, nameCode, name, exportPath, schema, namespace, metadataFormat, oaiSourceURL, oaiSet,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source OAI. Unable to check OAI URL or Data source id \"" + dataProviderId + "\" was not valid.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source OAI. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source OAI. Data source with id \"" + id + "\" already exists.");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source OAI.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source OAI.");
        }
    }

    @Deprecated
    public void createDataSourceZ3950Timestamp(OutputStream out, String dataProviderId, String id, String description,
                                               String schema, String namespace, String address, String port, String database,
                                               String user, String password, String recordSyntax, String charset, String earliestTimestampString,
                                               String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, ParseException {
    }

    public void createDataSourceZ3950Timestamp(OutputStream out, String dataProviderId, String id, String description,
                                               String nameCode, String name, String exportPath, String schema, String namespace,
                                               String address, String port, String database, String user, String password,
                                               String recordSyntax, String charset, String earliestTimestampString,
                                               String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, ParseException, IOException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceZ3950Timestamp(dataProviderId,
                    id, description, nameCode, name, exportPath, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, earliestTimestampString, recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Base Z3950 Timestamp.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source Z3950 Timestamp.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source Z39.50 Timestamp. Data source id \"" + dataProviderId + "\" was not valid.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Z39.50 Timestamp. Data source with id \"" + id + "\" already exists.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source Z3950 Timestamp.");
        }
    }

    @Deprecated
    public void createDataSourceZ3950IdList(OutputStream out, String dataProviderId, String id, String description,
                                            String schema, String namespace, String address, String port, String database,
                                            String user, String password, String recordSyntax, String charset, String filePath,
                                            String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException {
    }

    public void createDataSourceZ3950IdList(OutputStream out, String dataProviderId, String id, String description,
                                            String nameCode, String name, String exportPath, String schema, String namespace,
                                            String address, String port, String database, String user, String password,
                                            String recordSyntax, String charset, String filePath,
                                            String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, IOException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceZ3950IdList(dataProviderId,
                    id, description, nameCode, name, exportPath, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, filePath, recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source Z39.50 Id List not Found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source Z39.50 Id List. Data source id \"" + dataProviderId + "\" was not valid.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Z39.50 Id List. Data source with id \"" + id + "\" already exists.");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data base Source Z39.50 Id List.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source Z39.50 Id List.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source Z39.50 Id List.");
        }
    }

    @Deprecated
    public void createDataSourceZ3950IdSequence(OutputStream out, String dataProviderId, String id, String description,
                                                String schema, String namespace, String address, String port,
                                                String database, String user, String password, String recordSyntax,
                                                String charset, String maximumIdString, String recordIdPolicyClass,
                                                String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, ParseException {
    }

    public void createDataSourceZ3950IdSequence(OutputStream out, String dataProviderId, String id, String description,
                                                String nameCode, String name, String exportPath, String schema, String namespace,
                                                String address, String port, String database, String user, String password,
                                                String recordSyntax, String charset, String maximumIdString,
                                                String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri) throws DocumentException, IOException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceZ3950IdSequence(dataProviderId,
                    id, description, nameCode, name, exportPath, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, maximumIdString, recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source Z39.50 Id List.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source Z39.50 Id Sequence. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating a Data Source Z39.50 Id Sequence. Data source id \"" + dataProviderId + "\" was not valid.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Z39.50 Id Sequence. Data source with id \"" + id + "\" already exists.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source Z39.50 Id Sequence.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source Z39.50 Id Sequence.");
        }
    }


    @Deprecated
    public void createDataSourceFtp(OutputStream out, String dataProviderId, String id, String description, String schema, String namespace,
                                    String metadataFormat, String isoFormat, String charset,
                                    String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                    String recordXPath, String server, String user, String password, String ftpPath) throws DocumentException {
    }

    public void createDataSourceFtp(OutputStream out, String dataProviderId, String id, String description,
                                    String nameCode, String name, String exportPath, String schema, String namespace,
                                    String metadataFormat, String isoFormat, String charset,
                                    String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                    String recordXPath, String server, String user, String password, String ftpPath) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceFtp(dataProviderId,
                    id, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri, recordXPath, server, user, password,
                    ftpPath, new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source. Check the record id policy; or data source id \"" + dataProviderId + "\" could be not valid.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source FTP. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source FTP.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source FTP. Data source with id \"" + id + "\" already exists.");
        }
    }

    @Deprecated
    public void createDataSourceHttp(OutputStream out, String dataProviderId, String id, String description, String schema, String namespace,
                                     String metadataFormat, String isoFormat, String charset,
                                     String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                     String recordXPath, String url) throws DocumentException {
    }

    public void createDataSourceHttp(OutputStream out, String dataProviderId, String id, String description,
                                     String nameCode, String name, String exportPath, String schema, String namespace,
                                     String metadataFormat, String isoFormat, String charset,
                                     String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                     String recordXPath, String url) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceHttp(dataProviderId,
                    id, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri, recordXPath, url,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source HTTP. Invalid URL or record id policy or the data source id \"" + dataProviderId + "\" was not valid.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error creating a Data Source HTTP. Data provider with id \"" + dataProviderId + "\" was not found.");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.ERROR_DATABASE, "Error creating Data Source HTTP.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source HTTP. Data source with id \"" + id + "\" already exists.");
        }
    }


    @Deprecated
    public void createDataSourceFolder(OutputStream out, String dataProviderId, String id, String description, String schema, String namespace,
                                       String metadataFormat, String isoFormat, String charset,
                                       String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                       String recordXPath, String sourcesDirPath) throws DocumentException {
    }

    public void createDataSourceFolder(OutputStream out, String dataProviderId, String id, String description,
                                       String nameCode, String name, String exportPath, String schema, String namespace,
                                       String metadataFormat, String isoFormat, String charset,
                                       String recordIdPolicyClass, String idXpath, String namespacePrefix, String namespaceUri,
                                       String recordXPath, String sourcesDirPath) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).createDataSourceFolder(dataProviderId,
                    id, description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri, recordXPath, sourcesDirPath,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source Folder. Invalid record id policy ot the data source id \"" + dataProviderId + "\" was not valid.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source Folder. Invalid record id policy ot the data source id \"" + dataProviderId + "\" was not valid.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating a Data Source Folder. Data source with id \"" + id + "\" already exists.");
        } catch (SQLException e) {
            createErrorMessage(out, MessageType.OTHER, "Error creating Data Source Folder");
        }
    }

    @Deprecated
    public void updateDataSourceOai(OutputStream out, String id, String description, String schema, String namespace,
                                    String metadataFormat, String oaiSourceURL, String oaiSet) throws DocumentException {
    }

    public void updateDataSourceOai(OutputStream out, String id, String description,
                                    String nameCode, String name, String exportPath, String schema, String namespace,
                                    String metadataFormat, String oaiSourceURL, String oaiSet) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceOai(id, id,
                    description, nameCode, name, exportPath, schema, namespace, metadataFormat, oaiSourceURL, oaiSet,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating an OAI Data Source. Data Provider was not found or the Data Source with id \"" + id + "\" was not found.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating an OAI Data Source. Unable to check OAI URL.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating an OAI Data Source. Incompatible Type OAI URL.");
        }
    }

    @Deprecated
    public void updateDataSourceZ3950Timestamp(OutputStream out, String id, String description, String schema, String namespace,
                                               String address, String port, String database, String user, String password,
                                               String recordSyntax, String charset, String earliestTimestampString,
                                               String recordIdPolicyClass, String idXpath, String namespacePrefix,
                                               String namespaceUri) throws DocumentException, ParseException {
    }

    public void updateDataSourceZ3950Timestamp(OutputStream out, String id, String description,
                                               String nameCode, String name, String exportPath, String schema, String namespace,
                                               String address, String port, String database, String user, String password,
                                               String recordSyntax, String charset, String earliestTimestampString,
                                               String recordIdPolicyClass, String idXpath, String namespacePrefix,
                                               String namespaceUri) throws DocumentException, ParseException, IOException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceZ3950Timestamp(id, id,
                    description, nameCode, name, exportPath, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, earliestTimestampString, recordIdPolicyClass, idXpath, namespacePrefix,
                    namespaceUri, new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Z39.50 Data Source with Time Stamp. Data Source with id \"" + id + "\" was not found.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating a Z39.50 Data Source with Time Stamp. Data Source with id \"" + id + "\" Incompatible type.");
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating a Z39.50 Data Source with Time Stamp. Data Source with id \"" + id + "\" Invalid Arguments");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating a Z39.50 Data Source with Time Stamp. Data Source with id \"" + id + "\" was not a Z39.50 Data Source with Time Stamp.");
        }
    }

    @Deprecated
    public void updateDataSourceZ3950IdList(OutputStream out, String id, String description, String schema,
                                            String namespace, String address, String port, String database,
                                            String user, String password, String recordSyntax, String charset,
                                            String filePath, String recordIdPolicyClass, String idXpath,
                                            String namespacePrefix, String namespaceUri) throws DocumentException, ParseException {
    }

    public void updateDataSourceZ3950IdList(OutputStream out, String id, String description, String nameCode, String name,
                                            String exportPath, String schema, String namespace, String address,
                                            String port, String database, String user, String password, String recordSyntax,
                                            String charset, String filePath, String recordIdPolicyClass, String idXpath,
                                            String namespacePrefix, String namespaceUri) throws DocumentException, ParseException, IOException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceZ3950IdList(id, id,
                    description, nameCode, name, exportPath, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, filePath, recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating a Z39.50 Data Source with ID List. Data Source with id \"" + id + "\" Invalid Arguments.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Z39.50 Data Source with ID List. Data Source with id \"" + id + "\" was not found.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating a Z39.50 Data Source with ID List. Data Source with id \"" + id + "\" Incompatible type.");
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating a Z39.50 Data Source with ID List. Data Source with id \"" + id + "\" was not a Z39.50 Data Source with ID List.");
        }
    }

    @Deprecated
    public void updateDataSourceZ3950IdSequence(OutputStream out, String id, String description, String schema,
                                                String namespace, String address, String port, String database,
                                                String user, String password, String recordSyntax, String charset,
                                                String maximumIdString, String recordIdPolicyClass, String idXpath,
                                                String namespacePrefix, String namespaceUri) throws DocumentException, ParseException {
    }

    public void updateDataSourceZ3950IdSequence(OutputStream out, String id, String description, String nameCode,
                                                String name, String exportPath, String schema, String namespace,
                                                String address, String port, String database, String user, String password,
                                                String recordSyntax, String charset, String maximumIdString,
                                                String recordIdPolicyClass, String idXpath, String namespacePrefix,
                                                String namespaceUri) throws DocumentException, ParseException, IOException {
        saveNewMetadataSchema(MetadataFormat.MarcXchange.toString(), schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceZ3950IdSequence(id, id,
                    description, nameCode, name, exportPath, schema, namespace, address, port, database, user, password,
                    recordSyntax, charset, maximumIdString, recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating a Z39.50 Data Source with ID Sequence. Data Source with id \"" + id + "\" Invalid Arguments.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Z39.50 Data Source with ID Sequence. Data Provider was not found.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating a Z39.50 Data Source with ID Sequence. Data Source with id \"" + id + "\" Incompatible type..");
        }
    }

    @Deprecated
    public void updateDataSourceFtp(OutputStream out, String id, String description, String schema, String namespace,
                                    String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass,
                                    String idXpath, String namespacePrefix, String namespaceUri, String recordXPath,
                                    String server, String user, String password, String ftpPath) throws DocumentException {
    }

    public void updateDataSourceFtp(OutputStream out, String id, String description, String nameCode, String name,
                                    String exportPath, String schema, String namespace, String metadataFormat,
                                    String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
                                    String namespacePrefix, String namespaceUri, String recordXPath,
                                    String server, String user, String password, String ftpPath) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceFtp(id, id,
                    description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri, recordXPath, server, user, password,
                    ftpPath, new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating Data Source FTP. Invalid record id policy.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating Data Source FTP. Data Provider was not found.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating Data Source FTP. Data Provider was not Incompatible.");
        }
    }

    @Deprecated
    public void updateDataSourceHttp(OutputStream out, String id, String description, String schema, String namespace,
                                     String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass,
                                     String idXpath, String namespacePrefix, String namespaceUri, String recordXPath,
                                     String url) throws DocumentException {
    }

    public void updateDataSourceHttp(OutputStream out, String id, String description, String nameCode, String name,
                                     String exportPath, String schema, String namespace, String metadataFormat,
                                     String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
                                     String namespacePrefix, String namespaceUri, String recordXPath,
                                     String url) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceHttp(id, id,
                    description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri, recordXPath, url,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error updating Data Source HTTP. Invalid record id policy.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error updating a Data Source HTTP. Data Provider was not found.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error updating a Data Source HTTP. Data Provider was not found.");
        }
    }

    @Deprecated
    public void updateDataSourceFolder(OutputStream out, String id, String description, String schema, String namespace,
                                       String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass,
                                       String idXpath, String namespacePrefix, String namespaceUri, String recordXPath,
                                       String sourcesDirPath) throws DocumentException {
    }


    public void updateDataSourceFolder(OutputStream out, String id, String description, String nameCode, String name,
                                       String exportPath, String schema, String namespace, String metadataFormat,
                                       String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
                                       String namespacePrefix, String namespaceUri, String recordXPath,
                                       String sourcesDirPath) throws DocumentException, IOException {
        saveNewMetadataSchema(metadataFormat, schema, namespace, out);
        try {
            if(exportPath.isEmpty())
                exportPath = ((RepoxConfigurationEuropeana)ConfigSingleton.getRepoxContextUtil().
                        getRepoxManager().getConfiguration()).getExportDefaultFolder() + File.separator + id;

            DataSource dataSource = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).updateDataSourceFolder(id, id,
                    description, nameCode, name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                    recordIdPolicyClass, idXpath, namespacePrefix, namespaceUri, recordXPath, sourcesDirPath,
                    new HashMap<String, MetadataTransformation>(), new ArrayList<ExternalRestService>());
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSource.getId());
            RestUtils.writeRestResponse(out, dataSourceContainer.createElement());
        } catch (InvalidArgumentsException e) {
            createErrorMessage(out, MessageType.INVALID_ARGUMENTS, "Error creating Data Source Folder. Invalid record id policy.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error updating Data Source Folder. Data Source with id \"" + id + "\" was not a Folder data source.");
        } catch (IncompatibleInstanceException e) {
            createErrorMessage(out, MessageType.INCOMPATIBLE_TYPE, "Error creating Data Source Folder. Incompatible record id policy.");
        }
    }

    @Override
    public void countRecordsDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException, SQLException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            int recordCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSourceId).getCount();

            Element successElement = DocumentHelper.createElement("recordCount");
            successElement.setText(String.valueOf(recordCount));
            RestUtils.writeRestResponse(out, successElement);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error counting records. Data source with ID \"" + dataSourceId + "\" was not found.");
        }
    }

    @Override
    public void deleteDataSource(OutputStream out, String id) throws DocumentException, IOException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().deleteDataSourceContainer(id);
            Element currentDataProviderElement = DocumentHelper.createElement("success");
            currentDataProviderElement.setText("Data Source with id \"" + id + "\" was successfully deleted.");
            RestUtils.writeRestResponse(out, currentDataProviderElement);
        } catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error deleting Data Source with id \"" + id + "\".");
        }
    }

    @Override
    public void getDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException {
        try {
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);

            if(dataSourceContainer != null){
                Element dataSourcesElement = dataSourceContainer.createElement();
                RestUtils.writeRestResponse(out, dataSourcesElement);
            }
            else{
                createErrorMessage(out, MessageType.NOT_FOUND, "Error retrieving Data Source. Data Source with id \"" + dataSourceId + "\" was not found.");
            }
        }
        catch (IOException e) {
            createErrorMessage(out, MessageType.OTHER, "Error retrieving Data Source with id \"" + dataSourceId + "\".");
        }
    }

    @Override
    public void startIngestDataSource(OutputStream out, String dataSourceId, boolean fullIngest) throws DocumentException, IOException, NoSuchMethodException, ClassNotFoundException, ParseException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startIngestDataSource(dataSourceId, fullIngest);
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Harvest of Data Source with ID \"" + dataSourceId + "\" will start in a few seconds.");
            RestUtils.writeRestResponse(out, successElement);
        } catch (NoSuchMethodException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source ingestion.");
        } catch (ClassNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source ingestion.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source ingestion.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error starting the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error starting the Data Source ingestion. ID \"" + dataSourceId + "\" is already harvesting.");
        }
    }

    @Override
    public void stopIngestDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException, NoSuchMethodException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().stopIngestDataSource(dataSourceId, Task.Status.CANCELED);
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Task for Data Source with ID \"" + dataSourceId + "\" was stopped successfully.");
            RestUtils.writeRestResponse(out, successElement);
        } catch (ObjectNotFoundException e) {
            if(e.getMessage().equals(dataSourceId)) {
                createErrorMessage(out, MessageType.OTHER, "Error stopping the Data Source task. No task is running for Data Source with ID \"" + dataSourceId + "\".");
            }
            else{
                createErrorMessage(out, MessageType.NOT_FOUND, "Error stopping the Data Source task. ID \"" + dataSourceId + "\" was not found.");
            }
        } catch (ClassNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error stopping the Data Source task.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error stopping the Data Source task.");
        }
    }

    @Override
    public void scheduleIngestDataSource(OutputStream out, String dataSourceId, String firstRunDate, String firstRunHour,
                                         String frequency, String xmonths, String fullIngest) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            DataSource dataSource = dataSourceContainer.getDataSource();
            String newTaskId = dataSource.getNewTaskId();
            ScheduledTask scheduledTask = new ScheduledTask();
            scheduledTask.setId(newTaskId);
            scheduledTask.setDate(firstRunDate);
            scheduledTask.setHour(Integer.valueOf(firstRunHour.split(":")[0]));
            scheduledTask.setMinute(Integer.valueOf(firstRunHour.split(":")[1]));

            if(frequency.equalsIgnoreCase("once")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.ONCE);
            }
            else if(frequency.equalsIgnoreCase("daily")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.DAILY);
            }
            else if(frequency.equalsIgnoreCase("weekly")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.WEEKLY);
            }
            else if(frequency.equalsIgnoreCase("xmonthly")){
                scheduledTask.setFrequency(ScheduledTask.Frequency.XMONTHLY);
                scheduledTask.setXmonths(Integer.valueOf(xmonths));
            }

            scheduledTask.setTaskClass(IngestDataSource.class);
            // Parameter 0 -> taskId; Parameter 1 -> dataSourceId; Parameter 2 -> isFullIngest?
            String[] parameters = new String[]{newTaskId, dataSource.getId(), (Boolean.valueOf(fullIngest)).toString()};
            scheduledTask.setParameters(parameters);

            if(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().taskAlreadyExists(dataSource.getId(), DateUtil.date2String(scheduledTask.getFirstRun().getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS), scheduledTask.getFrequency(), fullIngest)){
                createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error scheduling the Data Source ingestion. A task for this specific hour and data source ID \"" + dataSourceId + "\" is already scheduled.");
            }
            else{
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().saveTask(scheduledTask);

                Element successElement = DocumentHelper.createElement("success");
                successElement.setText("Ingest successfully scheduled for Data Source with ID \"" + dataSourceId + "\" .");
                RestUtils.writeRestResponse(out, successElement);
            }
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    @Override
    public void scheduleListDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            Element scheduleTasksElement = DocumentHelper.createElement("scheduleTasks");

            for (ScheduledTask scheduledTask : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getScheduledTasks()) {
                if(scheduledTask.getParameters()[1].equals(dataSourceId)){


                    Element scheduledTaskElement = scheduleTasksElement.addElement("task");
                    scheduledTaskElement.addAttribute("id", scheduledTask.getId());

                    Element timeElement = scheduledTaskElement.addElement("time");
                    timeElement.setText(DateUtil.date2String(scheduledTask.getFirstRun().getTime(), TimeUtil.LONG_DATE_FORMAT_NO_SECS));

                    Element frequencyElement = scheduledTaskElement.addElement("frequency");
                    frequencyElement.addAttribute("type", scheduledTask.getFrequency().toString());
                    if(scheduledTask.getFrequency().equals(ScheduledTask.Frequency.XMONTHLY)) {
                        frequencyElement.addAttribute("xmonthsPeriod", scheduledTask.getXmonths().toString());
                    }

                    scheduledTaskElement.addElement("fullIngest").addText(scheduledTask.getParameters()[2]);
                }
            }
            RestUtils.writeRestResponse(out, scheduleTasksElement);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    @Override
    public void harvestStatusDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            DataSource dataSource = dataSourceContainer.getDataSource();

            Element harvestingStatus = DocumentHelper.createElement("harvestingStatus");
            if(dataSource.getStatus() != null){
                if(dataSource.getStatusString().equalsIgnoreCase(DataSource.StatusDS.RUNNING.name())){
                    try {
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Element statusMessage = DocumentHelper.createElement("status");
                    statusMessage.setText(dataSource.getStatusString());
                    harvestingStatus.add(statusMessage);

                    long timeLeft = dataSource.getTimeLeft();
                    if(timeLeft != -1){
                        Element timeLeftMessage = DocumentHelper.createElement("timeLeft");
                        timeLeftMessage.setText(String.valueOf(timeLeft));
                        harvestingStatus.add(timeLeftMessage);
                    }

                    float percentage = dataSource.getPercentage();
                    if(percentage >= 0){
                        Element percentageMessage = DocumentHelper.createElement("percentage");
                        percentageMessage.setText(String.valueOf(percentage));
                        harvestingStatus.add(percentageMessage);
                    }

                    float totalRecords = dataSource.getTotalRecords2Harvest();
                    if(totalRecords > 0){
                        Element recordsMessage = DocumentHelper.createElement("records");
                        try {
                            recordsMessage.setText(String.valueOf(dataSource.getIntNumberRecords() + "/" + dataSource.getNumberOfRecords2HarvestStr()));
                            harvestingStatus.add(recordsMessage);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else{
                    Element statusMessage = DocumentHelper.createElement("status");
                    statusMessage.setText(dataSource.getStatusString());
                    harvestingStatus.add(statusMessage);

                    if(dataSource.getStatusString().equalsIgnoreCase(DataSource.StatusDS.OK.name())){
                        Element recordsMessage = DocumentHelper.createElement("records");
                        try {
                            recordsMessage.setText(String.valueOf(dataSource.getIntNumberRecords()) + "/" + String.valueOf(dataSource.getIntNumberRecords()));
                            harvestingStatus.add(recordsMessage);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else{
                Element statusMessage = DocumentHelper.createElement("status");
                statusMessage.setText("undefined");
                harvestingStatus.add(statusMessage);
            }
            RestUtils.writeRestResponse(out, harvestingStatus);
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    @Override
    public void startExportDataSource(OutputStream out, String dataSourceId, String recordsPerFile, String metadataExportFormat) throws DocumentException, IOException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startExportDataSource(dataSourceId, recordsPerFile, metadataExportFormat);
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Exportation of Data Source with ID \"" + dataSourceId + "\" will start in a few seconds.");
            RestUtils.writeRestResponse(out, successElement);
        } catch (AlreadyExistsException e) {
            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error starting the Data Source exportation. ID \"" + dataSourceId + "\" is already exporting.");
        } catch (ClassNotFoundException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source exportation.");
        } catch (NoSuchMethodException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source exportation.");
        } catch (ParseException e) {
            createErrorMessage(out, MessageType.OTHER, "Error starting Data Source exportation.");
        } catch (ObjectNotFoundException e) {
            createErrorMessage(out, MessageType.NOT_FOUND, "Error starting the Data Source exportation. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    @Override
    public void logDataSource(OutputStream out, String dataSourceId) throws DocumentException, IOException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId);
        if(dataSourceContainer != null){
            DataSource dataSource = dataSourceContainer.getDataSource();
            if(dataSource.getLogFilenames().size() > 0){

                ArrayList<String> logFileContent = FileUtil.readFile(new File(dataSource.getLogsDir(), dataSource.getLogFilenames().get(0)));

                Element logElement = DocumentHelper.createElement("log");

                for (String line : logFileContent) {
                    logElement.addElement("line").addText(line);
                }

                RestUtils.writeRestResponse(out, logElement);
            }
            else{
                createErrorMessage(out, MessageType.OTHER, "Error showing log file for Data Source. There is no logs for Data Source with ID \"" + dataSourceId + "\".");
            }
        }
        else{
            createErrorMessage(out, MessageType.NOT_FOUND, "Error scheduling the Data Source ingestion. ID \"" + dataSourceId + "\" was not found.");
        }
    }

    @Override
    public void harvestingDataSources(OutputStream out) throws DocumentException, IOException {
        Element runningTasksElement = DocumentHelper.createElement("runningTasks");
        for (Task task : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getRunningTasks()){
            runningTasksElement.addElement("dataSource").addText(task.getParameters()[1]);
        }
        RestUtils.writeRestResponse(out, runningTasksElement);
    }


    @Override
    public void getRecord(OutputStream out, Urn recordUrn) throws IOException, DocumentException, SQLException {
        Node detachedRecordNode = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getRecord(recordUrn);

        Element recordResultElement = DocumentHelper.createElement("recordResult");
        recordResultElement.addAttribute("urn", recordUrn.toString());
        recordResultElement.add(detachedRecordNode);
        RestUtils.writeRestResponse(out, recordResultElement);
    }

    @Override
    public void saveRecord(OutputStream out, String recordId, String dataSourceId, String recordString) throws IOException, DocumentException {
        MessageType returnMessage = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).saveRecord(recordId, dataSourceId, recordString);
        if(returnMessage == MessageType.OK){
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Record with id " + recordId + " saved successfully");
            RestUtils.writeRestResponse(out, successElement);
        }
        else if(returnMessage == MessageType.NOT_FOUND){
            createErrorMessage(out, MessageType.NOT_FOUND, "Unable to save or update record. Data source with ID \"" + dataSourceId + "\" was not found.");
        }
        else{
            createErrorMessage(out, MessageType.OTHER, "Unable to save Record");
        }
    }

    @Override
    public void deleteRecord(OutputStream out, String recordId) throws IOException {
        MessageType returnMessage = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).deleteRecord(recordId);
        if(returnMessage == MessageType.OK){
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Record with id " + recordId + " marked as deleted successfully");
            RestUtils.writeRestResponse(out, successElement);
        }
        else{
            createErrorMessage(out, MessageType.OTHER, "Unable to permanently remove Record");
        }
    }

    @Override
    public void eraseRecord(OutputStream out, String recordId) throws IOException {
        MessageType returnMessage = ((DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).eraseRecord(recordId);
        if(returnMessage == MessageType.OK){
            Element successElement = DocumentHelper.createElement("success");
            successElement.setText("Record with id " + recordId + " permanently removed successfully");
            RestUtils.writeRestResponse(out, successElement);
        }
        else{
            createErrorMessage(out, MessageType.OTHER, "Unable to permanently remove Record");
        }
    }

    public void createErrorMessage(OutputStream out, MessageType type, String cause) throws IOException {
        Element errorMessage = DocumentHelper.createElement("error");
        errorMessage.addAttribute("type", type.name());
        errorMessage.addAttribute("requestURI", getRequestURI());
        errorMessage.addAttribute("cause", cause);

        RestUtils.writeRestResponse(out, errorMessage);
    }

    private void saveNewMetadataSchema(String metadataFormat,String schema, String namespace,OutputStream out){
        // Create new MDR schema if it doesn't exist
        boolean exists = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                schemaExists(metadataFormat);

        if(!exists){
            List<MetadataSchemaVersion> metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
            metadataSchemaVersions.add(new MetadataSchemaVersion(1.0,schema));
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                    saveMetadataSchema(null,metadataFormat,null,namespace,null,null,metadataSchemaVersions);
        }
    }
}