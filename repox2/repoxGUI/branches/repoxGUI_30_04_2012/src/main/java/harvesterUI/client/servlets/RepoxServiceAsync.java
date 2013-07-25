/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.RepoxStatisticsUI;
import harvesterUI.shared.TransformationUI;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;

import java.util.List;
import java.util.Map;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface RepoxServiceAsync {

    public void loadAdminFormInfo(AsyncCallback<BaseModel> callback);
    public void saveAdminFormInfo(BaseModel results, AsyncCallback callback);

    public void getFullCountryList(AsyncCallback<Map<String,String>> callback);
    public void getFullCharacterEncodingList(AsyncCallback<List<String>> callback);
    public void getFullTransformationsList(AsyncCallback<List<TransformationUI>> callback);

    public void getStatisticsInfo(AsyncCallback<RepoxStatisticsUI> callback);

    public void saveTransformation(TransformationUI transformationUI,String oldTransId,AsyncCallback<String> callback);
    public void deleteTransformation(List<String> transfomationIDs,AsyncCallback<String> callback);
    public void getPagedTransformations(PagingLoadConfig config,AsyncCallback<PagingLoadResult<TransformationUI>> callback);

    public void getRepoxVersion(AsyncCallback<String> callback);
    public void getRepoxProjectType(AsyncCallback<String> callback);

    public void getValidationState(String dataSetID,AsyncCallback<ExternalServiceResultUI> callback);
}
