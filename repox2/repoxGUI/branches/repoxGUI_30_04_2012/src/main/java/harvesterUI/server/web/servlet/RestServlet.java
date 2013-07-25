package harvesterUI.server.web.servlet;

import harvesterUI.server.ProjectType;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.MessageType;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.services.web.WebServices;
import pt.utl.ist.repox.services.web.impl.WebServicesImpl;
import pt.utl.ist.repox.services.web.impl.WebServicesImplEuropeana;
import pt.utl.ist.repox.services.web.rest.InvalidRequestException;
import pt.utl.ist.repox.services.web.rest.RestRequest;
import pt.utl.ist.repox.services.web.rest.RestUtils;
import pt.utl.ist.repox.util.*;
import pt.utl.ist.util.InvalidInputException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map.Entry;
import java.util.Properties;

public class RestServlet extends HttpServlet {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RestServlet.class);
    private static final String BASE_URI = "/rest";
    private static final String RECORDS_URL_NAME = "records";
    private static final String AGGREGATORS_URL_NAME = "aggregators";
    private static final String DPROVIDERS_URL_NAME = "dataProviders";
    private static final String DSOURCES_URL_NAME = "dataSources";

    private RepoxManager repoxManager;
    public static ProjectType projectType;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        Properties properties = PropertyUtil.loadCorrectedConfiguration("gui.properties");
        projectType = ProjectType.valueOf(properties.getProperty("project.type"));

//        if(projectType == ProjectType.LIGHT){
//            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
//            this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
//        } else if(projectType == ProjectType.EUDML){
//            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuDml());
//            this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
//        } else if(projectType == ProjectType.EUROPEANA){
            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuropeana());
            this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
//        }
    }

    @Override
    /**
     * Processes a request related with registers and writes to a given OutputStream
     * the response.
     * Processed requests:
     *
     * .records operations list (this list)
     *  http://[server]/rest/records
     * .View record with urn "1" in default schema
     *  http://[server]/rest/records/1/
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if(projectType == ProjectType.LIGHT ||
                    projectType == ProjectType.EUDML){
                WebServicesImpl webServicesLight = new WebServicesImpl();
                responseLight(request, response, webServicesLight);
            } else if(projectType == ProjectType.EUROPEANA){
                WebServicesImplEuropeana webServicesEuropeana = new WebServicesImplEuropeana();
                responseEuropeana(request, response, webServicesEuropeana);
            }

        } catch (Exception e) {
            log.error("Error in Rest GET request", e);
        }
    }

    private void responseLight(HttpServletRequest request, HttpServletResponse response, WebServicesImpl webServices) throws InvalidRequestException, IOException, DocumentException, ParseException, ClassNotFoundException, NoSuchMethodException, InvalidInputException, SQLException {
        RestRequest restRequest = RestUtils.processRequest(BASE_URI, request);
        response.setContentType("text/xml");
        ServletOutputStream out = response.getOutputStream();

        webServices.setRequestURI(request.getRequestURL().toString());

        // data providers
        if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(DPROVIDERS_URL_NAME)) {

            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getDataProviderOperationListLight(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Provider
                if(restRequest.getUriHierarchy().size() == 2) {

                    if(restRequest.getUriHierarchy().get(1).equals("list")) {
                        webServices.writeDataProviders(out);
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("create")) {
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");

                        if(name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty()){
                            webServices.createDataProvider(out, name, country, description);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                    "Data Provider: invalid arguments." +
                                    "Syntax: /rest/dataProviders/create?name=NAME" +
                                    "&description=DESCRIPTION&country=2_LETTERS_COUNTRY [mandatory fields: name, " +
                                    "country]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");

                        if(id != null && !id.isEmpty() &&
                                name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty()){
                            webServices.updateDataProvider(out, id, name, country, description);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/update?id=ID" +
                                    "&name=NAME&description=DESCRIPTION&country=2_LETTERS_COUNTRY [mandatory fields: " +
                                    "id, name, country]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.deleteDataProvider(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/delete?id=ID [mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataProvider")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.getDataProvider(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/getDataProvider?id=ID [mandatory " +
                                    "field: id]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }
        }
        // data sources
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(DSOURCES_URL_NAME)) {

            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getDataSourceOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Source
                if(restRequest.getUriHierarchy().size() == 2) {

                    if(restRequest.getUriHierarchy().get(1).equals("list")){
                        if(restRequest.getRequestParameters().size() == 0) {
                            webServices.writeDataSources(out);
                        }
                        else{
                            String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                            if (restRequest.getRequestParameters().size() == 1 &&
                                    dataProviderId != null && !dataProviderId.isEmpty()) {
                                webServices.writeDataSources(out, dataProviderId);
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error listing the" +
                                        "Data Source: invalid arguments." +
                                        "Syntax: /rest/dataSources/list?dataProviderId=DATA_PROVIDER_ID");
                            }
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createOai")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                oaiURL != null && !oaiURL.isEmpty()){

                            webServices.createDataSourceOai(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, oaiURL, !oaiSet.isEmpty() ? oaiSet : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET [Mandatory" +
                                    " fields: dataProviderId, id, description, schema, namespace, metadataFormat, " +
                                    "oaiURL]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950Timestamp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String earliestTimestamp = restRequest.getRequestParameters().get("earliestTimestamp");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                earliestTimestamp != null && !earliestTimestamp.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.createDataSourceZ3950Timestamp(out, dataProviderId, id, description, schema,
                                    namespace, address, port, database, user, password, recordSyntax, charset,
                                    earliestTimestamp, recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950Timestamp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, earliestTimestamp, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdList")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String filePath = restRequest.getRequestParameters().get("filePath");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                filePath != null && !filePath.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.createDataSourceZ3950IdList(out, dataProviderId, id, description,  schema,
                                    namespace, address, port, database, user, password, recordSyntax, charset,
                                    filePath, recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950IdList?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, filePath, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdSequence")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String maximumId = restRequest.getRequestParameters().get("maximumId");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                maximumId != null && !maximumId.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.createDataSourceZ3950IdSequence(out, dataProviderId, id, description, schema,
                                    namespace, address, port, database, user, password, recordSyntax, charset,
                                    maximumId, recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950IdSequence?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, maximumId, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createFtp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String server = restRequest.getRequestParameters().get("server");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String ftpPath = restRequest.getRequestParameters().get("ftpPath");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                server != null && !server.isEmpty() &&
                                ftpPath != null && !ftpPath.isEmpty()){

                            webServices.createDataSourceFtp(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri, recordXPath, server, (user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "", ftpPath);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFtp?dataProviderId=DATA_PROVIDER_ID&" +
                                    "id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory fields: dataProviderId, id, description, schema, " +
                                    "namespace, metadataFormat, recordIdPolicy, server, ftpPath (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createHttp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String url = restRequest.getRequestParameters().get("url");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                url != null && !url.isEmpty()){

                            webServices.createDataSourceHttp(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri, recordXPath, url);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createHttp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory fields: dataProviderId, id, " +
                                    "description, schema, namespace, metadataFormat, recordIdPolicy, url (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createFolder")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String folder = restRequest.getRequestParameters().get("folder");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                folder != null && !folder.isEmpty()){

                            webServices.createDataSourceFolder(out, dataProviderId, id, description, schema, namespace,
                                    metadataFormat, isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri, recordXPath, folder);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFolder?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "folder(Note: if recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix, " +
                                    "and namespaceUri are also mandatory and if metadataFormat=ISO2709 the fields " +
                                    "isoFormat and charset are also mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateOai")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                oaiURL != null && !oaiURL.isEmpty()){

                            webServices.updateDataSourceOai(out, id, description, schema, namespace, metadataFormat,
                                    oaiURL, !oaiSet.isEmpty() ? oaiSet : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the OAI" +
                                    "Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET " +
                                    "[Mandatory fields: id, description, schema, namespace, metadataFormat, " +
                                    "oaiURL]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950Timestamp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String earliestTimestamp = restRequest.getRequestParameters().get("earliestTimestamp");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                earliestTimestamp != null && !earliestTimestamp.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.updateDataSourceZ3950Timestamp(out, id, description, schema, namespace, address,
                                    port, database, user, password, recordSyntax, charset, earliestTimestamp,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                                    "Z39.50 Data Source with Time Stamp: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950Timestamp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, earliestTimestamp, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdList")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String filePath = restRequest.getRequestParameters().get("filePath");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                filePath != null && !filePath.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){
                            webServices.updateDataSourceZ3950IdList(out, id, description, schema, namespace, address,
                                    port, database, user, password, recordSyntax, charset, filePath, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                                    "Z39.50 Data Source with ID list: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950IdList?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, filePath, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdSequence")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String maximumId = restRequest.getRequestParameters().get("maximumId");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                maximumId != null && !maximumId.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.updateDataSourceZ3950IdSequence(out, id, description, schema, namespace,
                                    address, port, database, user, password, recordSyntax, charset, maximumId,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Z39.50" +
                                    "Data Source with ID Sequence: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950IdSequence?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, maximumId, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateFtp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String server = restRequest.getRequestParameters().get("server");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String ftpPath = restRequest.getRequestParameters().get("ftpPath");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                server != null && !server.isEmpty() &&
                                ftpPath != null && !ftpPath.isEmpty()){

                            webServices.updateDataSourceFtp(out, id, description, schema, namespace, metadataFormat,
                                    isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix, namespaceUri,
                                    recordXPath, server, (user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "",
                                    ftpPath);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the FTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFtp?dataProviderId=id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory fields: id, description, schema, namespace, " +
                                    "metadataFormat, recordIdPolicy, server, ftpPath (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateHttp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String url = restRequest.getRequestParameters().get("url");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                url != null && !url.isEmpty()){

                            webServices.updateDataSourceHttp(out, id, description, schema, namespace, metadataFormat,
                                    isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix, namespaceUri,
                                    recordXPath, url);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the HTTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateHttp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory fields: id, description, schema, " +
                                    "namespace, metadataFormat, recordIdPolicy, server, ftpPath (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateFolder")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String folder = restRequest.getRequestParameters().get("folder");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                folder != null && !folder.isEmpty()){

                            webServices.updateDataSourceFolder(out, id, description, schema, namespace, metadataFormat,
                                    isoFormat, charset, recordIdPolicy, idXpath, namespacePrefix, namespaceUri, recordXPath,
                                    folder);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Folder Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFolder?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory fields: id, description, " +
                                    "schema, namespace, metadataFormat, recordIdPolicy, folder (Note: if" +
                                    "recordIdPolicy=IdExtracted the fields: idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory and if metadataFormat=ISO2709 the fields isoFormat and charset " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.deleteDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/delete?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataSource")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.getDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/getDataSource?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("countRecords")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.countRecordsDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error counting records from" +
                                    " Data Source: invalid arguments. Syntax: /rest/dataSources/countRecords?id=ID");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("startIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String fullIngest = restRequest.getRequestParameters().get("fullIngest");
                        if(id != null && !id.isEmpty() &&
                                fullIngest != null && !fullIngest.isEmpty() &&
                                (fullIngest.equalsIgnoreCase("true") || fullIngest.equalsIgnoreCase("false"))){
                            webServices.startIngestDataSource(out, id, Boolean.valueOf(fullIngest));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source ingestion: invalid arguments. Syntax: /rest/dataSources/startIngest?id=ID&" +
                                    "fullIngest=BOOLEAN [Mandatory fields: id, fullIngest]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("stopIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.stopIngestDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error stopping the Data" +
                                    "Source ingestion: invalid arguments. Syntax: /rest/dataSources/stopIngest?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("scheduleIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String firstRunDate = restRequest.getRequestParameters().get("firstRunDate");
                        String firstRunHour = restRequest.getRequestParameters().get("firstRunHour");
                        String frequency = restRequest.getRequestParameters().get("frequency");
                        String xmonths = restRequest.getRequestParameters().get("xmonths");
                        String fullIngest = restRequest.getRequestParameters().get("fullIngest");
                        if(id != null && !id.isEmpty() &&
                                firstRunDate != null && !firstRunDate.isEmpty() &&
                                firstRunHour != null && !firstRunHour.isEmpty() &&
                                frequency != null && !frequency.isEmpty() &&
                                xmonths != null && !xmonths.isEmpty() &&
                                fullIngest != null && !fullIngest.isEmpty() &&
                                (fullIngest.equalsIgnoreCase("true") || fullIngest.equalsIgnoreCase("false"))){
                            webServices.scheduleIngestDataSource(out, id, firstRunDate, firstRunHour, frequency,
                                    xmonths, fullIngest);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error scheduling the" +
                                    "Data Source ingestion: invalid arguments. Syntax: /rest/dataSources/scheduleIngest?id=ID" +
                                    "&firstRunDate=YYYY-MM-DD&firstRunHour=HH:MM&frequency=ONCE_DAILY_WEEKLY_XMONTHLY" +
                                    "&xmonths=NUMBER&fullIngest=BOOLEAN [Mandatory fields: id, firstRunDate, firstRunHour, " +
                                    "frequency, xmonths, fullIngest]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("scheduleList")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.scheduleListDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source ingestion schedule: invalid arguments. Syntax: /rest/dataSources/scheduleList?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("harvestStatus")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.harvestStatusDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source harvest status: invalid arguments. Syntax: /rest/dataSources/harvestStatus?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("harvesting")) {
                        if(restRequest.getRequestParameters().size() == 0){
                            webServices.harvestingDataSources(out);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the list" +
                                    "of data sources harvesting: invalid arguments. Syntax: /rest/dataSources/harvesting");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("startExport")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String recordsPerFile = restRequest.getRequestParameters().get("recordsPerFile");
                        String metadataExportFormat = restRequest.getRequestParameters().get("metadataExportFormat");
                        if(id != null && !id.isEmpty() &&
                                recordsPerFile != null && !recordsPerFile.isEmpty()){
                            webServices.startExportDataSource(out, id, recordsPerFile, metadataExportFormat);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source exportation: invalid arguments. Syntax: /rest/dataSources/startExport?id=ID&" +
                                    "recordsPerFile=RECORDS_NUMBER&metadataExportFormat=METADATA_EXPORT_FORMAT [Mandatory fields: id, recordsPerFile]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("log")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.logDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source log: invalid arguments. Syntax: /rest/dataSources/log?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }
        }
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RECORDS_URL_NAME)) {
            // records
            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available records operations
                Element rootElement = getRecordsOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Source
                if(restRequest.getUriHierarchy().size() == 2) {
                    if(restRequest.getUriHierarchy().get(1).equals("getRecord")){
                        if(restRequest.getRequestParameters().get("recordId") != null){
                            webServices.getRecord(out, new Urn(restRequest.getRequestParameters().get("recordId")));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source log: invalid arguments. Syntax: /rest/records/getRecord?recordId=RECORD_ID");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("saveRecord")){
                        if(restRequest.getRequestParameters().get("recordId") != null &&
                                restRequest.getRequestParameters().get("dataSourceId") != null &&
                                restRequest.getRequestParameters().get("recordString") != null){

                            webServices.saveRecord(out, restRequest.getRequestParameters().get("recordId"),
                                    restRequest.getRequestParameters().get("dataSourceId"),
                                    restRequest.getRequestParameters().get("recordString"));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error saving record:" +
                                    "invalid arguments. Syntax: /rest/records/saveRecord?recordId=RECORD_ID" +
                                    "&dataSourceId=DATA_SOURCE_ID&recordString=RECORD_STRING");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("deleteRecord")){
                        if(restRequest.getRequestParameters().get("recordId") != null){
                            webServices.deleteRecord(out, restRequest.getRequestParameters().get("recordId"));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting record:" +
                                    "invalid arguments. Syntax: /rest/records/deleteRecord?recordId=RECORD_ID");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("eraseRecord")){
                        if(restRequest.getRequestParameters().get("recordId") != null){
                            webServices.eraseRecord(out, restRequest.getRequestParameters().get("recordId"));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error erasing record:" +
                                    "invalid arguments. Syntax: /rest/records/eraseRecord?recordId=RECORD_ID");
                        }
                    }
                }
            }
        }
        else{
            // list operations
            Element rootElement = DocumentHelper.createElement("repoxOperationsList");

            Element dataProviderOperations = getOperationElement("dataProviderOperationsList",
                    "Retrieve the list of the available operations over Data Providers",
                    restRequest,
                    "/rest/dataProviders");

            Element dataSourceOperations = getOperationElement("dataSourceOperationsList",
                    "Retrieve the list of the available operations over Data Sources",
                    restRequest,
                    "/rest/dataSources");

            Element recordsOperations = getOperationElement("recordOperationsList",
                    "Retrieve the list of the available operations over Records",
                    restRequest,
                    "/rest/records");

            rootElement.add(dataProviderOperations);
            rootElement.add(dataSourceOperations);
            rootElement.add(recordsOperations);

            RestUtils.writeRestResponse(out, rootElement);
        }
    }


    private void responseEuropeana(HttpServletRequest request, HttpServletResponse response, WebServicesImplEuropeana webServices) throws InvalidRequestException, IOException, DocumentException, ParseException, ClassNotFoundException, NoSuchMethodException, InvalidInputException, SQLException {
        RestRequest restRequest = RestUtils.processRequest(BASE_URI, request);
        response.setContentType("text/xml");
        ServletOutputStream out = response.getOutputStream();

        webServices.setRequestURI(request.getRequestURL().toString());

        // aggregators
        if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(AGGREGATORS_URL_NAME)) {
            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getAggregatorOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over an Aggregator
                if(restRequest.getUriHierarchy().size() == 2) {

                    if(restRequest.getUriHierarchy().get(1).equals("list")) {
                        webServices.writeAggregators(out);
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("create")) {
                        String name = restRequest.getRequestParameters().get("name");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String homepage = restRequest.getRequestParameters().get("homepage");

                        if(name != null && !name.isEmpty()){
                            webServices.createAggregator(out, name, nameCode, homepage);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/create?name=NAME" +
                                    "&nameCode=NAME_CODE&homepage=HOMEPAGE [mandatory fields: name]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String name = restRequest.getRequestParameters().get("name");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String homepage = restRequest.getRequestParameters().get("homepage");

                        if(id != null && !id.isEmpty() &&
                                name != null && !name.isEmpty()){
                            webServices.updateAggregator(out, id, name, nameCode, homepage);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating" +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/update?id=ID" +
                                    "&name=NAME&nameCode=NAME_CODE&homepage=HOMEPAGE [mandatory fields: id, name]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.deleteAggregator(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting " +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/delete?id=ID" +
                                    " [mandatory field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getAggregator")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.getAggregator(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving " +
                                    "Aggregator: invalid arguments. Syntax: /rest/aggregators/getAggregator?id=ID" +
                                    " [mandatory field: id]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }

        }
        // data providers
        if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(DPROVIDERS_URL_NAME)) {

            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getDataProviderOperationListEuropeana(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Provider
                if(restRequest.getUriHierarchy().size() == 2) {
                    if(restRequest.getUriHierarchy().get(1).equals("list")) {
                        if(restRequest.getRequestParameters().size() == 0) {
                            webServices.writeDataProviders(out);
                        }
                        else{
                            String aggregatorId = restRequest.getRequestParameters().get("aggregatorId");
                            if (restRequest.getRequestParameters().size() == 1 &&
                                    aggregatorId != null && !aggregatorId.isEmpty()) {
                                webServices.writeDataProviders(out, aggregatorId);
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error listing" +
                                        " Data Provider: invalid arguments." +
                                        "Syntax: /rest/dataProviders/list?aggregatorId=AGGREGATOR_ID");
                            }
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("create")) {
                        String aggregatorId = restRequest.getRequestParameters().get("aggregatorId");
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String url = restRequest.getRequestParameters().get("url");
                        String dataSetType = restRequest.getRequestParameters().get("dataSetType");
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");

                        if(aggregatorId != null && !aggregatorId.isEmpty() &&
                                name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty() &&
                                dataSetType != null && !dataSetType.isEmpty()){

                            if(dataProviderId.isEmpty()){
                                webServices.createDataProvider(out, aggregatorId, name, country, description, nameCode,
                                        url, dataSetType);
                            }
                            else{
                                webServices.createDataProvider(out, aggregatorId, dataProviderId, name, country,
                                        description, nameCode, url, dataSetType);
                            }
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                    "Data Provider: invalid arguments." +
                                    "Syntax: /rest/dataProviders/create?aggregatorId=AGGREGATOR_ID" +
                                    "&dataProviderId=DATA_PROVIDER_ID&name=NAME" +
                                    "&description=DESCRIPTION&country=2_LETTERS_COUNTRY&nameCode=NAME_CODE" +
                                    "&url=URL&dataSetType=DATA_SET_TYPE [mandatory fields: aggregatorId, name, " +
                                    "country, dataSetType]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String name = restRequest.getRequestParameters().get("name");
                        String country = restRequest.getRequestParameters().get("country");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String url = restRequest.getRequestParameters().get("url");
                        String dataSetType = restRequest.getRequestParameters().get("dataSetType");

                        if(id != null && !id.isEmpty() &&
                                name != null && !name.isEmpty() &&
                                country != null && !country.isEmpty() &&
                                dataSetType != null && !dataSetType.isEmpty()){
                            webServices.updateDataProvider(out, id, name, country, description, nameCode, url,
                                    dataSetType);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/update?id=ID" +
                                    "&name=NAME&description=DESCRIPTION&country=2_LETTERS_COUNTRY&nameCode=NAME_CODE" +
                                    "&url=URL&dataSetType=DATA_SET_TYPE [mandatory fields: " +
                                    "id, name, country, dataSetType]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.deleteDataProvider(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/delete?id=ID [mandatory " +
                                    "field: id");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataProvider")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.getDataProvider(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/getDataProvider?id=ID [mandatory " +
                                    "field: id");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("move")) {
                        String idDataProvider = restRequest.getRequestParameters().get("idDataProvider");
                        String idNewAggr = restRequest.getRequestParameters().get("idNewAggr");

                        if(idDataProvider != null && !idDataProvider.isEmpty() &&
                                idNewAggr != null && !idNewAggr.isEmpty()){
                            webServices.moveDataProvider(out, idDataProvider, idNewAggr);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error moving Data" +
                                    "Provider: invalid arguments. Syntax: /rest/dataProviders/move?idDataProvider=" +
                                    "ID_DATA_PROVIVER&idNewAggr=ID_NEW_AGGREGATOR [mandatory fields: " +
                                    "idDataProvider, idNewAggr]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }
        }
        // data sources
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(DSOURCES_URL_NAME)) {

            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available data providers operations
                Element rootElement = getDataSourceOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Source
                if(restRequest.getUriHierarchy().size() == 2) {
                    if(restRequest.getUriHierarchy().get(1).equals("list")){
                        if(restRequest.getRequestParameters().size() == 0) {
                            webServices.writeDataSources(out);
                        }
                        else{
                            String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                            if (restRequest.getRequestParameters().size() == 1 &&
                                    dataProviderId != null && !dataProviderId.isEmpty()) {
                                webServices.writeDataSources(out, dataProviderId);
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error listing the" +
                                        "Data Sources: invalid arguments." +
                                        "Syntax: /rest/dataSources/list?dataProviderId=DATA_PROVIDER_ID");
                            }
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createOai")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                oaiURL != null && !oaiURL.isEmpty()){

                            webServices.createDataSourceOai(out, dataProviderId, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, oaiURL,
                                    !oaiSet.isEmpty() ? oaiSet : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET" +
                                    " [Mandatory fields: dataProviderId, id, description, schema, namespace, metadataFormat, " +
                                    "oaiURL]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950Timestamp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String earliestTimestamp = restRequest.getRequestParameters().get("earliestTimestamp");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                earliestTimestamp != null && !earliestTimestamp.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.createDataSourceZ3950Timestamp(out, dataProviderId, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, earliestTimestamp, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950Timestamp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, earliestTimestamp, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdList")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String filePath = restRequest.getRequestParameters().get("filePath");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                filePath != null && !filePath.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.createDataSourceZ3950IdList(out, dataProviderId, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, filePath, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);

                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950IdList?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, filePath, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdSequence")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String maximumId = restRequest.getRequestParameters().get("maximumId");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                maximumId != null && !maximumId.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.createDataSourceZ3950IdSequence(out, dataProviderId, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, maximumId, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createZ3950IdSequence?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, maximumId, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createFtp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String server = restRequest.getRequestParameters().get("server");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String ftpPath = restRequest.getRequestParameters().get("ftpPath");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals("IdExtracted") ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                server != null && !server.isEmpty() &&
                                ftpPath != null && !ftpPath.isEmpty()){

                            webServices.createDataSourceFtp(out, dataProviderId, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, server, (user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "",
                                    ftpPath);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFtp?dataProviderId=DATA_PROVIDER_ID&" +
                                    "id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "server, ftpPath (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createHttp")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String url = restRequest.getRequestParameters().get("url");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals("IdExtracted") ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                url != null && !url.isEmpty()){

                            webServices.createDataSourceHttp(out, dataProviderId, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, url);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createHttp?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "url (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("createFolder")) {
                        String dataProviderId = restRequest.getRequestParameters().get("dataProviderId");
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String folder = restRequest.getRequestParameters().get("folder");

                        if(dataProviderId != null && !dataProviderId.isEmpty() &&
                                id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals("IdExtracted") ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                folder != null && !folder.isEmpty()){

                            webServices.createDataSourceFolder(out, dataProviderId, id, description, nameCode,
                                    name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri, recordXPath, folder);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                    "Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/createFolder?dataProviderId=DATA_PROVIDER_ID" +
                                    "&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory fields: " +
                                    "dataProviderId, id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "folder (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateOai")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String oaiURL = restRequest.getRequestParameters().get("oaiURL");
                        String oaiSet = restRequest.getRequestParameters().get("oaiSet");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                oaiURL != null && !oaiURL.isEmpty()){

                            webServices.updateDataSourceOai(out, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, oaiURL,
                                    !oaiSet.isEmpty() ? oaiSet : null);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the OAI" +
                                    "Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET " +
                                    "[Mandatory fields: id, description, schema, namespace, metadataFormat, " +
                                    "oaiURL]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950Timestamp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String earliestTimestamp = restRequest.getRequestParameters().get("earliestTimestamp");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                earliestTimestamp != null && !earliestTimestamp.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){


                            webServices.updateDataSourceZ3950Timestamp(out, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, earliestTimestamp, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }

                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                                    "Z39.50 Data Source with Time Stamp: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950Timestamp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, earliestTimestamp, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdList")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String filePath = restRequest.getRequestParameters().get("filePath");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                filePath != null && !filePath.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){
                            webServices.updateDataSourceZ3950IdList(out, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, filePath, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the" +
                                    "Z39.50 Data Source with ID list: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950IdList?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, filePath, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdSequence")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String address = restRequest.getRequestParameters().get("address");
                        String port = restRequest.getRequestParameters().get("port");
                        String database = restRequest.getRequestParameters().get("database");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String recordSyntax = restRequest.getRequestParameters().get("recordSyntax");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String maximumId = restRequest.getRequestParameters().get("maximumId");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                address != null && !address.isEmpty() &&
                                port != null && !port.isEmpty() &&
                                database != null && !database.isEmpty() &&
                                recordSyntax != null && !recordSyntax.isEmpty() &&
                                charset != null && !charset.isEmpty() &&
                                maximumId != null && !maximumId.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty()){

                            webServices.updateDataSourceZ3950IdSequence(out, id, description, nameCode,
                                    name, exportPath, schema, namespace, address, port, database, user, password,
                                    recordSyntax, charset, maximumId, recordIdPolicy, idXpath, namespacePrefix,
                                    namespaceUri);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Z39.50" +
                                    "Data Source with ID Sequence: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateZ3950IdSequence?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                    "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID" +
                                    "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI [Mandatory fields: " +
                                    "id, description, schema, namespace, address, port, database, " +
                                    "recordSyntax, charset, maximumId, recordIdPolicy (if " +
                                    "recordIdPolicy=IdExtracted the fields idXpath, namespacePrefix and namespaceUri " +
                                    "are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateFtp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String server = restRequest.getRequestParameters().get("server");
                        String user = restRequest.getRequestParameters().get("user");
                        String password = restRequest.getRequestParameters().get("password");
                        String ftpPath = restRequest.getRequestParameters().get("ftpPath");

                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals("IdExtracted") ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                server != null && !server.isEmpty() &&
                                ftpPath != null && !ftpPath.isEmpty()){

                            webServices.updateDataSourceFtp(out, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, server,
                                    (user != null && !user.isEmpty()) ? user : "",
                                    (password != null && !password.isEmpty()) ? password : "",
                                    ftpPath);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the FTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFtp?dataProviderId=id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD" +
                                    "&ftpPath=FTP_PATH [Mandatory fields: " +
                                    "id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "server, ftpPath (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateHttp")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String url = restRequest.getRequestParameters().get("url");
                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals("IdExtracted") ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                url != null && !url.isEmpty()){

                            webServices.updateDataSourceHttp(out, id, description, nameCode, name,
                                    exportPath, schema, namespace, metadataFormat, isoFormat, charset, recordIdPolicy,
                                    idXpath, namespacePrefix, namespaceUri, recordXPath, url);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the HTTP Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateHttp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&url=URL [Mandatory fields: " +
                                    "id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "server, ftpPath (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("updateFolder")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String description = restRequest.getRequestParameters().get("description");
                        String nameCode = restRequest.getRequestParameters().get("nameCode");
                        String name = restRequest.getRequestParameters().get("name");
                        String exportPath = restRequest.getRequestParameters().get("exportPath");
                        String schema = restRequest.getRequestParameters().get("schema");
                        String namespace = restRequest.getRequestParameters().get("namespace");
                        String metadataFormat = restRequest.getRequestParameters().get("metadataFormat");
                        String isoFormat = restRequest.getRequestParameters().get("isoFormat");
                        String charset = restRequest.getRequestParameters().get("charset");
                        String recordIdPolicy = restRequest.getRequestParameters().get("recordIdPolicy");
                        String idXpath = restRequest.getRequestParameters().get("idXpath");
                        String namespacePrefix = restRequest.getRequestParameters().get("namespacePrefix");
                        String namespaceUri = restRequest.getRequestParameters().get("namespaceUri");
                        String recordXPath = restRequest.getRequestParameters().get("recordXPath");
                        String folder = restRequest.getRequestParameters().get("folder");
                        if(id != null && !id.isEmpty() &&
                                description != null && !description.isEmpty() &&
                                schema != null && !schema.isEmpty() &&
                                namespace != null && !namespace.isEmpty() &&
                                metadataFormat != null && !metadataFormat.isEmpty() &&
                                recordIdPolicy != null && !recordIdPolicy.isEmpty() &&
                                (!recordIdPolicy.equals("IdExtracted") ||
                                        (idXpath != null && !idXpath.isEmpty() &&
                                                namespacePrefix != null && !namespacePrefix.isEmpty() &&
                                                namespaceUri != null && !namespaceUri.isEmpty())) &&
                                folder != null && !folder.isEmpty()){

                            webServices.updateDataSourceFolder(out, id, description, nameCode,
                                    name, exportPath, schema, namespace, metadataFormat, isoFormat, charset,
                                    recordIdPolicy, idXpath, namespacePrefix, namespaceUri, recordXPath, folder);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Folder Data Source: invalid arguments." +
                                    "Syntax: /rest/dataSources/updateFolder?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                    "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                    "&schema=SCHEMA&namespace=NAMESPACE" +
                                    "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                    "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                    "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                    "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH [Mandatory fields: " +
                                    "id, description, schema, namespace, metadataFormat, recordIdPolicy, " +
                                    "folder (Note: if recordIdPolicy=IdExtracted the fields: idXpath," +
                                    "namespacePrefix and namespaceUri are mandatory and if metadataFormat=ISO2709 " +
                                    "the fields isoFormat and charset are mandatory)]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.deleteDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/delete?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("getDataSource")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.getDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error retrieving Data" +
                                    "Source: invalid arguments. Syntax: /rest/dataSources/getDataSource?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("countRecords")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.countRecordsDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error counting records from" +
                                    " Data Source: invalid arguments. Syntax: /rest/dataSources/countRecords?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("startIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String fullIngest = restRequest.getRequestParameters().get("fullIngest");
                        if(id != null && !id.isEmpty() &&
                                fullIngest != null && !fullIngest.isEmpty() &&
                                (fullIngest.equalsIgnoreCase("true") || fullIngest.equalsIgnoreCase("false"))){
                            webServices.startIngestDataSource(out, id, Boolean.valueOf(fullIngest));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source ingestion: invalid arguments. Syntax: /rest/dataSources/startIngest?id=ID&" +
                                    "fullIngest=BOOLEAN [Mandatory fields: id, fullIngest]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("stopIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.stopIngestDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error stopping the Data" +
                                    "Source ingestion: invalid arguments. Syntax: /rest/dataSources/stopIngest?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("scheduleIngest")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String firstRunDate = restRequest.getRequestParameters().get("firstRunDate");
                        String firstRunHour = restRequest.getRequestParameters().get("firstRunHour");
                        String frequency = restRequest.getRequestParameters().get("frequency");
                        String xmonths = restRequest.getRequestParameters().get("xmonths");
                        String fullIngest = restRequest.getRequestParameters().get("fullIngest");

                        if(id != null && !id.isEmpty() &&
                                firstRunDate != null && !firstRunDate.isEmpty() &&
                                firstRunHour != null && !firstRunHour.isEmpty() &&
                                frequency != null && !frequency.isEmpty() &&
                                xmonths != null && !xmonths.isEmpty() &&
                                fullIngest != null && !fullIngest.isEmpty() &&
                                (fullIngest.equalsIgnoreCase("true") || fullIngest.equalsIgnoreCase("false"))){
                            webServices.scheduleIngestDataSource(out, id, firstRunDate, firstRunHour, frequency,
                                    xmonths, fullIngest);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error scheduling the" +
                                    "Data Source ingestion: invalid arguments. Syntax: /rest/dataSources/scheduleIngest?id=ID" +
                                    "&firstRunDate=YYYY-MM-DD&firstRunHour=HH:MM&frequency=ONCE_DAILY_WEEKLY_XMONTHLY" +
                                    "&xmonths=NUMBER&fullIngest=BOOLEAN [Mandatory fields: id, firstRunDate, firstRunHour, " +
                                    "frequency, xmonths, fullIngest]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("scheduleList")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.scheduleListDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source ingestion schedule: invalid arguments. Syntax: /rest/dataSources/" +
                                    "scheduleList?id=ID [Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("harvestStatus")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.harvestStatusDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source harvest status: invalid arguments. Syntax: /rest/dataSources/harvestStatus?id=ID " +
                                    "[Mandatory field: id]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("harvesting")) {
                        if(restRequest.getRequestParameters().size() == 0){
                            webServices.harvestingDataSources(out);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the list" +
                                    "of data sources harvesting: invalid arguments. Syntax: /rest/dataSources/harvesting");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("startExport")) {
                        String id = restRequest.getRequestParameters().get("id");
                        String recordsPerFile = restRequest.getRequestParameters().get("recordsPerFile");
                        String metadataExportFormat = restRequest.getRequestParameters().get("metadataExportFormat");
                        if(id != null && !id.isEmpty() &&
                                recordsPerFile != null && !recordsPerFile.isEmpty()){
                            webServices.startExportDataSource(out, id, recordsPerFile, metadataExportFormat);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                    "Source exportation: invalid arguments. Syntax: /rest/dataSources/startExport?id=ID&" +
                                    "recordsPerFile=RECORDS_NUMBER&metadataExportFormat=METADATA_EXPORT_FORMAT [Mandatory fields: id, recordsPerFile]");
                        }
                    }

                    else if(restRequest.getUriHierarchy().get(1).equals("log")) {
                        String id = restRequest.getRequestParameters().get("id");
                        if(id != null && !id.isEmpty()){
                            webServices.logDataSource(out, id);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source log: invalid arguments. Syntax: /rest/dataSources/log?id=ID [Mandatory " +
                                    "field: id]");
                        }
                    }
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }
            }
        }
        else if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
                && restRequest.getUriHierarchy().get(0).equals(RECORDS_URL_NAME)) {
            // records
            if(restRequest.getUriHierarchy().size() == 1) {
                //list all available records operations
                Element rootElement = getRecordsOperationList(restRequest);
                RestUtils.writeRestResponse(out, rootElement);
            }
            else { // operation over a Data Source
                if(restRequest.getUriHierarchy().size() == 2) {
                    if(restRequest.getUriHierarchy().get(1).equals("getRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        if(recordId != null && !recordId.isEmpty()){
                            webServices.getRecord(out, new Urn(recordId));
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                    "Source log: invalid arguments. Syntax: /rest/records/getRecord?recordId=RECORD_ID" +
                                    " [Mandatory field: recordId]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("saveRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        String dataSourceId = restRequest.getRequestParameters().get("dataSourceId");
                        String recordString = restRequest.getRequestParameters().get("recordString");
                        if(recordId != null && !recordId.isEmpty() &&
                                dataSourceId != null && !dataSourceId.isEmpty() &&
                                recordString != null && !recordString.isEmpty()){

                            webServices.saveRecord(out, recordId, dataSourceId, recordString);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error saving record:" +
                                    "invalid arguments. Syntax: /rest/records/saveRecord?recordId=RECORD_ID" +
                                    "&dataSourceId=DATA_SOURCE_ID&recordString=RECORD_STRING [Mandatory fields: " +
                                    "recordId, dataSourceId, recordString]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("deleteRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        if(recordId != null && !recordId.isEmpty()){
                            webServices.deleteRecord(out, recordId);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting record:" +
                                    "invalid arguments. Syntax: /rest/records/deleteRecord?recordId=RECORD_ID " +
                                    "[Mandatory field: recordId]");
                        }
                    }
                    else if(restRequest.getUriHierarchy().get(1).equals("eraseRecord")){
                        String recordId = restRequest.getRequestParameters().get("recordId");
                        if(recordId != null && !recordId.isEmpty()){
                            webServices.eraseRecord(out, recordId);
                        }
                        else{
                            webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error erasing record:" +
                                    "invalid arguments. Syntax: /rest/records/eraseRecord?recordId=RECORD_ID " +
                                    "[Mandatory field: recordId]");
                        }
                    }
                }
            }

        }
        else{
            // list operations
            Element rootElement = DocumentHelper.createElement("repoxOperationsList");

            Element aggregatorOperations = getOperationElement("aggregatorOperationsList",
                    "Retrieve the list of the available operations over Aggregators",
                    restRequest,
                    "/rest/aggregators");

            Element dataProviderOperations = getOperationElement("dataProviderOperationsList",
                    "Retrieve the list of the available operations over Data Providers",
                    restRequest,
                    "/rest/dataProviders");

            Element dataSourceOperations = getOperationElement("dataSourceOperationsList",
                    "Retrieve the list of the available operations over Data Sources",
                    restRequest,
                    "/rest/dataSources");

            Element recordsOperations = getOperationElement("recordOperationsList",
                    "Retrieve the list of the available operations over Records",
                    restRequest,
                    "/rest/records");

            rootElement.add(aggregatorOperations);
            rootElement.add(dataProviderOperations);
            rootElement.add(dataSourceOperations);
            rootElement.add(recordsOperations);

            RestUtils.writeRestResponse(out, rootElement);
        }
    }


    @Override
    /**
     * Processes a post related with registers and writes to a given OutputStream
     * the response.
     * Processed requests:
     *
     * .Save new record to dataSource
     *  http://[server]/rest/records/
     *  Parameters: operation - save; dataSourceId - the DataSource Id; record - the record String
     *
     * .Mark record as deleted from dataSource
     *  http://[server]/rest/records/
     *  Parameters: operation - delete; dataSourceId - the DataSource Id; recordId - the record Id
     *
     * .Permanently remove record from dataSource
     *  http://[server]/rest/records/
     *  Parameters: operation - erase; dataSourceId - the DataSource Id; recordId - the record Id
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            WebServices webServices = null;
            if(projectType == ProjectType.LIGHT){
                webServices = new WebServicesImpl();
            } else if(projectType == ProjectType.EUDML){
                webServices = new WebServicesImpl();
            } else if(projectType == ProjectType.EUROPEANA){
                webServices = new WebServicesImplEuropeana();
            }


            RestRequest restRequest = RestUtils.processRequest(BASE_URI, request);
            response.setContentType("text/xml");
            ServletOutputStream out = response.getOutputStream();

            if((restRequest.getUriHierarchy() != null) && restRequest.getUriHierarchy().size() == 1
                    && restRequest.getUriHierarchy().get(0).equals(RECORDS_URL_NAME)) {
                String operationParameter = restRequest.getRequestParameters().get("operation");
                String recordParameter = restRequest.getRequestParameters().get("record");
                String recordIdParameter = restRequest.getRequestParameters().get("recordId");

                if(operationParameter != null && operationParameter.equals("save")
                        && recordParameter != null && !recordParameter.trim().isEmpty()) { // Save Record
                    String dataSourceId = restRequest.getRequestParameters().get("dataSourceId");

                    boolean validParameters = validateSaveParameters(restRequest, out, dataSourceId, operationParameter, recordIdParameter);

                    if(!validParameters) {
                        return;
                    }

                    webServices.saveRecord(out, dataSourceId, recordParameter, recordIdParameter);
                }
                else if(operationParameter != null && operationParameter.equals("delete")
                        && validateRecordId(restRequest, out, recordIdParameter)) { // Mark record as deleted
                    webServices.deleteRecord(out, recordIdParameter);
                }
                else if(operationParameter != null && operationParameter.equals("erase")
                        && validateRecordId(restRequest, out, recordIdParameter)) { // Permanently remove record
                    webServices.eraseRecord(out, recordIdParameter);
                }
                else {
                    RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
                }

            }
            else {
                RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), response.getOutputStream());
            }

        } catch (Exception e) {
            log.error("Error in Rest POST request", e);
        }
    }

    /**
     * @param restRequest
     * @return a simple xml report of the REST request
     */
    private String xmlDebugRequest(RestRequest restRequest) {
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        xmlResponse.append("<xml><response>");

        xmlResponse.append("<parsedRequestURI>" + restRequest.getParsedRequestURI() + "</parsedRequestURI>");

        for (String currentURIDir : restRequest.getUriHierarchy()) {
            xmlResponse.append("<uriDir>" + currentURIDir + "</uriDir>");
        }

        if(restRequest.getRequestParameters() != null) {
            for (Entry<String, String> stringStringEntry : restRequest.getRequestParameters().entrySet()) {
                Entry<String, String> elem = stringStringEntry;
                xmlResponse.append("<requestParameter>");
                xmlResponse.append("<currentKey>" + elem.getKey() + "</currentKey>");
                xmlResponse.append("<currentValue>" + elem.getValue() + "</currentValue>");
                xmlResponse.append("</requestParameter>");
            }
        }

        xmlResponse.append("</response></xml>");

        return xmlResponse.toString();
    }

    private boolean validateOperationParameter(RestRequest restRequest, OutputStream out, String operationParameter)
            throws IOException, DocumentException {
        if(operationParameter == null || operationParameter.trim().isEmpty()) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), "operationParameter is mandatory ", out);
            return false;
        }

        return true;
    }

    private boolean validateDataSource(RestRequest restRequest, OutputStream out, String dataSourceId)
            throws IOException, DocumentException {
        DataSource dataSource = repoxManager.getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
        if(dataSource == null) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), "Data Source is invalid", out);
            return false;
        }

        return true;
    }

    private boolean validateRecordId(RestRequest restRequest, OutputStream out, String recordIdParameter)
            throws IOException, DocumentException {
        if(recordIdParameter == null || recordIdParameter.trim().isEmpty()) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), "recordId Parameter is mandatory ", out);
            return false;
        }

        return true;
    }

    private boolean validateRecordUrn(RestRequest restRequest, ServletOutputStream out, String recordUrnString)
            throws IOException, DocumentException, SQLException {
        Urn recordUrn;

        try {
            recordUrn = new Urn(recordUrnString);
        }
        catch (Exception e) {
            return false;
        }

        return !(!validateDataSource(restRequest, out, recordUrn.getDataSourceId())
                || !validateRecordId(restRequest, out, recordUrnString)
                || ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(recordUrn) == null);

    }

    private boolean validateSaveParameters(RestRequest restRequest, OutputStream out, String dataSourceId,
                                           String operationParameter, String recordIdParameter) throws IOException, DocumentException {
        if(!validateOperationParameter(restRequest, out, operationParameter)) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(),
                    "operationParameter is mandatory for this operation: " + operationParameter,
                    out);
            return false;
        }
        else if(!validateDataSource(restRequest, out, dataSourceId)) {
            RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(),
                    "dataSourceId parameter is mandatory for this operation: " + operationParameter,
                    out);
            return false;
        }
        else {
            DataSource dataSource = repoxManager.getDataManager().getDataSourceContainer(dataSourceId).getDataSource();

            if(dataSource.getRecordIdPolicy() instanceof IdProvided
                    && !validateRecordId(restRequest, out, recordIdParameter)) {
                RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(),
                        "recordId parameter is mandatory for this operation: " + operationParameter,
                        out);
                return false;
            }
        }

        return true;
    }


    /**
     * Retrieve the list of the available operations over Aggregators
     * @param restRequest
     * @return
     */
    private Element getAggregatorOperationList(RestRequest restRequest) {
        Element aggregatorOperations = getOperationElement("dataProviderOperationsList",
                "Retrieve the list of the available operations over Aggregators",
                restRequest,
                "/rest/aggregators");

        Element aggregatorList = getOperationElement("aggregators",
                "Retrieve all available Aggregators",
                restRequest,
                "/rest/aggregators/list");

        Element aggregatorCreate = getOperationElement("aggregators",
                "Creates an Aggregator",
                restRequest,
                "/rest/aggregators/create");

        Element aggregatorUpdate = getOperationElement("aggregators",
                "Updates an Aggregator",
                restRequest,
                "/rest/aggregators/update");

        Element aggregatorDelete = getOperationElement("aggregators",
                "Deletes an Aggregator",
                restRequest,
                "/rest/aggregators/delete");

        Element aggregatorGet = getOperationElement("aggregators",
                "Gets an Aggregator",
                restRequest,
                "/rest/aggregators/getAggregator");

        Element rootElement = DocumentHelper.createElement("aggregatorOperationsList");
        rootElement.add(aggregatorOperations);
        rootElement.add(aggregatorList);
        rootElement.add(aggregatorCreate);
        rootElement.add(aggregatorUpdate);
        rootElement.add(aggregatorDelete);
        rootElement.add(aggregatorGet);
        return rootElement;
    }

    /**
     * Retrieve the list of the available operations over Data Providers
     * @param restRequest
     * @return
     */
    private Element getDataProviderOperationListLight(RestRequest restRequest) {
        Element dataProviderOperations = getOperationElement("dataProviderOperationsList",
                "Retrieve the list of the available operations over Data Providers",
                restRequest,
                "/rest/dataProviders");

        Element dataProviderList = getOperationElement("dataProviders",
                "Retrieve all available Data Providers",
                restRequest,
                "/rest/dataProviders/list");

        Element dataProviderCreate = getOperationElement("dataProviders",
                "Creates a Data Provider",
                restRequest,
                "/rest/dataProviders/create");

        Element dataProviderUpdate = getOperationElement("dataProviders",
                "Updates a Data Provider",
                restRequest,
                "/rest/dataProviders/update");

        Element dataProviderDelete = getOperationElement("dataProviders",
                "Deletes a Data Provider",
                restRequest,
                "/rest/dataProviders/delete");
        Element dataProviderGet = getOperationElement("dataProviders",
                "Gets a Data Provider",
                restRequest,
                "/rest/dataProviders/getDataProvider");

        Element rootElement = DocumentHelper.createElement("dataProviderOperationsList");
        rootElement.add(dataProviderOperations);
        rootElement.add(dataProviderList);
        rootElement.add(dataProviderCreate);
        rootElement.add(dataProviderUpdate);
        rootElement.add(dataProviderDelete);
        rootElement.add(dataProviderGet);
        return rootElement;
    }


    private Element getDataProviderOperationListEuropeana(RestRequest restRequest) {
        Element dataProviderOperations = getOperationElement("dataProviderOperationsList",
                "Retrieve the list of the available operations over Data Providers",
                restRequest,
                "/rest/dataProviders");

        Element dataProviderList = getOperationElement("dataProviders",
                "Retrieve all available Data Providers",
                restRequest,
                "/rest/dataProviders/list");

        Element dataSourceFromDataProviderList = getOperationElement("dataSources",
                "Retrieve all available Data Providers from a specific Aggregator",
                restRequest,
                "/rest/dataProviders/list?aggregatorId=AGGREGATOR_ID");

        Element dataProviderCreate = getOperationElement("dataProviders",
                "Creates a Data Provider",
                restRequest,
                "/rest/dataProviders/create");

        Element dataProviderUpdate = getOperationElement("dataProviders",
                "Updates a Data Provider",
                restRequest,
                "/rest/dataProviders/update");

        Element dataProviderDelete = getOperationElement("dataProviders",
                "Deletes a Data Provider",
                restRequest,
                "/rest/dataProviders/delete");

        Element dataProviderGet = getOperationElement("dataProviders",
                "Gets a Data Provider",
                restRequest,
                "/rest/dataProviders/getDataProvider");

        Element dataProviderMove = getOperationElement("dataProviders",
                "Moves a Data Provider from aggregator",
                restRequest,
                "/rest/dataProviders/move");

        Element rootElement = DocumentHelper.createElement("dataProviderOperationsList");
        rootElement.add(dataProviderOperations);
        rootElement.add(dataProviderList);
        rootElement.add(dataSourceFromDataProviderList);
        rootElement.add(dataProviderCreate);
        rootElement.add(dataProviderUpdate);
        rootElement.add(dataProviderDelete);
        rootElement.add(dataProviderMove);
        rootElement.add(dataProviderGet);
        return rootElement;
    }


    /**
     * Retrieve the list of the available operations over Data Providers
     * @param restRequest
     * @return
     */
    private Element getDataSourceOperationList(RestRequest restRequest) {
        Element dataSourceOperations = getOperationElement("dataSourceOperationsList",
                "Retrieve the list of the available operations over Data Sources",
                restRequest,
                "/rest/dataSources");

        Element dataSourceList = getOperationElement("dataSources",
                "Retrieves all available Data Sources",
                restRequest,
                "/rest/dataSources/list");

        Element dataSourceFromDataProviderList = getOperationElement("dataSources",
                "Retrieve all available Data Sources from a specific Data Provider",
                restRequest,
                "/rest/dataSources/list?dataProviderId=DATA_PROVIDER_ID");

        Element dataSourceCreateOai = getOperationElement("dataSources",
                "Creates a Data Source from OAI-PMH",
                restRequest,
                "/rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                        "&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER" +
                        "&oaiSet=OAI_SET");

        Element dataSourceCreateZ3950IdList = getOperationElement("dataSources",
                "Creates a Data Source from Z39.50 with Id List",
                restRequest,
                "/rest/dataSources/createZ3950IdList?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&address=ADDRESS&port=PORT" +
                        "&database=DATABASE&user=USER&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET" +
                        "&filePath=FILE_PATH");

        Element dataSourceCreateZ3950Timestamp = getOperationElement("dataSources",
                "Creates a Data Source from Z39.50 with Timestamp",
                restRequest,
                "/rest/dataSources/createZ3950Timestamp?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&address=ADDRESS&port=PORT" +
                        "&database=DATABASE&user=USER&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET" +
                        "&earliestTimestamp=DATE(YYYYMMDD)");

        Element dataSourceCreateZ3950IdSequence = getOperationElement("dataSources",
                "Creates a Data Source from Z39.50 with Id Sequence",
                restRequest,
                "/rest/dataSources/createZ3950IdSequence?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&address=ADDRESS&port=PORT" +
                        "&database=DATABASE&user=USER&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET" +
                        "&maximumId=MAXIMUM_ID");

        Element dataSourceCreateFtp = getOperationElement("dataSources",
                "Creates a Data Source from FTP server",
                restRequest,
                "/rest/dataSources/createFtp?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT" +
                        "&isoFormat=ISO_FORMAT&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY" +
                        "&idXpath=ID_XPATH&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&" +
                        "recordXPath=RECORDS_XPATH&server=SERVER&user=USER&password=PASSWORD&ftpPath=FTP_PATH");

        Element dataSourceCreateHttp = getOperationElement("dataSources",
                "Creates a Data Source from HTTP server",
                restRequest,
                "/rest/dataSources/createHttp?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID&description=DESCRIPTION" +
                        "&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                        "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH&url=URL");

        Element dataSourceCreateFolder = getOperationElement("dataSources",
                "Creates a Data Source from Folder",
                restRequest,
                "/rest/dataSources/createFolder?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT" +
                        "&isoFormat=ISO_FORMAT&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH" +
                        "&folder=FOLDER_PATH");

        Element dataSourceUpdateOai = getOperationElement("dataSources",
                "Updates an OAI-PMH Data Source",
                restRequest,
                "/rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET");

        Element dataSourceUpdateZ3950IdList = getOperationElement("dataSources",
                "Updates a Z39.50 Data Source with Id List",
                restRequest,
                "/rest/dataSources/updateZ3950IdList?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                        "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&filePath=FILE_PATH");

        Element dataSourceUpdateZ3950Timestamp = getOperationElement("dataSources",
                "Updates a Z39.50 Data Source with Timestamp",
                restRequest,
                "/rest/dataSources/updateZ3950Timestamp?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                        "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&earliestTimestamp=DATE(YYYYMMDD)");

        Element dataSourceUpdateZ3950IdSequence = getOperationElement("dataSources",
                "Updates a Z39.50 Data Source with Id Sequence",
                restRequest,
                "/rest/dataSources/updateZ3950IdSequence?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                        "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID");

        Element dataSourceUpdateFtp = getOperationElement("dataSources",
                "Updates a FTP Data Source",
                restRequest,
                "/rest/dataSources/updateDataSourceFtp?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT&charset=CHAR_SET" +
                        "&password=PASSWORD&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&recordIdPolicy=RECORD_ID_POLICY" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH" +
                        "&server=SERVER&user=USER&password=PASSWORD&ftpPath=FTP_PATH");

        Element dataSourceUpdateHttp = getOperationElement("dataSources",
                "Updates a HTTP Data Source",
                restRequest,
                "/rest/dataSources/updateHttp?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                        "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                        "&recordXPath=RECORDS_XPATH&url=URL");

        Element dataSourceUpdateFolder = getOperationElement("dataSources",
                "Updates a Folder Data Source",
                restRequest,
                "/rest/dataSources/updateFolder?id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA" +
                        "&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT&charset=CHAR_SET" +
                        "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH&namespacePrefix=NAMESPACE_PREFIX" +
                        "&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH");


        Element dataSourceDelete = getOperationElement("dataSources",
                "Deletes a Data Source",
                restRequest,
                "/rest/dataSources/delete");

        Element dataSourceGet = getOperationElement("dataSources",
                "Gets a Data Source",
                restRequest,
                "/rest/dataSources/getDataSource");

        Element dataSourceStartIngest = getOperationElement("dataSources",
                "Starts the Data Source ingest",
                restRequest,
                "/rest/dataSources/startIngest");

        Element dataSourceCountRecords = getOperationElement("dataSources",
                "Counts the number of records",
                restRequest,
                "/rest/dataSources/countRecords");

        Element dataSourceStopIngest = getOperationElement("dataSources",
                "Stops the Data Source ingest",
                restRequest,
                "/rest/dataSources/stopIngest");

        Element dataSourceScheduleIngest = getOperationElement("dataSources",
                "Schedules a Data Source ingest",
                restRequest,
                "/rest/dataSources/scheduleIngest");

        Element dataSourceScheduleList = getOperationElement("dataSources",
                "Retrieves the list of all Data Source schedules",
                restRequest,
                "/rest/dataSources/scheduleList");

        Element dataSourceHarvesting = getOperationElement("dataSources",
                "Retrieves the list of all harvesting Data Sources",
                restRequest,
                "/rest/dataSources/harvesting");

        Element dataSourceStartExport = getOperationElement("dataSources",
                "Starts the Data Source export",
                restRequest,
                "/rest/dataSources/startExport");

        Element dataSourcesHarvestStatus = getOperationElement("dataSources",
                "Retrieves the Data Source status",
                restRequest,
                "/rest/dataSources/harvestStatus");

        Element dataSourceLog = getOperationElement("dataSources",
                "Retrieves the last Data Source ingest log",
                restRequest,
                "/rest/dataSources/log");


        Element rootElement = DocumentHelper.createElement("dataSourceOperationsList");
        rootElement.add(dataSourceOperations);
        rootElement.add(dataSourceList);
        rootElement.add(dataSourceFromDataProviderList);
        rootElement.add(dataSourceCreateOai);
        rootElement.add(dataSourceCreateZ3950Timestamp);
        rootElement.add(dataSourceCreateZ3950IdList);
        rootElement.add(dataSourceCreateZ3950IdSequence);
        rootElement.add(dataSourceCreateFtp);
        rootElement.add(dataSourceCreateHttp);
        rootElement.add(dataSourceCreateFolder);
        rootElement.add(dataSourceUpdateOai);
        rootElement.add(dataSourceUpdateZ3950Timestamp);
        rootElement.add(dataSourceUpdateZ3950IdList);
        rootElement.add(dataSourceUpdateZ3950IdSequence);
        rootElement.add(dataSourceUpdateFtp);
        rootElement.add(dataSourceUpdateHttp);
        rootElement.add(dataSourceUpdateFolder);
        rootElement.add(dataSourceDelete);
        rootElement.add(dataSourceGet);
        rootElement.add(dataSourceCountRecords);
        rootElement.add(dataSourceStartIngest);
        rootElement.add(dataSourceStopIngest);
        rootElement.add(dataSourceScheduleIngest);
        rootElement.add(dataSourceScheduleList);
        rootElement.add(dataSourceHarvesting);
        rootElement.add(dataSourceStartExport);
        rootElement.add(dataSourcesHarvestStatus);
        rootElement.add(dataSourceLog);
        return rootElement;
    }

    private Element getRecordsOperationList(RestRequest restRequest) {
        Element operationsList = getOperationElement("recordOperationsList",
                "Retrieve the list of the available operations over records",
                restRequest,
                "/rest/records");

        Element recordGet = getOperationElement("getRecord",
                "Retrieves a specific record",
                restRequest,
                "/rest/records/getRecord");

        Element recordSave = getOperationElement("saveRecord",
                "Save record",
                restRequest,
                "/rest/records/saveRecord");

        Element recordDelete = getOperationElement("deleteRecord",
                "Delete record (mark as deleted)",
                restRequest,
                "/rest/records/deleteRecord");

        Element recordErase = getOperationElement("eraseRecord",
                "Erase Record (remove permanent)",
                restRequest,
                "/rest/records/eraseRecord");


        Element rootElement = DocumentHelper.createElement("operationsList");
        rootElement.add(operationsList);
        rootElement.add(recordGet);
        rootElement.add(recordSave);
        rootElement.add(recordDelete);
        rootElement.add(recordErase);
        return rootElement;
    }

    /**
     * Builds the Element from the given operation parameters.
     */
    private Element getOperationElement(String operationName, String operationDescription,
                                        RestRequest restRequest, String operationSyntax) {
        Element operationElement = DocumentHelper.createElement(operationName);

        Element operationDescriptionElement = DocumentHelper.createElement("description");
        operationDescriptionElement.setText(operationDescription);
        Element operationExampleElement = DocumentHelper.createElement("syntax");
        operationSyntax = restRequest.getContextURL() + operationSyntax;
        operationExampleElement.setText(operationSyntax);

        operationElement.add(operationDescriptionElement);
        operationElement.add(operationExampleElement);
        return operationElement;
    }
}
