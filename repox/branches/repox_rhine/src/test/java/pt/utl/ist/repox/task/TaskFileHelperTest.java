package pt.utl.ist.repox.task;

import eu.europeana.definitions.domain.Language;
import eu.europeana.repox2sip.Repox2SipException;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.data.AggregatorRepox;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.data.dataSource.IdGenerated;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.marc.Iso2709FileExtract;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.CompareUtil;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class TaskFileHelperTest {
	TaskManager taskManager;
	DataProvider newDP;
	DataSourceIngestTask dSIngestTask1;
	DataSourceIngestTask dSIngestTask2;
	DataSourceExportTask dSExportTask1;
	DataSourceExportTask dSExportTask2;
	
	@Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, Repox2SipException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/Test-configuration.properties"));
        String dummydir = properties.getProperty("dummy.dir");
        RepoxContextUtil.useRepoxManagerTest();
        taskManager = RepoxContextUtil.getRepoxManager().getTaskManager();
        taskManager.stop(); //avoid starting execution of Tasks for testing purposes

        ArrayList<DataSource> dataSources = new ArrayList<DataSource>();

        AggregatorRepox aggregatorRepox = new AggregatorRepox(null, "agg1", "dummy", null, null, null);
        newDP = new DataProvider(aggregatorRepox, "dummyDP", "dummyDP", "testing purposes only", dataSources);
        dataSources.add(new DataSourceOai(newDP, "dummyDSIngest", "test DS", MetadataFormat.oai_dc.toString(),
                "http://dummy.oai.rp", "noset", new IdGenerated(), null, Language.PT, null, null, -1));
        dataSources.add(new DataSourceDirectoryImporter(newDP, "dummyDSExport1", "test DS", MetadataFormat.oai_dc.toString(),
                new Iso2709FileExtract("pt.utl.ist.marc.iso2709.IteratorIso2709"), pt.utl.ist.repox.marc.CharacterEncoding.UTF_8,
                dummydir, new IdGenerated(), null, null, null, Language.PT, null, null, -1));
        dataSources.add(new DataSourceDirectoryImporter(newDP, "dummyDSExport2", "test DS", MetadataFormat.oai_dc.toString(),
                new Iso2709FileExtract("pt.utl.ist.marc.iso2709.IteratorIso2709"), pt.utl.ist.repox.marc.CharacterEncoding.UTF_8,
                dummydir, new IdGenerated(), null, null, null, Language.PT, null, null, -1));
        RepoxContextUtil.getRepoxManager().getDataManager().saveDataProvider(newDP);


        Calendar now = new GregorianCalendar(2015, 0, 1, 0, 0);
        //Calendar now = Calendar.getInstance();
        //now.set(Calendar.MILLISECOND, 0); // Because millisecond is not saved to file, not setting this would cause difference after loading

       // now.set(Calendar.HOUR, 1);

        dSIngestTask1 = new DataSourceIngestTask( "1","dummyDSIngest", "false", now, null, Task.Status.OK, 5, 1, 120);
        dSIngestTask2 = new DataSourceIngestTask( "1","dummyDSIngest", "false", now, null, Task.Status.OK, 5, 1, 120);
        dSExportTask1 = new DataSourceExportTask( "1","dummyDSExport1", dummydir, "1000", now, null, Task.Status.OK, 5, 1, 120);
        dSExportTask2 = new DataSourceExportTask( "1","dummyDSExport2", dummydir, "1000", now, null, Task.Status.OK, 5, 1, 120);

    }
	
	@Test
	public void compareEqualTasks() {
		//Assert.assertEquals(1,1);
        Assert.assertTrue(CompareUtil.compareObjectsAndNull(dSIngestTask1, dSIngestTask2));
	}

	@Test
	public void compareNotEqualTasks() {
		Assert.assertTrue(!CompareUtil.compareObjectsAndNull(dSExportTask1, dSExportTask2));
	}

	@Test
	public void testSaveAndLoadTasks() throws IOException, ClassNotFoundException, DocumentException, NoSuchMethodException, ParseException {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(dSIngestTask1);
		tasks.add(dSExportTask1);
		tasks.add(dSExportTask2);

		TaskFileHelper.saveTasks(taskManager.getRunningTasksFile(), tasks);
		List<Task> loadedTasks = TaskFileHelper.loadTasks(taskManager.getRunningTasksFile());
		
		Assert.assertTrue(CompareUtil.compareListsAndNull(tasks, loadedTasks));
	}

	@After
	public void tearDown() throws IOException, DocumentException, Repox2SipException {
		RepoxContextUtil.getRepoxManager().getDataManager().deleteDataProvider(newDP.getId());
		
		List<Integer> ids = new ArrayList<Integer>();
        for (Object o : taskManager.getScheduledTasks()) {
            ScheduledTask currentScheduledTask = (ScheduledTask) o;
            ids.add(currentScheduledTask.getId());
        }

		for (Integer currentId : ids) {
			try {
				taskManager.deleteTask(currentId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
