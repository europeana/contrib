package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.administration.AdminForm;
import harvesterUI.client.panels.administration.userManagement.UserManagementGrid;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:50
 */
public class AdministrationView extends View {

    private UserManagementGrid userManagementGrid;
    private AdminForm adminForm;

    public AdministrationView(Controller controller) {
        super(controller);
        adminForm = new AdminForm();
        Registry.register("adminConfig", adminForm);
    }

    @Override
    protected void handleEvent(AppEvent event){
        if (event.getType() == AppEvents.ViewUserManagementForm) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            userManagementGrid.loadUsers();
            centerPanel.add(userManagementGrid);
            centerPanel.layout();
        } else if (event.getType() == AppEvents.ViewAdminForm) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            adminForm.editAdminForm();
            centerPanel.add(adminForm);
            centerPanel.layout();
        } else if (event.getType() == AppEvents.LoadMainConfigs) {
            adminForm.editAdminForm();
        }else if (event.getType() == AppEvents.ReloadUsers) {
            if(userManagementGrid.isVisible())
                userManagementGrid.loadUsers();
        }
    }

    @Override
    protected void initialize(){
        userManagementGrid = new UserManagementGrid();
    }
}
