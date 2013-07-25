package pt.utl.ist.repox.web.servlet;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.MessageType;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.services.web.impl.WebServicesImplEuropeana;
import pt.utl.ist.repox.services.web.rest.RestRequest;
import pt.utl.ist.repox.services.web.rest.RestUtils;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilEuropeana;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map.Entry;

public class RestServlet extends HttpServlet{
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RestServlet.class);
    private static final String BASE_URI = "/rest";
    private static final String RECORDS_URL_NAME = "records";
    private static final String AGGREGATORS_URL_NAME = "aggregators";
    private static final String DPROVIDERS_URL_NAME = "dataProviders";
    private static final String DSOURCES_URL_NAME = "dataSources";

    private RepoxManager repoxManager;
    private WebServicesImplEuropeana webServices;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuropeana());

        this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        this.webServices = new WebServicesImplEuropeana();
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
                            if(restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("homepage") != null){
                                webServices.createAggregator(out,
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("homepage"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                        "Aggregator: invalid arguments. Syntax: /rest/aggregators/create?name=NAME" +
                                        "&nameCode=NAME_CODE&homepage=HOMEPAGE");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("homepage") != null){
                                webServices.updateAggregator(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("homepage"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating" +
                                        "Aggregator: invalid arguments. Syntax: /rest/aggregators/update?id=ID" +
                                        "&name=NAME&nameCode=NAME_CODE&homepage=HOMEPAGE");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                            if(restRequest.getRequestParameters().get("id") != null){
                                webServices.deleteAggregator(out,
                                        restRequest.getRequestParameters().get("id"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting " +
                                        "Aggregator: invalid arguments. Syntax: /rest/aggregators/delete?id=ID");
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
                    Element rootElement = getDataProviderOperationList(restRequest);
                    RestUtils.writeRestResponse(out, rootElement);
                }
                else { // operation over a Data Provider
                    if(restRequest.getUriHierarchy().size() == 2) {

                        if(restRequest.getUriHierarchy().get(1).equals("list")) {
                            if(restRequest.getRequestParameters().size() == 0) {
                                webServices.writeDataProviders(out);
                            }
                            else{
                                if (restRequest.getRequestParameters().size() == 1 &&
                                        restRequest.getRequestParameters().get("aggregatorId") != null) {
                                    webServices.writeDataProviders(out, restRequest.getRequestParameters().get("aggregatorId"));
                                }
                                else{
                                    webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating" +
                                            "the Data Source: invalid arguments." +
                                            "Syntax: /rest/dataSources/create?name=NAME&description=DESCRIPTION" +
                                            "&country=2_LETTERS_COUNTRY");
                                }
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("list")) {

                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("create")) {
                            if(restRequest.getRequestParameters().get("aggregatorId") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("country") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("url") != null &&
                                    restRequest.getRequestParameters().get("dataSetType") != null){
                                webServices.createDataProvider(out,
                                        restRequest.getRequestParameters().get("aggregatorId"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("country"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("url"),
                                        restRequest.getRequestParameters().get("dataSetType"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                        "Data Provider: invalid arguments." +
                                        "Syntax: /rest/dataProviders/create?aggregatorId=AGGREGATOR_ID&name=NAME" +
                                        "&description=DESCRIPTION&country=2_LETTERS_COUNTRY&nameCode=NAME_CODE" +
                                        "&url=URL&dataSetType=DATA_SET_TYPE");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("update")) {
                            if(restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("country") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("url") != null &&
                                    restRequest.getRequestParameters().get("dataSetType") != null){
                                webServices.updateDataProvider(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("country"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("url"),
                                        restRequest.getRequestParameters().get("dataSetType"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating Data" +
                                        "Provider: invalid arguments. Syntax: /rest/dataProviders/update?id=ID" +
                                        "&name=NAME&description=DESCRIPTION&country=2_LETTERS_COUNTRY");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                            if(restRequest.getRequestParameters().get("id") != null){
                                webServices.deleteDataProvider(out,
                                        restRequest.getRequestParameters().get("id"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                        "Provider: invalid arguments. Syntax: /rest/dataProviders/delete?id=ID");
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
                                if (restRequest.getRequestParameters().size() == 1 &&
                                        restRequest.getRequestParameters().get("dataProviderId") != null) {
                                    webServices.writeDataSources(out, restRequest.getRequestParameters().get("dataProviderId"));
                                }
                                else{
                                    webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the" +
                                            "Data Source: invalid arguments." +
                                            "Syntax: /rest/dataSources/create?name=NAME&description=DESCRIPTION" +
                                            "&country=2_LETTERS_COUNTRY");
                                }
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("createOai")) {
                            if(restRequest.getRequestParameters().get("dataProviderId") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("oaiURL") != null &&
                                    restRequest.getRequestParameters().get("oaiSet") != null){

                                webServices.createDataSourceOai(out,
                                        restRequest.getRequestParameters().get("dataProviderId"),
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("oaiURL"),
                                        restRequest.getRequestParameters().get("oaiSet"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data Source: invalid arguments." +
                                        "Syntax: /rest/dataSources/createOai?dataProviderId=DATA_PROVIDER_ID" +
                                        "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                        "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("createZ3950Timestamp")) {
                            if(restRequest.getRequestParameters().get("dataProviderId") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("address") != null &&
                                    restRequest.getRequestParameters().get("port") != null &&
                                    restRequest.getRequestParameters().get("database") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("recordSyntax") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("earliestTimestamp") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null){

                                webServices.createDataSourceZ3950Timestamp(out,
                                        restRequest.getRequestParameters().get("dataProviderId"),
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("address"),
                                        restRequest.getRequestParameters().get("port"),
                                        restRequest.getRequestParameters().get("database"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("recordSyntax"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("earliestTimestamp"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"));
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
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdList")) {
                            if(restRequest.getRequestParameters().get("dataProviderId") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("address") != null &&
                                    restRequest.getRequestParameters().get("port") != null &&
                                    restRequest.getRequestParameters().get("database") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("recordSyntax") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("filePath") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null){

                                webServices.createDataSourceZ3950IdList(out,
                                        restRequest.getRequestParameters().get("dataProviderId"),
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("address"),
                                        restRequest.getRequestParameters().get("port"),
                                        restRequest.getRequestParameters().get("database"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("recordSyntax"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("filePath"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"));
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
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("createZ3950IdSequence")) {
                            if(restRequest.getRequestParameters().get("dataProviderId") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("address") != null &&
                                    restRequest.getRequestParameters().get("port") != null &&
                                    restRequest.getRequestParameters().get("database") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("recordSyntax") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("maximumId") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null){

                                webServices.createDataSourceZ3950IdSequence(out,
                                        restRequest.getRequestParameters().get("dataProviderId"),
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("address"),
                                        restRequest.getRequestParameters().get("port"),
                                        restRequest.getRequestParameters().get("database"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("recordSyntax"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("maximumId"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error creating the Data" +
                                        "Source: invalid arguments." +
                                        "Syntax: /rest/dataSources/createZ3950IdSequence?dataProviderId=DATA_PROVIDER_ID" +
                                        "&id=DATA_SOURCE_ID&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE" +
                                        "&address=ADDRESS&port=PORT&database=DATABASE&user=USER&password=PASSWORD" +
                                        "&recordSyntax=RECORDS_SYNTAX&charset=CHARSET&maximumId=MAXIMUM_ID" +
                                        "&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("createFtp")) {
                            if(restRequest.getRequestParameters().get("dataProviderId") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("isoFormat") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null &&
                                    restRequest.getRequestParameters().get("recordXPath") != null &&
                                    restRequest.getRequestParameters().get("server") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("ftpPath") != null){

                                webServices.createDataSourceFtp(out,
                                        restRequest.getRequestParameters().get("dataProviderId"),
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("isoFormat"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"),
                                        restRequest.getRequestParameters().get("recordXPath"),
                                        restRequest.getRequestParameters().get("server"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("ftpPath"));
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
                                        "&ftpPath=FTP_PATH");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("createHttp")) {
                            if(restRequest.getRequestParameters().get("dataProviderId") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("isoFormat") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null &&
                                    restRequest.getRequestParameters().get("recordXPath") != null &&
                                    restRequest.getRequestParameters().get("url") != null){

                                webServices.createDataSourceHttp(out,
                                        restRequest.getRequestParameters().get("dataProviderId"),
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("isoFormat"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"),
                                        restRequest.getRequestParameters().get("recordXPath"),
                                        restRequest.getRequestParameters().get("url"));
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
                                        "&recordXPath=RECORDS_XPATH&url=URL");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("createFolder")) {
                            if(restRequest.getRequestParameters().get("dataProviderId") != null &&
                                    restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("isoFormat") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null &&
                                    restRequest.getRequestParameters().get("recordXPath") != null &&
                                    restRequest.getRequestParameters().get("folder") != null){

                                webServices.createDataSourceFolder(out,
                                        restRequest.getRequestParameters().get("dataProviderId"),
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("isoFormat"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"),
                                        restRequest.getRequestParameters().get("recordXPath"),
                                        restRequest.getRequestParameters().get("folder"));
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
                                        "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH");
                            }
                        }

                        else if(restRequest.getUriHierarchy().get(1).equals("updateOai")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("oaiURL") != null &&
                                    restRequest.getRequestParameters().get("oaiSet") != null){

                                webServices.updateDataSourceOai(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("oaiURL"),
                                        restRequest.getRequestParameters().get("oaiSet"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the OAI" +
                                        "Data Source: invalid arguments." +
                                        "Syntax: /rest/dataSources/updateOai?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                        "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                        "&schema=SCHEMA&namespace=NAMESPACE" +
                                        "&metadataFormat=METADATA_FORMAT&oaiURL=URL_OAI_SERVER&oaiSet=OAI_SET");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950Timestamp")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("address") != null &&
                                    restRequest.getRequestParameters().get("port") != null &&
                                    restRequest.getRequestParameters().get("database") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("recordSyntax") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("earliestTimestamp") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null){

                                webServices.updateDataSourceZ3950Timestamp(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("address"),
                                        restRequest.getRequestParameters().get("port"),
                                        restRequest.getRequestParameters().get("database"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("recordSyntax"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("earliestTimestamp"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"));
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
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdList")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("address") != null &&
                                    restRequest.getRequestParameters().get("port") != null &&
                                    restRequest.getRequestParameters().get("database") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("recordSyntax") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("filePath") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null){

                                webServices.updateDataSourceZ3950IdList(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("address"),
                                        restRequest.getRequestParameters().get("port"),
                                        restRequest.getRequestParameters().get("database"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("recordSyntax"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("filePath"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"));
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
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("updateZ3950IdSequence")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("address") != null &&
                                    restRequest.getRequestParameters().get("port") != null &&
                                    restRequest.getRequestParameters().get("database") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("recordSyntax") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("maximumId") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null){

                                webServices.updateDataSourceZ3950IdSequence(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("address"),
                                        restRequest.getRequestParameters().get("port"),
                                        restRequest.getRequestParameters().get("database"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("recordSyntax"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("maximumId"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"));
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
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("updateFtp")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("isoFormat") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null &&
                                    restRequest.getRequestParameters().get("recordXPath") != null &&
                                    restRequest.getRequestParameters().get("server") != null &&
                                    restRequest.getRequestParameters().get("user") != null &&
                                    restRequest.getRequestParameters().get("password") != null &&
                                    restRequest.getRequestParameters().get("ftpPath") != null){

                                webServices.updateDataSourceFtp(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("isoFormat"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"),
                                        restRequest.getRequestParameters().get("recordXPath"),
                                        restRequest.getRequestParameters().get("server"),
                                        restRequest.getRequestParameters().get("user"),
                                        restRequest.getRequestParameters().get("password"),
                                        restRequest.getRequestParameters().get("ftpPath"));
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
                                        "&ftpPath=FTP_PATH");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("updateHttp")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("isoFormat") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null &&
                                    restRequest.getRequestParameters().get("recordXPath") != null &&
                                    restRequest.getRequestParameters().get("url") != null){

                                webServices.updateDataSourceHttp(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("isoFormat"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"),
                                        restRequest.getRequestParameters().get("recordXPath"),
                                        restRequest.getRequestParameters().get("url"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the HTTP Data Source: invalid arguments." +
                                        "Syntax: /rest/dataSources/updateHttp?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                        "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                        "&schema=SCHEMA&namespace=NAMESPACE" +
                                        "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                        "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                        "&recordXPath=RECORDS_XPATH&url=URL");
                            }
                        }
                        else if(restRequest.getUriHierarchy().get(1).equals("updateFolder")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("description") != null &&
                                    restRequest.getRequestParameters().get("nameCode") != null &&
                                    restRequest.getRequestParameters().get("name") != null &&
                                    restRequest.getRequestParameters().get("exportPath") != null &&
                                    restRequest.getRequestParameters().get("schema") != null &&
                                    restRequest.getRequestParameters().get("namespace") != null &&
                                    restRequest.getRequestParameters().get("metadataFormat") != null &&
                                    restRequest.getRequestParameters().get("isoFormat") != null &&
                                    restRequest.getRequestParameters().get("charset") != null &&
                                    restRequest.getRequestParameters().get("recordIdPolicy") != null &&
                                    restRequest.getRequestParameters().get("idXpath") != null &&
                                    restRequest.getRequestParameters().get("namespacePrefix") != null &&
                                    restRequest.getRequestParameters().get("namespaceUri") != null &&
                                    restRequest.getRequestParameters().get("recordXPath") != null &&
                                    restRequest.getRequestParameters().get("folder") != null){

                                webServices.updateDataSourceFolder(out,
                                        restRequest.getRequestParameters().get("id"),
                                        restRequest.getRequestParameters().get("description"),
                                        restRequest.getRequestParameters().get("nameCode"),
                                        restRequest.getRequestParameters().get("name"),
                                        restRequest.getRequestParameters().get("exportPath"),
                                        restRequest.getRequestParameters().get("schema"),
                                        restRequest.getRequestParameters().get("namespace"),
                                        restRequest.getRequestParameters().get("metadataFormat"),
                                        restRequest.getRequestParameters().get("isoFormat"),
                                        restRequest.getRequestParameters().get("charset"),
                                        restRequest.getRequestParameters().get("recordIdPolicy"),
                                        restRequest.getRequestParameters().get("idXpath"),
                                        restRequest.getRequestParameters().get("namespacePrefix"),
                                        restRequest.getRequestParameters().get("namespaceUri"),
                                        restRequest.getRequestParameters().get("recordXPath"),
                                        restRequest.getRequestParameters().get("folder"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error updating the Folder Data Source: invalid arguments." +
                                        "Syntax: /rest/dataSources/updateFolder?id=DATA_SOURCE_ID&description=DESCRIPTION" +
                                        "&nameCode=NAME_CODE&name=NAME&exportPath=EXPORT_PATH" +
                                        "&schema=SCHEMA&namespace=NAMESPACE" +
                                        "&metadataFormat=METADATA_FORMAT&isoFormat=ISO_FORMAT" +
                                        "&charset=CHAR_SET&recordIdPolicy=RECORD_ID_POLICY&idXpath=ID_XPATH" +
                                        "&namespacePrefix=NAMESPACE_PREFIX&namespaceUri=NAMESPACE_URI" +
                                        "&recordXPath=RECORDS_XPATH&folder=FOLDER_PATH");
                            }
                        }


                        else if(restRequest.getUriHierarchy().get(1).equals("delete")) {
                            if(restRequest.getRequestParameters().get("id") != null){
                                webServices.deleteDataSource(out,
                                        restRequest.getRequestParameters().get("id"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error deleting Data" +
                                        "Source: invalid arguments. Syntax: /rest/dataSources/delete?id=ID");
                            }
                        }

                        else if(restRequest.getUriHierarchy().get(1).equals("startIngest")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("fullIngest") != null){
                                webServices.startIngestDataSource(out, restRequest.getRequestParameters().get("id"),
                                        Boolean.valueOf(restRequest.getRequestParameters().get("fullIngest")));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error starting the Data" +
                                        "Source ingestion: invalid arguments. Syntax: /rest/dataSources/startIngest?id=ID&" +
                                        "fullIngest=BOOLEAN");
                            }
                        }

                        else if(restRequest.getUriHierarchy().get(1).equals("stopIngest")) {
                            if(restRequest.getRequestParameters().get("id") != null){
                                webServices.stopIngestDataSource(out, restRequest.getRequestParameters().get("id"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error stopping the Data" +
                                        "Source ingestion: invalid arguments. Syntax: /rest/dataSources/stopIngest?id=ID");
                            }
                        }

                        else if(restRequest.getUriHierarchy().get(1).equals("scheduleIngest")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("firstRunDate") != null &&
                                    restRequest.getRequestParameters().get("firstRunHour") != null &&
                                    restRequest.getRequestParameters().get("frequency") != null &&
                                    restRequest.getRequestParameters().get("xmonths") != null &&
                                    restRequest.getRequestParameters().get("fullIngest") != null){
                                webServices.scheduleIngestDataSource(out, restRequest.getRequestParameters().get("id"), restRequest.getRequestParameters().get("firstRunDate"),
                                        restRequest.getRequestParameters().get("firstRunHour"), restRequest.getRequestParameters().get("frequency"),
                                        restRequest.getRequestParameters().get("xmonths"), restRequest.getRequestParameters().get("fullIngest"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error scheduling the" +
                                        "Data Source ingestion: invalid arguments. Syntax: /rest/dataSources/scheduleIngest?id=ID" +
                                        "&firstRunDate=YYYY-MM-DD&firstRunHour=HH:MM&frequency=ONCE_DAILY_WEEKLY_XMONTHLY" +
                                        "&xmonths=NUMBER");
                            }
                        }

                        else if(restRequest.getUriHierarchy().get(1).equals("scheduleIngest")) {
                            if(restRequest.getRequestParameters().get("id") != null &&
                                    restRequest.getRequestParameters().get("firstRunDate") != null &&
                                    restRequest.getRequestParameters().get("firstRunHour") != null &&
                                    restRequest.getRequestParameters().get("frequency") != null &&
                                    restRequest.getRequestParameters().get("xmonths") != null &&
                                    restRequest.getRequestParameters().get("fullIngest") != null){
                                webServices.scheduleIngestDataSource(out, restRequest.getRequestParameters().get("id"), restRequest.getRequestParameters().get("firstRunDate"),
                                        restRequest.getRequestParameters().get("firstRunHour"), restRequest.getRequestParameters().get("frequency"),
                                        restRequest.getRequestParameters().get("xmonths"), restRequest.getRequestParameters().get("fullIngest"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error scheduling the Data" +
                                        "Source ingestion: invalid arguments. Syntax: /rest/dataSources/scheduleIngest?id=ID" +
                                        "&firstRunDate=YYYY-MM-DD&firstRunHour=HH:MM&frequency=ONCE_DAILY_WEEKLY_XMONTHLY" +
                                        "&xmonths=NUMBER");
                            }
                        }

                        else if(restRequest.getUriHierarchy().get(1).equals("scheduleList")) {
                            if(restRequest.getRequestParameters().get("id") != null){
                                webServices.scheduleListDataSource(out, restRequest.getRequestParameters().get("id"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                        "Source ingestion schedule: invalid arguments. Syntax: /rest/dataSources/scheduleList?id=ID");
                            }
                        }

                        else if(restRequest.getUriHierarchy().get(1).equals("harvestStatus")) {
                            if(restRequest.getRequestParameters().get("id") != null){
                                webServices.harvestStatusDataSource(out, restRequest.getRequestParameters().get("id"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                        "Source harvest status: invalid arguments. Syntax: /rest/dataSources/harvestStatus?id=ID");
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



                        else if(restRequest.getUriHierarchy().get(1).equals("log")) {
                            if(restRequest.getRequestParameters().get("id") != null){
                                webServices.logDataSource(out, restRequest.getRequestParameters().get("id"));
                            }
                            else{
                                webServices.createErrorMessage(out, MessageType.INVALID_REQUEST, "Error getting the Data" +
                                        "Source log: invalid arguments. Syntax: /rest/dataSources/log?id=ID");
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

        } catch (Exception e) {
            log.error("Error in Rest GET request", e);
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

        Element rootElement = DocumentHelper.createElement("aggregatorOperationsList");
        rootElement.add(aggregatorOperations);
        rootElement.add(aggregatorList);
        rootElement.add(aggregatorCreate);
        rootElement.add(aggregatorUpdate);
        rootElement.add(aggregatorDelete);
        return rootElement;
    }

    /**
     * Retrieve the list of the available operations over Data Providers
     * @param restRequest
     * @return
     */
    private Element getDataProviderOperationList(RestRequest restRequest) {
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

        Element rootElement = DocumentHelper.createElement("dataProviderOperationsList");
        rootElement.add(dataProviderOperations);
        rootElement.add(dataProviderList);
        rootElement.add(dataSourceFromDataProviderList);
        rootElement.add(dataProviderCreate);
        rootElement.add(dataProviderUpdate);
        rootElement.add(dataProviderDelete);
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
                "/rest/dataSources/createDataSourceFtp?dataProviderId=DATA_PROVIDER_ID&id=DATA_SOURCE_ID" +
                        "&description=DESCRIPTION&schema=SCHEMA&namespace=NAMESPACE&metadataFormat=METADATA_FORMAT" +
                        "&isoFormat=ISO_FORMAT&charset=CHAR_SET&password=PASSWORD&recordSyntax=RECORDS_SYNTAX" +
                        "&charset=CHARSET&recordIdPolicy=RECORD_ID_POLICY&namespacePrefix=NAMESPACE_PREFIX" +
                        "&namespaceUri=NAMESPACE_URI&recordXPath=RECORDS_XPATH&server=SERVER&user=USER" +
                        "&password=PASSWORD&ftpPath=FTP_PATH");

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

        Element dataSourceStartIngest = getOperationElement("dataSources",
                "Starts the Data Source ingest",
                restRequest,
                "startIngest");

        Element dataSourceStopIngest = getOperationElement("dataSources",
                "Stops the Data Source ingest",
                restRequest,
                "/rest/dataSources/stopIngest");

        Element dataSourceScheduleIngest = getOperationElement("dataSources",
                "Schedules a Data Source ingest",
                restRequest,
                "/rest/dataSources/scheduleIngest");

        Element dataSourceScheduleList = getOperationElement("dataSources",
                "Retrives the list of all Data Source schedules",
                restRequest,
                "/rest/dataSources/scheduleList");

        Element dataSourceHarvesting = getOperationElement("dataSources",
                "Updates a Data Source",
                restRequest,
                "/rest/dataSources/harvesting");

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
        rootElement.add(dataSourceStartIngest);
        rootElement.add(dataSourceStopIngest);
        rootElement.add(dataSourceScheduleIngest);
        rootElement.add(dataSourceScheduleList);
        rootElement.add(dataSourceHarvesting);
        rootElement.add(dataSourcesHarvestStatus);
        rootElement.add(dataSourceLog);
        return rootElement;
    }

    private Element getRecordsOperationList(RestRequest restRequest) {
        Element operationsList = getOperationElement("recordOperationsList",
                "retrieve the list of the available operations over records",
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
