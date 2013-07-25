package pt.utl.ist.repox.externalServices;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class MonitoredExternalServiceTest {
    private final String DATA_PROVIDER_ID = "DP_OAI";
    private final String DATA_SOURCE_ID = "EDP_COCV";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";
    private final String SOURCE_URL = "http://server.bd2.inesc-id.pt/repox2Eudml/OAIHandler";
    private final String SOURCE_SET = "EDP_COCV";
    private final String SOURCE_SCHEMA = "http://jats.nlm.nih.gov/archiving/1.0/xsd/JATS-archivearticle1.xsd";
    private final String SOURCE_NAMESPACE = "http://jats.nlm.nih.gov";
    private final String SOURCE_METADATA_FORMAT = "eudml-article2";

    DataManagerDefault dataManager;
    private DataProvider provider;
    private DataSource dataSourceOai;

    @Before
    public void setUp() {

        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
        dataManager = (DataManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();

        try {
            provider = dataManager.createDataProvider(DATA_PROVIDER_ID, "pt", "DP_description");

            dataSourceOai = dataManager.createDataSourceOai(provider.getId(), DATA_SOURCE_ID, DATA_SOURCE_DESCRIPTION,
                    SOURCE_SCHEMA, SOURCE_NAMESPACE, SOURCE_METADATA_FORMAT, SOURCE_URL, SOURCE_SET, null, null, null);
            dataSourceOai.setStatus(DataSource.StatusDS.OK);
            dataSourceOai.setExternalServicesRunType(ExternalServiceStates.ContainerType.SEQUENTIAL);

            String uri = "http://bd2.inesc-id.pt:8080/eudmlImport/start/byChain";
            String statusUri = "http://bd2.inesc-id.pt:8080/eudmlImport/getStatus";
            String type = "PRE_PROCESS";
            ExternalServiceType serviceType = ExternalServiceType.MONITORED;
            ExternalRestService externalRestService = new ExternalRestService("test_service_monitor","Test Service No Monitor",uri,statusUri,type,serviceType);

            ServiceParameter serviceParameter = new ServiceParameter("cenas","",true,"","NONE");
            serviceParameter.setValue("OAI::http://bd2.inesc-id.pt:8080/repox2Eudml/OAIHandler::eudml-article2::EDP_COCV_external;Xslt::resource:articleToInternalArticle.xsl;NlmWriter::/home/jedmundo/importConf/recordsTemp/EDP_COCV_internal");
            externalRestService.getServiceParameters().add(serviceParameter);
            serviceParameter = new ServiceParameter("dataSetID","",true,"","DATA_SET_ID");
            serviceParameter.setValue("EDP_COCV");
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
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startIngestDataSource(DATA_SOURCE_ID, true);
            while(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getOnetimeTasks().size() > 0){
                Thread.sleep(7000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
