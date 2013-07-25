package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.users.User;
import harvesterUI.shared.users.UserRole;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 18:29
 */
public class UpdateUserDialog extends Dialog{

    private UserManagementServiceAsync service;
    private SimpleComboBox<String> roleCombo;
    private TextField<String> emailField, username;
    private User updateUser;
    private UserManagementGrid userManagementGrid;

    public UpdateUserDialog(UserManagementGrid grid) {
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        userManagementGrid = grid;
        createUpdateUserDialog();
    }

    private void createUpdateUserDialog() {
        setButtons("");
        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.updateUser());
        setIcon(HarvesterUI.ICONS.user_add());
        setResizable(false);
        setModal(true);
        setSize(600,200);

        DefaultFormPanel updateUserFormPanel = new DefaultFormPanel();
        updateUserFormPanel.setHeaderVisible(false);

        updateUserFormPanel.setLayout(new EditableFormLayout(175));

        FormData formData = new FormData("95%");

        username = new TextField<String>();
        username.setFieldLabel(HarvesterUI.CONSTANTS.username() + HarvesterUI.REQUIRED_STR);
        username.setId("username");
        username.setAllowBlank(false);
        updateUserFormPanel.add(username,formData);

        Validator usernameValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.matches("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$"))
                    return HarvesterUI.CONSTANTS.usernameValidateMessage();
                return null;
            }
        };
        username.setValidator(usernameValidator);

        emailField = new TextField<String>();
        emailField.setFieldLabel(HarvesterUI.CONSTANTS.email() + HarvesterUI.REQUIRED_STR);
        emailField.setId("emailField");
        emailField.setAllowBlank(false);
        updateUserFormPanel.add(emailField,formData);

        Validator emailValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
                    return HarvesterUI.CONSTANTS.emailValidateMessage();
                return null;
            }
        };
        emailField.setValidator(emailValidator);

        roleCombo = new SimpleComboBox<String>();
        roleCombo.setEditable(false);
        roleCombo.setFieldLabel(HarvesterUI.CONSTANTS.role()+ HarvesterUI.REQUIRED_STR);
        roleCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        for(UserRole userRole : UserRole.values()){
            if(userRole != UserRole.ANONYMOUS)
                roleCombo.add(userRole.name());
        }
        roleCombo.setValue(roleCombo.getStore().getAt(0));
        updateUserFormPanel.add(roleCombo,formData);

        Button b = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                final String oldUserName = updateUser.getUserName();
                AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        unmask();
                    }
                    public void onSuccess(ResponseState result) {
                        if(result == ResponseState.USER_ALREADY_EXISTS){
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.updateUser(),HarvesterUI.CONSTANTS.usernameAlreadyExists());
                            updateUser.setUserName(oldUserName);
                            return;
                        }
                        unmask();
                        hide();
                        userManagementGrid.getStore().update(updateUser);
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.updateUser(),HarvesterUI.CONSTANTS.updateUserSuccess());
                        Dispatcher.get().dispatch(AppEvents.ViewUserManagementForm);
                        // change logged user username if changed
                        if(oldUserName.equals(HarvesterUI.UTIL_MANAGER.getLoggedUserName())){
                            Registry.register("LOGGED_USER_NAME",updateUser.getUserName());
                            AppView appView = (AppView) Registry.get("appView");
                            appView.refreshUserName();
                        }
                    }
                };
                mask(HarvesterUI.CONSTANTS.updateUserMask());
                String role = roleCombo.getValue().getValue();
                String email = emailField.getValue();
                updateUser.setRole(role.trim());
                updateUser.setMail(email.trim());
                updateUser.setUserName(username.getValue().trim());
                service.updateUser(updateUser,oldUserName, callback);
            }
        });
        updateUserFormPanel.addButton(b);
        updateUserFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
                Dispatcher.get().dispatch(AppEvents.ViewUserManagementForm);
            }
        }));

        updateUserFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(updateUserFormPanel);
        binding.addButton(b);

        add(updateUserFormPanel);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }

    public void edit(User user) {
        updateUser = user;
        emailField.setValue(user.getMail());
        username.setValue(user.getUserName());

        roleCombo.setSimpleValue(user.getRole());
    }
}
