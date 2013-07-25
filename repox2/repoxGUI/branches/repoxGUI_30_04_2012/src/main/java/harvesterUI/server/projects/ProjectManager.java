package harvesterUI.server.projects;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import harvesterUI.server.dataManagement.FilterServiceImpl;
import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
import harvesterUI.shared.RepoxStatisticsUI;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.*;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterType;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.tasks.OldTaskUI;

import java.util.List;
import java.util.Map;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 30-04-2012
 * Time: 11:31
 */
public abstract class ProjectManager {

    public abstract RepoxStatisticsUI getStatisticsInfo() throws ServerSideException;
    public abstract Map<String,String> getFullCountryList() throws ServerSideException;
    public abstract BaseModel loadAdminFormInfo() throws ServerSideException;
    public abstract void saveAdminFormInfo(BaseModel results) throws ServerSideException;

    public abstract List<DataContainer> getParsedData(int offSet, int limit) throws ServerSideException;
    public abstract DataContainer getSearchResult(ModelData data) throws ServerSideException;

    public abstract List<DataContainer> getViewResult(int offset, int limit, String type) throws ServerSideException;

    public abstract List<FilterAttribute> getDPAttributes(FilterType filterType) throws ServerSideException;

    public abstract DataContainer getFilteredData(List<FilterQuery> filterQueries, FilterServiceImpl filterService)throws ServerSideException;

    /*********************************************************
     Save Functions
     **********************************************************/

    public abstract SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize) throws ServerSideException;
    public abstract String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException;


    public abstract SaveDataResponse saveDataSource(boolean update, String type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException;
    public abstract String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
                               String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException;

    public abstract SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize) throws ServerSideException;
    public abstract String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException;

    public abstract Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException;
    public abstract List<OldTaskUI> getParsedOldTasks() throws ServerSideException;

    public abstract String sendFeedbackEmail(String userEmail, String title, String message, String messageType) throws ServerSideException;
    public abstract ResponseState sendUserDataEmail(String username, String email, String password) throws ServerSideException;

    public abstract DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException;

    public abstract List<ModelData> getSearchComboData() throws ServerSideException;

    public abstract int getDataPage(String id, int pageSize);
    /*********************************************************
     Europeana Only Functions
     **********************************************************/

    public abstract SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviders, ModelData aggregatorUI, int pageSize) throws ServerSideException;
    public abstract boolean isCorrectAggregator(String dataSetId, String aggregatorId) throws ServerSideException;
    public abstract List<ModelData> getAllAggregators() throws ServerSideException;
    public abstract SaveDataResponse saveAggregator(boolean update, AggregatorUI aggregatorUI, int pageSize) throws ServerSideException;
    public abstract String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException;

    protected ModelData createModel(String id,String name) {
        ModelData m = new BaseModelData();
        m.set("id", id);
        m.set("name", name);
        return m;
    }
}
