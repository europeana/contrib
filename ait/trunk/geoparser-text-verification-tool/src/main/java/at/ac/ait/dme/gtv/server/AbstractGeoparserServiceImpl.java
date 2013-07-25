package at.ac.ait.dme.gtv.server;

import at.ac.ait.dme.gtv.client.model.GeoparsedText;
import at.ac.ait.dme.gtv.client.model.Location;
import at.ac.ait.dme.gtv.client.server.GeoparserService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Abstract implementation of the {@link GeoparserService}
 *
 * @author Manuel Bernhardt
 */
public abstract class AbstractGeoparserServiceImpl extends RemoteServiceServlet implements GeoparserService {

    @Override
    public GeoparsedText getGeoparsedText(String url) {

        String text = null;
        try {
            text = retrieveText(url);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if(text == null) {
            return null;
        }

        List<Location> locations = getLocations(url, text);

        // compute the HTML and populate the Location IDs
        int n = 0;
        for (Location l : locations) {
            while (text.indexOf(l.getName()) > -1) {
                String id = "node-" + n;
                text = text.replaceFirst(l.getName(), "<span id=\"" + id + "\" style=\"background-color:#ff0000\">" + "__PLACEHOLDER__" + "</span>");
                l.setId(id);
                n++;
            }
            text = text.replaceAll("__PLACEHOLDER__", l.getName());
        }
        GeoparsedText res = new GeoparsedText();
        res.setHtml(text);
        res.setUrl(url);
        res.setLocations(locations);
        return res;
    }

    /**
     * Reads a text given an URL
     *
     * @param url the URL at which the text can be retrieved
     * @return a String with the content at the URL
     * @throws IOException in case a problem occurs while reading the URL
     */
    protected String retrieveText(String url) throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod get = new GetMethod(url);
        String text;
        client.executeMethod(get);

        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(get.getResponseBodyAsStream(), "UTF-8"));
        while ((line = r.readLine()) != null) {
            sb.append(line).append("<br />");
        }
        text = sb.toString();

        return text;
    }

    /**
     * Computes a list of locations given a URL and the text at this URL
     * @param url the URL at the origin of the text
     * @param text the plain text available at the URL
     * @return a List of recognized {@link at.ac.ait.dme.gtv.client.model.Location}-s
     */
    protected abstract List<Location> getLocations(String url, String text);
}
