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
 * The public timeline.
 * 
 * @author Rainer Simon
 */
public class TimelinePage extends BaseAnnotationListPage {
	
	private Logger logger = Logger.getLogger(TimelinePage.class);
	
	private static final String TITLE = "YUMA Annotation Server - Public Timeline";
	private static final String HEADLINE = "public timeline";
	private static final String FEED_URL = "feeds/timeline";
	
	public TimelinePage(final PageParameters parameters) {
		setTitle(TITLE);
		setHeadline(HEADLINE);
		setAnnotations(getMostRecent(20));
		setFeedURL(Config.getInstance().getServerBaseUrl() + FEED_URL);
	}
    
	private List<Annotation> getMostRecent(int n) {
		AbstractAnnotationDB db = null;
		List<Annotation> mostRecent = new ArrayList<Annotation>();
		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect();
			mostRecent = db.getMostRecent(n, true);
		} catch (AnnotationDatabaseException e) {
			logger.fatal(e.getMessage());
		} finally {
			if(db != null) db.disconnect();
		}
		
		return mostRecent;
	}

}
