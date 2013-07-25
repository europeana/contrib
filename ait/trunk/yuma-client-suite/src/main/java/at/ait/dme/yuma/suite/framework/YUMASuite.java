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

package at.ait.dme.yuma.suite.framework;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;

import at.ait.dme.yuma.suite.framework.pages.audio.AudioExamplePage;
import at.ait.dme.yuma.suite.framework.pages.audio.AudioHostPage;
import at.ait.dme.yuma.suite.framework.pages.image.ImageExamplePage;
import at.ait.dme.yuma.suite.framework.pages.image.ImageHostPage;
import at.ait.dme.yuma.suite.framework.pages.map.MapExamplePage;
import at.ait.dme.yuma.suite.framework.pages.map.MapHostPage;
import at.ait.dme.yuma.suite.framework.pages.video.VideoExamplePage;
import at.ait.dme.yuma.suite.framework.pages.video.VideoHostPage;

/**
 * The entry point for the YUMA Suite Wicket Frontend.
 * 
 * @author Rainer Simon
 */
public class YUMASuite extends WebApplication {    

	private static final String HTTP = "http://";
	
	public YUMASuite() {
		// Image tool URL mappings
		this.mountBookmarkablePage("image", ImageHostPage.class);
		this.mountBookmarkablePage("image/examples", ImageExamplePage.class);
		
		// Map tool URL mappings
		this.mountBookmarkablePage("map", MapHostPage.class);
		this.mountBookmarkablePage("map/examples", MapExamplePage.class);
		
		// Audio tool URL mappings
		this.mountBookmarkablePage("audio", AudioHostPage.class);
		this.mountBookmarkablePage("audio/examples", AudioExamplePage.class);
		
		// Video tool URL mappings
		this.mountBookmarkablePage("video", VideoHostPage.class);
		this.mountBookmarkablePage("video/examples", VideoExamplePage.class);
	}

	@Override
	public Class<? extends Page> getHomePage() {
		// TODO needs to point to the HomePage class (when done)
		return ImageExamplePage.class;
	}
	
	@Override
	public final Session newSession(Request request, Response response) {
		return new YUMAWebSession(request);
	}
	
	/**
	 * Utility method that returns true when the YUMA Suite is 
	 * deployed in Development Mode.
	 * @return true when the YUMA Suite is in dev mode
	 */
	public static boolean isDevMode() {
		return YUMASuite.get()
			.getConfigurationType().equals(WebApplication.DEVELOPMENT);
	}
	
	/**
	 * Utility method that constructs the base URL for a servlet request.
	 * @param request the request
	 * @return the base URL
	 */
    public static String getBaseUrl(HttpServletRequest request) {
		String baseURL = HTTP + request.getServerName();
		
		int serverPort = request.getServerPort();
		if (serverPort != 80)
			baseURL += ":" + serverPort;
		
		if (request.getContextPath() != null && request.getContextPath().length() > 0) {
			baseURL += request.getContextPath();
		} else if (request.getPathInfo() != null && request.getPathInfo().length()> 0) {
			int lastSlash = request.getPathInfo().lastIndexOf("/");
			if (lastSlash > 0)
				baseURL += request.getPathInfo().substring(0, lastSlash);
		}
		return baseURL + "/";
    }
    
    public static String getAnnotationServerBaseUrl() {
    	String baseUrl = (String) YUMASuite.get().getServletContext()
    		.getInitParameter("annotation.server.base.url");
    	
    	if (!baseUrl.endsWith("/"))
    		baseUrl += "/";
    	
    	return baseUrl; 
    }
    
    public static String getGoogleMapsAPIKey() {
    	return (String) YUMASuite.get().getServletContext().getInitParameter("gmaps.api.key"); 
    }

}
