package at.ait.dme.yuma.server.gui.feeds;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.rss.RSSAnnotationController;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.gui.BaseAnnotationListPage;
import at.ait.dme.yuma.server.model.Annotation;

public class ObjectPage extends BaseAnnotationListPage {
	
	private Logger logger = Logger.getLogger(ObjectPage.class);
	
	private static final String UTF8 = "UTF-8";
	
	public static final String PARAM_OBJECT = "object";
	
	private static final String TITLE = "YUMA Annotation Server - ";
	private static final String HEADLINE = "Annotations for ";
	private static final String FEEDS = "feeds/object/";
	
	public ObjectPage(final PageParameters parameters) {
		try {
			String objectId = URLDecoder.decode(parameters.getString(PARAM_OBJECT), UTF8);		
			String screenName = RSSAnnotationController.toScreenName(objectId);

			setTitle(TITLE + screenName);
			setHeadline(HEADLINE + "'" + screenName + "'" );		
			setAnnotations(getAnnotationsForObject(objectId));

			setFeedURL(Config.getInstance().getServerBaseUrl() + FEEDS 
					+ URLEncoder.encode(objectId, UTF8).replace("%", "%25"));
		} catch (UnsupportedEncodingException e) {
			// Should never ever happen
			logger.fatal(e.getMessage());
		}
	}
	
	private List<Annotation> getAnnotationsForObject(String objectId) {
		AbstractAnnotationDB db = null;		
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect();
			return db.findAnnotationsForObject(objectId).asFlatList();
		} catch (AnnotationDatabaseException e) {
			logger.fatal(e.getMessage());
		} finally {
			if(db != null) db.disconnect();
		}
		return new ArrayList<Annotation>();
	}

}
