package eu.europeana.sip.licensing.network;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import eu.europeana.sip.licensing.domain.Answers;
import eu.europeana.sip.licensing.domain.CreativeCommonsModel;
import eu.europeana.sip.licensing.domain.License;
import eu.europeana.sip.licensing.model.CustomClassLoader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implementation of the LicenseService.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class LicenseServiceImpl implements LicenseService {

    private final static Logger LOG = Logger.getLogger(LicenseServiceImpl.class.getName());

    private final HttpClient httpClient = new HttpClient();
    private static final String CC_ISSUE = "http://api.creativecommons.org/rest/1.5/license/standard/issue?"; // todo: move to properties file
    private static final String CC_STANDARD = "http://api.creativecommons.org/rest/dev/license/standard"; // todo: move to properties file

    @Override
    public License requestLicense(Answers answers) throws IOException {
        LOG.info(String.format("Sending request to server%n%s", answers.toXML()));
        PostMethod postMethod = new PostMethod(CC_ISSUE);
        postMethod.addParameter("answers", answers.toXML());
        int result = httpClient.executeMethod(postMethod);
        XStream xStream = new XStream(new DomDriver()); // todo: do we need a domdriver?
        xStream.setClassLoader(CustomClassLoader.getInstance());
        xStream.processAnnotations(License.class);
        LOG.info(String.format("Got response from server : %d %n%s", result, postMethod.getResponseBodyAsString()));
        return (License) xStream.fromXML(postMethod.getResponseBodyAsString());
    }

    @Override
    public CreativeCommonsModel retrieveStandardLicenseSet() throws IOException {
        LOG.info(String.format("Sending request to server"));
        GetMethod getMethod = new GetMethod(CC_STANDARD);
        int result = httpClient.executeMethod(getMethod);
        XStream xStream = new XStream(new DomDriver()); // todo: do we need a domdriver?
        xStream.setClassLoader(CustomClassLoader.getInstance());
        xStream.processAnnotations(CreativeCommonsModel.class);
        LOG.info(String.format("Got response from server : %d%n%s", result, getMethod.getResponseBodyAsString()));
        return (CreativeCommonsModel) xStream.fromXML(getMethod.getResponseBodyAsString());
    }
}
