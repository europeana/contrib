package harvesterUI.server.dataManagement;

import com.extjs.gxt.ui.client.util.DateWrapper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.dataManagement.FilterService;
import harvesterUI.server.ProjectType;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.*;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.DataSourceUI;
import harvesterUI.shared.filters.*;
import pt.utl.ist.repox.dataProvider.*;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;

import java.util.*;

public class FilterServiceImpl extends RemoteServiceServlet implements FilterService {

    public FilterServiceImpl() {

    }

    public List<FilterAttribute> getDataProviderTypes() throws ServerSideException {
        return removeDuplicates(getDPAttributes(FilterType.DP_TYPE));
    }

    public List<FilterAttribute> getCountries() throws ServerSideException {
        return removeDuplicates(getDPAttributes(FilterType.COUNTRY));
    }

    public List<FilterAttribute> getMetadataFormats()throws ServerSideException{
        return removeDuplicates(getDSAttributes(FilterType.METADATA_FORMAT));
    }

    public List<FilterAttribute> getTransformations()throws ServerSideException{
        return removeDuplicates(getDSAttributes(FilterType.TRANSFORMATION));
    }

    public List<FilterAttribute> getIngestType()throws ServerSideException{
        return removeDuplicates(getDSAttributes(FilterType.INGEST_TYPE));
    }

    private List<FilterAttribute> getDPAttributes(FilterType filterType) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().getDPAttributes(filterType);
    }

    private List<FilterAttribute> getDSAttributes(FilterType filterType) throws ServerSideException {
        List<FilterAttribute> values = new ArrayList<FilterAttribute>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        for(Object object : allDataList){
            if(object instanceof DataSourceContainer){
                DataSourceContainer dataSourceContainer = (DataSourceContainer)object;
                DataSource dataSource = dataSourceContainer.getDataSource();
                if(filterType.equals(FilterType.METADATA_FORMAT)){
                    values.add(new FilterAttribute(dataSource.getMetadataFormat(),dataSource.getMetadataFormat()));
                }else if(filterType.equals(FilterType.TRANSFORMATION)){
                    for(MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values())
                        values.add(new FilterAttribute(metadataTransformation.getId(),metadataTransformation.getId()));
                }else if(filterType.equals(FilterType.INGEST_TYPE)){
//                        dataSource.get
//                        values.add(new FilterAttribute(metadataTransformation.getId(),metadataTransformation.getId()));
                }
            }
        }
        return values;
    }

    public DataContainer getFilteredData(List<FilterQuery> filterQueries)throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().getFilteredData(filterQueries,this);
    }

    public void compareDataSetValues(DataSourceContainer dataSourceContainer, FilterQuery filterQuery, List<Object> dataToAdd) throws ServerSideException{
        try{
            DataSource dataSource = dataSourceContainer.getDataSource();
            if(compareRecords(dataSource.getIntNumberRecords(), filterQuery))
                addDataObjectToList(dataToAdd,dataSourceContainer);
            if(dataSource.getOldTasksList().size() > 0 && compareLastIngest(dataSource.getOldTasksList().get(0).getActualDate(), filterQuery))
                addDataObjectToList(dataToAdd,dataSourceContainer);
            if(compareMetadataFormat(dataSource.getMetadataFormat(), filterQuery))
                addDataObjectToList(dataToAdd,dataSourceContainer);
            if(compareTransformation(dataSource, filterQuery))
                addDataObjectToList(dataToAdd,dataSourceContainer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public boolean compareCountry(String countryToCompare, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.COUNTRY){
            for(String countryValue : filterQuery.getValues()){
                if(countryValue.equals(countryToCompare))
                    return true;
            }
            return false;
        }else
            return false;
    }

    public boolean compareDPType(String typeToCompare, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.DP_TYPE){
            for(String typeValue : filterQuery.getValues()){
                if(typeValue.equals(typeToCompare))
                    return true;
            }
            return false;
        }else
            return false;
    }

    public boolean compareRecords(int recordsToCompare, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.RECORDS){
            FilterQueryRecords filterQueryRecords = (FilterQueryRecords) filterQuery;
            int beginRecords = filterQueryRecords.getBeginRecords();
            int endRecords = filterQueryRecords.getEndRecords();
            int onRecords = filterQueryRecords.getOnRecords();

            if(onRecords != -1)
                return onRecords == recordsToCompare;
            else if(beginRecords != -1 && endRecords != -1)
                return recordsToCompare >= beginRecords && recordsToCompare <= endRecords;
            else if(beginRecords != -1)
                return recordsToCompare >= beginRecords;
            else if(endRecords != -1)
                return recordsToCompare <= endRecords;


            return false;
        }else
            return false;
    }

    public boolean compareLastIngest(Date lastIngestDate, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.LAST_INGEST){
            FilterQueryLastIngest query = (FilterQueryLastIngest) filterQuery;
            boolean result = false;

            if(query.getOnDate() != null){
                lastIngestDate.setHours(0);
                lastIngestDate.setMinutes(0);
                lastIngestDate.setSeconds(0);
                result = query.getOnDate().equals(lastIngestDate);
            } else if(query.getBeginDate() != null && query.getEndDate() != null)
                result = lastIngestDate.after(query.getBeginDate()) && lastIngestDate.before(query.getEndDate());
            else if(query.getBeginDate() != null)
                result = lastIngestDate.after(query.getBeginDate());
            else if(query.getEndDate() != null)
                result = lastIngestDate.before(query.getEndDate());


            if(result || (query.getBeginDate() == null && query.getEndDate() == null && query.getOnDate() == null)){
                if(query.getBeginTime() != null && query.getEndTime() != null) {
                    boolean result1;
                    boolean result2;
                    result1 = compareTime(query.getBeginTime(),lastIngestDate,1);
                    result2 = compareTime(query.getEndTime(),lastIngestDate,2);
                    result = result1&&result2;
                } else {
                    if(query.getBeginTime() != null) {
                        result = compareTime(query.getBeginTime(),lastIngestDate,1);
                    }
                    else if(query.getEndTime() != null) {
                        result = compareTime(query.getEndTime(),lastIngestDate,2);
                    }
                }
            }

            return result;
        }else
            return false;
    }

    public boolean compareTime(Date date1, Date date2, int type)  {
        DateWrapper dw1 = new DateWrapper(date1);
        int hrs = dw1.getHours();
        int min = dw1.getMinutes();
        int sec = dw1.getSeconds();

        DateWrapper dw2 = new DateWrapper(date2);
        int hrs2 = dw2.getHours();
        int min2 = dw2.getMinutes();
        int sec2 = dw2.getSeconds();

        return compareRawTime(hrs,hrs2,min,min2,sec,sec2,type);
    }

    public boolean compareRawTime(int hrs,int hrs2,int min,int min2, int sec, int sec2,int type){
        if(type == 1)
            return hrs2 > hrs || hrs == hrs2 && min2 >= min || hrs == hrs2 && min == min2 && sec >= sec2;
        else if(type == 2)
            return hrs2 < hrs || hrs == hrs2 && min2 <= min || hrs == hrs2 && min == min2 && sec <= sec2;
        return true;
    }

    public boolean compareMetadataFormat(String metadataFormat, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.METADATA_FORMAT){
            for(String metadataFormatValue : filterQuery.getValues()){
                if(metadataFormatValue.equals(metadataFormat))
                    return true;
            }
            return false;
        }else
            return false;
    }

    public boolean compareTransformation(DataSource dataSource, FilterQuery filterQuery){
        if(filterQuery.getFilterType() == FilterType.TRANSFORMATION){
            for(String transformationValue : filterQuery.getValues()){
                for(MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values()){
                    if(transformationValue.equals(metadataTransformation.getId()))
                        return true;
                }
            }
            return false;
        }else
            return false;
    }

    public void addDataObjectToList(List<Object> finalList, Object dataObject){
        if(!finalList.contains(dataObject))
            finalList.add(dataObject);
    }

    // Remove duplicates of a list of FilterAttributes compared with their value
    public List<FilterAttribute> removeDuplicates(List<FilterAttribute> list) {
        List<FilterAttribute> noDuplicates = new ArrayList<FilterAttribute>();
        boolean doAdd = true;
        for (int i = 0; i < list.size(); i++) {
            FilterAttribute testValue = list.get(i);
            // Check if the tree node has the attribute
            if(list.get(i).getValue() != null && testValue.getValue() != null
                    && !list.get(i).getValue().equals("")) {
                for (int j = 0; j < list.size(); j++) {
                    // Check if the tree node has the attribute
                    if(list.get(j).getValue() != null && testValue.getValue() != null
                            && !list.get(j).getValue().equals("")) {
                        if (i == j)
                            break;
                        else if (list.get(j).getValue().equals(testValue.getValue())) {
                            doAdd = false;
                            break;
                        }
                    }
                }
                if (doAdd)
                    noDuplicates.add(testValue);
                else
                    doAdd = true;
            }
        }
        return noDuplicates;
    }

    public boolean isCorrectDataProvider(String dataSetId, String dataProviderId) throws ServerSideException{
        return RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(dataSetId).getId().equals(dataProviderId);
    }

    public boolean isCorrectAggregator(String dataSetId, String aggregatorId) throws ServerSideException{
        return RepoxServiceImpl.getProjectManager().isCorrectAggregator(dataSetId, aggregatorId);
    }

    public boolean isNextDataProviderSame(String currentDataSetId, String nextDataSetId) throws ServerSideException{
        String currentDPId = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(currentDataSetId).getId();
        String nextDPId = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(nextDataSetId).getId();
        return currentDPId.equals(nextDPId);
    }
}
