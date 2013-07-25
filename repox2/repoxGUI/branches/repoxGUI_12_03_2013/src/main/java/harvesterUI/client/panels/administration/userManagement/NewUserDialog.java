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
 * Time: 17:19
 */
public class NewUserDialog extends Dialog {

    private UserManagementServiceAsync service;
    private TextField<String> userNameField, emailField;
    private SimpleComboBox<String> roleCombo;
    private User newUser;
    private UserManagementGrid userManagementGrid;
    private Button saveButton;
    private DefaultFormPanel newUserFormPanel;

    public NewUserDialog(UserManagementGrid grid) {
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        userManagementGrid = grid;
        createNewUSerDialog();
    }

    private void createNewUSerDialog() {
        setButtons("");
        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.addUser());
        setIcon(HarvesterUI.ICONS.add16());
        setResizable(false);
        setModal(true);
        setSize(600,200);
        setBodyBorder(false);
        FormData formData = new FormData("95%");

        newUserFormPanel = new DefaultFormPanel();
        newUserFormPanel.setHeaderVisible(false);

        newUserFormPanel.setLayout(new EditableFormLayout(175));

        userNameField = new TextField<String>();
        userNameField.setFieldLabel(HarvesterUI.CONSTANTS.username() + HarvesterUI.REQUIRED_STR);
        userNameField.setId("userNameField");
        userNameField.setMinLength(4);
        userNameField.setAllowBlank(false);
        newUserFormPanel.add(userNameField, formData);

        Validator usernameValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.matches("^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$"))
                    return HarvesterUI.CONSTANTS.usernameValidateMessage();
                return null;
            }
        };
        userNameField.setValidator(usernameValidator);

        emailField = new TextField<String>();
        emailField.setFieldLabel(HarvesterUI.CONSTANTS.email() + HarvesterUI.REQUIRED_STR);
        emailField.setId("emailField");
        emailField.setAllowBlank(false);
        newUserFormPanel.add(emailField, formData);

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
        roleCombo.setFieldLabel(HarvesterUI.CONSTANTS.role() + HarvesterUI.REQUIRED_STR);
        roleCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        for(UserRole userRole : UserRole.values()){
            if(userRole != UserRole.ANONYMOUS)
                roleCombo.add(userRole.name());
        }
        roleCombo.setValue(roleCombo.getStore().getAt(0));
        newUserFormPanel.add(roleCombo, formData);

        saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                AsyncCallback<User> callback = new AsyncCallback<User>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(User user) {
                        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                            public void onFailure(Throwable caught) {
                                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                            }
                            public void onSuccess(ResponseState result) {
                                unmask();
                                if(result == ResponseState.USER_ALREADY_EXISTS){
                                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.newUser(),HarvesterUI.CONSTANTS.usernameAlreadyExists());
                                    return;
                                }
                                hide();
                                userManagementGrid.getStore().add(newUser);
                                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.newUser(),HarvesterUI.CONSTANTS.saveNewUserSuccess());
                                Dispatcher.get().dispatch(AppEvents.ViewUserManagementForm);
                            }
                        };
                        mask(HarvesterUI.CONSTANTS.saveUserMask());
                        newUserFormPanel.submit();
                        String role = roleCombo.getValue().getValue().trim();
                        String userName = userNameField.getValue().trim();
                        String email = emailField.getValue().trim();
                        String password = "teste";
                        newUser = new User(userName, password, role, email,15);
                        service.saveNewUser(newUser, callback);
                    }
                };
                String userName = userNameField.getValue();
                service.getUser(userName, callback);
            }
        });
        newUserFormPanel.addButton(saveButton);
        newUserFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
                Dispatcher.get().dispatch(AppEvents.ViewUserManagementForm);
            }
        }));

        newUserFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(newUserFormPanel);
        binding.addButton(saveButton);

        add(newUserFormPanel);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }

    public void resetValues() {
        userNameField.reset();
        emailField.reset();
        roleCombo.setValue(roleCombo.getStore().getAt(1));
    }
}
