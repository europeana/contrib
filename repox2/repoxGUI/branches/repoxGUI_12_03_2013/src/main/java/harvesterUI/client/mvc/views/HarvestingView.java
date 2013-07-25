package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.harvesting.RunningTasksList;
import harvesterUI.client.panels.harvesting.ScheduledTasksList;
import harvesterUI.client.panels.harvesting.calendar.CalendarTaskManager;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 23-03-2011
 * Time: 12:24
 */
public class HarvestingView extends View {

    private CalendarTaskManager calendarTaskManager;
    private ScheduledTasksList scheduledTasksList;
    private RunningTasksList runningTasksList;

    public HarvestingView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event){
        if (event.getType() == AppEvents.ViewScheduledTasksCalendar){
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            centerPanel.add(calendarTaskManager);
            centerPanel.layout();
        }
        else if (event.getType() == AppEvents.ViewScheduledTasksList){
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            centerPanel.add(scheduledTasksList);
            centerPanel.layout();
        }
        else if (event.getType() == AppEvents.ViewRunningTasksList) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            runningTasksList.updateRunningTasks();
            centerPanel.add(runningTasksList);
            centerPanel.layout();
        }
    }

    @Override
    protected void initialize(){
        calendarTaskManager = new CalendarTaskManager();
        Registry.register("calendarTaskManager", calendarTaskManager);

        scheduledTasksList = new ScheduledTasksList();
        runningTasksList = new RunningTasksList();
    }
}
