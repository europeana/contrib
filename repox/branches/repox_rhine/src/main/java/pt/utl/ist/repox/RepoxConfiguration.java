/*
 * Created on 23/Mar/2006
 *
 */
package pt.utl.ist.repox;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


/**
 * Represents all the global available to the application. Just for convenience.
 * Not to be used for having globals that are important for the model.
 *
 * @author Nuno Freire
 */
public class RepoxConfiguration {
    public static final String TEMP_DIRNAME = "temp";
    public static final String METADATA_TRANSFORMATIONS_DIRNAME = "xslt";
    public static final String LOGOS_DIRNAME = "logos";

    private static final String PROPERTY_BASE_URN = "baseurn";
    private static final String PROPERTY_REPOSITORY_DIR = "repository.dir";
    private static final String PROPERTY_XML_CONFIG_DIR = "xmlConfig.dir";
    private static final String PROPERTY_OAI_REQUEST_DIR = "oairequests.dir";
    private static final String PROPERTY_ADMINISTRATOR_EMAIL = "administrator.email";
    private static final String PROPERTY_SMTP_SERVER = "smtp.server";
    private static final String PROPERTY_DEFAULT_EMAIL = "default.email";
    private static final String PROPERTY_MAIL_PASS = "administrator.email.pass";

    private static final String PROPERTY_DB_DIR = "database.dir";
    private static final String PROPERTY_DB_DRIVER_CLASSNAME = "database.driverClassName";
    private static final String PROPERTY_DB_EMBEDDED_DRIVER = "database.embeddedDriver";
    private static final String PROPERTY_DB_URL = "database.url";
    private static final String PROPERTY_DB_CREATE = "database.create";
    private static final String PROPERTY_DB_USER = "database.user";
    private static final String PROPERTY_DB_PASSWORD = "database.password";
    private static final String PROPERTY_USE_SIP_DB = "useSipDatabase";


    private String mailPassword;

    private String useSipDataBase;
    private String baseUrn;
    private String repositoryPath;
    private String xmlConfigPath;
    private String oaiRequestPath;
    private String administratorEmail;
    private String smtpServer;
    private String defaultEmail;

    private String databasePath;

    private String databaseDriverClassName;
    private boolean databaseEmbeddedDriver;
    private String databaseUrl;
    private boolean databaseCreate;
    private String databaseUser;
    private String databasePassword;


    public RepoxConfiguration(Properties configurationProperties) throws IOException {
        super();

        this.baseUrn = configurationProperties.getProperty(PROPERTY_BASE_URN);
        this.repositoryPath = configurationProperties.getProperty(PROPERTY_REPOSITORY_DIR);
        if(this.repositoryPath != null){
            File repositoryFile = new File(repositoryPath);
            if(!repositoryFile.exists()) {
                repositoryFile.mkdirs();
            }
        }
        this.xmlConfigPath = configurationProperties.getProperty(PROPERTY_XML_CONFIG_DIR);
        if(this.xmlConfigPath != null){
            File xmlConfigFile = new File(xmlConfigPath);
            if(!xmlConfigFile.exists()) {
                xmlConfigFile.mkdirs();
            }
        }
        this.oaiRequestPath = configurationProperties.getProperty(PROPERTY_OAI_REQUEST_DIR);
        if(this.oaiRequestPath != null){
            File oaiRequestFile = new File(oaiRequestPath);
            if(!oaiRequestFile.exists()) {
                oaiRequestFile.mkdirs();
            }
        }

        File tempDir = getTempDir();
        if(!tempDir.exists()) {
            tempDir.mkdirs();
        }

        this.administratorEmail = configurationProperties.getProperty(PROPERTY_ADMINISTRATOR_EMAIL);
        this.smtpServer = configurationProperties.getProperty(PROPERTY_SMTP_SERVER);
        this.defaultEmail = configurationProperties.getProperty(PROPERTY_DEFAULT_EMAIL);
        this.databasePath = configurationProperties.getProperty(PROPERTY_DB_DIR);
        this.databaseDriverClassName = configurationProperties.getProperty(PROPERTY_DB_DRIVER_CLASSNAME);
        this.databaseEmbeddedDriver = Boolean.parseBoolean(configurationProperties.getProperty(PROPERTY_DB_EMBEDDED_DRIVER));
        this.databaseUrl = configurationProperties.getProperty(PROPERTY_DB_URL);
        this.databaseCreate = Boolean.parseBoolean(configurationProperties.getProperty(PROPERTY_DB_CREATE));
        this.databaseUser = configurationProperties.getProperty(PROPERTY_DB_USER);
        this.databasePassword = configurationProperties.getProperty(PROPERTY_DB_PASSWORD);
        this.useSipDataBase = configurationProperties.getProperty(PROPERTY_USE_SIP_DB);
        if(this.useSipDataBase == null)
            this.useSipDataBase = "false";
        this.mailPassword = configurationProperties.getProperty(PROPERTY_MAIL_PASS);
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public String getBaseUrn() {
        return baseUrn;
    }

    public void setBaseUrn(String baseUrn) {
        this.baseUrn = baseUrn;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getXmlConfigPath() {
        return xmlConfigPath;
    }

    public void setXmlConfigPath(String xmlConfigPath) {
        this.xmlConfigPath = xmlConfigPath;
    }

    public String getOaiRequestPath() {
        return oaiRequestPath;
    }

    public void setOaiRequestPath(String oaiRequestPath) {
        this.oaiRequestPath = oaiRequestPath;
    }

    public String getAdministratorEmail() {
        return administratorEmail;
    }

    public void setAdministratorEmail(String administratorEmail) {
        this.administratorEmail = administratorEmail;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getDatabaseDriverClassName() {
        return databaseDriverClassName;
    }

    public void setDatabaseDriverClassName(String databaseDriverClassName) {
        this.databaseDriverClassName = databaseDriverClassName;
    }

    public boolean isDatabaseEmbeddedDriver() {
        return databaseEmbeddedDriver;
    }

    public void setDatabaseEmbeddedDriver(boolean databaseEmbeddedDriver) {
        this.databaseEmbeddedDriver = databaseEmbeddedDriver;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public boolean isDatabaseCreate() {
        return databaseCreate;
    }

    public void setDatabaseCreate(boolean databaseCreate) {
        this.databaseCreate = databaseCreate;
    }

    public File getTempDir() {
        return new File(xmlConfigPath, TEMP_DIRNAME);
    }

    public File getLogosDir() {
        return new File(repositoryPath, LOGOS_DIRNAME);
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public String getUseSipDataBase() {
        return useSipDataBase;
    }

    public void setUseSipDataBase(String useSipDataBase) {
        this.useSipDataBase = useSipDataBase;
    }

    public boolean isConfigurationFileOk(){
        return getXmlConfigPath() != null && getRepositoryPath() != null && getOaiRequestPath() != null && getDatabasePath() != null && getBaseUrn() != null;
    }
}
