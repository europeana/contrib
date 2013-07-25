package europeana.geoparser.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import europeana.geoparser.Geoparser;
import europeana.geoparser.GeoparserFactory;
import europeana.geoparser.NamedEntityRecognitionException;
import europeana.geoparser.ResolutionException;
import europeana.geoparser.europeanaMetadata.InvalidMetadataRecordException;

/**
 * A servlet for the Geoparser REST interface
 * 
 * @author nfreire
 */
public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(ControllerServlet.class);

    GeoparsingControl geoparsingControl;
    
	/**
	 * Processes a request receive either by post or get
	 * 
	 * @param req the HTTP request
	 * @param resp the HTTP response
	 * @throws ServletException  
	 * @throws IOException  
	 */
	protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		if (req.getCharacterEncoding() == null) 
			req.setCharacterEncoding("UTF-8");
		try {
			if(req.getPathInfo()==null || req.getPathInfo().equals("/")) {
				getServletContext().getRequestDispatcher("/WEB-INF/views/form.jsp").forward(req,resp);
			} else if(req.getPathInfo().equals("/freeText")) {
				String freeText=req.getParameter("freeText");
				String language=req.getParameter("language");
				String stylesheet=req.getParameter("stylesheet");
				Document xml=geoparsingControl.geoparseFreeText(freeText, language, stylesheet);
				respondWithXml(xml, resp);
			} else if(req.getPathInfo().equals("/metadata")) {
				String metadata=req.getParameter("metadata");
				String stylesheet=req.getParameter("stylesheet");
				Document xml=geoparsingControl.geoparseMetadata(metadata, stylesheet);
				respondWithXml(xml, resp);
			}
		} catch (NamedEntityRecognitionException e) {
			respondWithError("ResolutionException", e, resp);
		} catch (ResolutionException e) {
			respondWithError("ResolutionException", e, resp);
		} catch (InvalidMetadataRecordException e) {
			respondWithError("InvalidMetadataRecordException", e, resp);
		} catch (DocumentException e) {
			respondWithError("InvalidInput", e, resp);
		} catch (IOException e) {
			respondWithError("InvalidInput", e, resp);
		}
	}
	
	
    /**
     * Send and HTTP response containing XML
     * @param xml the xml to send in the esponse
     * @param response the HTTP response
     */
    protected void respondWithXml(Document xml, HttpServletResponse response) {
        try {
        	response.setCharacterEncoding("UTF-8");
            response.setContentType("text/xml");
            PrintWriter writer = response.getWriter();
            writer.append(xml.asXML());
            writer.flush();
        } catch (IOException ex) {
            log.info(ex.getMessage(), ex);
        }
    }

    /**
     * send and HTTP response containing the error descritpion in XML 
     * @param errorType the type of error
     * @param exception the exception that caused the error
     * @param response the HTTP response
     */
    protected void respondWithError(String errorType, Exception exception, HttpServletResponse response) {
        log.info(exception.getMessage(), exception);
        try {
        	response.setCharacterEncoding("UTF-8");
            Document errorDoc = DocumentHelper.createDocument();
            Element rootEl = errorDoc.addElement("geoparsingResult");
            Element errorEl = rootEl.addElement("error");
            errorEl.addAttribute("type", errorType);
            errorEl.setText(exception.getMessage());
            response.setContentType("text/xml");
            PrintWriter writer = response.getWriter();
            writer.append(errorDoc.asXML());
            writer.flush();
        } catch (IOException ex) {
            log.info(ex.getMessage(), ex);
        }
    }

	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Geoparser geoparser=GeoparserFactory.newInstance(new File(getServletContext().getRealPath(""),"WEB-INF/tmp"));
			geoparsingControl=new GeoparsingControl(geoparser);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}
	
}
