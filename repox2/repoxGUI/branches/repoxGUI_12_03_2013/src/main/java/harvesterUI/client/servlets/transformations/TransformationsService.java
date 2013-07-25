/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.transformations;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

@RemoteServiceRelativePath("transformationsService")
public interface TransformationsService extends RemoteService {

    public List<TransformationUI> getFullTransformationsList() throws ServerSideException;
    public String saveTransformation(TransformationUI transformationUI, String oldTransId) throws ServerSideException;
    public String deleteTransformation(List<String> transfomationIDs) throws ServerSideException;
    public PagingLoadResult<TransformationUI> getPagedTransformations(PagingLoadConfig config) throws ServerSideException;

    public List<TransformationUI> getMdrMappings(String schema, String metadataNamespace) throws ServerSideException;

    public List<TransformationUI> getAllMdrMappings() throws ServerSideException;

    public BaseModel importMdrTransformation(List<TransformationUI> transformationUIs) throws ServerSideException;
    public BaseModel updateMdrTransformations(List<TransformationUI> transformationUIs) throws ServerSideException;

    public PagingLoadResult<SchemaUI> getPagedSchemas(PagingLoadConfig config) throws ServerSideException;
    public List<SchemaTreeUI> getSchemasTree() throws ServerSideException;
    public ResponseState deleteMetadataSchema(List<String> schemaIds) throws ServerSideException;
    public ResponseState saveMetadataSchema(SchemaUI schemaUI,String oldSchemaUIId) throws ServerSideException;
    public List<SchemaUI> getAllMetadataSchemas() throws ServerSideException;
}
