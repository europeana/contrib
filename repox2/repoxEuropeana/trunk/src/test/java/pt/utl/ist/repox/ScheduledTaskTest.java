package pt.utl.ist.repox;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.task.DataSourceIngestTask;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.task.ScheduledTask.Frequency;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilEuropeana;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledTaskTest {
	private ScheduledTask goodTask;

	@Before
	public void setUp(){
		try {
            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuropeana());
            ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest();

			//id min hour day month weekday taskClass Parameters...

			// Jan 1st 00:00
			Calendar firstRun = new GregorianCalendar(2009, 0, 1, 0, 0);
			goodTask = new ScheduledTask("1", firstRun, Frequency.ONCE, null, new DataSourceIngestTask("1", "1", "false"));
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
		GregorianCalendar isTime = new GregorianCalendar(2009, 0, 1, 0, 0);
		Assert.assertTrue(goodTask.isTimeToRun(isTime));
	}
}
