//package harvesterUI.server.projects.EuDML;
//
//import harvesterUI.client.panels.forms.dataProviders.DataProviderEuDMLForm;
//import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
//import harvesterUI.server.projects.EuDMLAndLightManager;
//import harvesterUI.server.util.Util;
//import harvesterUI.shared.ServerSideException;
//import harvesterUI.shared.dataTypes.DataProviderUI;
//import harvesterUI.shared.dataTypes.DataSourceUI;
//import harvesterUI.shared.dataTypes.SaveDataResponse;
//import pt.utl.ist.repox.RepoxManagerEuDml;
//import pt.utl.ist.repox.dataProvider.Countries;
//import pt.utl.ist.repox.dataProvider.DataSource;
//import pt.utl.ist.repox.dataProvider.DataSourceContainer;
//import pt.utl.ist.repox.util.ConfigSingleton;
//import pt.utl.ist.repox.util.RepoxContextUtilEuDml;
//
//import java.util.List;
//
///**
// * Created to Project REPOX
// * User: Edmundo
// * Date: 30-04-2012
// * Time: 11:29
// */
//public class EuDMLManager extends EuDMLAndLightManager {
//
//    public EuDMLManager() {
//        super();
//        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuDml());
//    }
//
//    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize) throws ServerSideException {
//        try{
//            return EuDMLSaveData.saveDataProvider(update, dataProviderUI, pageSize);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException{
//        try{
//            return EuDMLSaveData.deleteDataProviders(dataProviderUIs);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public SaveDataResponse saveDataSource(boolean update, String type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
//        try{
//            return EuDMLSaveData.saveDataSource(update, type, originalDSset, dataSourceUI,pageSize);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
//                               String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException{
//        try{
//            // Check http URLs
//            String checkUrlResult = DataSetOperationsServiceImpl.checkURL(url);
//            if(checkUrlResult.equals("URL_MALFORMED"))
//                return "URL_MALFORMED";
//            else if(checkUrlResult.equals("URL_NOT_EXISTS"))
//                return "URL_NOT_EXISTS";
//
//            EuDMLSaveData.addAllOAIURL(url.trim(), dataProviderID, dsSchema, dsNamespace, dsMTDFormat, dataSetOperationsService.checkOAIURL(url.trim()));
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//        return "SUCCESS";
//    }
//
//
//    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException{
//        try{
//            return EuDMLSaveData.deleteDataSources(dataSourceUIs);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{
//        try {
//            RepoxManagerEuDml repoxManagerEuDml = (RepoxManagerEuDml)ConfigSingleton.getRepoxContextUtil().getRepoxManager();
//            DataSourceContainer dataSourceContainer = repoxManagerEuDml.getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet());
//
//            DataSource dataSource = dataSourceContainer.getDataSource();
//            dataSource.setExportDir(dataSourceUI.getExportDirectory());
//
//            String recordsPerFile;
//            if(dataSourceUI.getRecordsPerFile().equals("All"))
//                recordsPerFile = "-1";
//            else
//                recordsPerFile = dataSourceUI.getRecordsPerFile();
//
//            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
//                    startExportDataSource(dataSourceUI.getDataSourceSet(), recordsPerFile, dataSourceUI.getExportFormat());
//        } catch (Exception e) {
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//        return true;
//    }
//
////    public static DataProviderUI parseDataProviderEuDML(DataProvider dataProvider)  throws ServerSideException{
////        String country;
////        if(dataProvider.getCountry() != null)
////            country = dataProvider.getCountry();
////        else
////            country = "none";
////
////        DataProviderUI newDataProviderUI = new DataProviderUI(dataProvider.getId(),dataProvider.getName(),
////                country, (country != null && !country.equals("")) ? Countries.getCountries().get(country) : "");
////        newDataProviderUI.setDescription(dataProvider.getDescription());
////        return newDataProviderUI;
////    }
//}
