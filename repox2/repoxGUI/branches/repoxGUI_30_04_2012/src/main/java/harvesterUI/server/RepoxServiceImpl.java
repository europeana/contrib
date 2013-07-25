package harvesterUI.server;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
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
import harvesterUI.shared.TransformationUI;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
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

    public PagingLoadResult<TransformationUI> getPagedTransformations(PagingLoadConfig config) throws ServerSideException{
        List<TransformationUI> transformationUIList = repoxDataExchangeManager.getFullTransformationsList();

        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
                Collections.sort(transformationUIList, config.getSortInfo().getSortDir().comparator(new Comparator<TransformationUI>() {
                    public int compare(TransformationUI p1, TransformationUI p2) {
                        if (sortField.equals("identifier")) {
                            return p1.getIdentifier().compareTo(p2.getIdentifier());
                        } else if (sortField.equals("srcFormat")) {
                            return p1.getSrcFormat().compareTo(p2.getSrcFormat());
                        } else if (sortField.equals("destFormat")) {
                            return p1.getDestFormat().compareTo(p2.getDestFormat());
                        }
                        return 0;
                    }
                }));
            }
        }
        ArrayList<TransformationUI> sublist = new ArrayList<TransformationUI>();
        int start = config.getOffset();
        int limit = transformationUIList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(transformationUIList.get(i));
        }
        return new BasePagingLoadResult<TransformationUI>(sublist, config.getOffset(), transformationUIList.size());
    }

    public List<TransformationUI> getFullTransformationsList() throws ServerSideException{
        try{
            return repoxDataExchangeManager.getFullTransformationsList();
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

    // Schema mapper
    public String saveTransformation(TransformationUI transformationUI,String oldTransId) throws ServerSideException{
        try {
            String xslFilePath = transformationUI.getXslFilePath().toLowerCase();

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(new MetadataTransformation(transformationUI.getIdentifier(),
                            transformationUI.getDescription(),transformationUI.getSrcFormat(),
                            transformationUI.getDestFormat(),xslFilePath,
                            false,transformationUI.getIsXslVersion2(),transformationUI.getSchema(),transformationUI.getMetadataNamespace()),oldTransId);
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteTransformation(List<String> transfomationIDs) throws ServerSideException{
        try {
            for(String transformationID : transfomationIDs){
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                        deleteMetadataTransformation(transformationID);
            }
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
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

    public String getRepoxProjectType() throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadCorrectedConfiguration("gui.properties");
            return properties.getProperty("project.type");
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
