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
public class TestZ3950WebServices extends TestWebServices {

    @Before
    public void setUp() {}

    @After
    public void tearDown(){}

    @Test
    public void createZ3950DataSetIdFile() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/dataSources/createZ3950IdList?dataProviderId=aTESTr0&id=z3950IdFile&" +
                "description=TEST_DESCRIPTION&nameCode=00124&name=Z3950-IdFile&exportPath=D:/Projectos/repoxdata_new&" +
                "schema=info:lc/xmlns/marcxchange-v1.xsd&namespace=info:lc/xmlns/marcxchange-v1&address=aleph.lbfl.li&port=9909&" +
                "database=LLB_IDS&user=&password=&recordSyntax=usmarc&charset=UTF-8&" +
                "recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&namespaceUri=";
        String xmlPath = "src/test/resources/z3950IdFile.txt";
        doPost(strURL,xmlPath);
    }

    @Test
    public void updateMappingSimple() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/dataSources/updateZ3950IdList?id=z3950IdFile&description=DESCRIPTION_CHANGED";
        String xmlPath = "src/test/resources/z3950IdFile.txt";
        doPost(strURL,xmlPath);
    }

    @Test
    public void updateMappingWithXSL() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/dataSources/updateZ3950IdList?id=z3950IdFile&description=DESCRIPTION_CHANGED";
        String xmlPath = "src/test/resources/z3950IdFile.txt";
        doPost(strURL,xmlPath);
    }
}
