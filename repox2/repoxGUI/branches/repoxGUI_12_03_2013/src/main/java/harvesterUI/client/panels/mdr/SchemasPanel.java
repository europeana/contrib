package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.mdr.forms.ShowConnectedDSDialog;
import harvesterUI.client.panels.overviewGrid.contextMenus.AggregatorContextMenu;
import harvesterUI.client.panels.overviewGrid.contextMenus.DataProviderWithSingleDSContextMenu;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;
import harvesterUI.shared.users.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:31
 */
public class SchemasPanel extends ContentPanel {

    private TransformationsServiceAsync service;
    private ToolBar topToolbar;
    private TreeGrid<SchemaTreeUI> grid;

    public SchemasPanel() {
        service = (TransformationsServiceAsync)Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        setLayout(new FitLayout());
        setHeaderVisible(false);
        setBodyBorder(false);
        setBorders(false);

        topToolbar = new ToolBar();
        setTopComponent(topToolbar);

        createTransformationsGrid();

        // todo: enhance code
        // only show when user is admin or normal
        UserRole userRole = HarvesterUI.UTIL_MANAGER.getLoggedUserRole();
        if(userRole == UserRole.ADMIN || userRole == UserRole.NORMAL){

            Button addSchema = new Button();
            addSchema.setText(HarvesterUI.CONSTANTS.addSchema());
            addSchema.setIcon(HarvesterUI.ICONS.schema_new());
            addSchema.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog);
                }
            });
            topToolbar.add(addSchema);

            final SchemasOperations schemasOperations = new SchemasOperations(grid);
            grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<SchemaTreeUI>() {
                @Override
                public void selectionChanged(SelectionChangedEvent<SchemaTreeUI> se) {
                    if (se.getSelectedItem() != null) {
                        if(se.getSelectedItem() instanceof SchemaUI)
                            schemasOperations.showTransformationButtons(topToolbar,true);
                        else{
                            schemasOperations.showTransformationButtons(topToolbar,false);
                            schemasOperations.hideDeleteButton(topToolbar);
                        }
                    } else {
                        schemasOperations.hideTransformationButtons(topToolbar);
                    }
                }
            });
        }
    }

    private void createTransformationsGrid(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<SchemaTreeUI> sm = new CheckBoxSelectionModel<SchemaTreeUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        ColumnConfig cc = new ColumnConfig("schema",HarvesterUI.CONSTANTS.schema(),100);
        cc.setRenderer(new TreeGridCellRenderer<ModelData>());
        configs.add(cc);

        GridCellRenderer linkRenderer = new GridCellRenderer<SchemaTreeUI>() {
            public Object render(final SchemaTreeUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SchemaTreeUI> store, Grid<SchemaTreeUI> grid) {
                if(property.equals("namespace") && model instanceof SchemaVersionUI){
                    String link = model.getParent().get("namespace");
                    if(link.startsWith("http://"))
                        return "<a href='"+link+"' target='_blank'>"+link+"</a>";
                    else
                        return link;
                }

                String link = model.get(property);
                if(link.startsWith("http://"))
                    return "<a href='"+link+"' target='_blank'>"+link+"</a>";
                else
                    return link;
            }
        };

//        configs.add(new ColumnConfig("version","Version",75));

        ColumnConfig column = new ColumnConfig("namespace",HarvesterUI.CONSTANTS.namespace(),150);
        column.setRenderer(linkRenderer);
        configs.add(column);

        column = new ColumnConfig("xsdLink",HarvesterUI.CONSTANTS.xsdLink(),150);
        column.setRenderer(linkRenderer);
        configs.add(column);

        // Usage Column dialog button
        column = new ColumnConfig("usage","Used",75);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setRenderer(new GridCellRenderer<SchemaTreeUI>() {
            public Object render(final SchemaTreeUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SchemaTreeUI> store, Grid<SchemaTreeUI> grid) {
                // Sum schema version numbers if it is a schema
                if(model instanceof SchemaUI){
                    SchemaUI schemaUI = (SchemaUI)model;
                    int total = 0;
                    for(SchemaVersionUI schemaVersionUI : schemaUI.getSchemaVersions()){
                        if(schemaVersionUI.getMdrDataStatistics() != null){
                            total += schemaVersionUI.getMdrDataStatistics().getNumberTimesUsed();
                        }
                    }
                    return total;
                }

                if(model.getMdrDataStatistics() == null || model.getMdrDataStatistics().getNumberTimesUsed() == 0)
                    return "0";

                ImageButton imageButton = new ImageButton();
                imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        new ShowConnectedDSDialog(model.getMdrDataStatistics().getUsedInList()).showAndCenter();
                    }
                });

                ToolTipConfig tooltipConfig = new ToolTipConfig();
                tooltipConfig.setShowDelay(10);
                String viewLog = "<div style='text-align: center;'>" + "See Data Sets used in this schema" + "</div>";
                imageButton.setIcon(HarvesterUI.ICONS.table());
                tooltipConfig.setTitle("Schema Usage List");
                tooltipConfig.setText(viewLog);
                imageButton.setToolTip(tooltipConfig);

                LayoutContainer container = new LayoutContainer();
                HBoxLayout layout = new HBoxLayout();
                layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                layout.setPack(BoxLayout.BoxLayoutPack.CENTER);
                container.setLayout(layout);
                container.add(new LabelToolItem(String.valueOf(model.getMdrDataStatistics().getNumberTimesUsed())), new HBoxLayoutData(new Margins(0, 10, 0, 0)));
                container.add(imageButton,new HBoxLayoutData(new Margins(0, 0, 0, 0)));

                return container;
            }
        });
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

//        RpcProxy<PagingLoadResult<SchemaUI>> proxy = new RpcProxy<PagingLoadResult<SchemaUI>>() {
//            @Override
//            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<SchemaUI>> callback) {
//                service.getPagedSchemas((PagingLoadConfig) loadConfig, callback);
//            }
//        };

        // loader
//        final PagingLoader<PagingLoadResult<SchemaUI>> loader = new BasePagingLoader<PagingLoadResult<SchemaUI>>(
//                proxy);
//        loader.setRemoteSort(true);

        final TreeStore<SchemaTreeUI> store = new TreeStore<SchemaTreeUI>();

//        PagingToolBar toolBar = new PagingToolBar(25);
//        toolBar.bind(loader);

//        setBottomComponent(toolBar);

        grid = new TreeGrid<SchemaTreeUI>(store, cm);
        grid.getView().setForceFit(true);
        grid.setSelectionModel(sm);
        grid.setStripeRows(true);
        grid.setLoadMask(true);
        grid.setTrackMouseOver(false);
        grid.addPlugin(sm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noTransformationsAvailable());
        grid.addListener(Events.Attach, new Listener<GridEvent<ScheduledTaskUI>>() {
            public void handleEvent(GridEvent<ScheduledTaskUI> be) {
                mask("Loading Schemas...");
                AsyncCallback<List<SchemaTreeUI>> callback = new AsyncCallback<List<SchemaTreeUI>>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(List<SchemaTreeUI> schemasTreeData) {
                        grid.getStore().removeAll();
                        store.add(schemasTreeData,true);
                        grid.expandAll();
                        unmask();
                    }
                };
                TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
                service.getSchemasTree(callback);
            }
        });
        add(grid);

        grid.setIconProvider(new ModelIconProvider<SchemaTreeUI>() {
            public AbstractImagePrototype getIcon(SchemaTreeUI model) {
                if (model instanceof SchemaUI) {
                    return HarvesterUI.ICONS.schema();
                }else if (model instanceof SchemaVersionUI) {
                    return HarvesterUI.ICONS.schema_version();
                }
                return null;
            }
        });

        Menu contextMenu = new Menu();
        grid.setContextMenu(contextMenu);
        grid.addListener(Events.ContextMenu, new Listener<TreeGridEvent<ModelData>>() {
            public void handleEvent(TreeGridEvent<ModelData> event) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0){
                    SchemaTreeUI selectedNode = grid.getSelectionModel().getSelectedItems().get(0);
                    if(selectedNode != null){
                        if(selectedNode instanceof SchemaUI)
                            grid.setContextMenu(new SchemaContextMenu(grid));
                        else if(selectedNode instanceof SchemaVersionUI) {
                            grid.setContextMenu(new SchemaVersionContextMenu(grid));
                        }
                    }
                }
            }
        });
    }

//    @Override
//    protected void onAttach() {
//        super.onAttach();
//    }

}
