package at.ait.dme.yuma.server.gui.feeds;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.gui.BaseAnnotationListPage;
import at.ait.dme.yuma.server.model.Annotation;

public class RepliesPage extends BaseAnnotationListPage {
	
	private Logger logger = Logger.getLogger(RepliesPage.class);
	
	public static final String PARAM_PARENT_ID = "parentId";
	
	private static final String TITLE = "YUMA Annotation Server - Replies to ";
	private static final String HEADLINE = "Replies to ";
	private static final String FEEDS = "feeds/replies/";
	
	public RepliesPage(final PageParameters parameters) {
		Annotation parent = 
			getParentAnnotation(parameters.getString(PARAM_PARENT_ID));		
		
		if (parent == null) 
			throw new AbortWithHttpStatusException(404, true);

		setTitle(TITLE + "'" + parent.getTitle() + "'");
		setHeadline(HEADLINE + "'" + parent.getTitle() + "'");		
		setAnnotations(getReplies(parent.getAnnotationID()));
		setFeedURL(Config.getInstance().getServerBaseUrl() + FEEDS + parent.getAnnotationID());
	}
	
	private Annotation getParentAnnotation(String id) {
		AbstractAnnotationDB db = null;
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect();
			return db.findAnnotationById(id);
		} catch (AnnotationDatabaseException e) {
			logger.fatal(e.getMessage());
		} catch (AnnotationNotFoundException e) {
			logger.warn(e.getMessage());
		} finally {
			if (db != null)
				db.disconnect();
		}
		return null;
	}
	
	private List<Annotation> getReplies(String id) {
		AbstractAnnotationDB db = null;
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect();
			return db.getReplies(id).asFlatList();
		} catch (AnnotationDatabaseException e) {
			logger.fatal(e.getMessage());
		} catch (AnnotationNotFoundException e) {
			// Should never happen
			logger.fatal(e.getMessage());
		} finally {
			if (db != null)
				db.disconnect();
		}
		return new ArrayList<Annotation>();
	}

}
