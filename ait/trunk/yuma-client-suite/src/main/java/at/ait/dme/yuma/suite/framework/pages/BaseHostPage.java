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

package at.ait.dme.yuma.suite.framework.pages;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import at.ait.dme.yuma.suite.apps.core.shared.model.User;
import at.ait.dme.yuma.suite.framework.LinkHeaderContributor;
import at.ait.dme.yuma.suite.framework.YUMASuite;
import at.ait.dme.yuma.suite.framework.YUMAWebSession;
import at.ait.dme.yuma.suite.framework.auth.MD5Util;

/**
 * Base class for all host pages. Host pages are (mostly) empty pages
 * that launch the GWT annotation tool app. There is a separate host 
 * page for each annotation tool; host pages are mapped to
 * yuma-suite/[mediatype].
 * 
 * @author Rainer Simon
 */
public abstract class BaseHostPage extends WebPage {
	
	public BaseHostPage(String title, String js, final PageParameters params) {
		try {
			add(LinkHeaderContributor.forRss(
					YUMASuite.getAnnotationServerBaseUrl() + "feeds/object/" + 
					URLEncoder.encode(params.getString("objectURI"), "UTF-8")
						.replace("%", "%25")));
			
			add(LinkHeaderContributor.forRDF(
					YUMASuite.getAnnotationServerBaseUrl() + "api/tree/" + 
					URLEncoder.encode(params.getString("objectURI"), "UTF-8")
						.replace("%", "%25")));
		} catch (UnsupportedEncodingException e) {
			// Should never ever happen
		}
		
		add(JavascriptPackageResource.getHeaderContribution(js));
		add(new Label("title", title));
		
		// Add the user to the session, if credentials are in the query string 
		User user = getUser(params);
		if (user != null)
			YUMAWebSession.get().setUser(getUser(params));	

		String baseUrl = YUMASuite.getBaseUrl(getWebRequestCycle().getWebRequest().getHttpServletRequest());
		
		// Add URL of the annotated object and the page base URL to the JS dictionary 
		String dictionary = "\nvar parameters = {\n" +
							"  objectURI:  \"" + params.getString("objectURI") + "\",\n" +
							"  baseURL:    \"" + baseUrl + "\", \n" +
							"  feedURL:    \"" + YUMASuite.getAnnotationServerBaseUrl() + "feeds/\", \n" +
							"  rdfBaseURL: \"" + YUMASuite.getAnnotationServerBaseUrl() + "api/annotation/\"\n" +
							"}\n";
		add(new Label("dictionary", dictionary).setEscapeModelStrings(false));
    }; 
    
    /**
     * Extracts user credentials from the query string and
     * creates a user object, if credentials are valid.
     * 
     * TODO modify this! needs to work with the encrypted/signed
     * auth token; plain username+email MUST NOT BE ALLOWED when
     * application is deployed in Deployment Mode.
     * 
     * @param params query string params
     * @return the User
     */
    private User getUser(PageParameters params) {
		String username = params.getString("username");
		if (username == null)
			return null;
		
		User user = new User(username);

		String email = params.getString("email");
		if (email != null)
			user.setGravatarHash(MD5Util.md5Hex(email));
		
    	return user;
    }
    
}
