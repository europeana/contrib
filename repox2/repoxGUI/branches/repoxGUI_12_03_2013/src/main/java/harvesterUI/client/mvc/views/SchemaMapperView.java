package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.History;
import harvesterUI.client.core.AppEvents;
//import harvesterUI.client.panels.mdr.SchemaMapperContainer;
import harvesterUI.client.panels.mdr.MDRContainer;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:32
 */
public class SchemaMapperView extends View {

    private MDRContainer schemaMapperContainer;

    public SchemaMapperView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.ViewSchemaMapper) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            centerPanel.add(schemaMapperContainer);
            centerPanel.layout();
        } else if(event.getType() == AppEvents.ReloadTransformations ||
                event.getType() == AppEvents.ReloadSchemas){
            if(schemaMapperContainer.isVisible())
                History.fireCurrentHistoryState();
        }
    }

    @Override
    protected void initialize() {
        schemaMapperContainer = new MDRContainer();
    }
}
