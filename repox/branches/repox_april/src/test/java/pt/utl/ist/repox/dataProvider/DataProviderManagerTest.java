package pt.utl.ist.repox.dataProvider;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.CompareUtil;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DataProviderManagerTest {
	private final String OUTPUT_CONFIGURATION_FILE_NAME = RepoxContextUtil.DATA_PROVIDERS_FILENAME;
	private final String OUTPUT_CONFIGURATION_FILE_NAME_ALT = "dataProviders.temp.xml";

	private File configurationFile;
	private File configurationFileAlt;

	private List<DataProvider> getTestDataProviders() {
		List<DataProvider> dataProviders = new ArrayList<DataProvider>();

		List<DataSource> dataSources = new ArrayList<DataSource>();
		DataSource source1 = new DataSourceOai(null, "source1", "test", MetadataFormat.oai_dc.toString(),
				"http://dummyurl1", "set1", new IdGenerated(), null);
		DataSource source2 = new DataSourceOai(null, "source2", "test", MetadataFormat.oai_dc.toString(),
				"http://dummyurl2", "set2", new IdGenerated(), null);
		dataSources.add(source1);
		dataSources.add(source2);

		DataProvider provider = new DataProvider("provider1", "provider1", "pt", "Dummy Data Provider", dataSources, null);
		dataProviders.add(provider);

		return dataProviders;
	}


	private boolean dataProvidersEqual(List<DataProvider> sourceDataProviders, List<DataProvider> targetDataProviders) {
		if(sourceDataProviders == null && targetDataProviders == null) {
			return true;
		}
		else if((sourceDataProviders == null || targetDataProviders == null)
			|| sourceDataProviders.size() != targetDataProviders.size()) {
			return false;
		}

		for (int i = 0; i < sourceDataProviders.size(); i++) {
			DataProvider sourceDataProvider = sourceDataProviders.get(i);
			DataProvider targetDataProvider = targetDataProviders.get(i);
			if(!dataProvidersEqual(sourceDataProvider, targetDataProvider)) {
				return false;
			}
		}

		return true;
	}

	private boolean dataProvidersEqual(DataProvider sourceDataProvider, DataProvider targetDataProvider) {
        return CompareUtil.compareObjectsAndNull(sourceDataProvider.getId(), targetDataProvider.getId())
                && CompareUtil.compareObjectsAndNull(sourceDataProvider.getDescription(), targetDataProvider.getDescription())
                && dataSourcesEqual(new ArrayList<DataSource>(sourceDataProvider.getDataSources()),
                new ArrayList<DataSource>(targetDataProvider.getDataSources()));
	}


	private boolean dataSourcesEqual(List<DataSource> dataSources, List<DataSource> otherDataSources) {
		boolean result = true;
		if(dataSources.size() != otherDataSources.size()) {
			result = false;
		}
		else {
			for (int i = 0; i < dataSources.size(); i++) {
				DataSource dataSource = dataSources.get(i);
				DataSource loadedDataSource = otherDataSources.get(i);
				if((dataSource == null && loadedDataSource != null)
						|| (dataSource != null && !dataSource.equals(loadedDataSource))) {
					result = false;
				}
			}
		}

		return result;
	}

	@Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException {
        RepoxContextUtil.useRepoxManagerTest();
          TaskManager taskManager = RepoxContextUtil.getRepoxManager().getTaskManager();
            taskManager.stop();
        String xmlBasedir = RepoxContextUtil.getRepoxManager().getConfiguration().getXmlConfigPath();
        configurationFile = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME);
        configurationFileAlt = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME_ALT);
    }

	@Test
	public void testClasses() throws IOException, DocumentException {
		MetadataTransformationManager transformationManager = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager();
		DataProviderManager dataProviderManager = new DataProviderManager(configurationFile, transformationManager);
		List<DataProvider> dataProviders = getTestDataProviders();
		dataProviderManager.saveDataProviders(dataProviders);

		List<DataProvider> loadedDataProviders = new ArrayList<DataProvider>(dataProviderManager.loadDataProviders());

		Assert.assertTrue(dataProvidersEqual(dataProviders, loadedDataProviders));

        Element dataSourcesElement = XmlUtil.getRootElement(configurationFile);
        DataProviderManager dataProviderManagerReloaded = new DataProviderManager(configurationFileAlt, transformationManager);
		dataProviderManagerReloaded.saveDataProviders(loadedDataProviders);
		Element loadedDataSourcesElement = XmlUtil.getRootElement(configurationFileAlt);

		Assert.assertTrue(XmlUtil.elementsEqual(dataSourcesElement, loadedDataSourcesElement));
	}

	/*@Test
	public void testXml() throws IOException, DocumentException {
		MetadataTransformationManager transformationManager = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager();
		DataProviderManager dataProviderManager = new DataProviderManager(configurationFile, transformationManager);
		List<DataProvider> dataProviders = getTestDataProviders();
		dataProviderManager.saveDataProviders(dataProviders);
		Element dataSourcesElement = XmlUtil.getRootElement(configurationFile);

		List<DataProvider> loadedDataProviders = new ArrayList<DataProvider>(dataProviderManager.loadDataProviders());
		DataProviderManager dataProviderManagerReloaded = new DataProviderManager(configurationFileAlt, transformationManager);
		dataProviderManagerReloaded.saveDataProviders(loadedDataProviders);
		Element loadedDataSourcesElement = XmlUtil.getRootElement(configurationFileAlt);

		Assert.assertTrue(XmlUtil.elementsEqual(dataSourcesElement, loadedDataSourcesElement));
	}*/

	@After
	public void tearDown() {
		if(configurationFile.exists()) {
			configurationFile.delete();
		}

		if(configurationFileAlt.exists()) {
			configurationFileAlt.delete();
		}
	}
}
