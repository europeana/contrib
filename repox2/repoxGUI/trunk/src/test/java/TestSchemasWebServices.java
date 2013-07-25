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
public class TestSchemasWebServices extends TestWebServices {

    @Before
    public void setUp() {}

    @After
    public void tearDown(){}

    @Test
    public void createSchemaWithOneVersion() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/createSchema?shortDesignation=TEST_SCHEMA&namespace=http://eudml.org/schema/1.1/eudml-book" +
                "&designation=EUDML_BOOK&description=&notes=&oaiAvailable=true" +
                "&versions=1.1--http://eudml.org/schema/1.1/eudml-book-1.1.xsd";
        doGet(strURL);
    }

    @Test
    public void createSchemaMultipleVersions() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/createSchema?shortDesignation=TEST_SCHEMA&namespace=http://eudml.org/schema/1.1/eudml-book" +
                "&designation=EUDML_BOOK&description=&notes=&oaiAvailable=true" +
                "&versions=1.1--http://eudml.org/schema/1.1/eudml-book-1.1.xsd;1.2--http://eudml.org/schema/1.2/eudml-book-1.2.xsd";
        doGet(strURL);
    }

    @Test
    public void updateSchemaSimple() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/updateSchema?shortDesignation=TEST_SCHEMA&" +
                "namespace=http://eudml.org/schema/6.1/eudml-book6";
        doGet(strURL);
    }

    @Test
    public void removeSchema() throws IOException {
        String strURL = "http://127.0.0.1:8888/rest/removeSchema?id=TEST_SCHEMA";
        doGet(strURL);
    }
}
