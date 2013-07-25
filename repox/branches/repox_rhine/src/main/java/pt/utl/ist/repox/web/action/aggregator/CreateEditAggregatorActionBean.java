package pt.utl.ist.repox.web.action.aggregator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import eu.europeana.repox2sip.Repox2SipException;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.data.DataManager;
import pt.utl.ist.repox.web.action.RepoxActionBean;

/**
 *
 * @author Georg Petz
 */
public class CreateEditAggregatorActionBean extends RepoxActionBean {
    private static final Logger log = Logger.getLogger(CreateEditAggregatorActionBean.class);

    private Boolean editing;
    private AggregatorRepox aggregator;
    private String aggregatorId;

    public AggregatorRepox getAggregator() {
        return aggregator;
    }

    public void setAggregator(AggregatorRepox aggregatorRepox) {
        this.aggregator = aggregatorRepox;
    }

    public Boolean getEditing() {
        return editing;
    }

    public void setEditing(Boolean editing) {
        this.editing = editing;
    }

    public String getAggregatorId() {
        return aggregatorId;
    }

    public void setAggregatorId(String aggregatorId) {
        this.aggregatorId = aggregatorId;
    }

    @DefaultHandler
    public Resolution preEdit() throws DocumentException, IOException {

        if(aggregatorId != null && !aggregatorId.isEmpty()) {
            editing = true;
            aggregator = context.getRepoxManager().getDataManager().getAggregator(aggregatorId);

        }

        //System.out.println("pre-edit: getAggregatorId() = " + getAggregatorId());
        //System.out.println("pre-edit: aggregator = " + aggregator);

        FlashScope.getCurrent(getContext().getRequest(), true).put(this);
        return new ForwardResolution("/jsp/aggregator/create.jsp");
    }

    public Resolution submitAggregator() throws IOException, DocumentException, Repox2SipException {

        //System.out.println(aggregator.getName());
        //System.out.println(aggregator.getHomePage());
        //System.out.println(aggregator.getNameCode());

        //System.out.println("aggregator_submitAggregator = " + aggregator);

        DataManager dataManager = context.getRepoxManager().getDataManager();

        if(aggregatorId == null) {
            String generatedId = AggregatorRepox.generateId(aggregator.getName());
            aggregator.setId(generatedId);
        }
        else {
            aggregator.setId(aggregatorId);
        }

        if(editing != null && editing) {
            AggregatorRepox loadedAggregatorRepox = dataManager.getAggregator(aggregatorId);
            aggregator.setDataProviders(loadedAggregatorRepox.getDataProviders());
            aggregator.setIdDb(loadedAggregatorRepox.getIdDb());
            dataManager.updateAggregator(aggregator, aggregatorId);

            log.debug("Updated existing AggregatorRepox with id " + aggregatorId + " to id " + aggregator.getId());
            context.getMessages().add(new LocalizableMessage("aggregator.edit.success", aggregator.getName()));

            //after a new aggreator is created a dataprovider has to be created too
            return new RedirectResolution("/aggregator/ViewAggregator.action?aggregatorId=" + aggregator.getId());
        }
        else {
            dataManager.saveAggregator(aggregator);
            log.debug("Saved new DataProvider with id " + aggregator.getId());
            context.getMessages().add(new LocalizableMessage("aggregator.create.success", aggregator.getName(), aggregator.getId(), context.getServletContext().getContextPath()));

            //after a new aggreator is created a dataprovider has to be created too
            return new RedirectResolution("/dataProvider/CreateEditDataProvider.action?aggregatorId=" + aggregator.getId());
        }


    }

    //user hits the cancel button
    public Resolution cancel() {
        return new RedirectResolution("/Homepage.action");
    }

    @ValidationMethod(on = {"submitAggregator"})
    public void validataDataProvider(ValidationErrors errors) {
        //check if the homepage exits
        if(aggregator.getHomePage() != null && !aggregator.getHomePage().toString().equals("")){
            try {
                exists(aggregator.getHomePage().toString());
            } catch (Exception ex) {
                errors.add("aggregator", new LocalizableError("error.aggregator.invalidUrl"));
            }
        }
    }

    static boolean exists(String URLName) throws IOException {
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
