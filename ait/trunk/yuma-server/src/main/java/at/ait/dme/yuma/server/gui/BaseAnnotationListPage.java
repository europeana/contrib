package at.ait.dme.yuma.server.gui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import at.ait.dme.yuma.server.URIBuilder;
import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.gizmos.LanguageGuesser;
import at.ait.dme.yuma.server.gui.feeds.UserPage;
import at.ait.dme.yuma.server.gui.search.Search;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.tag.SemanticTag;

/**
 * Base class for all pages that lists of annotations (i.e. feed and
 * search result pages).
 * 
 * TODO Memo to myself: currently throws a RuntimeException if not all
 * template components are set (title, headline, annotations, feed URL).
 * Probably better to fill member fields in the setters and then override
 * the onRender method, replacing nulls where necessary.
 * 
 * @author Rainer Simon
 */
public abstract class BaseAnnotationListPage extends WebPage {

	private static final String SPAN = "<span class=\"grad\"></span>";
	private static final String ELLIPSIS = "...";
	private static final String SERVER_BASE_URL = Config.getInstance().getServerBaseUrl();
	private static final String SUITE_BASE_URL = Config.getInstance().getSuiteBaseUrl();
	
	public BaseAnnotationListPage() {
		add(new BookmarkablePageLink<String>("home", Search.class));
	}
	
	public void setTitle(String title) {
		add(new Label("title", title));				
	}
		
	public void setHeadline(String headline) {
		add(new Label("heading", SPAN + headline).setEscapeModelStrings(false));		
	}
	
	public void setAnnotations(List<Annotation> annotations) {
		add(new AnnotationListView("annotations", annotations));				
	}
	
	public void setFeedURL(String feedUrl) {
		if (feedUrl == null) {
			add(new ExternalLink("list-feed-url", "#").add(new SimpleAttributeModifier("style", "visibility:hidden")));
		} else {
			add(LinkHeaderContributor.forRss(feedUrl));
			add(new ExternalLink("list-feed-url", feedUrl));
		}
	}
	
	private class AnnotationListView extends ListView<Annotation> {

		private static final long serialVersionUID = 6677934776500475422L;
		
		public AnnotationListView(String id, List<Annotation> list) {
			super(id, list);
		}

		@Override
		protected void populateItem(ListItem<Annotation> item) {
			Annotation a = (Annotation) item.getModelObject();
			item.add(new ExternalLink("reply-feed-url", 
					SERVER_BASE_URL + "feeds/replies/" + a.getAnnotationID()));
			item.add(new Label("title", a.getTitle()));
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(UserPage.PARAM_USERNAME, a.getCreatedBy().getUsername());
			item.add(
				new BookmarkablePageLink<String>("author-href", UserPage.class, new PageParameters(params))
					.add(new Label("author-label", a.getCreatedBy().getUsername())));
			
			String feedPageUri;
			try {
				feedPageUri = SERVER_BASE_URL + "object/" + URLEncoder
					.encode(a.getObjectUri(), "UTF-8")
					.replace("%", "%25");
			} catch (UnsupportedEncodingException e) {
				// Should never happen
				e.printStackTrace();
				feedPageUri = "#";
			}
			String screenUri = a.getObjectUri();
			if (screenUri.length() > 55)
				screenUri = screenUri.substring(0, 55) + ELLIPSIS;
			item.add(new ExternalLink("objectPage", feedPageUri, screenUri));
			item.add(new ExternalLink("objectUri", a.getObjectUri())
			.add(new SimpleAttributeModifier("title", "Open Original File in New Window")));

			item.add(new Label("created", a.getCreated().toString()));
			item.add(new Label("lastModified", a.getLastModified().toString()));
			item.add(new Label("text", a.getText()));
			item.add(new Label("scope", a.getScope().name()));
			item.add(new TagListView("tags", a.getTags()));
			
			String uri = URIBuilder.toURI(a.getAnnotationID()).toString();
			item.add(new ExternalLink("uri", uri, uri));
			
			item.add(new Label("lang", LanguageGuesser
						.getInstance().guess(a.getText())));
	
			item.add(new ExternalLink("dl-json", uri + ".json", "JSON"));
			item.add(new ExternalLink("dl-rdf-xml", uri + ".rdf", "RDF/XML"));
			item.add(new ExternalLink("dl-rdf-n3", uri + ".n3", "N3"));
			item.add(new ExternalLink("dl-rdf-turtle", uri + ".turtle", "Turtle"));
			item.add(new ExternalLink("dl-rdf-xml-oac", uri + ".oac", "OAC (Beta)"));
			
			item.add(new ExternalLink("open-in-client", 
								SUITE_BASE_URL + a.getType().name().toLowerCase() + "?objectURI=" + a.getObjectUri(),
								"Open in " + a.getType().getScreenName() + " Tool")
							.add(new SimpleAttributeModifier("title", "Open in " + a.getType().getScreenName() + " Annotation Tool"))
			);
		}
		
	}
	
	private class TagListView extends ListView<SemanticTag> {

		private static final long serialVersionUID = -8059370816141108712L;

		public TagListView(String id, List<SemanticTag> list) {
			super(id, list);
		}

		@Override
		protected void populateItem(ListItem<SemanticTag> item) {
			SemanticTag t = item.getModelObject();
			
			ExternalLink link = new ExternalLink("tag", t.getURI().toString(), t.getPrimaryLabel());
			if (t.getPrimaryDescription() != null) {
				link.add(new SimpleAttributeModifier("title", t.getPrimaryDescription()));
			}
			item.add(link);
		}
		
	}

}
