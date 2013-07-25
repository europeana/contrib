package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import harvesterUI.client.HarvesterUI;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 12/06/12
 * Time: 18:49
 */
public class MDRContainer extends ContentPanel{

    private ToggleButton schemas,mappings;
    private MappingsPanel mappingsPanel;
    private SchemasPanel schemasPanel;
    private ToolBar topToolbar;

    public MDRContainer() {
        setHeading(HarvesterUI.CONSTANTS.schemaMapper());
        setIcon(HarvesterUI.ICONS.schema_mapper_icon());
        setLayout(new FitLayout());

        topToolbar = new ToolBar();
        topToolbar.setSpacing(5);
        topToolbar.addStyleName("topNavToolbar");
        getHeader().addTool(topToolbar);

        mappingsPanel = new MappingsPanel();
        schemasPanel = new SchemasPanel();

        schemas = new ToggleButton(HarvesterUI.CONSTANTS.schemas(),HarvesterUI.ICONS.schemas_icon());
        schemas.setAllowDepress(false);
        mappings = new ToggleButton(HarvesterUI.CONSTANTS.mappings(),HarvesterUI.ICONS.mappings_icon());
        mappings.setAllowDepress(false);
        mappings.setToggleGroup("mdr_panels_button_group");
        schemas.setToggleGroup("mdr_panels_button_group");
        schemas.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                removeAll();
                add(schemasPanel);
                layout();
            }
        });
        mappings.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                removeAll();
                add(mappingsPanel);
                layout();
            }
        });

        topToolbar.add(new FillToolItem());
        topToolbar.add(schemas);
        topToolbar.add(mappings);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        schemas.fireEvent(Events.Select);
        schemas.toggle(true);
    }
}
