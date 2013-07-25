package pt.utl.ist.repox.oai;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.dataProvider.DataManagerEuropeana;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.DataSourceContainerEuropeana;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilEuropeana;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class HarvestTest {
    private final String DATA_PROVIDER_ID = "DP_OAI";
    private final String DATA_SOURCE_ID = "DS_OAI";

    private final String SOURCE_URL = "http://bd1.inesc-id.pt:8080/repoxel/OAIHandler";
    private final String SOURCE_SET = "bmfinancas";
    private final String SOURCE_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd";
    private final String SOURCE_NAMESPACE = "http://www.europeana.eu/schemas/ese/";
    private final String SOURCE_METADATA_FORMAT = "ese";
    private final int RECORD_COUNT = 36;

    private DataSourceOai dataSourceOai;

    @Before
    public void setUp() {
        try {
            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuropeana());
            DataManagerEuropeana dataManager = (DataManagerEuropeana)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();

            HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();
            DataProvider newDataProvider = new DataProvider(DATA_PROVIDER_ID, "data provider", "pt", "it's new", dataSourceContainers);

            dataSourceOai = new DataSourceOai(
                    newDataProvider,
                    DATA_SOURCE_ID,
                    "description...",
                    SOURCE_SCHEMA,
                    SOURCE_NAMESPACE,
                    SOURCE_METADATA_FORMAT,
                    SOURCE_URL,
                    SOURCE_SET,
                    new IdGenerated(),
                    null);

            dataSourceContainers.put(dataSourceOai.getId(), new DataSourceContainerEuropeana(dataSourceOai, "nameCode", "name", "exportPath"));
            dataManager.saveData();

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
            dataSourceOai.ingestRecords(logFile, true);
            AccessPointsManager accessPointsManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager();
            int[] recordCountLastrowPair = accessPointsManager.getRecordCountLastrowPair(dataSourceOai, null, null, null);
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
