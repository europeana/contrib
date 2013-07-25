/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.filters.FilterQuery;

import java.util.List;

@RemoteServiceRelativePath("filterService")
public interface FilterService extends RemoteService {

    public List<FilterAttribute> getCountries() throws ServerSideException;
    public List<FilterAttribute> getMetadataFormats()throws ServerSideException;
    public List<FilterAttribute> getTransformations()throws ServerSideException;
    public List<FilterAttribute> getDataProviderTypes() throws ServerSideException;
    public List<FilterAttribute> getIngestType()throws ServerSideException;
    public DataContainer getFilteredData(List<FilterQuery> filterQueries)throws ServerSideException;
}
