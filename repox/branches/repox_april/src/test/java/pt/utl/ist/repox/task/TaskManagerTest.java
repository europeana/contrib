package pt.utl.ist.repox.task;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.task.ScheduledTask.Frequency;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class TaskManagerTest {
	TaskManager taskManager;
	private ScheduledTask task1;
	private ScheduledTask task2;
	
	@Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException {

        RepoxContextUtil.useRepoxManagerTest();
        taskManager = RepoxContextUtil.getRepoxManager().getTaskManager();
        taskManager.stop();
        String dataSourceId = "1";
        Calendar firstRun = new GregorianCalendar(2015, 0, 1, 0, 0);
        task1 = new ScheduledTask(1, firstRun, Frequency.ONCE, null, new DataSourceIngestTask(dataSourceId, "1", "false"));
        task2 = new ScheduledTask(2, firstRun, Frequency.ONCE, null, new DataSourceIngestTask(dataSourceId, "1", "false"));

    }

	@Test
	public void testSaveAndLoadScheduledTask() throws IOException {
		taskManager.saveTask(task1);
		Assert.assertNotNull(taskManager.getTask(task1.getId()));
        //Assert.assertEquals(1,1);
	}

	@Test
	public void testDeleteScheduledTask() throws IOException {
		taskManager.saveTask(task1);
		taskManager.saveTask(task2);
		Assert.assertTrue(taskManager.deleteTask(task1.getId()));
		Assert.assertTrue(taskManager.deleteTask(task2.getId()));
	}
	
	@After
	public void tearDown() {
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
