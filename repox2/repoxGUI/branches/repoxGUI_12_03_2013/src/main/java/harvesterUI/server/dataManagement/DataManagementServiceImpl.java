package harvesterUI.server.dataManagement;

import com.extjs.gxt.ui.client.data.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.dataManagement.DataManagementService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataSourceUI;
import pt.utl.ist.repox.dataProvider.DataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManagementServiceImpl extends RemoteServiceServlet implements DataManagementService {

//    private ViewResultsManager viewResultsManager;

    public DataManagementServiceImpl() {
        try{
            int showSize = RepoxServiceImpl.getRepoxManager().getDataManager().getShowSize();
//            viewResultsManager = new ViewResultsManager(showSize);
        } catch (ServerSideException e) {
            e.printStackTrace();
        }
    }

    public DataContainer getMainData(final PagingLoadConfig config) throws ServerSideException {
//        if (config.getSortInfo().getSortField() != null) {
//            final String sortField = config.getSortInfo().getSortField();
//            if (sortField != null) {
//                Collections.sort(mainData, config.getSortInfo().getSortDir().comparator(new Comparator<DataProviderUI>() {
//                    public int compare(DataProviderUI p1, DataProviderUI p2) {
//                        if (sortField.equals("name")) {
//                            return p1.getName().compareTo(p2.getName());
//                        }
//                        // todo: sort when table header clicked
////                        else if (sortField.equals("username")) {
////                            return p1.getUsername().compareTo(p2.getUsername());
////                        } else if (sortField.equals("subject")) {
////                            return p1.getSubject().compareTo(p2.getSubject());
////                        } else if (sortField.equals("date")) {
////                            return p1.getDate().compareTo(p2.getDate());
////                        }
//                        return 0;
//                    }
//                }));
//            }
//        }
        List<DataContainer> parsedData;
        if(config.get("VIEW_TYPE") != null){
            parsedData = RepoxServiceImpl.getProjectManager().getViewResult(config.getOffset(), config.getLimit(), (String) config.get("VIEW_TYPE"));
        }else{
            parsedData = RepoxServiceImpl.getProjectManager().getParsedData(config.getOffset(), config.getLimit());
        }

        DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
        for(DataContainer model : parsedData)
            dataContainer.add(model);
        return dataContainer;
    }

    public PagingLoadResult<DataContainer> getPagingData(PagingLoadConfig config) throws ServerSideException {
        try{
            int showSize = RepoxServiceImpl.getRepoxManager().getDataManager().getShowSize();
            return new BasePagingLoadResult<DataContainer>(null, config.getOffset(), showSize);
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getDataSetInfo(dataSetId);
    }

    public List<ModelData> getAllDataProviders() throws ServerSideException{
        List<ModelData> dataProviderIds = new ArrayList<ModelData>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        try{
            for (Object data : allDataList){
                if(data instanceof DataProvider){
                    dataProviderIds.add(createModel(((DataProvider) data).getId(),((DataProvider) data).getName()));
                }
            }
            return dataProviderIds;
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<ModelData> getAllAggregators() throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getAllAggregators();
    }

    public List<ModelData> getSearchComboData() throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getSearchComboData();
    }

    public DataContainer getSearchResult(ModelData data) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getSearchResult(data);
    }

    private ModelData createModel(String id,String name) {
        ModelData m = new BaseModelData();
        m.set("id", id);
        m.set("name", name);
        return m;
    }
}
