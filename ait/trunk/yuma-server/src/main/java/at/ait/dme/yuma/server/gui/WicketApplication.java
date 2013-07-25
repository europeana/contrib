package at.ait.dme.yuma.server.gui;

import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;

import at.ait.dme.yuma.server.gui.admin.Dashboard;
import at.ait.dme.yuma.server.gui.admin.LoginPage;
import at.ait.dme.yuma.server.gui.admin.LogoutPage;
import at.ait.dme.yuma.server.gui.documentation.API;
import at.ait.dme.yuma.server.gui.documentation.Contact;
import at.ait.dme.yuma.server.gui.documentation.Developer;
import at.ait.dme.yuma.server.gui.documentation.Overview;
import at.ait.dme.yuma.server.gui.feeds.ObjectPage;
import at.ait.dme.yuma.server.gui.feeds.RepliesPage;
import at.ait.dme.yuma.server.gui.feeds.TimelinePage;
import at.ait.dme.yuma.server.gui.feeds.UserPage;
import at.ait.dme.yuma.server.gui.search.Search;

/**
 * Application object for your web application. If you want to run 
 * this application without deploying, run the Start class.
 * 
 * @see at.ait.dme.yuma.server.bootstrap.StartAnnotationServer#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication {    
    
	public WicketApplication() {
		this.mountBookmarkablePage("timeline", TimelinePage.class);
		
		// User feed pages
		this.mount(new MixedParamUrlCodingStrategy(
				"user", 
				UserPage.class,
				new String[]{UserPage.PARAM_USERNAME}
		));

		// Object feed pages
		this.mount(new MixedParamUrlCodingStrategy(
				"object", 
				ObjectPage.class,
				new String[]{ObjectPage.PARAM_OBJECT}
		));
		
		// Replies feed pages
		this.mount(new MixedParamUrlCodingStrategy(
				"replies", 
				RepliesPage.class,
				new String[]{RepliesPage.PARAM_PARENT_ID}
		));

		// Doc pages
		this.mountBookmarkablePage("doc", Overview.class);
		this.mountBookmarkablePage("doc/overview", Overview.class);
		this.mountBookmarkablePage("doc/api", API.class);
		this.mountBookmarkablePage("doc/developer", Developer.class);
		this.mountBookmarkablePage("doc/contact", Contact.class);

		// Admin area
		this.mountBookmarkablePage("admin", Dashboard.class);
		this.mountBookmarkablePage("admin/login", LoginPage.class);
		this.mountBookmarkablePage("admin/logout", LogoutPage.class);
	}
	
    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return YUMAWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }
	
	public Class<Search> getHomePage() {
		return Search.class;
	}

}
