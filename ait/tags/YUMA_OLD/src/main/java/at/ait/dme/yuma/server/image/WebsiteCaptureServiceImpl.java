/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.server.image;

import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import at.ait.dme.yuma.client.server.WebsiteCaptureService;
import at.ait.dme.yuma.client.server.exception.WebsiteCaptureException;
import at.ait.dme.yuma.server.util.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Prototypical implementation of a service that captures a web site and stores it as 
 * image using cutycapt (http://cutycapt.sourceforge.net/).
 * 
 * @author Christian Sadilek
 */
public class WebsiteCaptureServiceImpl extends RemoteServiceServlet implements
		WebsiteCaptureService {

	private static final long serialVersionUID = -3049854680823082130L;

	private static Logger logger = Logger.getLogger(WebsiteCaptureServiceImpl.class);
	private static final String MIN_WIDH_PROPERTY = "website.capture.min.width";
	private static final String TARGET_PATH_PROPERTY = "website.capture.target.path";
	private static final String RELATIVE_IMAGEURL_PATH = "captures";
	
	private static Integer minWidth = 0;
	private static String targetPath;

	/**
	 * reads the key store directory from the servlet context. in case it's not
	 * found there it tries to read it from the property file.
	 */
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		Config config = new Config(servletConfig, 
				getClass().getResourceAsStream("website-capture-service.properties"));
		minWidth=config.getIntegerProperty(MIN_WIDH_PROPERTY);
		targetPath=config.getStringProperty(TARGET_PATH_PROPERTY);
	}

	@Override
	public String captureSite(String url) throws WebsiteCaptureException {
		String imageName = "";

		try {						
			imageName = new Long(System.currentTimeMillis()).toString() + ".jpg";
			String filename = targetPath + "/" + imageName;
			Process p = Runtime.getRuntime().exec("cutycapt --min-width="+ minWidth.toString() +
					" --url="+url+" --out="+ filename);			
			
			if(p.waitFor()!=0) {
				String errorMessage = "";
				InputStream errorStream = p.getErrorStream();
				if(errorStream!=null) 
					errorMessage = IOUtils.toString(errorStream, "UTF-8");					
				
				throw new WebsiteCaptureException("failed to capture site: " + errorMessage);
			}
		} catch (Throwable t) {
			logger.fatal(t.getMessage(), t);
			throw new WebsiteCaptureException(t);
		}
		return RELATIVE_IMAGEURL_PATH + "/" + imageName;
	}
}
