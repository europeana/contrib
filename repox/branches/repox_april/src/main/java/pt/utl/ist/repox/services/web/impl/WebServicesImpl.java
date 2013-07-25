package pt.utl.ist.repox.services.web.impl;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.services.web.WebServices;
import pt.utl.ist.repox.services.web.rest.RestUtils;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class WebServicesImpl implements WebServices {
	private static final Logger log = Logger.getLogger(WebServicesImpl.class);

	public WebServicesImpl() {
		super();
	}

	public void writeRecord(OutputStream out, Urn recordUrn) throws IOException, DocumentException, SQLException {
		byte[] recordMetadata = RepoxContextUtil.getRepoxManager().getAccessPointsManager().getRecord(recordUrn).getMetadata();
		SAXReader reader = new SAXReader();
		Document recordDocument = reader.read(new ByteArrayInputStream(recordMetadata));

		Element recordResultElement = DocumentHelper.createElement("recordResult");
		recordResultElement.addAttribute("urn", recordUrn.toString());
		Node detachedRecordNode = recordDocument.getRootElement().detach();
		recordResultElement.add(detachedRecordNode);
		RestUtils.writeRestResponse(out, recordResultElement);
	}

	public void writeDataSources(OutputStream out) throws DocumentException, IOException {
		List<DataSource> dataSources = RepoxContextUtil.getRepoxManager().getDataProviderManager().loadDataSources();

		Element dataSourcesElement = DocumentHelper.createElement("dataSources");

		for (DataSource dataSource : dataSources) {
			Element currentDataSourceElement = DocumentHelper.createElement("dataSource");
			currentDataSourceElement.addAttribute("id", dataSource.getId());
			currentDataSourceElement.setText(dataSource.getDescription());
			dataSourcesElement.add(currentDataSourceElement);
		}

		RestUtils.writeRestResponse(out, dataSourcesElement);
	}

	public void writeSaveRecord(OutputStream out, String dataSourceId, String recordString, String recordId) throws IOException {
		try {
			Element recordRoot = (Element) DocumentHelper.parseText(recordString).getRootElement().detach();
			DataSource dataSource = RepoxContextUtil.getRepoxManager().getDataProviderManager().getDataSource(dataSourceId);
			RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(recordRoot, recordId, false, false);
			RepoxContextUtil.getRepoxManager().getAccessPointsManager().processRecord(dataSource, recordRepox);

			Element successElement = DocumentHelper.createElement("success");
			successElement.addElement("urn").setText(new Urn(dataSourceId, recordRepox.getId()).toString());
			successElement.addElement("id").setText(recordRepox.getId().toString());
			successElement.addElement("description").setText("Record with id " + recordRepox.getId() + " saved successfully");
			RestUtils.writeRestResponse(out, successElement);
		}
		catch(Exception e) {
			log.error("Unable to save or update record", e);

			Element errorElement = DocumentHelper.createElement("error");
			errorElement.addAttribute("type", "Unable to save Record");
			RestUtils.writeRestResponse(out, errorElement);
		}
	}

	public void writeDeleteRecord(OutputStream out, String recordId) throws IOException {
		try {
			Urn recordUrn = new Urn(recordId);
			RepoxContextUtil.getRepoxManager().getAccessPointsManager().deleteRecord(recordUrn);

			Element successElement = DocumentHelper.createElement("success");
			successElement.addElement("urn").setText(recordUrn.toString());
			successElement.addElement("id").setText(recordId.toString());
			successElement.addElement("description").setText("Record with id " + recordId + " marked as deleted successfully");
			RestUtils.writeRestResponse(out, successElement);
		}
		catch(Exception e) {
			log.error("Unable to permanently remove record", e);

			Element errorElement = DocumentHelper.createElement("error");
			errorElement.addAttribute("type", "Unable to permanently remove Record");
			RestUtils.writeRestResponse(out, errorElement);

		}
	}

	public void writeEraseRecord(OutputStream out, String recordId) throws IOException {
		try {
			Urn recordUrn = new Urn(recordId);
			RepoxContextUtil.getRepoxManager().getAccessPointsManager().removeRecord(recordUrn);

			Element successElement = DocumentHelper.createElement("success");
			successElement.addElement("urn").setText(recordUrn.toString());
			successElement.addElement("id").setText(recordId.toString());
			successElement.addElement("description").setText("Record with id " + recordId + " permanently removed successfully");
			RestUtils.writeRestResponse(out, successElement);
		}
		catch(Exception e) {
			log.error("Unable to permanently remove record", e);

			Element errorElement = DocumentHelper.createElement("error");
			errorElement.addAttribute("type", "Unable to permanently remove Record");
			RestUtils.writeRestResponse(out, errorElement);

		}

	}
}
