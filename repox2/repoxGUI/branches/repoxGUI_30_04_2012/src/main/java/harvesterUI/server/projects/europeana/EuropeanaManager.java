package harvesterUI.server.projects.europeana;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import eu.europeana.definitions.domain.Country;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.FilterServiceImpl;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
import harvesterUI.server.projects.ProjectManager;
import harvesterUI.server.util.PagingUtil;
import harvesterUI.server.util.Util;
import harvesterUI.shared.RepoxStatisticsUI;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.*;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterType;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.tasks.OldTaskUI;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.RepoxManagerEuropeana;
import pt.utl.ist.repox.dataProvider.*;
import pt.utl.ist.repox.dataProvider.dataSource.IdExtracted;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.statistics.RepoxStatisticsEuropeana;
import pt.utl.ist.repox.statistics.StatisticsManagerEuropeana;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.util.*;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

//import com.google.common.collect.Iterables;
//import pt.utl.ist.repox.RepoxManagerEuDml;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 30-04-2012
 * Time: 11:29
 */
public class EuropeanaManager extends ProjectManager {

    public EuropeanaManager() {
        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuropeana());
    }

    public RepoxStatisticsUI getStatisticsInfo() throws ServerSideException {
        try {
//            DecimalFormat dec = new DecimalFormat("#.##");
            StatisticsManagerEuropeana manager = (StatisticsManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getStatisticsManager();
            RepoxStatisticsEuropeana statistics = (RepoxStatisticsEuropeana)manager.generateStatistics();

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
            String totalRecords = numberFormat.format(statistics.getRecordsTotal());

            int recordsAvgDataSource = (int)statistics.getRecordsAvgDataSource();
            int recordsAvgDataProvider = (int)statistics.getRecordsAvgDataProvider();

            RepoxStatisticsUI repoxStatisticsUI = new RepoxStatisticsUI(statistics.getGenerationDate(),
                    statistics.getDataSourcesIdExtracted(),
                    statistics.getDataSourcesIdGenerated(),statistics.getDataSourcesIdProvided(),
                    statistics.getDataProviders(),statistics.getDataSourcesOai(),statistics.getDataSourcesZ3950(),
                    statistics.getDataSourcesDirectoryImporter(),statistics.getDataSourcesMetadataFormats(),
                    recordsAvgDataSource,recordsAvgDataProvider,
                    statistics.getCountriesRecords(),totalRecords);
            repoxStatisticsUI.setAggregators(statistics.getAggregators());
            return repoxStatisticsUI;

        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Map<String,String> getFullCountryList() throws ServerSideException{
        try{
            return createEuropeanaCountriesMap();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private Map<String,String> createEuropeanaCountriesMap() {
        Map<String,String> results = new HashMap<String,String>();

        Country[] countries = Country.values();

        for(int index=0; index < countries.length; index++){
            String countryName = countries[index].name();

            Iterator iterator = Countries.getCountries().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry mapEntry=(Map.Entry)iterator.next();
                if(mapEntry.getValue().equals(countryName)) {
                    results.put((String)mapEntry.getKey(),(String)mapEntry.getValue());
                }
            }
        }
        return results;
    }

    public BaseModel loadAdminFormInfo() throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadCorrectedConfiguration(RepoxContextUtilDefault.CONFIG_FILE);
            BaseModel adminInfo = new BaseModel();
            adminInfo.set("repositoryFolder",properties.getProperty("repository.dir"));
            adminInfo.set("configFilesFolder",properties.getProperty("xmlConfig.dir"));
            adminInfo.set("oaiRequestFolder",properties.getProperty("oairequests.dir"));
            adminInfo.set("derbyDbFolder",properties.getProperty("database.dir"));
            adminInfo.set("baseUrn",properties.getProperty("baseurn"));
            adminInfo.set("defaultExportFolder",properties.getProperty("exportDefaultFolder"));
            adminInfo.set("adminEmail",properties.getProperty("administrator.email"));
            adminInfo.set("smtpServer",properties.getProperty("smtp.server"));
            adminInfo.set("smtpPort",properties.getProperty("smtp.port"));
            adminInfo.set("repoxDefualtEmailSender",properties.getProperty("default.email"));
            if(properties.getProperty("administrator.email.pass") != null)
                adminInfo.set("adminPass",properties.getProperty("administrator.email.pass"));
            adminInfo.set("httpRequestFolder",properties.getProperty("httprequests.dir"));
            adminInfo.set("ftpRequestFolder",properties.getProperty("ftprequests.dir"));
            adminInfo.set("sampleRecords",properties.getProperty("sample.records"));
            return adminInfo;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public void saveAdminFormInfo(BaseModel results) throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadCorrectedConfiguration(RepoxContextUtilDefault.CONFIG_FILE);
            properties.setProperty("repository.dir",(String)results.get("repositoryFolder"));
            properties.setProperty("xmlConfig.dir",(String)results.get("configFilesFolder"));
            properties.setProperty("oairequests.dir",(String)results.get("oaiRequestFolder"));
            properties.setProperty("database.dir",(String)results.get("derbyDbFolder"));
            properties.setProperty("baseurn",(String)results.get("baseUrn"));
            properties.setProperty("exportDefaultFolder",(String)results.get("defaultExportFolder"));
            properties.setProperty("administrator.email",(String)results.get("adminEmail"));
            properties.setProperty("smtp.server",(String)results.get("smtpServer"));
            properties.setProperty("smtp.port",(String)results.get("smtpPort"));
            properties.setProperty("default.email",(String)results.get("repoxDefaultEmailSender"));
            if(results.get("adminPass") != null)
                properties.setProperty("administrator.email.pass",(String)results.get("adminPass"));
            properties.setProperty("httprequests.dir",(String)results.get("httpRequestFolder"));
            properties.setProperty("ftprequests.dir",(String)results.get("ftpRequestFolder"));
            properties.setProperty("sample.records",(String)results.get("sampleRecords"));
            PropertyUtil.saveProperties(properties, RepoxContextUtilDefault.CONFIG_FILE);
            ConfigSingleton.getRepoxContextUtil().reloadProperties();
            System.out.println("Done save admin");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }


    /*********************************************************
     Paging Europeana Functions
     **********************************************************/

    public List<DataContainer> getParsedData(int offSet, int limit) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
            List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
            int realOffset = 0;
            int realLimit = 0;
            int numberLines = limit - offSet;

            for(int i = 0; i < allDataList.size() && offSet >= 0; i++){
                if(allDataList.get(i) instanceof DataProvider){
                    DataProvider dataProvider = (DataProvider)allDataList.get(i);
                    if(dataProvider.getDataSourceContainers() != null && offSet > 0) {
                        if(dataProvider.getDataSourceContainers().size() == 1){
                            offSet++;
                        }
                    }
                }
                offSet--;
                realOffset = i;
            }

            for(int i = realOffset; i < allDataList.size() && numberLines >= 0; i++){
                if(allDataList.get(i) instanceof DataProvider){
                    DataProvider dataProvider = (DataProvider)allDataList.get(i);
                    if(dataProvider.getDataSourceContainers() != null && numberLines > 0) {
                        if(dataProvider.getDataSourceContainers().size() == 1){
                            numberLines++;
                        }
                    }
                }
                numberLines--;
                realLimit = i;
            }
            if(allDataList.size() > 0){
                DataProviderUI currentDataProvider = null;
                AggregatorUI currentAggregator = null;
                for (int i = realOffset; i<(limit <= allDataList.size() ? realLimit : realLimit+1); i++){
                    if(allDataList.get(i) instanceof AggregatorEuropeana){
                        currentAggregator = parseAggregatorEuropeana((AggregatorEuropeana) allDataList.get(i));
                        mainData.add(currentAggregator);
                    }else if(allDataList.get(i) instanceof DataProvider){
                        if(currentAggregator == null){
                            currentAggregator = parseAggregatorEuropeana(((DataManagerEuropeana) RepoxServiceImpl.
                                    getRepoxManager().getDataManager()).getAggregatorParent(((DataProvider) allDataList.get(i)).getId()));
                            mainData.add(currentAggregator);
                        }
                        currentDataProvider = parseDataProviderEuropeana((DataProvider) allDataList.get(i), currentAggregator);
                        currentAggregator.add(currentDataProvider);
                        currentAggregator.addDataProvider(currentDataProvider);
                    } else if(allDataList.get(i) instanceof DataSourceContainer){
                        if(currentAggregator == null){
                            String parentDataProviderId = RepoxServiceImpl.
                                    getRepoxManager().getDataManager().getDataProviderParent(((DataSourceContainer) allDataList.get(i)).getDataSource().getId()).getId();
                            currentAggregator = parseAggregatorEuropeana(((DataManagerEuropeana) RepoxServiceImpl.
                                    getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId));
                            mainData.add(currentAggregator);
                        }
                        if(currentDataProvider == null){
                            currentDataProvider = parseDataProviderEuropeana(RepoxServiceImpl.getRepoxManager().getDataManager().
                                    getDataProviderParent(((DataSourceContainer) allDataList.get(i)).getDataSource().getId()),
                                    currentAggregator);
                            currentAggregator.add(currentDataProvider);
                            currentAggregator.addDataProvider(currentDataProvider);
                        }

                        // Show DP with only 1 DS on same row
                        if(RepoxServiceImpl.getRepoxManager().getDataManager().getDataProvider(currentDataProvider.getId()).getDataSourceContainers().size() == 1 ||
                                (i+1 > realLimit && currentDataProvider.getDataSourceUIList().size() == 0) ||
                                (i+1 < allDataList.size() &&  allDataList.get(i+1) instanceof DataProvider && currentDataProvider.getDataSourceUIList().size() == 0) ||
                                (allDataList.size() == i+1 && currentDataProvider.getDataSourceUIList().size() == 0)){
                            DataSourceUI dataSourceUI = parseDataSource((DataSourceContainer) allDataList.get(i), currentDataProvider);
                            currentDataProvider.setGridPropertiesForSingleDS(dataSourceUI.getName(), dataSourceUI.getDataSourceSet(),
                                    dataSourceUI.getMetadataFormat(), dataSourceUI.getIngest(), dataSourceUI.getRecords(),
                                    dataSourceUI.getUsedDate(),(String) dataSourceUI.get("type"));
                            currentDataProvider.addDataSource(dataSourceUI);
                        }else{
                            currentDataProvider.resetGridPropertiesForSingleDS();
                            DataSourceUI dataSourceUI = parseDataSource((DataSourceContainer) allDataList.get(i), currentDataProvider);
                            currentDataProvider.add(dataSourceUI);
                            currentDataProvider.addDataSource(dataSourceUI);
                        }
                    }
                }
            }

            return mainData;
        } catch (IndexOutOfBoundsException e){
//            return mainData;
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public DataContainer getSearchResult(ModelData data) throws ServerSideException{
        try{
            DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
            String id = data.get("id");

            if(data.get("prefix").equals("AGG")){
                DataManagerEuropeana dataManagerEuropeana = (DataManagerEuropeana)RepoxServiceImpl.getRepoxManager().getDataManager();
                AggregatorEuropeana aggregator = dataManagerEuropeana.getAggregator(id);
                AggregatorUI aggregatorUI = parseAggregatorEuropeana(aggregator);
                dataContainer.add(aggregatorUI);
                for(DataProvider dataProvider : aggregator.getDataProviders()){
                    DataProviderUI dataProviderUI = parseDataProviderEuropeana(dataProvider,aggregatorUI);
                    aggregatorUI.add(dataProviderUI);
                    aggregatorUI.addDataProvider(dataProviderUI);

                    if(dataProvider.getDataSourceContainers().size() == 1){
                        DataSourceContainer dataSourceContainer = getFirstHashElement(dataProvider.getDataSourceContainers());
                        DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                        dataProviderUI.setGridPropertiesForSingleDS(dataSourceUI.getName(), dataSourceUI.getDataSourceSet(),
                                dataSourceUI.getMetadataFormat(), dataSourceUI.getIngest(), dataSourceUI.getRecords(),
                                dataSourceUI.getUsedDate(),(String) dataSourceUI.get("type"));
                        dataProviderUI.addDataSource(dataSourceUI);
                    } else {
                        for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
                            DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                            dataProviderUI.add(dataSourceUI);
                            dataProviderUI.addDataSource(dataSourceUI);
                        }
                    }
                }
            }else if(data.get("prefix").equals("DP")){
                DataProvider dataProvider = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProvider(id);
                AggregatorUI aggregatorUI = parseAggregatorEuropeana(((DataManagerEuropeana)RepoxServiceImpl.
                        getRepoxManager().getDataManager()).getAggregatorParent(dataProvider.getId()));
                dataContainer.add(aggregatorUI);
                DataProviderUI dataProviderUI = parseDataProviderEuropeana(dataProvider,aggregatorUI);
                aggregatorUI.add(dataProviderUI);
                aggregatorUI.addDataProvider(dataProviderUI);

                if(dataProvider.getDataSourceContainers().size() == 1){
                    DataSourceContainer dataSourceContainer = getFirstHashElement(dataProvider.getDataSourceContainers());
                    DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                    dataProviderUI.setGridPropertiesForSingleDS(dataSourceUI.getName(), dataSourceUI.getDataSourceSet(),
                            dataSourceUI.getMetadataFormat(), dataSourceUI.getIngest(), dataSourceUI.getRecords(),
                            dataSourceUI.getUsedDate(),(String) dataSourceUI.get("type"));
                    dataProviderUI.addDataSource(dataSourceUI);
                } else {
                    for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
                        DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                        dataProviderUI.add(dataSourceUI);
                        dataProviderUI.addDataSource(dataSourceUI);
                    }
                }
            }else if(data.get("prefix").equals("DS")){
                String parentDataProviderId = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(id).getId();
                AggregatorUI aggregatorUI = parseAggregatorEuropeana(((DataManagerEuropeana)RepoxServiceImpl.
                        getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId));
                dataContainer.add(aggregatorUI);
                DataProviderUI dataProviderUI = parseDataProviderEuropeana(RepoxServiceImpl.getRepoxManager().getDataManager().
                        getDataProviderParent(id), aggregatorUI);
                aggregatorUI.add(dataProviderUI);
                aggregatorUI.addDataProvider(dataProviderUI);

                DataSourceContainer dataSourceContainer = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(id);
                DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                dataProviderUI.setGridPropertiesForSingleDS(dataSourceUI.getName(), dataSourceUI.getDataSourceSet(),
                        dataSourceUI.getMetadataFormat(), dataSourceUI.getIngest(), dataSourceUI.getRecords(),
                        dataSourceUI.getUsedDate(),(String) dataSourceUI.get("type"));
                dataProviderUI.addDataSource(dataSourceUI);
            }
            return dataContainer;
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<DataContainer> getViewResult(int offset, int limit, String type) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
            if(type.equals("AGGREAGATORS")){
                DataManagerEuropeana dataManagerEuropeana = (DataManagerEuropeana) RepoxServiceImpl.getRepoxManager().getDataManager();
                List<AggregatorEuropeana> aggregatorEuropeanaList = dataManagerEuropeana.getAggregatorsEuropeana();
                for (int i = offset; i<limit && i<aggregatorEuropeanaList.size(); i++){
                    mainData.add(parseAggregatorEuropeana(aggregatorEuropeanaList.get(i)));
                }
            }else if(type.equals("DATA_PROVIDERS")){
                List<DataProvider> dpList = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders();
                for (int i = offset; i<limit && i<dpList.size(); i++){
                    AggregatorUI currentAggregator = parseAggregatorEuropeana(((DataManagerEuropeana)RepoxServiceImpl.
                            getRepoxManager().getDataManager()).getAggregatorParent(dpList.get(i).getId()));
                    mainData.add(parseDataProviderEuropeana(dpList.get(i), currentAggregator));
                }
            }else if(type.equals("DATA_SETS")){
                DataManagerEuropeana dataManagerEuropeana = (DataManagerEuropeana) RepoxServiceImpl.getRepoxManager().getDataManager();
                List<AggregatorEuropeana> aggregatorEuropeanaList = dataManagerEuropeana.getAggregatorsEuropeana();
                for(AggregatorEuropeana aggregatorEuropeana : aggregatorEuropeanaList){
                    for(DataProvider dataProvider : aggregatorEuropeana.getDataProviders()){
                        if(dataProvider.getDataSourceContainers() != null) {
                            for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
                                DataProviderUI currentDataProvider = RepoxDataExchangeManager.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
                                        getDataProviderParent(dataSourceContainer.getDataSource().getId()));
                                mainData.add(parseDataSource(dataSourceContainer, currentDataProvider));
                            }
                        }
                    }
                }

            }
            return mainData;
        }catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<FilterAttribute> getDPAttributes(FilterType filterType) throws ServerSideException {
        Map<String,String> countryMap = createEuropeanaCountriesMap();

        List<FilterAttribute> values = new ArrayList<FilterAttribute>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        for(Object object : allDataList){
            if(object instanceof DataProvider){
                DataProvider dataProvider = (DataProvider)object;
                if(filterType.equals(FilterType.COUNTRY)){
                    String showName = "<img src=\"resources/images/countries/" +
                            dataProvider.getCountry() + ".png\" alt=\"" + countryMap.get(dataProvider.getCountry()) + "\" title=\"" +
                            countryMap.get(dataProvider.getCountry()) + "\"/> " + countryMap.get(dataProvider.getCountry());
                    values.add(new FilterAttribute(showName,dataProvider.getCountry()));
                }else if(filterType.equals(FilterType.DP_TYPE)){
                    DataProviderEuropeana dataProviderEuropeana = (DataProviderEuropeana)dataProvider;
                    values.add(new FilterAttribute(dataProviderEuropeana.getDataSetType().name(),dataProviderEuropeana.getDataSetType().name()));
                }
            }
        }
        return values;
    }

    public DataContainer getFilteredData(List<FilterQuery> filterQueries, FilterServiceImpl filterService)throws ServerSideException{
        try{
            List<Object> filteredData = new ArrayList<Object>(RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders());
            List<Object> dataToAdd = new ArrayList<Object>();
            for(FilterQuery filterQuery : filterQueries){
                dataToAdd.clear();
                for(Object data : filteredData){
                    // data provider comparison
                    if(data instanceof DataProvider){
                        if(filterService.compareCountry(((DataProvider) data).getCountry(), filterQuery))
                            filterService.addDataObjectToList(dataToAdd, data);
                        if(data instanceof DataProviderEuropeana && filterService.compareDPType(((DataProviderEuropeana) data).getDataSetType().name(), filterQuery))
                            filterService.addDataObjectToList(dataToAdd, data);

                        // data source comparison
                        for(DataSourceContainer dataSourceContainer : ((DataProvider) data).getDataSourceContainers().values()){
                            filterService.compareDataSetValues(dataSourceContainer, filterQuery, dataToAdd);
                        }
                    }
                }
                filteredData.clear();
                filteredData.addAll(dataToAdd);
            }

            DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
            for(DataContainer model : getParsedDataEuropeana(0,dataToAdd.size(),dataToAdd,filterService))
                dataContainer.add(model);
            return dataContainer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private List<DataContainer> getParsedDataEuropeana(int offSet, int limit, List<Object> filterList, FilterServiceImpl filterService) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
//            int extra = 0;
            DataProviderUI currentDataProvider = null;
            AggregatorUI currentAggregator = null;
            for (int i = offSet; i<limit && i<filterList.size(); i++){
                if(filterList.get(i) instanceof AggregatorEuropeana){
                    currentAggregator = parseAggregatorEuropeana((AggregatorEuropeana) filterList.get(i));
                    mainData.add(currentAggregator);
                }else if(filterList.get(i) instanceof DataProvider){
                    if(currentAggregator == null){
                        currentAggregator = parseAggregatorEuropeana(((DataManagerEuropeana)RepoxServiceImpl.
                                getRepoxManager().getDataManager()).getAggregatorParent(((DataProvider)filterList.get(i)).getId()));
                        mainData.add(currentAggregator);
                    }
                    currentDataProvider = parseDataProviderEuropeana((DataProvider)filterList.get(i),currentAggregator);
                    fillDataProvidersDataSets(currentDataProvider, (DataProvider) filterList.get(i));
                    currentAggregator.add(currentDataProvider);
                    currentAggregator.addDataProvider(currentDataProvider);
                } else if(filterList.get(i) instanceof DataSourceContainer){
                    if(currentAggregator == null || (currentAggregator != null && !filterService.isCorrectAggregator(((DataSourceContainer) filterList.get(i)).getDataSource().getId(), currentAggregator.getId()))){
                        String parentDataProviderId = RepoxServiceImpl.
                                getRepoxManager().getDataManager().getDataProviderParent(((DataSourceContainer) filterList.get(i)).getDataSource().getId()).getId();
                        currentAggregator = parseAggregatorEuropeana(((DataManagerEuropeana)RepoxServiceImpl.
                                getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId));
                        mainData.add(currentAggregator);
                    }
                    if(currentDataProvider == null || (currentDataProvider != null && !filterService.isCorrectDataProvider(((DataSourceContainer) filterList.get(i)).getDataSource().getId(), currentDataProvider.getId()))){
                        currentDataProvider = parseDataProviderEuropeana(RepoxServiceImpl.getRepoxManager().getDataManager().
                                getDataProviderParent(((DataSourceContainer)filterList.get(i)).getDataSource().getId()),
                                currentAggregator);
                        currentAggregator.add(currentDataProvider);
                        currentAggregator.addDataProvider(currentDataProvider);
                    }

                    // Show DP with only 1 DS on same row
                    if(RepoxServiceImpl.getRepoxManager().getDataManager().getDataProvider(currentDataProvider.getId()).getDataSourceContainers().size() == 1 ||
                            (filterList.size() == i+1 && currentDataProvider.getDataSourceUIList().size() == 0) ||
                            (i+1 < filterList.size() && !filterService.isNextDataProviderSame(((DataSourceContainer) filterList.get(i)).getDataSource().getId(), ((DataSourceContainer) filterList.get(i + 1)).getDataSource().getId())) && currentDataProvider.getDataSourceUIList().size() == 0){
                        DataSourceUI dataSourceUI = parseDataSource((DataSourceContainer)filterList.get(i),currentDataProvider);
                        currentDataProvider.setGridPropertiesForSingleDS(dataSourceUI.getName(), dataSourceUI.getDataSourceSet(),
                                dataSourceUI.getMetadataFormat(), dataSourceUI.getIngest(), dataSourceUI.getRecords(),
                                dataSourceUI.getUsedDate(),(String) dataSourceUI.get("type"));
                        currentDataProvider.addDataSource(dataSourceUI);
                    }else{
                        DataSourceUI dataSourceUI = parseDataSource((DataSourceContainer)filterList.get(i),currentDataProvider);
                        currentDataProvider.add(dataSourceUI);
                        currentDataProvider.addDataSource(dataSourceUI);
                    }
                }
            }

            return mainData;
        } catch (IndexOutOfBoundsException e){
//            return mainData;
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    /*********************************************************
     Save Europeana Functions
     **********************************************************/

    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize) throws ServerSideException {
        try{
            return EuropeanaSaveData.saveDataProvider(update, dataProviderUI,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviders, ModelData aggregatorUI, int pageSize) throws ServerSideException{
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            DataManagerEuropeana europeanaManager = (DataManagerEuropeana) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager();
            for(DataProviderUI dataProvider : dataProviders) {
                europeanaManager.moveDataProvider((String)aggregatorUI.get("id"),dataProvider.getId());
            }
            // Jump to the page of the FIRST data provider moved on the list
            saveDataResponse.setPage(PagingUtil.getDataPage(dataProviders.get(0).getId(), pageSize));
            saveDataResponse.setResponseState(ResponseState.SUCCESS);
            return saveDataResponse;
        } catch (IOException e) {
            saveDataResponse.setResponseState(ResponseState.ERROR);
            return saveDataResponse;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException{
        try{
            return EuropeanaSaveData.deleteDataProviders(dataProviderUIs);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse saveDataSource(boolean update, String type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
        try{
            return EuropeanaSaveData.saveDataSource(update, type, originalDSset, dataSourceUI,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
                               String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException{
        try{
            // Check http URLs
            String checkUrlResult = DataSetOperationsServiceImpl.checkURL(url);
            if(checkUrlResult.equals("URL_MALFORMED"))
                return "URL_MALFORMED";
            else if(checkUrlResult.equals("URL_NOT_EXISTS"))
                return "URL_NOT_EXISTS";

            EuropeanaSaveData.addAllOAIURL(url.trim(),dataProviderID,dsSchema,dsNamespace,dsMTDFormat,dataSetOperationsService.checkOAIURL(url.trim()),
                    name,nameCode,exportPath);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return "SUCCESS";
    }

    public SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize) throws ServerSideException{
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            DataManagerEuropeana europeanaManager = (DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager();
            for(DataSourceUI dataSourceUI : dataSourceUIs) {
                europeanaManager.moveDataSource((String)dataProviderUI.get("id"), dataSourceUI.getDataSourceSet());
            }

            // Jump to the page of the FIRST data source moved on the list
            saveDataResponse.setPage(PagingUtil.getDataPage(dataSourceUIs.get(0).getDataSourceSet(), pageSize));
            saveDataResponse.setResponseState(ResponseState.SUCCESS);
            return saveDataResponse;
        } catch (IOException e) {
            saveDataResponse.setResponseState(ResponseState.ERROR);
        } catch (DocumentException e) {
            saveDataResponse.setResponseState(ResponseState.ERROR);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return saveDataResponse;
    }

    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException{
        try{
            return EuropeanaSaveData.deleteDataSources(dataSourceUIs);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{
        try {
            RepoxManagerEuropeana repoxManagerEuropeana = (RepoxManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager();
            DataSourceContainer dataSourceContainer = repoxManagerEuropeana.getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet());

            DataSource dataSource = dataSourceContainer.getDataSource();
            dataSource.setExportDir(dataSourceUI.getExportDirectory());

            String recordsPerFile;
            if(dataSourceUI.getRecordsPerFile().equals("All"))
                recordsPerFile = "-1";
            else
                recordsPerFile = dataSourceUI.getRecordsPerFile();

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    startExportDataSource(dataSourceUI.getDataSourceSet(), recordsPerFile, dataSourceUI.getExportFormat());
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return true;
    }

    public List<OldTaskUI> getParsedOldTasks() throws ServerSideException{
        try{
            List<OldTaskUI> oldTaskUIs = new ArrayList<OldTaskUI>();
            List<AggregatorEuropeana> aggregatorEuropeanaList = ((DataManagerEuropeana)(RepoxServiceImpl.getRepoxManager().getDataManager())).getAggregatorsEuropeana();
            for(AggregatorEuropeana aggregatorEuropeana : aggregatorEuropeanaList){
                for(DataProvider dataProvider : aggregatorEuropeana.getDataProviders()){
                    for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
                        for(OldTask oldTask: dataSourceContainer.getDataSource().getOldTasksList()) {
                            OldTaskUI oldTaskUI = new OldTaskUI(dataSourceContainer.getDataSource().getId(),oldTask.getId(),oldTask.getLogName(),
                                    oldTask.getIngestType(),oldTask.getStatus(),oldTask.getRetries(),
                                    oldTask.getRetryMax(),oldTask.getDelay(),oldTask.getDateString(),oldTask.getRecords());
                            oldTaskUIs.add(oldTaskUI);
                        }
                    }
                }
            }
            return oldTaskUIs;
        } catch (Exception e){
            throw  new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private DataSourceUI parseDataSource(DataSourceContainer dataSourceContainer,DataProviderUI dataProviderUI) throws ServerSideException{
        DataSource dataSource = dataSourceContainer.getDataSource();
        String metadataFormat = dataSource.getMetadataFormat();
        String oaiSchemas = metadataFormat;
        if(dataSource.getMetadataTransformations() != null) {
            if(dataSource.getMetadataTransformations().size() > 0) {
                oaiSchemas = metadataFormat + " | " +
                        ((MetadataTransformation)dataSource.
                                getMetadataTransformations().values().toArray()[0]).getDestinationFormat();
            }
        }

        String recordPolicy;
        if(dataSource.getRecordIdPolicy() instanceof IdExtracted)
            recordPolicy = "IdExtracted";
        else
            recordPolicy = "IdGenerated";

        DataSourceUI newDataSourceUI = new DataSourceUI(dataProviderUI,dataSource.getDescription(),
                dataSource.getId(),oaiSchemas,"TODO","",
                dataSource.getDescription(),"","","","",
                recordPolicy,dataSource.getMetadataFormat());

        newDataSourceUI.setSchema(dataSource.getSchema());
        newDataSourceUI.setMetadataNamespace(dataSource.getNamespace());

        // External Services Run Type
        if(dataSource.getExternalServicesRunType() != null)
            newDataSourceUI.setExternalServicesRunType(dataSource.getExternalServicesRunType().name());

        DataSourceContainerEuropeana dsEurop = (DataSourceContainerEuropeana)dataSourceContainer;
        newDataSourceUI.setName(dsEurop.getName());
        newDataSourceUI.setNameCode(dsEurop.getNameCode());

        newDataSourceUI.setType(dataProviderUI.getType());

        newDataSourceUI.setExportDirectory(dataSource.getExportDir() != null ? dataSource.getExportDir().getAbsolutePath() : "");

        RepoxDataExchangeManager.parseDataSourceSubType(newDataSourceUI, dataSource);
        RepoxDataExchangeManager.getOldTasks(dataSource, newDataSourceUI);
        RepoxDataExchangeManager.getScheduledTasks(newDataSourceUI);

        RepoxDataExchangeManager.getMetadataTransformations(dataSource, newDataSourceUI);
        RepoxDataExchangeManager.getDataSetInfo(dataSource, newDataSourceUI);
        RepoxDataExchangeManager.getExternalServices(dataSource, newDataSourceUI);
        return newDataSourceUI;
    }

    public String sendFeedbackEmail(String userEmail, String title, String message, String messageType) throws ServerSideException {
        try {
//            String fromEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String developTeamMail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String[] recipientsEmail = new String[]{developTeamMail};
            String messageTitle = "[" + messageType + "] - " + title + " - Sent by user: " + userEmail;
            File[] attachments = null;
            // Europeana
            HashMap map = new HashMap<String, String>();
            map.put("mailType","sendFeedback");
            map.put("message",message);
            ((EmailUtilEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient()).
                    sendEmail(developTeamMail, recipientsEmail, messageTitle, "", null, map);

            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState sendUserDataEmail(String username, String email, String password) throws ServerSideException {
        try {
            // Europeana
            String fromEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String subject = "REPOX User Account Data";

            HashMap map = new HashMap<String, String>();
            map.put("user", username);
            map.put("password", password);
            map.put("mailType","userAccount");
            ((EmailUtilEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient()).
                    sendEmail(fromEmail, new String[] {email}, subject, "", null, map);

            return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public boolean isCorrectAggregator(String dataSetId, String aggregatorId) throws ServerSideException{
        String parentDataProviderId = RepoxServiceImpl.
                getRepoxManager().getDataManager().getDataProviderParent(dataSetId).getId();
        return ((DataManagerEuropeana)RepoxServiceImpl.getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId).getId().equals(aggregatorId);
    }

    private AggregatorUI parseAggregatorEuropeana(AggregatorEuropeana aggregatorEuropeana)  throws ServerSideException{
        String url;
        if(aggregatorEuropeana.getHomePage() != null)
            url = aggregatorEuropeana.getHomePage().toString();
        else
            url = "";
        return new AggregatorUI(aggregatorEuropeana.getId(),
                aggregatorEuropeana.getName(),aggregatorEuropeana.getNameCode(),url);
    }

    private DataProviderUI parseDataProviderEuropeana(DataProvider dataProvider, AggregatorUI aggregatorUI)  throws ServerSideException{
        DataProviderEuropeana dataProviderEuropeana = (DataProviderEuropeana) dataProvider;
        String country;
        if(dataProviderEuropeana.getCountry() != null)
            country = dataProviderEuropeana.getCountry();
        else
            country = "none";

        DataProviderUI newDataProviderUI = new DataProviderUI(dataProviderEuropeana.getId(),dataProviderEuropeana.getName(),
                country, (country != null && !country.equals("")) ? Countries.getCountries().get(country) : "");
        newDataProviderUI.setName(dataProviderEuropeana.getName());
        newDataProviderUI.setNameCode(dataProviderEuropeana.getNameCode());
        newDataProviderUI.setDescription(dataProviderEuropeana.getDescription());
        newDataProviderUI.setType(dataProviderEuropeana.getDataSetType().name());
        if(dataProviderEuropeana.getHomePage() != null)
            newDataProviderUI.setHomepage(dataProviderEuropeana.getHomePage().toString());
        newDataProviderUI.setType(dataProviderEuropeana.getDataSetType().name());
        newDataProviderUI.setParentAggregatorID(aggregatorUI.getId());
        return newDataProviderUI;
    }

    public void fillDataProvidersDataSets(DataProviderUI currentDataProvider, DataProvider dataProvider) throws ServerSideException{
        for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
            if(RepoxServiceImpl.getRepoxManager().getDataManager().getDataProvider(currentDataProvider.getId()).getDataSourceContainers().size() == 1){
                DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,currentDataProvider);
                currentDataProvider.setGridPropertiesForSingleDS(dataSourceUI.getName(), dataSourceUI.getDataSourceSet(),
                        dataSourceUI.getMetadataFormat(), dataSourceUI.getIngest(), dataSourceUI.getRecords(),
                        dataSourceUI.getUsedDate(),(String) dataSourceUI.get("type"));
                currentDataProvider.addDataSource(dataSourceUI);
            }else{
                DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,currentDataProvider);
                currentDataProvider.add(dataSourceUI);
                currentDataProvider.addDataSource(dataSourceUI);
            }
        }
    }

    public DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException{
        try{
            DataProvider dataProvider = RepoxServiceImpl.getRepoxManager().getDataManager().
                    getDataProviderParent(dataSetId);
            if(dataProvider == null)
                return null;
            DataProviderUI currentDataProvider = RepoxDataExchangeManager.parseDataProvider(dataProvider);
            DataSourceContainer container = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId);
            if(container == null)
                return null;
            return parseDataSource(container,currentDataProvider);
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<ModelData> getAllAggregators() throws ServerSideException{
        List<ModelData> aggregators = new ArrayList<ModelData>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        try{
            for (Object data : allDataList){
                if(data instanceof AggregatorEuropeana){
                    aggregators.add(createModel(((AggregatorEuropeana) data).getId(),((AggregatorEuropeana) data).getName()));
                }
            }
            return aggregators;
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse saveAggregator(boolean update, AggregatorUI aggregatorUI, int pageSize) throws ServerSideException{
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            String homepage = aggregatorUI.getHomepage();

            URL url;
            if(homepage != null && !homepage.isEmpty())
                url = new URL(aggregatorUI.getHomepage());
            else
                url = null;

            // Url doesn't exist
            if(url != null && !pt.utl.ist.util.FileUtil.checkUrl(aggregatorUI.getHomepage())){
                saveDataResponse.setResponseState(ResponseState.URL_NOT_EXISTS);
                return saveDataResponse;
            }

            if(update) {
                DataManagerEuropeana europeanaManager = (DataManagerEuropeana)RepoxServiceImpl.getRepoxManager().getDataManager();

                AggregatorEuropeana aggregatorEuropeana = europeanaManager.getAggregator(aggregatorUI.getId());
                aggregatorEuropeana = europeanaManager.updateAggregator(aggregatorEuropeana.getId(), aggregatorUI.getName(),
                        aggregatorUI.getNameCode(), aggregatorUI.getHomepage());

                saveDataResponse.setPage(PagingUtil.getDataPage(aggregatorEuropeana.getId(), pageSize));
                saveDataResponse.setResponseState(ResponseState.SUCCESS);
                return saveDataResponse;
            }
            else {
                DataManagerEuropeana europeanaManager = (DataManagerEuropeana)RepoxServiceImpl.getRepoxManager().getDataManager();
                AggregatorEuropeana aggregatorEuropeana = europeanaManager.createAggregator(aggregatorUI.getName(),
                        aggregatorUI.getNameCode(), aggregatorUI.getHomepage());

                saveDataResponse.setPage(PagingUtil.getDataPage(aggregatorEuropeana.getId(),pageSize));
                saveDataResponse.setResponseState(ResponseState.SUCCESS);
                return saveDataResponse;
            }
        } catch (AlreadyExistsException e){
            saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
            return saveDataResponse;
        }catch (MalformedURLException e) {
            saveDataResponse.setResponseState(ResponseState.URL_MALFORMED);
            return saveDataResponse;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException{
        try {
            for (AggregatorUI aggregatorUI : aggregatorUIs) {
                DataManagerEuropeana europeanaManager = (DataManagerEuropeana)RepoxServiceImpl.getRepoxManager().getDataManager();
                try {
                    europeanaManager.deleteAggregator(aggregatorUI.getId());
                    System.out.println("Done aggres removed");
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            System.out.println("Done aggres removed");
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<ModelData> getSearchComboData() throws ServerSideException{
        List<ModelData> searchData = new ArrayList<ModelData>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        try{
            for (Object data : allDataList){
                if(data instanceof AggregatorEuropeana){
                    ModelData agg = createModel(((AggregatorEuropeana) data).getId(),((AggregatorEuropeana) data).getName());
                    agg.set("prefix","AGG");
                    searchData.add(agg);
                }else if(data instanceof DataProvider){
                    ModelData dp = createModel(((DataProvider) data).getId(),((DataProvider) data).getName());
                    dp.set("prefix","DP");
                    searchData.add(dp);
                }else if(data instanceof DataSourceContainer){
                    ModelData ds = createModel(((DataSourceContainer) data).getDataSource().getId(),((DataSourceContainer) data).getDataSource().getId());
                    ds.set("prefix","DS");
                    searchData.add(ds);
                }
            }
            return searchData;
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public int getDataPage(String id, int pageSize){
        try{
            List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
            int showSize = RepoxServiceImpl.getRepoxManager().getDataManager().getShowSize();
            int extra = 0;
            for(int i = 0; i<showSize+extra; i+=pageSize){
                for(int j = i; j<pageSize+i && j<showSize+extra; j++){
                    String modelId = null;
                    if(allDataList.get(j) instanceof AggregatorEuropeana){
                        modelId = ((AggregatorEuropeana) allDataList.get(j)).getId();
                    }else if(allDataList.get(j) instanceof DataProvider){
                        DataProvider dataProvider = ((DataProvider) allDataList.get(j));
                        modelId = dataProvider.getId();
                        if(dataProvider.getDataSourceContainers().values().size() == 1)
                            extra++;
                    } else if(allDataList.get(j) instanceof DataSourceContainer){
                        modelId = ((DataSourceContainer) allDataList.get(j)).getDataSource().getId();
                    }

                    if(modelId != null && modelId.equals(id)){
                        return (i/pageSize)+1;
                    }
                }
            }

        } catch (ServerSideException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return -1;
    }

    private DataSourceContainer getFirstHashElement(HashMap<String,DataSourceContainer> containerHashMap){
        for(DataSourceContainer dataSourceContainer : containerHashMap.values()){
            return dataSourceContainer;
        }
        return null;
    }
}
