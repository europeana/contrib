package pt.utl.ist.repox.web.action.dataProvider;

import eu.europeana.repox2sip.Repox2SipException;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.LocalizableError;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.DataProvider;
import pt.utl.ist.repox.data.DataSource;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.task.DataSourceExportTask;
import pt.utl.ist.repox.task.DataSourceIngestTask;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.action.scheduler.SchedulingActionHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ViewDataProviderActionBean extends SchedulingActionHelper {
    private static final Logger log = Logger.getLogger(ViewDataProviderActionBean.class);

    private DataProvider dataProvider;
    private Map<String, File> dataSourceLastLogMap;

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public Map<String, File> getDataSourceLastLogMap() {
        return dataSourceLastLogMap;
    }

    public void setDataSourceLastLogMap(Map<String, File> dataSourceLastLogMap) {
        this.dataSourceLastLogMap = dataSourceLastLogMap;
    }

    public List<ScheduledTask> getScheduledTasks() {
        return getContext().getRepoxManager().getTaskManager().getScheduledTasks();
    }

    public ViewDataProviderActionBean() {
        super();
    }

    @DefaultHandler
    public Resolution view() throws DocumentException, IOException {
        String dataProviderId = context.getRequest().getParameter("dataProviderId");
        if(dataProviderId == null) {
            dataProviderId = (String) context.getRequest().getAttribute("dataProviderId");
        }

        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        if(dataSourceId == null) {
            dataSourceId = (String) context.getRequest().getAttribute("dataSourceId");
        }

        return view(dataProviderId, dataSourceId);
    }

    private Resolution view(String dataProviderId, String dataSourceId) throws DocumentException, IOException {
        dataProviderId = URLDecoder.decode(dataProviderId, "UTF-8");
        dataProvider = context.getRepoxManager().getDataManager().getDataProvider(dataProviderId);

        if(dataProvider == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        dataSourceLastLogMap = new TreeMap<String, File>();
        for (DataSource currentDataSource : dataProvider.getDataSources()) {
            File logDir = currentDataSource.getLogsDir();
            String[] logs = logDir.list();
            if(logs != null && logs.length > 0) {
                File lastLog = new File(logDir, logs[logs.length - 1]);
                dataSourceLastLogMap.put(currentDataSource.getId(), lastLog);
            }
        }

        String resolutionPage = "/jsp/dataProvider/view.jsp";
        /*
          if(dataSourceId != null) {
              resolutionPage += "?dataSourceId=" + dataSourceId;
          }
          */

        return new ForwardResolution(resolutionPage);
    }

    public Resolution countRecords() throws IOException, DocumentException, SQLException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        context.getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true);

        return view(dataSource.getDataProvider().getId(), dataSource.getId());
    }

    public Resolution deleteRecords() throws IOException, DocumentException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }
        try {
            dataSource.cleanUp();
        } catch(Exception e) {
            log.error("Unable to delete Data Source dir from Data Source with id " + dataSource.getId());
        }

        return view(dataSource.getDataProvider().getId(), dataSource.getId());
    }

    private DataSource getDataSourceFromParameter() throws DocumentException, IOException {
        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        if(dataSourceId == null) {
            dataSourceId = (String) context.getRequest().getAttribute("dataSourceId");
        }

        return context.getRepoxManager().getDataManager().getDataSource(dataSourceId);
    }

    public Resolution updateFile() throws IOException, DocumentException, NoSuchMethodException, ClassNotFoundException, ParseException {
        DataSourceDirectoryImporter dataSource = (DataSourceDirectoryImporter)getDataSourceFromParameter();
        dataSource.updateURLFile();

        //TODO: use a RedirectResolution after an intercepter or store the messages somehow so the feedback message isn't lost
//		return view(dataSource.getDataProvider().getId());
        return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + dataSource.getDataProvider().getId());
    }


    public Resolution harvest() throws IOException, DocumentException, NoSuchMethodException, ClassNotFoundException, ParseException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        Task harvestTask = new DataSourceIngestTask(String.valueOf(dataSource.getNewTaskId()), dataSource.getId(), "false");

        if(RepoxContextUtil.getRepoxManager().getTaskManager().isTaskExecuting(harvestTask)) {
            getContext().getValidationErrors().add("dataSource", new LocalizableError("error.dataSource.duplicatedHarvest"));
            return view(dataSource.getDataProvider().getId(), dataSource.getId());
        }
        RepoxContextUtil.getRepoxManager().getTaskManager().addOnetimeTask(harvestTask);

        context.getMessages().add(new LocalizableMessage("dataSource.harvestStart", dataSource.getId()));

        //TODO: use a RedirectResolution after an intercepter or store the messages somehow so the feedback message isn't lost
//		return view(dataSource.getDataProvider().getId());
        return new RedirectResolution("/dataProvider/ViewDataProvider.action?dataProviderId=" + dataSource.getDataProvider().getId());
    }


    public Resolution exportToFilesystem() throws IOException, DocumentException, NoSuchMethodException, ClassNotFoundException, ParseException, Repox2SipException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        // automatically fields the export directory
        //exportDirectory = dataSource.getExportDir().getAbsolutePath();

        File exportDir = new File(exportDirectory);
        if((exportDir.exists() && !exportDir.isDirectory())
                || (!exportDir.exists() && !exportDir.mkdir())) {
            context.getValidationErrors().addGlobalError(new LocalizableError("error.dataSource.invalidDirectory", exportDir.getAbsolutePath()));

            return view(dataSource.getDataProvider().getId(), dataSource.getId());
        }

        Task exportTask = new DataSourceExportTask(String.valueOf(dataSource.getNewTaskId()), dataSource.getId(), exportDirectory, String.valueOf(recordsPerFile));

        if(RepoxContextUtil.getRepoxManager().getTaskManager().isTaskExecuting(exportTask)) {
            getContext().getValidationErrors().add("dataSource", new LocalizableError("error.dataSource.duplicatedExport"));
            return view(dataSource.getDataProvider().getId(), dataSource.getId());
        }

        RepoxContextUtil.getRepoxManager().getTaskManager().addOnetimeTask(exportTask);

        context.getMessages().add(new LocalizableMessage("dataSource.exportToFilesystemStart", exportDir.getAbsolutePath()));

        return view(dataSource.getDataProvider().getId(), dataSource.getId());
    }

    public Resolution scheduleHarvest() throws DocumentException, IOException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        scheduleIngestMethod(context, dataSource, scheduledTask, editing, taskId, fullIngest);

        return view(dataSource.getDataProvider().getId(), dataSource.getId());
    }

    public Resolution scheduleExportFilesystem() throws DocumentException, IOException {
        DataSource dataSource = getDataSourceFromParameter();

        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        //exportDirectory = dataSource.getExportDir().getAbsolutePath();
        scheduleExportFilesystemMethod(context, dataSource, exportDirectory, scheduledTask, editing, taskId);

        return view(dataSource.getDataProvider().getId(), dataSource.getId());
    }

    public Resolution deleteScheduledTask() throws IOException, DocumentException {
        deleteScheduledTaskMethod(context, taskId);

        String dataProviderId = context.getRequest().getParameter("dataProviderId");
        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        return view(dataProviderId, dataSourceId);
    }


    public Resolution viewLogFile() throws DocumentException, IOException {
        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        String logFilename = context.getRequest().getParameter("logFilename");
        if(dataSourceId == null || logFilename == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        DataSource dataSource = context.getRepoxManager().getDataManager().getDataSource(dataSourceId);
        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        final File logFile = new File(dataSource.getLogsDir(), logFilename);
        if(!logFile.exists()) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        return new StreamingResolution("text/plain") {
            @Override
            public void stream(HttpServletResponse response) throws IOException {
                FileInputStream fileInputStream = new FileInputStream(logFile);
                OutputStream out = response.getOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = fileInputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
            }
        };
    }

    public Resolution viewExportFile() throws DocumentException, IOException {
        String dataSourceId = context.getRequest().getParameter("dataSourceId");
        String exportFileName = context.getRequest().getParameter("exportFileName");
        if(dataSourceId == null || exportFileName == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        DataSource dataSource = context.getRepoxManager().getDataManager().getDataSource(dataSourceId);
        if(dataSource == null) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        final File exportFile = new File(dataSource.getExportPath(), exportFileName);
        if(!exportFile.exists()) {
            return new ForwardResolution("/jsp/common/unknownResource.jsp");
        }

        return new StreamingResolution("application/zip") {
            @Override
            public void stream(HttpServletResponse response) throws IOException {
                FileInputStream fileInputStream = new FileInputStream(exportFile);
                OutputStream out = response.getOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = fileInputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
            }
        }.setFilename(exportFile.getName());
    }

    private Resolution returnNotFound() {
        return new StreamingResolution("text/html") {
            @Override
            public void stream(HttpServletResponse response) throws IOException {
                response.sendError(404);
            }
        };
    }

}