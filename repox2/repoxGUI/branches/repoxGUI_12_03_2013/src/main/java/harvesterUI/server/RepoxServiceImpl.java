package harvesterUI.server;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.RepoxService;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
//import harvesterUI.server.projects.EuDML.EuDMLManager;
import harvesterUI.server.projects.europeana.EuropeanaManager;
import harvesterUI.server.projects.Light.LightManager;
import harvesterUI.server.projects.ProjectManager;
import harvesterUI.server.util.Util;
import harvesterUI.shared.RepoxStatisticsUI;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.util.*;

import java.net.URL;
import java.util.*;

public class RepoxServiceImpl extends RemoteServiceServlet implements RepoxService {

    private RepoxDataExchangeManager repoxDataExchangeManager = null;

    private static ProjectManager projectManager;

    public RepoxServiceImpl() {
        repoxDataExchangeManager = new RepoxDataExchangeManager();

        Properties properties = PropertyUtil.loadCorrectedConfiguration("gui.properties");
        ProjectType projectType = ProjectType.valueOf(properties.getProperty("project.type"));

        switch (projectType){
            case LIGHT:
                projectManager = new LightManager();
                break;
//            case EUDML:
//                projectManager = new EuDMLManager();
//                break;
            case EUROPEANA:
                projectManager = new EuropeanaManager();
                break;
        }
    }

    public static ProjectManager getProjectManager(){
        return projectManager;
    }

    public static RepoxManager getRepoxManager() throws ServerSideException{
        try{
            return ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    /**
     *
     * Data Lists
     *
     */

    public Map<String,String> getFullCountryList() throws ServerSideException{
        try{
            return getProjectManager().getFullCountryList();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<String> getFullCharacterEncodingList() throws ServerSideException{
        try{
            return repoxDataExchangeManager.getFullCharacterEncodingList();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    /*
    * Administrator settings
    */
    public BaseModel loadAdminFormInfo() throws ServerSideException{
        return getProjectManager().loadAdminFormInfo();
    }

    public void saveAdminFormInfo(BaseModel results) throws ServerSideException{
        getProjectManager().saveAdminFormInfo(results);
    }

    // Statistics Parser
    public RepoxStatisticsUI getStatisticsInfo() throws ServerSideException{
        return getProjectManager().getStatisticsInfo();
    }

    public String getRepoxVersion() throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadCorrectedConfiguration("gui.properties");
            return properties.getProperty("repox.version");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public BaseModel getInitialConfigData() throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadCorrectedConfiguration("gui.properties");
            BaseModel initialData = new BaseModel();

            initialData.set("PROJECT_TYPE",properties.getProperty("project.type"));

            initialData.set("MDR_URL",RepoxServiceImpl.getRepoxManager().getConfiguration().getMdrUrl());

            return initialData;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ExternalServiceResultUI getValidationState(String dataSetID) throws ServerSideException {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(dataSetID));

            String reportFile = document.valueOf("//response/reportFile");

            if(reportFile == null || reportFile.equals("NOT_FOUND"))
                return null;

            String state = document.valueOf("//report/status");

            return new ExternalServiceResultUI(state);
        }catch (DocumentException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }
}
