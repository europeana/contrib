package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.users.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:51
 */
public class UserManagementGrid extends ContentPanel{

    private UserManagementServiceAsync service;
    private ListStore<User>  store;
    private NewUserDialog newUserDialog;
    private UpdateUserDialog updateUserDialog;

    public UserManagementGrid() {
        setHeading(HarvesterUI.CONSTANTS.userManagement());
        setFrame(true);
        setIcon(HarvesterUI.ICONS.user_manage_icon());
        setLayout(new FitLayout());

        store = new ListStore<User>();
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);

        newUserDialog = new NewUserDialog(this);
        updateUserDialog = new UpdateUserDialog(this);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<User> sm = new CheckBoxSelectionModel<User>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        ColumnConfig column = new ColumnConfig("userName",HarvesterUI.CONSTANTS.username(),150);
        configs.add(column);

        column = new ColumnConfig("mail",HarvesterUI.CONSTANTS.email(),150);
        configs.add(column);

        final SimpleComboBox<String> roleCombo = new SimpleComboBox<String>();
        roleCombo.setForceSelection(true);
        roleCombo.setTriggerAction(TriggerAction.ALL);
        roleCombo.add("admin");
        roleCombo.add("normal");
        roleCombo.add("special");

        column = new ColumnConfig("role",HarvesterUI.CONSTANTS.role(),130);
        configs.add(column);

        loadUsers();

        ColumnModel cm = new ColumnModel(configs);

        final Grid<User> grid = new Grid<User>(store, cm);
        grid.getView().setForceFit(true);
        grid.setAutoExpandColumn("userName");
        grid.setBorders(true);
        grid.setSelectionModel(sm);
        grid.addPlugin(sm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noUsersAvailable());
        add(grid);

        Menu contextMenu = new Menu();

        MenuItem updateUser = new MenuItem();
        updateUser.setText(HarvesterUI.CONSTANTS.updateUser());
        updateUser.setIcon(HarvesterUI.ICONS.user_add());
        updateUser.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0) {
                    User selected = grid.getSelectionModel().getSelectedItems().get(0);
                    updateUserDialog.edit(selected);
                    updateUserDialog.show();
                    updateUserDialog.center();
                }
            }
        });
        contextMenu.add(updateUser);

        MenuItem remove = new MenuItem();
        remove.setText(HarvesterUI.CONSTANTS.removeUsers());
        remove.setIcon(HarvesterUI.ICONS.user_delete());
        remove.addSelectionListener(new SelectionListener<MenuEvent>() {
            final SelectionListener<ButtonEvent> userRemoveListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    List<User> selected = grid.getSelectionModel().getSelectedItems();

                    if(checkIfSelfSelected(selected))
                        HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.deleteUsers(),HarvesterUI.CONSTANTS.cannotRemoveSelf());
                    else
                        removeUsers(selected);
                }
            };

            @Override
            public void componentSelected(MenuEvent ce) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0) {
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteUserConfirmMessage(), userRemoveListener);
                }
            }

        });
        contextMenu.add(remove);

        grid.setContextMenu(contextMenu);

        ToolBar toolBar = new ToolBar();
        Button add = new Button(HarvesterUI.CONSTANTS.addUser());
        add.setIcon(HarvesterUI.ICONS.add16());
        add.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                newUserDialog.resetValues();
                newUserDialog.show();
                newUserDialog.center();
            }

        });
        toolBar.add(add);
        setTopComponent(toolBar);
    }

    private boolean checkIfSelfSelected(List<User> selectedUsers){
        for(User user : selectedUsers){
            if(user.getUserName().equals(HarvesterUI.UTIL_MANAGER.getLoggedUserName()))
                return true;
        }
        return false;
    }

    public void loadUsers(){
        AsyncCallback<List<User>> callback = new AsyncCallback<List<User>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<User> users) {
                store.removeAll();
                store.add(users);
            }
        };
        service.getUsers(callback);
    }

    private void removeUsers(List<User> users){
        for (User sel : users)
            store.remove(sel);

        AsyncCallback callback = new AsyncCallback() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Object result) {
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteUsers(), HarvesterUI.CONSTANTS.usersDeleted());
            }
        };
        service.removeUsers(users, callback);
    }

    public ListStore<User> getStore() {return store;}
}
