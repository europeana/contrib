package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.TransformationsOperations;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.shared.TransformationUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:31
 */
public class SchemaMapperPanel extends ContentPanel {

    private RepoxServiceAsync service;

    public SchemaMapperPanel() {
        service = (RepoxServiceAsync)Registry.get(HarvesterUI.REPOX_SERVICE);
        setIcon(HarvesterUI.ICONS.schema_mapper_icon());
        setHeading(HarvesterUI.CONSTANTS.schemaMapper());
        setLayout(new FitLayout());
        createTransformationsGrid();
    }

    private void createTransformationsGrid(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<TransformationUI> sm = new CheckBoxSelectionModel<TransformationUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        configs.add(new ColumnConfig("identifier",HarvesterUI.CONSTANTS.id(),75));
        configs.add(new ColumnConfig("description",HarvesterUI.CONSTANTS.description(),100));
        ColumnConfig column = new ColumnConfig("srcFormat",HarvesterUI.CONSTANTS.sourceFormat(),55);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);
        column = new ColumnConfig("destFormat",HarvesterUI.CONSTANTS.destinationFormat(),65);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);
        configs.add(new ColumnConfig("schema",HarvesterUI.CONSTANTS.schema(),150));
        configs.add(new ColumnConfig("mtdNamespace",HarvesterUI.CONSTANTS.metadataNamespace(),150));
        configs.add(new ColumnConfig("xslFilePath",HarvesterUI.CONSTANTS.xsl(),75));

        column = new ColumnConfig("download",HarvesterUI.CONSTANTS.download(),40);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setRenderer(new GridCellRenderer<TransformationUI>() {
            public Object render(final TransformationUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<TransformationUI> store, Grid<TransformationUI> grid) {
                ImageButton imageButton = new ImageButton();
                imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        Window.open(GWT.getModuleBaseURL() + "transformationfiledownload?fileName=" +
                                model.getIdentifier(), "", "");
                    }
                });

                ToolTipConfig tooltipConfig = new ToolTipConfig();
                tooltipConfig.setShowDelay(1000);
                String viewLog = "<div style='text-align: center;'>" + HarvesterUI.CONSTANTS.download()+" "+HarvesterUI.CONSTANTS.xslFile() + "</div>";
                imageButton.setIcon(HarvesterUI.ICONS.xsl_image());
                tooltipConfig.setTitle(HarvesterUI.CONSTANTS.download());
                tooltipConfig.setText(viewLog);
                imageButton.setToolTip(tooltipConfig);

                LayoutContainer container = new LayoutContainer();
                HBoxLayout layout = new HBoxLayout();
                layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                layout.setPack(BoxLayout.BoxLayoutPack.CENTER);
                container.setLayout(layout);
                container.add(imageButton,new HBoxLayoutData(new Margins(0, 0, 0, 0)));

                return container;
            }
        });
        configs.add(column);

        final ToolBar topToolbar = new ToolBar();
        Button addTransformationButton = new Button();
        addTransformationButton.setText(HarvesterUI.CONSTANTS.addTransformation());
        addTransformationButton.setIcon(HarvesterUI.ICONS.add());
        addTransformationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog);
            }
        });
        topToolbar.add(addTransformationButton);

        setTopComponent(topToolbar);

        ColumnModel cm = new ColumnModel(configs);

        RpcProxy<PagingLoadResult<TransformationUI>> proxy = new RpcProxy<PagingLoadResult<TransformationUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<TransformationUI>> callback) {
                service.getPagedTransformations((PagingLoadConfig) loadConfig, callback);
            }
        };

        // loader
        final PagingLoader<PagingLoadResult<TransformationUI>> loader = new BasePagingLoader<PagingLoadResult<TransformationUI>>(
                proxy);
        loader.setRemoteSort(true);

        ListStore<TransformationUI> store = new ListStore<TransformationUI>(loader);

        PagingToolBar toolBar = new PagingToolBar(25);
        toolBar.bind(loader);

        setBottomComponent(toolBar);

        final Grid<TransformationUI> grid = new Grid<TransformationUI>(store, cm);
        grid.getView().setForceFit(true);
        grid.setSelectionModel(sm);
        grid.setStripeRows(true);
        grid.setLoadMask(true);
        grid.setTrackMouseOver(false);
        grid.addPlugin(sm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noTransformationsAvailable());
        grid.addListener(Events.Attach, new Listener<GridEvent<ScheduledTaskUI>>() {
            public void handleEvent(GridEvent<ScheduledTaskUI> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                config.setLimit(25);

                Map<String, Object> state = grid.getState();
                if (state.containsKey("offset")) {
                    int offset = (Integer)state.get("offset");
                    int limit = (Integer)state.get("limit");
                    config.setOffset(offset);
                    config.setLimit(limit);
                }
                if (state.containsKey("sortField")) {
                    config.setSortField((String)state.get("sortField"));
                    config.setSortDir(Style.SortDir.valueOf((String) state.get("sortDir")));
                }
                loader.load(config);
            }
        });

        final TransformationsOperations transformationsOperations = new TransformationsOperations(grid);

        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<TransformationUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<TransformationUI> se) {
                if(se.getSelectedItem() != null) {
                    transformationsOperations.showTransformationButtons(topToolbar);
                } else {
                    transformationsOperations.hideTransformationButtons(topToolbar);
                }
            }
        });
        add(grid);
    }

}
