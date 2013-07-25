package at.ac.ait.dme.gtv.server;

import at.ac.ait.dme.gtv.client.model.Location;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the GeoparserService using Yahoo! Placemaker
 */
public class YahooPlacemakerGeoparserServiceImpl extends AbstractGeoparserServiceImpl {

    private static final long serialVersionUID = 8405518097771500353L;

    private static final String YAHOO_APP_ID = "zi9mdSvV34F.bAJZAtkgmKnNNlBUid0hAQ4To0G.SnLlcJpSOkkmOzk9MgvPcQ";
    private static final int YAHOO_PLACEMAKER_MAX_LENGTH = 50000;
    private static final String YAHOO_PLACEMAKER_URL = "http://wherein.yahooapis.com/v1/document";

    protected List<Location> getLocations(String url, String text) {
        if (text.length() < YAHOO_PLACEMAKER_MAX_LENGTH) {
            PostMethod post = getBasicPlaceMakerPost();
            post.addParameter("documentURL", url);
            return getLocationsBasic(post);
        } else {
            List<Location> result = new ArrayList<Location>();
            int i = 0;
            while (i < text.length()) {

                // FIXME we're cutting words into pieces here

                PostMethod post = getBasicPlaceMakerPost();
                if (i + YAHOO_PLACEMAKER_MAX_LENGTH < text.length()) {
                    post.addParameter("documentContent", text.substring(i, i + YAHOO_PLACEMAKER_MAX_LENGTH));
                } else {
                    post.addParameter("documentContent", text.substring(i, text.length()));
                }
                result.addAll(getLocationsBasic(post));
                i += YAHOO_PLACEMAKER_MAX_LENGTH;
            }
            return result;
        }
    }

    private PostMethod getBasicPlaceMakerPost() {
        PostMethod post = new PostMethod(YAHOO_PLACEMAKER_URL);
        post.addParameter("documentType", "text/plain");
        post.addParameter("appId", YAHOO_APP_ID);
        return post;
    }

    List<Location> getLocationsBasic(PostMethod post) {
        InputStream result;

        HttpClient client = new HttpClient();

        try {
            client.executeMethod(post);
            //System.out.println(post.getResponseBodyAsString());
            result = post.getResponseBodyAsStream();
            return parsePlaceMakerResult(result);

        } catch (HttpException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (YahooAPIException e) {
            e.printStackTrace();
        }

        return new ArrayList<Location>();
    }

    private List<Location> parsePlaceMakerResult(InputStream s) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, YahooAPIException {
        List<Location> r = new ArrayList<Location>();

        Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(s);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        // first, check if this is not an error
        if (xPath.evaluate("/yahoo:error", response, XPathConstants.NODE) != null) {
            String message = (String) xPath.evaluate("yahoo:error/yahoo:description", response, XPathConstants.STRING);
            throw new YahooAPIException(message);
        }

        Node documentNode = (Node) xPath.evaluate("/contentlocation/document", response, XPathConstants.NODE);
        if (documentNode != null) {
            for (int i = 0; i < documentNode.getChildNodes().getLength(); i++) {
                Node n = documentNode.getChildNodes().item(i);
                if (n.getNodeName().equals("placeDetails")) {
                    String woeId = (String) xPath.evaluate("place/woeId", n, XPathConstants.STRING);
                    String realName = (String) xPath.evaluate("place/name", n, XPathConstants.STRING);
                    String placeType = (String) xPath.evaluate("place/type", n, XPathConstants.STRING);
                    String longitude = (String) xPath.evaluate("place/centroid/latitude", n, XPathConstants.STRING);
                    String latitude = (String) xPath.evaluate("place/centroid/longitude", n, XPathConstants.STRING);

                    // fetch details from the reference in the referenceList
                    String referenceName = (String) xPath.evaluate("/contentlocation/document/referenceList/reference[woeIds='" + woeId + "']/text", n, XPathConstants.STRING);
                    //String start = (String) xPath.evaluate("/contentlocation/document/referenceList/reference[woeIds='" + woeId + "']/start", n, XPathConstants.STRING);
                    //String end = (String) xPath.evaluate("/contentlocation/document/referenceList/reference[woeIds='" + woeId + "']/end", n, XPathConstants.STRING);

                    System.out.println(i + " " + realName + " " + longitude + " " + latitude + " " + placeType);

                    // http://developer.yahoo.com/geo/geoplanet/guide/concepts.html#placetypes
                    Location.PlaceType t;
                    if (placeType.equals("Town")) {
                        t = Location.PlaceType.TOWN;
                    } else if (placeType.equals("Country")) {
                        t = Location.PlaceType.COUNTRY;
                    } else if (placeType.equals("State")) {
                        t = Location.PlaceType.STATE;
                    } else if (placeType.equals("County")) {
                        t = Location.PlaceType.COUNTY;
                    } else {
                        throw new RuntimeException("Unmapped place type '" + placeType + "'");
                    }

                    r.add(new Location(referenceName, Double.parseDouble(longitude), Double.parseDouble(latitude), t));
                }
            }
        }
        return r;
    }

    @Override
    protected void doUnexpectedFailure(Throwable arg0) {
        super.doUnexpectedFailure(arg0);
        arg0.printStackTrace();
    }

    static class YahooAPIException extends Exception {
        public YahooAPIException(String message) {
            super(message);
        }
    }

}
