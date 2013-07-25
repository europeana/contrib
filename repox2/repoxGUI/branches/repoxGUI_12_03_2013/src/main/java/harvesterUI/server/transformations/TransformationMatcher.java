package harvesterUI.server.transformations;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.SimpleDataSetInfo;
import harvesterUI.shared.mdr.MdrDataStatistics;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.metadataSchemas.MetadataSchema;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.util.ConfigSingleton;

import java.io.IOException;
import java.util.*;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 28-05-2012
 * Time: 16:44
 */
public class TransformationMatcher {

    private Map<String,MdrDataStatistics> transformationStatisticsMap;
    private Map<String,MdrDataStatistics> schemaVersionsStatisticsMap;

    public TransformationMatcher() {

    }

    public void processMatches() throws ServerSideException{
        try{
            List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
            List<DataSource> dataSourceList = new ArrayList<DataSource>();
            for(Object object : allDataList){
                if(object instanceof DataSourceContainer){
                    dataSourceList.add(((DataSourceContainer) object).getDataSource());
                }
            }

            Map<String,List<MetadataTransformation>> transformations =
                    RepoxServiceImpl.getRepoxManager().getMetadataTransformationManager().getMetadataTransformations();
            Iterator iterator=transformations.entrySet().iterator();

            while(iterator.hasNext()){
                Map.Entry mapEntry=(Map.Entry)iterator.next();
                for (MetadataTransformation metadataTransformation : (List<MetadataTransformation>)mapEntry.getValue()) {
                    int used = 0;
                    List<SimpleDataSetInfo> simpleDataSetInfoList = new ArrayList<SimpleDataSetInfo>();
                    for(DataSource dataSource : dataSourceList){
                        Map<String,MetadataTransformation> dataSetTransformations = dataSource.getMetadataTransformations();
                        for(MetadataTransformation dataSetTransformation :dataSetTransformations.values()){
                            if(metadataTransformation.getId().equals(dataSetTransformation.getId())){
                                SimpleDataSetInfo simpleDataSetInfo = new SimpleDataSetInfo(dataSource.getId(),dataSource.getDescription());
                                simpleDataSetInfoList.add(simpleDataSetInfo);
                                used++;
                            }
                        }
                    }
                    MdrDataStatistics mdrDataStatistics = new MdrDataStatistics(used,simpleDataSetInfoList);
                    getTransformationStatisticsMap().put(metadataTransformation.getId(), mdrDataStatistics);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void processSchemaStatistics() throws ServerSideException{
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        for(Object object : allDataList){
            if(object instanceof DataSourceContainer){
                dataSourceList.add(((DataSourceContainer) object).getDataSource());
            }
        }

        MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager();

        for (MetadataSchema metadataSchema : metadataSchemaManager.getMetadataSchemas()) {
            for (MetadataSchemaVersion metadataSchemaVersion : metadataSchema.getMetadataSchemaVersions()) {
                int used = 0;
                List<SimpleDataSetInfo> simpleDataSetInfoList = new ArrayList<SimpleDataSetInfo>();
                for(DataSource dataSource : dataSourceList){
                    if(dataSource.getSchema().equals(metadataSchemaVersion.getXsdLink())){
                        SimpleDataSetInfo simpleDataSetInfo = new SimpleDataSetInfo(dataSource.getId(),dataSource.getDescription());
                        simpleDataSetInfoList.add(simpleDataSetInfo);
                        used++;
                    }
                }
                MdrDataStatistics mdrDataStatistics = new MdrDataStatistics(used,simpleDataSetInfoList);
                getSchemaVersionsStatisticsMap().put(metadataSchema.getShortDesignation()+metadataSchemaVersion.getVersion(), mdrDataStatistics);
            }
        }
    }

    public Map<String, MdrDataStatistics> getTransformationStatisticsMap() {
        if(transformationStatisticsMap == null){
            transformationStatisticsMap = new HashMap<String, MdrDataStatistics>();
        }
        return transformationStatisticsMap;
    }

    public Map<String, MdrDataStatistics> getSchemaVersionsStatisticsMap() {
        if(schemaVersionsStatisticsMap == null){
            schemaVersionsStatisticsMap = new HashMap<String, MdrDataStatistics>();
        }
        return schemaVersionsStatisticsMap;
    }
}
