package pt.utl.ist.repox.web.spring;

import java.util.List;
import java.util.Properties;

import eu.europeana.repox2sip.models.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.utl.ist.repox.md5.MD5Generator;
import pt.utl.ist.repox.util.PropertyUtil;
import pt.utl.ist.repox.util.RepoxContextUtil;

/**
 *
 * Controller to edit properties file
 *
 * @author Georg Petz
 */
@Controller
public class PropertiesController {

    @Autowired
    private Validator validator;

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @RequestMapping(value = "/propertiesForm.html", method = RequestMethod.GET)
    public void propertiesForm(Model model) {

        Properties properties = PropertyUtil.loadCorrectedConfiguration(RepoxContextUtil.CONFIG_FILE);
        PropertiesForm propertiesForm = new PropertiesForm();

        propertiesForm.setRepository_dir(properties.getProperty("repository.dir"));
        propertiesForm.setXmlConfig_dir(properties.getProperty("xmlConfig.dir"));
        propertiesForm.setOairequests_dir(properties.getProperty("oairequests.dir"));
        propertiesForm.setDatabase_dir(properties.getProperty("database.dir"));
        propertiesForm.setBaseurn(properties.getProperty("baseurn"));
        propertiesForm.setAdministrator_email(properties.getProperty("administrator.email"));
        propertiesForm.setSmtp_server(properties.getProperty("smtp.server"));
        propertiesForm.setSmtp_port(properties.getProperty("smtp.port"));
        propertiesForm.setDefault_email(properties.getProperty("default.email"));
        propertiesForm.setAdministrator_email_pass(properties.getProperty("administrator.email.pass"));
        propertiesForm.setExportation_folder(properties.getProperty("exportDefaultFolder"));

        model.addAttribute(propertiesForm);
    }

    @RequestMapping(value = "/properties.html", method = RequestMethod.POST)
    public String properties(@ModelAttribute PropertiesForm propertiesForm, BindingResult bindingResult, Model model) {

        Properties properties = PropertyUtil.loadCorrectedConfiguration(RepoxContextUtil.CONFIG_FILE);

        model.addAttribute("repository_dir", propertiesForm.getRepository_dir());
        model.addAttribute("xmlConfig_dir", propertiesForm.getXmlConfig_dir());
        model.addAttribute("oairequests_dir", propertiesForm.getOairequests_dir());
        model.addAttribute("database_dir", propertiesForm.getDatabase_dir());
        model.addAttribute("baseurn", propertiesForm.getBaseurn());
        model.addAttribute("administrator_email", propertiesForm.getAdministrator_email());
        model.addAttribute("smtp_server", propertiesForm.getSmtp_server());
        model.addAttribute("smtp_port", propertiesForm.getSmtp_port());
        model.addAttribute("default_email", propertiesForm.getDefault_email());
        model.addAttribute("administrator_email_pass", propertiesForm.getAdministrator_email_pass());
        model.addAttribute("exportation_folder", propertiesForm.getExportation_folder());


        model.addAttribute(propertiesForm);

        properties.setProperty("baseurn", propertiesForm.getBaseurn());
        properties.setProperty("administrator.email", propertiesForm.getAdministrator_email());
        properties.setProperty("repository.dir", propertiesForm.getRepository_dir());
        properties.setProperty("xmlConfig.dir", propertiesForm.getXmlConfig_dir());
        properties.setProperty("oairequests.dir", propertiesForm.getOairequests_dir());
        properties.setProperty("database.dir", propertiesForm.getDatabase_dir());
        properties.setProperty("default.email", propertiesForm.getDefault_email());
        properties.setProperty("smtp.server", propertiesForm.getSmtp_server());
        properties.setProperty("smtp.port", propertiesForm.getSmtp_port());
        properties.setProperty("administrator.email.pass", propertiesForm.getAdministrator_email_pass());
        properties.setProperty("exportDefaultFolder", propertiesForm.getExportation_folder());

        validator.validate(propertiesForm, bindingResult);



        if (bindingResult.hasErrors()) {
            String errorsString = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorsString = errorsString + error.getField().replace("_", ".") + " ";
            }
            model.addAttribute("blank_fields", errorsString);

        } else {

            PropertyUtil.saveProperties(properties, RepoxContextUtil.CONFIG_FILE);

            // todo: allow to change REPOX default password
            //MD5Generator md5Generator = new MD5Generator();
            //String result = md5Generator.MD5(propertiesForm.getRepox_administration_pass());


            // reload REPOX properties
            RepoxContextUtil.reloadProperties();
            //everything ok, no blank fields
            return "redirect://Homepage.action";
        }

        return null;

    }
}
