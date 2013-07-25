package pt.utl.ist.repox.web.action.dataProvider;

import eu.europeana.repox2sip.Repox2SipException;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.*;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.data.DataManager;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.action.RepoxActionBean;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CreateEditDataProviderActionBean extends RepoxActionBean {
    private static final Logger log = Logger.getLogger(CreateEditDataProviderActionBean.class);

    private AggregatorRepox aggregatorRepox;
    protected String aggregatorId;

    private String dataProviderId;
    @ValidateNestedProperties({
            @Validate(field = "name", required = true, on = {"submitDataProvider"})
            /*, @Validate(field = "description", required = true, on = {"submitDataProvider"}) */
    })
    private DataProvider dataProvider;
    private Boolean editing;
    private FileBean logoFile;

    public String getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(String dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public Boolean getEditing() {
        return editing;
    }

    public void setEditing(Boolean editing) {
        this.editing = editing;
    }

    public FileBean getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(FileBean logoFile) {
        this.logoFile = logoFile;
    }

    public AggregatorRepox getAggregator() {
        return aggregatorRepox;
    }

    public void setAggregator(AggregatorRepox aggregatorRepox) {
        this.aggregatorRepox = aggregatorRepox;
    }

    public String getAggregatorId() {
        return aggregatorId;
    }

    public void setAggregatorId(String aggregatorId) {
        this.aggregatorId = aggregatorId;
    }


    @DefaultHandler
    public Resolution preEdit() throws DocumentException, IOException {
        if(dataProviderId != null && !dataProviderId.isEmpty()) {
            editing = true;
            dataProvider = context.getRepoxManager().getDataManager().getDataProvider(dataProviderId);
            aggregatorRepox = RepoxContextUtil.getRepoxManager().getDataManager().getAggregator(dataProvider.getAggregatorId());
        }
        
        FlashScope.getCurrent(getContext().getRequest(), true).put(this);
        return new ForwardResolution("/jsp/dataProvider/create.jsp");
    }

    public Resolution cancel() {
//		context.getMessages().add(new LocalizableMessage("common.cancel.message"));
        if(dataProvider == null && (context.getRequest().getParameter("dataProviderId") == null
                || context.getRequest().getParameter("dataProviderId").isEmpty())) {
            return new RedirectResolution("/Homepage.action");
        }
        return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + context.getRequest().getParameter("dataProviderId"));
    }


    @ValidationMethod(on = {"submitDataProvider"})
    public void validataDataProvider(ValidationErrors errors) throws DocumentException, IOException {
        DataManager dataManager = context.getRepoxManager().getDataManager();
        if(editing == null || !editing) {
            List<AggregatorRepox> aggregators = dataManager.loadAggregatorsRepox();

            for (AggregatorRepox aggregatorRepox : aggregators) {
                for (DataProvider currentDataProvider : aggregatorRepox.getDataProviders()) {
                    if(currentDataProvider.getName().equals(dataProvider.getName())) {
                        errors.add("dataProvider", new LocalizableError("error.dataProvider.existingName", dataProvider.getName()));
                    }
                }
            }
        }
        else if(!dataProviderId.equals(dataProvider.getId())) { //ID Changed
            if(dataManager.getDataProvider(dataProvider.getId()) != null) {
                errors.add("dataProvider", new LocalizableError("error.dataSource.existingId", dataProvider.getId()));
            }
        }

        //check if the homepage exits
        if(dataProvider.getHomePage() != null && !dataProvider.getHomePage().toString().equals("")){
            try {
                exists(dataProvider.getHomePage().toString());
            } catch (Exception ex) {
                errors.add("dataProvider", new LocalizableError("error.dataProvider.invalidUrl"));
            }
        }
    }

    public Resolution submitDataProvider() throws DocumentException, IOException, Repox2SipException {
        DataManager dataManager = context.getRepoxManager().getDataManager();
        if(dataProviderId == null) {
            String generatedId = DataProvider.generateId(dataProvider.getName());
            dataProvider.setId(generatedId);
        }
        else {
            dataProvider.setId(dataProviderId);
        }
        aggregatorRepox = dataManager.getAggregator(aggregatorId);

        dataProvider.setAggregator(aggregatorRepox);
        dataProvider.setAggregatorId(aggregatorRepox.getId());
        dataProvider.setAggregatorIdDb(aggregatorRepox.getIdDb());


        if(editing != null && editing) {
            DataProvider loadedDataProvider = dataManager.getDataProvider(dataProviderId);
            dataProvider.setDataSources(loadedDataProvider.getDataSources());
            dataProvider.setIdDb(loadedDataProvider.getIdDb());

            dataManager.updateDataProvider(dataProvider, dataProviderId);

            log.debug("Updated existing DataProvider with id " + dataProviderId + " to id " + dataProvider.getId());
            context.getMessages().add(new LocalizableMessage("dataProvider.edit.success", dataProvider.getName(), dataProvider.getId(), context.getServletContext().getContextPath()));
        }
        else {
            dataManager.saveDataProvider(dataProvider);
            log.debug("Saved new DataProvider with id " + dataProvider.getId());
            context.getMessages().add(new LocalizableMessage("dataProvider.create.success", dataProvider.getName(), dataProvider.getId(), context.getServletContext().getContextPath()));
        }
        return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + dataProvider.getId());
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