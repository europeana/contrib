package pt.utl.ist.repox.web.servlet;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.services.web.WebServices;
import pt.utl.ist.repox.services.web.impl.WebServicesImpl;
import pt.utl.ist.repox.services.web.rest.RestRequest;
import pt.utl.ist.repox.services.web.rest.RestUtils;
import pt.utl.ist.repox.util.RepoxContextUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;

public class RestServlet extends HttpServlet
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RestServlet.class);
	private static final String BASE_URI = "/rest";
	private static final String RECORDS_URL_NAME = "records";

	private RepoxManager repoxManager;
	private WebServices webServices;

	@Override
	public void init(ServletConfig config) throws ServletException {
    	super.init(config);

		this.repoxManager = RepoxContextUtil.getRepoxManager();
		this.webServices = new WebServicesImpl();
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

    		if((restRequest.getUriHierarchy() != null) && !restRequest.getUriHierarchy().isEmpty()
    						&& restRequest.getUriHierarchy().get(0).equals(RECORDS_URL_NAME)) {

				if(restRequest.getUriHierarchy().size() == 1) { // records operations list
					String operationParameter = restRequest.getRequestParameters().get("operation");
					if(operationParameter != null && operationParameter.equals("listDataSources")) {
						webServices.writeDataSources(out);
					}
					else {
						Element rootElement = getOperationList(restRequest);
						RestUtils.writeRestResponse(out, rootElement);
					}
				}
				else { // operation over a Data Source
					if(restRequest.getUriHierarchy().size() == 2) { // View Record
						String recordUrn = URLDecoder.decode(restRequest.getUriHierarchy().get(1), "UTF-8");

						if(!validateRecordUrn(restRequest, out, recordUrn)) {
							RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(),
									"invalid record Urn: " + recordUrn,
									out);
							return;
						}

						webServices.writeRecord(out, new Urn(recordUrn));
					}
					else {
						RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
					}
				}

    		}
    		else {
    			RestUtils.writeInvalidRequest(restRequest.getFullRequestURI(), out);
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

					webServices.writeSaveRecord(out, dataSourceId, recordParameter, recordIdParameter);
				}
				else if(operationParameter != null && operationParameter.equals("delete")
								&& validateRecordId(restRequest, out, recordIdParameter)) { // Mark record as deleted
					webServices.writeDeleteRecord(out, recordIdParameter);
				}
				else if(operationParameter != null && operationParameter.equals("erase")
						&& validateRecordId(restRequest, out, recordIdParameter)) { // Permanently remove record
					webServices.writeEraseRecord(out, recordIdParameter);
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
		DataSource dataSource = repoxManager.getDataProviderManager().getDataSource(dataSourceId);
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
                || RepoxContextUtil.getRepoxManager().getAccessPointsManager().getRecord(recordUrn) == null);

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
			DataSource dataSource = repoxManager.getDataProviderManager().getDataSource(dataSourceId);

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

	private Element getOperationList(RestRequest restRequest) {
		Element operationsList = getOperationElement("recordOperationsList",
				"retrieve the list of the available operations over records",
				restRequest,
				"/rest/records");

		Element dataSourceStatus = getOperationElement("dataSourceStatus",
				"get the status of the dataSource",
				restRequest,
				"/rest/records/[dataSource]");

		Element registerTypeList = getOperationElement("registerTypeList",
				"get the list of registers of a given type from a DataSource",
				restRequest,
				"/rest/records/[dataSource]/[record-type]");

		Element registerWithId = getOperationElement("registerWithId",
				"get register with ID of a given type from a DataSource",
				restRequest,
				"/rest/records/[dataSource]/[record-type]/[ID]");

		Element rootElement = DocumentHelper.createElement("operationsList");
		rootElement.add(operationsList);
		rootElement.add(dataSourceStatus);
		rootElement.add(registerTypeList);
		rootElement.add(registerWithId);
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
