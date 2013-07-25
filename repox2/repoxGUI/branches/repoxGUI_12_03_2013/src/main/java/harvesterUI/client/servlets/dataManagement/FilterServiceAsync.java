package harvesterUI.client.servlets.dataManagement;

import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;

import java.util.List;

public interface FilterServiceAsync {

    public void getCountries(AsyncCallback<List<FilterAttribute>> callback);
    public void getMetadataFormats(AsyncCallback<List<FilterAttribute>> callback);
    public void getTransformations(AsyncCallback<List<FilterAttribute>> callback);
    public void getDataProviderTypes(AsyncCallback<List<FilterAttribute>> callback);
    public void getIngestType(AsyncCallback<List<FilterAttribute>> callback);
    public void getFilteredData(List<FilterQuery> filterQueries,AsyncCallback<DataContainer> callback);

}
