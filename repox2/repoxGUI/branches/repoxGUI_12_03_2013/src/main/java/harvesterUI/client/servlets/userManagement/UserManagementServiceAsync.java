/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.userManagement;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.server.ProjectType;
import harvesterUI.shared.servletResponseStates.RepoxServletResponseStates;
import harvesterUI.shared.users.User;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface UserManagementServiceAsync {

    public void savePerPageData(String username,int dataPerPage,AsyncCallback<String> callback);
//    public void saveLanguageData(String username, String language,AsyncCallback<String> callback);
    public void isFirstTimeRepoxUsed(AsyncCallback<Boolean> callback);
    public void registerNewEntity(String name, String mail,String institution, String skypeContact, String repoxUrl, AsyncCallback<RepoxServletResponseStates.GeneralStates> callback);

    public void validateSessionId(String sessionId,AsyncCallback<String> callback);
    public void confirmLogin(String user, String password, AsyncCallback<BaseModel> callback);
    public void getUsers(AsyncCallback<List<User>> callback);
    public void getUser(String userName, AsyncCallback<User> callback);
    public void saveNewUser(User user,AsyncCallback<ResponseState> callback);
    public void updateUser(User user,String oldUserName,AsyncCallback<ResponseState> callback);
    public void resetUserPassword(String userName, String email, String projectType,AsyncCallback<Boolean> callback);
    public void removeUsers(List<User> users,AsyncCallback callback);
    public void getSessionID(AsyncCallback<String> callback);

    public void sendFeedbackEmail(String userEmail, String title, String message, String messageType, AsyncCallback<String> callback);

    public void checkLDAPAuthentication(String loginDN, String password, AsyncCallback<Boolean> callback);
}
