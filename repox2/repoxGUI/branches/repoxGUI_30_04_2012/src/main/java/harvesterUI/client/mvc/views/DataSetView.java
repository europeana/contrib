package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.dataSourceView.DataSetViewInfo;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 21-03-2011
 * Time: 16:33
 */
public class DataSetView extends View {

    private DataSetViewInfo dataSetViewInfo;

    public DataSetView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.ViewDataSetInfo) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            if(event.getData() instanceof DataSourceUI) {
                DataSourceUI dataSourceUI = (DataSourceUI) event.getData();
                dataSetViewInfo.createForm(dataSourceUI);
                UtilManager.unmaskCentralPanel();
            }

            centerPanel.add(dataSetViewInfo);
            centerPanel.layout();
        }
    }

    @Override
    protected void initialize(){
        dataSetViewInfo = new DataSetViewInfo();
    }
}

