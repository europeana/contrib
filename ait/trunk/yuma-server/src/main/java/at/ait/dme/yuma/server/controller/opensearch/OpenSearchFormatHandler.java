package at.ait.dme.yuma.server.controller.opensearch;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.module.opensearch.entity.OSQuery;
import com.sun.syndication.feed.module.opensearch.impl.OpenSearchModuleImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import at.ait.dme.yuma.server.URIBuilder;
import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.FormatHandler;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.AnnotationTree;

/**
 * Format handler for OpenSearch (serialization only)
 * 
 * @author Rainer Simon
 */
public class OpenSearchFormatHandler implements FormatHandler {
	
	private static final String RSS2_0 = "rss_2.0";
	private static final String UTF8 = "UTF-8";
	
	private static final String TITLE = "YUMA Server Search: ";
	private static final String DESCRIPTION = "Search results for ";
	private static final String LINK = Config.getInstance().getServerBaseUrl() + "api/opensearch/";
	private static final String ELLIPSIS = "...";
	
	private static final String ROLE_REQUEST = "request";
		
	private Logger logger = Logger.getLogger(OpenSearchFormatHandler.class);
	
	private String query;
	private int totalResults;
	private int startIdx;
	private int itemsPerPage;
	
	public OpenSearchFormatHandler(String query, int totalResults, int startIdx, int itemsPerPage) {
		this.query = query;
		this.totalResults = totalResults;
		this.startIdx = startIdx;
		this.itemsPerPage = itemsPerPage;
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
		feed.setTitle(TITLE + query);
		feed.setDescription(DESCRIPTION + query);
		feed.setLink(LINK);
		feed.setEncoding(UTF8);
		
		@SuppressWarnings("unchecked")
		List<OpenSearchModule> modules = feed.getModules();
		OpenSearchModule osm = new OpenSearchModuleImpl();
		osm.setTotalResults(totalResults);
		osm.setStartIndex(startIdx);
		osm.setItemsPerPage(itemsPerPage);
		
		OSQuery osq = new OSQuery();
		osq.setRole(ROLE_REQUEST);
		osq.setSearchTerms(query);
		
		osm.addQuery(osq);
		modules.add(osm);
		feed.setModules(modules);
		
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		for (Annotation a : annotations) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(a.getTitle());
			
			String text = a.getText();
			if (text.length() > 100)
				text = text.substring(0, 100).trim() + ELLIPSIS;
			
			SyndContent body = new SyndContentImpl();
			body.setValue(text);
			entry.setDescription(body);
			
			entry.setLink(URIBuilder.toURI(a.getAnnotationID()).toString());
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
