package pt.utl.ist.repox.web.spring;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Email;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

public class PropertiesForm {

    @NotBlank
    private String repox_administration_pass;
    @NotBlank
    private String repository_dir;
    @NotBlank
    private String xmlConfig_dir;
    @NotBlank
    private String oairequests_dir;
    @NotBlank
    private String database_dir;
    @NotBlank
    private String baseurn;
    //    @Email
    @NotBlank
    private String administrator_email;
    @NotBlank
    private String smtp_server;
    @NotBlank
    private String smtp_port;
    @NotBlank
    private String default_email;
    @NotBlank
    private String exportation_folder;

    private String administrator_email_pass;

    public String getRepox_administration_pass() {
        return repox_administration_pass;
    }

    public void setRepox_administration_pass(String repox_administration_pass) {
        this.repox_administration_pass = repox_administration_pass;
    }

    public String getRepository_dir() {
        return repository_dir;
    }

    public void setRepository_dir(String repository_dir) {
        this.repository_dir = repository_dir;
    }

    public String getXmlConfig_dir() {
        return xmlConfig_dir;
    }

    public void setXmlConfig_dir(String xmlConfig_dir) {
        this.xmlConfig_dir = xmlConfig_dir;
    }

    public String getOairequests_dir() {
        return oairequests_dir;
    }

    public void setOairequests_dir(String oairequests_dir) {
        this.oairequests_dir = oairequests_dir;
    }

    public String getDatabase_dir() {
        return database_dir;
    }

    public void setDatabase_dir(String database_dir) {
        this.database_dir = database_dir;
    }

    public String getBaseurn() {
        return baseurn;
    }

    public void setBaseurn(String baseurn) {
        this.baseurn = baseurn;
    }

    public String getAdministrator_email() {
        return administrator_email;
    }

    public void setAdministrator_email(String administrator_email) {
        this.administrator_email = administrator_email;
    }

    public String getSmtp_server() {
        return smtp_server;
    }

    public void setSmtp_server(String smtp_server) {
        this.smtp_server = smtp_server;
    }

    public String getSmtp_port() {
        return smtp_port;
    }

    public void setSmtp_port(String smtp_port) {
        this.smtp_port = smtp_port;
    }

    public String getDefault_email() {
        return default_email;
    }

    public void setDefault_email(String default_email) {
        this.default_email = default_email;
    }

    public String getAdministrator_email_pass() {
        return administrator_email_pass;
    }

    public void setAdministrator_email_pass(String administrator_email_pass) {
        this.administrator_email_pass = administrator_email_pass;
    }

    public String getExportation_folder() {
        return exportation_folder;
    }

    public void setExportation_folder(String exportation_folder) {
        this.exportation_folder = exportation_folder;
    }
}
