/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.userManagement;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.server.ProjectType;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.servletResponseStates.RepoxServletResponseStates;
import harvesterUI.shared.users.User;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

@RemoteServiceRelativePath("userManagementService")
public interface UserManagementService extends RemoteService {

    public String savePerPageData(String username,int dataPerPage) throws ServerSideException;
    public String saveLanguageData(String username, String language) throws ServerSideException;
    public boolean isFirstTimeRepoxUsed() throws ServerSideException;
    public RepoxServletResponseStates.GeneralStates registerNewEntity(String name, String mail, String institution, String skypeContact,String repoxUrl) throws ServerSideException;

    public String validateSessionId(String sessionId) throws ServerSideException;
    public BaseModel confirmLogin(String user, String password) throws ServerSideException;
    public List<User> getUsers() throws ServerSideException;
    public User getUser(String userName) throws ServerSideException;
    public ResponseState saveNewUser(User user) throws ServerSideException;
    public ResponseState updateUser(User user,String oldUserName) throws ServerSideException;
    public boolean resetUserPassword(String userName, String email, String projectType) throws ServerSideException;
    public void removeUsers(List<User> users) throws ServerSideException;
    public String getSessionID() throws ServerSideException;

    public String sendFeedbackEmail(String userEmail, String title, String message, String messageType) throws ServerSideException;

}
