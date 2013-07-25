//package harvesterUI.server.dataManagement;
//
//import harvesterUI.server.ProjectType;
//import harvesterUI.server.RepoxServiceImpl;
//import harvesterUI.server.util.Util;
//import harvesterUI.shared.dataTypes.AggregatorUI;
//import harvesterUI.shared.dataTypes.DataContainer;
//import harvesterUI.shared.dataTypes.DataProviderUI;
//import harvesterUI.shared.ServerSideException;
//import pt.utl.ist.repox.dataProvider.AggregatorEuropeana;
//import pt.utl.ist.repox.dataProvider.DataManagerEuropeana;
//import pt.utl.ist.repox.dataProvider.DataProvider;
//import pt.utl.ist.repox.dataProvider.DataSourceContainer;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created to REPOX Project.
// * User: Edmundo
// * Date: 29-03-2012
// * Time: 17:25
// */
//public class ViewResultsManager {
//
//    private int showSize;
//
//    public ViewResultsManager(int showSize) {
//        this.showSize = showSize;
//    }
//
//    public List<DataContainer> getViewResult(int offset, int limit, String type) throws ServerSideException{
//        List<DataContainer> mainData = new ArrayList<DataContainer>();
//        try{
//            if(type.equals("AGGREAGATORS")){
//                DataManagerEuropeana dataManagerEuropeana = (DataManagerEuropeana) RepoxServiceImpl.getRepoxManager().getDataManager();
//                List<AggregatorEuropeana> aggregatorEuropeanaList = dataManagerEuropeana.getAggregatorsEuropeana();
//                for (int i = offset; i<limit && i<aggregatorEuropeanaList.size(); i++){
//                    mainData.add(DataManagementServiceImpl.parseAggregatorEuropeana(aggregatorEuropeanaList.get(i)));
//                }
//                showSize = aggregatorEuropeanaList.size();
//            }else if(type.equals("DATA_PROVIDERS")){
//                List<DataProvider> dpList = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders();
//                for (int i = offset; i<limit && i<dpList.size(); i++){
//                    if(RepoxServiceImpl.getProjectType() == ProjectType.EUROPEANA){
//                        AggregatorUI currentAggregator = DataManagementServiceImpl.parseAggregatorEuropeana(((DataManagerEuropeana)RepoxServiceImpl.
//                                getRepoxManager().getDataManager()).getAggregatorParent(dpList.get(i).getId()));
//                        mainData.add(DataManagementServiceImpl.parseDataProviderEuropeana(dpList.get(i),currentAggregator));
//                    } else
//                        mainData.add(DataManagementServiceImpl.parseDataProvider(dpList.get(i)));
//                }
//                showSize = dpList.size();
//            }else if(type.equals("DATA_SETS")){
//                if(RepoxServiceImpl.getProjectType() == ProjectType.EUROPEANA){
//                    DataManagerEuropeana dataManagerEuropeana = (DataManagerEuropeana) RepoxServiceImpl.getRepoxManager().getDataManager();
//                    List<AggregatorEuropeana> aggregatorEuropeanaList = dataManagerEuropeana.getAggregatorsEuropeana();
//                    for(AggregatorEuropeana aggregatorEuropeana : aggregatorEuropeanaList){
//                        for(DataProvider dataProvider : aggregatorEuropeana.getDataProviders()){
//                            if(dataProvider.getDataSourceContainers() != null) {
//                                for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
//                                    DataProviderUI currentDataProvider = DataManagementServiceImpl.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
//                                            getDataProviderParent(dataSourceContainer.getDataSource().getId()));
//                                    mainData.add(DataManagementServiceImpl.parseDataSource(dataSourceContainer,currentDataProvider));
//                                }
//                            }
//                        }
//                    }
//                }else{
//                    for(DataProvider dataProvider : RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders()){
//                        if(dataProvider.getDataSourceContainers() != null) {
//                            for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
//                                DataProviderUI currentDataProvider = DataManagementServiceImpl.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
//                                        getDataProviderParent(dataSourceContainer.getDataSource().getId()));
//                                mainData.add(DataManagementServiceImpl.parseDataSource(dataSourceContainer,currentDataProvider));
//                            }
//                        }
//                    }
//                }
//            }
//            return mainData;
//        }catch (Exception e) {
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public int getShowSize() {
//        return showSize;
//    }
//}
