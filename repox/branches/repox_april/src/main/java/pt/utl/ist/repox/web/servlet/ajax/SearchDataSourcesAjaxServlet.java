package pt.utl.ist.repox.web.servlet.ajax;

import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.util.RepoxContextUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


/* ------------------------------------------------------------ */
/** Dummy Servlet Request.
 * 
 */
public class SearchDataSourcesAjaxServlet extends HttpServlet {
	private static final String SEPARATOR = "|";
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger( SearchDataSourcesAjaxServlet.class);
    /* ------------------------------------------------------------ */
    public void init(ServletConfig config) throws ServletException
    {
    	super.init(config);
    }

    /* ------------------------------------------------------------ */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    /* ------------------------------------------------------------ */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String searchString = request.getParameter("q");
    	String limitString = request.getParameter("limit");
    	log.debug("searchString q: " + searchString);

    	response.setContentType("text/plain");
//    	response.setCharacterEncoding(System.getProperty("file.encoding"));
    	response.setCharacterEncoding("ISO-8859-1");
    	ServletOutputStream out = response.getOutputStream();
    	    	
    	try {
    		
			List<DataProvider> dataProviders = RepoxContextUtil.getRepoxManager().getDataProviderManager().loadDataProviders();
			
			for (DataProvider dataProvider : dataProviders) {
//				dp: "type"|"dp.id"|"dp.name"|"dp.descr"
//				ds: "type"|"dp.id"|"dp.name"|"ds.descr"|"ds.id"
				String dpSearchString = dataProvider.getId() + " " + dataProvider.getName() + " "
					+ (dataProvider.getDescription() != null ? dataProvider.getDescription() : "");
				
				if(isResultValid(searchString, dpSearchString)) {
					String row = getResultRow("dp", dataProvider.getId(), dataProvider.getName(), dataProvider.getDescription(), null);
					out.println(row);
				}
				
				if(dataProvider.getDataSources() != null) {
					for (DataSource dataSource : dataProvider.getDataSources()) {
						String dsSearchString = dataProvider.getId() + " " + dataProvider.getName() + " "
							+ (dataSource.getDescription() != null ? dataSource.getDescription() : "") + " " + dataSource.getId();
						
						if(isResultValid(searchString, dsSearchString)) {
							String row = getResultRow("ds", dataProvider.getId(), dataProvider.getName(), dataSource.getDescription(),
									dataSource.getId());
							out.println(row);
						}
					}
				}
			}
			
		}
		catch (Exception e) {
			log.error("Error loading Data Providers", e);
		}
        
        out.flush();
    }
    
    private String getResultRow(String type, String dataProviderId, String dataProviderName, String description, String dataSourceId)
    		throws UnsupportedEncodingException {
    	String resultRow = type + SEPARATOR;
    	resultRow += dataProviderId + SEPARATOR;
//		resultRow += new String(dataProviderName.getBytes(), ENCODING) + SEPARATOR;
		resultRow += dataProviderName + SEPARATOR;
//    	resultRow += (description == null ? "" : new String(description.getBytes(), ENCODING)) + SEPARATOR;
    	resultRow += (description == null ? "" : description) + SEPARATOR;
    	if(dataSourceId != null) {
    		resultRow += dataSourceId;
    	}
    	
    	return resultRow;
    }

	private boolean isResultValid(String searchString, String dataString) {
		boolean isValid = dataString.matches(".*(?i)" + searchString + ".*");		
		return isValid;
	}

}
