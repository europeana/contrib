import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.File;
import java.io.IOException;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 05-11-2012
 * Time: 14:30
 */
public class TestWebServices {

    protected void doPost(String url, String xmlPath) throws IOException {
        // Get target URL
//        String strURL = "http://digmap3.ist.utl.pt:8080/riskManager/rest/functions/calculateRiskLevel?functionName=Function_X&projectName=SAP_IMPORT";

        // Get file to be posted
        File input = new File(xmlPath);

        // Prepare HTTP post
        PostMethod post = new PostMethod(url);

        // Request content will be retrieved directly
        // from the input stream
        // Per default, the request content needs to be buffered
        // in order to determine its length.
        // Request body buffering can be avoided when
        // content length is explicitly specified
//        post.setRequestEntity(new InputStreamRequestEntity(
//                new FileInputStream(input), input.length()));

        // Specify content type and encoding
        // If content encoding is not explicitly specified
        // ISO-8859-1 is assumed
        post.setRequestHeader("Content-type", "application/xml; charset=ISO-8859-1");

        // Get HTTP client
        HttpClient httpclient = new HttpClient();

        // Execute request
        try {

            int result = httpclient.executeMethod(post);

            // Display status code
            System.out.println("Response status code: " + result);

            // Display response
            System.out.println("Response body: ");
            System.out.println(post.getResponseBodyAsString());

        } finally {
            // Release current connection to the connection pool
            // once you are done
            post.releaseConnection();
        }
    }

    protected void doGet(String url) throws IOException {
        // Get target URL
//        String strURL = "http://digmap3.ist.utl.pt:8080/riskManager/rest/functions/calculateRiskLevel?functionName=Function_X&projectName=SAP_IMPORT";

        // Prepare HTTP get
        GetMethod get = new GetMethod(url);

        // Get HTTP client
        HttpClient httpclient = new HttpClient();

        // Execute request
        try {

            int result = httpclient.executeMethod(get);

            // Display status code
            System.out.println("Response status code: " + result);

            // Display response
            System.out.println("Response body: ");
            System.out.println(get.getResponseBodyAsString());

        } finally {
            // Release current connection to the connection pool
            // once you are done
            get.releaseConnection();
        }
    }
}
