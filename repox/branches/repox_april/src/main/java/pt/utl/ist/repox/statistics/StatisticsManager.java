package pt.utl.ist.repox.statistics;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.IdExtracted;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class StatisticsManager {
	private static final Logger log = Logger.getLogger(StatisticsManager.class);

	private File configurationFile;

	public File getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}

	public StatisticsManager(File configurationFile) {
		super();
		this.configurationFile = configurationFile;
	}

	public RepoxStatistics generateStatistics() throws IOException, DocumentException, SQLException {
		int dataSourcesIdExtracted = 0;
		int dataSourcesIdGenerated = 0;
		int dataSourcesIdProvided = 0;

		int dataProviders = 0;
		int dataSourcesOai = 0;
		int dataSourcesDirectoryImporter = 0;

		Map<String, Integer> dataSourcesMetadataFormats = new TreeMap<String, Integer>();

		Map<String, Integer> countriesRecords = new TreeMap<String, Integer>();
		int recordsTotal = 0;

		List<DataProvider> dataProvidersList = RepoxContextUtil.getRepoxManager().getDataProviderManager().loadDataProviders();

		for(DataProvider dataProvider : dataProvidersList) {
			dataProviders++;

			for(DataSource dataSource : dataProvider.getDataSources()) {
				if(dataSource instanceof DataSourceOai) {
					dataSourcesOai++;
				}
				if(dataSource instanceof DataSourceDirectoryImporter) {
					dataSourcesDirectoryImporter++;
				}

				if(dataSource.getRecordIdPolicy() instanceof IdProvided) {
					dataSourcesIdProvided++;
				}
				else if(dataSource.getRecordIdPolicy() instanceof IdExtracted) {
					dataSourcesIdExtracted++;
				}
				else if(dataSource.getRecordIdPolicy() instanceof IdGenerated) {
					dataSourcesIdGenerated++;
				}
				else {
					throw new RuntimeException("DataSource of unsupported class:" + dataSource.getClass().getName());
				}

				Integer dataSourceFormats = dataSourcesMetadataFormats.get(dataSource.getMetadataFormat());
				if(dataSourceFormats == null) {
					dataSourcesMetadataFormats.put(dataSource.getMetadataFormat(), 1);
				}
				else {
					dataSourcesMetadataFormats.put(dataSource.getMetadataFormat(), dataSourceFormats + 1);
				}

				int dataSourceCount = RepoxContextUtil.getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId()).getCount();
				
				if(dataSource.getDataProvider().getCountry() != null) {
					int countryRecordsTotal = dataSourceCount;
					
					if(countriesRecords.get(dataSource.getDataProvider().getCountry()) != null) {
						countryRecordsTotal += countriesRecords.get(dataSource.getDataProvider().getCountry());
					}
					
					countriesRecords.put(dataSource.getDataProvider().getCountry(), countryRecordsTotal);
				}
				
				recordsTotal += dataSourceCount;
			}
		}

		int dataSourcesTotal = dataSourcesOai + dataSourcesDirectoryImporter;
		float recordsAvgDataSource = (dataSourcesTotal == 0 ? 0 : (float) recordsTotal / (float) dataSourcesTotal);
		float recordsAvgDataProvider = (dataProvidersList.size() == 0 ? 0 : (float) recordsTotal / (float) dataProvidersList.size());
		
		RepoxStatistics repoxStatistics = new RepoxStatistics(dataSourcesIdExtracted, dataSourcesIdGenerated, dataSourcesIdProvided,
						dataProviders, dataSourcesOai, dataSourcesDirectoryImporter, dataSourcesMetadataFormats, recordsAvgDataSource,
						recordsAvgDataProvider, countriesRecords, recordsTotal);

		saveStatistics(repoxStatistics);

		return repoxStatistics;
	}

	public synchronized void saveStatistics(RepoxStatistics repoxStatistics) throws IOException {
		Document document = DocumentHelper.createDocument();

		Element rootNode = document.addElement("repox-statistics");
		rootNode.addAttribute("generationDate", DateFormatUtils.format(repoxStatistics.getGenerationDate(), TimeUtil.LONG_DATE_FORMAT_TIMEZONE));

		rootNode.addElement("dataSourcesIdExtracted").setText(String.valueOf(repoxStatistics.getDataSourcesIdExtracted()));
		rootNode.addElement("dataSourcesIdGenerated").setText(String.valueOf(repoxStatistics.getDataSourcesIdGenerated()));
		rootNode.addElement("dataSourcesIdProvided").setText(String.valueOf(repoxStatistics.getDataSourcesIdProvided()));
		rootNode.addElement("dataProviders").setText(String.valueOf(repoxStatistics.getDataProviders()));
		rootNode.addElement("dataSourcesOai").setText(String.valueOf(repoxStatistics.getDataSourcesOai()));
		rootNode.addElement("dataSourcesDirectoryImporter").setText(String.valueOf(repoxStatistics.getDataSourcesDirectoryImporter()));
		if(repoxStatistics.getDataSourcesMetadataFormats() != null && !repoxStatistics.getDataSourcesMetadataFormats().isEmpty()) {
			Element dataSourcesMetadataFormatsElement = rootNode.addElement("dataSourcesMetadataFormats");
			for (Entry<String, Integer> currentFormat : repoxStatistics.getDataSourcesMetadataFormats().entrySet()) {
				Element currentDSMF = dataSourcesMetadataFormatsElement.addElement("dataSourcesMetadataFormat");
				currentDSMF.addElement("metadataFormat").setText(currentFormat.getKey());
				currentDSMF.addElement("dataSources").setText(currentFormat.getValue().toString());
			}
		}

		rootNode.addElement("recordsAvgDataSource").setText(String.valueOf(repoxStatistics.getRecordsAvgDataSource()));
		rootNode.addElement("recordsAvgDataProvider").setText(String.valueOf(repoxStatistics.getRecordsAvgDataProvider()));
		Element countriesRecordsElement = rootNode.addElement("countriesRecords");
		if(repoxStatistics.getCountriesRecords() != null && !repoxStatistics.getCountriesRecords().isEmpty()) {
			for (Entry<String, Integer> currentCountry : repoxStatistics.getCountriesRecords().entrySet()) {
				Element currentCR = countriesRecordsElement.addElement("countryRecords");
				currentCR.addAttribute("country", currentCountry.getKey());
				currentCR.addElement("records").setText(currentCountry.getValue().toString());
			}
		}
		rootNode.addElement("recordsTotal").setText(String.valueOf(repoxStatistics.getRecordsTotal()));

		XmlUtil.writePrettyPrint(configurationFile, document);
	}

	public synchronized RepoxStatistics loadRepoxStatistics() throws IOException, DocumentException, SQLException, ParseException {
		if(!configurationFile.exists()) {
			RepoxStatistics repoxStatistics = generateStatistics();
			saveStatistics(repoxStatistics);
			return repoxStatistics;
		}

		SAXReader reader = new SAXReader();
        Document document = reader.read(configurationFile);

        Element statisticsElement = document.getRootElement();

        Date generationDate = new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT_TIMEZONE).parse(statisticsElement.attributeValue("generationDate"));
        int dataSourcesIdExtracted = Integer.parseInt(statisticsElement.element("dataSourcesIdExtracted").getText());
		int dataSourcesIdGenerated = Integer.parseInt(statisticsElement.element("dataSourcesIdGenerated").getText());
		int dataSourcesIdProvided = Integer.parseInt(statisticsElement.element("dataSourcesIdProvided").getText());

		int dataProviders = Integer.parseInt(statisticsElement.element("dataProviders").getText());
		int dataSourcesOai = Integer.parseInt(statisticsElement.element("dataSourcesOai").getText());
		int dataSourcesDirectoryImporter = Integer.parseInt(statisticsElement.element("dataSourcesDirectoryImporter").getText());

		Map<String, Integer> dataSourcesMetadataFormats = new TreeMap<String, Integer>();
		if(statisticsElement.element("dataSourcesMetadataFormats") != null) {
			List<Element> dataSourceFormats = statisticsElement.element("dataSourcesMetadataFormats").elements("dataSourcesMetadataFormat");
			if(dataSourceFormats != null && !dataSourceFormats.isEmpty()) {
				for (Element currentElement : dataSourceFormats) {
					dataSourcesMetadataFormats.put(currentElement.elementText("metadataFormat"), Integer.valueOf(currentElement.elementText("dataSources")));
				}
			}
		}

		float recordAvgDataSource = Float.parseFloat(statisticsElement.element("recordAvgDataSource").getText());
		float recordAvgDataProvider = Float.parseFloat(statisticsElement.element("recordAvgDataProvider").getText());
		
		Map<String, Integer> countriesRecords = new TreeMap<String, Integer>();
		if(statisticsElement.element("countriesRecords") != null) {
			List<Element> countriesRecordsList = statisticsElement.element("countriesRecords").elements("countryRecords");
			if(countriesRecordsList != null && !countriesRecordsList.isEmpty()) {
				for (Element currentElement : countriesRecordsList) {
					countriesRecords.put(currentElement.attributeValue("country"), Integer.valueOf(currentElement.elementText("records")));
				}
			}
		}
		
		int recordsTotal = Integer.parseInt(statisticsElement.element("recordsTotal").getText());

		RepoxStatistics repoxStatistics = new RepoxStatistics(dataSourcesIdExtracted, dataSourcesIdGenerated, dataSourcesIdProvided,
						dataProviders, dataSourcesOai, dataSourcesDirectoryImporter, dataSourcesMetadataFormats, recordAvgDataSource,
						recordAvgDataProvider, countriesRecords, recordsTotal);
        repoxStatistics.setGenerationDate(generationDate);
		return repoxStatistics;
	}
}
