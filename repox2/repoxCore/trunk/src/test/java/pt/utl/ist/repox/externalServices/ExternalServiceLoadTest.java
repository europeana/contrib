package pt.utl.ist.repox.externalServices;

import org.junit.Assert;
import org.junit.Test;
import pt.utl.ist.repox.RepoxConfigurationDefault;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.PropertyUtil;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;

import java.io.File;
import java.util.Properties;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 03-10-2012
 * Time: 18:46
 */
public class ExternalServiceLoadTest {

    private File configurationFile;
    private File repositoryPath;

    private RepoxConfigurationDefault configurationDefault;
    public static final String CONFIG_FILE = "configuration.properties";
    private final String OUTPUT_CONFIGURATION_FILE_NAME = RepoxContextUtilDefault.DATA_PROVIDERS_FILENAME;

    @Test
    public void testReplaceSemantics() {
        try {
            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
            Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
            configurationDefault = new RepoxConfigurationDefault(configurationProperties);

            MetadataTransformationManager transformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataTransformationManager();
            MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataSchemaManager();

            repositoryPath = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getRepositoryPath());

            String xmlBasedir = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath();
            configurationFile = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME);

            File oldTasksFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath(), RepoxContextUtilDefault.OLD_TASKS_FILENAME);
            DataManagerDefault dataManager = new DataManagerDefault(configurationFile, transformationManager,
                    metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
