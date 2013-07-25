package pt.utl.ist.repox;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.task.DataSourceIngestTask;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.task.ScheduledTask.Frequency;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ScheduledTaskTest {
	private ScheduledTask goodTask;
    TaskManager taskManager;

	@Before
	public void setUp(){
        try {
			RepoxContextUtil.useRepoxManagerTest();
            taskManager = RepoxContextUtil.getRepoxManager().getTaskManager();
            taskManager.stop();
			//id min hour day month weekday taskClass Parameters...

			// Jan 1st 00:00
			Calendar firstRun = new GregorianCalendar(2015, 0, 1, 0, 0);
			goodTask = new ScheduledTask(1, firstRun, Frequency.ONCE, null, new DataSourceIngestTask("1", "1", "false"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIsNotTimeToRun() {
		GregorianCalendar notTime = new GregorianCalendar();
		notTime.set(GregorianCalendar.MONTH, 5);
		Assert.assertFalse(goodTask.isTimeToRun(notTime));
	}
	
	@Test
	public void testIsTimeToRun() {
		GregorianCalendar isTime = new GregorianCalendar(2015, 0, 1, 0, 0);
		Assert.assertTrue(goodTask.isTimeToRun(isTime));
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
