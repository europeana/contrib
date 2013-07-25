/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.*;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("repoxservice")
public interface RepoxService extends RemoteService {

    public BaseModel loadAdminFormInfo() throws ServerSideException;
    public void saveAdminFormInfo(BaseModel results) throws ServerSideException;

    public Map<String,String> getFullCountryList() throws ServerSideException;
    public List<String> getFullCharacterEncodingList() throws ServerSideException;
    public List<TransformationUI> getFullTransformationsList() throws ServerSideException;

    public RepoxStatisticsUI getStatisticsInfo() throws ServerSideException;

    public String saveTransformation(TransformationUI transformationUI,String oldTransId) throws ServerSideException;
    public String deleteTransformation(List<String> transfomationIDs) throws ServerSideException;
    public PagingLoadResult<TransformationUI> getPagedTransformations(PagingLoadConfig config) throws ServerSideException;

    public String getRepoxVersion() throws ServerSideException;
    public String getRepoxProjectType() throws ServerSideException;

    public ExternalServiceResultUI getValidationState(String dataSetID) throws ServerSideException;
}
