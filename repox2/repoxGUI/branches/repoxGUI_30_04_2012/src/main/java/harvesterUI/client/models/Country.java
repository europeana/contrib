package harvesterUI.client.models;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 21-02-2012
 * Time: 19:19
 */
public class Country extends BaseModel{

    public Country() {
    }

    public Country(String countryAbrev, String countryFlagAbrev, String countryName) {
        set("countryFlagAbrev",countryFlagAbrev);
        set("countryName",countryName);
        set("countryAbrev",countryAbrev);
    }

    public String getCountryAbrev(){
        return (String) get("countryAbrev");
    }

    public void changeLocale(){
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(String result) {
                System.out.println("New UI Language Set");
                Window.Location.assign(UtilManager.getServerUrl() + "?locale=" + getCountryAbrev());
            }
        };
        UserManagementServiceAsync userManagementService = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        userManagementService.saveLanguageData(HarvesterUI.UTIL_MANAGER.getLoggedUserName(), getCountryAbrev(), callback);
    }
}
