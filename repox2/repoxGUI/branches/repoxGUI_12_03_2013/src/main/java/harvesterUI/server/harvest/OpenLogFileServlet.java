package harvesterUI.server.harvest;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.util.ConfigSingleton;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 14-03-2012
 * Time: 10:46
 */
public class OpenLogFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        String dataSetId = request.getParameter("dataSetId");
        String logName = request.getParameter("logName");

        DataSource dataSource = null;
        try {
            dataSource = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId).getDataSource();
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ServerSideException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        String logPath = "";
        if(logName == null){
            String delimFolder = "\\\\";
            assert dataSource != null;
            String[] tokensFolder = dataSource.getLogFilenames().get(0).split(delimFolder);
            String correctFilename;
            if(tokensFolder.length > 1)
                correctFilename = tokensFolder[0] + "/" + tokensFolder[1];
            else
                correctFilename = dataSource.getLogFilenames().get(0);
            logPath = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                    .getConfiguration().getRepositoryPath()+ "/"+dataSource.getId()+ "/logs/" + correctFilename;
        }
        else{
            assert dataSource != null;
            for(String currentLogName : dataSource.getLogFilenames()) {
                if(currentLogName.equals(logName)) {
                    logPath = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                            .getConfiguration().getRepositoryPath()+ "/"+dataSource.getId()+ "/logs/" + logName;
                    break;
                }
            }
        }

        // output an txt
        response.setContentType("text/plain");
        ServletOutputStream out = response.getOutputStream();

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(logPath));
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isr);
            String text = "";

            while ((text = reader.readLine()) != null) {
                out.print(text + "\n");
            }
            //            in = new BufferedInputStream
//                    (new FileInputStream(path) );
//            int ch;
//            while ((ch = in.read()) !=-1) {
//                out.print((char)ch);
//            }
        }
        finally {
            if (in != null) in.close();  // very important
        }
    }

}
