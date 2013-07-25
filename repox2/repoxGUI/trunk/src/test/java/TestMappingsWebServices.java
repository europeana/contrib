import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 05-11-2012
 * Time: 14:30
 */
public class TestMappingsWebServices extends TestWebServices {

    @Before
    public void setUp() {}

    @After
    public void tearDown(){}

    @Test
    public void createMapping() throws IOException {
//        String strURL = "http://localhost:9095/rest/functions/calculateRiskLevel?functionName=Function_X&projectName=SAP_IMPORT";
        String strURL = "http://127.0.0.1:8888/rest/createMapping?id=TEST_MAPPING&description=DESCRIPTION&srcSchemaId=ese&srcSchemaVersion=3.4&destSchemaId=oai_dc&destSchemaVersion=2.0&isXslVersion2=true&xslFilename=testMappingXSL";
        String xmlPath = "src/test/resources/testXslForUpload.xsl";
        doPost(strURL,xmlPath);
    }

    @Test
    public void updateMappingSimple() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/updateMapping?id=TEST_MAPPING&description=DESCRIPTION_CHANGED";
        String xmlPath = "src/test/resources/testXslForUpload.xsl";
        doPost(strURL,xmlPath);
    }

    @Test
    public void updateMappingWithXSL() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/updateMapping?id=TEST_MAPPING&xslFilename=testMappingChanged";
        String xmlPath = "src/test/resources/testXslForUpload.xsl";
        doPost(strURL,xmlPath);
    }

    @Test
    public void removeMapping() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/removeMapping?id=TEST_MAPPING";
        doGet(strURL);
    }
}
