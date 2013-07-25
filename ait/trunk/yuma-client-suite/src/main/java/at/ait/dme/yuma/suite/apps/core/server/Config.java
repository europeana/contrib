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

package at.ait.dme.yuma.suite.apps.core.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

public class Config {
	private static Logger logger = Logger.getLogger(Config.class);

	private ServletConfig servletConfig = null;

	public Config(ServletConfig servletConfig) throws ServletException {
		this.servletConfig = servletConfig;
	}
	
	public Integer getIntegerProperty(String key) throws ServletException {
		Integer result = null;
		String value = null;
		
		try {
			if(servletConfig!=null) {
				ServletContext application = servletConfig.getServletContext();
				value = application.getInitParameter(key);
				if (value != null)
					result = Integer.parseInt(value);
			}
		} catch (NumberFormatException nfe) {
			logger.fatal("invalid value of " + key);
			throw new ServletException("invalid context parameter");
		}

		return result;
	}
	
	public Boolean getBooleanProperty(String key) throws ServletException {
		Boolean result = null;
		String value = null;
		
		if(servletConfig!=null) {
			ServletContext application = servletConfig.getServletContext();
			value = application.getInitParameter(key);
			if (value != null)
				result = Boolean.parseBoolean(value);
		}
	
		return result;
	}

	public String getStringProperty(String key) throws ServletException {
		String result = null;

		if(servletConfig!=null) {
			ServletContext application = servletConfig.getServletContext();
			result = application.getInitParameter(key);
		}
		
		if(result == null) {
			logger.fatal("property" + key + "could not be found");
			throw new ServletException("missing property");
		}

		return result;
	}
}
