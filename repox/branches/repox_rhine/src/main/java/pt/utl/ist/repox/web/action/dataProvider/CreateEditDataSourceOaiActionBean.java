package pt.utl.ist.repox.web.action.dataProvider;

import eu.europeana.repox2sip.Repox2SipException;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.oclc.oai.harvester2.verb.ListSets;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.IdGenerated;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CreateEditDataSourceOaiActionBean extends CreateEditDataSourceActionBean {
	private static final Logger log = Logger.getLogger(CreateEditDataSourceOaiActionBean.class);

	private DataSourceOai dataSource;
	private List<String> sets;
	private List<String> setsRecords;

	public DataSourceOai getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceOai dataSource) {
		this.dataSource = dataSource;
	}

	public List<String> getSets() {
		return sets;
	}

	public void setSets(List<String> sets) {
		this.sets = sets;
	}

	public List<String> getSetsRecords() {
		return setsRecords;
	}

	public void setSetsRecords(List<String> setsRecords) {
		this.setsRecords = setsRecords;
	}

	public CreateEditDataSourceOaiActionBean() throws IOException, DocumentException {
		super();
	}

	@Override
	public DataSource getBeanDataSource() {
		return dataSource;
	}

	@Override
	public Class<? extends DataSource> getDataSourceClass() {
		return DataSourceOai.class;
	}

	@Override
	public void prepareSubmission() {
		dataSource.setRecordIdPolicy(new IdGenerated());
	}

	@Override
	public void validateDataSource(ValidationErrors errors) throws DocumentException {
		if(dataSource == null || dataSource.getOaiSourceURL() == null || dataSource.getOaiSourceURL().trim().isEmpty()) {
			errors.add("dataSource", new LocalizableError("error.dataSource.oaiSourceURL"));
		} else if (!dataSource.getOaiSourceURL().startsWith("http://")) {
			dataSource.setOaiSourceURL("http://" + dataSource.getOaiSourceURL());
		}

/*
		if(dataSource == null || dataSource.getOaiSet() == null || dataSource.getOaiSet().trim().isEmpty()) {
			errors.add("dataSource", new LocalizableError("error.dataSource.oaiSet"));
		}
*/
	}

	public Resolution testOaiSourceURL() throws DocumentException {
		if(dataSource == null || dataSource.getOaiSourceURL() == null || dataSource.getOaiSourceURL().isEmpty()) {
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.noURL"));
			return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
		}

		ListSets listSets = null;
		try {
			listSets = new ListSets(dataSource.getOaiSourceURL());
		}
		catch(Exception e) {
		}
		try
		{
			if(listSets == null || (listSets.getErrors() != null && listSets.getErrors().getLength() > 0)) {
				XPath xPathError = DocumentHelper.createXPath("//oai:error[@code='noSetHierarchy']");
				HashMap<String, String> nameSpaces = new HashMap<String, String>();
				nameSpaces.put("oai", "http://www.openarchives.org/OAI/2.0/");
				xPathError.setNamespaceURIs(nameSpaces);
				List errorNodes = xPathError.selectNodes(DocumentHelper.parseText(listSets.toString()));
				if(errorNodes != null && errorNodes.size() > 0) {
					sets = new ArrayList<String>();
					setsRecords = new ArrayList<String>();
					FlashScope.getCurrent(getContext().getRequest(), true).put(this);
				}
				else {
					context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.invalidURL", dataSource.getOaiSourceURL()));
				}
				
				return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
			}
			else {
				Document document = DocumentHelper.parseText(listSets.toString());
	//			String xpath = "//setSpec";
	//			String xpath = "/OAI-PMH/request/ListSets/set/setSpec";
				XPath xPath = DocumentHelper.createXPath("//oai:setSpec");
				HashMap<String, String> nameSpaces = new HashMap<String, String>();
				nameSpaces.put("oai", "http://www.openarchives.org/OAI/2.0/");
				xPath.setNamespaceURIs(nameSpaces);
				List<Node> nodeList = xPath.selectNodes(document.getRootElement());

				if(nodeList == null || nodeList.size() == 0) {
					context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.noSets", dataSource.getOaiSourceURL()));
					return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
				}

				sets = new ArrayList<String>();
				setsRecords = new ArrayList<String>();

                for (Node aNodeList : nodeList) {
                    String currentSet = aNodeList.getText();
                    sets.add(currentSet);

                    /* TODO: Getting all the sets record number takes too long, use this only when needed
                         ListIdentifiers listIdentifiers = new ListIdentifiers(dataSource.getOaiSourceURL(), null, null, currentSet, "oai_dc");
                         Document documentIdentifiers = DocumentHelper.parseText(listIdentifiers.toString());
                         XPath xPathIdentifiers = DocumentHelper.createXPath("//@completeListSize");
                         xPathIdentifiers.setNamespaceURIs(nameSpaces);
                         Node size = xPathIdentifiers.selectSingleNode(documentIdentifiers.getRootElement());
                         if(size != null) {
                             setsRecords.add(size.getText());
                         }
                         else {
                             setsRecords.add("");
                         }*/
                }
			}

		}
		catch(Exception e) {
			log.error(e);
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.unableToCheckServer", dataSource.getOaiSourceURL()));
			return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
		}

		FlashScope.getCurrent(getContext().getRequest(), true).put(this);
		return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
	}

	public Resolution getAllOaiSources() throws DocumentException, IOException {
		if(dataProviderId != null) {
			dataProvider = RepoxContextUtil.getRepoxManager().getDataManager().getDataProvider(dataProviderId);
		}
		else {
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSource.invalidDataProvider"));
			return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
		}

		if(dataSource == null || dataSource.getOaiSourceURL() == null || dataSource.getOaiSourceURL().isEmpty()) {
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.noURL"));
			return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
		}

		ListSets listSets = null;
		try {
			listSets = new ListSets(dataSource.getOaiSourceURL());
		}
		catch(Exception e) {
		}

		try
		{
			if(listSets == null || (listSets.getErrors() != null && listSets.getErrors().getLength() > 0)) {
				context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.invalidURL", dataSource.getOaiSourceURL()));
				return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
			}
			else {
				Document document = DocumentHelper.parseText(listSets.toString());
				XPath xPath = DocumentHelper.createXPath("//oai:set");
				HashMap<String, String> nameSpaces = new HashMap<String, String>();
				nameSpaces.put("oai", "http://www.openarchives.org/OAI/2.0/");
				xPath.setNamespaceURIs(nameSpaces);
				List<Node> nodeList = xPath.selectNodes(document.getRootElement());

				if(nodeList == null || nodeList.size() == 0) {
					context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.noSets", dataSource.getOaiSourceURL()));
					return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
				}

				Collection<DataSource> oldDataSources = dataProvider.getDataSources();

				if(oldDataSources == null) {
					dataProvider.setDataSources(new ArrayList<DataSource>());
				}

                for (Node aNodeList : nodeList) {
                    Element currentSetElement = (Element) aNodeList;
                    String setSpec = currentSetElement.element("setSpec").getText();
                    String setDescription = currentSetElement.element("setName").getText();

                    String setId = setSpec.replaceAll("[^a-zA-Z_0-9]", "_");

                    DataSourceOai dataSourceOai = new DataSourceOai(dataProvider, setId, setDescription, MetadataFormat.oai_dc.toString(),
                            dataSource.getOaiSourceURL(), setSpec, new IdGenerated(), null, dataSource.getLanguage(), dataSource.getName(), dataSource.getNameCode(), dataSource.getIdDb());

                    boolean isDuplicate = false;
                    if (oldDataSources != null) {
                        for (DataSource oldDataSource : oldDataSources) {
                            if (oldDataSource instanceof DataSourceOai
                                    && ((DataSourceOai) oldDataSource).isSameDataSource(dataSourceOai)) {
                                isDuplicate = true;
                            }
                        }
                    }

                    if (!isDuplicate) {
                        while (RepoxContextUtil.getRepoxManager().getDataManager().getDataSource(dataSourceOai.getId()) != null) {
                            dataSourceOai.setId(dataSourceOai.getId() + "_new");
                        }

                        dataProvider.getDataSources().add(dataSourceOai);
                        dataSourceOai.initAccessPoints();
                    }
                }
				RepoxContextUtil.getRepoxManager().getDataManager().updateDataProvider(dataProvider, dataProvider.getId());
				context.getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSources());

				context.getMessages().add(new LocalizableMessage("dataSource.createOaiSources.success", dataProvider.getName(), getBeanDataSource().getId()));
			}
		}
		catch(Exception e) {
			log.error("Error getting all OAI Data Sources", e);
			context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSourceOai.unableToCheckServer", dataSource.getOaiSourceURL()));
			return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
		}

		FlashScope.getCurrent(getContext().getRequest(), true).put(this);
		return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + dataProviderId);
	}

	@Override
	public void loadDataSource() throws DocumentException, IOException {
		dataSource = (DataSourceOai) context.getRepoxManager().getDataManager().getDataSource(dataSourceId);
	}

	@Override
	public Resolution getCreationResolution() {
		return new ForwardResolution("/jsp/dataProvider/createDataSourceOai.jsp");
	}

}