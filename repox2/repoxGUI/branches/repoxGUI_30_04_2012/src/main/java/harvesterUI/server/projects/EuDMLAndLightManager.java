package harvesterUI.server.projects;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.FilterServiceImpl;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
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
import pt.utl.ist.repox.dataProvider.*;
import pt.utl.ist.repox.dataProvider.dataSource.IdExtracted;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.statistics.RepoxStatisticsDefault;
import pt.utl.ist.repox.statistics.StatisticsManagerDefault;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.PropertyUtil;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 30-04-2012
 * Time: 11:29
 */
public class EuDMLAndLightManager extends ProjectManager {

    public EuDMLAndLightManager() {
        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
    }

    /*
     * Same functions as LIGHT
     */

    public RepoxStatisticsUI getStatisticsInfo() throws ServerSideException {
        try {
//            DecimalFormat dec = new DecimalFormat("#.##");
            StatisticsManagerDefault manager = (StatisticsManagerDefault) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getStatisticsManager();
            RepoxStatisticsDefault statistics = (RepoxStatisticsDefault)manager.generateStatistics();

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
            String totalRecords = numberFormat.format(statistics.getRecordsTotal());

            int recordsAvgDataSource = (int)statistics.getRecordsAvgDataSource();
            int recordsAvgDataProvider = (int)statistics.getRecordsAvgDataProvider();

            return new RepoxStatisticsUI(statistics.getGenerationDate(),statistics.getDataSourcesIdExtracted(),
                    statistics.getDataSourcesIdGenerated(),statistics.getDataSourcesIdProvided(),
                    statistics.getDataProviders(), statistics.getDataSourcesOai(), statistics.getDataSourcesZ3950(),
                    statistics.getDataSourcesDirectoryImporter(),statistics.getDataSourcesMetadataFormats(),
                    recordsAvgDataSource,recordsAvgDataProvider,
                    statistics.getCountriesRecords(),totalRecords);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Map<String,String> getFullCountryList() throws ServerSideException{
        try{
            return Countries.getCountries();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
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
            adminInfo.set("defaultExportFolder",properties.getProperty("repository.dir"));
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
     Paging Light and Eudml Functions
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
                for (int i = realOffset; i<(limit <= allDataList.size() ? realLimit : realLimit+1); i++){
                    if(allDataList.get(i) instanceof DataProvider){
                        currentDataProvider = RepoxDataExchangeManager.parseDataProvider((DataProvider) allDataList.get(i));
                        mainData.add(currentDataProvider);
                    } else if(allDataList.get(i) instanceof DataSourceContainer){
                        if(currentDataProvider == null){
                            currentDataProvider = RepoxDataExchangeManager.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
                                    getDataProviderParent(((DataSourceContainer) allDataList.get(i)).getDataSource().getId()));
                            mainData.add(currentDataProvider);
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

            if(data.get("prefix").equals("DP")){
                DataProvider dataProvider = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProvider(id);
                DataProviderUI dataProviderUI = RepoxDataExchangeManager.parseDataProvider(dataProvider);
                dataContainer.add(dataProviderUI);

                if(dataProvider.getDataSourceContainers().size() == 1){
                    DataSourceContainer dataSourceContainer = getFirstHashElement(dataProvider.getDataSourceContainers());
                    DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer, dataProviderUI);
                    dataProviderUI.setGridPropertiesForSingleDS(dataSourceUI.getName(), dataSourceUI.getDataSourceSet(),
                            dataSourceUI.getMetadataFormat(), dataSourceUI.getIngest(), dataSourceUI.getRecords(),
                            dataSourceUI.getUsedDate(),(String) dataSourceUI.get("type"));
                    dataProviderUI.addDataSource(dataSourceUI);
                } else {
                    for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
                        DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer, dataProviderUI);
                        dataProviderUI.add(dataSourceUI);
                        dataProviderUI.addDataSource(dataSourceUI);
                    }
                }
            }else if(data.get("prefix").equals("DS")){
                DataProviderUI dataProviderUI = RepoxDataExchangeManager.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
                        getDataProviderParent(id));
                dataContainer.add(dataProviderUI);

                DataSourceContainer dataSourceContainer = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(id);
                DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer, dataProviderUI);
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

    private DataSourceContainer getFirstHashElement(HashMap<String,DataSourceContainer> containerHashMap){
        for(DataSourceContainer dataSourceContainer : containerHashMap.values()){
            return dataSourceContainer;
        }
        return null;
    }

    public List<DataContainer> getViewResult(int offset, int limit, String type) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
            if(type.equals("DATA_PROVIDERS")){
                List<DataProvider> dpList = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders();
                for (int i = offset; i<limit && i<dpList.size(); i++){
                    mainData.add(RepoxDataExchangeManager.parseDataProvider(dpList.get(i)));
                }
            }else if(type.equals("DATA_SETS")){
                for(DataProvider dataProvider : RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders()){
                    if(dataProvider.getDataSourceContainers() != null) {
                        for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
                            DataProviderUI currentDataProvider = RepoxDataExchangeManager.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
                                    getDataProviderParent(dataSourceContainer.getDataSource().getId()));
                            mainData.add(parseDataSource(dataSourceContainer, currentDataProvider));
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
        Map<String,String> countryMap = Countries.getCountries();

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
            for(DataContainer model : getParsedDataLight(0, dataToAdd.size(), dataToAdd, filterService))
                dataContainer.add(model);
            return dataContainer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private List<DataContainer> getParsedDataLight(int offSet, int limit, List<Object> filterList, FilterServiceImpl filterService) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
//            int extra = 0;
            DataProviderUI currentDataProvider = null;
            for (int i = offSet; i<limit && i<filterList.size(); i++){
                if(filterList.get(i) instanceof DataProvider){
                    currentDataProvider = RepoxDataExchangeManager.parseDataProvider((DataProvider)filterList.get(i));
                    fillDataProvidersDataSets(currentDataProvider, (DataProvider) filterList.get(i));
                    mainData.add(currentDataProvider);
                } else if(filterList.get(i) instanceof DataSourceContainer){
                    if(currentDataProvider == null || (currentDataProvider != null && !filterService.isCorrectDataProvider(((DataSourceContainer) filterList.get(i)).getDataSource().getId(), currentDataProvider.getId()))){
                        currentDataProvider = RepoxDataExchangeManager.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
                                getDataProviderParent(((DataSourceContainer)filterList.get(i)).getDataSource().getId()));
                        mainData.add(currentDataProvider);
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
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    /*********************************************************
     Save Light Functions
     **********************************************************/

    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize) throws ServerSideException{return null;}
    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException{return null;}
    public SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviders, ModelData aggregatorUI, int pageSize) throws ServerSideException{return null;}

    public SaveDataResponse saveDataSource(boolean update, String type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {return null;}
    public String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
                               String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException{return null;}
    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException{return null;}

    public SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize) throws ServerSideException{
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            DataManager dataManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager();
            for(DataSourceUI dataSourceUI : dataSourceUIs) {
                dataManager.moveDataSource((String)dataProviderUI.get("id"), dataSourceUI.getDataSourceSet());
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

    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{return null;}

    public List<OldTaskUI> getParsedOldTasks() throws ServerSideException{
        try{
            List<OldTaskUI> oldTaskUIs = new ArrayList<OldTaskUI>();

            List<DataProvider> dataProviders = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders();
            for(DataProvider dataProvider : dataProviders){
                for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
                    for(OldTask oldTask: dataSourceContainer.getDataSource().getOldTasksList()) {
                        OldTaskUI oldTaskUI = new OldTaskUI(dataSourceContainer.getDataSource().getId(),oldTask.getId(),oldTask.getLogName(),
                                oldTask.getIngestType(),oldTask.getStatus(),oldTask.getRetries(),
                                oldTask.getRetryMax(),oldTask.getDelay(),oldTask.getDateString(),oldTask.getRecords());
                        oldTaskUIs.add(oldTaskUI);
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
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient().sendEmail(developTeamMail,
                    recipientsEmail, messageTitle, message, attachments, null);
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState sendUserDataEmail(String username, String email, String password) throws ServerSideException {
        try {
            // Tel
            String smtpServer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer();
            if(smtpServer == null || smtpServer.isEmpty()) {
                return ResponseState.ERROR;
            }

            String fromEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String subject = "REPOX User Account Data";
            String[] recipientsEmail = new String[]{email};
            File[] attachments = null;

            String message = "Your user name is " + username
                    + "\nYour REPOX password is " + password
                    + "\nAfter you login into REPOX you can change your password in the Edit Account menu."
                    + "\n\n--------------------------------------------------------------------------------\n"
                    + "This email is sent automatically by REPOX. Do not reply to this message.";

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient().sendEmail(fromEmail, recipientsEmail, subject, message, attachments, null);

            return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public boolean isCorrectAggregator(String dataSetId, String aggregatorId) throws ServerSideException{
        return false;
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

    public List<ModelData> getSearchComboData() throws ServerSideException{
        List<ModelData> searchData = new ArrayList<ModelData>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        try{
            for (Object data : allDataList){
                if(data instanceof DataProvider){
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
                    if(allDataList.get(j) instanceof DataProvider){
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

    public List<ModelData> getAllAggregators() throws ServerSideException{return null;}
    public SaveDataResponse saveAggregator(boolean update, AggregatorUI aggregatorUI, int pageSize) throws ServerSideException{return null;}
    public String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException{return null;}
}
