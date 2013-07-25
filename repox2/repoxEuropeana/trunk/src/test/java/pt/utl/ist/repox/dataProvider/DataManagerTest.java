package pt.utl.ist.repox.dataProvider;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.RepoxConfigurationDefault;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class DataManagerTest {
	private final String OUTPUT_CONFIGURATION_FILE_NAME = RepoxContextUtilDefault.DATA_PROVIDERS_FILENAME;
	private final String OUTPUT_CONFIGURATION_FILE_NAME_ALT = "dataProviders.temp.xml";
    public static final String CONFIG_FILE = "Test-configuration.properties";

	private File configurationFile;
	private File configurationFileAlt;
	private File repositoryPath;

    private RepoxConfigurationDefault configurationDefault;

	private List<DataProvider> getTestDataProviders() {
		List<DataProvider> dataProviders = new ArrayList<DataProvider>();

		List<DataSource> dataSources = new ArrayList<DataSource>();
		DataSource source1 = new DataSourceOai(null, "source1", "test", "schema", "namespace", MetadataFormat.oai_dc.toString(),
				"http://dummyurl1", "set1", new IdGenerated(), null);

		DataSource source2 = new DataSourceOai(null, "source2", "test", "schema", "namespace", MetadataFormat.oai_dc.toString(),
				"http://dummyurl2", "set2", new IdGenerated(), null);

        HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();
        dataSourceContainers.put(source1.getId(), new DataSourceContainerEuropeana(source1, "nameCode1", "name1", "exportPath1"));
        dataSourceContainers.put(source2.getId(), new DataSourceContainerEuropeana(source2, "nameCode2", "name2", "exportPath2"));

		DataProvider provider = new DataProvider("provider1", "provider1", "pt", "Dummy Data Provider", dataSourceContainers);
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
                && dataSourceContainersEqual(new HashMap<String, DataSourceContainer>(sourceDataProvider.getDataSourceContainers()),
                new HashMap<String, DataSourceContainer>(targetDataProvider.getDataSourceContainers()));
	}


	private boolean dataSourceContainersEqual(HashMap<String, DataSourceContainer> dataSourceContainers, HashMap<String, DataSourceContainer> otherDataSourceContainers) {
		boolean result = true;
		if(dataSourceContainers.size() != otherDataSourceContainers.size()) {
			result = false;
		}
		else {
			if(dataSourceContainers.keySet().equals(otherDataSourceContainers.keySet())){
                result = true;
            }
		}

		return result;
	}

	@Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException {

        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuropeana());

        String xmlBasedir = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath();
        repositoryPath = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getRepositoryPath());
        configurationFile = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME);
        configurationFileAlt = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME_ALT);

        Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
        configurationDefault = new RepoxConfigurationDefault(configurationProperties);
    }

	@Test
	public void testClasses() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException {
		MetadataTransformationManager transformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataTransformationManager();
		MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataSchemaManager();
		File oldTasksFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath(), RepoxContextUtilDefault.OLD_TASKS_FILENAME);
        DataManagerDefault dataManager = new DataManagerDefault(configurationFile, transformationManager,metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);

		List<DataProvider> dataProviders = getTestDataProviders();
		for(DataProvider dp: dataProviders) {
			dataManager.saveData();
		}

		DataManagerDefault dataManager2 = new DataManagerDefault(configurationFile, transformationManager, metadataSchemaManager ,repositoryPath, oldTasksFile,configurationDefault);

		List<DataProvider> loadedDataProviders = dataManager2.getDataProviders();

		Assert.assertTrue(dataProvidersEqual(dataProviders, loadedDataProviders));
	}

	@Test
	public void testXml() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException {
		MetadataTransformationManager transformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataTransformationManager();
        MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataSchemaManager();
		File oldTasksFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath(), RepoxContextUtilDefault.OLD_TASKS_FILENAME);
        DataManagerDefault dataManager = new DataManagerDefault(configurationFile, transformationManager, metadataSchemaManager ,repositoryPath, oldTasksFile,configurationDefault);
		List<DataProvider> dataProviders = getTestDataProviders();
		for(DataProvider dp: dataProviders) {
			dataManager.saveData();
		}

		Element dataSourcesElement = XmlUtil.getRootElement(configurationFile);

		DataManagerDefault dataManager2 = new DataManagerDefault(configurationFile, transformationManager,metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);
		List<DataProvider> loadedDataProviders = dataManager2.getDataProviders();

		DataManagerDefault dataManagerReloaded = new DataManagerDefault(configurationFileAlt, transformationManager, metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);
		for(DataProvider dp: loadedDataProviders) {
			dataManagerReloaded.saveData();
		}
		
		Element loadedDataSourcesElement = XmlUtil.getRootElement(configurationFileAlt);

		Assert.assertTrue(XmlUtil.elementsEqual(dataSourcesElement, loadedDataSourcesElement));
	}

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
