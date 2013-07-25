package nl.kb.siwasoftware.server;

/**
 * Created by IntelliJ IDEA.
 * Developer: Thomas Beekman (thomas.beekman@kb.nl)
 * Date: 9-feb-2010
 * Time: 13:48:34
 */

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import javax.servlet.ServletContext;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class: ServicesReader
 * Description: This class contains the functions to read a services.xml and to transform a loaded services.xml to
 * a JSON String or a HTML table which provides the functionality to edit the services object. The two static
 * functions can be used to retrieve the services.xml, if the class is objectified it can be used to transform the
 * services.xml into the desired format.
 */
public class ServicesReader {
    /**
     * Static method for retrieving a services.xml which is bundled with the application and thus resides on the server.
     *
     * @param sc          the ServletContext, needed to retrieve the resource.
     * @param servicesUrl the relative URL to the resource.
     * @return the services.xml as a single String (in XML format).
     */
    public static String getServicesXmlFromResources(ServletContext sc, String servicesUrl) {
        if (servicesUrl.isEmpty()) {
            servicesUrl = "/resources/services13.xml";
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(sc.getResourceAsStream(servicesUrl)));
        StringBuilder servicesXml = new StringBuilder();
        try {
            String currLine;
            while ((currLine = br.readLine()) != null) {
                servicesXml.append(currLine).append('\n');
            }
            br.close();
        }
        catch (Exception ioe) {
            servicesXml.append(ioe.getMessage());
        }
        finally {
            try {
                br.close();
            }
            catch (IOException ioe2) {
                System.out.println("Error closing BufferedReader: " + ioe2.getMessage());
            }
        }

        return servicesXml.toString();
    }

    /**
     * Static method for retrieving a services.xml from an URL.
     *
     * @param sc servlet context
     * @param servicesUrl The URL to the services.xml
     * @return the services.xml as a single String (in XML format).
     */
    public static String getServicesXmlFromUrl(ServletContext sc, String servicesUrl) {
        String servicesXml = "";
        try {
            URL servUrl = new URL(servicesUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(servUrl.openStream()));

            String currLine = "";
            while ((currLine = br.readLine()) != null)
                servicesXml += currLine + "\n";
            br.close();
        }
        catch (MalformedURLException murle) {
            // It could be a resource file, handle it with the other Xml loader!
            return ServicesReader.getServicesXmlFromResources(sc, servicesUrl);
        }
        catch (IOException ioe) {
            System.out.println("Couldn't read from URL" + ioe.getMessage());
            return "";
        }

        return servicesXml;
    }

    /**
     * Returns the loaded services.xml as a JSON String.
     *
     * @param servicesXml the XML description?
     * @return the JSON String.
     */
    public static String getServicesJSON(String servicesXml) {

        // Fast fix: "type" will not be converted to JSON (the field "type" is neglected by net.sf.json) // todo: fix for real
        servicesXml = servicesXml.replaceAll("type", "SIWAtypeSIWA");

        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setSkipNamespaces(true);
        JSON json = xmlSerializer.read(servicesXml);

        // Get the result as a JSON object
        String result = json.toString(2);

        // Fast fix: change "SIWAtypeSIWA" back to "type" todo: fix for real
        result = result.replaceAll("SIWAtypeSIWA", "type");
        result = result.replaceAll("\"@xsi:type\": \"dcterms:URI\",\n", "");

        return result;
    }

    /**
     * Sets up an HTML page and includes the Javascripts to provide the user to alter his services object.
     *
     * @param url the URL to the JSON object to be processed by the Javascript.
     * @return an HTML page set-up to load the user's JSON Object and present it with the ability to edit.
     */
    public static String getServicesHtml(String url) {
        return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"" +
                "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html>\n" +
                "<head>\n" +
                "<script type=\"text/javascript\" src=\"servedit.js\"></script>\n" +
                "<link rel=\"stylesheet\" href=\"style.css\">\n" +
                "<title>Services.xml</title>\n" +
                "</head>\n"+
                "<body>\n" +
                "<div id=\"menutab\">" +
                "<span id=\"servview\" class=\"menuitemsel\">Services List</span>" +
                "<span id=\"editview\" class=\"menuitem\">Edit Field</span>" +
                "<span id=\"regview\" class=\"menuitem\">Browse through registry</span>" +
                "<span id=\"loadview\" class=\"menuitem\">Load services file</span>" +
                "<span id=\"resetserv\" class=\"resetservs\">Reset services object</span>" +
                "</div>" +
                "<div id=\"servicestab\"></div>\n" +
                "<div id=\"edittab\"><h3>No field selected for editing!</h3></div>\n" +
                "<div id=\"regtab\">\n" +
                "  <div id=\"regsearch\"></h3></div>\n" +
                "  <div id=\"regbox\"></h3></div>\n" +
                "</div>\n" +
                "<div id=\"loadtab\"></div>\n" +
                "<div id=\"jsonbox\"></div>\n" +
                "<script type=\"text/javascript\">\n" +
                "  var url=\"" + url + "\";\n" +
                "  loadJson(this.document, url);\n" +
                "</script>\n" + "</body>\n</html>";
    }
}
