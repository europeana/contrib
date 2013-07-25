package pt.utl.ist.repox.web.spring;

import eu.europeana.repox2sip.Repox2SipException;
import org.apache.log4j.Level;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import eu.europeana.definitions.domain.Country;
import eu.europeana.repox2sip.models.ProviderType;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.data.DataManager;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.spring.session.SessionService;

/**
 *
 * Implementation of CRUD operations for DataProvider
 *
 * @author Georg Petz
 */
@Controller
@RequestMapping(value = "/springDataProvider")
public class DataProviderController {

    private static final Logger log = Logger.getLogger(DataProviderController.class);
    @Autowired
    private Validator validator;
    private SessionService session;

    public SessionService getRepoxSessionService() {
        return session;
    }

    public void setRepoxSessionService(SessionService repoxSessionService) {
        this.session = repoxSessionService;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * create a dataprovider
     *
     * @param aggregatorId id of the aggregator to be created
     * @param model
     * @return reference data: countrylist, providertypes, message from
     * session bean and id of the aggregator
     */
    @RequestMapping(value = "/createDataProvider.html", method = RequestMethod.GET)
    public Map createAggregator(
            @RequestParam(value = "aggregatorId") String aggregatorId,
            Model model) {
        DataProviderForm dataProviderForm = new DataProviderForm();
        model.addAttribute(dataProviderForm);

        Map referenceData = new HashMap();

        Map<String, String> countryList = new LinkedHashMap<String, String>();
        for (Country country : Country.values()) {
            countryList.put(country.name(), country.getEnglishName());
        }
        referenceData.put("countryList", countryList);

        Map<String, String> dataSetTypeList = new LinkedHashMap<String, String>();
        for (ProviderType type : ProviderType.values()) {
            dataSetTypeList.put(type.name(), type.name());
        }
        referenceData.put("typeList", dataSetTypeList);

        referenceData.put("message", getRepoxSessionService().getMessage());
        referenceData.put("aggregatorId", aggregatorId);

        return referenceData;
    }

    /**
     * view a dataprovider
     * 
     * @param dataProviderId id of the dataprovider to view
     * @param model with dataprovider and aggregator
     * @param dataSources datasources of the dataprovider
     */
    @RequestMapping(value = "/viewDataProvider.html", method = RequestMethod.GET)
    public void viewAggregator(
            @RequestParam(value = "dataProviderId") String dataProviderId,
            Model model,
            @ModelAttribute("dataSources") ArrayList<DataSource> dataSources) {

        RepoxManager manager = RepoxContextUtil.getRepoxManager();

        try {
            DataProvider dataProvider = manager.getDataManager().getDataProvider(dataProviderId);
            AggregatorRepox aggregator = dataProvider.getAggregator();

            model.addAttribute("aggregator", aggregator);
            model.addAttribute("dataProvider", dataProvider);

            //get all datasources assigned to the dataprovider
            Collection<DataSource> sources = dataProvider.getDataSources();
            for (DataSource source : sources) {
                dataSources.add(source);
            }

            //pick the message
            model.addAttribute("message", getRepoxSessionService().getMessage());
            getRepoxSessionService().setMessage("");

        } catch (DocumentException ex) {
            Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
        }
    }

    /**
     * edit a dataprovider
     *
     * @param dataProviderId id of the dataprovider to edit
     * @param model model with the dataproviderform
     */
    @RequestMapping(value = "/editDataProvider.html", method = RequestMethod.GET)
    public void editDataProvider(
            @RequestParam(value = "dataProviderId") String dataProviderId,
            Model model) {
        DataProviderForm dataProviderForm = new DataProviderForm();

        DataManager manager = RepoxContextUtil.getRepoxManager().getDataManager();

        Map<String, String> countryList = new LinkedHashMap<String, String>();
        for (Country country : Country.values()) {
            countryList.put(country.name(), country.getEnglishName());
        }
        model.addAttribute("countryList", countryList);

        Map<String, String> dataSetTypeList = new LinkedHashMap<String, String>();
        for (ProviderType type : ProviderType.values()) {
            dataSetTypeList.put(type.name(), type.name());
        }
        model.addAttribute("typeList", dataSetTypeList);


        if (dataProviderId != null && !dataProviderId.isEmpty()) {
            try {
                DataProvider dataProvider = manager.getDataProvider(dataProviderId);
                dataProviderForm.setDataProvider_name(dataProvider.getName());
                dataProviderForm.setDataProvider_nameCode(dataProvider.getNameCode());
                dataProviderForm.setDataProvider_id(dataProvider.getId());
                if (dataProvider.getHomePage() != null) {
                    dataProviderForm.setDataProvider_homePage(dataProvider.getHomePage().toString());
                }
                dataProviderForm.setDataProvider_description(dataProvider.getDescription());

                dataProviderForm.setDataProvider_country(dataProvider.getCountry().toString());
                dataProviderForm.setDataProvider_dataSetType(dataProvider.getDataSetType().toString());
            } catch (DocumentException ex) {
                Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
            }
        }

        model.addAttribute(dataProviderForm);
    }

    /**
     * delete a dataprovider
     * 
     * @param dataProviderId
     * @param model
     * @return
     */
    @RequestMapping(value = "/deleteDataProvider.html", method = RequestMethod.GET)
    public String deleteDataProvider(
            @RequestParam(value = "dataProviderId") String dataProviderId,
            Model model) {
        if (dataProviderId != null && !dataProviderId.isEmpty()) {
            RepoxManager manager = RepoxContextUtil.getRepoxManager();
            try {
                DataProvider dataProvider = manager.getDataManager().getDataProvider(dataProviderId);
                manager.getDataManager().deleteDataProvider(dataProvider.getId());
            } catch (Repox2SipException ex) {
                Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
            }
        }
        return "redirect://Homepage.action";
    }

    /**
     *
     * process and validate form
     *
     * @param cancel
     * @param aggregatorForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "/editCreateDataProvider.html", method = RequestMethod.POST)
    public String submitAggregator(
            @RequestParam(value = "cancel", required = false) String cancel,
            @RequestParam(value = "aggregatorId", required = false) String aggregatorId,
            @Valid@ModelAttribute final DataProviderForm dataProviderForm,
            final BindingResult bindingResult,
            Model model) throws DocumentException, IOException, Repox2SipException {

        boolean editing = !dataProviderForm.getDataProvider_id().equals("");

        model.addAttribute("aggregatorId",aggregatorId);

        //user hits the cancel button
        if ("Cancel".equals(cancel)) {
            return "redirect://Homepage.action";
        }

        model.addAttribute(dataProviderForm);

        Map<String, String> countryList = new LinkedHashMap<String, String>();
        for (Country country : Country.values()) {
            countryList.put(country.name(), country.getEnglishName());
        }
        model.addAttribute("countryList", countryList);

        Map<String, String> dataSetTypeList = new LinkedHashMap<String, String>();
        for (ProviderType type : ProviderType.values()) {
            dataSetTypeList.put(type.name(), type.name());
        }
        model.addAttribute("typeList", dataSetTypeList);

        //validate the form
        validator.validate(dataProviderForm, bindingResult);

        //errors exist, show user what is wrong
        if (bindingResult.hasErrors()) {
            if (editing) {
                model.addAttribute("editing", true);
                return null;
            } else {
                model.addAttribute("editing", false);
                return null;
            }
        } else
        {
            DataManager dataManager = RepoxContextUtil.getRepoxManager().getDataManager();

            if (editing) {
                DataProvider loadedDataProvider = dataManager.getDataProvider(dataProviderForm.getDataProvider_id());
                loadedDataProvider.setName(dataProviderForm.getDataProvider_name());
                loadedDataProvider.setDescription(dataProviderForm.getDataProvider_description());
                if (dataProviderForm.getDataProvider_homePage().isEmpty()) {
                    loadedDataProvider.setHomePage(null);
                } else {
                    loadedDataProvider.setHomePage(new URL(dataProviderForm.getDataProvider_homePage()));
                }
                loadedDataProvider.setNameCode(dataProviderForm.getDataProvider_nameCode());
                loadedDataProvider.setCountry(Country.valueOf(dataProviderForm.getDataProvider_country()));
                loadedDataProvider.setDataSetType(ProviderType.get(dataProviderForm.getDataProvider_dataSetType()));

                dataManager.updateDataProvider(loadedDataProvider, dataProviderForm.getDataProvider_id());

                log.debug("Updated existing DataProvider with id " + dataProviderForm.getDataProvider_id() + " to id " + loadedDataProvider.getId());

                getRepoxSessionService().setMessage("Data Provider with name " + dataProviderForm.getDataProvider_name() + " was edited successfully. To create a new Data Set for this Data Provider click <a href=\"/dataProvider/CreateEditDataSourceDirectoryImporter.action?schema=tel&dataProviderId=" + dataProviderForm.getDataProvider_id() + "\">here</a>.");

                return "redirect://dataProvider/ViewDataProvider.action?dataProviderId=" + loadedDataProvider.getId();

            } else {


                AggregatorRepox aggregatorRepox = dataManager.getAggregator(aggregatorId);

                DataProvider dataProvider = new DataProvider();

                dataProvider.setName(dataProviderForm.getDataProvider_name());
                String generatedId = DataProvider.generateId(dataProvider.getName());
                dataProvider.setId(generatedId);
                dataProvider.setAggregator(aggregatorRepox);
                dataProvider.setAggregatorId(aggregatorRepox.getId());
                dataProvider.setAggregatorIdDb(aggregatorRepox.getIdDb());
                dataProvider.setCountry(Country.valueOf(dataProviderForm.getDataProvider_country()));
                dataProvider.setDataSetType(ProviderType.get(dataProviderForm.getDataProvider_dataSetType()));
                dataProvider.setNameCode(dataProviderForm.getDataProvider_nameCode());
                dataProvider.setDescription(dataProviderForm.getDataProvider_description());
                if (dataProviderForm.getDataProvider_homePage().isEmpty()) {
                    dataProvider.setHomePage(null);
                } else {
                    dataProvider.setHomePage(new URL(dataProviderForm.getDataProvider_homePage()));
                }

                dataManager.saveDataProvider(dataProvider);

                log.debug("Saved new DataProvider with id " + dataProvider.getId());

                getRepoxSessionService().setMessage("Data Provider with name " + dataProvider.getName() + " was created successfully. To create a new Data Set for this Data Provider click <a href=\"/dataProvider/CreateEditDataSourceDirectoryImporter.action?schema=tel&dataProviderId=" + dataProvider.getId() + "\">here</a>.");

                return "redirect://dataProvider/ViewDataProvider.action?dataProviderId=" + dataProvider.getId();
            }
        }
    }
}
