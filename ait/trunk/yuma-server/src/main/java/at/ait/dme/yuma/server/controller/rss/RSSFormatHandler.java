package at.ait.dme.yuma.server.controller.rss;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import at.ait.dme.yuma.server.controller.FormatHandler;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.AnnotationTree;

/**
 * Format handler for RSS (serialization only)
 * 
 * @author Rainer Simon
 */
public class RSSFormatHandler implements FormatHandler {

	private static final String RSS2_0 = "rss_2.0";
	private static final String UTF8 = "UTF-8";
	private static final String MIME_HTML = "text/html";
	
	private String feedTitle;
	private String feedDescription;
	private String feedLink;
	
	private Logger logger = Logger.getLogger(RSSFormatHandler.class);

	public RSSFormatHandler(String feedTitle, String feedDescription, String feedLink) {
		this.feedTitle = feedTitle;
		this.feedDescription = feedDescription;
		this.feedLink = feedLink;
	}
	
	@Override
	public Annotation parse(String serialized) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String serialize(Annotation annotation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String serialize(AnnotationTree tree) {
		return serialize(tree.asFlatList());
	}

	@Override
	public String serialize(List<Annotation> annotations) {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(RSS2_0);
		feed.setTitle(feedTitle);
		feed.setDescription(feedDescription);
		feed.setLink(feedLink);
		feed.setEncoding(UTF8);
		
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		for (Annotation a : annotations) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(a.getTitle());
			entry.setAuthor(a.getCreatedBy().getUsername());
			entry.setPublishedDate(a.getCreated());
			entry.setUpdatedDate(a.getLastModified());
			
			SyndContent body = new SyndContentImpl();
			body.setType(MIME_HTML);
			body.setValue(a.getText());
			entry.setDescription(body);
			
			entries.add(entry);
		}
		feed.setEntries(entries);
		
		try {
			SyndFeedOutput output = new SyndFeedOutput();
			StringWriter sw = new StringWriter();
			output.output(feed, sw);
			return sw.toString();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (FeedException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

}
