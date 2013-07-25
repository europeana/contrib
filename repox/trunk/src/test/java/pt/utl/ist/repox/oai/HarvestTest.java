package pt.utl.ist.repox.oai;

import eu.europeana.definitions.domain.Country;
import eu.europeana.definitions.domain.Language;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.models.ProviderType;
import eu.europeana.repox2sip.models.Request;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.data.DataManager;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.IdGenerated;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class HarvestTest {
	private final String DATA_PROVIDER_ID = "DP_OAI";
	private final String DATA_SOURCE_ID = "DS_OAI";
    private String aggregator;

//	private String sourceUrl = "http://digar.nlib.ee/otsing/oai.jsp";
//	private String sourceSet = "objects";
	private final String SOURCE_URL = "http://www.asdlib.org/oai/oai.php";
	private final String SOURCE_SET = "Reference";
	private final int RECORD_COUNT = 8;

	private DataSourceOai dataSourceOai;

    private final String OUTPUT_CONFIGURATION_FILE_NAME = RepoxContextUtil.DATA_PROVIDERS_FILENAME;

	private File configurationFile;

	@Before
	public void setUp() {
        try {
            RepoxContextUtil.useRepoxManagerTest();
			String xmlBasedir = RepoxContextUtil.getRepoxManager().getConfiguration().getXmlConfigPath();
            configurationFile = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME);

            
            TaskManager taskManager = RepoxContextUtil.getRepoxManager().getTaskManager();
            taskManager.stop();
			DataManager dataManager = RepoxContextUtil.getRepoxManager().getDataManager();

            aggregator = AggregatorRepox.generateId("agg1");
            AggregatorRepox aggregatorRepox = new AggregatorRepox(aggregator, "agg1", "dummy", new URL("http://www.google.com"), null, null);
            dataManager.saveAggregator(aggregatorRepox);

            ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
            DataProvider newDataProvider = new DataProvider(aggregatorRepox, DATA_PROVIDER_ID, "data provider", "it's new", dataSources, "name_code", ProviderType.MUSEUM, Country.PORTUGAL, new URL("http://www.sapo.pt"), -1);
            dataManager.saveDataProvider(newDataProvider);
            newDataProvider.setAggregatorIdDb(aggregatorRepox.getIdDb());

            dataSourceOai = new DataSourceOai(newDataProvider, DATA_SOURCE_ID, "test", MetadataFormat.oai_dc.toString(),
					SOURCE_URL, SOURCE_SET, new IdGenerated(), null, "nameDummy", "nameCodeDummy", -1, null);

            dataManager.saveDataSource(dataSourceOai);

			RepoxContextUtil.getRepoxManager().getAccessPointsManager().initialize(dataSources);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws IOException, DocumentException, Repox2SipException {
		RepoxContextUtil.getRepoxManager().getDataManager().deleteAggregator(aggregator);
        
        if(configurationFile.exists()) {
			configurationFile.delete();
		}
        TaskManager taskManager = RepoxContextUtil.getRepoxManager().getTaskManager();
            taskManager.stop();
	}

	@Test
	public void testRun() {
		try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/test/resources/Test-configuration.properties"));
			File logFile = new File(properties.getProperty("repox.dir")+"/log.txt");
			dataSourceOai.ingestRecords(new Request(), logFile, true);
			AccessPointsManager accessPointsManager = RepoxContextUtil.getRepoxManager().getAccessPointsManager();
			int[] recordCountLastrowPair = accessPointsManager.getRecordCountLastrowPair(dataSourceOai, null, null, null);
			int recordCount = recordCountLastrowPair[0];
			Assert.assertEquals(RECORD_COUNT, recordCount);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
        Assert.assertEquals(1,1);
	}

	

}
