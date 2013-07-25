package at.ait.dme.yuma.server.controller.rss;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.AbstractAnnotationController;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.model.Annotation;

@Path("/feeds")
public class RSSAnnotationController extends AbstractAnnotationController {
	
	private static final int FEED_LENGTH = 20;
	private static final String SERVER_BASEURL = Config.getInstance().getServerBaseUrl();
	
	private static final String TIMELINE_TITLE = "YUMA Public Timeline";
	private static final String TIMELINE_DESCRIPTION = "Most recent annotations on this YUMA annotation server.";
	private static final String TIMELINE_URL = SERVER_BASEURL + "timeline";
	
	private static final String OBJECT_FEED_TITLE = "Annotations for ";
	private static final String OBJECT_FEED_DESCRIPTION = "Annotation feed for object with ID ";
	private static final String OBJECT_FEED_URL = SERVER_BASEURL + "object/";
	
	private static final String USER_FEED_TITLE = "'s Annotations";
	private static final String USER_FEED_DESCRIPTION = "Annotations by ";
	private static final String USER_FEED_URL = SERVER_BASEURL + "user/";
	
	private static final String REPLY_FEED_TITLE = "Replies to ";
	private static final String REPLY_FEED_DESCRIPTION = "Replies to annotation ";	
	private static final String REPLY_FEED_URL = SERVER_BASEURL + "replies/";
	
	/**
	 * Returns a feed with the most recent public annotations in the system.
	 * @return the feed
	 * @throws AnnotationDatabaseException if anything goes wrong with the DB
	 * @throws UnsupportedEncodingException in case of encoding problem
	 */
	@GET
	@Produces("application/rss+xml")
	@Path("/timeline")
	public Response getTimeline()
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		return super.getMostRecent(FEED_LENGTH, new RSSFormatHandler(
				TIMELINE_TITLE,
				TIMELINE_DESCRIPTION,
				TIMELINE_URL));
	}
	
	/**
	 * Returns a feed with the public annotations by the specified user.
	 * @param name the user name
	 * @return the feed
	 * @throws AnnotationDatabaseException if anything goes wrong with the DB
	 * @throws UnsupportedEncodingException in case of encoding problem
	 */
	@GET
	@Produces("application/rss+xml")
	@Path("/user/{name}")	
	public Response getUserFeed(@PathParam("name") String name) 
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		String username = URLDecoder.decode(name, URL_ENCODING);
		
		return super.getAnnotationsForUser(name, new RSSFormatHandler(
				username + USER_FEED_TITLE,
				USER_FEED_DESCRIPTION + username,
				USER_FEED_URL + username));
	}
	
	/**
	 * Returns a feed with the public annotation to the specified object.
	 * @param objectId the object ID
	 * @return the feed
	 * @throws AnnotationDatabaseException if anything goes wrong with the DB
	 * @throws UnsupportedEncodingException in case of encoding problem
	 */
	@GET
	@Produces("application/rss+xml")
	@Path("/object/{objectId}")
	public Response getObjectFeed(@PathParam("objectId") String objectId) 
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		String screenName = toScreenName(URLDecoder.decode(objectId, "UTF-8"));

		return super.getAnnotationTree(objectId, new RSSFormatHandler(
				OBJECT_FEED_TITLE + "'" + screenName + "'",
				OBJECT_FEED_DESCRIPTION + "'" + screenName + "'",
				OBJECT_FEED_URL + objectId));
	}
	
	/**
	 * Returns a feed with the public replies to the specified annotation.
	 * @param id the annotation id
	 * @return the feed
	 * @throws AnnotationDatabaseException if anything goes wrong with the DB
	 * @throws AnnotationNotFoundException if the specified annotation does not exist
	 * @throws UnsupportedEncodingException in case of encoding problem
	 */
	@GET
	@Produces("application/rss+xml")
	@Path("/replies/{id}")
	public Response getAnnotationFeed(@PathParam("id") String id) 
		throws AnnotationDatabaseException, AnnotationNotFoundException, UnsupportedEncodingException {

		AbstractAnnotationDB db = Config.getInstance().getAnnotationDatabase();
		db.connect(request, response);
		Annotation parent = db.findAnnotationById(URLDecoder.decode(id, URL_ENCODING));

		return super.getReplies(id, new RSSFormatHandler(
				REPLY_FEED_TITLE + "'" + parent.getTitle() + "'",
				REPLY_FEED_DESCRIPTION + "'" + parent.getTitle() + "'",
				REPLY_FEED_URL + id));
	}
	
	/**
	 * Turns the URI of an object into a nicer 'screen name'. If the URI
	 * points to a tile set (or anything with an xml), the parent path element
	 * is used as screen name. In all other cases, the part after the last slash
	 * is used. 
	 * @param objectUri the URI
	 */
	public static String toScreenName(String objectUri) {
		String screenName = objectUri;
		
		if (screenName.endsWith(".xml")) {
			int lastSlash = screenName.lastIndexOf('/');
			screenName = screenName.substring(screenName.lastIndexOf('/', lastSlash - 1), lastSlash);
		}
		
		if (screenName.lastIndexOf('/') > -1)
			screenName = screenName.substring(screenName.lastIndexOf('/') + 1);	
		return screenName;
	}
	
}
