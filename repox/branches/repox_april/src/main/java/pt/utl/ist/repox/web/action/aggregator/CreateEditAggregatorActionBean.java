package pt.utl.ist.repox.web.action.aggregator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import pt.utl.ist.repox.web.action.RepoxActionBean;

/**
 *
 * @author Georg Petz
 */
public class CreateEditAggregatorActionBean extends RepoxActionBean {

    private String name;
    private String url;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @DefaultHandler
    public Resolution preEdit() {
        return new ForwardResolution("/jsp/aggregator/create.jsp");
    }

    public Resolution submitAggregator() {

        System.out.println(getName());
        System.out.println(getUrl());
        System.out.println(getCode());

        //after a new aggreator is created a dataprovider has to be created too
        return new RedirectResolution("/dataProvider/CreateEditDataProvider.action?preEdit");
    }

    //user hits the cancel button
    public Resolution cancel() {
        return new RedirectResolution("/Homepage.action");
    }

    @ValidationMethod(on = {"submitAggregator"})
    public void validataDataProvider(ValidationErrors errors) {

        //check if the homepage exits
        try {
            boolean exists = exists(getUrl());
        } catch (Exception ex) {
            errors.add("aggregator", new LocalizableError("error.aggregator.invalidUrl"));
        }

    }

    static boolean exists(String URLName) throws MalformedURLException, IOException {
            HttpURLConnection.setFollowRedirects(false);
            //http://www.java-tips.org/java-se-tips/java.net/check-if-a-page-exists-2.html
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
    }
}
