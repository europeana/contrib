package pt.utl.ist.repox.web.spring;

import eu.europeana.repox2sip.Repox2SipException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.data.DataManager;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.web.spring.session.SessionService;

/**
 *
 * Implementation of CRUD operations for Aggregator
 *
 * @author Georg Petz
 */
@Controller
@RequestMapping(value = "/springAggregator")
public class AggregatorController {

    private static final Logger log = Logger.getLogger(AggregatorController.class);
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
     *
     * create an aggregator
     *
     * @param model
     */
    @RequestMapping(value = "/createAggregator.html", method = RequestMethod.GET)
    public void createAggregator(Model model) {
        AggregatorForm aggregatorForm = new AggregatorForm();
        model.addAttribute(aggregatorForm);
    }

    /**
     *
     * view an aggregator
     *
     * @param aggregatorId
     * @param model
     * @param dataProviders list of dataproviders
     */
    @RequestMapping(value = "/viewAggregator.html", method = RequestMethod.GET)
    public void viewAggregator(
            @RequestParam(value = "aggregatorId") String aggregatorId,
            Model model,
            @ModelAttribute("dataProviders") ArrayList<DataProvider> dataProviders) {

        model.addAttribute("aggregator_id", aggregatorId);

        RepoxManager manager = RepoxContextUtil.getRepoxManager();

        try {
            AggregatorRepox aggregator = manager.getDataManager().getAggregator(aggregatorId);
            model.addAttribute("aggregator_name", aggregator.getName());
            model.addAttribute("aggregator_nameCode", aggregator.getNameCode());
            URL homePage = aggregator.getHomePage();
            if (homePage != null) {
                model.addAttribute("aggregator_homePage", homePage.toString());
            }

            //get all dataproviders assigned to the aggregator
            List<DataProvider> providers = aggregator.getDataProviders();
            for (DataProvider provider : providers) {
                dataProviders.add(provider);
            }

        } catch (DocumentException ex) {
            Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
        }
    }

    /**
     *
     * edit an aggregator
     *
     * @param aggregatorId
     * @param model
     */
    @RequestMapping(value = "/editAggregator.html", method = RequestMethod.GET)
    public void editAggregator(
            @RequestParam(value = "aggregatorId") String aggregatorId,
            Model model) {

        AggregatorForm aggregatorForm = new AggregatorForm();

        RepoxManager manager = RepoxContextUtil.getRepoxManager();

        if (aggregatorId != null && !aggregatorId.isEmpty()) {
            try {
                AggregatorRepox aggregator = manager.getDataManager().getAggregator(aggregatorId);
                aggregatorForm.setAggregator_id(aggregatorId);
                aggregatorForm.setAggregator_name(aggregator.getName());
                if (aggregator.getHomePage() != null) {
                    aggregatorForm.setAggregator_homePage(aggregator.getHomePage().toString());
                }
                aggregatorForm.setAggregator_nameCode(aggregator.getNameCode());
            } catch (DocumentException ex) {
                Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
            }
        }
        model.addAttribute(aggregatorForm);
    }

    /**
     *
     * delete an aggregator
     *
     * @param aggregatorId
     * @param model
     * @return
     */
    @RequestMapping(value = "/deleteAggregator.html", method = RequestMethod.GET)
    public String deleteAggregator(
            @RequestParam(value = "aggregatorId") String aggregatorId,
            Model model) {
        if (aggregatorId != null && !aggregatorId.isEmpty()) {
            RepoxManager manager = RepoxContextUtil.getRepoxManager();
            try {
                AggregatorRepox aggregatorRepox = manager.getDataManager().getAggregator(aggregatorId);
                manager.getDataManager().deleteAggregator(aggregatorRepox.getId());
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
    @RequestMapping(value = "/editCreateAggregator.html", method = RequestMethod.POST)
    public String submitAggregator(
            @RequestParam(value = "cancel", required = false) String cancel,
            @ModelAttribute AggregatorForm aggregatorForm,
            BindingResult bindingResult,
            Model model) {

        boolean editing = !aggregatorForm.getAggregator_id().equals("");

        //user hits the cancel button
        if ("Cancel".equals(cancel)) {
            return "redirect://Homepage.action";
        }

        model.addAttribute(aggregatorForm);

        //validate the form
        validator.validate(aggregatorForm, bindingResult);

        //errors exist, show user what is wrong
        if (bindingResult.hasErrors()) {
            if (editing) {
                model.addAttribute("editing", true);
                return null;
            } else {
                model.addAttribute("editing", false);
                return null;
            }

        } else //edit or create an aggregator
        {
            DataManager dataManager = RepoxContextUtil.getRepoxManager().getDataManager();

            if (editing) {
                try {
                    AggregatorRepox loadedAggregatorRepox = dataManager.getAggregator(aggregatorForm.getAggregator_id());
                    loadedAggregatorRepox.setName(aggregatorForm.getAggregator_name());
                    loadedAggregatorRepox.setNameCode(aggregatorForm.getAggregator_nameCode());
                    if (aggregatorForm.getAggregator_homePage().equals("")) {
                        loadedAggregatorRepox.setHomePage(null);
                    } else {
                        boolean exists = exists(aggregatorForm.getAggregator_homePage());
                        if (exists) {
                            loadedAggregatorRepox.setHomePage(new URL(aggregatorForm.getAggregator_homePage()));
                        } else {
                            //TODO: use Homepage Validator Annotation instead
                            model.addAttribute("error", "Homepage doesn't exists!");
                            return null;
                        }
                    }

                    dataManager.updateAggregator(loadedAggregatorRepox, aggregatorForm.getAggregator_id());

                    log.debug("Updated existing AggregatorRepox with id " + aggregatorForm.getAggregator_id() + " to id " + loadedAggregatorRepox.getId());

                    getRepoxSessionService().setMessage("Aggregator with name " + loadedAggregatorRepox.getName() + " was edited successfully.");

                    return "redirect://springAggregator/viewAggregator.html?aggregatorId=" + loadedAggregatorRepox.getId();
                    
                } catch (DocumentException ex) {
                    Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
                } catch (Repox2SipException ex) {
                    Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
                }

            } else {
                AggregatorRepox aggregator = new AggregatorRepox();
                try {
                    aggregator.setName(aggregatorForm.getAggregator_name());
                    aggregator.setNameCode(aggregatorForm.getAggregator_nameCode());
                    if (!aggregatorForm.getAggregator_homePage().equals("")) {
                        boolean exists = exists(aggregatorForm.getAggregator_homePage());
                        if (exists) {
                            aggregator.setHomePage(new URL(aggregatorForm.getAggregator_homePage()));
                        } else {
                            //TODO: use Homepage Validator Annotation instead
                            model.addAttribute("error", "Homepage doesn't exists!");
                            return null;
                        }
                    }

                    aggregator.setId(AggregatorRepox.generateId(aggregator.getName()));

                    DataManager.Status result = dataManager.saveAggregator(aggregator);

                    if (result == DataManager.Status.OK) {

                        log.debug("Saved new DataProvider with id " + aggregator.getId());

                        getRepoxSessionService().setMessage("Aggregator with name " + aggregator.getName() + " was created successfully.");

                        return "redirect://springDataProvider/createDataProvider.html?aggregatorId=" + aggregator.getId();

                    } else if (result == DataManager.Status.ALREADY_EXISTS) {

                        //TODO: use Homepage Validator Annotation instead
                        model.addAttribute("error", "Aggregator with Name Code '" + aggregatorForm.getAggregator_name() + "' already exists.");

                    } else {
                        // todo: alert user that an error occurred while adding the new aggregator to DB
                    }

                } catch (IOException ex) {
                    Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
                } catch (DocumentException ex) {
                    Logger.getLogger(AggregatorController.class.getName()).log(Level.FATAL, null, ex);
                }
            }
        }
        return null;
    }

    /**
     *
     * check whether a web page exists or not.
     *
     * @param URLName
     * @return
     * @throws IOException
     */
    private boolean exists(String URLName) {
        boolean exists = true;
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return con.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException ex) {
            exists = false;
        }
        return exists;
    }
}
