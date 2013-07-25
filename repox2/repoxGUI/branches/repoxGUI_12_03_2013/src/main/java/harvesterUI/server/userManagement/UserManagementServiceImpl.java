package harvesterUI.server.userManagement;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.userManagement.UserManagementService;
import harvesterUI.server.ProjectType;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.ldap.LDAPAuthenticator;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.servletResponseStates.RepoxServletResponseStates;
import harvesterUI.shared.users.User;
import harvesterUI.shared.servletResponseStates.ResponseState;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.mindrot.jbcrypt.BCrypt;
import pt.utl.ist.repox.util.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class UserManagementServiceImpl extends RemoteServiceServlet implements UserManagementService {

    public UserManagementServiceImpl() {}

    private HttpSession getSession() {
        return this.getThreadLocalRequest().getSession();
    }

    public String validateSessionId(String sessionId) throws ServerSideException {
        try{
            if(getSession().getAttribute("sid") != null){
                String sid = getSession().getAttribute("sid").toString();
                if(sid.equals(sessionId)){
                    return (String)getSession().getAttribute("userRole");
                }
            }
            return "error";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String getSessionID() throws ServerSideException {
        try{
            return getSession().getId();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public BaseModel confirmLogin(String user, String password) throws ServerSideException {
        BaseModel loginData = new BaseModel();
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            List list = document.selectNodes("//users/user");

            for(Object node: list) {
                Node n = (Node) node;
                String usr = n.valueOf( "userName" );
                String pss = n.valueOf( "pass" );
                String role = n.valueOf( "role" );
//                String language = n.valueOf( "language" );
                if(user.equals(usr) && BCrypt.checkpw(password, pss)) {
                    String sessionID = getSession().getId();
                    getSession().setAttribute("userName",usr);
                    getSession().setAttribute("sid",sessionID);
                    getSession().setAttribute("userRole",role);

                    loginData.set("sessionID",sessionID);
                    loginData.set("userName",usr);
                    loginData.set("role",role);
//                    loginData.set("language",language);
                    loginData.set("status","succeeded");
                    break;
                }
                else
                    loginData.set("status","failed");
            }
            return loginData;
        }catch (Exception e){
            loginData.set("status", "corrupt");
            return loginData;
        }
    }

    public List<User> getUsers() throws ServerSideException {
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            List<User> result = new ArrayList<User>();
            List list = document.selectNodes("//users/user");

            for(Object node: list){
                Node n = (Node) node;
                String usr = n.valueOf( "userName" );
                String role = n.valueOf( "role" );
                String pass = n.valueOf( "pass" );
                String mail = n.valueOf( "email" );
                String pageSize = n.valueOf( "pageSize" );
//                String language = n.valueOf( "language" );
                result.add(new User(usr,pass,role,mail,Integer.parseInt(pageSize)));
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public User getUser(String userName) throws ServerSideException {
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            List list = document.selectNodes("//users/user");

            for(Object node: list) {
                Node n = (Node) node;
                String usr = n.valueOf( "userName" );
                if(usr.equals(userName)) {
                    String role = n.valueOf( "role" );
                    String pass = n.valueOf( "pass" );
                    String mail = n.valueOf( "email" );
                    String pageSize = n.valueOf( "pageSize" );
//                    String language = n.valueOf( "language" );
                    return new User(usr,pass,role,mail,Integer.parseInt(pageSize));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return null;
    }

    private boolean userExists(String userName) throws ServerSideException {
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            List list = document.selectNodes("//users/user");

            for(Object node: list) {
                Node n = (Node) node;
                String usr = n.valueOf( "userName" );
                if(usr.toLowerCase().equals(userName.toLowerCase()))
                    return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return false;
    }

    public ResponseState saveNewUser(User user) throws ServerSideException {
        try {
            // Check if username already exists
            if(userExists(user.getUserName())){
                return ResponseState.USER_ALREADY_EXISTS;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            Element userEl = document.getRootElement().addElement( "user" );
            userEl.addElement( "userName" ).addText(user.getUserName());

            String password = generateRandomPassword();

            if(sendUserDataEmail(user.getUserName(),user.getMail(),password) == ResponseState.ERROR)
                return ResponseState.ERROR;

            userEl.addElement( "pass" ).addText(BCrypt.hashpw(password, BCrypt.gensalt()));
            userEl.addElement( "role" ).addText(user.getRole());
            userEl.addElement( "email" ).addText(user.getMail());
            userEl.addElement( "pageSize" ).addText(String.valueOf(user.getPageSize()));
//            userEl.addElement( "language" ).addText(user.getLanguage());

            File usersFile = new File(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            XmlUtil.writePrettyPrint(usersFile, document);
            return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState updateUser(User user, String oldUserName) throws ServerSideException {
        try {
            // Check if username already exists
            if(!user.getUserName().equals(oldUserName) && userExists(user.getUserName())){
                return ResponseState.USER_ALREADY_EXISTS;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));
            List list = document.selectNodes("//users/user");

            for(Object node: list) {
                Node n = (Node) node;
                String userName = n.valueOf("userName");
                if(userName.equals(oldUserName)) {
                    if(!user.getUserName().equals(n.valueOf("userName")))
                        n.selectSingleNode("userName").setText(user.getUserName());
                    if(!user.getRole().equals(n.valueOf("role")))
                        n.selectSingleNode("role").setText(user.getRole());
                    if(!user.getMail().equals(n.valueOf("email")))
                        n.selectSingleNode("email").setText(user.getMail());
                    if(!user.getPassword().equals(n.valueOf("pass")))
                        n.selectSingleNode("pass").setText(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                    if(sendUserDataEmail(user.getUserName(),user.getMail(),"the same") == ResponseState.ERROR)
                        return ResponseState.ERROR;
                    break;
                }
            }

            File usersFile = new File(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            XmlUtil.writePrettyPrint(usersFile, document);
            return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public void removeUsers(List<User> users) throws ServerSideException {
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));
            for(User user : users){
                List list = document.selectNodes("//users/user");

                for(Object node: list){
                    Node n = (Node) node;
                    String userName = n.valueOf("userName");
                    if(userName.equals(user.getUserName()))
                        n.detach();
                }
            }

            File usersFile = new File(getSession().getServletContext().getRealPath("/resources/data/users.xml"));
            XmlUtil.writePrettyPrint(usersFile, document);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public boolean resetUserPassword(String userName, String email, String projectType) throws ServerSideException {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(getSession().getServletContext().getRealPath("/resources/data/users.xml"));

            List list = document.selectNodes("//users/user");
            boolean foundMatch = false;

            for(Object node: list) {
                Node n = (Node) node;
                String usr = n.valueOf( "userName" );
                String mail = n.valueOf( "email" );
                if(usr.equals(userName) && mail.equals(email)) {
                    String resetPassword = generateRandomPassword();
                    n.selectSingleNode("pass").setText(BCrypt.hashpw(resetPassword, BCrypt.gensalt()));
                    foundMatch = true;

                    if(sendUserDataEmail(userName,email,resetPassword) == ResponseState.ERROR)
                        return false;
                }
            }

            if(foundMatch) {
                File usersFile = new File(getSession().getServletContext().getRealPath("/resources/data/users.xml"));
                XmlUtil.writePrettyPrint(usersFile, document);
            }
            return foundMatch;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

//    private String encryptPassword(String pwd) {
//        MessageDigest mdEnc = null; // Encryption algorithm
//        try {
//            mdEnc = MessageDigest.getInstance("MD5");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        mdEnc.reset();
//        mdEnc.update(pwd.getBytes(), 0, pwd.length());
//        return new BigInteger(1, mdEnc.digest()).toString(16); // Encrypted string
//    }

    private static String generateRandomPassword(){
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    public String sendFeedbackEmail(String userEmail, String title, String message, String messageType) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().sendFeedbackEmail(userEmail, title, message, messageType);
    }

    public String savePerPageData(String username, int dataPerPage) throws ServerSideException {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(this.getThreadLocalRequest().getSession().getServletContext().getRealPath("/resources/data/users.xml"));
            List list = document.selectNodes("//users/user");

            for(Object node: list) {
                Node n = (Node) node;
                String userName = n.valueOf("userName");
                if(userName.equals(username)) {
                    n.selectSingleNode("pageSize").setText(String.valueOf(dataPerPage));
                }
            }

            File usersFile = new File(this.getThreadLocalRequest().getSession().getServletContext().getRealPath("/resources/data/users.xml"));
            XmlUtil.writePrettyPrint(usersFile, document);
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

//    public String saveLanguageData(String username, String language) throws ServerSideException {
//        try {
//            SAXReader reader = new SAXReader();
//            Document document = reader.read(this.getThreadLocalRequest().getSession().getServletContext().getRealPath("/resources/data/users.xml"));
//            List list = document.selectNodes("//users/user");
//
//            for(Object node: list) {
//                Node n = (Node) node;
//                String userName = n.valueOf("userName");
//                if(userName.equals(username)) {
//                    n.selectSingleNode("language").setText(language);
//                }
//            }
//
//            File usersFile = new File(this.getThreadLocalRequest().getSession().getServletContext().getRealPath("/resources/data/users.xml"));
//            XmlUtil.writePrettyPrint(usersFile, document);
//            return "SUCCESS";
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }

    public RepoxServletResponseStates.GeneralStates registerNewEntity(String name, String mail, String institution, String skypeContact,String repoxUrl) throws ServerSideException {
        try {
            Properties properties = PropertyUtil.loadCorrectedConfiguration(RepoxContextUtilDefault.CONFIG_FILE);
            properties.setProperty("firstTimeUser","false");
            PropertyUtil.saveProperties(properties, RepoxContextUtilDefault.CONFIG_FILE);

            SAXReader reader = new SAXReader();
            String createRepoxUser = "http://repox.ist.utl.pt/repoxManagementServices/rest/createRegistration?name="+name+
                    "&email="+mail+"&institution="+institution+
                    "&skypeContact="+(skypeContact == null ? "" : skypeContact)+
                    "&repoxUrl="+(repoxUrl == null ? "" : repoxUrl);

            reader.read(new URL(createRepoxUser));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return RepoxServletResponseStates.GeneralStates.ERROR;
        } catch (DocumentException e) {
//            e.printStackTrace();
            // When no internet connection available
            saveEntityRegistration(name, mail, institution, skypeContact, repoxUrl);
            return RepoxServletResponseStates.GeneralStates.NO_INTERNET_CONNECTION;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }

        return RepoxServletResponseStates.GeneralStates.SUCCESS;
    }

    private void saveEntityRegistration(String name, String mail, String institution, String skypeContact,String repoxUrl) throws ServerSideException {
        try{
            File registrationFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                    getConfiguration().getXmlConfigPath() + File.separator + "registrationInfo.xml");

            if(!registrationFile.exists()){
                Document document = DocumentHelper.createDocument();

                Element registrationNode = document.addElement("registrationInfo");
                registrationNode.addAttribute("name",name);
                registrationNode.addAttribute("mail",mail);
                registrationNode.addAttribute("institution",institution);
                registrationNode.addAttribute("skypeContact",skypeContact);
                registrationNode.addAttribute("repoxUrl",repoxUrl);

                XmlUtil.writePrettyPrint(registrationFile, document);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public boolean isFirstTimeRepoxUsed() throws ServerSideException {
        try{
            Properties properties = PropertyUtil.loadCorrectedConfiguration(RepoxContextUtilDefault.CONFIG_FILE);
            boolean isFirstTime = Boolean.valueOf(properties.getProperty("firstTimeUser"));
            if(!isFirstTime){
                trySendRegistrationFromXML();
            }
            return isFirstTime;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private String trySendRegistrationFromXML() throws ServerSideException {
        try{
            File registrationFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                    getConfiguration().getXmlConfigPath() + File.separator + "registrationInfo.xml");

            if(registrationFile.exists()){
                SAXReader reader = new SAXReader();
                Document document = reader.read(registrationFile);

                String name = document.valueOf("//registrationInfo/@name");
                String mail = document.valueOf("//registrationInfo/@mail");
                String institution = document.valueOf("//registrationInfo/@institution");
                String skypeContact = document.valueOf("//registrationInfo/@skypeContact");
                String repoxUrl = document.valueOf("//registrationInfo/@repoxUrl");

                RepoxServletResponseStates.GeneralStates state =  registerNewEntity(name, mail, institution, skypeContact,repoxUrl);
                if(state == RepoxServletResponseStates.GeneralStates.SUCCESS)
                    registrationFile.delete();
            }
            return "OK";
        }catch (NullPointerException e){
//            e.printStackTrace();
            return "OK";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private ResponseState sendUserDataEmail(String username, String email, String password) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().sendUserDataEmail(username, email, password);
    }

    public boolean checkLDAPAuthentication(String username, String password) throws ServerSideException{
        String ldapHost = RepoxServiceImpl.getRepoxManager().getConfiguration().getLdapHost();
        String ldapUSerPrefix = RepoxServiceImpl.getRepoxManager().getConfiguration().getLdapUserPrefix();
        String ldapLoginDN = RepoxServiceImpl.getRepoxManager().getConfiguration().getLdapLoginDN();
        String loginDN = ldapUSerPrefix + username + ldapLoginDN;
        return LDAPAuthenticator.checkLDAPAuthentication(ldapHost,loginDN,password);
    }
}
