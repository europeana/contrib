package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.History;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.mdr.SchemaMapperPanel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:32
 */
public class SchemaMapperView extends View {

    private SchemaMapperPanel schemaMapperPanel;

    public SchemaMapperView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.ViewSchemaMapper) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            centerPanel.add(schemaMapperPanel);
            centerPanel.layout();
        } else if(event.getType() == AppEvents.ReloadTransformations){
            History.fireCurrentHistoryState();
        }
    }

    @Override
    protected void initialize() {
        schemaMapperPanel = new SchemaMapperPanel();
    }
}
