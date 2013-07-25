package pt.utl.ist.repox.externalServices;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class ExternalServicesTests {
    private final String DATA_PROVIDER_ID = "DP_OAI";
    private final String DATA_SOURCE_ID = "DS_OAI";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";
    private final String SOURCE_URL = "http://oai.bnf.fr/oai2/OAIHandler";
    private final String SOURCE_SET = "gallica";
    private final String SOURCE_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd";
    private final String SOURCE_NAMESPACE = "http://www.europeana.eu/schemas/ese/";
    private final String SOURCE_METADATA_FORMAT = MetadataFormat.oai_dc.name();
    private final int RECORD_COUNT = 37;

    DataManagerDefault dataManager;
    private DataProvider provider;
    private DataSource dataSourceOai;

    @Before
    public void setUp() {
        try {
            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
            dataManager = (DataManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();
            provider = dataManager.createDataProvider(DATA_PROVIDER_ID, "pt", "DP_description");

            dataSourceOai = dataManager.createDataSourceOai(provider.getId(), DATA_SOURCE_ID, DATA_SOURCE_DESCRIPTION,
                    SOURCE_SCHEMA, SOURCE_NAMESPACE, SOURCE_METADATA_FORMAT, SOURCE_URL, SOURCE_SET, null, null, null);
            dataSourceOai.setStatus(DataSource.StatusDS.OK);

            String testUri = "http://server.bd2.inesc-id.pt/repox2/doSomething";

            ExternalServiceNoMonitor externalRestService = new ExternalServiceNoMonitor("test_service_no_monitor","Test Service No Monitor",testUri,dataSourceOai);
            ServiceParameter serviceParameter = new ServiceParameter("dsId","",true,"","DATA_SET_ID");
//            serviceParameter.setValue("DATA_SET_ID");
            externalRestService.getServiceParameters().add(serviceParameter);
            serviceParameter = new ServiceParameter("status","",true,"","DATA_SET_STATUS");
//            serviceParameter.setValue("DATA_SET_STATUS");
            externalRestService.getServiceParameters().add(serviceParameter);
            dataSourceOai.getExternalRestServices().add(externalRestService);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        }
        catch (AlreadyExistsException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws IOException, DocumentException, ClassNotFoundException, ParseException, ObjectNotFoundException {
        dataManager.deleteDataProvider(provider.getId());
    }

    @Test
    public void testRun() {
        try {
            File logFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath(),"testLog.xml");
            for(ExternalRestService externalRestService : dataSourceOai.getExternalRestServices()){
                if(externalRestService instanceof ExternalServiceNoMonitor)
                    new ExternalNoMonitorServiceThread((ExternalServiceNoMonitor)externalRestService,logFile).start();
            }

//            Thread.sleep(10000);
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
