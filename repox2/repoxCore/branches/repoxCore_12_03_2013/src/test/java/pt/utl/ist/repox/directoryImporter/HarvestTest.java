package pt.utl.ist.repox.directoryImporter;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.marc.iso2709.IteratorIso2709;
import pt.utl.ist.repox.accessPoint.AccessPointsManagerDefault;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.DataSourceContainerDefault;
import pt.utl.ist.repox.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.dataProvider.dataSource.SimpleFileExtract;
import pt.utl.ist.repox.marc.*;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HarvestTest {
    private final String DATA_PROVIDER_ID = "DP_DIMPORTER";
    private final String DATA_SOURCE_ID = "DS_DIMPORTER";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";
    private final String SOURCE_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd";
    private final String SOURCE_NAMESPACE = "http://www.europeana.eu/schemas/ese/";
    private final String SOURCE_METADATA_FORMAT = "ese";//ISO2709
    private final String SOURCE_RECORDXPATH = "record";
    private final String SOURCE_PATH= "D:\\Projectos\\repoxCore\\src\\test\\java\\pt\\utl\\ist\\repox\\directoryImporter\\Folder";
    private final int    RECORD_COUNT = 689;//o outro Folder é 689

    private DataSourceDirectoryImporter dataSourceDirectoryImporter;

    @Before
    public void setUp() {
        try {
            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
            DataManagerDefault dataManager = (DataManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();

            HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();
            DataProvider newDataProvider = new DataProvider(DATA_PROVIDER_ID, "data provider", "pt", "it's new", dataSourceContainers);

            FileExtractStrategy extractStrategy = new SimpleFileExtract();
           /* String extractStrategyString = currentDataSourceElement.elementText("fileExtract");
            //FileExtractStrategy extractStrategy = null;

            if(extractStrategyString.equals(Iso2709FileExtract.class.getSimpleName())) {
                //characterEncoding = CharacterEncoding.get(currentDataSourceElement.attributeValue("characterEncoding"));
                String isoImplementationClass = currentDataSourceElement.attributeValue("isoImplementationClass");
                extractStrategy = new Iso2709FileExtract(isoImplementationClass);
            } else if(extractStrategyString.equals(MarcXchangeFileExtract.class.getSimpleName())) {
                extractStrategy = new MarcXchangeFileExtract();
            } else if(extractStrategyString.equals(SimpleFileExtract.class.getSimpleName())) {
                extractStrategy = new SimpleFileExtract();*/

           /* if(extractStrategy instanceof Iso2709FileExtract) {
                //new SimpleFileExtract();
              //  new Iso2709FileExtract("IteratorIso2709");
              Iso2709FileExtract extract = new Iso2709FileExtract(IteratorIso2709.class.getName());
                extract = new Iso2709FileExtract("IteratorIso2709");
            }*/
            //String recordXPath = "record";
           

            Map<String, String> namespaces = null;
            namespaces = new TreeMap<String, String>();

            dataSourceDirectoryImporter = new DataSourceDirectoryImporter(
                    newDataProvider,
                    DATA_SOURCE_ID,
                    DATA_SOURCE_DESCRIPTION,
                    SOURCE_SCHEMA,
                    SOURCE_NAMESPACE,
                    SOURCE_METADATA_FORMAT,
                    new SimpleFileExtract(),
                    //new Iso2709FileExtract("IteratorIso2709"),
                    new DataSourceFolder(),
                    null,
                    SOURCE_PATH,
                    new IdGenerated(),
                    new TreeMap<String, MetadataTransformation>(),
                    SOURCE_RECORDXPATH,
                    namespaces);

            dataSourceContainers.put(dataSourceDirectoryImporter.getId(), new DataSourceContainerDefault(dataSourceDirectoryImporter));
            dataManager.addDataProvider(newDataProvider);

            ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager().initialize(dataSourceContainers);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, ObjectNotFoundException {
        ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager().deleteDataProvider(DATA_PROVIDER_ID);
    }

    @Test
    public void testRun() {
        try {
            File logFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getTempDir() + "/log.txt");
            dataSourceDirectoryImporter.ingestRecords(logFile, true);
            AccessPointsManagerDefault accessPointsManager = (AccessPointsManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager();
            int[] recordCountLastrowPair = accessPointsManager.getRecordCountLastrowPair(dataSourceDirectoryImporter, null, null, null);
            int recordCount = recordCountLastrowPair[0];
            Assert.assertEquals(RECORD_COUNT, recordCount);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }


    }
/*
	@Test
	public void testRunFromFile() {
		Assert.assertTrue(false);
	}
*/
}
