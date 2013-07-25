package at.ait.dme.yuma.server.gui.feeds;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.gui.BaseAnnotationListPage;
import at.ait.dme.yuma.server.model.Annotation;

/**
 * A user's public feed page.
 * 
 * TODO the feed page is actually NOT public right now - also 
 * includes the private annotations!
 * 
 * @author Rainer Simon
 */
public class UserPage extends BaseAnnotationListPage {

	private Logger logger = Logger.getLogger(UserPage.class);
	
	public static final String PARAM_USERNAME = "username";
	
	private static final String TITLE = "YUMA Annotation Server - User ";
	private static final String HEADLINE = "'s public feed";
	private static final String FEEDS = "feeds/user/";
	
	public UserPage(final PageParameters parameters) {
		String username = parameters.getString(PARAM_USERNAME);		
		
		setTitle(TITLE + username);
		setHeadline(username + HEADLINE);		
		setAnnotations(getAnnotationsByUser(username));
		setFeedURL(Config.getInstance().getServerBaseUrl() + FEEDS + username);
	}
	
	private List<Annotation> getAnnotationsByUser(String username) {
		AbstractAnnotationDB db = null;
		List<Annotation> annotations = new ArrayList<Annotation>();
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect();
			annotations = db.findAnnotationsForUser(username);
		} catch (AnnotationDatabaseException e) {
			logger.fatal(e.getMessage());
		} finally {
			if(db != null) db.disconnect();
		}
		
		return annotations;
	}

}
