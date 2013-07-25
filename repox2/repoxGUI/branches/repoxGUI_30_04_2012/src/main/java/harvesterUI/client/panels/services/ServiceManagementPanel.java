package harvesterUI.client.panels.services;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.externalServices.ESManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.externalServices.ExternalServiceUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:31
 */
public class ServiceManagementPanel extends ContentPanel {

    private Grid<ExternalServiceUI> servicesGrid;
    private NewServiceDialog newServiceDialog;
    private ESManagementServiceAsync esManagementServiceAsync;

    public ServiceManagementPanel() {
        setHeading(HarvesterUI.CONSTANTS.externalServicesManager());
        setIcon(HarvesterUI.ICONS.externalServicesIcon());

        esManagementServiceAsync = (ESManagementServiceAsync) Registry.get(HarvesterUI.ES_MANAGEMENT_SERVICE);

        createServicesGrid();
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FitLayout());
    }

    private void createServicesGrid() {
        newServiceDialog = new NewServiceDialog();

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig("name",HarvesterUI.CONSTANTS.name(),100);
        configs.add(column);

        column = new ColumnConfig("uri",HarvesterUI.CONSTANTS.uri(),250);
        configs.add(column);
        column = new ColumnConfig("type",HarvesterUI.CONSTANTS.type(),100);
        configs.add(column);

        ListStore<ExternalServiceUI> store = new ListStore<ExternalServiceUI>();

        ColumnModel cm = new ColumnModel(configs);

        servicesGrid = new Grid<ExternalServiceUI>(store, cm);
        servicesGrid.getView().setForceFit(true);
        servicesGrid.setAutoExpandColumn("id");
        servicesGrid.getView().setEmptyText(HarvesterUI.CONSTANTS.noServicesAvailable());
        servicesGrid.getStore().setDefaultSort("name", Style.SortDir.DESC);
        add(servicesGrid);

        Menu contextMenu = new Menu();

        MenuItem updateUser = new MenuItem();
        updateUser.setText(HarvesterUI.CONSTANTS.editExternalService());
        updateUser.setIcon(HarvesterUI.ICONS.form());
        updateUser.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                mask(HarvesterUI.CONSTANTS.loadingServiceData());
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        if(servicesGrid.getSelectionModel().getSelectedItems().size() > 0) {
                            ExternalServiceUI selected = servicesGrid.getSelectionModel().getSelectedItems().get(0);
                            newServiceDialog.show();
                            newServiceDialog.edit(selected);
                            unmask();
                        }
                    }
                });
            }
        });
        contextMenu.add(updateUser);

        MenuItem remove = new MenuItem();
        remove.setText(HarvesterUI.CONSTANTS.removeExternalService());
        remove.setIcon(HarvesterUI.ICONS.delete());
        remove.addSelectionListener(new SelectionListener<MenuEvent>() {
            final SelectionListener<ButtonEvent> removeServiceListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                        AsyncCallback<String> callback = new AsyncCallback<String>() {
                            public void onFailure(Throwable caught) {
                                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                            }
                            public void onSuccess(String result) {
                                Dispatcher.forwardEvent(AppEvents.ViewServiceManager);
                                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteServices(), HarvesterUI.CONSTANTS.deleteServiceSuccess());
                            }
                        };
                        List<ExternalServiceUI> selected = servicesGrid.getSelectionModel().getSelectedItems();
                        esManagementServiceAsync.removeExternalService(selected, callback);
                }
            };

            @Override
            public void componentSelected(MenuEvent ce) {
                if(servicesGrid.getSelectionModel().getSelectedItems().size() > 0) {
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteServiceConfirmMessage(), removeServiceListener);
                }
            }

        });
        contextMenu.add(remove);

        servicesGrid.setContextMenu(contextMenu);

        ToolBar toolBar = new ToolBar();
        Button add = new Button(HarvesterUI.CONSTANTS.addExternalService());
        add.setIcon(HarvesterUI.ICONS.add());
        add.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                mask(HarvesterUI.CONSTANTS.loadingServiceData());
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        newServiceDialog.show();
                        newServiceDialog.resetValues();
                        newServiceDialog.addField();
                        unmask();
                    }
                });
            }
        });
        toolBar.add(add);
        setTopComponent(toolBar);
        setButtonAlign(Style.HorizontalAlignment.CENTER);
    }

    public void reloadExternalServices() {
        AsyncCallback<List<ExternalServiceUI>> callback = new AsyncCallback<List<ExternalServiceUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<ExternalServiceUI> eServices) {
                servicesGrid.getStore().removeAll();
                servicesGrid.getStore().add(eServices);
            }
        };
        esManagementServiceAsync.getAllExternalServices(callback);
    }
}
